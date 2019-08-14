package com.miaoshaproject.error;

public enum EmBussinessError implements CommonError {
    //通用错误类型
    PARAMS_VALIDATION_ERROR(100001, "参数不合法"),
    UNKNOWN_ERROR(100002, "未知错误"),

    USER_NOT_EXIST(20001, "用户不存在"),

    //20000开头为用户信息相关错误定义
    USER_LOGIN_FAIL(20002, "用户手机号或者密码不正确"),

    //20000开头为用户信息相关错误定义
    USER_NOT_LOGIN(20003, "用户还未登陆"),

    //30000开头为交易相关错误定义
    STOCK_NOT_ENOUGH(30001, "库存不足"),
    MQ_SEND_FAIL(30002,"库存异步消息失败"),
    RATELIMIT(30003,"活动太火爆，请稍后再世") ,

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
