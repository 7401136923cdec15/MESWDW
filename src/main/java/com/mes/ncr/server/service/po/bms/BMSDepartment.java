package com.mes.ncr.server.service.po.bms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BMSDepartment implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public String Name = "";

	public int Active = 0;

	public int ParentID = 0;

	public List<BMSDepartment> SonList = new ArrayList<>();
	
	public int Type = 0;

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

	public int getActive() {
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

	public List<BMSDepartment> getSonList() {
		return SonList;
	}

	public void setSonList(List<BMSDepartment> sonList) {
		SonList = sonList;
	}

	public BMSDepartment() {
		this.ID = -1;
		this.Name = "";
		this.SonList = new ArrayList<>();
		this.Active = 0;
		this.ParentID = 0;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}
}
