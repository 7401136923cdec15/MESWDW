package com.mes.ncr.server.service.po.rro;

/**
 * 返修任务状态
 * 
 * @author ShrisJava
 *
 */
public enum RROStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待上传图片
	 */
	ToPictures(1, "待上传图片"),
	/**
	 * 待确认
	 */
	ToConfirmed(2, "待确认"),
	/**
	 * 已确认
	 */
	Confirmed(3, "已确认"),
	/**
	 * 已确认
	 */
	Done(20, "已确认"),
	/**
	 * 已确认
	 */
	Confirm(25, "已确认"),
	/**
	 * 撤销
	 */
	Cancle(21, "撤销");

	private int value;
	private String lable;

	private RROStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RROStatus getEnumType(int val) {
		for (RROStatus type : RROStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return RROStatus.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
