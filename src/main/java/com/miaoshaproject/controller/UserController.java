package com.miaoshaproject.controller;


import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;
import sun.security.rsa.RSASignature;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
//@CrossOrigin
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登陆接口
    @RequestMapping(value = "/login",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String passwprd) throws BussinessException, NoSuchAlgorithmException {
        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)||org.apache.commons.lang3.StringUtils.isEmpty(passwprd)){
            throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR);
        }

        //用户登陆服务，用来校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone,this.EncodeByMd5(passwprd));
        // 加入到用户登陆成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.creat(null);
    }

    //用户注册接口
    @RequestMapping(value = "/register",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone") String telphone,
                                     @RequestParam(name="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name="age") Integer age,
                                     @RequestParam(name="gender") Integer gender,
                                     @RequestParam(name="password") String password) throws NoSuchAlgorithmException {
        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode,inSessionOtpCode)){

           return CommonReturnType.creat("验证码不正确！！！","failure");
        }

        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setThirdPartyId("11");
        userModel.setRegisterMode("byphone");


        userModel.setEncrptPassword(this.EncodeByMd5(password));

        try {
            userService.register(userModel);
        } catch (BussinessException e) {
            e.printStackTrace();
            return CommonReturnType.creat("手机号码已存在！！！","failure");

        }
        return CommonReturnType.creat(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newstr = base64Encoder.encode(md5.digest(str.getBytes()));
        return newstr;
    }

    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone){
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(9999);
        randomInt+=10000;
        String otpCode = String.valueOf(randomInt);


        //将OTP验证码同对应用户的手机号关联,使用httpSession的方式绑定他的手机号与OTPCODE
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将OTP验证码通过短信通道发给用户，省略
        System.out.println("telphone="+telphone+"&otpCode="+otpCode);

        return CommonReturnType.creat(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BussinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
        //获取的对应用户信息不存在
        if (userModel == null) {
            throw new BussinessException(EmBussinessError.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.creat(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }





}
