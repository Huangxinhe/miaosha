package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @program miaosha
 * @description: 订单处理
 * @author: huangxinhe
 * @create: 2019/06/15 13:36
 */
public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BussinessException;
}
