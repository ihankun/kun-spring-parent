package com.ihankun.core.base.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DataSourceRefreshEvent extends ApplicationEvent {

    private final String dbMark;

    private final String domain;

    public DataSourceRefreshEvent(Object source, String dbMark, String domain) {
        super(source);
        this.dbMark = dbMark;
        this.domain = domain;
    }
}
