package com.miaoshaproject.service;

import com.miaoshaproject.service.model.PromoModel;

/**
 * @Param
 * @return
 * @Description //TODO
 * @Date 2019/6/16 4:09 PM
 **/
public interface PromoService {
    //根据itemId获取即将进行或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
