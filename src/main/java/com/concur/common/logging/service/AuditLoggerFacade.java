package com.concur.common.logging.service;

import com.concur.common.logging.auditor.WebServicesAuditor;

public class AuditLoggerFacade {
    private static WebServicesAuditor webServicesAuditor = new WebServicesAuditor();
    private static AuditService webServiceAuditService;

    public AuditLoggerFacade() {
    }

    public static AuditService getWebServiceAuditService() {
        return webServiceAuditService;
    }

    static {
        webServiceAuditService = new AuditService(webServicesAuditor);
    }
}
