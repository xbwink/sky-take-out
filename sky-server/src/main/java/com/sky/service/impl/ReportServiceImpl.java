package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-17 15:16
 * @vesion 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1、创建集合存储从begin到end的每一天
        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dates.add(begin);
        }

        //2、遍历集合获取每一天的营销额
        ArrayList<Double> amountList = new ArrayList<>();
        for (LocalDate date : dates) {
            //获得今天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end", endTime);
            Double dayAmount = ordersMapper.sumByMap(map);
            dayAmount = dayAmount != null ? dayAmount : 0.0; //为空设置默认值
            amountList.add(dayAmount);
        }


        //3、构造返回结果对象
        TurnoverReportVO vo = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .turnoverList(StringUtils.join(amountList,","))
                .build();
        return vo;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1、创建集合存储从begin到end的每一天
        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dates.add(begin);
        }

        //2、遍历集合查询每天的用户总和新增用户
        ArrayList<Long> totalUserList = new ArrayList<>();
        ArrayList<Long> newUserList = new ArrayList<>();
        for (LocalDate date : dates) {
            //获得今天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            //查询截止到今天的用户总量
            map.put("end",endTime);
            Long total = userMapper.userCountByMap(map);

            //查询今天创建账号的用户数量
            map.put("begin",beginTime);
            Long dayTotal = userMapper.userCountByMap(map);

            totalUserList.add(total);
            newUserList.add(dayTotal);
        }

        //3、构建返回结果对象
        UserReportVO vo = UserReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
        return vo;
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //1、记录从begin到end的每一天
        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dates.add(begin);
        }

        //2、遍历dates获得每日的订单数以及有效订单数
        ArrayList<Integer> orderCountList = new ArrayList<>();
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dates) {
            //获得今天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap map = new HashMap();

            //查询今日的订单总数
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer orderCount = ordersMapper.countByMap(map);

            //查询今日有效订单数
            map.put("status",5);
            Integer validOrderCount = ordersMapper.countByMap(map);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //3、将集合内的数量累加获得订单总数和有效订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);
        //订单完成率 = 有效订单数 / 总订单数 * 100
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){ //防止总数为0时出现异常
            orderCompletionRate = Double.valueOf(validOrderCount) / totalOrderCount; //因为前端已经计算*100
        }

        //4、构建返回结果对象
        OrderReportVO vo = OrderReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return vo;
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //1、初始化开始时间和结束时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //2、查询获得商品名称和对应的销量
        HashMap map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        map.put("status",5);
        List<GoodsSalesDTO> dtos = orderDetailMapper.getNameWithNumberByMap(map);

        //3、将商品名称和销量独立储存到两个集合
        List<String> nameList = dtos.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = dtos.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        //4、构建返回结果对象
        SalesTop10ReportVO vo = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
        return vo;
    }

}
