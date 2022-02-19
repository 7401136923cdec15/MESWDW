package com.mes.ncr.server.service.mesenum;


public enum NCRSteps {
	
	/**
	 * 默认
	 */
	Default(0, "默认"),
	
	/**
	 * 生产作业人员发起
	 */
	SCZYApply(5, "生产作业人员发起"),
	
	/**
	 * 工区主管审批
	 */
	GQZGAudit(6, "工区主管审批"),
	
	/**
	 * 质检员填写
	 */
	ZJYFill(7, "质检员填写"),
	
	/**
	 * 质量工程师评级
	 */
	ZLGCLevel(8, "质量工程师评级"),
	
	/**
	 * 工艺员填写
	 */
	GYYFill(9, "工艺员填写"),
	
	/**
	 * 相关部门审批
	 */
	XGBMAudit(10, "相关部门审批"),
	
	/**
	 * 质量工程师填写
	 */
	ZLGCFill(11, "质量工程师填写"),
	
	/**
	 * 质量工程师审批
	 */
	ZLGCAudit(12, "质量工程师审批"),
	
	/**
	 * 质量部长审批
	 */
	ZLBZAudit(13, "质量部长审批"),
	
	/**
	 * 技术总工程师审批
	 */
	JSZGAudit(14, "技术总工程师审批"),
	
	/**
	 * 待确认
	 */
	ToConfirm(15, "待确认"),
	
	/**
	 * 质检员发起
	 */
	CheckApply(26, "质检员发起"),
	
	/**
	 * 质工程师发起
	 */
	ZLGCApply(27, "质工程师发起");
	
	private int value;
	private String lable;

	private NCRSteps(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRSteps getEnumType(int val) {
		for (NCRSteps type : NCRSteps.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRSteps.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
