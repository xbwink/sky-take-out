package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author xb
 * @description 购物车相关服务
 * @create 2023-07-07 19:49
 * @vesion 1.0
 */

public interface ShoppingCartService {


    /**
     * 添加购物车
     * @param dto
     */
    void add(ShoppingCartDTO dto);

    /**
     * 查询购用户物车列表
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车列表
     */
    void clean();

    /**
     * 删除购物车中一个商品
     * @param dto
     */
    void sub(ShoppingCartDTO dto);
}
