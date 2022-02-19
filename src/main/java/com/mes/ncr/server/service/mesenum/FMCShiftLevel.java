package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



public enum FMCShiftLevel {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Day(1, "白班"),
	/**
	 * APP
	 */
	Middle(2, "中班"),
	/**
	 * APP
	 */
	Night(3, "晚班");
	private int value;
	private String lable;

	private FMCShiftLevel(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static FMCShiftLevel getEnumType(int val) {
		for (FMCShiftLevel type : FMCShiftLevel.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (FMCShiftLevel type : FMCShiftLevel.values()) {
			CFGItem wItem=new CFGItem();
			wItem.ID=type.getValue();
			wItem.ItemName=type.getLable();
			wItem.ItemText=type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}
	
	public static int MaxValue() {
		return Night.value;
	}
	public static int MinValue() {
		return Day.value;
	}
	
	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
