package com.whl.rpc.common;

/**
 * 
 * @description: 封装的rpc响应对象
 * @author whling
 * @date 2017年6月28日下午6:45:46
 *
 */
public class RpcResponse {
	/**
	 * 请求id、返回值、错误情况下的信息
	 */

	private String requestId;
	private Object result;
	private Throwable error;

	/**
	 * 判断返回的信息是不是有错误
	 * 
	 * @return
	 */
	public boolean isError() {
		if (error != null) {
			return true;
		}
		return false;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

}
