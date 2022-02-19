package com.mes.ncr.server.service.po.bms;

import java.io.Serializable;
import java.util.Calendar;

public class BMSEmployee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int ID;

	public String Name = "";

	public String LoginName = "";

	public String Password = "";

	public int DepartmentID;

	public String Department = "";

	public int Active = 1;

	public int Grad = 0;

	public int Manager;

	public String Operator = "";

	public String Phone = "";

	public String Email = "";

	public Calendar CreateDate = Calendar.getInstance();

	public int Position;

	public String WeiXin = "";

	public long PhoneMAC;

	public int Online;

	public int OnShift;

	public Calendar OnLineTime = Calendar.getInstance();

	public Calendar DepartureDate = Calendar.getInstance();

	public Calendar LastOnLineTime = Calendar.getInstance();

	public int CompanyID;

	public String LoginID;

	public int DutyID;
	
	//public List<Integer> RoleIDList=new ArrayList<>();

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

	public String getLoginName() {
		return LoginName;
	}

	public void setLoginName(String loginName) {
		LoginName = loginName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
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

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getGrad() {
		return Grad;
	}

	public void setGrad(int grad) {
		Grad = grad;
	}

	public int getManager() {
		return Manager;
	}

	public void setManager(int manager) {
		Manager = manager;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public Calendar getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(Calendar createDate) {
		CreateDate = createDate;
	}

	public int getPosition() {
		return Position;
	}

	public void setPosition(int position) {
		Position = position;
	}

	public String getWeiXin() {
		return WeiXin;
	}

	public void setWeiXin(String weiXin) {
		WeiXin = weiXin;
	}

	public long getPhoneMAC() {
		return PhoneMAC;
	}

	public void setPhoneMAC(long phoneMAC) {
		PhoneMAC = phoneMAC;
	}

	public int getOnline() {
		return Online;
	}

	public void setOnline(int online) {
		Online = online;
	}

	public int getOnShift() {
		return OnShift;
	}

	public void setOnShift(int onShift) {
		OnShift = onShift;
	}

	public Calendar getOnLineTime() {
		return OnLineTime;
	}

	public void setOnLineTime(Calendar onLineTime) {
		OnLineTime = onLineTime;
	}

	public Calendar getDepartureDate() {
		return DepartureDate;
	}

	public void setDepartureDate(Calendar departureDate) {
		DepartureDate = departureDate;
	}

	public Calendar getLastOnLineTime() {
		return LastOnLineTime;
	}

	public void setLastOnLineTime(Calendar lastOnLineTime) {
		LastOnLineTime = lastOnLineTime;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}

	public String getLoginID() {
		return LoginID;
	}

	public void setLoginID(String loginID) {
		LoginID = loginID;
	}

	public int getDutyID() {
		return DutyID;
	}

	public void setDutyID(int dutyID) {
		DutyID = dutyID;
	}

	public BMSEmployee() {
		this.ID = -1;
		this.CreateDate.set(2000, 0, 1);
		this.OnLineTime.set(2000, 0, 1);
		this.DepartureDate.set(2000, 0, 1);
		this.LastOnLineTime.set(2000, 0, 1);
	}
}
