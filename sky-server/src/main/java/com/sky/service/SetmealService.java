package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-08 16:48
 * @vesion 1.0
 */
public interface SetmealService {

    /**
     * 新增套餐
     * @param dto
     */
    void addSetmeal(SetmealDTO dto);

    /**
     * 套餐分页查询
     * @param dto
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO dto);

    /**
     * 根据id批量删除套餐及对应的关系表
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询套餐详细信息
     * @return
     */
    SetmealVO queryById(Long id);

    /**
     * 修改套餐
     * @param dto
     */
    void update(SetmealDTO dto);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
