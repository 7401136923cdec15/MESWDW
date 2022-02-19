package com.mes.ncr.server.service.po.fmc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 班组工位
 * @author PengYouWang
 * @CreateTime 2020-1-8 10:11:09
 * @LastEditTime 2020-1-8 10:11:14
 *
 */
public class FMCWorkCharge implements Serializable {
	private static final long serialVersionUID = 1L;

	public int Active = 0;
	public int ClassID = 0;
	public String ClassName = "";
	public Calendar CreateTime = Calendar.getInstance();
	public String Creator = "";
	public int CreatorID = 0;
	public Calendar EditTime = Calendar.getInstance();
	public String Editor = "";
	public int EditorID = 0;
	public int ID = 0;
	public int StationID = 0;
	public String StationName = "";

	public FMCWorkCharge() {
	}

	public FMCWorkCharge Clone() {
		FMCWorkCharge wItem = new FMCWorkCharge();

		wItem.Active = this.Active;
		wItem.ClassID = this.ClassID;
		wItem.ClassName = this.ClassName;
		wItem.CreateTime = this.CreateTime;
		wItem.Creator = this.Creator;
		wItem.CreatorID = this.CreatorID;
		wItem.EditTime = this.EditTime;
		wItem.Editor = this.Editor;
		wItem.EditorID = this.EditorID;
		wItem.ID = this.ID;
		wItem.StationID = this.StationID;
		wItem.StationName = this.StationName;

		return wItem;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getClassID() {
		return ClassID;
	}

	public void setClassID(int classID) {
		ClassID = classID;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
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

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}
}
