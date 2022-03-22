package com.example.rocketmqutil.processor;

import com.example.rocketmqutil.annotation.RocketMqConsumerListener;
import com.example.rocketmqutil.properties.ConsumerProperties;
import com.example.rocketmqutil.consumer.AbstractRocketMqConsumer;
import javax.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 *
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
        if (bean instanceof AbstractRocketMqConsumer) {
            RocketMqConsumerListener listener = bean.getClass().getAnnotation(RocketMqConsumerListener.class);
            if (listener == null) {
                BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
            } else {
                AbstractRocketMqConsumer abstractRocketMqConsumer = (AbstractRocketMqConsumer) bean;
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerProperties.getGroupName());
                String topic = listener.topic();
                String tag = listener.tag();
                consumer.setNamesrvAddr(consumerProperties.getNamesrvAddr());
                consumer.setConsumeThreadMin(listener.threadNum());
                consumer.setConsumeThreadMax(consumerProperties.getConsumeThreadMax());
                consumer.setConsumeMessageBatchMaxSize(consumerProperties.getConsumeMessageBatchMaxSize());
                // 设置监听
                consumer.registerMessageListener(abstractRocketMqConsumer);
                /**
                 * 设置consumer第一次启动是从队列头部开始还是队列尾部开始
                 * 如果不是第一次启动，那么按照上次消费的位置继续消费
                 */
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                consumer.setMessageModel(MessageModel.CLUSTERING);
                try {
                    consumer.subscribe(topic,tag);
                } catch (MQClientException e) {
                    log.error("===================consumer订阅失败!=====================");
                }
                log.info("=================consumer 订阅成功 groupName={}, topics={}, tag={}=====================", consumerProperties.getGroupName(), topic, tag);
                return consumer;
            }
        }
        return bean;
    }
}