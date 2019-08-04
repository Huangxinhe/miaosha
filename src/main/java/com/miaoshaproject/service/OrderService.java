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
    //使用1.通过前端 url上传过来秒杀活动id，然后下单接口内检验对应的id是否属于对应商品且活动已开始
    //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BussinessException;
}
