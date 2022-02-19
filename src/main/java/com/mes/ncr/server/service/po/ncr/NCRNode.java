package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;

/**
 * NCR节点
 * 
 * @author ShrisJava
 *
 */
public class NCRNode implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 状态文本
	 */
	public String StatusText = "";
	/**
	 * 人员名称
	 */
	public String UserName = "";
	/**
	 * 部门名称
	 */
	public String DepartmentName = "";
	/**
	 * 编辑时刻文本
	 */
	public String EditTimeText = "";
	/**
	 * 是否完成
	 */
	public boolean IsFinish;
	/**
	 * 节点类型：1：开始；2：中间；3：结束
	 */
	public int NodeType;
	/**
	 * 备注(用于存放相关部门的信息)
	 */
	public String Remark = "";

	public NCRNode() {
	}

	public NCRNode(String statusText, String userName, String departmentName, String editTimeText, boolean isFinish,
			int nodeType, String wRemark) {
		super();
		StatusText = statusText;
		UserName = userName;
		DepartmentName = departmentName;
		EditTimeText = editTimeText;
		IsFinish = isFinish;
		NodeType = nodeType;
		Remark = wRemark;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public String getEditTimeText() {
		return EditTimeText;
	}

	public void setEditTimeText(String editTimeText) {
		EditTimeText = editTimeText;
	}

	public boolean isIsFinish() {
		return IsFinish;
	}

	public void setIsFinish(boolean isFinish) {
		IsFinish = isFinish;
	}

	public int getNodeType() {
		return NodeType;
	}

	public void setNodeType(int nodeType) {
		NodeType = nodeType;
	}
}
