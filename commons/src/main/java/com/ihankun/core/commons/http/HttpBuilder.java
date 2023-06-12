package com.ihankun.core.commons.http;

import com.ihankun.core.commons.http.impl.RestTemplateHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

/**
 * @author hankun
 */
@Slf4j
public class HttpBuilder {

    public static final class HttpBuilderHolder {
        public static HttpType HTTP_TYPE = null;
        public static HttpBuilder HTTP_BUILDER = null;
    }

    private final HttpInterface httpInterface;

    public static HttpBuilder ins(){
        return ins(HttpType.R_COMM);
    }

    public static HttpBuilder ins(HttpType type){
        HttpBuilderHolder.HTTP_TYPE = type;
        if(HttpBuilderHolder.HTTP_BUILDER == null) {
            HttpBuilderHolder.HTTP_BUILDER = new HttpBuilder(HttpBuilderHolder.HTTP_TYPE);
        }
        return HttpBuilderHolder.HTTP_BUILDER;
    }

    public HttpBuilder(HttpType type) {
        HttpInterface http;
        if(type.equals(HttpType.R_COMM)) {
            http = new RestTemplateHttp();
        }else {
            http = null;
        }
        this.httpInterface = http;
    }

    public <T> T get(String url, ParameterizedTypeReference<T> T){
        return httpInterface.get(url, T);
    }
}
