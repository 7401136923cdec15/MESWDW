package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 返修任务类
 * 
 * @author ShrisJava
 *
 */
public class RROTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	public int ID = 0;
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 工位ID
	 */
	public int StationID;
	/**
	 * 工位名称
	 */
	public String StationName = "";
	/**
	 * 返修任务类型
	 */
	public int IsDelivery;
	/**
	 * 发起人ID
	 */
	public int UpFlowID;
	/**
	 * 发起人名称
	 */
	public String UpFlowName = "";

	/**
	 * 返修编号(系统自动生成)
	 */
	public String Code = "";
	/**
	 * 车型ID
	 */
	public int CarTypeID;
	/**
	 * 车型名称
	 */
	public String CarTypeName = "";
	/**
	 * 车号
	 */
	public String CarNumber = "";

	/**
	 * 发起时刻
	 */
	public Calendar UpFlowTime = Calendar.getInstance();

//	/**
//	 * 专检任务ID
//	 */
//	public int SpecialTaskID = 0;

	/**
	 * 客户ID
	 */
	public int CustomerID = 0;

	/**
	 * 客户
	 */
	public String CustomerName = "";

	/**
	 * 修程ID
	 */
	public int LineID = 0;

	/**
	 * 修程
	 */
	public String LineName = "";

	/**
	 * 返修项
	 */
	public List<RROItemTask> RepairItemList = new ArrayList<RROItemTask>();

	/**
	 * 验收检验员名称
	 */
	public String CheckName;

	/**
	 * 返修任务类型
	 */
	public String IsDeliveryName = "";

	// 辅助属性
	/**
	 * 工位数量
	 */
	public int StationNum = 0;

	public RROTask() {
		CheckName = "";
	}

	public List<RROItemTask> getRepairItemList() {
		return RepairItemList;
	}

	public void setRepairItemList(List<RROItemTask> repairItemList) {
		RepairItemList = repairItemList;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getCarTypeID() {
		return CarTypeID;
	}

	public void setCarTypeID(int carTypeID) {
		CarTypeID = carTypeID;
	}

	public String getIsDeliveryName() {
		return IsDeliveryName;
	}

	public int getStationNum() {
		return StationNum;
	}

	public void setIsDeliveryName(String isDeliveryName) {
		IsDeliveryName = isDeliveryName;
	}

	public void setStationNum(int stationNum) {
		StationNum = stationNum;
	}

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
	}

	public int getIsDelivery() {
		return IsDelivery;
	}

	public void setIsDelivery(int isDelivery) {
		IsDelivery = isDelivery;
	}

	public String getCarTypeName() {
		return CarTypeName;
	}

	public void setCarTypeName(String carTypeName) {
		CarTypeName = carTypeName;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getCheckName() {
		return CheckName;
	}

	public void setCheckName(String checkName) {
		CheckName = checkName;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getUpFlowID() {
		return UpFlowID;
	}

	public void setUpFlowID(int upFlowID) {
		UpFlowID = upFlowID;
	}

	public String getUpFlowName() {
		return UpFlowName;
	}

	public void setUpFlowName(String upFlowName) {
		UpFlowName = upFlowName;
	}

	public Calendar getUpFlowTime() {
		return UpFlowTime;
	}

	public void setUpFlowTime(Calendar upFlowTime) {
		UpFlowTime = upFlowTime;
	}
}
