package com.concur.common.logging.configuration;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.concur.common.logging.aop.JoinPointHelper;
import com.concur.common.logging.aop.MvcAuditLoggerAspect;
import com.concur.common.logging.aop.MvcAuditLoggerAspectHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ConditionalOnClass({ EnableAspectJAutoProxy.class, Aspect.class, Advice.class })
public class MvcAuditLoggerAutoconfiguration {
	@Configuration
	@ConditionalOnWebApplication
	public static class WebAuditLogger {
		@Bean
		public MvcAuditLoggerAspect webAuditLoggerAspect(MvcAuditLoggerAspectHelper auditLoggerHelper) {
			MvcAuditLoggerAspect aspect = new MvcAuditLoggerAspect();
			aspect.setAuditLoggerHelper(auditLoggerHelper);
			return aspect;
		}

		@Bean
		public MvcAuditLoggerAspectHelper webAuditLoggerAspectHelper(@Value("${spring.application.name:}") String applicationName, JoinPointHelper joinPointHelper, ObjectMapper mapper) {
			MvcAuditLoggerAspectHelper helper = new MvcAuditLoggerAspectHelper(applicationName, joinPointHelper, mapper);
			return helper;
		}

		@Bean
		public JoinPointHelper joinPointHelper() {
			return new JoinPointHelper();
		}
	}
}
