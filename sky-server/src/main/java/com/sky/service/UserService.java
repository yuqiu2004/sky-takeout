package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
    /**
     * 微信用户登录
     * @param code
     * @return
     */
    User wxLogin(UserLoginDTO code);
}
