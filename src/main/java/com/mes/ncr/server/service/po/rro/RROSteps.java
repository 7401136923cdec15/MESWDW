package com.mes.ncr.server.service.po.rro;

/**
 * 返修步骤枚举
 * 
 * @author ShrisJava
 *
 */
public enum RROSteps {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 供应商流程
	 */
	Supplier(1, "供应商流程"),
	/**
	 * 工区、班组已赋值
	 */
	DonAssign(2, "工区、班组已赋值"),
	/**
	 * 未赋值
	 */
	NotAssign(3, "未赋值");


	private int value;
	private String lable;

	private RROSteps(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RROSteps getEnumType(int val) {
		for (RROSteps type : RROSteps.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return RROSteps.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
