package com.example.rocketmqutil.utils;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/22 10:55
 */
public class SendMessageUtil {

    private static final Logger log = LoggerFactory.getLogger(SendMessageUtil.class);


    private final DefaultMQProducer defaultMQProducer;

    public SendMessageUtil(DefaultMQProducer producer) {
        this.defaultMQProducer = producer;
    }

    public void sendMessage(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message("mq_topic_test", "tag1", msg.getBytes());
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("==========发送消息结果：{}================",sendResult.toString());
    }
}
