package com.mes.ncr.server.service.po.ncr;

import java.util.ArrayList;
import java.util.List;

public class NCRPartTaskShow {

	/**
	 * OrderID
	 */
	public Integer OrderID;

	/**
	 * 工序任务ID
	 */
	public String PartNo = "";

	/**
	 * 工位ID
	 */
	public Integer PartID;

	/**
	 * 工位
	 */
	public String PartName = "";

	/**
	 * 工位待做任务列表
	 */
	public List<NCRTask> UndoList = new ArrayList<NCRTask>();

	/**
	 * 工位已做任务列表
	 */
	public List<NCRTask> DoList = new ArrayList<NCRTask>();

	/**
	 * 发起任务列表
	 */
	public List<NCRTask> SendList = new ArrayList<NCRTask>();

	public Integer getOrderID() {
		return OrderID;
	}

	public void setOrderID(Integer orderID) {
		OrderID = orderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public List<NCRTask> getUndoList() {
		return UndoList;
	}

	public void setUndoList(List<NCRTask> undoList) {
		UndoList = undoList;
	}

	public List<NCRTask> getDoList() {
		return DoList;
	}

	public void setDoList(List<NCRTask> doList) {
		DoList = doList;
	}

	public Integer getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public List<NCRTask> getSendList() {
		return SendList;
	}

	public void setPartID(Integer partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setSendList(List<NCRTask> sendList) {
		SendList = sendList;
	}

}
