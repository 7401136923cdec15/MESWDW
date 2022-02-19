package com.mes.ncr.server.service.po.fpc;


public enum FPCPartType {
	
	/**
	 * 默认
	 */
	Default(0, "默认"),
	
	/**
	 *  普通工位
	 */
	Common(1, " 普通工位"),
	
	/**
	 * 预检工位
	 */
	Precheck(2, "预检工位"),
	
	/**
	 * 质量工位
	 */
	Quality(3, "质量工位"),
	/**
	 * 普查工位
	 */
	Cences(4, "普查工位");
	
	private int value;
	private String lable;

	private FPCPartType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static FPCPartType getEnumType(int val) {
		for (FPCPartType type : FPCPartType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return FPCPartType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}

}
