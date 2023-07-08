package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-08 16:48
 * @vesion 1.0
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Transactional
    @Override
    public void addSetmeal(SetmealDTO dto) {
        //1、拷贝属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);

        //2、先插入套餐
        setmealMapper.insert(setmeal);

        //获取insert语句生成的主键值
        Long setmealId = setmeal.getId();

        //3、再插入套餐关联的菜品信息
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        if(!setmealDishes.isEmpty()){
           setmealDishes.forEach(setmealDish -> {
               setmealDish.setSetmealId(setmealId);
           });
            //向setmealDishes表插入n条数据
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }


    @Override
    public PageResult pageQuery(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        //下一条sql语句会自动拼接limit
        Page<SetmealVO> setmealPage = setmealMapper.page(dto);

        return new PageResult(setmealPage.getTotal(),setmealPage.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {

        //遍历套餐
        for (Long id : ids) {
            //套餐为启售状态时不可删除
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus()==1){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }

            //删除套餐时还需删除套餐包含的菜品表
            setmealDishMapper.deleteBySetmealId(setmeal.getId());
        }

        //删除套餐表数据
        setmealMapper.deleteBatch(ids);



    }

    @Override
    public SetmealVO queryById(Long id) {
        //1、构建返回对象
        SetmealVO setmealVO = new SetmealVO();
        //2、查询数据并拷贝
        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        //3、查询套餐信息并赋值
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Transactional
    @Override
    public void update(SetmealDTO dto) {
        //1、拷贝属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);

        //2、清除套餐包含的所有菜品
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        //3、重新插入新的菜品
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        if(!setmealDishes.isEmpty()){
            //为新的菜品设置套餐id
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
            setmealDishMapper.insertBatch(dto.getSetmealDishes());
        }

        //4、修改套餐
        setmealMapper.update(setmeal);
    }


    @Override
    public void updateStatus(Integer status, Long id) {
        //1、先查询出套餐
        Setmeal setmeal = setmealMapper.getById(id);
        if(setmeal == null){
            throw new AccountNotFoundException("套餐不存在");
        }
        //2、设置更新值
        setmeal.setStatus(status);
        //3、更新
        setmealMapper.update(setmeal);
    }

}
