package com.ihankun.core.base;

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
@ConfigurationProperties(prefix = "kun.base")
@ComponentScan(basePackageClasses = BaseAutoConfiguration.class)
public class BaseAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("BaseAutoConfiguration.init.start");
    }
}
