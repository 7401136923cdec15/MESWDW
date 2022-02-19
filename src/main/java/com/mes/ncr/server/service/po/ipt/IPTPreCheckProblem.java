package com.mes.ncr.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.po.bms.BMSDepartment;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;



/**
 * 预检问题项
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 14:27:10
 * @LastEditTime 2020-2-12 14:27:15
 *
 */
public class IPTPreCheckProblem extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
//	public int ID = 0;
	/**
	 * 预检单ID
	 */
	public int IPTPreCheckTaskID = 0;
	/**
	 * 预检标准项ID
	 */
	public int IPTItemID = 0;
	/**
	 * 预检标准项名称
	 */
	public String IPTItemName = "";
	/**
	 * 预检问题解决方案ID
	 */
	public int SolveID = 0;
	/**
	 * 问题简述
	 */
	public String Description = "";
	/**
	 * 问题描述详情
	 */
	public String Details = "";
	/**
	 * 问题描述图片列表
	 */
	public List<String> ImageList = new ArrayList<String>();
	/**
	 * 问题描述视频列表
	 */
	public List<String> VideoList = new ArrayList<String>();
	/**
	 * 解决方案列表
	 */
	public List<IPTSOP> IPTSOPList = new ArrayList<IPTSOP>();
	/**
	 * 解决方案完整描述
	 */
	public String FullDescribe = "";
	/**
	 * 预检人
	 */
	public int PreCheckID = 0;
	public String PreCheckName = "";
	/**
	 * 车型
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String CarNumber = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";
	/**
	 * 局段
	 */
	public int CustomID = 0;
	public String CustomName = "";

	/**
	 * 预检时间
	 */
	public Calendar PreCheckTime = Calendar.getInstance();

	// 现场工艺节点
	public int CarftID = 0;// 分配人ID
	public String CraftName = "";// 分配人名称
	public Calendar CarftTime = Calendar.getInstance();// 分配时间
	public List<BMSDepartment> DepartmentList = new ArrayList<BMSDepartment>();// 分配的部门列表
	public List<BMSEmployee> EmployeeList = new ArrayList<BMSEmployee>();// 通知人员列表
	public BMSEmployee Manager = new BMSEmployee();// 部门负责人
	// 部门负责人节点
	/**
	 * 执行工位
	 */
	public int DoStationID = 0;
	public String DoStationName = "";

	public int DoDepartmentID = 0;// 执行部门ID
	public String DoDepartmentName = "";// 执行部门名称
	public int DepartmentIssueID = 0;// 部门下发人ID
	public String DepartmentIssueName = "";// 部门下发人名称
	public Calendar DepartmentIssueTime = Calendar.getInstance();// 下发时刻
	public int DoClassID = 0;// 执行班组ID
	public String DoClassName = "";// 执行班组名称

	// 班组长节点
	/**
	 * 执行工序
	 */
	public int DoPartPointID = 0;
	public String DoPartPointName = "";

	public int ClassIssueID = 0;// 班组下发人ID
	public String ClassIssueName = "";// 班组下发人名称
	public Calendar ClassIssueTime = Calendar.getInstance();// 班组下发时刻
	public int DoPersonID = 0;// 执行人ID
	public String DoPersonName = "";// 执行人名称

	/**
	 * 预检问题项状态
	 */
