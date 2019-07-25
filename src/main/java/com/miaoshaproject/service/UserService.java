package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    //通过用户Id获取用户对象的
    UserModel getUserById(Integer id);

    //通过缓存获取用户对象
    UserModel getUserByIdInCache(Integer id);
    void register(UserModel userModel) throws BussinessException;

    /**
     *
     * @param telphone 用户注册手机
     * @param encrptPpassword 用户加密后的密码
     * @throws BussinessException
     */
    UserModel validateLogin(String telphone,String encrptPpassword) throws BussinessException;
}
