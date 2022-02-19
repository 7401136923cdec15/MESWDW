package com.mes.ncr.server.service.po;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServiceResult<T> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResult() {
	}

	public ServiceResult(T wT) {
		Result = wT;
	}

	/**
	 * ==返回对象
	 */
	public T Result;

	/**
	 * ==错误代码
	 */
	public String FaultCode = "";

	public int ErrorCode = 0;

	/**
	 * ==自定义返回结果 慎用
	 */
	public Map<String, Object> CustomResult = new HashMap<String, Object>();

	public T getResult() {
		return Result;
	}

	public void setResult(T result) {
		Result = result;
	}

	public String getFaultCode() {
		return FaultCode;
	}

	public void setFaultCode(String faultCode) {
		FaultCode = faultCode;
	}
 
	public void put(Map<String, Object> customResult) {
		if(customResult==null)
			return;
		for (String key : customResult.keySet()) {
			CustomResult.put(key,customResult.get(key));
		} 
	}

	/**
	 * 获取自定义返回数据
	 * 
	 * @param customKey
	 * @return
	 */
	public Object Get(String customKey) {

		if (!CustomResult.containsKey(customKey))
			return new Object();

		return CustomResult.get(customKey);
	}

	/**
	 * 添加自定义返回数据
	 * 
	 * @param customKey
	 * @param customValue
	 */
	public void Put(String customKey, Object customValue) {
		CustomResult.put(customKey, customValue);
	}

	public int getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}

}
