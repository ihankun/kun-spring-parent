//package com.ihankun.core.db.dynamic;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.ds.ItemDataSource;
//import com.ihankun.core.base.events.DataSourceRefreshEvent;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class DataSourceSwitcher implements ApplicationEventPublisherAware {
//
//    public static final String DS_SPLIT = "_";
//
//    @Resource
//    private DynamicRoutingDataSource dataSource;
//
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    @Resource
//    private PropertiesHolder holder;
//
//
//    @EventListener
//    @Async
//    public void listenDestroy(DataSourceRefreshEvent event) {
//        List<String> aliases = holder.getAlias(event.getDbMark());
//        if (CollectionUtils.isEmpty(aliases)) {
//            log.info("DataSourceSwitcher.close.not.have.alias,domain={},dbMark={}", event.getDomain(), event.getDbMark());
//            return;
//        }
//        log.info("DataSourceSwitcher.try.close.dataSource,domain={},aliases={}", event.getDomain(), aliases);
//        for (String alias : aliases) {
//            disable(alias, event.getDomain());
//        }
//    }
//
//    public boolean disable(String alias, String domain) {
//        String name = alias + DS_SPLIT + domain;
//        log.info("DataSourceSwitcher.disable.datasource,name={}", name);
//        return removeDataSource(name);
//    }
//
//    private boolean removeDataSource(String name) {
//        DataSource source = dataSource.getCurrentDataSources().get(name);
//        if (source == null) {
//            log.info("DataSourceSwitcher.dataSource.name.not.exists,name={}", name);
//            return false;
//        }
//        if (source instanceof ItemDataSource) {
//            ItemDataSource itemDataSource = (ItemDataSource) source;
//            //苞米豆没有强制限制数据源访问名称和itemDataSource中的名称一致，此处仅为保险起见
//            if (!itemDataSource.getName().equals(name)) {
//                log.info("DataSourceSwitcher.dataSource.name.not.equal,name={},dataSource={}", name, itemDataSource.getName());
//                return false;
//            }
//            dataSource.removeDataSource(name);
//            //数据源移除事件
//            applicationEventPublisher.publishEvent(new DataSourceRemoveEvent(this, itemDataSource));
//            //苞米豆存在bug，数据源可能关闭失败，再次确认并关闭
//            DataSource realDataSource = itemDataSource.getRealDataSource();
//            if (realDataSource instanceof DruidDataSource) {
//                if (!((DruidDataSource) realDataSource).isClosed()) {
//                    log.info("DataSourceSwitcher.dataSource.start.to.close,name={}", name);
//                    ((DruidDataSource) realDataSource).close();
//                    log.info("DataSourceSwitcher.dataSource.close.directly,name={}", name);
//                }
//            }
//        } else {
//            log.error("DataSourceSwitcher.dataSource.can.not.operate,,name={},type={}", name, source.getClass().getName());
//        }
//        return true;
//    }
//
//    @Override
//    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//}
