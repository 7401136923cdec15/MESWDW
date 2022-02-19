package com.mes.ncr.server.service.po.ncr;
 

public enum NCRHandleResult {

	/**
	 * 默认
	 */
	Default(0, "默认"), 
	/**
	 * 返工
	 */
	Rework(1, "返工"),
	/**
	 * 返修
	 */
	Repair(2, "返修"), 
	/**
	 * 让步放行(关闭时间)
	 */
	LetGoTime(3, "让步放行(关闭时间)"), 
	/**
	 * 报废
	 */
	sCrapt(4, "报废"),
	/**
	 * 退回供应商
	 */
	BackGYS(5, "退回供应商"),
	/**
	 * 下发方案
	 */
	SendFA(6, "下发方案"), 
	/**
	 * 其他
	 */
	Others(7, "其他"),
	/**
	 * 让步放行(关闭工位)
	 */
	LetGoStation(8, "让步放行(关闭工位)"),
	
	/**
	 * 让步接收
	 */
	LetGoReceive(9, "让步接收");

	private int value;
	private String lable;

	private NCRHandleResult(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static NCRHandleResult getEnumType(int val) {
		for (NCRHandleResult type : NCRHandleResult.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return NCRHandleResult.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