//	public int Status = 0;

	public IPTPreCheckProblem() {
		PreCheckTime.set(2000, 0, 1);
		CarftTime.set(2000, 0, 1);
		DepartmentIssueTime.set(2000, 0, 1);
		ClassIssueTime.set(2000, 0, 1);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getIPTPreCheckTaskID() {
		return IPTPreCheckTaskID;
	}

	public void setIPTPreCheckTaskID(int iPTPreCheckTaskID) {
		IPTPreCheckTaskID = iPTPreCheckTaskID;
	}

	public int getIPTItemID() {
		return IPTItemID;
	}

	public void setIPTItemID(int iPTItemID) {
		IPTItemID = iPTItemID;
	}

	public String getIPTItemName() {
		return IPTItemName;
	}

	public void setIPTItemName(String iPTItemName) {
		IPTItemName = iPTItemName;
	}

	public int getSolveID() {
		return SolveID;
	}

	public void setSolveID(int solveID) {
		SolveID = solveID;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	public List<String> getImageList() {
		return ImageList;
	}

	public void setImageList(List<String> imageList) {
		ImageList = imageList;
	}

	public List<String> getVideoList() {
		return VideoList;
	}

	public void setVideoList(List<String> videoList) {
		VideoList = videoList;
	}

	public List<IPTSOP> getIPTSOPList() {
		return IPTSOPList;
	}

	public void setIPTSOPList(List<IPTSOP> iPTSOPList) {
		IPTSOPList = iPTSOPList;
	}

	public String getFullDescribe() {
		return FullDescribe;
	}

	public void setFullDescribe(String fullDescribe) {
		FullDescribe = fullDescribe;
	}

	public int getPreCheckID() {
		return PreCheckID;
	}

	public void setPreCheckID(int preCheckID) {
		PreCheckID = preCheckID;
	}

	public String getPreCheckName() {
		return PreCheckName;
	}

	public void setPreCheckName(String preCheckName) {
		PreCheckName = preCheckName;
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

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
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

	public int getCustomID() {
		return CustomID;
	}

	public void setCustomID(int customID) {
		CustomID = customID;
	}

	public String getCustomName() {
		return CustomName;
	}

	public void setCustomName(String customName) {
		CustomName = customName;
	}

	public int getDoStationID() {
		return DoStationID;
	}

	public void setDoStationID(int doStationID) {
		DoStationID = doStationID;
	}

	public String getDoStationName() {
		return DoStationName;
	}

	public void setDoStationName(String doStationName) {
		DoStationName = doStationName;
	}

	public int getDoPartPointID() {
		return DoPartPointID;
	}

	public void setDoPartPointID(int doPartPointID) {
		DoPartPointID = doPartPointID;
	}

	public String getDoPartPointName() {
		return DoPartPointName;
	}

	public void setDoPartPointName(String doPartPointName) {
		DoPartPointName = doPartPointName;
	}

	public Calendar getPreCheckTime() {
		return PreCheckTime;
	}

	public void setPreCheckTime(Calendar preCheckTime) {
		PreCheckTime = preCheckTime;
	}

	public int getCarftID() {
		return CarftID;
	}

	public void setCarftID(int carftID) {
		CarftID = carftID;
	}

	public String getCraftName() {
		return CraftName;
	}

	public void setCraftName(String craftName) {
		CraftName = craftName;
	}

	public Calendar getCarftTime() {
		return CarftTime;
	}

	public void setCarftTime(Calendar carftTime) {
		CarftTime = carftTime;
	}

	public List<BMSDepartment> getDepartmentList() {
		return DepartmentList;
	}

	public void setDepartmentList(List<BMSDepartment> departmentList) {
		DepartmentList = departmentList;
	}

	public List<BMSEmployee> getEmployeeList() {
		return EmployeeList;
	}

	public void setEmployeeList(List<BMSEmployee> employeeList) {
		EmployeeList = employeeList;
	}

	public int getDoDepartmentID() {
		return DoDepartmentID;
	}

	public void setDoDepartmentID(int doDepartmentID) {
		DoDepartmentID = doDepartmentID;
	}

	public String getDoDepartmentName() {
		return DoDepartmentName;
	}

	public void setDoDepartmentName(String doDepartmentName) {
		DoDepartmentName = doDepartmentName;
	}

	public int getDepartmentIssueID() {
		return DepartmentIssueID;
	}

	public void setDepartmentIssueID(int departmentIssueID) {
		DepartmentIssueID = departmentIssueID;
	}

	public String getDepartmentIssueName() {
		return DepartmentIssueName;
	}

	public void setDepartmentIssueName(String departmentIssueName) {
		DepartmentIssueName = departmentIssueName;
	}

	public Calendar getDepartmentIssueTime() {
		return DepartmentIssueTime;
	}

	public void setDepartmentIssueTime(Calendar departmentIssueTime) {
		DepartmentIssueTime = departmentIssueTime;
	}

	public int getDoClassID() {
		return DoClassID;
	}

	public void setDoClassID(int doClassID) {
		DoClassID = doClassID;
	}

	public String getDoClassName() {
		return DoClassName;
	}

	public void setDoClassName(String doClassName) {
		DoClassName = doClassName;
	}

	public int getClassIssueID() {
		return ClassIssueID;
	}

	public void setClassIssueID(int classIssueID) {
		ClassIssueID = classIssueID;
	}

	public String getClassIssueName() {
		return ClassIssueName;
	}

	public void setClassIssueName(String classIssueName) {
		ClassIssueName = classIssueName;
	}

	public Calendar getClassIssueTime() {
		return ClassIssueTime;
	}

	public void setClassIssueTime(Calendar classIssueTime) {
		ClassIssueTime = classIssueTime;
	}

	public int getDoPersonID() {
		return DoPersonID;
	}

	public void setDoPersonID(int doPersonID) {
		DoPersonID = doPersonID;
	}

	public String getDoPersonName() {
		return DoPersonName;
	}

	public void setDoPersonName(String doPersonName) {
		DoPersonName = doPersonName;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
}
