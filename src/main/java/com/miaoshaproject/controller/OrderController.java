package com.miaoshaproject.controller;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.mq.MqProducer;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.util.CodeUtil;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.*;

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

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(20);

    }

    //生成验证码
    @RequestMapping(value = "/generateverifycode", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public void generateverifycode(HttpServletResponse response) throws BussinessException, IOException {
        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能生成验证码");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能生成验证码");
        }
        CodeUtil.generateCodeAndPic();

        Map<String, Object> map = CodeUtil.generateCodeAndPic();
        redisTemplate.opsForValue().set("verify_code_" + userModel.getId(), map.get("code"));
        redisTemplate.expire("verify_code_" + userModel.getId(),10,TimeUnit.MINUTES);


        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());
    }

    //生成秒杀令牌
    @RequestMapping(value = "/generatetoken", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType generatetoken(@RequestParam(name = "itemId") Integer itemId,
                                          @RequestParam(name = "promoId") Integer promoId,
                                          @RequestParam(name = "verifyCode") String verifyCode) throws BussinessException {
        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }

        //通过verifyCode验证验证码的有效性
        String redisVerifyCode = (String) redisTemplate.opsForValue().get("verify_code_" + userModel.getId());
        if (StringUtils.isEmpty(redisVerifyCode)){
            throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR,"请求非法");
        }
        if (!redisVerifyCode.equalsIgnoreCase(verifyCode)){
            throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR,"请求非法,验证码错误");
        }
        
        //获取秒杀访问令牌
        String promoToken = promoService.generateSecondKillToken(promoId, itemId, userModel.getId());
        if (promoToken == null) {
            throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR, "生成令牌失败");
        }
        return CommonReturnType.creat(promoToken);
    }


    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "promoToken", required = false) String promoToken) throws BussinessException {


        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }
        //校验秒杀令牌是否正确
        if (promoId != null) {
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userModel.getId() + "_itemid_" + itemId);
            if (inRedisPromoToken == null) {
                throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR, "秒杀令牌校验失败");
            }
            if (!org.apache.commons.lang3.StringUtils.equals(promoToken, inRedisPromoToken)) {
                throw new BussinessException(EmBussinessError.PARAMS_VALIDATION_ERROR, "秒杀令牌校验失败");
            }
        }

        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪

        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = itemService.initStockLog(itemId, amount);

                //再去完成对应的下单事务型消息

                if (!mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId)) {
                    throw new BussinessException(EmBussinessError.UNKNOWN_ERROR, "下单失败");
                }
                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.UNKNOWN_ERROR);
        }

        return CommonReturnType.creat(null);

    }

}
