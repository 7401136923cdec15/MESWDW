package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



public enum APSShiftPeriod {
	/**
	 * SCH
	 */
	Minute(1, "分"),
	/**
	 * APP
	 */
	Hour(2, "小时"),
	/**
	 * SCH
	 */
	Shift(3, "班"),
	/**
	 * APP
	 */
	Day(4, "天"),
	/**
	 * APP
	 */
	Week(5, "周"),
	/**
	 * APP
	 */
	Month(6, "月");
	private int value;
	private String lable;

	private APSShiftPeriod(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSShiftPeriod getEnumType(int val) {
		for (APSShiftPeriod type : APSShiftPeriod.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (APSShiftPeriod type : APSShiftPeriod.values()) {
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
