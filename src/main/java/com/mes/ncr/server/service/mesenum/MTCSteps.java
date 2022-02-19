package com.mes.ncr.server.service.mesenum;


public enum MTCSteps {
	
	/**
	 * 默认
	 */
	Default(0, "默认"),
	
	/**
	 * 发起申请
	 */
	SendApply(1, "发起申请"),
	
	/**
	 * 接收方工区审批
	 */
	ReceiveApproval(2, "接收方工区审批"),
	
	/**
	 * 四工区审批
	 */
	FourApproval(3, "四工区审批"),
	
	/**
	 * 移车班成员处理
	 */
	Finished(4, "移车班成员处理");
	
	private int value;
	private String lable;

	private MTCSteps(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MTCSteps getEnumType(int val) {
		for (MTCSteps type : MTCSteps.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return MTCSteps.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
