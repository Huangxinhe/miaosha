package com.miaoshaproject.controller;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @program miaosha
 * @description: 下单接口
 * @author: huangxinhe
 * @create: 2019/06/15 15:17
 */
@Controller("/order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId",required = false) Integer promoId) throws BussinessException {

//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)){
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN,"用户还未登录，不能下单");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN,"用户还未登录，不能下单");
        }

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId,promoId, amount);

        return CommonReturnType.creat(null);

    }

}
