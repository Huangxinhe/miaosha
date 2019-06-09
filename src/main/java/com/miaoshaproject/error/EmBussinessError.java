package com.miaoshaproject.error;

public enum EmBussinessError implements CommonError {
    //通用错误类型
    PARAMS_VALIDATION_ERROR(100001, "参数不合法"),
    UNKNOWN_ERROR(100002, "未知错误"),

    USER_NOT_EXIST(20001, "用户不存在"),

    //20000开头为用户信息相关错误定义
    USER_LOGIN_FAIL(20002, "用户手机号或者密码不正确"),

    SMS_INVALID(100003,"验证码无效！！！")
    ;

    EmBussinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;


    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String msg) {
        this.errMsg = msg;
        return this;
    }
}
