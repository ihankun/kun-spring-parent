package com.ihankun.core.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(value = "ihankun.database.flow.control")
public class FlowControlConfig {

    private Boolean enable = false;
    private Boolean recordOnly = false;
    private Integer maxWindowSize;
    private Integer cycleSize;
    private Map<String, FlowControlFilter> limits;
}
