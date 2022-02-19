package com.mes.ncr.server.service.po.rro;

import java.util.ArrayList;
import java.util.List;

public class RROSeleteType {
	
	/**
	 * 订单
	 */
	public Integer OrderID = 0;
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * 修程ID
	 */
	public int LineID;
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 车型ID
	 */
	public int ProductID;
	/**
	 * 车型编码
	 */
	public String ProductNo = "";
	/**
	 * 局段ID
	 */
	public int BureauSectionID;
	/**
	 * 局段
	 */
	public String BureauSection = "";

	/**
	 * 车号
	 */
	public String PartNo = "";
	
	/**
	 * 车号
	 */
	public List<Integer>  TypeList=new ArrayList<Integer>();
	

}
