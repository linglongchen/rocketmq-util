package com.example.rocketmqutil.processor;

import com.example.rocketmqutil.annotation.RocketMqConsumerListener;
import com.example.rocketmqutil.constants.MessageTypeConstant;
import com.example.rocketmqutil.consumer.AbstractOrderlyRocketMqConsumer;
import com.example.rocketmqutil.properties.ConsumerProperties;
import com.example.rocketmqutil.consumer.AbstractConcurrentlyRocketMqConsumer;
import javax.annotation.Resource;

import io.netty.util.internal.ThrowableUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 消费者处理器
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 16:01
 */
public class RocketMqConsumerProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(RocketMqConsumerProcessor.class);

    @Resource
    private ConsumerProperties consumerProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MessageListener) {
            RocketMqConsumerListener annotation = bean.getClass().getAnnotation(RocketMqConsumerListener.class);
            if (annotation == null) {
                BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
            } else {
                String messageType = annotation.messageType();
                String topic = annotation.topic();
                String tag = annotation.tag();
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerProperties.getGroupName());
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                consumer.setMessageModel(MessageModel.valueOf(annotation.messageModel()));
                consumer.setNamesrvAddr(consumerProperties.getNamesrvAddr());
                consumer.setConsumeThreadMin(annotation.threadNum());
                consumer.setConsumeThreadMax(consumerProperties.getConsumeThreadMax());
                consumer.setConsumeMessageBatchMaxSize(consumerProperties.getConsumeMessageBatchMaxSize());
                if (StringUtils.equals(messageType, MessageTypeConstant.CONCURRENTLY)) {
                    AbstractConcurrentlyRocketMqConsumer abstractRocketMqConsumer = (AbstractConcurrentlyRocketMqConsumer) bean;
                    // 设置监听
                    consumer.registerMessageListener(abstractRocketMqConsumer);
                } else if (StringUtils.equals(messageType, MessageTypeConstant.ORDERLY)) {
                    AbstractOrderlyRocketMqConsumer abstractOrderlyRocketMqConsumer = (AbstractOrderlyRocketMqConsumer) bean;
                    consumer.registerMessageListener(abstractOrderlyRocketMqConsumer);
                }
                try {
                    consumer.subscribe(topic,tag);
                } catch (MQClientException e) {
                    log.error("===================consumer subscribe failed! failed reason: [{}]=====================", ThrowableUtil.stackTraceToString(e));
                }
                log.info("=================consumer subscribe success  groupName={}, topics={}, tag={}=====================", consumerProperties.getGroupName(), topic, tag);
                return consumer;
            }
        }
        return bean;
    }
}
