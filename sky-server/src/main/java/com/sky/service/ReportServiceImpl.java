package com.sky.service;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
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
import java.util.Map;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-17 15:16
 * @vesion 1.0
 */
@Service
public class ReportServiceImpl implements ReportService{

    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    UserMapper userMapper;
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

}
