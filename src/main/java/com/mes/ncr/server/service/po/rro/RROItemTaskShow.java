package com.mes.ncr.server.service.po.rro;

import java.util.ArrayList;
import java.util.List;

public class RROItemTaskShow {

	/**
	 * OrderID
	 */
	public Integer OrderID;

	/**
	 * 车号
	 */
	public String PartNo;

	/**
	 * 待做任务列表
	 */
	public List<RROItemTask> UndoList = new ArrayList<RROItemTask>();

	/**
	 * 已做任务列表
	 */
	public List<RROItemTask> DoList = new ArrayList<RROItemTask>();

	/**
	 * 发起任务列表
	 */
	public List<RROItemTask> SendList = new ArrayList<RROItemTask>();

	/**
	 * 工位总数
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

	public List<RROItemTask> getUndoList() {
		return UndoList;
	}

	public List<RROItemTask> getSendList() {
		return SendList;
	}

	public void setSendList(List<RROItemTask> sendList) {
		SendList = sendList;
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

	public int getStationNum() {
		return StationNum;
	}

	public void setStationNum(int stationNum) {
		StationNum = stationNum;
	}
}
