package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

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
}
