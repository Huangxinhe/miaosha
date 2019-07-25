package com.miaoshaproject.service;

/**
 * @program miaosha
 * @description: //封装本地热点缓存
 * @author: huangxinhe
 * @create: 2019/07/06 17:45
 */
public interface CacheService {
    //存方法
    void setCommonCache(String key,Object value);

    //取方法
    Object getFromCommonCache(String key);
}
