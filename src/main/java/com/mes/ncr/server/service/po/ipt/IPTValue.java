package com.mes.ncr.server.service.po.ipt;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 表单值
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-13 09:58:19
 * @LastEditTime 2020-1-13 09:58:22
 *
 */
public class IPTValue implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public long ID;

	public long StandardID;

	public long IPTItemID;

	public String Value;

	public String Remark;

	public int Result;

	// ---------广机特有-----------
	public int TaskID = 0;
	public int IPTMode = 0;

	public int ItemType = 0;
	public String ImagePath = "";
	public String VideoPath = "";
	public int SolveID = 0;
	public int SubmitID = 0;
	public String Submitor = "";
	public Calendar SubmitTime = Calendar.getInstance();

	public IPTValue() {
		Value = "";
		Result = 1;
		Remark = "";
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public int getTaskID() {
		return TaskID;
	}

	public void setTaskID(int taskID) {
		TaskID = taskID;
	}

	public int getIPTMode() {
		return IPTMode;
	}

	public void setIPTMode(int iPTMode) {
		IPTMode = iPTMode;
	}

	public long getStandardID() {
		return StandardID;
	}

	public void setStandardID(long standardID) {
		StandardID = standardID;
	}

	public long getIPTItemID() {
		return IPTItemID;
	}

	public void setIPTItemID(long iPTItemID) {
		IPTItemID = iPTItemID;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getResult() {
		return Result;
	}

	public void setResult(int result) {
		Result = result;
	}

	public int getItemType() {
		return ItemType;
	}

	public void setItemType(int itemType) {
		ItemType = itemType;
	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}

	public String getVideoPath() {
		return VideoPath;
	}

	public void setVideoPath(String videoPath) {
		VideoPath = videoPath;
	}

	public int getSolveID() {
		return SolveID;
	}

	public void setSolveID(int solveID) {
		SolveID = solveID;
	}

	public int getSubmitID() {
		return SubmitID;
	}

	public void setSubmitID(int submitID) {
		SubmitID = submitID;
	}

	public String getSubmitor() {
		return Submitor;
	}

	public void setSubmitor(String submitor) {
		Submitor = submitor;
	}

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
	}
}