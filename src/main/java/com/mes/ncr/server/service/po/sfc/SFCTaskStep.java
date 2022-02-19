package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 派工任务
 * 
 * @author ShrisJava
 *
 */
public class SFCTaskStep implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	public int TaskLineID = 0;
	public int TaskPartID = 0;
	/**
	 * 工序任务ID
	 */
	public int TaskStepID = 0;
	public int WorkShopID = 0;
	public int LineID = 0;
	public int PartID = 0;
	public int StepID = 0;
	public int ProductID = 0; // 产品ID

	/**
	 * 派工班组长
	 */
	public int MonitorID = 0;
	public String Monitor = "";
	/**
	 * 派工指定班次
	 */
	public int ShiftID = 0; // 班次
	/**
	 * 分配工时
	 */
	public int WorkHour = 0;
	/**
	 * 操作工
	 */
	public int OperatorID = 0;
	public String Operator = "";
	/**
	 * 派工任务创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 任务下达时刻
	 */
	public Calendar ReadyTime = Calendar.getInstance();
	/**
	 * 任务开始时刻
	 */
	public Calendar StartTime = Calendar.getInstance();
	/**
	 * 任务结束时刻
	 */
	public Calendar EndTime = Calendar.getInstance();
	/**
	 * 是否开工(1：开工 0：未开工)
	 */
	public int IsStartWork = 0;

	public int Status = 0; // 状态
	public String OrderNo = "";
	public String ProductNo = "";
	public String PartName = "";
	public String MaterialNo = "";
	public String MaterialName = "";
	public int PlanerID = 0;
	public String PlanerName = "";
	public String PartPointName = "";
	public String LineName = "";
	public int StepOrderID = 0; // 工序先后循序ID
	public String TaskText = "";
	public Calendar ShiftDate = Calendar.getInstance(); // 任务计划日期

	// 优化设计
	public SFCTaskStep() {
		this.ID = 0;
		this.OrderID = 0;
		this.TaskLineID = 0;
		this.TaskPartID = 0;
		this.LineID = 0;

		this.PartID = 0;

		this.ShiftID = 0;
		this.ProductID = 0;
		this.Status = 0;
		this.PlanerID = 0;

		this.StepOrderID = 0;

		this.OrderNo = "";
		this.ProductNo = "";
		this.LineName = "";
		this.PartName = "";
		this.PartPointName = "";

		this.MaterialNo = "";
		this.MaterialName = "";
		this.PlanerName = "";
		this.TaskText = "";

		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
		this.ShiftDate = Calendar.getInstance();
	}

	public SFCTaskStep Clone() {
		SFCTaskStep wTaskStep = new SFCTaskStep();
		wTaskStep.ID = this.ID;
		wTaskStep.OrderID = this.OrderID;
		wTaskStep.TaskLineID = this.TaskLineID;
		wTaskStep.TaskPartID = this.TaskPartID;
		wTaskStep.LineID = this.LineID;
		wTaskStep.PartID = this.PartID;
		wTaskStep.ShiftID = this.ShiftID;
		wTaskStep.ProductID = this.ProductID;

		wTaskStep.StartTime = this.StartTime;
		wTaskStep.EndTime = this.EndTime;
		wTaskStep.Status = this.Status;
		wTaskStep.OrderNo = this.OrderNo;
		wTaskStep.ProductNo = this.ProductNo;

		wTaskStep.PartName = this.PartName;
		wTaskStep.MaterialNo = this.MaterialNo;
		wTaskStep.MaterialName = this.MaterialName;
		wTaskStep.PlanerID = this.PlanerID;
		wTaskStep.PlanerName = this.PlanerName;

		wTaskStep.LineName = this.LineName;
		wTaskStep.StepOrderID = this.StepOrderID;
		wTaskStep.TaskText = this.TaskText;
		wTaskStep.ShiftDate = this.ShiftDate;

		return wTaskStep;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getTaskStepID() {
		return TaskStepID;
	}

	public int getMonitorID() {
		return MonitorID;
	}

	public void setMonitorID(int monitorID) {
		MonitorID = monitorID;
	}

	public String getMonitor() {
		return Monitor;
	}

	public void setMonitor(String monitor) {
		Monitor = monitor;
	}

	public void setTaskStepID(int taskStepID) {
		TaskStepID = taskStepID;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getTaskLineID() {
		return TaskLineID;
	}

	public void setTaskLineID(int taskLineID) {
		TaskLineID = taskLineID;
	}

	public int getTaskPartID() {
		return TaskPartID;
	}

	public void setTaskPartID(int taskPartID) {
		TaskPartID = taskPartID;
	}

	public int getLineID() {
		return LineID;
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

	public Calendar getStartTime() {
		return StartTime;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public int getWorkHour() {
		return WorkHour;
	}

	public void setWorkHour(int workHour) {
		WorkHour = workHour;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public Calendar getReadyTime() {
		return ReadyTime;
	}

	public void setReadyTime(Calendar readyTime) {
		ReadyTime = readyTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
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

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public int getPlanerID() {
		return PlanerID;
	}

	public void setPlanerID(int planerID) {
		PlanerID = planerID;
	}

	public String getPlanerName() {
		return PlanerName;
	}

	public void setPlanerName(String planerName) {
		PlanerName = planerName;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getStepOrderID() {
		return StepOrderID;
	}

	public void setStepOrderID(int stepOrderID) {
		StepOrderID = stepOrderID;
	}

	public String getTaskText() {
		return TaskText;
	}

	public void setTaskText(String taskText) {
		TaskText = taskText;
	}

	public Calendar getShiftDate() {
		return ShiftDate;
	}

	public void setShiftDate(Calendar shiftDate) {
		ShiftDate = shiftDate;
	}

//	public List<SFCMaterial> getMaterialList() {
//		return MaterialList;
//	}
//
//	public void setMaterialList(List<SFCMaterial> materialList) {
//		MaterialList = materialList;
//	}
//
//	public boolean isMaterialCheck() {
//		return MaterialCheck;
//	}
//
//	public void setMaterialCheck(boolean materialCheck) {
//		MaterialCheck = materialCheck;
//	}
//
//	public int getMaterialCheckID() {
//		return MaterialCheckID;
//	}
//
//	public void setMaterialCheckID(int materialCheckID) {
//		MaterialCheckID = materialCheckID;
//	}
//
//	public List<SFCMaterialCheck> getMaterialCheckList() {
//		return MaterialCheckList;
//	}
//
//	public void setMaterialCheckList(List<SFCMaterialCheck> materialCheckList) {
//		MaterialCheckList = materialCheckList;
//	}
//
//	public List<SFCPartLogger> getPartLoggerList() {
//		return PartLoggerList;
//	}
//
//	public void setPartLoggerList(List<SFCPartLogger> partLoggerList) {
//		PartLoggerList = partLoggerList;
//	}
//
//	public int getWLMode() {
//		return WLMode;
//	}
//
//	public void setWLMode(int wLMode) {
//		WLMode = wLMode;
//	}
//
//	public int getBGMode() {
//		return BGMode;
//	}
//
//	public void setBGMode(int bGMode) {
//		BGMode = bGMode;
//	}
}
