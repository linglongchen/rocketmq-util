package com.example.rocketmqutil.service;

import com.example.rocketmqutil.annotation.RocketMqConsumerListener;
import com.example.rocketmqutil.consumer.AbstractRocketMqConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 16:23
 */
@Slf4j
@RocketMqConsumerListener(topic = "mq_topic_test",tag = "tag1")
public class ConsumerService extends AbstractRocketMqConsumer<String> {
    @Override
    protected void consume(String object) {
        log.info("================消息消费：{}===========",object);
        if (object.equals("111")) {
            throw new RuntimeException();
        }
    }
}
