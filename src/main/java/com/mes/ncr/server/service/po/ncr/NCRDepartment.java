package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;

/**
 * NCR人员选择
 * 
 * @author ShrisJava
 *
 */
public class NCRDepartment implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;
	/**
	 * 部门名称
	 */
	public String Name = "";
	/**
	 * 人员数量统计
	 */
	public int EmployeeCount;

	public NCRDepartment() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getEmployeeCount() {
		return EmployeeCount;
	}

	public void setEmployeeCount(int employeeCount) {
		EmployeeCount = employeeCount;
	}
}
