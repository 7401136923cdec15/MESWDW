package com.mes.ncr.server.service.mesenum;

public enum MTCStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待发起移车
	 */
	ToSendTask(1, "待发起移车"),
	/**
	 * 发起移车任务
	 */
	SendTask(2, "发起移车任务"),
//	/**
//	 * 待四工区主管审批
//	 */
//	SGAudit(3, "待四工区主管审批"),
//	/**
//	 * 四工区主管驳回
//	 */
//	SGReject(4, "四工区主管驳回"),
	/**
	 * 任务撤销
	 */
	TaskCancel(21, "任务撤销"),
	/**
	 * 已驳回
	 */
	TaskReject(22, "已驳回"),
	/**
	 * 已完工
	 */
	Completion(5, "已完工");
	
	
	
	private int value;
	private String lable;

	private MTCStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MTCStatus getEnumType(int val) {
		for (MTCStatus type : MTCStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return MTCStatus.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
	
}
