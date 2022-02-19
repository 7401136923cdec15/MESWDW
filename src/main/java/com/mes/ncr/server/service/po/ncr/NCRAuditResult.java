package com.mes.ncr.server.service.po.ncr;
 
/**
 * 审批结果
 * @author ShrisJava
 *
 */
public enum NCRAuditResult {
	/**
	 * 默认
	 */
	Default(0, "默认"), 
	/**
	 * 已确认
	 */
	Confirmed(1, "已确认"),
	/**
	 * 已驳回
	 */
	Rejected(2, "已驳回"); 

	private int value;
	private String lable;

	private NCRAuditResult(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRAuditResult getEnumType(int val) {
		for (NCRAuditResult type : NCRAuditResult.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRAuditResult.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
