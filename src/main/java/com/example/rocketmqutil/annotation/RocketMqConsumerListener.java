package com.example.rocketmqutil.annotation;

import com.example.rocketmqutil.constants.MessageModelConstant;
import com.example.rocketmqutil.constants.MessageTypeConstant;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * @author chenlingl
 * @version 1.0
 * @date 2022/3/21 15:57
 */
@Component
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RocketMqConsumerListener {
    String topic();

    String tag();

    int threadNum() default 5;

    String messageModel() default MessageModelConstant.CLUSTERING;

    String messageType() default MessageTypeConstant.CONCURRENTLY;

    /**
     * 重试次数
     * @return 次数
     */
    int reConsumeTimes() default 26;

}
