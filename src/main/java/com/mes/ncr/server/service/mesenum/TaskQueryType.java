package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;

public enum TaskQueryType {
	/**
	 * 待办
	 */
	ToHandle(1, "待办"),
	/**
	 * 已办
	 */
	Handled(4, "已办"),
	/**
	 * 发起
	 */
	Sended(2, "发起");

	private int value;
	private String lable;

	private TaskQueryType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static TaskQueryType getEnumType(int val) {
		for (TaskQueryType type : TaskQueryType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (TaskQueryType type : TaskQueryType.values()) {
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
