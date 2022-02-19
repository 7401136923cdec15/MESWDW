package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;

/**
 * MES状态字典
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-10-22 09:11:42
 */
public class MESStatusDictionary implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 状态Key
	 */
	public String Key = "";
	/**
	 * 状态值
	 */
	public String Value = "";
	/**
	 * 模块ID
	 */
	public int ModuleID = 0;

	public MESStatusDictionary() {
		super();
	}

	public MESStatusDictionary(int iD, String key, String value, int moduleID) {
		super();
		ID = iD;
		Key = key;
		Value = value;
		ModuleID = moduleID;
	}

	public int getID() {
		return ID;
	}

	public String getKey() {
		return Key;
	}

	public String getValue() {
		return Value;
	}

	public int getModuleID() {
		return ModuleID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setKey(String key) {
		Key = key;
	}

	public void setValue(String value) {
		Value = value;
	}

	public void setModuleID(int moduleID) {
		ModuleID = moduleID;
	}
}
