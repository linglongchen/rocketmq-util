package com.example.rocketmqutil.config;

import com.example.rocketmqutil.event.RocketMqApplicationEvent;
import com.example.rocketmqutil.processor.RocketMqConsumerProcessor;
import com.example.rocketmqutil.properties.ConsumerProperties;
import com.example.rocketmqutil.properties.ProducerProperties;
import com.example.rocketmqutil.utils.SendMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * RocketMqUtil's configuration
 * @author chenlingl
 */
@Configuration
public class RocketMqConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RocketMqConfiguration.class);

    /**
     * init DefaultMQProducer and inject this defaultMQProducer to beanFactory
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQProducer init(ProducerProperties producerProperties) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerProperties.getGroupName());
        producer.setNamesrvAddr(producerProperties.getNamesrvAddr());
        producer.setVipChannelEnabled(false);
        producer.setMaxMessageSize(producerProperties.getMaxMessageSize());
        producer.setSendMsgTimeout(producerProperties.getSendMsgTimeOut());
        producer.setRetryTimesWhenSendAsyncFailed(producerProperties.getRetryTimesWhenSendFailed());
        producer.start();
        log.info("===============rocketmq producer server start success!===============");
        return producer;
    }

    @Bean
    @ConditionalOnBean({ProducerProperties.class})
    public SendMessageUtil sendMessageUtil(ProducerProperties producerProperties) throws MQClientException {
        return new SendMessageUtil(this.init(producerProperties));
    }

    @Bean
    public RocketMqConsumerProcessor rocketMqConsumerProcessor() {
        return new RocketMqConsumerProcessor();
    }


    @Bean
    public RocketMqApplicationEvent rocketMqApplicationEvent() {
        return new RocketMqApplicationEvent();
    }

}
