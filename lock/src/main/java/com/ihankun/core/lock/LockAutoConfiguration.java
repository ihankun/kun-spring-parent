package com.ihankun.core.lock;

import com.ihankun.core.cache.CacheBuilder;
import com.ihankun.core.cache.CacheManager;
import com.ihankun.core.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ihankun.lock")
@ComponentScan(basePackageClasses = LockAutoConfiguration.class)
public class LockAutoConfiguration {

    /**
     * 为请求锁的接口声明通用的缓存管理器
     *
     * @return
     */
    @ConditionalOnMissingBean
    @Order
    @Bean
    public CacheManager<String, String> msunRequestLockCacheManager() {
        return CacheBuilder.build(CacheType.REDIS);
    }


    @PostConstruct
    public void init() {
        log.info("LockAutoConfiguration.init.start");
    }
}
