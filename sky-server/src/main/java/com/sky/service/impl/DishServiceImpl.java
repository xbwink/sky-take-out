package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xb
 * @description 菜品
 * @create 2023-07-07 19:50
 * @vesion 1.0
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dto
     */
    @Transactional
    @Override
    public void addDishWithFlavor(DishDTO dto) {

        //1、将数据拷贝至dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        //2、向dish表插入数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        //3、向菜品口味表插入数据
        //取出菜品口味列表
        List<DishFlavor> flavors = dto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);//后绪步骤实现
        }

    }

    /**
     * 分页查询菜品
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<DishVO> page = dishMapper.pageQuery(dto);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(Long[] ids) {

        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            //菜品为起售状态时不能删除
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //菜品关联套餐时不能删除
            List<SetmealDish> list =setmealDishMapper.getByDishId(dish.getId());
            if(!list.isEmpty()){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            //删除菜品时对应的口味数据也要删除
            dishFlavorMapper.deleteBatchByDishId(dish.getId());
        }

        //执行批量删除
        dishMapper.deleteBatch(ids);

    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //1、构建返回结果对象
        DishVO dishVO = new DishVO();
        //2、查询数据并拷贝
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish,dishVO);
        //3、查询对应的口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品信息
     * @param dto
     */
    @Transactional
    @Override
    public void update(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);

        //1、修改dish表数据
        dishMapper.update(dish);

        //2、为前端传过来的口味设置dishId
        List<DishFlavor> flavors = dto.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dto.getId());
        });

        //3、直接删除菜品的口味信息重新插入
        dishFlavorMapper.deleteBatchByDishId(dto.getId());
        if(!dto.getFlavors().isEmpty()){
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(dto.getFlavors());
        }
    }

    /**
     * 菜品启售停售
     * @param status
     * @param id
     */
    @Override
    public void editStatus(Integer status, Long id) {
        //查询菜品是否存在
        Dish dish = dishMapper.getById(id);
        if(dish==null){
            throw new AccountNotFoundException("该菜品不存在");
        }
        //更新状态
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    /**
     * 根据categoryId查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> getByCategoryId(Long categoryId) {
        return categoryMapper.getByCategoryId(categoryId);
    }

}
