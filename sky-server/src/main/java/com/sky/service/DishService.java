package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

/**
 * @author xb
 * @description 菜品相关服务
 * @create 2023-07-07 19:49
 * @vesion 1.0
 */

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishVO
     */
    void addDishWithFlavor(DishVO dishVO);

    /**
     * 分页查询菜品
     * @param dto
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dto);

    /**
     * 根据id批量删除数据
     * @param ids
     */
    void deleteBatch(Long[] ids);
}
