package com.mes.ncr.server.shristool;

public enum LoggerEnumFile {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Exception(1, "Exception"),
	/**
	 * APP
	 */
	Logger(2, "Logger");
	private int value;
	private String lable;

	private LoggerEnumFile(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static LoggerEnumFile getEnumType(int val) {
		for (LoggerEnumFile type : LoggerEnumFile.values()) {
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
