package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 11:22
 * @vesion 1.0
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper cartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);
        //只能查询自己的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //1、查询该菜品或套餐是否存在购物车，存在则number++即可
        List<ShoppingCart> shoppingCarts = cartMapper.list(shoppingCart);
        if(shoppingCarts!=null && shoppingCarts.size()>0){
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber() + 1);
            cartMapper.update(cart);
            return;
        }

        //2、菜品或套餐不存在，向数据库插入数据
        if (dto.getDishId() != null) {
            //用户选择的是菜品
            Dish dish = dishMapper.getById(dto.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());
        }else {
            //用户选择的是套餐
            Setmeal setmeal = setmealMapper.getById(dto.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setImage(setmeal.getImage());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        cartMapper.insert(shoppingCart);
    }

    @Override
    public void sub(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);
        //只能查询自己的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //1、该菜品或套餐存在购物车且数量>1，则number--即可
        List<ShoppingCart> shoppingCarts = cartMapper.list(shoppingCart);
        ShoppingCart cart = shoppingCarts.get(0);
        if(shoppingCarts!=null && cart.getNumber() > 1){
            cart.setNumber(cart.getNumber() - 1);
            cartMapper.update(cart);
            return;
        }

        //2、该菜品或套餐只有一份时直接删除
        cartMapper.delByDishIdOrSetmealId(shoppingCart);
    }


    @Override
    public List<ShoppingCart> list() {
        //通过ThreadLocal取出用户id
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return cartMapper.list(shoppingCart);
    }

    @Override
    public void clean() {
        //取出用户id
        Long userId = BaseContext.getCurrentId();
        //删除所有该用户的购物车数据
        cartMapper.deleteAllByUserId(userId);
    }



}
