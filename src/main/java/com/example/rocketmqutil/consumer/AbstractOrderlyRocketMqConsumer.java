package com.example.rocketmqutil.consumer;

import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson.JSON;
import com.example.rocketmqutil.config.AbstractRocketMqConsumerConfig;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 顺序消费者处理类
 * @author  chenlingl
 * @date  2022/3/23 9:35
 * @version 1.0
 */
public class AbstractOrderlyRocketMqConsumer<T> extends AbstractRocketMqConsumerConfig implements MessageListenerOrderly {

    private static final Logger log = LoggerFactory.getLogger(AbstractOrderlyRocketMqConsumer.class);


    protected void consume(T object) {
    }

    protected void consume(T obj, Message message) {
        this.consume(obj);
    }


    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        for (MessageExt msg : msgs) {
            String msgId = msg.getMsgId();
            String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
            String topic = msg.getTopic();
            String tag = msg.getTags();
            int reConsumeTimes = msg.getReconsumeTimes();
            log.info("msg Topic：[{}],msg Tag：[{}],reConsumeTimes：[{}]，msg body：[{}]，reSend before msgID:{},reSend after msgId :{}", topic, tag,reConsumeTimes, msgBody,msgId, msgId);
            Type type = TypeUtil.getTypeArgument(this.getClass());
            Class<?> clazz = TypeUtil.getClass(type);
            Object o = JSON.parseObject(msgBody, clazz);
            try {
                this.consume((T)o,msg);
            }catch (Exception e) {
                if (reConsumeTimes >= this.getReConsumeTimes(this.getClass())) {
                    return ConsumeOrderlyStatus.SUCCESS;
                }
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
