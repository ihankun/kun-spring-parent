package com.ihankun.core.mq;

import com.ihankun.core.base.thread.NamedThreadFactory;
import com.ihankun.core.base.utils.SpringHelpers;
import com.ihankun.core.mq.config.GrayMark;
import com.ihankun.core.mq.config.MqProperties;
import com.ihankun.core.mq.constants.MqTypeEnum;
import com.ihankun.core.mq.consumer.ConsumerListener;
import com.ihankun.core.mq.consumer.MqConsumer;
import com.ihankun.core.mq.producer.MqProducer;
import com.ihankun.core.mq.producer.impl.KafkaMqProducer;
import com.ihankun.core.mq.producer.impl.RocketMqProducer;
import com.ihankun.core.mq.rule.MqAccessRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ihankun.mq")
@ComponentScan(basePackageClasses = MqAutoConfiguration.class)
public class MqAutoConfiguration implements ApplicationContextAware {

    private static final String META_MARK = "mark";

    public static final String GRAY_MARK = "gray";

    private MqProperties mq;

    private ApplicationContext context;

    private List<String> loadedListenerList = new ArrayList<>(16);

    public static final int UPDATE_TIME = 5;

    @Resource
    private NacosPropertiesLoader nacosPropertiesLoader;

    @Resource
    private MqAccessRule mqAccessRule;

    @PostConstruct
    public void initConsumer() {
        if (mq == null) {
            log.info("mq.config is null");
            return;
        }
        boolean enable = mq.getConsumer() != null && mq.getConsumer().isEnable();
        if (!enable) {
            log.warn("mq.consumer.enable=false");
            return;
        }


        //开启定时扫描任务
        new NamedThreadFactory("mq.consumer.scan").newThread(() -> {
            while (true) {
                try {
                    scanConsumerListener();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }

            }
        }).start();


    }

    private void updateNacos() {
        Map<String, String> properties = nacosPropertiesLoader.getProperties();
        String meta = properties.get(META_MARK);
        GrayMark.setGrayMark(meta);
    }

    /**
     * 扫描动态加入的listener
     */
    private void scanConsumerListener() {

        String type = mq.getType();

        List<ConsumerListener> listenerList = fetchListener();
        if (listenerList == null || listenerList.size() == 0) {
            return;
        }

        try {
            MqConsumer consumer = context.getBean(type + "Consumer", MqConsumer.class);
            consumer.initialize(mq, listenerList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("MqConfiguration.scanConsumerListener.init,listenerList={}", listenerList);
    }

    /**
     * 使用懒加载模式，避免在没有生产者的场景下也对生产者进行初始化
     *
     * @return
     */
    @Bean
    @Lazy
    public MqProducer getMqProducer() {

        if (mq == null) {
            log.info("mq.config is null");
            return null;
        }
        boolean enable = mq.getProducer() != null && mq.getProducer().isEnable();
        if (!enable) {
            log.warn("mq.producer.enable=false");
            return null;
        }

        String type = mq.getType();

        MqProducer producer = null;

        if (MqTypeEnum.ROCKETMQ.getType().equals(type)) {

            producer = new RocketMqProducer();

        } else if (MqTypeEnum.KAFKA.getType().equals(type)) {

            producer = new KafkaMqProducer();

        } else {
            log.error("can't find this kind producer implement of type +" + type);
        }

        if (null == producer) {
            log.warn("mq.producer init after is null");
            return null;
        }

        producer.init(mq);
        return producer;
    }


    private List<ConsumerListener> fetchListener() {
        if (context == null) {
            log.error("ApplicationContext is null");
            return null;
        }

        Map<String, ConsumerListener> consumers;

        try {
            consumers = context.getBeansOfType(ConsumerListener.class);
        } catch (Exception e) {
            return null;
        }

        if (consumers == null || consumers.size() == 0) {
            return null;
        }
        List<ConsumerListener> listenerList = new ArrayList<>(16);
        consumers.forEach((key, value) -> {
            if (!mqAccessRule.auth(value.subscribeTopic())) {
                return;
            }
            if (!loadedListenerList.contains(key)) {
                listenerList.add(value);
                loadedListenerList.add(key);
            }
        });

        return listenerList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        SpringHelpers.setContext(this.context);
    }

    @PostConstruct
    public void init() {
        log.info("MqAutoConfiguration.init.start");
    }
}
