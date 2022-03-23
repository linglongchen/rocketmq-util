package com.example.rocketmqutil.utils;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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

    /**
     * sync send normal message
     * @param topic
     * @param tag
     * @param o
     * @return
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    public SendResult sendSyncMessage(String topic,String tag,Object o) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(o));
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("==========send message result：{}================",sendResult.toString());
        return sendResult;
    }
}
