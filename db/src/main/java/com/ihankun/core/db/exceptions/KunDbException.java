package com.ihankun.core.db.exceptions;

/**
 * @author hankun
 */
public class KunDbException extends RuntimeException{

    public KunDbException(String message) {
        super(message);
    }

    public KunDbException(Throwable throwable) {
        super(throwable);
    }

    public KunDbException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
