package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 17:06
 * @vesion 1.0
 */
public interface OrderService {

    /**
     * 用户下单
     * @param dto
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO dto);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     *
     * @param dto
     * @return
     */
    PageResult pageQueryHistoryOrders(OrdersPageQueryDTO dto);

    /**
     * 根据orderId查询订单详情
     * @param orderId
     * @return
     */
    OrderVO queryOrderDetail(Long orderId);

    /**
     * 根据orderId取消订单
     * @param orderId
     */
    void cancelOrder(Long orderId);


    /**
     * 再来一单
     * @param orderId
     */
    void repetition(Long orderId);
}
