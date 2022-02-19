package com.mes.ncr.server.service.po.bms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BMSPosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int ID ;

    public String Name ="";

    public int Active=0 ;

    public int ParentID ;

    public int DepartmentID ;

    public int DutyID ;

    public int OperatorID ;

    public String Operator ="";

    public Calendar EditTime =Calendar.getInstance();

    public List<BMSPosition> SonList =new ArrayList<BMSPosition>();

	public BMSPosition() {
		EditTime.set(2000, 0, 1);
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

	public int isActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setDepartmentID(int departmentID) {
		DepartmentID = departmentID;
	}

	public int getDutyID() {
		return DutyID;
	}

	public void setDutyID(int dutyID) {
		DutyID = dutyID;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public List<BMSPosition> getSonList() {
		return SonList;
	}

	public void setSonList(List<BMSPosition> sonList) {
		SonList = sonList;
	}
}
