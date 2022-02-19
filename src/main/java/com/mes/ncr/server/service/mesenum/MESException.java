package com.mes.ncr.server.service.mesenum;

public enum MESException {
	/**
	 * 默认
	 */
	Default(0, ""),
	/**
	 * 逻辑错误
	 */
	Logic(1, "逻辑错误"),
	/**
	 * 数据库错误
	 */
	DBInstance(2, "数据库错误"),
	/**
	 * SQL语法错误
	 */
	DBSQL(3, "SQL语法错误"),
	/**
	 * 参数错误
	 */
	Parameter(4, "参数错误"),
	/**
	 * 系统异常
	 */
	Exception(5, "系统异常"),
	/**
	 * 无授权
	 */
	UnPower(6, "无授权"),
	/**
	 * 文件异常
	 */
	File(7, "文件异常");

	private int value;
	private String lable;

	private MESException(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MESException getEnumType(int val) {
		for (MESException type : MESException.values()) {
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
