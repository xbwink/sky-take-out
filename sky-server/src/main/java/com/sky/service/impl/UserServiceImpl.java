package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-13 11:12
 * @vesion 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    WeChatProperties weChatProperties;

    //微信服务接口地址
    public static final String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";


    @Override
    public User wxLogin(String code){
        //1、调用 auth.code2Session 接口，换取 用户唯一标识 OpenID
        String openId = getOpenId(code);

        //2、判断openId是否为空，如果为空表示登录失败
        if(openId == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3、判断当前用户是否为新用户
        User user = userMapper.getByOpenId(openId);
        if (user == null) {
            //如果为新用户，自动完成注册
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //4、返回这个用户对象
        return user;
    }

    /**
     * 获取openId
     * @return
     */
    private String getOpenId(String code){
        HashMap<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(requestUrl, map);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openId = jsonObject.getString("openid");
        return openId;
    }


}
