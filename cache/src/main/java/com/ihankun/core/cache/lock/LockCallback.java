package com.ihankun.core.cache.lock;

import com.ihankun.core.base.exception.BusinessException;
import com.ihankun.core.cache.error.KunCacheErrorCode;

/**
 * @author hankun
 */
public interface LockCallback<T> {

    /**
     * 加锁成功后执行方法
     *
     * @return
     * @throws Throwable
     */
    T success();

    /**
     * 默认加锁失败时执行方法。
     *
     * @return
     */
    default T fail() {
        throw BusinessException.build(KunCacheErrorCode.NOT_FOUND_REDISSON);
    }
}
