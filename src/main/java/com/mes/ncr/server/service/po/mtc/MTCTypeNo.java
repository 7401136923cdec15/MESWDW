package com.mes.ncr.server.service.po.mtc;

/**
 * 车号及车号对应相关信息
 */
public class MTCTypeNo {
	
	/**
	 * 车型
	 */
	public String ProductNo;
	/**
	 * 车号
	 */
	public String PartNo;
	/**
	 * 转运部件类型
	 */
	public int Transport = 0;
	
	/**
	 * 客户ID (局段)
	 */
	public int CustomerID = 0;

	/**
	 * 客户名称（局段名称）
	 */
	public String CustomerName = "";
	
	/**
	 *库位ID
	 */
	public int StockID;
	/**
	 *台位ID
	 */
	public int StationID;
	/**
	 *库位名称
	 */
	public String StockName;
	/**
	 *台位名称
	 */
	public String StationName;
	
	/**
	 *车号
	 */
	public String Number;
	
	/**
	 *订单ID
	 */
	public int OrderID;
	
	/**
	 * 车型ID
	 */
	public int ProductID;
	
	
	public String getPartNo() {
		return PartNo;
	}
	public void setPartNo(String partNo) {
		PartNo = partNo;
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
	public int getStockID() {
		return StockID;
	}
	public void setStockID(int stockID) {
		StockID = stockID;
	}
	public int getStationID() {
		return StationID;
	}
	public void setStationID(int stationID) {
		StationID = stationID;
	}
	public String getStockName() {
		return StockName;
	}
	public void setStockName(String stockName) {
		StockName = stockName;
	}
	public String getStationName() {
		return StationName;
	}
	public void setStationName(String stationName) {
		StationName = stationName;
	}
	public String getProductNo() {
		return ProductNo;
	}
	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}
	public int getTransport() {
		return Transport;
	}
	public void setTransport(int transport) {
		Transport = transport;
	}
	public String getNumber() {
		return Number;
	}
	public void setNumber(String number) {
		Number = number;
	}
	public int getOrderID() {
		return OrderID;
	}
	public void setOrderID(int orderID) {
		OrderID = orderID;
	}
	public int getProductID() {
		return ProductID;
	}
	public void setProductID(int productID) {
		ProductID = productID;
	}
	
}
