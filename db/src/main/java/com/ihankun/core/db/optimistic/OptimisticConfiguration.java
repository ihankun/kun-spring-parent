package com.ihankun.core.db.optimistic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Configuration
@MapperScan(OptimisticConfiguration.SCANER_CLASS_PATH)
public class OptimisticConfiguration {
    public static final String SCANER_CLASS_PATH = "com.ihankun.core.db.optimistic";
}
