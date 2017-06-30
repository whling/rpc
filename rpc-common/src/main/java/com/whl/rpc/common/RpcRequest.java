package com.whl.rpc.common;
/**
 * 
 * @description: 封装的rpc请求对象
 * @author whling
 * @date 2017年6月28日下午5:26:57
 *
 */

import java.util.List;

public class RpcRequest {
	/**
	 * 封装需要反射的类名、方法名、方法参数、参数类型
	 */
	private String requestId;
	private String className;
	private String methodName;
	private Object[] parameter;
	private Class<?>[] parameterTypes;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getParameter() {
		return parameter;
	}

	public void setParameter(Object[] parameter) {
		this.parameter = parameter;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
}
