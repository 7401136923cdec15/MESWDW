package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;

import com.mes.ncr.server.service.po.oms.OMSOrder;

/**
 * NCR任务车辆分类信息
 */
public class NCRCarInfo implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 待办数
	 */
	public int FQTYToDo = 0;
	/**
	 * 已办数
	 */
	public int FQTYDone = 0;
	/**
	 * 订单详情
	 */
	public OMSOrder OMSOrder = new OMSOrder();
	/**
	 * 订单ID
	 */
	public int OrderID = 0;

	public NCRCarInfo() {
		super();
	}

	public NCRCarInfo(String partNo, int fQTYToDo, int fQTYDone) {
		super();
		PartNo = partNo;
		FQTYToDo = fQTYToDo;
		FQTYDone = fQTYDone;
	}

	public String getPartNo() {
		return PartNo;
	}

	public int getFQTYToDo() {
		return FQTYToDo;
	}

	public int getFQTYDone() {
		return FQTYDone;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setFQTYToDo(int fQTYToDo) {
		FQTYToDo = fQTYToDo;
	}

	public void setFQTYDone(int fQTYDone) {
		FQTYDone = fQTYDone;
	}

	public OMSOrder getOMSOrder() {
		return OMSOrder;
	}

	public void setOMSOrder(OMSOrder oMSOrder) {
		OMSOrder = oMSOrder;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}
}
