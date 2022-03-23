package com.example.rocketmqutil.consumer;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.example.rocketmqutil.config.RocketMqConfiguration;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.rocketmqutil.properties.ConsumerProperties;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 16:05
 */
public class AbstractConcurrentlyRocketMqConsumer<T> implements MessageListenerConcurrently {

    private static final Logger log = LoggerFactory.getLogger(AbstractConcurrentlyRocketMqConsumer.class);


    protected void consume(T object) {
    }

    protected void consume(T obj, Message message) {
        this.consume(obj);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            String messageId = messageExt.getMsgId();
            String msgBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            int reConsumeTImes = messageExt.getReconsumeTimes();
            log.info("消息Topic：[{}],消息Tag：[{}],消息消费次数：[{}]，消息实体：[{}]，重推前消息ID:{},重推后消息ID:{}", messageExt.getTopic(), messageExt.getTags(),reConsumeTImes, msgBody,messageId, messageExt.getMsgId());
            Type type = TypeUtil.getTypeArgument(this.getClass());
            Class<?> clazz = TypeUtil.getClass(type);
            Object o = JSON.parseObject(msgBody, clazz);
            try {
                this.consume((T) o,messageExt);
            }catch (Exception e) {
                //大于最大重试次数，直接终止
                if (reConsumeTImes >= this.getReConsumeTimes()) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }


    public int getReConsumeTimes(){
        ConsumerProperties consumerProperties = SpringUtil.getBean(ConsumerProperties.class);
        return consumerProperties.getReConsumeTimes();
    }
}
