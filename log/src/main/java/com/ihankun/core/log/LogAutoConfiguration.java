package com.ihankun.core.log;

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
@ConfigurationProperties(prefix = "kun.log")
@ComponentScan(basePackageClasses = LogAutoConfiguration.class)
public class LogAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("LogAutoConfiguration.init.start");
    }
}
