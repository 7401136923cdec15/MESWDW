package com.mes.ncr.server.service.po.mtc;

/**
 * 移车任务类型
 */
public enum MTCTaskType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	
	/**
	 * 普通类型
	 */
	Common(1, "普通类型"),
	
	/**
	 * 直移类型
	 */
	Translation(2, "直移类型");
	
	
	
	private int value;
	private String lable;

	private MTCTaskType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MTCTaskType getEnumType(int val) {
		for (MTCTaskType type : MTCTaskType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return MTCTaskType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
