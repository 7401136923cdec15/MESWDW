package com.mes.ncr.server.service.po.rsm;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



/**
 * 转序申请单状态
 * 
 * @author ShrisJava
 *
 */
public enum RSMTurnOrderTaskStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 评审中
	 */
	Auditing(1, "评审中"),
	/**
	 * 已通过
	 */
	Passed(2, "已通过"),
	/**
	 * 已拒绝
	 */
	Rejected(3, "已拒绝");

	private int value;
	private String lable;

	private RSMTurnOrderTaskStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RSMTurnOrderTaskStatus getEnumType(int val) {
		for (RSMTurnOrderTaskStatus type : RSMTurnOrderTaskStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (RSMTurnOrderTaskStatus type : RSMTurnOrderTaskStatus.values()) {
			CFGItem wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
