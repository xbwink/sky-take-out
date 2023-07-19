package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-18 15:43
 * @vesion 1.0
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public BusinessDataVO businessData() {
        //设置今日时间0.到23.5999
        LocalDate dateTime = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(dateTime, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateTime, LocalTime.MAX);

        HashMap map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        //查询今日的订单总数
        Integer orderCount = ordersMapper.countByMap(map);

        //查询今日营业额
        map.put("status",5);
        Double turnover = ordersMapper.sumByMap(map);
        if(turnover == null) turnover = 0.0;

        //查询今日有效订单数
        Integer validOrderCount = ordersMapper.countByMap(map);

        //查询今天创建账号的用户数量
        Long dayTotal = userMapper.userCountByMap(map);

        //今日订单完成率
        Double orderCompletionRate = 0.0;
        if(orderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / orderCount;
        }

        //平均客单价 营业额 / 有效订单数
        Double unitPrice = 0.0;
        if(validOrderCount!=0){
            unitPrice = turnover / validOrderCount;
        }

        BusinessDataVO vo = BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(dayTotal.intValue())
                .build();
        return vo;
    }

    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        HashMap map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);

        //查询总订单数
        Integer totalOrderCount = ordersMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        //营业额
        Double turnover = ordersMapper.sumByMap(map);
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = ordersMapper.countByMap(map);

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Long newUsers = userMapper.userCountByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers.intValue())
                .build();
    }

    @Override
    public OrderOverViewVO overviewOrders() {
        //设置今日时间0.到23.5999
        LocalDate dateTime = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(dateTime, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateTime, LocalTime.MAX);

        HashMap map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);

        //全部订单
        Integer allOrders = ordersMapper.getCountByMap(map);

        //待接单
        map.put("status",2);
        Integer waitingOrders = ordersMapper.getCountByMap(map);

        //待派送
        map.put("status",3);
        Integer deliveredOrders = ordersMapper.getCountByMap(map);

        //已完成数量
        map.put("status",5);
        Integer completedOrders = ordersMapper.getCountByMap(map);

        //已取消数量
        map.put("status",6);
        Integer cancelledOrders = ordersMapper.getCountByMap(map);

        OrderOverViewVO vo = OrderOverViewVO.builder()
                .allOrders(allOrders)
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .build();
        return vo;
    }
}

