package com.ihankun.core.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ihankun.rule.redis.size.check")
public class RedisSizeCheckProperties {

    /**
     * 是否开启
     */
    private boolean enabled = false;
    /**
     * 配置
     */
    private Map<String, Long> configs;
}
