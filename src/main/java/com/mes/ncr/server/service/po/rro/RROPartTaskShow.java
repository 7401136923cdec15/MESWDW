package com.mes.ncr.server.service.po.rro;

import java.util.ArrayList;
import java.util.List;

public class RROPartTaskShow {

	/**
	 * OrderID
	 */
	public Integer OrderID;

	/**
	 * 车号
	 */
	public String PartNo;

	/**
	 * 工位
	 */
	public Integer PartID;

	/**
	 * 工位名称
	 */
	public String PartName = "";

	/**
	 * 工位待做任务列表
	 */
	public List<RROItemTask> UndoList = new ArrayList<RROItemTask>();

	/**
	 * 工位已做任务列表
	 */
	public List<RROItemTask> DoList = new ArrayList<RROItemTask>();
	/**
	 * 发起任务列表
	 */
	public List<RROItemTask> SendList = new ArrayList<RROItemTask>();

	public Integer getOrderID() {
		return OrderID;
	}

	public void setOrderID(Integer orderID) {
		OrderID = orderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public Integer getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public List<RROItemTask> getSendList() {
		return SendList;
	}

	public void setPartID(Integer partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setSendList(List<RROItemTask> sendList) {
		SendList = sendList;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public List<RROItemTask> getUndoList() {
		return UndoList;
	}

	public void setUndoList(List<RROItemTask> undoList) {
		UndoList = undoList;
	}

	public List<RROItemTask> getDoList() {
		return DoList;
	}

	public void setDoList(List<RROItemTask> doList) {
		DoList = doList;
	}

}
