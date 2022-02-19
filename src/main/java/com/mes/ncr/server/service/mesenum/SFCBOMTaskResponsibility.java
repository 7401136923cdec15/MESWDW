package com.mes.ncr.server.service.mesenum;

public enum SFCBOMTaskResponsibility {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 公司
	 */
	Company(1, "公司"),
	/**
	 * 原厂
	 */
	Plant(2, "原厂");

	private int value;
	private String lable;

	private SFCBOMTaskResponsibility(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCBOMTaskResponsibility getEnumType(int val) {
		for (SFCBOMTaskResponsibility type : SFCBOMTaskResponsibility.values()) {
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
