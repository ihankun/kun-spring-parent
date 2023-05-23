package com.ihankun.core.spring.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.spring")
@ComponentScan(basePackageClasses = SpringCloudAutoConfiguration.class)
public class SpringCloudAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("SpringCloudAutoConfiguration.init.start");
    }
}
