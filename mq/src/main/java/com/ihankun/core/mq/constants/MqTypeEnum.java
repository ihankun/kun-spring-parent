package com.ihankun.core.mq.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MqTypeEnum {

    /**
     * rocketMq
     */
    ROCKETMQ("rocketmq", "RocketMQ"),
    /**
     * kafka
     */
    KAFKA("kafka", "Kafka");

    private String type;
    private String description;
}
