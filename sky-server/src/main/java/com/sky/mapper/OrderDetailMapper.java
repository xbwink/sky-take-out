package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 17:42
 * @vesion 1.0
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单详细信息
     * @param orderDetails
     */
    void insertBatch(ArrayList<OrderDetail> orderDetails);

}
