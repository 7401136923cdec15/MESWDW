package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.po.fmc.FMCStation;
import com.mes.ncr.server.shristool.LoggerTool;



/**
 * 巡检任务
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-5-27 15:50:11
 * @LastEditTime 2020-5-27 15:50:11
 *
 */
public class SFCTaskIPT implements Serializable {
	// 33
	private static final long serialVersionUID = 1L;

	public int ID = 0;
	// [DataMember(Name = "LineID", Order = 1)]
	public int LineID = 0; // 产线ID
	// [DataMember(Name = "PartID", Order = 2)]
	public int PartID = 0; // 工段ID
	// [DataMember(Name = "PartPointID", Order = 3)]
	public int PartPointID = 0; // 工序ID
	// [DataMember(Name = "StationID", Order = 4)]
	public int StationID = 0; // 工位ID
	// [DataMember(Name = "ProductID", Order = 5)]
	public int ProductID = 0; // 产品ID
	// [DataMember(Name = "TaskStepID", Order = 6)]
	public int TaskStepID = 0; // 工序任务ID
	// [DataMember(Name = "TaskType", Order = 7)]
	public int TaskType = 0; // 巡检类别ID
	// [DataMember(Name = "ModuleVersionID", Order = 8)]
	public int ModuleVersionID = 0; // 版本（版本一旦用过，不允许修改，必须创建新版本）
	// [DataMember(Name = "OperatorID", Order = 9)]
	public int OperatorID = 0; // 操作员ID
	// [DataMember(Name = "ShiftID", Order = 10)]
	public int ShiftID = 0; // 班次ID
	// [DataMember(Name = "ActiveTime", Order = 11)]
	public Calendar ActiveTime = Calendar.getInstance(); // 任务激活时间
	// [DataMember(Name = "Status", Order = 12)]
	public int Status = 0; // 任务状态
	// [DataMember(Name = "SubmitTime", Order = 13)]
	public Calendar SubmitTime = Calendar.getInstance(); // 任务提交时间
	// [DataMember(Name = "Result", Order = 14)]
	public int Result = 0; // 结果
	// [DataMember(Name = "WorkShopID", Order = 15)]
	public int WorkShopID = 0; // 车间ID
	// [DataMember(Name = "TaskMode", Order = 16)]
	public int TaskMode = 0;
	// [DataMember(Name = "Times", Order = 17)]
	public int Times = 0;
	// [DataMember(Name = "FQTYGood", Order = 18)]
	public int FQTYGood = 0;
	// [DataMember(Name = "FQTYBad", Order = 19)]
	public int FQTYBad = 0;
	// 辅助属性
	// [DataMember(Name = "OperatorName", Order = 20)]
	public String OperatorName; // 操作员
	// [DataMember(Name = "WorkShopName", Order = 21)]
	public String WorkShopName;
	// [DataMember(Name = "LineName", Order = 22)]
	public String LineName;
	// [DataMember(Name = "PartName", Order = 23)]
	public String PartName;
	// [DataMember(Name = "PartPointName", Order = 24)]
	public String PartPointName;
	// [DataMember(Name = "StationName", Order = 25)]
	public String StationName;
	// [DataMember(Name = "OrderNo", Order = 26)]
	public String OrderNo;
	// [DataMember(Name = "ProductNo", Order = 27)]
	public String ProductNo;
	// [DataMember(Name = "StatusText", Order = 28)]
	public String StatusText; // 任务状态
	// [DataMember(Name = "TypeText", Order = 29)]
	public String TypeText; // 巡检类别
	// [DataMember(Name = "ModeText", Order = 30)]
	public String ModeText;
	// [DataMember(Name = "EventID", Order = 31)]
	public int EventID = 0;
	// [DataMember(Name = "ItemList", Order = 32)]
	public String PartNo = "";// 车号
	public int OrderID = 0;// 订单号
	public List<SFCIPTItem> ItemList = new ArrayList<>();

	/**
	 * 局段ID
	 */
	public int CustomerID = 0;
	/**
	 * 局段名称
	 */
	public String CustomerName = "";
	/**
	 * 操作时间
	 */
	public Calendar OperateTime = Calendar.getInstance();
	// 2020-5-27 15:51:09
	/**
	 * 类型(工序任务、问题项任务、质量任务)
	 */
	public int Type = 0;
	/**
	 * 开始时间
	 */
	public Calendar StartTime = Calendar.getInstance();
	/**
	 * 结束时间
	 */
	public Calendar EndTime = Calendar.getInstance();
	/**
	 * 操作人列表
	 */
	public List<Integer> OperatorList = new ArrayList<Integer>();

	public SFCTaskIPT() {
		this.ID = 0;
		this.LineID = 0;
		this.PartID = 0;
		this.PartPointID = 0;
		this.StationID = 0;

		this.ProductID = 0;
		this.TaskStepID = 0;
		this.TaskType = 0;

		this.ModuleVersionID = 0;
		this.OperatorID = 0;
		this.ShiftID = 0;
		this.TaskMode = 0;
		this.Times = 0;

		this.FQTYGood = 0;
		this.FQTYBad = 0;
		this.WorkShopID = 0;
		this.EventID = 0;

		this.Status = 0;

		this.OperatorName = "";
		this.WorkShopName = "";
		this.LineName = "";
		this.PartName = "";
		this.PartPointName = "";
		this.StationName = "";
		this.OrderNo = "";

		this.ProductNo = "";
		this.StatusText = "";
		this.TypeText = "";
		this.ModeText = "";
		this.ActiveTime = Calendar.getInstance();
		this.SubmitTime = Calendar.getInstance();
		this.ItemList = new ArrayList<>();

		this.OperateTime.set(2000, 0, 1);
		this.StartTime.set(2000, 0, 1);
		this.EndTime.set(2000, 0, 1);
	}

