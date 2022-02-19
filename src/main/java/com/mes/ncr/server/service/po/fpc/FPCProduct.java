package com.mes.ncr.server.service.po.fpc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 车型结构
 */
public class FPCProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public String ProductName = "";

	public String ProductNo = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID = 0;

	public Calendar EditTime = Calendar.getInstance();

	public int EditorID = 0;

	public int Status = 0; // 审批状态

	public Calendar AuditTime = Calendar.getInstance();

	public int AuditorID = 0;

	public int BusinessUnitID = 0; // 事业部

	public int ProductTypeID = 0; // 产品类型

	// 转运部件类型
	public int TransportType = 0;

	public Double Length = 0.0;

	public String BusinessUnit = "";

	public String ProductType = "";

	public String Creator = "";

	public String Auditor = "";

	public String Editor = "";

	public String StatusText = ""; // 审批状态

	public int Active = 0; // 状态

	public int ERPID = 0; // ERP关联ID

	public String ProductCode = ""; // 物料编码

	public String PrevProductNo = "";

	/**
	 * 车节数
	 */
	public int ProductCount = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public int getBusinessUnitID() {
		return BusinessUnitID;
	}

	public void setBusinessUnitID(int businessUnitID) {
		BusinessUnitID = businessUnitID;
	}

	public int getProductTypeID() {
		return ProductTypeID;
	}

	public void setProductTypeID(int productTypeID) {
		ProductTypeID = productTypeID;
	}

	public String getBusinessUnit() {
		return BusinessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		BusinessUnit = businessUnit;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
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

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public int getERPID() {
		return ERPID;
	}

	public void setERPID(int eRPID) {
		ERPID = eRPID;
	}

	public String getProductCode() {
		return ProductCode;
	}

	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}

	public FPCProduct() {
		this.ProductName = "";
		this.ProductNo = "";
		this.BusinessUnit = "";
		this.ProductType = "";
		this.Editor = "";
		this.Creator = "";
		this.Auditor = "";
		this.CreateTime = Calendar.getInstance();
		this.AuditTime = Calendar.getInstance();
		this.EditTime = Calendar.getInstance();
		this.StatusText = "";
	}

	public FPCProduct Clone() {
		FPCProduct wItem = new FPCProduct();
		wItem.ID = this.ID;
		wItem.ProductName = this.ProductName;
		wItem.ProductNo = this.ProductNo;

		wItem.CreatorID = this.CreatorID;
		wItem.EditorID = this.EditorID;
		wItem.AuditorID = this.AuditorID;
		wItem.Status = this.Status;
		wItem.Active = this.Active;
		wItem.Active = this.Active;
		wItem.CreateTime = this.CreateTime;
		wItem.AuditTime = this.AuditTime;
		wItem.EditTime = this.EditTime;

		wItem.BusinessUnitID = this.BusinessUnitID;
		wItem.ProductTypeID = this.ProductTypeID;

		wItem.BusinessUnit = this.BusinessUnit;
		wItem.ProductType = this.ProductType;
		wItem.Creator = this.Creator;
		wItem.Editor = this.Editor;
		wItem.Auditor = this.Auditor;
		wItem.Status = this.Status;
		return wItem;
	}

	public int getTransportType() {
		return TransportType;
	}

	public void setTransportType(int transportType) {
		TransportType = transportType;
	}

	public Double getLength() {
		return Length;
	}

	public void setLength(Double length) {
		Length = length;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getPrevProductNo() {
		return PrevProductNo;
	}

	public void setPrevProductNo(String prevProductNo) {
		PrevProductNo = prevProductNo;
	}

	public int getProductCount() {
		return ProductCount;
	}

	public void setProductCount(int productCount) {
		ProductCount = productCount;
	}
}
