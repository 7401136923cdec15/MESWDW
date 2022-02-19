package com.mes.ncr.server.service.po.rro;

/**
 * 返修任务状态
 * 
 * @author ShrisJava
 *
 */
public enum RROTaskTypes {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 试运前返修
	 */
	Delivery(1, "试运前返修"),
	/**
	 * 过程检返修(专检不合格发起)
	 */
	IsDelivery(2, "过程检返修"),
	/**
	 * 供应商返修
	 */
	Supplier(3, "供应商返修"),
	/**
	 * 试运后返修
	 */
	PilotRun(4, "试运后返修"),
	/**
	 * 验收返修
	 */
	CheckRepair(5, "验收返修"),
	
	/**
	 * 过程中返修
	 */
	Plication(6, "过程中返修");
	


	private int value;
	private String lable;

	private RROTaskTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RROTaskTypes getEnumType(int val) {
		for (RROTaskTypes type : RROTaskTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return RROTaskTypes.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
