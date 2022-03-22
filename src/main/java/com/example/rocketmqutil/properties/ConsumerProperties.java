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
@ConfigurationProperties(value = "rocketmq.consumer")
public class ConsumerProperties {

    private String groupName;
    private String namesrvAddr;
    // 消费者线程数据量
    private Integer consumeThreadMin;
    private Integer consumeThreadMax;
    private Integer consumeMessageBatchMaxSize;
    /**
     * 重试次数
     */
    private Integer reConsumeTimes = 26;
}
