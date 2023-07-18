package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据订单id查询订单详情
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    /**
     * 条件查询
     * @return
     */
    List<GoodsSalesDTO> getNameWithNumberByMap(HashMap map);

}
