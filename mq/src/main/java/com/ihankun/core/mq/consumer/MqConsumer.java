package com.ihankun.core.mq.consumer;

import com.ihankun.core.mq.config.MqProperties;

import java.util.List;

/**
 * @author hankun
 */
public interface MqConsumer {

    /**
     * 初始化消费者
     *
     * @param config
     * @param listenerList
     */
    void initialize(MqProperties config, List<ConsumerListener> listenerList);
}
