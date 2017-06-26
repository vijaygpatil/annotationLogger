package com.concur.common.logging.exception;

import com.concur.common.logging.util.ConversationIdUtil;

public abstract class BusinessException extends Exception {
    private static final long serialVersionUID = 4894820430682637765L;
    private static final String referenceCode = ConversationIdUtil.getConversationId();

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessage() {
        String message = super.getMessage();
        if(referenceCode != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(message);
            builder.append(" referenceCode: ");
            builder.append(referenceCode);
            message = builder.toString();
        }

        return message;
    }

    /** @deprecated */
    @Deprecated
    public void setMessage(String message) {
    }

}
