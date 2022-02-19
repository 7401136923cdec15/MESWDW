package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;

/**
 * 工位分类返修
 */
public class RROPart implements Serializable {

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
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工位名称
	 */
	public String PartName = "";
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

	public RROPart() {
		super();
	}

	public RROPart(int orderID, String partNo, String lineName, String customer, int partID, String partName, int fQTY,
			int toDo, int done) {
		super();
		OrderID = orderID;
		PartNo = partNo;
		LineName = lineName;
		Customer = customer;
		PartID = partID;
		PartName = partName;
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

	public int getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
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
