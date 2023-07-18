package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealOverViewVO;

import java.util.List;

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
    void addDishWithFlavor(DishDTO dishVO);

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

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 修改菜品信息
     * @param dto
     */
    void update(DishDTO dto);

    /**
     * 菜品启售停售
     * @param status
     * @param id
     */
    void editStatus(Integer status, Long id);

    /**
     * 根据菜categoryId查询菜品
     *
     * @param categoryId
     * @return
     */
    List<DishVO> getByCategoryId(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO overviewOrders();



}
