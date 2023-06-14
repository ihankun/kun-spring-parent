package com.ihankun.core.commons.http.enums;

/**
 * @author hankun
 */
public enum HttpType {

    /**
     * RestTemplate 通用外部
     */
    R_COMM("rest_template_comm"),
    /**
     * RestTemplate 微服务接口
     * 需要增加鉴权信息
     */
    R_GATEWAY("rest_template_gateway");

    private String type;

    HttpType(String type){
        this.type = type;
    }
}
