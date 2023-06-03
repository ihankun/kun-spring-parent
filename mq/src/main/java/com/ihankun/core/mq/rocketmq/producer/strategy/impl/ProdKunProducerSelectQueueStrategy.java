package com.ihankun.core.mq.rocketmq.producer.strategy.impl;

import com.ihankun.core.mq.rocketmq.producer.strategy.AbstractMsunProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class ProdKunProducerSelectQueueStrategy extends AbstractMsunProducerSelectQueueStrategy {
    private int graySize;

    public ProdKunProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 生产消息发送到生产队列
        int queueSize = tpInfo.getMessageQueueList().size();
        List<MessageQueue> prodMessageQueueList = tpInfo.getMessageQueueList().subList(graySize, queueSize - graySize);
        return getMessageQueue(tpInfo, lastBrokerName, prodMessageQueueList);
    }
}
