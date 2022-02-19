package com.mes.ncr.server.service.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mes.ncr.server.service.utils.StringUtils;
 
 

public class APIReturnObject extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String msg = "";
	public Object list = new ArrayList<Object>();
	public Object info = new Object();

	public APIReturnObject(Map<String, Object> wMap) {

		super();
		if (wMap == null || wMap.size() < 1)
			return;

		if (wMap.containsKey("msg")) {
			this.msg = StringUtils.parseString(wMap.get("msg"));
		}
		if (wMap.containsKey("list")) {
			this.list = wMap.get("list");
		}
		if (wMap.containsKey("info")) {
			this.info = wMap.get("info");
		}
		this.putAll(wMap);

	}

	public APIReturnObject() {
		super();
	}

}
