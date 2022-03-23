package com.example.rocketmqutil.consumer;

import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  chenlingl
 * @date  2022/3/23 9:35
 * @version 1.0
 */
public class AbstractOrderlyRocketMqConsumer<T> implements MessageListenerOrderly {

    private static final Logger log = LoggerFactory.getLogger(AbstractOrderlyRocketMqConsumer.class);


    protected void consume(T object) {
    }

    protected void consume(T obj, Message message) {
        this.consume(obj);
    }


    @Override public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        return null;
    }
}
