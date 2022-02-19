package com.mes.ncr.server.service.po.sch;

public enum SCHSecondStatus {
	Default(0, "默认"),
	/**
	 * 申请
	 */
	Apply(1, "申请中"),
	/*
	 * 待相关工区主管借调
	 */
	ToOtherSecond(2, "待相关工区主管借调"),
	/**
	 * 已借调
	 */
	Seconded(3, "已借调");

	private int value;
	private String lable;

	private SCHSecondStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SCHSecondStatus getEnumType(int val) {
		for (SCHSecondStatus type : SCHSecondStatus.values()) {
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
