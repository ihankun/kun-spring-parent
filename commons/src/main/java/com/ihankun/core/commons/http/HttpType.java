package com.ihankun.core.commons.http;

/**
 * @author hankun
 */
public enum HttpType {

    /**
     * RestTemplate 通用外部
     */
    R_COMM("rest_template_comm"),
    /**
     * RestTemplate www.mxnzp.com 接口
     */
    R_MXNZP("rest_template_mxnzp"),
    /**
     * RestTemplate 微服务接口
     */
    R_GATEWAY("rest_template_gateway");
    private String type;

    HttpType(String type){
        this.type = type;
    }
}
