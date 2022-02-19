package com.mes.ncr.server.service.po.lfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 工区质检员数据结构【工区人员设置】
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-4-29 14:32:15
 * @LastEditTime 2020-4-29 14:32:19
 *
 */
public class LFSWorkAreaChecker implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;
	/**
	 * 工区ID
	 */
	public int WorkAreaID;
	/**
	 * 工区名称
	 */
	public String WorkArea = "";
	/**
	 * 工位检验员ID集合
	 */
	public List<Integer> CheckerIDList = new ArrayList<Integer>();
	/**
	 * 工位主管
	 */
	public List<Integer> LeaderIDList = new ArrayList<Integer>();
	/**
	 * 工位调度ID集合
	 */
	public List<Integer> ScheduleIDList = new ArrayList<Integer>();
	/**
	 * 编辑者ID
	 */
	public int CreateID;
	/**
	 * 编辑者
	 */
	public String Creator = "";
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();

	/**
	 * 激活关闭
	 */
	public int Active;

	public LFSWorkAreaChecker() {
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

	public List<Integer> getCheckerIDList() {
		return CheckerIDList;
	}

	public void setCheckerIDList(List<Integer> checkerIDList) {
		CheckerIDList = checkerIDList;
	}

	public List<Integer> getLeaderIDList() {
		return LeaderIDList;
	}

	public void setLeaderIDList(List<Integer> leaderIDList) {
		LeaderIDList = leaderIDList;
	}

	public List<Integer> getScheduleIDList() {
		return ScheduleIDList;
	}

	public void setScheduleIDList(List<Integer> scheduleIDList) {
		ScheduleIDList = scheduleIDList;
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
}
