package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 17:40
 * @vesion 1.0
 */
@Mapper
public interface OrdersMapper {

    /**
     * 插入订单
     * @param orders
     */
    void insert(Orders orders);
}
