package com.ihankun.core.mq.rocketmq.producer.strategy.impl;

import com.ihankun.core.mq.rocketmq.producer.strategy.AbstractMsunProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class GrayKunProducerSelectQueueStrategy extends AbstractMsunProducerSelectQueueStrategy {
    private int graySize;

    public GrayKunProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 灰度消息发送到灰度队列
        List<MessageQueue> grayMessageQueueList =  tpInfo.getMessageQueueList().subList(0, graySize);
        return getMessageQueue(tpInfo, lastBrokerName, grayMessageQueueList);
    }
}
