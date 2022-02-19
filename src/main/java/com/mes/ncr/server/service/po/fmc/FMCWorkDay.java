package com.mes.ncr.server.service.po.fmc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FMCWorkDay implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int ID= 0;                //1--8;

    public String Name= "";         //班次名称

    public Calendar StartTime= Calendar.getInstance();  //开始时刻

    public int Minutes= 0;          //总时长;

    public int WorkMinutes= 0;     //工作时长;

    public int IdleMinutes= 0;      //休息时长;

    public Calendar EndTime= Calendar.getInstance();     //结束时刻

    public List<FMCShift> ShiftList= new ArrayList<>();

    public int Active=0;

    public int Status= 0;

    public Calendar CreateTime= Calendar.getInstance();

    public int CreatorID= 0;

    public Calendar EditTime= Calendar.getInstance();

    public int EditorID= 0;

    public Calendar AuditTime= Calendar.getInstance();

    public int AuditorID= 0;

    public int FactoryID= 0;

    public String Factory= "";

    public String Creator= "";

    public String Auditor= "";

    public String Editor= "";

    public String StatusText= "";        //审批状态
    
    
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
	public Calendar getStartTime() {
		return StartTime;
	}
	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}
	public int getMinutes() {
		return Minutes;
	}
	public void setMinutes(int minutes) {
		Minutes = minutes;
	}
	public int getWorkMinutes() {
		return WorkMinutes;
	}
	public void setWorkMinutes(int workMinutes) {
		WorkMinutes = workMinutes;
	}
	public int getIdleMinutes() {
		return IdleMinutes;
	}
	public void setIdleMinutes(int idleMinutes) {
		IdleMinutes = idleMinutes;
	}
	public Calendar getEndTime() {
		return EndTime;
	}
	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}
	public List<FMCShift> getShiftList() {
		return ShiftList;
	}
	public void setShiftList(List<FMCShift> shiftList) {
		ShiftList = shiftList;
	}
	public int isActive() {
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
	public int getFactoryID() {
		return FactoryID;
	}
	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}
	public String getFactory() {
		return Factory;
	}
	public void setFactory(String factory) {
		Factory = factory;
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
	public FMCWorkDay()
    {
        this.ID = 0;
        this.Name = "";
        this.Minutes = 0;
        this.WorkMinutes = 0;
        this.IdleMinutes = 0;

        this.StartTime = Calendar.getInstance();
        this.EndTime=(Calendar)this.StartTime.clone();
        this.Minutes = 480;
        this.EndTime.add(Calendar.MINUTE, this.Minutes);
        this.ShiftList = new ArrayList<>();
        this.Factory = "";
        this.Creator = "";
        this.Editor = "";
        this.Auditor = "";
        this.StatusText = "";
    }
    public FMCWorkDay Clone()
    {
        FMCWorkDay wShift = new FMCWorkDay();
        wShift.ID = this.ID;
        wShift.Name = this.Name;
        wShift.Minutes = this.Minutes;
        wShift.WorkMinutes = this.WorkMinutes;
        wShift.IdleMinutes = this.IdleMinutes;

        wShift.StartTime = this.StartTime;
        wShift.EndTime = this.EndTime;

        wShift.FactoryID = this.FactoryID;
        wShift.AuditorID = this.AuditorID;
        wShift.EditorID = this.EditorID;
        wShift.CreatorID = this.CreatorID;
        wShift.Status = this.Status;
        wShift.Active = this.Active;
        wShift.ShiftList = new ArrayList<FMCShift>(this.ShiftList);
        return wShift;
    }
}
