package com.ihankun.core.mq.constants;

public enum EnvMark {

    PROD("prod", "生产环境"),
    GRAY("gray", "灰度环境"),
    ;

    private String env;

    private String desc;

    EnvMark(String env, String desc) {
        this.env = env;
        this.desc = desc;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
