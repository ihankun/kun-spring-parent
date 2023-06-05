package com.ihankun.core.db.exceptions;

import com.ihankun.core.base.exception.BusinessException;

/**
 * @author hankun
 */
public class KunSqlFlowControlException extends BusinessException {

    public KunSqlFlowControlException(String message) {
        super(DbExceptionErrorCode.FLOW_CONTROL_ERROR, message);
    }
}
