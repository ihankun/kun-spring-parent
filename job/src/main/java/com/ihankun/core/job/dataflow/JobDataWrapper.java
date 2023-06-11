package com.ihankun.core.job.dataflow;

import com.ihankun.core.job.dataflow.domain.JobDomainItem;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hankun
 */
@Data
public class JobDataWrapper<T> implements Serializable {

    /**
     * 待处理数据
     */
    private T data;

    /**
     * 链路追踪Id
     */
    private String traceId;

    /**
     * 域名信息
     */
    private JobDomainItem domain;
}
