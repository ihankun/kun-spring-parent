package com.ihankun.core.commons.http;

import org.springframework.core.ParameterizedTypeReference;

/**
 * @author hankun
 */
public interface HttpInterface {

    /**
     * get请求
     * @param T 类型
     * @author hankun
     * @return <T>
     */
    <T> T get(String url, ParameterizedTypeReference<T> T);
}
