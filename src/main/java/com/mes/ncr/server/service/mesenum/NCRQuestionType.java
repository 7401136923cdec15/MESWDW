package com.mes.ncr.server.service.mesenum;

public enum NCRQuestionType {

	/**
	 * 默认
	 */
	Default(0, "默认"),

	/**
	 * 产品质量问题
	 */
	QualityQusteion(1, "产品质量问题"),

	/**
	 * 生产组织问题
	 */
	ProductQuestion(2, "生产组织问题"),

	/**
	 * 采购组织问题
	 */
	PurchaseQustion(3, "采购组织问题");

	private int value;
	private String lable;

	private NCRQuestionType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRQuestionType getEnumType(int val) {
		for (NCRQuestionType type : NCRQuestionType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRQuestionType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
