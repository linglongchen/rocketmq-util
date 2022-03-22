package com.example.rocketmqutil.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 9:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq.producer")
public class ProducerProperties {
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 命名服务
     */
    private String namesrvAddr;
    /**
     * 消息最大值
     */
    private Integer maxMessageSize;
    /**
     * 消息发送超时时间
     */
    private Integer sendMsgTimeOut;
    /**
     * 失败重试次数
     */
    private Integer retryTimesWhenSendFailed;

}
