package com.mes.ncr.server.service.po.rro;

/**
 * 返修项状态
 * 
 * @author ShrisJava
 *
 */
public enum RRORepairStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待工区调度
	 */
	ToSchedule(1, "待工区调度"),
	/**
	 * 待分配班组
	 */
	ToDistribute(2, "待分配班组"),
	/**
	 * 驳回返修项
	 */
	RejectItem(3, "驳回返修项"),
	/**
	 * 待班组长分配
	 */
	ToMonitorHandle(4, "待班组长分配"),
	/**
	 * 待班组成员开工
	 */
	ToOperate(5, "待班组成员开工"),
	/**
	 * 班组成员已开工
	 */
	StartWork(10, "班组成员已开工"),
	
	/**
	 * 待班组长确认
	 */
	ToMonitorAffirm(7, "待班组长确认"),
	/**
	 * 待检验员确认
	 */
	ToConfirm(8, "待检验员确认"),
	
	/**
	 * 已发起不合格评审
	 */
	ItemFail(20, "已发起不合格评审"),
	
	/**
	 * 已确认
	 */
	Confirmed(25, "已确认");


	private int value;
	private String lable;

	private RRORepairStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RRORepairStatus getEnumType(int val) {
		for (RRORepairStatus type : RRORepairStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return RRORepairStatus.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
