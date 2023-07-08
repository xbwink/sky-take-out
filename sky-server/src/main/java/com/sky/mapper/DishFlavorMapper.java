package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xb
 * @description
 * @create 2023-07-07 20:10
 * @vesion 1.0
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据dishId批量删除口味数据
     * @param id
     */
    void deleteBatchByDishId(Long id);
}
