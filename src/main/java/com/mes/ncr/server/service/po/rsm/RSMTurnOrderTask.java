package com.mes.ncr.server.service.po.rsm;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 转序任务
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-3-18 15:24:27
 * @LastEditTime 2020-3-18 15:24:30
 *
 */
public class RSMTurnOrderTask implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 申请人ID
	 */
	public int ApplyID = 0;
	/**
	 * 申请时刻
	 */
	public Calendar ApplyTime = Calendar.getInstance();
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 申请工位ID
	 */
	public int ApplyStationID = 0;
	/**
	 * 目标工位ID
	 */
	public int TargetStationID = 0;
	/**
	 * 任务状态
	 */
	public int Status = 0;
	/**
	 * 工位任务ID
	 */
	public int TaskPartID = 0;

	// 辅助信息
	/**
	 * 申请人名称
	 */
	public String ApplyName = "";
	/**
	 * 订单编号
	 */
	public String OrderNo = "";
	/**
	 * 车号
	 */
	public String CarNo = "";
	/**
	 * 申请工位名称
	 */
	public String ApplyStationName = "";
	/**
	 * 目标工位名称
	 */
	public String TargetStationName = "";
	/**
	 * 状态文本
	 */
	public String StatusText = "";
	/**
	 * 转序类型：1：主动转序 2：自动转序
	 */
	public int Type = 0;
	/**
	 * 备注
	 */
	public String Remark = "";

	public RSMTurnOrderTask() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getApplyID() {
		return ApplyID;
	}

	public void setApplyID(int applyID) {
		ApplyID = applyID;
	}

	public Calendar getApplyTime() {
		return ApplyTime;
	}

	public void setApplyTime(Calendar applyTime) {
		ApplyTime = applyTime;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getApplyStationID() {
		return ApplyStationID;
	}

	public void setApplyStationID(int applyStationID) {
		ApplyStationID = applyStationID;
	}

	public int getTargetStationID() {
		return TargetStationID;
	}

	public void setTargetStationID(int targetStationID) {
		TargetStationID = targetStationID;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getApplyName() {
		return ApplyName;
	}

	public void setApplyName(String applyName) {
		ApplyName = applyName;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getCarNo() {
		return CarNo;
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public String getApplyStationName() {
		return ApplyStationName;
	}

	public void setApplyStationName(String applyStationName) {
		ApplyStationName = applyStationName;
	}

	public String getTargetStationName() {
		return TargetStationName;
	}

	public void setTargetStationName(String targetStationName) {
		TargetStationName = targetStationName;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getTaskPartID() {
		return TaskPartID;
	}

	public void setTaskPartID(int taskPartID) {
		TaskPartID = taskPartID;
	}
}
