package com.mes.ncr.server.service.po.mtc;

public class MTCRealTime {
	
	/**
	 * 车号
	 */
	public String PartNo;
	/**
	 *车辆长度
	 */
	public Double Length;
	/**
	 *车辆类型名称
	 */
	public String CarTypeName;
	/**
	 *库位名称
	 */
	public String StockName;
	/**
	 *台位名称
	 */
	public String StationName;
	
	
	public String getPartNo() {
		return PartNo;
	}
	public void setPartNo(String partNo) {
		PartNo = partNo;
	}
	public Double getLength() {
		return Length;
	}
	public void setLength(Double length) {
		Length = length;
	}
	public String getCarTypeName() {
		return CarTypeName;
	}
	public void setCarTypeName(String carTypeName) {
		CarTypeName = carTypeName;
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
	
}
