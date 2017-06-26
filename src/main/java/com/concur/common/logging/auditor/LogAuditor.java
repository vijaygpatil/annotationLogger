package com.concur.common.logging.auditor;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.concur.common.logging.aop.LogServiceStatus;
import com.concur.common.logging.util.ConversationIdUtil;

public class LogAuditor implements Auditor {
	private static final String UNKNOWN = "UNKNOWN";
	private static final String TOKEN_SEPERATOR = "|";
	private static Logger auditLog = LoggerFactory.getLogger(LogAuditor.class);

	public LogAuditor() {
	}

	public void logServiceAudit(String module, String clientId, String serviceName, String apiName, String apiParams,
			long serviceTimeinMs, LogServiceStatus status, String errorDetails, Class<? extends Exception> errorClass) {
		this.getAuditLogger().info(getMessage(clientId, serviceName, apiName, apiParams, serviceTimeinMs, status,
				errorDetails, module, errorClass));
	}

	public Logger getAuditLogger() {
		return auditLog;
	}

	protected static String getMessage(String clientId, String serviceName, String apiName, String apiParams,
			long serviceTimeinMs, LogServiceStatus status, String errorDetails, String moduleName,
			Class<? extends Exception> errorClass) {

		if (StringUtils.isEmpty(clientId)) {
			clientId = UNKNOWN;
		}

		StringBuilder buffer = new StringBuilder(256);
		buffer.append(cleanseClientId(clientId));
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(serviceName);
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(apiName);
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(escapeStringForLog(apiParams));
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(serviceTimeinMs);
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(cleanseStatus(status));
		buffer.append(TOKEN_SEPERATOR);
		boolean spaceNeeded = false;
		if (!StringUtils.isEmpty(errorDetails)) {
			buffer.append("msg=");
			buffer.append(escapeStringForLog(errorDetails));
			spaceNeeded = true;
		}

		if (errorClass != null) {
			if (spaceNeeded) {
				buffer.append(" ");
			}

			buffer.append("e=");
			buffer.append(errorClass.getName());
		}

		buffer.append(TOKEN_SEPERATOR);
		buffer.append(moduleName);
		buffer.append(TOKEN_SEPERATOR);
		buffer.append(escapeStringForLog(ConversationIdUtil.getConversationId()));
		return buffer.toString();
	}

	protected static String cleanseStatus(LogServiceStatus status) {
		return status != null ? status.name() : LogServiceStatus.FAILED.name();
	}

	protected static String cleanseClientId(String clientId) {
		return StringUtils.removeEnd(clientId, "-");
	}

	protected static String escapeStringForLog(String msg) {
		return msg == null ? ""
				: StringUtils.substring(StringUtils.remove(StringEscapeUtils.escapeJava(msg), TOKEN_SEPERATOR), 0,
						4999);
	}
}
