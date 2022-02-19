package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.po.ipt.IPTValue;
import com.mes.ncr.server.service.po.ipt.IPTItem;
import com.mes.ncr.server.service.po.ipt.IPTPreCheckProblem;


/**
 * 检验项任务
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-13 10:49:40
 * @LastEditTime 2020-1-13 10:49:44
 *
 */
public class SFCTaskIPTItem implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 工序任务ID
	 */
	public int APSTaskStepID = 0;
	/**
	 * 班次ID
	 */
	public int ShiftID = 0;
	/**
	 * 标准ID
	 */
	public int IPTStandardID = 0;
	/**
	 * 检验项ID
	 */
	public int IPTItemRecordID = 0;
	/**
	 * 自检人
	 */
	public int SelfCheckerID = 0;
	/**
	 * 自检时刻
	 */
	public Calendar SelfCheckTime = Calendar.getInstance();
	/**
	 * 互检人
	 */
	public int MutualCheckerID = 0;
	/**
	 * 互检时刻
	 */
	public Calendar MutualCheckTime = Calendar.getInstance();
	/**
	 * 专检人
	 */
	public int SpeCheckerID = 0;
	/**
	 * 专检时刻
	 */
	public Calendar SpeCheckTime = Calendar.getInstance();
	/**
	 * 任务状态
	 */
	public int Status = 0;

	// 辅助属性
	public int WorkShopID = 0;
	// 修程
	public int LineID = 0;
	public String LineName = "";
	// 局段
	public int CustomID = 0;
	public String CustomName = "";
	// 工位
	public int PartID = 0;
	public String PartNo = "";
	// 工序
	public int StepID = 0;
	public String StepName = "";
	// 车型
	public int ProductID = 0;
	public String ProductNo = "";

	public int StationID = 0;
	public int OrderID = 0;
	/**
	 * 自检、互检、专检结果
	 */
	public List<IPTValue> IPTValueList = new ArrayList<IPTValue>();
	/**
	 * 检验项
	 */
	public IPTItem IPTItem = new IPTItem();
	/**
	 * 自检人
	 */
	public String SelfChecker = "";
	/**
	 * 互检人
	 */
	public String MutualChecker = "";
	/**
	 * 专检人
	 */
	public String SpeChecker = "";
	/**
	 * 状态文本
	 */
	public String StatusText = "";
	/**
	 * 是否是预检
	 */
	public boolean IsPreCheck = false;
	/**
	 * 预检问题项
	 */
	public IPTPreCheckProblem IPTPreCheckProblem = new IPTPreCheckProblem();

	public SFCTaskIPTItem() {
		SelfCheckTime.set(2000, 0, 1);
		MutualCheckTime.set(2000, 0, 1);
		SpeCheckTime.set(2000, 0, 1);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getAPSTaskStepID() {
		return APSTaskStepID;
	}

	public void setAPSTaskStepID(int aPSTaskStepID) {
		APSTaskStepID = aPSTaskStepID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public int getIPTStandartID() {
		return IPTStandardID;
	}

	public void setIPTStandartID(int iPTStandartID) {
		IPTStandardID = iPTStandartID;
	}

	public int getIPTItemRecordID() {
		return IPTItemRecordID;
	}

	public void setIPTItemRecordID(int iPTItemRecordID) {
		IPTItemRecordID = iPTItemRecordID;
	}

	public int getSelfCheckerID() {
		return SelfCheckerID;
	}

	public void setSelfCheckerID(int selfCheckerID) {
		SelfCheckerID = selfCheckerID;
	}

	public Calendar getSelfCheckTime() {
		return SelfCheckTime;
	}

	public void setSelfCheckTime(Calendar selfCheckTime) {
		SelfCheckTime = selfCheckTime;
	}

	public int getMutualCheckerID() {
		return MutualCheckerID;
	}

	public void setMutualCheckerID(int mutualCheckerID) {
		MutualCheckerID = mutualCheckerID;
	}

	public Calendar getMutualCheckTime() {
		return MutualCheckTime;
	}

	public void setMutualCheckTime(Calendar mutualCheckTime) {
		MutualCheckTime = mutualCheckTime;
	}

	public int getSpeCheckerID() {
		return SpeCheckerID;
	}

	public void setSpeCheckerID(int speCheckerID) {
		SpeCheckerID = speCheckerID;
	}

	public Calendar getSpeCheckTime() {
		return SpeCheckTime;
	}

	public void setSpeCheckTime(Calendar speCheckTime) {
		SpeCheckTime = speCheckTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public List<IPTValue> getIPTValueList() {
		return IPTValueList;
	}

	public void setIPTValueList(List<IPTValue> iPTValueList) {
		IPTValueList = iPTValueList;
	}

	public IPTItem getIPTItem() {
		return IPTItem;
	}

	public void setIPTItem(IPTItem iPTItem) {
		IPTItem = iPTItem;
	}

	public String getSelfChecker() {
		return SelfChecker;
	}

	public void setSelfChecker(String selfChecker) {
		SelfChecker = selfChecker;
	}

	public String getMutualChecker() {
		return MutualChecker;
	}

	public void setMutualChecker(String mutualChecker) {
		MutualChecker = mutualChecker;
	}

	public String getSpeChecker() {
		return SpeChecker;
	}

	public void setSpeChecker(String speChecker) {
		SpeChecker = speChecker;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}
}
