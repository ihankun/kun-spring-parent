package com.ihankun.core.mq.producer;

import com.ihankun.core.mq.constants.KunMqSendResult;

/**
 * @author hankun
 */
public interface KunMqSendCallback {

    /**
     * 成功回调
     *
     * @param result
     */
    void success(KunMqSendResult result);

    /**
     * 异常回调
     *
     * @param e
     */
    void exception(Throwable e);
}
