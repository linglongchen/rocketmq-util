package com.example.rocketmqutil.event;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chenlingl
 */
public class RocketMqApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(RocketMqApplicationEvent.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, DefaultMQPushConsumer> beansOfType = applicationContext.getBeansOfType(DefaultMQPushConsumer.class);
        beansOfType.values().forEach(consumer -> {
            try {
                consumer.start();
            } catch (MQClientException e) {
                log.error("================消费者启动失败=================");
            }
            log.info("==========================消费者启动，订阅关系：[{}]====================",consumer.getSubscription());
        });
    }
}
