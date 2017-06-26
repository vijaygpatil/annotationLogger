package com.concur.common.logging.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ConversationIdUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ConversationIdUtil.class);
    private static final String CONVERSATION_ID = "conversationId";
    static final ThreadLocal<String> threadConverationId = new ThreadLocal<String>();

    public ConversationIdUtil() {
    }

    public static String getConversationId() {
        String threadConversationIdValue = threadConverationId.get();
        return threadConversationIdValue;
    }

    public static void setConversationId(String id) {
        threadConverationId.set(id);
    }

    public static void initializeConversationId(String conversationIdValue) {
        String value = (String)threadConverationId.get();
        if(value != null) {
            LOG.warn("ConversationId already initialized.  Value: " + value);
        } else {
            if(conversationIdValue == null) {
                LOG.warn("ConversationId header was not set!!");
                conversationIdValue = createReference();
            }

            setConversationId(conversationIdValue);
            Thread.currentThread().setName(conversationIdValue);
            MDC.put(CONVERSATION_ID, conversationIdValue);
        }

    }

    public static void clear() {
        threadConverationId.set(null);
        MDC.remove("conversationId");
    }

    private static String createReference() {
        int ID_LENGTH = 24;
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }
}
