package com.mes.ncr.server.service.mesenum;

public enum DBEnumType {
	/**
	 * SCH
	 */
	Default(0, "MySQL"),
	/**
	 * SCH
	 */
	MySQL(1, "MySQL"),
	/**
	 * APP
	 */
	SQLServer(2, "SQLServer"),
	/**
	 * APP
	 */
	Oracle(3, "Oracle"),
	
	Access(4, "Access");
	
	private int value;
	private String lable;

	private DBEnumType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static DBEnumType getEnumType(int val) {
		for (DBEnumType type : DBEnumType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
