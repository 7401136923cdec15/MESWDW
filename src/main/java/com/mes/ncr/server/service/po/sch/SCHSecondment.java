package com.mes.ncr.server.service.po.sch;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 人员借调单
 * 
 * @author PengYouWang
 * @CreateTime 2020年1月6日10:04:55
 * @LastEditTime 2020年1月6日22:24:46
 *
 */
public class SCHSecondment implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;
	/**
	 * 发起人
	 */
	public int SendID;
	public String SendName = "";
	/**
	 * 发起时刻
	 */
	public Calendar SendTime = Calendar.getInstance();
	/**
	 * 借调班组
	 */
	public int SecondDepartmentID;
	public String SecondDepartment = "";

	/**
	 * 是否跨工区
	 */
	public boolean IsOverArea;

	/**
	 * 跨工区ID
	 */
	public int AreaID;
	public String AreaName = "";

	/**
	 * 借调方工区主管信息
	 */
	public int SecondAuditID;
	public String SecondAuditor = "";
	public Calendar SendAuditTime = Calendar.getInstance();

	/**
	 * 被借调方工区主管信息
	 */
	public int BeSecondAuditID;
	public String BeSecondAuditor = "";
	public Calendar BeSecondAuditTime = Calendar.getInstance();

	/**
	 * 被借调人员
	 */
	public int SecondPersonID;
	public String SecondPerson = "";

	/**
	 * 截止时间
	 */
	public Calendar ValidDate = Calendar.getInstance();

	/**
	 * 被借调部门信息
	 */
	public int BeSecondDepartmentID;
	public String BeSecondDepartment = "";

	/**
	 * 单据状态
	 */
	public int Status;

//	/**
//	 * 作业工位ID
//	 */
//	public int StationID = 0;
//	/**
//	 * 作业工位名称
//	 */
//	public String Station = "";

	public SCHSecondment() {
		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1);

		SendTime = wBaseTime;
		SendAuditTime = wBaseTime;
		BeSecondAuditTime = wBaseTime;
		ValidDate = wBaseTime;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public int getSecondAuditID() {
		return SecondAuditID;
	}

//	public int getStationID() {
//		return StationID;
//	}
//
//	public void setStationID(int stationID) {
//		StationID = stationID;
//	}
//
//	public String getStation() {
//		return Station;
//	}
//
//	public void setStation(String station) {
//		Station = station;
//	}

	public void setSecondAuditID(int secondAuditID) {
		SecondAuditID = secondAuditID;
	}

	public String getSecondAuditor() {
		return SecondAuditor;
	}

	public void setSecondAuditor(String secondAuditor) {
		SecondAuditor = secondAuditor;
	}

	public int getBeSecondAuditID() {
		return BeSecondAuditID;
	}

	public void setBeSecondAuditID(int beSecondAuditID) {
		BeSecondAuditID = beSecondAuditID;
	}

	public String getBeSecondAuditor() {
		return BeSecondAuditor;
	}

	public void setBeSecondAuditor(String beSecondAuditor) {
		BeSecondAuditor = beSecondAuditor;
	}

	public int getBeSecondDepartmentID() {
		return BeSecondDepartmentID;
	}

	public void setBeSecondDepartmentID(int beSecondDepartmentID) {
		BeSecondDepartmentID = beSecondDepartmentID;
	}

	public String getBeSecondDepartment() {
		return BeSecondDepartment;
	}

	public void setBeSecondDepartment(String beSecondDepartment) {
		BeSecondDepartment = beSecondDepartment;
	}

	public Calendar getBeSecondAuditTime() {
		return BeSecondAuditTime;
	}

	public void setBeSecondAuditTime(Calendar beSeondAuditTime) {
		BeSecondAuditTime = beSeondAuditTime;
	}

	public int getSecondDepartmentID() {
		return SecondDepartmentID;
	}

	public void setSecondDepartmentID(int secondDepartmentID) {
		SecondDepartmentID = secondDepartmentID;
	}

	public String getSecondDepartment() {
		return SecondDepartment;
	}

	public void setSecondDepartment(String secondDepartment) {
		SecondDepartment = secondDepartment;
	}

	public int getSecondPersonID() {
		return SecondPersonID;
	}

	public int getSendID() {
		return SendID;
	}

	public void setSendID(int sendID) {
		SendID = sendID;
	}

	public String getSendName() {
		return SendName;
	}

	public void setSendName(String sendName) {
		SendName = sendName;
	}

	public void setSecondPersonID(int secondPersonID) {
		SecondPersonID = secondPersonID;
	}

	public String getSecondPerson() {
		return SecondPerson;
	}

	public void setSecondPerson(String secondPerson) {
		SecondPerson = secondPerson;
	}

	public Calendar getSendTime() {
		return SendTime;
	}

	public void setSendTime(Calendar sendTime) {
		SendTime = sendTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public boolean isIsOverArea() {
		return IsOverArea;
	}

	public void setIsOverArea(boolean isOverArea) {
		IsOverArea = isOverArea;
	}

	public Calendar getSendAuditTime() {
		return SendAuditTime;
	}

	public void setSendAuditTime(Calendar sendAuditTime) {
		SendAuditTime = sendAuditTime;
	}

	public Calendar getValidDate() {
		return ValidDate;
	}

	public void setValidDate(Calendar validDate) {
		ValidDate = validDate;
	}
}
