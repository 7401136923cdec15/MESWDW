package com.mes.ncr.server.service.po.mtc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 移车节数信息配置
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-12-16 09:27:03
 * @LastEditTime 2020-12-16 09:27:07
 *
 */
public class MTCSectionInfo implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 车节代表字符
	 */
	public String Name = "";
	/**
	 * 车型ID
	 */
	public int ProductID = 0;
	/**
	 * 车型编号
	 */
	public String ProductNo = "";
	/**
	 * 创建人ID
	 */
	public int CreateID = 0;
	/**
	 * 创建人
	 */
	public String Creator = "";
	/**
	 * 创建时间
	 */
	public Calendar CreateTime = Calendar.getInstance();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}
}
