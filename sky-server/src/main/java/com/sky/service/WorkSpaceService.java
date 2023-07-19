package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;

import java.time.LocalDateTime;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-18 15:43
 * @vesion 1.0
 */
public interface WorkSpaceService {

    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO businessData();

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO overviewOrders();

}
