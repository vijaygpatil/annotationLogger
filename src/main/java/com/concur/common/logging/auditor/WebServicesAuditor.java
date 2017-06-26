package com.concur.common.logging.auditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServicesAuditor extends LogAuditor {
    private static final Logger webserviceAuditLog = LoggerFactory.getLogger(WebServicesAuditor.class);

    public WebServicesAuditor() {
    }

    public Logger getAuditLogger() {
        return webserviceAuditLog;
    }
}
