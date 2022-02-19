package com.mes.ncr.server.service.po.ncr;

import java.util.ArrayList;
import java.util.List;

public class SendNCRPartTaskShow {
	/**
	 * OrderID
	 */
	public Integer OrderID;

	/**
	 * 工序任务ID
	 */
	public String PartNo;

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
	public List<SendNCRTask> UndoList = new ArrayList<SendNCRTask>();

	/**
	 * 工位已做任务列表
	 */
	public List<SendNCRTask> DoList = new ArrayList<SendNCRTask>();

	/**
	 * 发起任务列表
	 */
	public List<SendNCRTask> SendList = new ArrayList<SendNCRTask>();

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

	public List<SendNCRTask> getSendList() {
		return SendList;
	}

	public void setPartID(Integer partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setSendList(List<SendNCRTask> sendList) {
		SendList = sendList;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public List<SendNCRTask> getUndoList() {
		return UndoList;
	}

	public void setUndoList(List<SendNCRTask> undoList) {
		UndoList = undoList;
	}

	public List<SendNCRTask> getDoList() {
		return DoList;
	}

	public void setDoList(List<SendNCRTask> doList) {
		DoList = doList;
	}

}
