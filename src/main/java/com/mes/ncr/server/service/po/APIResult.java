package com.mes.ncr.server.service.po;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mes.ncr.server.service.utils.CloneTool;

public class APIResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public APIResult() {
	}

	private int resultCode = 1000;
	private Map<String, Object> returnObject = new HashMap<>();

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Map<String, Object> getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Map<String, Object> returnObject) {
		this.returnObject = returnObject;
	}

	public <T> T Info(Class<T> clazz) {
		try {
			return CloneTool.Clone(new APIReturnObject(returnObject).info, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> List(Class<T> clazz) {
		try {
			APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);

			return CloneTool.CloneArray(wAPIReturnObject.list, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	public String getMsg() {
		try {
			APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);

			return wAPIReturnObject.msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void setMsg(String wMsg) {
		try {
			APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);

			wAPIReturnObject.msg=wMsg;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public <T> T Custom(String wKey, Class<T> clazz) {
		try {
			APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);

			return CloneTool.Clone(wAPIReturnObject.get(wKey), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public <T> List<T> CustomArray(String wKey, Class<T> clazz) {
		try {
			APIReturnObject wAPIReturnObject = new APIReturnObject(returnObject);

			return CloneTool.CloneArray(wAPIReturnObject.get(wKey), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();

	}

	public APIReturnObject ReturnObejct() {
		return new APIReturnObject(returnObject);
	}
}
