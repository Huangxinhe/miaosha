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

    //活动发布
    void publishPromo(Integer promoId);

    //生成秒杀用的令牌
    String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId);
}
