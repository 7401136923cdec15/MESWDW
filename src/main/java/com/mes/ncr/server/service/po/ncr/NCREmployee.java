package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.bms.BMSEmployee;

/**
 * NCR人员选择
 * 
 * @author ShrisJava
 *
 */
public class NCREmployee implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 人员列表
	 */
	public List<BMSEmployee> EmployeeList = new ArrayList<BMSEmployee>();
	/**
	 * 部门列表
	 */
	public List<NCRDepartment> DepartmentList = new ArrayList<NCRDepartment>();

	public NCREmployee() {
	}

	public List<BMSEmployee> getEmployeeList() {
		return EmployeeList;
	}

	public void setEmployeeList(List<BMSEmployee> employeeList) {
		EmployeeList = employeeList;
	}

	public List<NCRDepartment> getDepartmentList() {
		return DepartmentList;
	}

	public void setDepartmentList(List<NCRDepartment> departmentList) {
		DepartmentList = departmentList;
	}
}
