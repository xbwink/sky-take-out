package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
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
    List<ShoppingCart> list(ShoppingCart cart);

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


    /**
     * 根据用户id清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAllByUserId(Long userId);

    /**
     * 根据菜品id或套餐id删除购物车对应数据
     */
    void delByDishIdOrSetmealId(ShoppingCart shoppingCart);

}
