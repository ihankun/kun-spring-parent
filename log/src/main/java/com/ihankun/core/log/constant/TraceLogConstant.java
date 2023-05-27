package com.ihankun.core.log.constant;


import com.ihankun.core.base.id.IdGenerator;
import com.ihankun.core.log.context.TraceLogContext;
import org.apache.commons.lang3.StringUtils;

public interface TraceLogConstant {
    /**
     * tracceId
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 判断当前线程是否绑定了traceId
     *
     * @return
     */
    public static boolean isBindTraceId() {
        return TraceLogContext.get() != null;
    }

    /**
     * 获取Mdc中的traceId
     *
     * @return
     */
    public static String getTraceId() {
        String traceId = TraceLogContext.get();
        if (StringUtils.isEmpty(traceId)) {
            traceId = String.valueOf(IdGenerator.ins().generator());
        }
        return traceId;
    }

    /**
     * 项Mdc中设置traceId
     *
     * @param traceId
     */
    public static void setTraceId(String traceId) {
        TraceLogContext.set(traceId);
    }
}
