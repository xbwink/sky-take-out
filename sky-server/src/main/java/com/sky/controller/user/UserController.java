package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-13 11:00
 * @vesion 1.0
 */
@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO dto) {
        log.info("用户临时登录凭证code:{}", dto.getCode());
        User user = userService.wxLogin(dto.getCode());

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        //构建返回结果对象
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token).build();

        return Result.success(userLoginVO);
    }

}
