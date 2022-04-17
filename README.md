### 作用

开源rocketmq客户端工具，通过该工具实现发送消息、消费消息快速接入，封装了消息消费的同步消费和顺序消费的两种方式，其它方式可以自己扩展。

### 功能点

- 同步消费实现

实现类：com.example.rocketmqutil.consumer.AbstractConcurrentlyRocketMqConsumer

通过该实现类接入消息同步消费逻辑，主要处理消费状态，消费异常的重试。暂未接入消息重推功能，消息重推规划消费异常时将消息发送到指定topic进行消费，消费消息存入数据库，然后做手动重推。

- 顺序消费实现

实现类：com.example.rocketmqutil.consumer.AbstractOrderlyRocketMqConsumer

通过该实现类接入消息顺序消费的逻辑，核心处理依然是rocketmq处理，封装的逻辑做消费异常的重试处理。

- 消费消息接入

注解实现消费消息：com.example.rocketmqutil.annotation.RocketMqConsumerListener

消费消息通过注解实现，消费注解相关字段如下：

```java
public @interface RocketMqConsumerListener {
    /**
     * 消息主题
     * @return
     */
    String topic();

    /**
     * 消息标签
     * @return
     */
    String tag();

    /**
     * 消费者消费线程数：默认5
     * @return
     */
    int threadNum() default 5;

    /**
     * 消息模型：集群和广播，默认集群
     * @return
     */
    String messageModel() default MessageModelConstant.CLUSTERING;

    /**
     * 消息类型：同步和顺序，默认同步
     * @return
     */
    String messageType() default MessageTypeConstant.CONCURRENTLY;

    /**
     * 重试次数
     * @return 次数
     */
    int reConsumeTimes() default -1;

}

```



- 发送消息工具

实现类：com.example.rocketmqutil.utils.SendMessageUtil

发送消息通过封装的工具类实现，该工具类中实现了同步发送、异步发送、延迟发送(暂还未实现)三种方式。

- 无缝接入Spring

该工具类无需做其它注入配置，加入该starter依赖，服务启动即可自动注入Spring。

### 如何使用

yml中实现配置文件：

```yaml
rocketmq-config:
  namesrvAddr: 127.0.0.1
rocketmq:
  producer:
    groupName: producerGroup
    namesrvAddr: ${rocketmq-config.namesrvAddr}
    maxMessageSize: 4096
    sendMsgTimeOut: 3000
    retryTimes: 2
  consumer:
    groupName: consumerGroup
    namesrvAddr: ${rocketmq-config.namesrvAddr}
    consumeThreadMin: 4
    consumeThreadMax: 32
    consumeMessageBatchMaxSize: 1
    reConsumeTimes: 3
```



发送消息实现：

```java
@RestController("/producer")
@Slf4j
public class ProducerController {

    @Resource
    private SendMessageUtil sendMessageUtil;

    @GetMapping("/send")
    public void sendMessage(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        sendMessageUtil.sendSyncMessage("mq_topic_test","tag7",msg);
    }
}

```

消费消息实现：

```java
@Slf4j
@Component
public class RocketConsumer {


    @RocketMqConsumerListener(topic = "mq_topic_test",tag = "tag7",reConsumeTimes = 3)
    class test1 extends AbstractConcurrentlyRocketMqConsumer<String> {
        @Override
        protected void consume(String object) {
            log.info("================消息消费：{}===========",object);
            if (object.equals("111")) {
                throw new RuntimeException();
            }
        }
    }
}
```



### 测试结果

- 同步消费

消费结果：

![image-20220417192109561](https://img-blog.csdnimg.cn/c6cb56122f0740978ed01fdaf0b8a119.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6ZmI5rGk5aeG,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)

- 异常消费重试

生产者实现：

```java
@RestController("/producer")
@Slf4j
public class ProducerController {

    @Resource
    private SendMessageUtil sendMessageUtil;

    @GetMapping("/send")
    public void sendMessage(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        sendMessageUtil.sendSyncMessage("mq_topic_test","tag7",msg);
    }
}
```

消费者实现：

```java
@Slf4j
@Component
public class RocketConsumer {

    @RocketMqConsumerListener(topic = "mq_topic_test",tag = "tag7",reConsumeTimes = 3)
    class test1 extends AbstractConcurrentlyRocketMqConsumer<String> {
        @Override
        protected void consume(String object) {
            log.info("================消息消费：{}===========",object);
            if (object.equals("111")) {
                throw new RuntimeException();
            }
        }
    }
}
```

消费结果：

![image-20220417193056595](https://img-blog.csdnimg.cn/e3944721bf5643ad8666084dbe0be3b0.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6ZmI5rGk5aeG,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)

- 顺序消费

生产者实现：

```java
@RestController("/producer")
@Slf4j
public class ProducerController {

    @Resource
    private SendMessageUtil sendMessageUtil;

    @GetMapping("/sendOrderly")
    public void sendOrderlyMessage(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        sendMessageUtil.sendSyncMessage("mq_topic_test","tag2",msg);
    }
}

```

消费者实现：

```java
@Slf4j
@Component
public class RocketConsumer {

    @RocketMqConsumerListener(topic = "mq_topic_test",tag = "tag2",messageType = MessageTypeConstant.ORDERLY)
    class test8 extends AbstractOrderlyRocketMqConsumer<String> {
        @Override
        protected void consume(String object) {
            log.info("================消息消费：{}===========",object);
            if (object.equals("111")) {
                throw new RuntimeException();
            }
        }
    }
}
```



消费结果：

![image-20220417193648667](https://img-blog.csdnimg.cn/dfe50f52dd35469aaf270f138d61ca42.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6ZmI5rGk5aeG,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)

### 成员协作
欢迎一起完善该工具！
### 更多