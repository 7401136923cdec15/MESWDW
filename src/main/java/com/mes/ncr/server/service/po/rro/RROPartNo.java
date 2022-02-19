package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;

/**
 * 车号分类返修
 */
public class RROPartNo implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段
	 */
	public String Customer = "";
	/**
	 * 返修总数
	 */
	public int FQTY = 0;
	/**
	 * 待处理数
	 */
	public int ToDo = 0;
	/**
	 * 已处理数
	 */
	public int Done = 0;

	public RROPartNo() {
		super();
	}

	public RROPartNo(int orderID, String partNo, String lineName, String customer, int fQTY, int toDo, int done) {
		super();
		OrderID = orderID;
		PartNo = partNo;
		LineName = lineName;
		Customer = customer;
		FQTY = fQTY;
		ToDo = toDo;
		Done = done;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getCustomer() {
		return Customer;
	}

	public int getFQTY() {
		return FQTY;
	}

	public int getToDo() {
		return ToDo;
	}

	public int getDone() {
		return Done;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public void setFQTY(int fQTY) {
		FQTY = fQTY;
	}

	public void setToDo(int toDo) {
		ToDo = toDo;
	}

	public void setDone(int done) {
		Done = done;
	}
}
