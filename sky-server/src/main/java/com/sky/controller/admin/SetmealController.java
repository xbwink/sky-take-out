package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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
@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealService setmealService;
    @ApiOperation("新增套餐")
    @PostMapping
    public Result addDish(@RequestBody SetmealDTO dto){
        log.info("新增套餐：{}",dto);
        setmealService.addSetmeal(dto);
        return Result.success();
    }

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO dto){
        log.info("套餐分页查询：{}",dto);
        PageResult pageResult = setmealService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐：{}",ids);
        setmealService.delete(ids);
        return Result.success();
    }

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐：{}",id);
        SetmealVO setmealVO =  setmealService.queryById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO dto){
        log.info("修改套餐：{}",dto);
        setmealService.update(dto);
        return Result.success();
    }

    @ApiOperation("套餐起售停售")
    @PostMapping("/status/{status}")
    public Result update(@PathVariable Integer status,Long id){
        log.info("套餐起售停售：{}",id);
        setmealService.updateStatus(status,id);
        return Result.success();
    }



}
