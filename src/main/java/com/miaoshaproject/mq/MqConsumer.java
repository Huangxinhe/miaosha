package com.miaoshaproject.mq;

import com.alibaba.fastjson.JSON;
import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @program miaosha
 * @description: 消费者
 * @author: huangxinhe
 * @create: 2019/07/24 23:21
 */
@Component
public class MqConsumer {

    @Value("${mq.nameserver.addr}")
    private String nameAddr;

    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    private DefaultMQPushConsumer consumer;
    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamesrvAddr(nameAddr);

        consumer.subscribe(topicName,"*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                Message msg = msgs.get(0);
                String jsonStr =  new String(msg.getBody());
                Map<String,Object> map = JSON.parseObject(jsonStr,Map.class);
                Integer itemid = (Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                itemStockDOMapper.decreaseStock(itemid,amount);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
