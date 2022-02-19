package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;

/**
 * 返修表内容
 * 
 * @author ShrisJava
 *
 */
public class RROTableBody implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 异常状态描述
	 */
	public String Describe = "";
	/**
	 * 责任车间（工区）
	 */
	public String WorkAreaName = "";
	/**
	 * 责任班组
	 */
	public String TeamName = "";
	/**
	 * 工序
	 */
	public String ProcessName = "";
	/**
	 * 责任人
	 */
	public String PersonLiable = "";
	/**
	 * 返修签字
	 */
	public String Signature = "";
	/**
	 * 检查结果1:合格2：不合格
	 */
	public int Result = 0;
	/**
	 * 检查员（签名）
	 */
	public String InspectorName = "";
	/**
	 * 日期
	 */
	public String Date = "";
	/**
	 * 备注
	 */
	public String Remark = "";
	
	
	public String getDescribe() {
		return Describe;
	}
	public void setDescribe(String describe) {
		Describe = describe;
	}
	public String getWorkAreaName() {
		return WorkAreaName;
	}
	public void setWorkAreaName(String workAreaName) {
		WorkAreaName = workAreaName;
	}
	public String getTreamName() {
		return TeamName;
	}
	public void setTreamName(String teamName) {
		TeamName = teamName;
	}
	public String getProcessName() {
		return ProcessName;
	}
	public void setProcessName(String processName) {
		ProcessName = processName;
	}
	public String getPersonLiable() {
		return PersonLiable;
	}
	public void setPersonLiable(String personLiable) {
		PersonLiable = personLiable;
	}
	public String getSignature() {
		return Signature;
	}
	public void setSignature(String signature) {
		Signature = signature;
	}
	public int getResult() {
		return Result;
	}
	public void setResult(int result) {
		Result = result;
	}
	public String getInspectorName() {
		return InspectorName;
	}
	public void setInspectorName(String inspectorName) {
		InspectorName = inspectorName;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
}
