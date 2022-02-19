package com.mes.ncr.server.service.po.ncr;

public enum NCRStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待工区主管审批
	 */
	ToWorkAreaAudit(1, "待工区主管审批"),
	/**
	 * 撤销
	 */
	Cancle(21, "撤销"),
	/**
	 * 驳回
	 */
	Rejected(22, "已驳回"),
	/**
	 * 待发起不合格评审
	 */
	ToCheckWrite(3, "待发起不合格评审"),
	/**
	 * NCR(工艺师赋值)
	 */
	CarfFill(4, "NCR(工艺师赋值)"),
	/**
	 * 待工艺填写
	 */
	ToCraftWrite(5, "待工艺填写"),
	/**
	 * 待相关部门填写
	 */
	ToRelaWrite(6, "待相关部门填写"),
	/**
	 * 待质量工程师填写
	 */
	ToQualityWrite(7, "待质量工程师填写"),
	/**
	 * 待质量工程师审批
	 */
	ToQualityAudit(8, "待质量工程师审批"),
	/**
	 * 待质量部长审批
	 */
	ToQualityBZAudit(9, "待质量部长审批"),
	/**
	 * 待技术总工程师审批
	 */
	ToEngineerAudit(10, "待技术总工程师审批"),
	/**
	 * 待确认
	 */
	ToConfirm(11, "待确认"),
	/**
	 * 已确认
	 */
	Confirmed(12, "已确认"),

	/**
	 * 已发起不合格评审
	 */
	SendNCR(13, "已发起不合格评审");

	private int value;
	private String lable;

	private NCRStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRStatus getEnumType(int val) {
		for (NCRStatus type : NCRStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRStatus.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
