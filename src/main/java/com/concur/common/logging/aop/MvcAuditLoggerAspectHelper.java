package com.concur.common.logging.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class MvcAuditLoggerAspectHelper {
	private static final Logger LOG = LoggerFactory.getLogger(MvcAuditLoggerAspectHelper.class);

	public static final String CLIENT_ID_HEADER = "X-concur-client-id";
	public static final String SEPARATOR = ",";
	public static final String EQUALS = "=";
	public static final String DEFAULT_APP_NAME = "bootstrap";

	private String applicationName;
	private JoinPointHelper joinPointHelper;
	private ObjectMapper mapper;

	public MvcAuditLoggerAspectHelper(String applicationName, JoinPointHelper joinPointHelper, ObjectMapper mapper) {
		this.applicationName = applicationName;
		this.joinPointHelper = joinPointHelper;
		this.mapper = mapper;
	}

	/**
	 * Create a parameter string for the audit logger. This method will only
	 * work if we are not using interfaces. If we start using interfaces we'll
	 * need to consider other alternatives.
	 * 
	 * @see "http://stackoverflow.com/questions/25226441/java-aop-joinpoint-does-not-get-parameter-names"
	 * 
	 * @param joinPoint
	 * @param auditLoggingAnnotation
	 * @return
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 */
	public String buildApiParams(ProceedingJoinPoint joinPoint, Log log) {
		List<ParameterDetail> parameterDetails = joinPointHelper.getParameterDetails(joinPoint);
		Stream<String> parameterDetailStream;
		if (log.include().length == 1 && StringUtils.equals(log.include()[0], "")) {
			parameterDetailStream = parameterDetails.stream().filter(parameterDetail -> {
				return parameterDetail.hasAnnotation(RequestParam.class) || parameterDetail.hasAnnotation(PathVariable.class) || parameterDetail.hasAnnotation(RequestBody.class);
			}).map(parameterDetail -> {
				if (parameterDetail.hasAnnotation(RequestBody.class)) {
					try {
						return parameterDetail.getName() + EQUALS + mapper.writeValueAsString(parameterDetail.getArgument());
					} catch (Exception e) {
						LOG.error("Unable to write value as json for logs", e);
						return "";
					}
				} else {
					return parameterDetail.getName() + EQUALS + parameterDetail.getArgument();
				}
			});
		} else {
			List<String> includes = Arrays.asList(log.include());
			parameterDetailStream = parameterDetails.stream().filter(parameterDetail -> {
				return includes.contains(parameterDetail.getName());
			}).map(parameterDetail -> {
				return parameterDetail.getName() + EQUALS + parameterDetail.getArgument();
			});
		}
		return parameterDetailStream.reduce((left, right) -> left + SEPARATOR + right).orElse("");
	}

	public void setJoinPointHelper(JoinPointHelper joinPointHelper) {
		this.joinPointHelper = joinPointHelper;
	}

	public String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	public String getContextName(HttpServletRequest request) {
		if (StringUtils.isNotEmpty(applicationName) && !StringUtils.equals(DEFAULT_APP_NAME, applicationName)) {
			return applicationName;
		} else {
			return request.getServletContext().getContextPath().substring(1);
		}
	}

	public String getClientIdHeader(HttpServletRequest request) {
		return request.getHeader(CLIENT_ID_HEADER);
	}

	public String getTargetClassName(JoinPoint joinPoint) {
		return joinPoint.getTarget().getClass().getSimpleName();
	}

	public String getTargetMethodName(JoinPoint joinPoint) {
		return joinPoint.getSignature().getName();
	}
}
