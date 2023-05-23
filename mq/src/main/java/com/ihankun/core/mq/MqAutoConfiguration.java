package com.ihankun.core.mq;

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
@ConfigurationProperties(prefix = "kun.mq")
@ComponentScan(basePackageClasses = MqAutoConfiguration.class)
public class MqAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("MqAutoConfiguration.init.start");
    }
}
