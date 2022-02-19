package com.mes.ncr.server.service.po.ncr;

import java.util.ArrayList;
import java.util.List;

public class NCRTaskShow {

	/**
	 * OrderID
	 */
	public Integer OrderID;

	/**
	 * 工序任务ID
	 */
	public String PartNo;

	/**
	 * 待做任务列表
	 */
	public List<NCRTask> UndoList = new ArrayList<NCRTask>();

	/**
	 * 已做任务列表
	 */
	public List<NCRTask> DoList = new ArrayList<NCRTask>();
	/**
	 * 发起任务列表
	 */
	public List<NCRTask> SendList = new ArrayList<NCRTask>();

	/**
	 * 工位数
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

	public List<NCRTask> getSendList() {
		return SendList;
	}

	public void setSendList(List<NCRTask> sendList) {
		SendList = sendList;
	}

	public int getStationNum() {
		return StationNum;
	}

	public void setStationNum(int stationNum) {
		StationNum = stationNum;
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

}
