package com.concur.common.logging.service;

import com.concur.common.logging.aop.LogServiceStatus;
import com.concur.common.logging.auditor.Auditor;
import com.concur.common.logging.auditor.LogAuditor;

public class AuditService {
    private Auditor auditor;

    public AuditService() {
    }

    public AuditService(Auditor auditor) {
        this.auditor = auditor;
    }

    public void logServiceAudit(String module, String clientId, String serviceName, String apiName, String apiParams, long serviceTimeinMs, LogServiceStatus status, String errorDetails) {
        this.logServiceAudit(module, clientId, serviceName, apiName, apiParams, serviceTimeinMs, status, errorDetails, null);
    }

    public void logServiceAudit(String module, String clientId, String serviceName, String apiName, String apiParams, long serviceTimeinMs, LogServiceStatus status, String errorDetails, Class<? extends Exception> errorClass) {
        Auditor auditor = this.getAuditor();
        auditor.logServiceAudit(module, clientId, serviceName, apiName, apiParams, serviceTimeinMs, status, errorDetails, errorClass);
    }

    public Auditor getAuditor() {
        if(this.auditor == null) {
            this.auditor = new LogAuditor();
        }

        return this.auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
}
