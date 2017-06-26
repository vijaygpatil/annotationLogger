package com.concur.common.logging.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class JoinPointHelper {
	public List<ParameterDetail> getParameterDetails(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String[] names = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		Parameter[] parameters = signature.getMethod().getParameters();
		List<ParameterDetail> parameterDetails = new ArrayList<ParameterDetail>(args.length);
		for (int i = 0; i < args.length; i++) {
			ParameterDetail parameterDetail = new ParameterDetail(names[i], args[i], parameters[i]);
			parameterDetails.add(parameterDetail);
		}
		return parameterDetails;
	}
}
