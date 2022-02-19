package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 返修表
 * 
 * @author ShrisJava
 *
 */
public class RRORepairTable implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 操作记录ID
	 */
	public int ID;
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
	public int LineID = 0;
	/**
	 * 修程名称
	 */
	public String LineName = "";
	/**
	 * 任务类型
	 */
	public int Type = 0;
	/**
	 * 任务类型名称
	 */
	public String TypeName = "";
	/**
	 * 局段ID
	 */
	public int CustomerID = 0;

	/**
	 * 局段名称
	 */
	public String CustomerName = "";
	/**
	 * 发起人ID
	 */
	public int SenderID;
	/**
	 * 发起人名称
	 */
	public String SenderName = "";
	/**
	 * 发起时刻
	 */
	public Calendar SendTime = Calendar.getInstance();

	/**
	 * 审批人ID
	 */
	public int ApprovalID;
	/**
	 * 审批人名称
	 */
	public String ApprovalName = "";
	/**
	 * 审批时刻
	 */
	public Calendar ApprovalTime = Calendar.getInstance();

	/**
	 * 任务状态 1：已提交 2：已审批
	 */
	public int Status;
	/**
	 * 表详情
	 */
	public List<RROTableBody> RROTableBodyList = new ArrayList<RROTableBody>();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getTypeName() {
		return TypeName;
	}

	public void setTypeName(String typeName) {
		TypeName = typeName;
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

	public int getSenderID() {
		return SenderID;
	}

	public void setSenderID(int senderID) {
		SenderID = senderID;
	}

	public String getSenderName() {
		return SenderName;
	}

	public void setSenderName(String senderName) {
		SenderName = senderName;
	}

	public Calendar getSendTime() {
		return SendTime;
	}

	public void setSendTime(Calendar sendTime) {
		SendTime = sendTime;
	}

	public int getApprovalID() {
		return ApprovalID;
	}

	public void setApprovalID(int approvalID) {
		ApprovalID = approvalID;
	}

	public String getApprovalName() {
		return ApprovalName;
	}

	public void setApprovalName(String approvalName) {
		ApprovalName = approvalName;
	}

	public Calendar getApprovalTime() {
		return ApprovalTime;
	}

	public void setApprovalTime(Calendar approvalTime) {
		ApprovalTime = approvalTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public List<RROTableBody> getRROTableBodyList() {
		return RROTableBodyList;
	}

	public void setRROTableBodyList(List<RROTableBody> rROTableBodyList) {
		RROTableBodyList = rROTableBodyList;
	}
}
