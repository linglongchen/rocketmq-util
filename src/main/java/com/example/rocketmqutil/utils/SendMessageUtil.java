package com.example.rocketmqutil.utils;

import com.alibaba.fastjson.JSON;
import io.netty.util.internal.ThrowableUtil;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
     * @param topic 消息主题
     * @param tag 消息标签
     * @param msgBody 消息体
     * @return
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    public void sendSyncMessage(String topic,String tag,Object msgBody) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("==========send message success,topic:{},tag:{},result：{}================",topic,tag,JSON.toJSONString(sendMsg));
    }

    /**
     * 异步发送消息
     * @param topic 消息主题
     * @param tag 消息标签
     * @param msgBody 消息体
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    public void sendASyncMessage(String topic, String tag, Object msgBody) throws RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        defaultMQProducer.send(sendMsg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==========send message success,topic:{},tag:{},result：{}================",topic,tag,JSON.toJSONString(sendResult));
            }
            @Override
            public void onException(Throwable e) {
                log.info("==========send message success,topic:{},tag:{},result：{}================",topic,tag, ThrowableUtil.stackTraceToString(e));
            }
        });
    }

    /**
     * 延迟消息
     * @param topic 消息主题
     * @param tag 消息标签
     * @param delayTime 延迟时间
     * @param msgBody 消息体
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    public void sendDelayMessage(String topic,String tag,long delayTime,Object msgBody) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        long curTime = System.currentTimeMillis() + delayTime;
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("==========send message success,topic:{},tag:{},result：{}================",topic,tag,JSON.toJSONString(sendResult));
    }
}
