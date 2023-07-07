package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xb
 * @description 菜品相关接口
 * @create 2023-07-07 19:44
 * @vesion 1.0
 */
@Api(tags = "菜品相关接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;
    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishVO dishVO){
        log.info("新增菜品：{}",dishVO);
        dishService.addDishWithFlavor(dishVO);
        return Result.success();
    }

}
