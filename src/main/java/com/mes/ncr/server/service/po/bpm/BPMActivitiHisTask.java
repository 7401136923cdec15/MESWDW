package com.mes.ncr.server.service.po.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List; 

//历史任务记录
public class BPMActivitiHisTask  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String ID = "";

	public String Name = "";

	public Calendar StartTime = Calendar.getInstance();

	public String ProcessDefinitionId = "";

	public String ActivitiID = "";

	public String ProcessInstanceId = "";

	public String ExecutionId = "";

	public String Description = "";

	public String Assignee = "";// 执行人

	public String Owner = "";// 委托人

	public Calendar dueCalendar = Calendar.getInstance(); // 到期时间

	public Calendar EndTime = Calendar.getInstance();

	public int Status = 0;

	public String deleteReason = "";// 删除原因

	public String BusinessKey = "";

	public String ProcessDefinitionName = "";

	public String TaskType = "";

	public List<BPMActivitiHisTaskVarinst> HisTaskVarinstList = new ArrayList<BPMActivitiHisTaskVarinst>();

	public List<BPMOperationStep> OperationStep = new ArrayList<BPMOperationStep>(); // 下一步操作

	public int CreateID = 0;

	public String getBusinessKey() {
		return BusinessKey;
	}

	public void setBusinessKey(String businessKey) {
		BusinessKey = businessKey;
	}

	public String getTaskType() {
		return TaskType;
	}

	public void setTaskType(String taskType) {
		TaskType = taskType;
	}

	public String getProcessDefinitionName() {
		return ProcessDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		ProcessDefinitionName = processDefinitionName;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String wDeleteReason) {
		deleteReason = wDeleteReason;
	}

 

	public List<BPMActivitiHisTaskVarinst> getHisTaskVarinstList() {
		return HisTaskVarinstList;
	}

	public Calendar getDueCalendar() {
		return dueCalendar;
	}

	public void setDueCalendar(Calendar dueCalendar) {
		this.dueCalendar = dueCalendar;
	}

	public List<BPMOperationStep> getOperationStep() {
		return OperationStep;
	}

	public void setOperationStep(List<BPMOperationStep> operationStep) {
		OperationStep = operationStep;
	}

	public void setHisTaskVarinstList(List<BPMActivitiHisTaskVarinst> hisTaskVarinstList) {
		HisTaskVarinstList = hisTaskVarinstList;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int wStatus) {
		Status = wStatus;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String wOwner) {
		Owner = wOwner;
	}

	public String getProcessDefinitionId() {
		return ProcessDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		ProcessDefinitionId = processDefinitionId;
	}

	public String getActivitiID() {
		return ActivitiID;
	}

	public void setActivitiID(String activitiID) {
		ActivitiID = activitiID;
	}

	public String getProcessInstanceId() {
		return ProcessInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		ProcessInstanceId = processInstanceId;
	}

	public String getExecutionId() {
		return ExecutionId;
	}

	public void setExecutionId(String executionId) {
		ExecutionId = executionId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getAssignee() {
		return Assignee;
	}

	public void setAssignee(String assignee) {
		Assignee = assignee;
	}

	public Calendar getStartTime() {
		return StartTime;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar EndTime) {
		this.EndTime = EndTime;
	}

	public BPMActivitiHisTask() {

	}
}
