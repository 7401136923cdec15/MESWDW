package com.mes.ncr.server.service.po.bfc;

import java.io.Serializable;
import java.util.Calendar;

public class BFCMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BFCMessage() {
		// TODO Auto-generated constructor stub
	}

	public int CompanyID;
	public long ID;
	/**
	 * 消息来源ID  如EXC的任务ID 
	 */
	public long MessageID;
	/**
	 * 配置模块ID  BPMEventModule
	 */
	public long ModuleID;
	/**
	 * 
	 */
	public long StationID;
	
	
	public String StationNo;
	
	
	public int StepID=0;
	
	/**
	 * 消息接收人ID
	 */
	public long ResponsorID;
	
	/**
	 * 0 默认 不需要执行  1 需要执行 
	 */
	public long Type;
	
	public String Title = "";
	public String MessageText = "";
	public int ShiftID = 0;
	public Calendar EditTime = Calendar.getInstance();
	public Calendar CreateTime = Calendar.getInstance();

	/**
	 *  0 未读 //   1 已发送未读  //  2 已读  （已读后不发送了）  //3 已办     // 4 已关闭
	 */
	public int Active = 0;
	
	public int SendStatus = 0;

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getMessageID() {
		return MessageID;
	}

	public void setMessageID(long messageID) {
		MessageID = messageID;
	}

	public long getModuleID() {
		return ModuleID;
	}

	public void setModuleID(long moduleID) {
		ModuleID = moduleID;
	}

	public long getResponsorID() {
		return ResponsorID;
	}

	public void setResponsorID(long responsorID) {
		ResponsorID = responsorID;
	}

	public long getType() {
		return Type;
	}

	public void setType(long type) {
		Type = type;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getMessageText() {
		return MessageText;
	}

	public void setMessageText(String messageText) {
		MessageText = messageText;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public long getStationID() {
		return StationID;
	}

	public void setStationID(long stationID) {
		StationID = stationID;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public String getStationNo() {
		return StationNo;
	}

	public void setStationNo(String stationNo) {
		StationNo = stationNo;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public int getSendStatus() {
		return SendStatus;
	}

	public void setSendStatus(int sendStatus) {
		SendStatus = sendStatus;
	}

}
