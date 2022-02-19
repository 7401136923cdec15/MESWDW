package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;



public enum BFCMessageStatus {
	/**
	 * 默认 未读
	 */
	Default(0, "默认"),

	/**
	 * 已发送
	 */
	Sent(1, "已发送"),
	/**
	 * 已读
	 */
	Read(2, "已读"),

	Finished(3, "已处理"),

	Close(4, "已关闭");
	
	private int value;
	private String lable;

	private BFCMessageStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BFCMessageStatus getEnumType(int val) {
		for (BFCMessageStatus type : BFCMessageStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (BFCMessageStatus type : BFCMessageStatus.values()) {
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
