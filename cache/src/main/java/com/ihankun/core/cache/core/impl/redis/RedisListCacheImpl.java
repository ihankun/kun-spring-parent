package com.ihankun.core.cache.core.impl.redis;

import com.ihankun.core.cache.RedisDataType;
import com.ihankun.core.cache.core.ListCache;
import com.ihankun.core.cache.key.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisListCacheImpl <V> extends AbstractRedisCache implements ListCache<V> {

    @Override
    public List<V> pop(CacheKey key, int size) {
        try {

            List<V> list = new ArrayList<>();

            while (true) {
                if (size == 0) {
                    break;
                }

                V pop = (V) getRedisTemplate().opsForList().leftPop(key.get());
                list.add(pop);
                size--;
            }

            return list;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean add(CacheKey key, V value) {
        return add(key, value, MAX_EXPIRE, TimeUnit.DAYS);
    }

    @Override
    public boolean add(CacheKey key, V value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForList().leftPush(key.get(), value);
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(CacheKey key, V value) {
        try {
            getRedisTemplate().opsForList().remove(key.get(), 1, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean save(CacheKey key, List<V> values, Long expire) {
        validate(key, values, expire, TimeUnit.SECONDS);
        try {
            getRedisTemplate().opsForList().leftPushAll(key.get(), values);

            expire(key, expire);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<V> get(CacheKey key) {
        ListOperations<String, V> ops = getRedisTemplate().opsForList();
        return ops.range(key.get(), 0, ops.size(key.get()));
    }

    @Override
    public boolean del(CacheKey key) {
        try {
            getRedisTemplate().delete(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(CacheKey key, List<V> value) {
        return update(key, value, MAX_EXPIRE, TimeUnit.DAYS);
    }

    @Override
    public boolean update(CacheKey key, List<V> value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            del(key);
            getRedisTemplate().opsForList().leftPushAll(key.get(), value);
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean expire(CacheKey key, Long expire) {
        try {
            getRedisTemplate().expire(key.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(CacheKey key) {
        try {
            Long size = getRedisTemplate().opsForList().size(key.get());
            if (size == null || size == 0) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    @Override
    protected Long size(CacheKey key) {
        try {
            return getRedisTemplate().opsForList().size(key.get());
        } catch (Exception e) {
            log.error("list 获取大小失败", e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.LIST;
    }
}
