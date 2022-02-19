package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;


/**
 *人员工区
 * 
 * @author ShrisJava
 *
 */
public class UserWorkArea implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 人员ID
	 */
	public int UserID;
	/**
	 *名称
	 */
	public String Name = "";
	/**
	 * 工区ID
	 */
	public int WorkID = 0;
	
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getWorkID() {
		return WorkID;
	}
	public void setWorkID(int workID) {
		WorkID = workID;
	}
}
