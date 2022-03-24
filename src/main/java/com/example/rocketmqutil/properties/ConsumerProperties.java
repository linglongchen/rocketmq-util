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
    /**
     * consumeThreadMin
     */
    private Integer consumeThreadMin;
    private Integer consumeThreadMax;
    private Integer consumeMessageBatchMaxSize;
    /**
     * reConsumeTimes
     */
    private Integer reConsumeTimes = 26;

    /**
     * consumeMethod: push or pull
     */
    private String consumeMethod = "push";
}
