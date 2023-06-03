package com.ihankun.core.cache;

import com.ihankun.core.cache.core.impl.redis.RedisListCacheImpl;
import com.ihankun.core.cache.core.impl.redis.RedisMapCacheImpl;
import com.ihankun.core.cache.core.impl.redis.RedisSetCacheImpl;
import com.ihankun.core.cache.core.impl.redis.RedisStringCacheImpl;

/**
 * @author hankun
 */
public class CacheBuilder {

    /**
     * 构造不同类型的cache管理器
     *
     * @param type
     * @return
     */
    public static CacheManager build(CacheType type) {
        CacheManager manager = null;
        if (type.equals(CacheType.REDIS)) {
            manager = new CacheManager(new RedisStringCacheImpl(), new RedisMapCacheImpl(), new RedisListCacheImpl(), new RedisSetCacheImpl());
        }
        return manager;
    }
}