	public SFCTaskIPT(FMCStation wStation, SFCTaskType wTaskYype) {
		this.StationID = wStation.ID;
		this.TaskType = wTaskYype.getValue();
		this.Status = 0;
		this.ActiveTime = Calendar.getInstance();
		this.SubmitTime = Calendar.getInstance();
		this.ModuleVersionID = wStation.IPTModuleID;
		this.WorkShopID = wStation.WorkShopID;
		this.LineID = wStation.LineID;
		this.ItemList = new ArrayList<>();
	}

	public SFCTaskIPT Clone() {
		SFCTaskIPT wItem = new SFCTaskIPT();
		try {
			wItem.LineID = this.LineID;
			wItem.PartID = this.PartID;
			wItem.PartPointID = this.PartPointID;
			wItem.StationID = this.StationID;
			wItem.ProductID = this.ProductID;
			wItem.TaskStepID = this.TaskStepID;
			wItem.TaskType = this.TaskType;
			wItem.ModuleVersionID = this.ModuleVersionID;
			wItem.OperatorID = this.OperatorID;
			wItem.ShiftID = this.ShiftID;
			wItem.ActiveTime = this.ActiveTime;
			wItem.Status = this.Status;
			wItem.SubmitTime = this.SubmitTime;
			wItem.Result = this.Result;
			wItem.WorkShopID = this.WorkShopID;
			wItem.TaskMode = this.TaskMode;
			wItem.Times = this.Times;
			wItem.FQTYGood = this.FQTYGood;
			wItem.FQTYBad = this.FQTYBad;
			wItem.EventID = this.EventID;
			wItem.OrderID = this.OrderID;
			wItem.OrderNo = this.OrderNo;
			wItem.PartNo = this.PartNo;
			wItem.Type = this.Type;
			wItem.ItemList = new ArrayList<SFCIPTItem>(this.ItemList);
		} catch (Exception ex) {
			LoggerTool.SaveException("SFCService", "SFCTaskIPT Clone", "Function Exception:" + ex.toString());
		}
		return wItem;

	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getLineID() {
		return LineID;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public List<Integer> getOperatorList() {
		return OperatorList;
	}

	public void setOperatorList(List<Integer> operatorList) {
		OperatorList = operatorList;
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

	public int getResult() {
		return Result;
	}

	public Calendar getOperateTime() {
		return OperateTime;
	}

	public void setOperateTime(Calendar operateTime) {
		OperateTime = operateTime;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public Calendar getStartTime() {
		return StartTime;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public int getTaskStepID() {
		return TaskStepID;
	}

	public void setTaskStepID(int taskStepID) {
		TaskStepID = taskStepID;
	}

	public int getTaskType() {
		return TaskType;
	}

	public void setTaskType(int taskType) {
		TaskType = taskType;
	}

	public int getModuleVersionID() {
		return ModuleVersionID;
	}

	public void setModuleVersionID(int moduleVersionID) {
		ModuleVersionID = moduleVersionID;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public Calendar getActiveTime() {
		return ActiveTime;
	}

	public void setActiveTime(Calendar activeTime) {
		ActiveTime = activeTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
	}

	public int isResult() {
		return Result;
	}

	public void setResult(int result) {
		Result = result;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getTaskMode() {
		return TaskMode;
	}

	public void setTaskMode(int taskMode) {
		TaskMode = taskMode;
	}

	public int getTimes() {
		return Times;
	}

	public void setTimes(int times) {
		Times = times;
	}

	public int getFQTYGood() {
		return FQTYGood;
	}

	public void setFQTYGood(int fQTYGood) {
		FQTYGood = fQTYGood;
	}

	public int getFQTYBad() {
		return FQTYBad;
	}

	public void setFQTYBad(int fQTYBad) {
		FQTYBad = fQTYBad;
	}

	public String getOperatorName() {
		return OperatorName;
	}

	public void setOperatorName(String operatorName) {
		OperatorName = operatorName;
	}

	public String getWorkShopName() {
		return WorkShopName;
	}

	public void setWorkShopName(String workShopName) {
		WorkShopName = workShopName;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public String getTypeText() {
		return TypeText;
	}

	public void setTypeText(String typeText) {
		TypeText = typeText;
	}

	public String getModeText() {
		return ModeText;
	}

	public void setModeText(String modeText) {
		ModeText = modeText;
	}

	public int getEventID() {
		return EventID;
	}

	public void setEventID(int eventID) {
		EventID = eventID;
	}

	public List<SFCIPTItem> getItemList() {
		return ItemList;
	}

	public void setItemList(List<SFCIPTItem> itemList) {
		ItemList = itemList;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}
}
