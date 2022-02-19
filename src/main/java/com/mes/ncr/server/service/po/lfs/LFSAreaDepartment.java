package com.mes.ncr.server.service.po.lfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 工区班组表(班组设置，可设置班组名称、班组人员、班组长)【班组人员表】
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-29 12:46:28
 * @LastEditTime 2020年1月6日11:49:14
 * 
 */
public class LFSAreaDepartment implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键 班组ID
	 */
	public int ID;

	/**
	 * 班组名称
	 */
	public String Name = "";
	/**
	 * 工区ID
	 */
	public int WorkAreaID;
	/**
	 * 工区名称
	 */
	public String WorkArea = "";

	/**
	 * 班组长
	 */
	public int LeaderID;

	/**
	 * 班组人员
	 */
	public List<Integer> ClassmateIDList = new ArrayList<>();

	/**
	 * 创建信息
	 */
	public int CreateID;
	public String Creator = "";
	public Calendar CreateTime = Calendar.getInstance();

	/**
	 * 编辑信息
	 */
	public int EditID;
	public String Editor = "";
	public Calendar EditTime = Calendar.getInstance();

	/**
	 * 激活关闭
	 */
	public int Active;

	public LFSAreaDepartment() {
	}

	public String getWorkArea() {
		return WorkArea;
	}

	public void setWorkArea(String workArea) {
		WorkArea = workArea;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getWorkAreaID() {
		return WorkAreaID;
	}

	public void setWorkAreaID(int workAreaID) {
		WorkAreaID = workAreaID;
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

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

//	public int getDepartmentID() {
//		return DepartmentID;
//	}
//
//	public void setDepartmentID(int departmentID) {
//		DepartmentID = departmentID;
//	}

//	public String getDepartment() {
//		return ClassName;
//	}
//
//	public void setDepartment(String departmentName) {
//		ClassName = departmentName;
//	}

	public int getEditID() {
		return EditID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getLeaderID() {
		return LeaderID;
	}

	public void setLeaderID(int leaderID) {
		LeaderID = leaderID;
	}

	public List<Integer> getClassmateIDList() {
		return ClassmateIDList;
	}

	public void setClassmateIDList(List<Integer> classmateIDList) {
		ClassmateIDList = classmateIDList;
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
}
