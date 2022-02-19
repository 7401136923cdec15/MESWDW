package com.mes.ncr.server.service.po.bpm;

import java.util.Calendar;
import java.util.List;

public class BPMActivitiProcessInstance {

	public String ID = "";

	public String ProcessDefinitionId = "";

	public String ProcessDefinitionName = "";

	public String Key = "";

	public Calendar StartTime = Calendar.getInstance();

	public Calendar EndTime = Calendar.getInstance();

	public Long DurationInMillis = 0L;// 持续时间

	public String AppayID = "";

	public String DeleteReason = "";

	public List<BPMActivitiHisTask> HisTaskList;// 历史任务列表
 

}
