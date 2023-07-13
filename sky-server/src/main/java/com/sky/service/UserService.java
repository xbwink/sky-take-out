package com.sky.service;

import com.sky.entity.User;

public interface UserService {

    /**
     * 用户微信登录
     *
     * @return
     */
    User wxLogin(String code);

}
