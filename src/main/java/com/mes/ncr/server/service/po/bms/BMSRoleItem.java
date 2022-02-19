package com.mes.ncr.server.service.po.bms;

import java.io.Serializable;

public class BMSRoleItem implements Serializable {
	private static final long serialVersionUID = 1L;

	public int UserID = 0;

	public int RoleID = 0;

	public int FunctionID = 0;

	public String Text = "";

	public int TypeID = 0;
	
	

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public int getRoleID() {
		return RoleID;
	}

	public void setRoleID(int roleID) {
		RoleID = roleID;
	}

	public int getFunctionID() {
		return FunctionID;
	}

	public void setFunctionID(int functionID) {
		FunctionID = functionID;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public int getTypeID() {
		return TypeID;
	}

	public void setTypeID(int typeID) {
		TypeID = typeID;
	}

	public BMSRoleItem() {
		this.RoleID = 0;
		this.FunctionID = 0;
		this.Text = "";
		this.TypeID = 0;
	}
}
