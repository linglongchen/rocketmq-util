package com.example.rocketmqutil.utils;

import com.alibaba.fastjson.JSON;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
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

import java.util.concurrent.*;


/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/22 10:55
 */
public class SendMessageUtil {

    private static final Logger log = LoggerFactory.getLogger(SendMessageUtil.class);


    private final DefaultMQProducer defaultMQProducer;

    /**
     * 时间论算法实现延迟消息
     */
    private final Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory());

    public SendMessageUtil(DefaultMQProducer producer) {
        this.defaultMQProducer = producer;
    }

    /**
     * sync send normal message
     * @param topic 消息主题
     * @param tag 消息标签
     * @param msgBody 消息体
     */
    public void sendSyncMessage(String topic,String tag,Object msgBody) {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        try {
            defaultMQProducer.send(sendMsg);
            log.info("send message success,topic:{},tag:{},msgBody：{}",topic,tag,JSON.toJSONString(msgBody));
        } catch (Exception e) {
            log.error("send message failed,topic:{},tag:{},exception：{}",topic,tag,ThrowableUtil.stackTraceToString(e));
        }
    }

    /**
     * 异步发送消息
     * @param topic 消息主题
     * @param tag 消息标签
     * @param msgBody 消息体
     */
    public void sendASyncMessage(String topic, String tag, Object msgBody) {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        try {
            defaultMQProducer.send(sendMsg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("send message success,topic:{},tag:{},msgBody：{}",topic,tag,JSON.toJSONString(msgBody));
                }
                @Override
                public void onException(Throwable e) {
                    log.info("send message success,topic:{},tag:{},exception：{}",topic,tag, ThrowableUtil.stackTraceToString(e));
                }
            });
        } catch (Exception e) {
            log.error("send message failed,topic:{},tag:{},exception：{}",topic,tag,ThrowableUtil.stackTraceToString(e));
        }
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
    public void sendDelayMessage(String topic,String tag,long delayTime,Object msgBody) {
        Message sendMsg = new Message(topic, tag, JSON.toJSONBytes(msgBody));
        try {
            log.info("======receive message time ：{}=======",System.currentTimeMillis());
            timer.newTimeout(timeout -> {
                SendResult sendResult = defaultMQProducer.send(sendMsg);
                log.info("send message success,topic:{},tag:{},msgBody：{}",topic,tag,JSON.toJSONString(msgBody));
            },delayTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("send message failed,topic:{},tag:{},exception：{}",topic,tag,ThrowableUtil.stackTraceToString(e));
        }
    }

}
