package com.mes.ncr.server.service.po.mtc;

import java.io.Serializable;
import java.util.Calendar;

import com.mes.ncr.server.service.po.bpm.BPMTaskBase;

/**
 * 移车业务表
 * 
 * @author ShrisJava
 *
 */
public class MTCTask extends BPMTaskBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 车型ID
	 */
	public int CarTypeID = 0;

	/**
	 * 车型名称
	 */
	public String CarType = "";

	/**
	 * 工件车号 在此业务工件是火车头指代台车编号
	 */
	public String PartNo = "";

	/**
	 * 台位ID
	 */
	public int PlaceID = 0;
	/**
	 * 台位名称
	 */
	public String PlaceName = "";

	/**
	 * 目标ID 这里指代目标台位(变更后的)
	 */
	public int TargetID = 0;
	/**
	 * 目标台位名称
	 */
	public String TargetName = "";

	/**
	 * 预计完成时间
	 */
	public Calendar ExpectedTime = Calendar.getInstance();

	/**
	 * 台位工区 不存
	 */
	public int WorkShopID = 0;

	/**
	 * 目标台位工区
	 */
	public int TargetWorkShopID = 0;

	/**
	 * 台位库 不存
	 */
	public int StockID = 0;

	/**
	 * 台位库名称
	 */
	public String StockName = "";

	/**
	 * 目标库位ID
	 */
	public int TargetStockID = 0;

	/**
	 * 目标库位
	 */
	public String TargetStockName = "";

	/**
	 * 目标ID 这里指代目标台位(原始的)
	 */
	public int TargetSID = 0;
	/**
	 * 目标台位名称
	 */
	public String TargetSName = "";

	/**
	 * 目标库位ID
	 */
	public int TargetSStockID = 0;

	/**
	 * 目标库位
	 */
	public String TargetSStockName = "";

	/**
	 * 客户ID (局段)
	 */
	public int CustomerID = 0;

	/**
	 * 客户名称（局段名称）
	 */
	public String CustomerName = "";

	/**
	 * 订单ID
	 */
	public int OrderID = 0;

	/**
	 * 任务类型： 1-普通类型 2-直移类型
	 */
	public int Type = 0;

	/**
	 * 工区ID
	 */
	public int DepartmentID = 0;

	/**
	 * 工区
	 */
	public String DepartmentName = "";

	/**
	 * 工区主管IDList
	 */
	public String AreaID = "";

	/**
	 * 工区主管IDList
	 */
	public String AreaName = "";
	/**
	 * 是否通知调车转运班（1：通知）
	 */
	public int InformShift = 0;

	/**
	 * 班组长IDList
	 */
	public String MonitorID = "";

	/**
	 * 班组长
	 */
	public String MonitorName = "";

	/**
	 * 是否为预移车(1：是 0：否)
	 */
	public int IsPreMove = 0;

	// 辅助属性
	public int IsMoveClass = 0;

	public int TagTypes = 0;

	public MTCTask() {
		super();
		ExpectedTime.set(2000, 0, 1, 0, 0, 0);
		PlaceName = "";
		TargetName = "";
		StockName = "";
		TargetStockName = "";
		DepartmentName = "";
		AreaID = "";
		MonitorID = "";
		MonitorName = "";
	}

	public String getPlaceName() {
		return PlaceName;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}

	public String getTargetName() {
		return TargetName;
	}

	public void setTargetName(String targetName) {
		TargetName = targetName;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getPlaceID() {
		return PlaceID;
	}

	public void setPlaceID(int placeID) {
		PlaceID = placeID;
	}

	public int getTargetID() {
		return TargetID;
	}

	public void setTargetID(int targetID) {
		TargetID = targetID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getTargetWorkShopID() {
		return TargetWorkShopID;
	}

	public void setTargetWorkShopID(int targetWorkShopID) {
		TargetWorkShopID = targetWorkShopID;
	}

	public int getStockID() {
		return StockID;
	}

	public void setStockID(int stockID) {
		StockID = stockID;
	}

	public int getTargetStockID() {
		return TargetStockID;
	}

	public void setTargetStockID(int targetStockID) {
		TargetStockID = targetStockID;
	}

	public String getStockName() {
		return StockName;
	}

	public void setStockName(String stockName) {
		StockName = stockName;
	}

	public String getTargetStockName() {
		return TargetStockName;
	}

	public void setTargetStockName(String targetStockName) {
		TargetStockName = targetStockName;
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

	public Calendar getExpectedTime() {
		return ExpectedTime;
	}

	public void setExpectedTime(Calendar expectedTime) {
		ExpectedTime = expectedTime;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getCarTypeID() {
		return CarTypeID;
	}

	public void setCarTypeID(int carTypeID) {
		CarTypeID = carTypeID;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getCarType() {
		return CarType;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setDepartmentID(int departmentID) {
		DepartmentID = departmentID;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public String getAreaID() {
		return AreaID;
	}

	public void setAreaID(String areaID) {
		AreaID = areaID;
	}

	public int getTargetSID() {
		return TargetSID;
	}

	public void setTargetSID(int targetSID) {
		TargetSID = targetSID;
	}

	public String getTargetSName() {
		return TargetSName;
	}

	public void setTargetSName(String targetSName) {
		TargetSName = targetSName;
	}

	public int getTargetSStockID() {
		return TargetSStockID;
	}

	public void setTargetSStockID(int targetSStockID) {
		TargetSStockID = targetSStockID;
	}

	public String getTargetSStockName() {
		return TargetSStockName;
	}

	public void setTargetSStockName(String targetSStockName) {
		TargetSStockName = targetSStockName;
	}

	public String getAreaName() {
		return AreaName;
	}

	public void setAreaName(String areaName) {
		AreaName = areaName;
	}

	public int getInformShift() {
		return InformShift;
	}

	public void setInformShift(int informShift) {
		InformShift = informShift;
	}

	public String getMonitorID() {
		return MonitorID;
	}

	public void setMonitorID(String monitorID) {
		MonitorID = monitorID;
	}

	public String getMonitorName() {
		return MonitorName;
	}

	public void setMonitorName(String monitorName) {
		MonitorName = monitorName;
	}

	public int getIsMoveClass() {
		return IsMoveClass;
	}

	public void setIsMoveClass(int isMoveClass) {
		IsMoveClass = isMoveClass;
	}

	public int getIsPreMove() {
		return IsPreMove;
	}

	public void setIsPreMove(int isPreMove) {
		IsPreMove = isPreMove;
	}
}
