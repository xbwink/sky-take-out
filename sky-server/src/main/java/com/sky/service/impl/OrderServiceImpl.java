package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.BaiDuMapUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 17:07
 * @vesion 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WeChatPayUtil weChatPayUtil;
    @Autowired
    BaiDuMapUtil baiDuMapUtil;
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        //1、处理各种业务异常
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        if(addressBook==null){
            //地址簿为空
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //根据userId查出对应的购物车数据
        ShoppingCart cart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(cart);
        if(shoppingCarts==null || shoppingCarts.isEmpty()){
            //购物车为空
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //用户距离与商家门店距离不能超于5km
        String userAddress = addressBook.getProvinceName() //省份
                            +addressBook.getCityName() //市
                            +addressBook.getDistrictName() //区
                            +addressBook.getDetail(); //详细地址
        if(baiDuMapUtil.checkDistance(userAddress)){
            //超出配送范围
            throw new AddressBookBusinessException("下单失败，超出配送范围(5公里)");
        }

        //2、向orders表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto,orders);
        //订单号 用户id+时间戳
        orders.setNumber(BaseContext.getCurrentId()+String.valueOf(System.currentTimeMillis()));
        orders.setUserId(BaseContext.getCurrentId());   //下单用户id
        orders.setOrderTime(LocalDateTime.now());       //下单时间
        orders.setStatus(Orders.PENDING_PAYMENT); //订单状态 默认待付款
        orders.setPayStatus(Orders.UN_PAID);//订单支付状态  默认未支付
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setPhone(addressBook.getPhone());//收货人手机号
        orders.setAddress(addressBook.getDetail());//详细收获地址
        ordersMapper.insert(orders);

        //3、向order——detail表插入一条或多条数据
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        //遍历shoppingCart集合设置属性
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        //4、清理购物车
        shoppingCartMapper.deleteAllByUserId(BaseContext.getCurrentId());

        //5、构建返回结果对象
        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime()).build();
        return submitVO;
    }

    @Override
    public PageResult pageQueryHistoryOrders(OrdersPageQueryDTO dto) {
        //1、分页查询当前用户的所有订单信息
        dto.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        //下一条sql语句会自动拼接limit
        Page<OrderVO> orderVOPage= ordersMapper.pageQuery(dto);

        //2、遍历查询每一份订单的详细信息并赋值
        for (OrderVO orderVO : orderVOPage.getResult()) {
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderVO.getId());
            orderVO.setOrderDetailList(orderDetails);
        }

        //3、构造返回结果对象
        return new PageResult(orderVOPage.getTotal(),orderVOPage.getResult());
    }

    @Override
    public OrderVO queryOrderDetail(Long orderId) {
        //1、根据id查询订单
        OrderVO orderVO = ordersMapper.getById(orderId);

        //2、根据id查询订单详细数据
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
        orderVO.setOrderDetailList(orderDetails);

        //3、构造返回结果对象
        return orderVO;
    }

    @Override
    public void cancelOrder(Long orderId) {
        Orders orders = new Orders();
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");//暂时硬编码
        orders.setId(orderId);
        orders.setStatus(6);
        ordersMapper.update(orders);
    }


    @Override
    public void repetition(Long orderId) {
        //将该订单的所有餐品信息加入至购物车
        //1、查询出该订单的所有餐品信息
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

        //2、将餐品信息添加至购物车
        orderDetails.forEach(orderDetail -> {
            //拷贝属性
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,cart);
            cart.setUserId(BaseContext.getCurrentId());
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        });

    }

    @Override
    public PageResult pageQueryOrders(OrdersPageQueryDTO dto) {
        //1、根据条件动态分页查询订单信息
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        //下一条sql语句会自动拼接limit
        Page<OrderVO> orderVOPage= ordersMapper.pageQuery(dto);

        //如果订单状态为2、3、4则需要查询其对应的菜品信息
        if(dto.getStatus()!=null){
            Integer status = dto.getStatus();
            if(status == 2 || status == 3 || status == 4){
                //遍历查询每一份订单菜品名并赋值
                for (OrderVO orderVO : orderVOPage.getResult()) {
                    List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderVO.getId());
                    //菜品名格式:宫保鸡丁* 3；红烧带鱼* 2；农家小炒肉* 1；
                    String orderDishes = new String();
                    for (OrderDetail orderDetail : orderDetails) {
                        orderDishes += orderDetail.getName() + "*";
                        orderDishes += orderDetail.getNumber()+"; ";
                    }
                    orderVO.setOrderDishes(orderDishes);
                }
            }
        }


        return new PageResult(orderVOPage.getTotal(),orderVOPage.getResult());
    }

    @Override
    public OrderStatisticsVO statistics() {
        //分别查询数据库待接单、待派送、派送中订单的数量
        Integer toBeConfirmedCount = ordersMapper.getToBeConfirmed();//待接单
        Integer confirmedCount = ordersMapper.getConfirmed();//待派送
        Integer deliveryInProgressCount = ordersMapper.getDeliveryInProgress();//派送中

        //构建返回结果对象
        return new OrderStatisticsVO(toBeConfirmedCount,confirmedCount,deliveryInProgressCount);
    }

    @Override
    public void confirm(Long orderId) {
        //将状态更改为:3已接单
        Orders orders = Orders.builder().id(orderId).status(3).build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO dto) {
        //选择拒单状态流转至已取消:6
        Orders orders = Orders.builder()
                .id(dto.getId())
                .status(6) //设置状态
                .rejectionReason(dto.getRejectionReason())//设置拒单原因
                .cancelReason("商家已拒单")//订单取消原因
                .cancelTime(LocalDateTime.now()).build();//取消时间
        ordersMapper.update(orders);
    }

    @Override
    public void cancel(OrdersCancelDTO dto) {
        //选择拒单状态流转至已取消:6
        Orders orders = Orders.builder()
                .id(dto.getId())
                .status(6) //设置状态
                .cancelReason(dto.getCancelReason())//订单取消原因
                .cancelTime(LocalDateTime.now()).build();//取消时间
        ordersMapper.update(orders);
    }

    @Override
    public void delivery(Long orderId) {
        //将状态修改为:派送中4
        Orders orders = Orders.builder()
                .id(orderId)
                .status(4) //设置状态
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void complete(Long orderId) {
        //将状态修改为:5已完成
        Orders orders = Orders.builder()
                .id(orderId)
                .status(5) //设置状态
                .deliveryTime(LocalDateTime.now())//送达时间
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);
    }



}
