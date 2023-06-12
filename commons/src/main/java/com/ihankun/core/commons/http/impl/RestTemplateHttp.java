package com.ihankun.core.commons.http.impl;

import com.alibaba.fastjson.JSON;
import com.ihankun.core.commons.http.HttpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author hankun
 */
@Slf4j
public class RestTemplateHttp implements HttpInterface {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public <T> T get(String url, ParameterizedTypeReference<T> T){
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> formEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> response;
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        response = restTemplate.exchange(url, HttpMethod.GET, formEntity, T);
        return response.getBody();
    }


}
