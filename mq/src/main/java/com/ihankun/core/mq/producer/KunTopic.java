package com.ihankun.core.mq.producer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author hankun
 */
@ToString
@Getter
@Builder
public class KunTopic {

    /**
     * 待发送主题，只允许设置一个，不允许为空
     */
    private String topic;

    /**
     * 待发送tags
     */
    private String tags;
}
