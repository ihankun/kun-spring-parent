package com.ihankun.core.mq.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.ihankun.core.base.context.DomainContext;
import com.ihankun.core.base.context.GrayContext;
import com.ihankun.core.base.context.LoginUserContext;
import com.ihankun.core.base.context.LoginUserInfo;
import com.ihankun.core.base.utils.ServerStateUtil;
import com.ihankun.core.base.utils.SpringHelpers;
import com.ihankun.core.log.context.TraceLogContext;
import com.ihankun.core.log.enums.LogTypeEnum;
import com.ihankun.core.mq.config.GrayMark;
import com.ihankun.core.mq.config.MqProperties;
import com.ihankun.core.mq.consumer.AbstractConsumer;
import com.ihankun.core.mq.consumer.ConsumerListener;
import com.ihankun.core.mq.consumer.ConsumerListenerConfig;
import com.ihankun.core.mq.consumer.MqConsumer;
import com.ihankun.core.mq.message.KunMqMsg;
import com.ihankun.core.mq.message.KunReceiveMessage;
import com.ihankun.core.mq.producer.KunTopic;
import com.ihankun.core.mq.producer.bean.TopicConfigInfo;
import com.ihankun.core.mq.rocketmq.RocketMqAdmin;
import com.ihankun.core.mq.rocketmq.consumer.KunMqPushConsumer;
import com.ihankun.core.mq.rocketmq.consumer.strategy.KunAllocateMessageQueueStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.common.subscription.SubscriptionGroupConfig;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author hankun
 */
@Component("rocketmqConsumer")
@Slf4j
public class RocketMqConsumer extends AbstractConsumer implements MqConsumer {
    private static final String RETRY = "RETRY";

