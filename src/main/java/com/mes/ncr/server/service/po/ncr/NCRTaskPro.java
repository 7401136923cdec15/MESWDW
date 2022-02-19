package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;
import java.util.Calendar;

import com.mes.ncr.server.service.po.bpm.BPMTaskBase;

/**
 * NCR任务
 * 
 * @author ShrisJava
 *
 */
public class NCRTaskPro extends BPMTaskBase implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 工序任务ID
	 */
	public int TaskStepID;

	/**
	 * 质量检查项点ID
	 */
	public int IPTItemID = 0;

	/**
	 * 订单ID
	 */
	public int OrderID = 0;

	/**
	 * 客户ID (局段)
	 */
	public int CustomerID = 0;

	/**
	 * 客户名称（局段名称）
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
	 * 工位ID
	 */
	public int StationID = 0;

	/**
	 * 工位
	 */
	public String StationName = "";

	/**
	 * 车型ID
	 */
	public int CarTypeID;
	/**
	 * 车型名称
	 */
	public String CarType = "";
	/**
	 * 车号
	 */
	public String CarNumber = "";
	/**
	 * 不合格品数量
	 */
	public int Number;
	/**
	 * 发生部门ID
	 */
	public int DepartmentID;
	/**
	 * 发生部门名称
	 */
	public String Department = "";
	/**
	 * 产品不合格描述
	 */
	public String DescribeInfo = "";
	/**
	 * 评定等级
	 */
	public int Level;
	/**
	 * 不合格品类别
	 */
	public int Type;
	/**
	 * 处理结果
	 */
	public int Result;
	/**
	 * 评定等级
	 */
	public String LevelName = "";
	/**
	 * 不合格品类别
	 */
	public String TypeName = "";
	/**
	 * 处理结果
	 */
	public String ResultName = "";

	/**
	 * 其他结果
	 */
	public String OtherResult = "";

	/**
	 * 关联NCRID
	 */
	public int NCRID;

	/**
	 * 关联NCR单据编号
	 */
	public String NCRCode;

	/**
	 * 自检任务ID
	 */
	public int TaskIPTID;

	/**
	 * 需关闭的工位ID
	 */
	public int CloseStationID;
	/**
	 * 需关闭的工位
	 */
	public String CloseStationName = "";

	// 蓝图第二版新增
	/**
	 * 发起类型
	 */
	public int SendType;
	/**
	 * 关闭节点时刻
	 */
	public Calendar CloseTime = Calendar.getInstance();

	/**
	 * 申请单ID
	 */
	public int SendNCRID;
	/**
	 * 申请人
	 */
	public String SendNCRName = "";

	/**
	 * 图片
	 */
	public String ImageList = "";

	/**
	 * 责任部门ID
	 */
	public String DutyDepartmentID = "";

	/**
	 * 责任部门
	 */
	public String DutyDepartmentName = "";

	/**
	 * 责任工艺师
	 */
	public String DutyCarfID = "";

	/**
	 * 责任工艺师名称
	 */
	public String DutyCarfName = "";

	// 新增字段2020-11-27 09:38:58
	/**
	 * 产品名称
	 */
	public String ProductName = "";
	/**
	 * 型号规格
	 */
	public String ModelNo = "";
	/**
	 * 问题类型
	 */
	public int QuestionType = 0;
	/**
	 * 问题类型文本
	 */
	public String QuestionTypeText = "";

	/**
	 * 单据类型 1：申请单 2：评审单
	 */
	public int FormType = 0;

	/**
	 * 车号集合
	 */
	public String PartNos = "";

	public int getTaskStepID() {
		return TaskStepID;
	}

	public void setTaskStepID(int taskStepID) {
		TaskStepID = taskStepID;
	}

	public NCRTaskPro() {
		super();
		CloseTime.set(2000, 0, 1, 0, 0, 0);
	}

	public int getSendType() {
		return SendType;
	}

	public void setSendType(int sendType) {
		SendType = sendType;
	}

	public Calendar getCloseTime() {
		return CloseTime;
	}

	public void setCloseTime(Calendar closeTime) {
		CloseTime = closeTime;
	}

	public String getOtherResult() {
		return OtherResult;
	}

	public void setOtherResult(String otherResult) {
		OtherResult = otherResult;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public String getCarType() {
		return CarType;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
	}

	public int getNumber() {
		return Number;
	}

	public void setNumber(int number) {
		Number = number;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setDepartmentID(int departmentID) {
		DepartmentID = departmentID;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getDescribeInfo() {
		return DescribeInfo;
	}

	public void setDescribeInfo(String describeInfo) {
		DescribeInfo = describeInfo;
	}

	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getResult() {
		return Result;
	}

	public void setResult(int result) {
		Result = result;
	}

	public int getCloseStationID() {
		return CloseStationID;
	}

	public void setCloseStationID(int closeStationID) {
		CloseStationID = closeStationID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
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

	public int getIPTItemID() {
		return IPTItemID;
	}

	public void setIPTItemID(int iPTItemID) {
		IPTItemID = iPTItemID;
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

	public int getSendNCRID() {
		return SendNCRID;
	}

	public void setSendNCRID(int sendNCRID) {
		SendNCRID = sendNCRID;
	}

	public String getImageList() {
		return ImageList;
	}

	public void setImageList(String imageList) {
		ImageList = imageList;
	}

	public String getCloseStationName() {
		return CloseStationName;
	}

	public void setCloseStationName(String closeStationName) {
		CloseStationName = closeStationName;
	}

	public String getLevelName() {
		return LevelName;
	}

	public void setLevelName(String levelName) {
		LevelName = levelName;
	}

	public String getTypeName() {
		return TypeName;
	}

	public void setTypeName(String typeName) {
		TypeName = typeName;
	}

	public String getResultName() {
		return ResultName;
	}

	public void setResultName(String resultName) {
		ResultName = resultName;
	}

	public String getDutyDepartmentName() {
		return DutyDepartmentName;
	}

	public void setDutyDepartmentName(String dutyDepartmentName) {
		DutyDepartmentName = dutyDepartmentName;
	}

	public String getDutyCarfID() {
		return DutyCarfID;
	}

	public void setDutyCarfID(String dutyCarfID) {
		DutyCarfID = dutyCarfID;
	}

	public String getDutyCarfName() {
		return DutyCarfName;
	}

	public void setDutyCarfName(String dutyCarfName) {
		DutyCarfName = dutyCarfName;
	}

	public String getDutyDepartmentID() {
		return DutyDepartmentID;
	}

	public void setDutyDepartmentID(String dutyDepartmentID) {
		DutyDepartmentID = dutyDepartmentID;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getModelNo() {
		return ModelNo;
	}

	public void setModelNo(String modelNo) {
		ModelNo = modelNo;
	}

	public int getQuestionType() {
		return QuestionType;
	}

	public void setQuestionType(int questionType) {
		QuestionType = questionType;
	}

	public String getQuestionTypeText() {
		return QuestionTypeText;
	}

	public void setQuestionTypeText(String questionTypeText) {
		QuestionTypeText = questionTypeText;
	}

	public String getSendNCRName() {
		return SendNCRName;
	}

	public void setSendNCRName(String sendNCRName) {
		SendNCRName = sendNCRName;
	}

	public int getNCRID() {
		return NCRID;
	}

	public void setNCRID(int nCRID) {
		NCRID = nCRID;
	}

	public String getNCRCode() {
		return NCRCode;
	}

	public void setNCRCode(String nCRCode) {
		NCRCode = nCRCode;
	}

	public int getTaskIPTID() {
		return TaskIPTID;
	}

	public void setTaskIPTID(int taskIPTID) {
		TaskIPTID = taskIPTID;
	}

	public int getFormType() {
		return FormType;
	}

	public void setFormType(int formType) {
		FormType = formType;
	}

	public String getPartNos() {
		return PartNos;
	}

	public void setPartNos(String partNos) {
		PartNos = partNos;
	}
}
