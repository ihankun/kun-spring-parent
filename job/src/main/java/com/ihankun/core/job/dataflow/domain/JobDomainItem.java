package com.ihankun.core.job.dataflow.domain;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class JobDomainItem {

    /**
     * 访问域名
     */
    private String host;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 医院名称
     */
    private String name;

    /**
     * 院内出口IP
     */
    private String localIp;

    /**
     * 是否支持Job
     */
    private Boolean isSupportJob = true;
}
