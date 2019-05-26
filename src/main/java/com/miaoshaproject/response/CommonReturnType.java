package com.miaoshaproject.response;

public class CommonReturnType {

    //表明对应status返回结果 success或者fail
    private String status;

    //若status为success则data内返回前端需要的json数据
    //若status为fail，则data内返回前端通用的错误码格式
    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType creat(Object result){
        return CommonReturnType.creat(result,"success");
    }

    public static CommonReturnType creat(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
