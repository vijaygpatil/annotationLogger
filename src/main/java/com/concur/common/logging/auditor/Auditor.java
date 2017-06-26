package com.concur.common.logging.auditor;

import com.concur.common.logging.aop.LogServiceStatus;

public interface Auditor {
	void logServiceAudit(String module, String clientId, String serviceName, String apiName, String apiParams,
			long serviceTimeinMs, LogServiceStatus status, String errorDetails, Class<? extends Exception> errorClass);
}