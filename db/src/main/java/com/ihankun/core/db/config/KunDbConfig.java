package com.ihankun.core.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * SQL相关配置
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(value = "ihankun.database.config")
public class KunDbConfig {
    private int maxRows = 10000;
    private boolean checkSql = true;
}
