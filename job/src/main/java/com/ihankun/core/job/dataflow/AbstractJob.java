package com.ihankun.core.job.dataflow;

import com.ihankun.core.commons.utils.ServerStateUtil;
import com.ihankun.core.base.help.SpringHelpers;
import com.ihankun.core.job.dataflow.domain.JobDomainHolder;
import com.ihankun.core.job.dataflow.domain.JobDomainItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractJob {

    private static final String DOMAIN_PROTOCOL_SPLIT = "://";
    private static final String DOMAIN_PORT_SPLIT = ":";

    /**
     * 是否灰度节点
     *
     * @return
     */
    protected String isGray() {
        String grayMark = ServerStateUtil.getGrayMark();
        log.info("灰度标识：{}", grayMark);
        return grayMark;
    }

    /**
     * 获取所有医院配置
     *
     * @return
     */
    protected List<JobDomainItem> getAllHospital() {

        ApplicationContext context = SpringHelpers.context();
        if (context == null) {
            log.error("AbstractJob.context.null");
            return null;
        }
        JobDomainHolder holder = context.getBean(JobDomainHolder.class);

        List<JobDomainItem> list = holder.getAllHospital();
        if (null == list || list.isEmpty()) {
            log.error("AbstractJob.getAllHospital.list.null,未获取到所有医院的配置，请检查是否引入配置文件 config-common-domain.properties");
            throw new NullPointerException("未获取到所有医院的配置，请检查是否引入配置文件 config-common-domain.properties");
        }
        List<JobDomainItem> collect = list.stream().filter(item -> item.getIsSupportJob() == null || item.getIsSupportJob().booleanValue() == true).collect(Collectors.toList());

        return collect;

    }

    /**
     * 格式化 domain 域名，去掉端口
     *
     * @param domain
     * @return
     */
    protected static String formatDomain(String domain) {

        if (!StringUtils.isEmpty(domain) && domain.contains(DOMAIN_PROTOCOL_SPLIT)) {
            domain = domain.split(DOMAIN_PROTOCOL_SPLIT)[1];
        }

        if (!StringUtils.isEmpty(domain) && domain.contains(DOMAIN_PORT_SPLIT)) {
            domain = domain.split(DOMAIN_PORT_SPLIT)[0];
        }
        return domain;
    }
}
