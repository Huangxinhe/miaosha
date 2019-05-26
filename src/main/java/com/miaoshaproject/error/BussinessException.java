package com.miaoshaproject.error;

public class BussinessException extends Exception implements CommonError{

    private CommonError commonError;

    //直接接收EmBusinessError的传参用于构造业务异常
    public BussinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }
    @Override
    public int getErrCode() {
        return 0;
    }

    public BussinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String msg) {
        this.commonError.setErrMsg(msg);
        return this;
    }
}
