package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xb
 * @description 自定义定时任务类
 * @create 2023-07-17 8:39
 * @vesion 1.0
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    OrdersMapper ordersMapper;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?") //每分钟执行一次
    public void timeoutOrders(){
        //每分钟检查一次是否存在支付超时订单（下单后超过15分钟仍未支付则判定为支付超时订单），
        // 如果存在则修改订单状态为“已取消”
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //1、查询所有下单时间超过15分钟的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        List<Orders> orders = ordersMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
        //2、遍历集合将订单状态修改为已取消
        orders.forEach(order -> {
            order.setStatus(6);
            order.setCancelReason("用户超时未支付");
            order.setCancelTime(LocalDateTime.now());
            //执行更新操作
            ordersMapper.update(order);
        });
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") //凌晨1点执行一次
    public void DeliveryOrders(){
        //每天凌晨1点检查一次是否存在“派送中”的订单，如果存在则修改订单状态为“已完成”
        log.info("定时处理派送中订单：{}",LocalDateTime.now());
        // select * from orders where status = 4 and order_time < 当前时间-1小时
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> orders = ordersMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        //2、遍历集合将订单状态修改为已完成
        orders.forEach(order -> {
            order.setStatus(5);

            //执行更新操作
            ordersMapper.update(order);
        });
    }


}
