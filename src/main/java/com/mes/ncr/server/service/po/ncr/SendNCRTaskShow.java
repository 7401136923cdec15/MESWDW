package com.mes.ncr.server.service.po.ncr;

import java.util.ArrayList;
import java.util.List;

public class SendNCRTaskShow {
	/**
	 * OrderID
	 */
	public Integer OrderID = 0;

	/**
	 * 工序任务ID
	 */
	public String PartNo = "";

	/**
	 * 待做任务列表
	 */
	public List<SendNCRTask> UndoList = new ArrayList<SendNCRTask>();
	/**
	 * 已做任务列表
	 */
	public List<SendNCRTask> DoList = new ArrayList<SendNCRTask>();
	/**
	 * 发起列表
	 */
	public List<SendNCRTask> SendList = new ArrayList<SendNCRTask>();

	/**
	 * 工位数量
	 */
	public int StationNum = 0;

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

	public int getStationNum() {
		return StationNum;
	}

	public void setStationNum(int stationNum) {
		StationNum = stationNum;
	}

	public List<SendNCRTask> getSendList() {
		return SendList;
	}

	public void setSendList(List<SendNCRTask> sendList) {
		SendList = sendList;
	}
}
