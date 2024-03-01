package com.sky.service;

import com.sky.pojo.dto.UserLoginDTO;
import com.sky.pojo.entity.User;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);
}