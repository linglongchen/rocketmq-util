package com.example.rocketmqutil.config;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.rocketmqutil.annotation.RocketMqConsumerListener;
import com.example.rocketmqutil.properties.ConsumerProperties;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

/**
 *
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/23 15:09
 */
public abstract class AbstractRocketMqConsumerConfig {
    /**
     * 获取重试次数
     * @param annotationEle 实体类
     * @return
     */
    public int getReConsumeTimes(AnnotatedElement annotationEle){
        RocketMqConsumerListener rocketMqListener = (RocketMqConsumerListener) AnnotationUtil.getAnnotation(annotationEle, RocketMqConsumerListener.class);
        if (rocketMqListener.reConsumeTimes() != -1) {
            return rocketMqListener.reConsumeTimes();
        }
        ConsumerProperties consumerProperties = SpringUtil.getBean(ConsumerProperties.class);
        if (Objects.isNull(consumerProperties)) {
            return 26;
        }
        return consumerProperties.getReConsumeTimes();
    }

}
