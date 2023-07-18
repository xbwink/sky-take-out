package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询状态为待接单的总订单数
     * @return
     */
    @Select("select count(id) from orders where status = 2")
    Integer getToBeConfirmed();

    /**
     * 查询状态为待派送的总订单数
     * @return
     */
    @Select("select count(id) from orders where status = 3")
    Integer getConfirmed();

    /**
     * 查询状态为派送中的总订单数
     * @return
     */
    @Select("select count(id) from orders where status = 4")
    Integer getDeliveryInProgress();

    /**
     * 根据状态和时间查询订单
     * @return
     */
    Integer getCountByMap(HashMap map);

    /**
     * 根据状态和下单时间查询订单
     * @param status
     * @param orderTime
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);


    /**
     * 根据条件查询日营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);


    /**
     * 根据条件查询日订单数
     *
     * @param map
     * @return
     */
    Integer countByMap(HashMap map);
}
