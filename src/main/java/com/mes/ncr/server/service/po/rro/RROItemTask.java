package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;

import com.mes.ncr.server.service.po.bpm.BPMTaskBase;

/**
 * 返修项
 * 
 * @author ShrisJava
 *
 */
public class RROItemTask extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

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
	 * 修程ID
	 */
	public int LineID;
	/**
	 * 修程
	 */
	public String LineName = "";

	/**
	 * 客户
	 */
	public String CustomerName = "";

	/**
	 * 工位名称
	 */
	public String StationName = "";

	/**
	 * 订单ID
	 */
	public int OrderID;
	/**
	 * 任务ID
	 */
	public int TaskID;
	/**
	 * 检查项
	 */
	public String Content = "";

	/**
	 * 分配的工区ID
	 */
	public int WorkAreaID;
	/**
	 * 返修工区名称
	 */
	public String WorkAreaName = "";
	/**
	 * 返修班组ID
	 */
	public int TeamID;
	/**
	 * 返修班组名称
	 */
	public String TeamName = "";

	/**
	 * 操作员ID
	 */
	public int OperatorID;
	/**
	 * 操作员名称
	 */
	public String OperatorName = "";
	/**
	 * 返修项是否合格 0：未操作 1合格 2不合格
	 */
	public int IsStatus;
	/**
	 * 操作员发起不合格评审状态（0：未发起 1：已发起 ）
	 */
	public int IsSendNCR;
	/**
	 * 绑定NCR的ID
	 */
	public int NCRID;
	/**
	 * 每张返修单据的返修项标识（第一项、第二项…………等等）
	 */
	public String ItemLogo;
	/**
	 * 工序任务ID
	 */
	public int TaskStepID = 0;

	/**
	 * 质量检查项点ID
	 */
	public int IPTItemID = 0;
	/**
	 * 客户ID
	 */
	public int CustomerID = 0;

	/**
	 * 工位ID
	 */
	public int StationID = 0;

	/**
	 * 工序ID
	 */
	public int ProcessID = 0;
	/**
	 * 工序名称
	 */
	public String ProcesName = "";
	/**
	 * 责任人备注
	 */
	public String Remark = "";

	/**
	 * 验收检验员名称
	 */
	public String CheckName;
	/**
	 * 返修任务类型
	 */
	public int IsDelivery;
	/**
	 * 返修任务类型
	 */
	public String IsDeliveryName = "";

	public int TagTypes = 0;

	/**
	 * 图片
	 */
	public String ImageUrl = "";

	/**
	 * 供应商ID
	 */
	public int SupplierID = 0;
	/**
	 * 供应商名称
	 */
	public String SupplierName = "";

	public RROItemTask() {
		ItemLogo = "";
		CheckName = "";
	}

	public String getItemLogo() {
		return ItemLogo;
	}

	public int getTaskStepID() {
		return TaskStepID;
	}

	public int getIPTItemID() {
		return IPTItemID;
	}

	public String getIsDeliveryName() {
		return IsDeliveryName;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTaskStepID(int taskStepID) {
		TaskStepID = taskStepID;
	}

	public void setIPTItemID(int iPTItemID) {
		IPTItemID = iPTItemID;
	}

	public void setIsDeliveryName(String isDeliveryName) {
		IsDeliveryName = isDeliveryName;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public void setItemLogo(String itemLogo) {
		ItemLogo = itemLogo;
	}

	public int getIsSendNCR() {
		return IsSendNCR;
	}

	public void setIsSendNCR(int isSendNCR) {
		IsSendNCR = isSendNCR;
	}

	public int getNCRID() {
		return NCRID;
	}

	public void setNCRID(int nCRID) {
		NCRID = nCRID;
	}

	public int getTeamID() {
		return TeamID;
	}

	public String getWorkAreaName() {
		return WorkAreaName;
	}

	public void setWorkAreaName(String workAreaName) {
		WorkAreaName = workAreaName;
	}

	public String getTeamName() {
		return TeamName;
	}

	public void setTeamName(String teamName) {
		TeamName = teamName;
	}

	public void setTeamID(int teamID) {
		TeamID = teamID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getTaskID() {
		return TaskID;
	}

	public void setTaskID(int taskID) {
		TaskID = taskID;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getWorkAreaID() {
		return WorkAreaID;
	}

	public void setWorkAreaID(int workAreaID) {
		WorkAreaID = workAreaID;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public String getOperatorName() {
		return OperatorName;
	}

	public void setOperatorName(String operatorName) {
		OperatorName = operatorName;
	}

	public int getIsStatus() {
		return IsStatus;
	}

	public void setIsStatus(int isStatus) {
		IsStatus = isStatus;
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

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
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

	public int getProcessID() {
		return ProcessID;
	}

	public void setProcessID(int processID) {
		ProcessID = processID;
	}

	public String getProcesName() {
		return ProcesName;
	}

	public void setProcesName(String procesName) {
		ProcesName = procesName;
	}

	public String getCheckName() {
		return CheckName;
	}

	public void setCheckName(String checkName) {
		CheckName = checkName;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getIsDelivery() {
		return IsDelivery;
	}

	public void setIsDelivery(int isDelivery) {
		IsDelivery = isDelivery;
	}

	public int getCarTypeID() {
		return CarTypeID;
	}

	public void setCarTypeID(int carTypeID) {
		CarTypeID = carTypeID;
	}

	public String getCarTypeName() {
		return CarTypeName;
	}

	public void setCarTypeName(String carTypeName) {
		CarTypeName = carTypeName;
	}

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
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

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	public int getSupplierID() {
		return SupplierID;
	}

	public void setSupplierID(int supplierID) {
		SupplierID = supplierID;
	}

	public String getSupplierName() {
		return SupplierName;
	}

	public void setSupplierName(String supplierName) {
		SupplierName = supplierName;
	}
}
