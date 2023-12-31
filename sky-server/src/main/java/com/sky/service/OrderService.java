package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
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
     * 用户端分页查询历史订单
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

    /**
     * 管理端分页查询
     * @param dto
     * @return
     */
    PageResult pageQueryOrders(OrdersPageQueryDTO dto);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param orderId
     */
    void confirm(Long orderId);


    /**
     * 商家拒单
     * @param dto
     */
    void rejection(OrdersRejectionDTO dto);

    /**
     * 商家取消订单
     * @param dto
     */
    void cancel(OrdersCancelDTO dto);

    /**
     * 派送订单
     * @param orderId
     */
    void delivery(Long orderId);

    /**
     * 完成订单
     * @param orderId
     */
    void complete(Long orderId);

    /**
     * 用户催单
     * @param id
     */
    void reminder(Long id);
}
