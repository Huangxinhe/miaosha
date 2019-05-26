package com.miaoshaproject.service;

import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    //通过用户Id获取用户对象的
    UserModel getUserById(Integer id);
}
