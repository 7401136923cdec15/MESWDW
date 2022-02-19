package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



public enum BMSDutyType {
	/**
	 * ==默认
	 */
	Default(0, "默认"),
	/**
	 * 班长
	 */
	Monitor(1, "班长"),
	/**
	 * 主管
	 */
	Director(2, "主管"),
	/**
	 * 调度
	 */
	Scheduler(3, "调度"),
	/**
	 * 经理
	 */
	Manager(4, "经理"),
	/**
	 * 组员
	 */
	Member(5, "组员");

	private int value;
	private String lable;

	private BMSDutyType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BMSDutyType getEnumType(int val) {
		for (BMSDutyType type : BMSDutyType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (BMSDutyType type : BMSDutyType.values()) {
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
