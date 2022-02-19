package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 审批部门
 * 
 * @author ShrisJava
 *
 */
public class NCRAuditDepartment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public long ID;
	/**
	 * NCR单ID
	 */
	public long TaskID;
	/**
	 * 部门ID
	 */
	public int DepartmentID;
	/**
	 * 部门名称
	 */
	public String Department;
	/**
	 * 审批人ID
	 */
	public int AuditorID;
	/**
	 * 审批人名称
	 */
	public String Auditor;
	/**
	 * 审批结果
	 */
	public int AuditResult;
	/**
	 * 图片路径
	 */
	public List<String> FilePath = new ArrayList<String>();
	/**
	 * 审批意见
	 */
	public String AuditOpinion = "";
	/**
	 * 审批时刻
	 */
	public Calendar AuditTime = Calendar.getInstance();

	public NCRAuditDepartment() {
		AuditTime.set(2000, 0, 1);
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getTaskID() {
		return TaskID;
	}

	public void setTaskID(long taskID) {
		TaskID = taskID;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setDepartmentID(int departmentID) {
		DepartmentID = departmentID;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public int getAuditResult() {
		return AuditResult;
	}

	public void setAuditResult(int auditResult) {
		AuditResult = auditResult;
	}
	
	public List<String> getFilePath() {
		return FilePath;
	}

	public void setFilePath(List<String> filePath) {
		FilePath = filePath;
	}

	public String getAuditOpinion() {
		return AuditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		AuditOpinion = auditOpinion;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}
}
