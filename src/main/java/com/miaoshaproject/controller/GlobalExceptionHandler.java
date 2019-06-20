package com.miaoshaproject.controller;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.CodeMsg;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.error.GlobalException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.response.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.BindException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program miaosha
 * @description: 全局异常处理
 * @author: huangxinhe
 * @create: 2019/06/19 23:28
 */
@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,Exception ex){
        ex.printStackTrace();
        Map<String,Object> responseData = new HashMap<>();
        if (ex instanceof BussinessException){
            BussinessException bussinessException = (BussinessException) ex;
            responseData.put("errCode", bussinessException.getErrCode());
            responseData.put("errMsg",bussinessException.getErrMsg());
        }else if(ex instanceof ServletRequestBindingException){
            responseData.put("errCode", EmBussinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","url绑定路由问题");
        }else if(ex instanceof NoHandlerFoundException){
            responseData.put("errCode", EmBussinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","没有找到对应的访问路径");
        }else {
            responseData.put("errCode",EmBussinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBussinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.creat(responseData,null);
    }
}