package com.mes.ncr.server.service.po;

import java.util.HashMap;
import java.util.Map;

public class OutResult<T> {

	public OutResult() {
		// TODO Auto-generated constructor stub
	}

	public OutResult(T wT) {
		Result = wT;
	}

	public T Result;

	public T get() {
		return Result;
	}

	private Map<String, Object> CustomResult = new HashMap<String, Object>();

	public Object get(String wKey) {
		if (!CustomResult.containsKey(wKey))
			return new Object();
		return CustomResult.get(wKey);
	}
 
	public void put(Map<String, Object> customResult) {
		if (customResult == null)
			return;
		for (String key : customResult.keySet()) {
			CustomResult.put(key, customResult.get(key));
		}
	}

	public void set(T wResult) {

		Result = wResult;
	}

	public void put(String key, Object value) {
		CustomResult.put(key, value);
	}

	public void add(String key, Object value) {
		CustomResult.put(key, value);
	}

	public void clear() {
		CustomResult.clear();
	}
}
