package com.example.rocketmqutil.controller;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 9:41
 */
@RestController("/producer")
@Slf4j
public class ProducerController {

    @Resource
    private DefaultMQProducer defaultMQProducer;

    @GetMapping("/send")
    public void sendMessage(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message sendMsg = new Message("mq_topic_test", "tag1", msg.getBytes());
        // 默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("==========发送消息结果：{}================",sendResult.toString());
    }
}
