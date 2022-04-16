### 作用



### 功能点



### 如何使用

yml中实现配置文件：
`rocketmq:
  producer:
    groupName: ${spring.application.name}
    namesrvAddr: 127.0.0.1:9876
    maxMessageSize: 4096
    sendMsgTimeOut: 3000
    retryTimesWhenSendFailed: 2
  consumer:
    groupName: ${spring.application.name}
    namesrvAddr: 127.0.0.1:9876
    consumeThreadMin: 4
    consumeThreadMax: 32
    consumeMessageBatchMaxSize: 1
    reConsumeTimes: 3`

![]( "")

![]( "")

### 测试结果




### 成员协作


### 更多

