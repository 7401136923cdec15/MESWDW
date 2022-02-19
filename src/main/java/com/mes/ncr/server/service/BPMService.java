package com.mes.ncr.server.service;

import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.utils.Configuration;

public interface BPMService {
	static String ServerUrl = Configuration.readConfigString("bpm.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("bpm.server.project.name", "config/config");

	/**
	 * 创建流程实例
	 * 
	 * @param wLoginUser
	 * @param wModule      模块
	 * @param wBusinessKey 业务单ID
	 * @param wData        提交数据
	 * @return
	 */
	APIResult BPM_CreateProcess(BMSEmployee wLoginUser, BPMEventModule wModule, int wBusinessKey, Object wData);

	/**
	 * 提交流程实例 返回数据有
	 * 
	 * @param wLoginUser
	 * @param wModule
	 * @param wData
	 * @return
	 */
	APIResult BPM_CompleteTask(BMSEmployee wLoginUser, int wTaskID, int wLocalScope, Object wData);

	/**
	 * 根据实例ID获取当前操作步骤
	 * 
	 * @param wLoginUser
	 * @param wProcessInstanceID
	 * @return
	 */
	APIResult BPM_CurrentTask(BMSEmployee wLoginUser, int wProcessInstanceID);

	/**
	 * 根据任务ID获取任务信息
	 * 
	 * @param wLoginUser
	 * @param wTaskID
	 * @return
	 */
	APIResult BPM_GetTask(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 根据任务ID获取可操作步骤
	 * 
	 * @param wLoginUser
	 * @param wTaskID
	 * @return
	 */
	APIResult BPM_GetOperationByTaskID(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 根据实例ID获取待办任务列表
	 * 
	 * @param wLoginUser
	 * @param wInstanceID
	 * @return
	 */
	APIResult BPM_GetTaskListByInstance(BMSEmployee wLoginUser, int wInstanceID);

	ServiceResult<Boolean> BPM_MsgUpdate(BMSEmployee wLoginUser, int wTaskID, int wLocalScope,
			BPMTaskBase paramBPMTaskBase, Object wData);

	APIResult BPM_GetInstanceByID(BMSEmployee wLoginUser, int wFlowID);

	APIResult BPM_DeleteInstanceByID(BMSEmployee wLoginUser, int wFlowID, String wReason);

	/**
	 * 获取已完成的流程数据
	 */
	APIResult BPM_GetActivitiHisTaskByPIId(BMSEmployee wLoginUser, int wFlowID);

	/**
	 * 驳回到指定节点
	 */
	APIResult BPM_RejectedSpecifiedActivity(BMSEmployee wLoginUser, String wTaskID, String targetActivityId,
			Object wData);

	APIResult BPM_GetHistoryInstanceByID(BMSEmployee wLoginUser, int processInstanceId);
}
