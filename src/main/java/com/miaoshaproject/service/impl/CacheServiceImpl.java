package com.miaoshaproject.service.impl;

import com.miaoshaproject.service.CacheService;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @program miaosha
 * @description: //本地热点缓存操作实现类
 * @author: huangxinhe
 * @create: 2019/07/06 17:46
 */
@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String,Object> commonCache = null;

    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                //设置缓存中最大可以存储100个key，超过100个之后会按照LRU的策略移除缓存项
                .maximumSize(100)
                //设置写缓存后多少秒过期
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
