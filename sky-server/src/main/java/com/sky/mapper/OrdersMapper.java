package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据条件动态分页查询订单
     * @param dto
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO dto);

    /**
     * 根据id查询订单
     * @param orderId
     * @return
     */
    @Select("select * from orders where id = #{orderId}")
    OrderVO getById(Long orderId);

    /**
     * 取消订单
     * @param orderId
     */
    @Update("update orders set status = 6 where id = #{orderId}")
    void cancelOrder(Long orderId);
}
