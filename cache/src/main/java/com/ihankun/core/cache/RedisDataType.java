package com.ihankun.core.cache;

/**
 * @author hankun
 */
public enum RedisDataType {

    STRING("string"), LIST("list"), SET("set"), MAP("map");

    private String value;

    RedisDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
