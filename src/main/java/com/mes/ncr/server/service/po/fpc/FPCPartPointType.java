package com.mes.ncr.server.service.po.fpc;


public enum FPCPartPointType {
	
	/**
	 * 默认
	 */
	Default(0, "默认"),
	
	/**
	 *  试运前
	 */
	Common(1, " 试运前"),
	
	/**
	 * 试运后
	 */
	Cences(2, "试运后");
	
	private int value;
	private String lable;

	private FPCPartPointType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static FPCPartPointType getEnumType(int val) {
		for (FPCPartPointType type : FPCPartPointType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return FPCPartPointType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}

}
