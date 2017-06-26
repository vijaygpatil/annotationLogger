package com.concur.common.logging.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.concur.common.logging.exception.BusinessException;
import com.concur.common.logging.service.AuditLoggerFacade;
import com.concur.common.logging.service.AuditService;


@Aspect
@Component
public class MvcAuditLoggerAspect {
	// logger for the runtime class of this Object
	private Logger LOG = LoggerFactory.getLogger(getClass());

	private final AuditService auditService;
	private MvcAuditLoggerAspectHelper auditLoggerHelper;

	public MvcAuditLoggerAspect() {
		this(AuditLoggerFacade.getWebServiceAuditService());
	}

	public MvcAuditLoggerAspect(AuditService auditService) {
		this.auditService = auditService;
	}

	/**
	 * Aspect for handling the audit for Spring MVC Controllers annotated as AuditLog
	 * 
	 * @param joinPoint
	 * @param auditLog
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(auditLog)")
	public Object handleControllerLogging(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		String url = auditLoggerHelper.getRequestUrl(request);
		System.out.println("url = " + url);
		String module = auditLoggerHelper.getContextName(request);
		System.out.println("module = " + module);
		String clientId = auditLoggerHelper.getClientIdHeader(request);
		System.out.println("clientId = " + clientId);
		String serviceName = auditLoggerHelper.getTargetClassName(joinPoint);
		System.out.println("serviceName = " + serviceName);
		String apiName = auditLoggerHelper.getTargetMethodName(joinPoint);
		System.out.println("apiName = " + apiName);
		String apiParams = auditLoggerHelper.buildApiParams(joinPoint, auditLog);
		System.out.println("apiParams = " + apiParams);

		LogServiceStatus status = LogServiceStatus.SUCCESS;
		String errorMsg = "";
		Class<? extends Exception> errorClass = null;

		StopWatch watch = new StopWatch();
		watch.start();
		try {
			return joinPoint.proceed();
		} catch (BusinessException | IllegalArgumentException exception) {
			LOG.debug("url={}, clientId={}, params={}", url, clientId, apiParams, exception);

			errorMsg = exception.getMessage();
			errorClass = exception.getClass();

			throw exception;
		} catch (Exception exception) {
			LOG.error("url={}, clientId={}, params={}", url, clientId, apiParams, exception);

			status = LogServiceStatus.FAILED;
			errorMsg = exception.getMessage();
			errorClass = exception.getClass();

			throw exception;
		} finally {
			watch.stop();

			long serviceTimeinMs = watch.getTime();

			auditService.logServiceAudit(module, clientId, serviceName, apiName, apiParams, serviceTimeinMs, status, errorMsg, errorClass);
		}
	}

	public void setAuditLoggerHelper(MvcAuditLoggerAspectHelper auditLoggerHelper) {
		this.auditLoggerHelper = auditLoggerHelper;
	}
}