package com.miaoshaproject.error;

/**
 * @program miaosha
 * @description: 全局异常
 * @author: huangxinhe
 * @create: 2019/06/19 23:30
 */


public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }

}