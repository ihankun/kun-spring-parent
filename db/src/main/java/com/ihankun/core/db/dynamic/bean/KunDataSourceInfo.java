package com.ihankun.core.db.dynamic.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 数据源信息
 * @author hankun
 */
@Data
@Builder
public class KunDataSourceInfo {

    private String ip;

    private String port;

    private String password;
}
