package com.concur.common.aop;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.concur.common.logging.aop.LogServiceStatus;
import com.concur.common.logging.aop.Log;
import com.concur.common.logging.aop.MvcAuditLoggerAspect;
import com.concur.common.logging.aop.MvcAuditLoggerAspectHelper;
import com.concur.common.logging.exception.BusinessException;
import com.concur.common.logging.exception.InvalidParameterException;
import com.concur.common.logging.service.AuditService;

public class MvcAuditLoggerAspectTest {
	@InjectMocks
	private MvcAuditLoggerAspect aspect;

	@Mock
	private AuditService auditService;

	@Mock
	private MvcAuditLoggerAspectHelper helper;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Mock
	private Log auditLog;

	@Mock
	private HttpServletRequest request;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		aspect = new MvcAuditLoggerAspect(auditService);
		aspect.setAuditLoggerHelper(helper);
	}

	@Test
	public void handleControllerLoggingLogsSuccess() throws Throwable {
		String requestUrl = "requestUrl";
		String module = "module";
		String clientId = "clientId";
		String serviceName = "serviceName";
		String apiName = "apiName";
		String apiParams = "apiParams";
		String errorMessage = "";
		Class<? extends Exception> errorClass = null;

		when(helper.getRequestUrl(request)).thenReturn(requestUrl);
		when(helper.getContextName(request)).thenReturn(module);
		when(helper.getClientIdHeader(request)).thenReturn(clientId);
		when(helper.getTargetClassName(joinPoint)).thenReturn(serviceName);
		when(helper.getTargetMethodName(joinPoint)).thenReturn(apiName);
		when(helper.buildApiParams(joinPoint, auditLog)).thenReturn(apiParams);

		aspect.handleControllerLogging(joinPoint, auditLog);

		verify(joinPoint).proceed();
		verify(auditService).logServiceAudit(eq(module), eq(clientId), eq(serviceName), eq(apiName), eq(apiParams),
				anyLong(), eq(LogServiceStatus.SUCCESS), eq(errorMessage), eq(errorClass));
	}

	@Test
	public void handleControllerLoggingLogsFailed() throws Throwable {
		String requestUrl = "requestUrl";
		String module = "module";
		String clientId = "clientId";
		String serviceName = "serviceName";
		String apiName = "apiName";
		String apiParams = "apiParams";
		String errorMessage = "errorMessage";
		Exception exception = new Exception(errorMessage);
		Class<? extends Exception> errorClass = exception.getClass();

		when(helper.getRequestUrl(request)).thenReturn(requestUrl);
		when(helper.getContextName(request)).thenReturn(module);
		when(helper.getClientIdHeader(request)).thenReturn(clientId);
		when(helper.getTargetClassName(joinPoint)).thenReturn(serviceName);
		when(helper.getTargetMethodName(joinPoint)).thenReturn(apiName);
		when(helper.buildApiParams(joinPoint, auditLog)).thenReturn(apiParams);
		when(joinPoint.proceed()).thenThrow(exception);

		try {
			aspect.handleControllerLogging(joinPoint, auditLog);
		} catch (Exception e) {
			assertEquals(exception, e);
		}

		verify(auditService).logServiceAudit(eq(module), eq(clientId), eq(serviceName), eq(apiName), eq(apiParams),
				anyLong(), eq(LogServiceStatus.FAILED), eq(errorMessage), eq(errorClass));
	}

	@Test
	public void handleControllerLoggingLogsSuccessForBusinessException() throws Throwable {
		String requestUrl = "requestUrl";
		String module = "module";
		String clientId = "clientId";
		String serviceName = "serviceName";
		String apiName = "apiName";
		String apiParams = "apiParams";
		String errorMessage = "errorMessage";
		BusinessException exception = new InvalidParameterException(errorMessage);
		Class<? extends Exception> errorClass = exception.getClass();

		when(helper.getRequestUrl(request)).thenReturn(requestUrl);
		when(helper.getContextName(request)).thenReturn(module);
		when(helper.getClientIdHeader(request)).thenReturn(clientId);
		when(helper.getTargetClassName(joinPoint)).thenReturn(serviceName);
		when(helper.getTargetMethodName(joinPoint)).thenReturn(apiName);
		when(helper.buildApiParams(joinPoint, auditLog)).thenReturn(apiParams);
		when(joinPoint.proceed()).thenThrow(exception);

		try {
			aspect.handleControllerLogging(joinPoint, auditLog);
		} catch (Exception e) {
			assertEquals(exception, e);
		}

		verify(auditService).logServiceAudit(eq(module), eq(clientId), eq(serviceName), eq(apiName), eq(apiParams),
				anyLong(), eq(LogServiceStatus.SUCCESS), eq(errorMessage), eq(errorClass));
	}

}
