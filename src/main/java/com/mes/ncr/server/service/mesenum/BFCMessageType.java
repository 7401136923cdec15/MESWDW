package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



public enum BFCMessageType {
	/**
	 * 默认 无须处理
	 */
	Default(0, "默认"),
	
	Notify(1, "通知"),
	/**
	 * APP
	 */
	Task(2, "任务");
	
	private int value;
	private String lable;

	private BFCMessageType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BFCMessageType getEnumType(int val) {
		for (BFCMessageType type : BFCMessageType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (BFCMessageType type : BFCMessageType.values()) {
			CFGItem wItem=new CFGItem();
			wItem.ID=type.getValue();
			wItem.ItemName=type.getLable();
			wItem.ItemText=type.getLable();
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
