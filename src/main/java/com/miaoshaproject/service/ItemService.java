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

}
