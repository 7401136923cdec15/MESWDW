package com.mes.ncr.server.service.po.lfs;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 工区工位数据
 * 
 * @author ShrisJava
 *
 */
public class LFSWorkAreaStation implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;
	/**
	 * 工区ID
	 */
	public int WorkAreaID;
	/**
	 * 工区名称
	 */
	public String WorkArea = "";
	/**
	 * 工位ID
	 */
	public int StationID;
	/**
	 * 工区名称
	 */
	public String StationName = "";
	/**
	 * 编辑者ID
	 */
	public int CreateID;
	/**
	 * 编辑者
	 */
	public String Creator = "";
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();

	/**
	 * 激活关闭
	 */
	public int Active;
	/**
	 * 顺序
	 */
	public int OrderNum;

	public LFSWorkAreaStation() {
	}

	public String getWorkArea() {
		return WorkArea;
	}

	public void setWorkArea(String workArea) {
		WorkArea = workArea;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getWorkAreaID() {
		return WorkAreaID;
	}

	public void setWorkAreaID(int workAreaID) {
		WorkAreaID = workAreaID;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
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

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}
}
