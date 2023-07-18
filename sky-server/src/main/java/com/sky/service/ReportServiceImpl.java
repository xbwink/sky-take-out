package com.sky.service;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.vo.TurnoverReportVO;
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

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1、该集合用于存储从begin到end的每一天
        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dates.add(begin);
        }

        //遍历集合获取每一天的营销额
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

}
