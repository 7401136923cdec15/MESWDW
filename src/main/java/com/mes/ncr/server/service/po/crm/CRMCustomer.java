package com.mes.ncr.server.service.po.crm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CRMCustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public String CustomerName = "";

	public String CustomerCode = "";

	public String TaxCode = "";

	public int CountryID = 0;

	public int ProvinceID = 0;

	public int CityID = 0;

	public String Country = "";

	public String Province = "";

	public String City = "";

	public String Address = "";

	public int Type = 0; // 行业

	public String TypeText = "";

	public int Grade = 0; // 等级

	public String GradeText = "";

	public String Creator = "";

	public String Auditor = "";

	public Calendar CreateTime = Calendar.getInstance();

	public Calendar AuditTime = Calendar.getInstance();

	public int Active = 0;

	public int Status = 0;

	public List<CRMLinkMan> LinkManList = new ArrayList<>();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}

	public String getTaxCode() {
		return TaxCode;
	}

	public void setTaxCode(String taxCode) {
		TaxCode = taxCode;
	}

	public int getCountryID() {
		return CountryID;
	}

	public void setCountryID(int countryID) {
		CountryID = countryID;
	}

	public int getProvinceID() {
		return ProvinceID;
	}

	public void setProvinceID(int provinceID) {
		ProvinceID = provinceID;
	}

	public int getCityID() {
		return CityID;
	}

	public void setCityID(int cityID) {
		CityID = cityID;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getProvince() {
		return Province;
	}

	public void setProvince(String province) {
		Province = province;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getTypeText() {
		return TypeText;
	}

	public void setTypeText(String typeText) {
		TypeText = typeText;
	}

	public int getGrade() {
		return Grade;
	}

	public void setGrade(int grade) {
		Grade = grade;
	}

	public String getGradeText() {
		return GradeText;
	}

	public void setGradeText(String gradeText) {
		GradeText = gradeText;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public List<CRMLinkMan> getLinkManList() {
		return LinkManList;
	}

	public void setLinkManList(List<CRMLinkMan> linkManList) {
		LinkManList = linkManList;
	}

	public CRMCustomer() {
		this.CreateTime = Calendar.getInstance();
		this.AuditTime = Calendar.getInstance();
		this.LinkManList = new ArrayList<>();
	}
}
