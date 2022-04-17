package com.example.rocketmqutil.consumer;

import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson.JSON;
import com.example.rocketmqutil.config.AbstractRocketMqConsumerConfig;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 同步消费者处理类：主要处理普通消息，消费者默认以该方式处理
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 16:05
 */
public class AbstractConcurrentlyRocketMqConsumer<T> extends AbstractRocketMqConsumerConfig implements MessageListenerConcurrently {

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
            int reConsumeTimes = messageExt.getReconsumeTimes();
            log.info("msg Topic：[{}],msg Tag：[{}],reConsumeTimes：[{}]，msg body：[{}]，reSend before msgID:{},reSend after msgId :{}", messageExt.getTopic(), messageExt.getTags(),reConsumeTimes, msgBody,messageId, messageId);
            Type type = TypeUtil.getTypeArgument(this.getClass());
            Class<?> clazz = TypeUtil.getClass(type);
            Object msgObj = JSON.parseObject(msgBody, clazz);
            try {
                this.consume((T)msgObj,messageExt);
            }catch (Exception e) {
                log.error("consume message failed！message topic：{}，tag：{}，message body：{}",messageExt.getTopic(),messageExt.getTags(),msgBody);
                //大于最大重试次数，直接终止
                if (reConsumeTimes >= this.getReConsumeTimes(this.getClass())) {
                    log.info("reConsumeTimes is max，this times is [{}],msg Topic:[{}],msg tag:[{}]",reConsumeTimes,messageExt.getTopic(),messageExt.getTags());
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
