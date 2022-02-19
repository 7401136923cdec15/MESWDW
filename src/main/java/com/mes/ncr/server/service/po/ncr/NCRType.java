package com.mes.ncr.server.service.po.ncr;
 

public enum NCRType {

	/**
	 * 默认
	 */
	Default(0, "默认"), 
	/**
	 * A类
	 */
	AType(1, "A类"),
	/**
	 * B类
	 */
	BType(2, "B类"), 
	/**
	 * C类
	 */
	CType(3, "C类"); 

	private int value;
	private String lable;

	private NCRType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRType getEnumType(int val) {
		for (NCRType type : NCRType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
