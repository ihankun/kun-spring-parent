package com.ihankun.core.base.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
@Slf4j
public class DomainContext {

    public static final String DOMAIN_HEADER_NAME = "domain";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new NamedThreadLocal<>(DOMAIN_HEADER_NAME);

    /**
     * 获取当前请求的访问域名
     */
    public static String get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 模拟域名信息
     */
    public static void mock(String gray) {
        if (!StringUtils.isEmpty(gray)) {
            CONTEXT_HOLDER.set(gray);
        }
    }
}
