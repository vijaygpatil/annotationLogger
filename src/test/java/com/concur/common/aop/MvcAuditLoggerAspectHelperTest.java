package com.concur.common.aop;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.concur.common.logging.aop.JoinPointHelper;
import com.concur.common.logging.aop.Log;
import com.concur.common.logging.aop.MvcAuditLoggerAspectHelper;
import com.concur.common.logging.aop.ParameterDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MvcAuditLoggerAspectHelperTest {
	private MvcAuditLoggerAspectHelper aspectHelper;
	private ObjectMapper mapper = new ObjectMapper();

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Mock
	private Log auditLog;

	@Mock
	private JoinPointHelper joinPointHelper;

	@Mock
	private HttpServletRequest request;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		when(auditLog.include()).thenReturn(new String[] { new String() });
		aspectHelper = new MvcAuditLoggerAspectHelper("", joinPointHelper, mapper);
	}

	@Test
	public void buildApiParamsNoParameters() throws JsonProcessingException, UnsupportedEncodingException {
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Collections.emptyList());

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("", result);
	}

	@Test
	public void buildApiParamsSingleRequestParameter() {
		ParameterDetail parameter = mock(ParameterDetail.class);
		when(parameter.getName()).thenReturn("parameter");
		when(parameter.getArgument()).thenReturn("argument");
		when(parameter.hasAnnotation(RequestParam.class)).thenReturn(true);
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Arrays.asList(parameter));

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("parameter=argument", result);
	}

	@Test
	public void buildApiParamsSinglePathVariable() {
		ParameterDetail parameter = mock(ParameterDetail.class);
		when(parameter.getName()).thenReturn("parameter");
		when(parameter.getArgument()).thenReturn("argument");
		when(parameter.hasAnnotation(PathVariable.class)).thenReturn(true);
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Arrays.asList(parameter));

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("parameter=argument", result);
	}

	@Test
	public void buildApiParamsRequestBody() {
		ParameterDetail parameter = mock(ParameterDetail.class);
		when(parameter.getName()).thenReturn("parameter");
		when(parameter.getArgument()).thenReturn(Collections.singletonMap("key", "value"));
		when(parameter.hasAnnotation(RequestBody.class)).thenReturn(true);
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Arrays.asList(parameter));

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("parameter={\"key\":\"value\"}", result);
	}

	@Test
	public void buildApiParamsMultipleParameters() {
		ParameterDetail parameter1 = mock(ParameterDetail.class);
		when(parameter1.getName()).thenReturn("parameter1");
		when(parameter1.getArgument()).thenReturn("argument1");
		when(parameter1.hasAnnotation(RequestParam.class)).thenReturn(true);
		ParameterDetail parameter2 = mock(ParameterDetail.class);
		when(parameter2.getName()).thenReturn("parameter2");
		when(parameter2.getArgument()).thenReturn("argument2");
		when(parameter2.hasAnnotation(RequestParam.class)).thenReturn(true);
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Arrays.asList(parameter1, parameter2));

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("parameter1=argument1,parameter2=argument2", result);
	}

	@Test
	public void buildApiParamsFromIncludes() {
		ParameterDetail parameter = mock(ParameterDetail.class);
		when(parameter.getName()).thenReturn("key");
		when(parameter.getArgument()).thenReturn("value");
		when(joinPointHelper.getParameterDetails(joinPoint)).thenReturn(Arrays.asList(parameter));
		when(auditLog.include()).thenReturn(new String[] { "key" });

		String result = aspectHelper.buildApiParams(joinPoint, auditLog);

		assertEquals("key=value", result);
	}

	@Test
	public void getRequestUrlReturnsRequestUrl() {
		StringBuffer requestUrlBuffer = new StringBuffer("requestURL");
		when(request.getRequestURL()).thenReturn(requestUrlBuffer);

		String result = aspectHelper.getRequestUrl(request);

		assertEquals(requestUrlBuffer.toString(), result);
	}

	@Test
	public void getContextNameReturnsContextNameForEmptyApplicationName() {
		String contextName = "context";
		ServletContext context = mock(ServletContext.class);
		when(request.getServletContext()).thenReturn(context);
		when(context.getContextPath()).thenReturn("/" + contextName);

		String result = aspectHelper.getContextName(request);

		assertEquals(contextName, result);
	}

	@Test
	public void getContextNameReturnsContextNameForDefaultApplicationName() {
		String contextName = "context";
		ServletContext context = mock(ServletContext.class);
		aspectHelper = new MvcAuditLoggerAspectHelper(MvcAuditLoggerAspectHelper.DEFAULT_APP_NAME, joinPointHelper, mapper);
		when(request.getServletContext()).thenReturn(context);
		when(context.getContextPath()).thenReturn("/" + contextName);

		String result = aspectHelper.getContextName(request);

		assertEquals(contextName, result);
	}

	@Test
	public void getContextNameReturnsApplicationName() {
		String applicationName = "applicationName";
		aspectHelper = new MvcAuditLoggerAspectHelper(applicationName, joinPointHelper, mapper);

		String result = aspectHelper.getContextName(request);

		assertEquals(applicationName, result);
	}

	@Test
	public void getClientIdHeaderReturnsHeader() {
		String clientId = "clientId";
		when(request.getHeader(MvcAuditLoggerAspectHelper.CLIENT_ID_HEADER)).thenReturn(clientId);

		String result = aspectHelper.getClientIdHeader(request);

		assertEquals(clientId, result);
	}

	@Test
	public void getTargetClassNameReturnsClassName() {
		when(joinPoint.getTarget()).thenReturn(new Object());

		String result = aspectHelper.getTargetClassName(joinPoint);

		assertEquals("Object", result);
	}

	@Test
	public void getTargetMethodName() {
		String name = "name";
		Signature signature = mock(Signature.class);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(name);

		String result = aspectHelper.getTargetMethodName(joinPoint);

		assertEquals(name, result);
	}

}
