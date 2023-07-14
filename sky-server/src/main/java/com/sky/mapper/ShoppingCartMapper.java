package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author xb
 * @description
 * @create 2023-07-07 20:10
 * @vesion 1.0
 */
@Mapper
public interface ShoppingCartMapper {



    /**
     * 动态条件查询购物车
     * @return
     */
    List<ShoppingCart> list(ShoppingCartDTO dto);

    /**
     * 更新购物车数据
     * @param cart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart cart);


    /**
     * 新增购物车数据
     * @param cart
     */
    void insert(ShoppingCart cart);


}