    private Map<String, DefaultMQPushConsumer> consumers = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("RocketMqConsumer.init");
    }

    /**
     * 监听初始化，遍历所有实现ConsumerListener接口的实现，对不同的topic+tags进行注册
     * 注意：统一个topic下不同tags的消费，要设置不同的消费组，否则会发生消费异常
     *
     * @param config       配置
     * @param listenerList 监听器
     */
    @Override
    public void initialize(MqProperties config, List<ConsumerListener> listenerList) {
        if (listenerList == null || listenerList.size() == 0) {
            log.error("listenerList is null,place implement the interface 'ConsumerListener' and add it to spring " +
                    "ApplicationContext");
            return;
        }

        for (ConsumerListener<?> listener : listenerList) {
            String consumerGroupName = getGroupName(listener);
            buildConsumer(config, consumerGroupName, listener);
        }
    }

    private void buildConsumer(MqProperties config, String consumerGroupName, ConsumerListener<?> listener) {
        if (consumers.containsKey(consumerGroupName)) {
            log.info("RocketMqConsumer.buildConsumer.prod.consumer.exists!name={}", consumerGroupName);
            return;
        }

        String grayMark = Optional.ofNullable(ServerStateUtil.getGrayMark()).orElse("false");
        // 如果当前节点为灰灰度客户端，则不进行初始化操作（灰灰度客户端不消费消息）
        DefaultMQPushConsumer consumer = buildConsumerTask(config, consumerGroupName, listener, grayMark);
        consumers.put(consumerGroupName, consumer);
        log.info("RocketMqConsumer.buildConsumer.build.consumer!name={}", consumerGroupName);
    }


    private DefaultMQPushConsumer buildConsumerTask(MqProperties config, String consumerGroupName, ConsumerListener listener, String grayMark) {
        // 初始化消费者
        DefaultMQPushConsumer consumer = initConsumer(config, consumerGroupName, grayMark, listener);

        // 配置订阅信息
        // 手动创建订阅关系配置信息，用于设置%RETRY%topic的队列大小以便消费端消息重发的灰度实现
        boolean createResult = this.createSub(config, consumerGroupName, listener);
        if (!createResult) {
            log.error(String.format("创建或者更新consumerId[%s]订阅信息时发生异常！！！使用服务端默认的订阅信息", consumerGroupName));
        }
        String subscribeTags = GrayMark.buildConsumerTags(listener.subscribeTags());
        try {
            consumer.subscribe(listener.subscribeTopic(), subscribeTags);
        } catch (MQClientException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        RocketMqConsumer point = this;
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {

            MDC.put(TraceLogContext.LOG_TYPE, LogTypeEnum.MQ.getValue());
            String topic = context.getMessageQueue().getTopic();
            log.info("RocketMqConsumer.received,topic={},list={}", topic, list);
            //构造MQ消息
            List<KunMqMsg> mqMsgs = convertMessage(list);

            if (!CollectionUtils.isEmpty(mqMsgs)) {
                //设置上下文域名信息，用于接收数据后能够自动切换数据源
                String telnet = mqMsgs.get(0).getTelnet();
                if (!StringUtils.isEmpty(telnet)) {
                    DomainContext.mock(telnet);
                }

                //设置上下文登录用户信息
                LoginUserInfo loginUserInfo = mqMsgs.get(0).getLoginUserInfo();
                if (loginUserInfo != null) {
                    LoginUserContext.mock(loginUserInfo);
                }

                String gray = mqMsgs.get(0).getGray();
                GrayContext.mock(gray);
                log.info("RocketMqConsumer.context.info=[telnet={},gray={}]", telnet, gray);
            }

            //构造Receive消息
            List<KunReceiveMessage> msgList = convertToReceiveMessage(listener, point, listener.config(), mqMsgs);
            //回调
            try {
                boolean result = true;
                if (!CollectionUtils.isEmpty(msgList)) {
                    result = listener.receive(msgList);
                }
                //消费成功
                if (result) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                //消费失败
                if (listener.config() != null && listener.config().isIdempotent()) {
                    for (KunReceiveMessage message : msgList) {
                        resetConsumed(getIdempotentKey(listener.config(), message.getMessageId(), message.getData()));
                    }
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } finally {
                MDC.remove(TraceLogContext.LOG_TYPE);
                TraceLogContext.reset();
                DomainContext.clear();
                LoginUserContext.clear();
                GrayContext.clear();
            }
        });
        log.info("RocketMqConsumer.buildConsumerTask,consumer={},groupName={},topic={},tags={}", listener.getClass().getSimpleName(), consumerGroupName, listener.subscribeTopic(), subscribeTags);
        try {
            consumer.start();
            // consumer.getDefaultMQPushConsumerImpl().doRebalance();
            return consumer;
        } catch (MQClientException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private DefaultMQPushConsumer initConsumer(MqProperties config, String consumerGroupName, String grayMark, ConsumerListener listener) {
        // 自定义队列的负载均衡策略
        AllocateMessageQueueStrategy allocateMessageQueueStrategy = new KunAllocateMessageQueueStrategy(config);

        DefaultMQPushConsumer consumer = new KunMqPushConsumer(consumerGroupName, null, allocateMessageQueueStrategy, grayMark);
        consumer.setInstanceName("consumer-" + UtilAll.getPid());

        consumer.setNamesrvAddr(config.getUrl());
        consumer.setVipChannelEnabled(false);
        if (config.getTimeOut() != null) {
            consumer.setConsumeTimeout(config.getTimeOut());
        }

        // 设置消费者线程
        consumer.setConsumeThreadMin(config.getConsumeThreadMin());
        consumer.setConsumeThreadMax(config.getConsumeThreadMax());

        // 广播模式设置
        ConsumerListenerConfig consumerListenerConfig = listener.config();
        if (consumerListenerConfig.isBoardCast()) {
            if (consumerListenerConfig.isIdempotent()) {
                log.error("RocketMqConsumer.buildConsumer.broadcast.error,config={}", consumerListenerConfig);
            }
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }

        return consumer;
    }

    private String getGroupName(ConsumerListener<?> listener) {
        Environment environment = SpringHelpers.context().getEnvironment();
        String consumerGroupName = environment.getProperty("spring.application.name", "default_consumer_group");
        return consumerGroupName + listener.subscribeTopic() + listener.subscribeTags();
    }

    /**
     * 数据格式转换
     *
     * @param list
     * @return
     */
    private List<KunMqMsg> convertMessage(List<MessageExt> list) {
        List<KunMqMsg> result = new ArrayList<>(list.size());
        for (MessageExt messageExt : list) {

            String body = new String(messageExt.getBody(), Charset.defaultCharset());

            KunMqMsg msg = JSON.parseObject(body, KunMqMsg.class);
            result.add(msg);
        }
        return result;
    }


    /**
     * 构造返回消息
     *
     * @param listener
     * @param point
     * @param config
     * @param list
     * @return
     */
    private List<KunReceiveMessage> convertToReceiveMessage(ConsumerListener listener, RocketMqConsumer point, ConsumerListenerConfig config, List<KunMqMsg> list) {

        List<KunReceiveMessage> messageList = new ArrayList<>(list.size());
        for (KunMqMsg msg : list) {

            KunReceiveMessage receive = new KunReceiveMessage();

            receive.setData(objectToClass(msg.getData(), listener));

            receive.setMessageId(msg.getMessageId());
            receive.setTopic(KunTopic.builder().topic(msg.getTopic()).tags(msg.getTag()).build());
            //处理幂等
            if (config != null && config.isIdempotent()) {
                //如果未设置幂等key，则以消息id为key
                String idempotentKey = getIdempotentKey(config, msg.getMessageId(), msg.getData());
                boolean beConsumed = checkConsumed(idempotentKey);
                receive.setBeConsumed(beConsumed);
                if (!beConsumed) {
                    point.confirmConsumed(idempotentKey);
                }
            }
            messageList.add(receive);

        }
        return messageList;
    }

    /**
     * 创建consumer group的订阅信息
     * @param mqProperties
     * @param consumerId
     * @param listener
     * @return
     */
    private boolean createSub(MqProperties mqProperties, String consumerId, ConsumerListener<?> listener) {
        boolean createResult;
        DefaultMQAdminExt mqAdminExt = new DefaultMQAdminExt();
        mqAdminExt.setNamesrvAddr(mqProperties.getUrl());
        SubscriptionGroupConfig subConfig = new SubscriptionGroupConfig();
        subConfig.setGroupName(consumerId);
        // 设置retryQueueNums为灰度队列长度的2倍
        subConfig.setRetryQueueNums(mqProperties.getGraySize() * 2);
        try {
            mqAdminExt.start();

            ClusterInfo clusterInfo = RocketMqAdmin.fetchClusterInfo(mqAdminExt);
            if (Objects.isNull(clusterInfo)) {
                log.error(String.format("consumerId[%s]的订阅信息时，无法获取到集群信息！！！", consumerId));
                mqAdminExt.shutdown();
                return false;
            }

            List<TopicConfigInfo> topicConfigInfos = RocketMqAdmin.fetchTopicConfig(mqAdminExt, listener.subscribeTopic(), clusterInfo);
            if (topicConfigInfos.isEmpty()) {
                log.error(String.format("无法获取topic[%s]配置信息！！！", listener.subscribeTopic()));
                mqAdminExt.shutdown();
                return false;
            }

            for (TopicConfigInfo topicConfigInfo : Optional.ofNullable(topicConfigInfos).orElse(Collections.emptyList())) {
                mqAdminExt.createAndUpdateSubscriptionGroupConfig(clusterInfo.getBrokerAddrTable().get(topicConfigInfo.getBrokerNameList().get(0)).selectBrokerAddr(), subConfig);
            }
            createResult = true;
        } catch (Exception e) {
            log.error(String.format("创建或者更新consumerId[%s]订阅信息时发生异常！！！", consumerId), e);
            return false;
        }
        mqAdminExt.shutdown();
        return createResult;
    }
}
