package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author xb
 * @description 店铺操作相关接口
 * @create 2023-07-07 16:15
 * @vesion 1.0
 */

@Api(tags = "店铺操作相关接口")
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    @Autowired
    RedisTemplate redisTemplate;
    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        log.info("获取到店铺的营业状态为：{}",status == 1 ? "营业中":"打烊");
        return Result.success(status);
    }

}
