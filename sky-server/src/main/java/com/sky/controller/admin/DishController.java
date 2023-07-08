package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result addDish(@RequestBody DishDTO dto){
        log.info("新增菜品：{}",dto);
        dishService.addDishWithFlavor(dto);
        return Result.success();
    }

    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto){
        log.info("菜品分页查询：{}",dto);
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result delete(Long[] ids){
        log.info("批量删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dto){
        log.info("修改菜品：{}",dto);
        dishService.update(dto);
        return Result.success();
    }

    /**
     * 菜品启售停售
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("菜品启售停售")
    @PostMapping("/status/{status}")
    public Result modifyStatus(@PathVariable Integer status,Long id) {
        log.info("菜品启售停售:",status,id);
        dishService.editStatus(status,id);
        return Result.success();
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
        public Result<List<DishVO>> getByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品:",categoryId);
        List<DishVO> dishVOS = dishService.getByCategoryId(categoryId);
        return Result.success(dishVOS);
    }

}
