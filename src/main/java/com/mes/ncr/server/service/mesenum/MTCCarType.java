package com.mes.ncr.server.service.mesenum;

public enum MTCCarType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 车体
	 */
	Body(1, "车体"),
	/**
	 * 底盘
	 */
	Chassis(2, "底盘");
	
	private int value;
	private String lable;

	private MTCCarType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MTCCarType getEnumType(int val) {
		for (MTCCarType type : MTCCarType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return MTCCarType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
	
}
