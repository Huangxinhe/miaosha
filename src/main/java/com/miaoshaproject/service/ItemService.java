package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.ItemModel;

import java.util.List;

/**
 * @program miaosha
 * @description: 商品
 * @author: huangxinhe
 * @create: 2019/06/09 17:11
 */
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BussinessException;

    //商品列表浏览
    List<ItemModel> listItem();


    //商品详情浏览
    ItemModel getItemById(Integer id);

    //验证item及promo缓存模型
    ItemModel getItemByIdInCache(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId,Integer amount) throws BussinessException;

    //库存回补
    boolean increaseStock(Integer itemId,Integer amount) throws BussinessException;

    //异步更新库存
    boolean aysncDecreaseStock(Integer itemId,Integer amount);

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount) throws BussinessException;


    //初始化库存流水
    String initStockLog(Integer itemId,Integer amount);
}
