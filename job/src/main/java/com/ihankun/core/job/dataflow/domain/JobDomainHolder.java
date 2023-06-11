package com.ihankun.core.job.dataflow.domain;

import com.ihankun.core.base.context.LoginUserContext;
import com.ihankun.core.base.context.LoginUserInfo;
import com.ihankun.core.base.error.CommonErrorCode;
import com.ihankun.core.base.exception.BusinessException;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Configuration
@ConfigurationProperties(value = "ihankun.domain")
public class JobDomainHolder {

    @Setter
    private Map<String, JobDomainItem> config;


    /**
     * 获取所有医院
     *
     * @return
     */
    public List<JobDomainItem> getAllHospital() {

        if (config == null) {
            return null;
        }

        List<JobDomainItem> list = config.values().stream().filter(item -> item.getIsSupportJob() == null || Boolean.TRUE.equals(item.getIsSupportJob())).collect(Collectors.toList());

        return list;
    }


    /**
     * 根据Host获取配置列表
     * host跟医院ID存在一对多的情况
     *
     * @param host 域名
     * @return
     */
    public List<JobDomainItem> getByHost(String host) {
        if (config == null) {
            return null;
        }

        List<JobDomainItem> domainItemList = config.values().stream().filter(
                item -> item.getHost().equals(host)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(domainItemList)) {
            throw BusinessException.build(CommonErrorCode.DOMAIN_NOT_FOUND);
        }
        return domainItemList;
    }


    /**
     * 根据当前登录用户信息获取域名配置信息
     *
     * @return
     */
    public JobDomainItem getByLoginUserInfo() {

        LoginUserInfo userInfo = LoginUserContext.getLoginUserInfo();
        if (userInfo == null) {
            return null;
        }

        Long orgId = userInfo.getOrgId();

        JobDomainItem item = this.getById(orgId);

        return item;
    }

    /**
     * 根据医院ID获取配置
     * 医院ID和host是一对一
     *
     * @param orgId
     * @return
     */
    public JobDomainItem getById(Long orgId) {
        if (config == null) {
            return null;
        }

        for (JobDomainItem item : config.values()) {
            if (item.getOrgId().equals(orgId)) {
                return item;
            }
        }
        throw BusinessException.build(CommonErrorCode.DOMAIN_NOT_FOUND);
    }
}
