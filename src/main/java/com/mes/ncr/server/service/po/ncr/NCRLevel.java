package com.mes.ncr.server.service.po.ncr;

public enum NCRLevel {

	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * I级
	 */
	OneLevel(1, "I级"),
	/**
	 * Ⅱ级
	 */
	TwoLevel(2, "Ⅱ级"),
	/**
	 * Ⅲ级
	 */
	ThreeLevel(3, "Ⅲ级");

	private int value;
	private String lable;

	private NCRLevel(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRLevel getEnumType(int val) {
		for (NCRLevel type : NCRLevel.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRLevel.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
