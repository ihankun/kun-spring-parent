package com.ihankun.core.nosql.es;

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
@ConfigurationProperties(prefix = "kun.es")
@ComponentScan(basePackageClasses = EsAutoConfiguration.class)
public class EsAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("EsAutoConfiguration.init.start");
    }
}
