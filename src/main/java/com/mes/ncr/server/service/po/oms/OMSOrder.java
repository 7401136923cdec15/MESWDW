package com.mes.ncr.server.service.po.oms;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 订单
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月27日12:54:19
 * @LastEditTime 2020-6-4 21:34:55
 *
 */
public class OMSOrder implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一编号
	 */
	public int ID;
	/**
	 * 生产命令票ID
	 */
	public int CommandID;
	/**
	 * ERPID
	 */
	public int ERPID;
	
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * 修程ID
	 */
	public int LineID;
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 车型ID
	 */
	public int ProductID;
	/**
	 * 车型编码
	 */
	public String ProductNo = "";
	/**
	 * 局段ID
	 */
	public int BureauSectionID;
	/**
	 * 局段
	 */
	public String BureauSection = "";

	/**
	 * 车号
	 */
	public String PartNo = "";
	
	/**
	 * BOM编号
	 */
	public String BOMNo = "";
	/**
	 * 优先级
	 */
	public int Priority = 0;
	/**
	 * 台车订单状态
	 */
	public int Status;
	/**
	 * 计划进厂日期
	 */
	public Calendar PlanReceiveDate = Calendar.getInstance();
	/**
	 * 实际进厂日期
	 */
	public Calendar RealReceiveDate = Calendar.getInstance();
	/**
	 * 预计完工日期
	 */
	public Calendar PlanFinishDate = Calendar.getInstance();
	/**
	 * 实际开工日期
	 */
	public Calendar RealStartDate = Calendar.getInstance();
	/**
	 * 实际完工日期
	 */
	public Calendar RealFinishDate = Calendar.getInstance();
	/**
	 * 发车日期 （交车日期）
	 */
	public Calendar RealSendDate = Calendar.getInstance();
	/**
	 * 备注信息
	 */
	public String Remark = "";
	/**
	 * 创建人ID
	 */
	public int CreateID;
	/**
	 * 创建人名称
	 */
	public String Creator = "";
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 编辑人ID
	 */
	public int EditID;
	/**
	 * 编辑人名称
	 */
	public String Editor = "";
	/**
	 * 编辑时刻
	 */
	public Calendar EditTime = Calendar.getInstance();
	/**
	 * 审核人ID
	 */
	public int AuditorID = 0;
	/**
	 * 审核人Name
	 */
	public String Auditor = "";
	/**
	 * 审核时刻
	 */
	public Calendar AuditTime = Calendar.getInstance();
	/**
	 * 订单有效状态
	 */
	public int Active = 0;
	/**
	 * 工艺路线ID
	 */
	public int RouteID = 0;

	// Command表属性
	/**
	 * WBS编号
	 */
	public String WBSNo = "";
	public int CustomerID = 0;
	public String Customer = "";
	public String ContactCode = "";
	public String No = "";
	public int LinkManID;
	public String LinkMan = "";
	public int FactoryID;
	public String Factory = "";
	public int BusinessUnitID;
	public String BusinessUnit = "";
	public int FQTYPlan;
	public int FQTYActual;

	public OMSOrder() {
		PlanReceiveDate.set(2000, 1, 1);
		RealReceiveDate.set(2000, 1, 1);
		PlanFinishDate.set(2000, 1, 1);
		RealStartDate.set(2000, 1, 1);
		RealFinishDate.set(2000, 1, 1);
		RealSendDate.set(2000, 1, 1);
		CreateTime.set(2000, 1, 1);
		EditTime.set(2000, 1, 1);
		AuditTime.set(2000, 1, 1);
	}

	public int getRouteID() {
		return RouteID;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}

	public int getCommandID() {
		return CommandID;
	}

	public void setCommandID(int commandID) {
		CommandID = commandID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getERPID() {
		return ERPID;
	}

	public void setERPID(int eRPID) {
		ERPID = eRPID;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getBureauSection() {
		return BureauSection;
	}

	public void setBureauSection(String bureauSection) {
		BureauSection = bureauSection;
	}

	public String getWBSNo() {
		return WBSNo;
	}

	public void setWBSNo(String wBSNo) {
		WBSNo = wBSNo;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public String getBOMNo() {
		return BOMNo;
	}

	public void setBOMNo(String bOMNo) {
		BOMNo = bOMNo;
	}

	public int getPriority() {
		return Priority;
	}

	public void setPriority(int priority) {
		Priority = priority;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public Calendar getPlanReceiveDate() {
		return PlanReceiveDate;
	}

	public void setPlanReceiveDate(Calendar planReceiveDate) {
		PlanReceiveDate = planReceiveDate;
	}

	public Calendar getRealReceiveDate() {
		return RealReceiveDate;
	}

	public void setRealReceiveDate(Calendar realReceiveDate) {
		RealReceiveDate = realReceiveDate;
	}

	public Calendar getPlanFinishDate() {
		return PlanFinishDate;
	}

	public void setPlanFinishDate(Calendar planFinishDate) {
		PlanFinishDate = planFinishDate;
	}

	public Calendar getRealStartDate() {
		return RealStartDate;
	}

	public void setRealStartDate(Calendar realStartDate) {
		RealStartDate = realStartDate;
	}

	public Calendar getRealFinishDate() {
		return RealFinishDate;
	}

	public void setRealFinishDate(Calendar realFinishDate) {
		RealFinishDate = realFinishDate;
	}

	public Calendar getRealSendDate() {
		return RealSendDate;
	}

	public void setRealSendDate(Calendar realSendDate) {
		RealSendDate = realSendDate;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getEditID() {
		return EditID;
	}

	public void setEditID(int editID) {
		EditID = editID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
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

	public int getBureauSectionID() {
		return BureauSectionID;
	}

	public void setBureauSectionID(int bureauSectionID) {
		BureauSectionID = bureauSectionID;
	}
}
