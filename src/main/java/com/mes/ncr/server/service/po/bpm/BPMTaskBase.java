package com.mes.ncr.server.service.po.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.mesenum.BPMEventModule;

/**
 * 流程业务基础表
 * 
 * @author ShrisJava
 *
 */
public class BPMTaskBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	public int ID = 0;

	/**
	 * 单据编号
	 */
	public String Code = "";

	/**
	 * 流程枚举 必须使用
	 */
	public int FlowType = BPMEventModule.Default.getValue();

	/**
	 * 流程实例ID
	 */
	public int FlowID = 0;

	/**
	 * 发起人
	 */
	public int UpFlowID = 0;

	/**
	 * 处理人ID
	 */
	public List<Integer> FollowerID = new ArrayList<Integer>();

	/**
	 * 发起人名称
	 */
	public String UpFlowName = "";

	/**
	 * 处理人名称
	 */
	public String FollowerName = "";

	/**
	 * 任务状态
	 */
	public int Status = 0;

	/**
	 * 无枚举，根据流程定义确定状态名称
	 */
	public String StatusText = "";

	public Calendar CreateTime = Calendar.getInstance();

	public Calendar SubmitTime = Calendar.getInstance();

	public int StepID = 0;

	public int getFlowID() {
		return FlowID;
	}

	public void setFlowID(int flowID) {
		FlowID = flowID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getFlowType() {
		return FlowType;
	}

	public void setFlowType(int flowType) {
		FlowType = flowType;
	}

	public BPMTaskBase() {
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getUpFlowID() {
		return UpFlowID;
	}

	public void setUpFlowID(int upFlowID) {
		UpFlowID = upFlowID;
	}

	public String getUpFlowName() {
		return UpFlowName;
	}

	public void setUpFlowName(String upFlowName) {
		UpFlowName = upFlowName;
	}

	public String getFollowerName() {
		return FollowerName;
	}

	public void setFollowerName(String followerName) {
		FollowerName = followerName;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
	}

	public List<Integer> getFollowerID() {
		return FollowerID;
	}

	public void setFollowerID(List<Integer> followerID) {
		FollowerID = followerID;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

}
