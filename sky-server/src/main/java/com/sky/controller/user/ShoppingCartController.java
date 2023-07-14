package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xb
 * @description 购物车相关接口
 * @create 2023-07-14 11:18
 * @vesion 1.0
 */
@Api(tags = "购物车相关接口")
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO dto){
        shoppingCartService.add(dto);
        return Result.success();
    }

    @ApiOperation("删除购物车中一个商品")
    @PostMapping("/sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO dto){
        shoppingCartService.sub(dto);
        return Result.success();
    }

    @ApiOperation("查看购物车列表")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();
        return Result.success(shoppingCarts);
    }

    @ApiOperation("清空购物车列表")
    @DeleteMapping("/clean")
    public Result del(){
        shoppingCartService.clean();
        return Result.success();
    }

}
