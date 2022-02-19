package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 偶换件不合格评审-物料子表
 */
public class SFCBOMTaskItem implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	/**
	 * 偶换件评审单ID
	 */
	public int SFCBOMTaskID = 0;

	/**
	 * 对应的BOMID 存储
	 */
	public int BOMID = 0;
	/**
	 * 对应的BOM子项ID
	 */
	public int BOMItemID = 0;

	/**
	 * 物料ID 存储
	 */
	public int MaterialID = 0;

	public String MaterialNo = "";
	public String MaterialName = "";

	/**
	 * 物料数量 存储
	 */
	public Double MaterialNumber = 0.0;
	/**
	 * 物料单位 存储
	 */
	public int UnitID = 0;
	public String UnitText = "";

	/**
	 * 等级 存储
	 */
	public int Level = 0;
	public String LevelName = "";

	/**
	 * 责任
	 */
	public int Responsibility = 0;
	public String ResponsibilityName = "";

	/**
	 * 1常规新件 2修复旧件
	 */
	public int SapType = 0;
	public String SapTypeName = "";

	/**
	 * 评审意见
	 */
	public int ReviewComments = 0;

	public String ReviewCommentsName = "";

	/**
	 * 虚拟驳回状态
	 */
	public int Status = 0;
	/**
	 * 虚拟驳回备注
	 */
	public String Remark = "";
	/**
	 * 虚拟驳回操作人ID
	 */
	public int OperatorID = 0;
	/**
	 * 虚拟驳回操作人
	 */
	public String Operator = "";
	/**
	 * 虚拟驳回操作时刻
	 */
	public Calendar OperateTime = Calendar.getInstance();
	/**
	 * 定额
	 */
	public int Quota = 0;

	/**
	 * 质量损失大类
	 */
	public String QualityLossBig = "";
	/**
	 * 质量损失小类
	 */
	public String QualityLossSmall = "";

	/**
	 * 班次
	 */
	public String ShiftID = "";

	public SFCBOMTaskItem() {
		super();
	}

	public int getSFCBOMTaskID() {
		return SFCBOMTaskID;
	}

	public int getBOMID() {
		return BOMID;
	}

	public String getQualityLossBig() {
		return QualityLossBig;
	}

	public String getQualityLossSmall() {
		return QualityLossSmall;
	}

	public void setQualityLossBig(String qualityLossBig) {
		QualityLossBig = qualityLossBig;
	}

	public void setQualityLossSmall(String qualityLossSmall) {
		QualityLossSmall = qualityLossSmall;
	}

	public String getReviewCommentsName() {
		return ReviewCommentsName;
	}

	public String getResponsibilityName() {
		return ResponsibilityName;
	}

	public int getQuota() {
		return Quota;
	}

	public void setQuota(int quota) {
		Quota = quota;
	}

	public void setResponsibilityName(String responsibilityName) {
		ResponsibilityName = responsibilityName;
	}

	public void setReviewCommentsName(String reviewCommentsName) {
		ReviewCommentsName = reviewCommentsName;
	}

	public int getBOMItemID() {
		return BOMItemID;
	}

	public int getMaterialID() {
		return MaterialID;
	}

	public int getReviewComments() {
		return ReviewComments;
	}

	public void setReviewComments(int reviewComments) {
		ReviewComments = reviewComments;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public Double getMaterialNumber() {
		return MaterialNumber;
	}

	public int getUnitID() {
		return UnitID;
	}

	public String getUnitText() {
		return UnitText;
	}

	public int getLevel() {
		return Level;
	}

	public String getLevelName() {
		return LevelName;
	}

	public int getResponsibility() {
		return Responsibility;
	}

	public int getSapType() {
		return SapType;
	}

	public String getSapTypeName() {
		return SapTypeName;
	}

	public void setSFCBOMTaskID(int sFCBOMTaskID) {
		SFCBOMTaskID = sFCBOMTaskID;
	}

	public void setBOMID(int bOMID) {
		BOMID = bOMID;
	}

	public void setBOMItemID(int bOMItemID) {
		BOMItemID = bOMItemID;
	}

	public void setMaterialID(int materialID) {
		MaterialID = materialID;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public void setMaterialNumber(Double materialNumber) {
		MaterialNumber = materialNumber;
	}

	public void setUnitID(int unitID) {
		UnitID = unitID;
	}

	public void setUnitText(String unitText) {
		UnitText = unitText;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public void setLevelName(String levelName) {
		LevelName = levelName;
	}

	public void setResponsibility(int responsibility) {
		Responsibility = responsibility;
	}

	public void setSapType(int sapType) {
		SapType = sapType;
	}

	public void setSapTypeName(String sapTypeName) {
		SapTypeName = sapTypeName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStatus() {
		return Status;
	}

	public String getRemark() {
		return Remark;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public String getOperator() {
		return Operator;
	}

	public Calendar getOperateTime() {
		return OperateTime;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public void setOperateTime(Calendar operateTime) {
		OperateTime = operateTime;
	}

	public String getShiftID() {
		return ShiftID;
	}

	public void setShiftID(String shiftID) {
		ShiftID = shiftID;
	}
}
