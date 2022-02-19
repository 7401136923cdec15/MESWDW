package com.mes.ncr.server.service.po.fpc;

public enum FPCPartTypes {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 生产工位
	 */
	Product(1, "生产工位"),
	/**
	 * 预检工位
	 */
	PrevCheck(2, "预检工位"),
	/**
	 * 终检工位
	 */
	QTFinally(3, "终检工位"),
	/**
	 * 出厂普查
	 */
	OutFactory(4, "出厂普查");
	private int value;
	private String lable;

	private FPCPartTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static FPCPartTypes getEnumType(int val) {
		for (FPCPartTypes type : FPCPartTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
