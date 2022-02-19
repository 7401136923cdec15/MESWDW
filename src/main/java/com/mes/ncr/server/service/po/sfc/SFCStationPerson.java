package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工位人员
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-18 11:35:49
 * @LastEditTime 2020-7-18 11:35:55
 *
 */
public class SFCStationPerson implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 工位ID
	 */
	public int StationID = 0;
	/**
	 * 工位名称
	 */
	public String StationName = "";
	/**
	 * 工区ID
	 */
	public int AreaID = 0;
	/**
	 * 工区名称
	 */
	public String AreaName = "";
	/**
	 * 工艺师ID集合
	 */
	public List<Integer> TechnicianList = new ArrayList<>();
	/**
	 * 工艺师名称
	 */
	public String Technicians = "";
	/**
	 * 工区主管ID集合
	 */
	public List<Integer> AuditorList = new ArrayList<>();
	/**
	 * 工区主管名称
	 */
	public String Auditors = "";

	public SFCStationPerson() {
		super();
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public int getAreaID() {
		return AreaID;
	}

	public void setAreaID(int areaID) {
		AreaID = areaID;
	}

	public String getAreaName() {
		return AreaName;
	}

	public void setAreaName(String areaName) {
		AreaName = areaName;
	}

	public List<Integer> getTechnicianList() {
		return TechnicianList;
	}

	public void setTechnicianList(List<Integer> technicianList) {
		TechnicianList = technicianList;
	}

	public String getTechnicians() {
		return Technicians;
	}

	public void setTechnicians(String technicians) {
		Technicians = technicians;
	}

	public List<Integer> getAuditorList() {
		return AuditorList;
	}

	public void setAuditorList(List<Integer> auditorList) {
		AuditorList = auditorList;
	}

	public String getAuditors() {
		return Auditors;
	}

	public void setAuditors(String auditors) {
		Auditors = auditors;
	}
}
