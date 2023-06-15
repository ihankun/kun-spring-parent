package com.ihankun.core.spring.server.utils;

import com.ihankun.core.base.context.*;
import com.ihankun.core.log.context.ApiLogContext;
import com.ihankun.core.log.context.TraceLogApiInfoContext;
import com.ihankun.core.log.context.TraceLogContext;
import com.ihankun.core.log.entity.ApiInfo;
import com.ihankun.core.log.entity.ApiLog;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hankun
 */
@Slf4j
public class KunThreadContextHolder {

    private LoginUserInfo loginUserInfo;
    private String domain;
    private String businessTime;
    private String gray;
    private String traceId;
    private String xid;
    private ApiInfo apiInfo;
    private ApiLog apiLog;

    /**
     * 捕获当前线程上下文信息
     *
     * @return
     */
    public static KunThreadContextHolder capture() {
        KunThreadContextHolder holder = new KunThreadContextHolder();
        holder.loginUserInfo = LoginUserContext.getLoginUserInfo();
        holder.domain = DomainContext.get();
        holder.businessTime = BusinessStartTimeContext.get();
        holder.gray = GrayContext.get();
        holder.traceId = TraceLogContext.get();
        holder.xid = RootContext.getXID();
        holder.apiInfo = TraceLogApiInfoContext.get();
        holder.apiLog = ApiLogContext.get();
        return holder;
    }

    /**
     *  保持上下文信息
     * @param loginUserInfo
     * @param domain
     * @param businessTime
     * @param gray
     * @param traceId
     * @param xid
     * @param apiInfo
     * @param apiLog
     * @return
     */
    public static KunThreadContextHolder capture(LoginUserInfo loginUserInfo, String domain, String businessTime, String gray, String traceId, String xid, ApiInfo apiInfo, ApiLog apiLog) {
        KunThreadContextHolder holder = new KunThreadContextHolder();
        holder.loginUserInfo = loginUserInfo;
        holder.domain = domain;
        holder.businessTime = businessTime;
        holder.gray = gray;
        holder.traceId = traceId;
        holder.xid = xid;
        holder.apiInfo = apiInfo;
        holder.apiLog = apiLog;
        return holder;
    }


    /**
     * 清理当前线程上下文内容
     */
    public static void clear() {
        LoginUserContext.clear();
        DomainContext.clear();
        BusinessStartTimeContext.clear();
        GrayContext.clear();
        TraceLogContext.reset();
        TraceLogApiInfoContext.reset();
        ApiLogContext.reset();
    }

    /**
     * 注入到新线程上下文中
     */
    public void inject() {
        LoginUserContext.mock(this.loginUserInfo);
        DomainContext.mock(this.domain);
        BusinessStartTimeContext.mock(this.businessTime);
        GrayContext.mock(this.gray);
        TraceLogContext.set(this.traceId);
        TraceLogApiInfoContext.set(this.apiInfo);
        ApiLogContext.set(this.apiLog);
    }


    @Override
    public String toString() {
        return "KunThreadContextHolder{" +
                "loginUserInfo=" + loginUserInfo +
                ", domain='" + domain + '\'' +
                ", businessTime='" + businessTime + '\'' +
                ", gray='" + gray + '\'' +
                ", traceId='" + traceId + '\'' +
                ", apiInfo='" + apiInfo + '\'' +
                ", apiLog='" + apiLog + '\'' +
                '}';
    }
}
