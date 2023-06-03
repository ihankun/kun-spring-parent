package com.ihankun.core.mq.rocketmq.producer.strategy.impl;

import com.ihankun.core.mq.rocketmq.producer.strategy.AbstractMsunProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author hankun
 */
public class GgrayKunProducerSelectQueueStrategy extends AbstractMsunProducerSelectQueueStrategy {

    private int graySize;

    public GgrayKunProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 灰灰度消息发送到灰灰度队列
        int queueSize = tpInfo.getMessageQueueList().size();
        List<MessageQueue> ggrayMessageQueueList = tpInfo.getMessageQueueList().subList(queueSize - graySize, queueSize);
        return getMessageQueue(tpInfo, lastBrokerName, ggrayMessageQueueList);
    }
}
