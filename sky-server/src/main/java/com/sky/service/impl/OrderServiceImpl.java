package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
