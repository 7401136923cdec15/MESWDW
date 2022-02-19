package com.mes.ncr.server.service.mesenum;

public enum SFCBOMTaskReviewComments {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 返工
	 */
	Rework(1, "返工"),
	/**
	 * 送修
	 */
	Repair(2, "送修"),
	/**
	 * 报废/更换
	 */
	Exchange(3, "报废&更换"),
	/**
	 * 更换
	 */
	Change(4, "更换");

	private int value;
	private String lable;

	private SFCBOMTaskReviewComments(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCBOMTaskReviewComments getEnumType(int val) {
		for (SFCBOMTaskReviewComments type : SFCBOMTaskReviewComments.values()) {
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
