package com.concur.common.logging.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class ParameterDetail {
	private final String name;
	private final Object argument;
	private final Parameter parameter;

	public ParameterDetail(String name, Object argument, Parameter parameter) {
		this.name = name;
		this.argument = argument;
		this.parameter = parameter;
	}

	public String getName() {
		return name;
	}

	public Object getArgument() {
		return argument;
	}

	public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
		return parameter.isAnnotationPresent(annotationClass);
	}

	@Override
	public String toString() {
		return "ParameterDetail [name=" + name + ", argument=" + argument + "]";
	}

}
