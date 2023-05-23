package com.ihankun.core.base.context;

import com.ihankun.core.commons.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 业务开始时间
 * @author hankun
 */
@Slf4j
public class BusinessStartTimeContext {

    public static final String BUSINESS_START_TIME_HEADER_NAME = "start-time";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new NamedThreadLocal<>(BUSINESS_START_TIME_HEADER_NAME);

    /**
     * 获取String类型
     */
    public static String getTimeStr() {
        String time = CONTEXT_HOLDER.get();
        if (StringUtils.isEmpty(time)) {
            time = DateUtils.getNowStr();
            log.info("BusinessStartTimeContext.getTime.not.set.will.auto.fill,time={}", time);
        }
        return time;
    }

    /**
     * 获取时间类型
     */
    public static Date getTime() {
        String time = getTimeStr();
        if (!StringUtils.isEmpty(time)) {
            return DateUtils.dateStrToDate(time);
        }
        return DateUtils.getNowDate();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 模拟时间信息
     */
    public static void mock(String time) {
        if (!StringUtils.isEmpty(time)) {
            CONTEXT_HOLDER.set(time);
        }
    }
}
