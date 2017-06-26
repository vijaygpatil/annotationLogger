package com.concur.common.logging.exception;

public class InvalidParameterException extends BusinessException {
    private static final long serialVersionUID = 6408769865437197717L;

    public InvalidParameterException() {
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
