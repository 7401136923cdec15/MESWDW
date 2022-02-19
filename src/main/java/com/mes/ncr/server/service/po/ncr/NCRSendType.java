package com.mes.ncr.server.service.po.ncr;

/**
 * 发起类型枚举
 * 
 * @author ShrisJava
 *
 */
public enum NCRSendType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 生产作业人员发起
	 */
	ProductWorkPerson(1, "生产作业人员发起"),
	/**
	 * 质检员发起
	 */
	QualityCheckPerson(2, "质检员发起"),
	/**
	 * 质量工程师发起
	 */
	QualityEngineer(3, "质量工程师发起");

	private int value;
	private String lable;

	private NCRSendType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRSendType getEnumType(int val) {
		for (NCRSendType type : NCRSendType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRSendType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
