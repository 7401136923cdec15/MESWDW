package com.mes.ncr.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCRoutePart;
import com.mes.ncr.server.service.po.ncr.NCRCarInfo;
import com.mes.ncr.server.service.po.ncr.NCRFrequency;
import com.mes.ncr.server.service.po.ncr.NCRPartTaskShow;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.NCRTaskPro;
import com.mes.ncr.server.service.po.ncr.NCRTaskShow;
import com.mes.ncr.server.service.po.ncr.SendNCRPartTaskShow;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.po.ncr.SendNCRTaskShow;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;

public interface NCRService {

	/**
	 * 条件查询NCR任务
	 * 
	 * @param wID
	 * @param wSenderID
	 * @param wCode
	 * @param wCarTypeID
	 * @param wCarNumber
	 * @param wStartTime
	 * @param wEndTime
	 * @param wStateIDList
	 * @return
	 */
	ServiceResult<List<NCRTask>> NCR_QueryTaskList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType,
			int wFlowID, int wLevel, int wSendType, int wTaskStepID, String wCode, int wCarTypeID, String wCarNumber,
			int wOrderID, int wCustomerID, int wLineID, int wStationID, int wSenderID, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStateIDList);

	ServiceResult<List<NCRTask>> NCR_QueryTaskList(BMSEmployee wLoginUser, int wFlowType, int wOrderID, int wLineID,
			int wCustomerID, int wProductID, int wLevel, String wCarNumber, int wPartID, Calendar wStartTime,
			Calendar wEndTime, int wStatus, String wIsRelease);

	ServiceResult<List<NCRTask>> NCR_QueryTaskListBySendType(BMSEmployee wLoginUser, int wSendType, int wOrderID,
			int wLineID, int wCustomerID, int wProductID, int wLevel, String wCarNumber, int wPartID,
			Calendar wStartTime, Calendar wEndTime, int wStatus, String wIsRelease);

	/**
	 * 更新NCR任务
	 * 
	 * @param wNCRTask
	 * @return
	 */
	ServiceResult<NCRTask> NCR_UpdateTask(BMSEmployee wLoginUser, NCRTask wNCRTask);

	/**
	 * 通过人员和标签类型查询NCR任务集合
	 * 
	 * @param wTagType
	 * @param wLoginID
	 * @return
	 */
	ServiceResult<List<BPMTaskBase>> NCR_QueryTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 获取后续工位ID的List
	 * 
	 * @param wNCRTask
	 */
	ServiceResult<List<Integer>> NCR_GetStationList(BMSEmployee wBMSEmployee, List<FPCRoutePart> wFPCRoutePartList,
			int wPartID);

	/**
	 * 时间段获取不合格评审任务
	 * 
	 * @param wNCRTask
	 */
	ServiceResult<List<NCRTask>> NCR_QueryTaskListByTime(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<NCRFrequency>> NCR_SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<Integer>> NCR_PartIDListByOrderID(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<List<Integer>> NCR_PartIDListByUser(BMSEmployee wLoginUser);

	ServiceResult<List<FPCPart>> NCR_ReturnPartIDList(BMSEmployee wLoginUser, List<Integer> wOrderStationIDList,
			List<Integer> wUserStationIDList);

	ServiceResult<NCRTask> NCR_CreateTask(BMSEmployee wBMSEmployee, BPMEventModule wModule);

	ServiceResult<Boolean> NCR_AddSonFolw(BMSEmployee wLoginUser, NCRTask wNCRTask);

	ServiceResult<NCRTask> NCR_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode);

	ServiceResult<SendNCRTask> NCR_CreateSendTask(BMSEmployee wBMSEmployee, BPMEventModule wModule);

	ServiceResult<SendNCRTask> NCR_UpdateSendTask(BMSEmployee wLoginUser, SendNCRTask wSendNCRTask);

	ServiceResult<List<BPMTaskBase>> NCR_QuerySendTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<SendNCRTask>> NCR_QuerySendTaskList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType,
			int wFlowID, int wLevel, int wSendType, int wTaskStepID, int wTaskID, int wRelaID, String wCode,
			int wCarTypeID, String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID,
			int wUpFlowID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList);

	ServiceResult<List<UserWorkArea>> NCR_GetDepartment(BMSEmployee wLoginUser);

	ServiceResult<SendNCRTask> NCR_QuerySendTask(BMSEmployee wLoginUser, int wUpFlowID);

	ServiceResult<NCRTask> NCR_QueryDefaultTask(BMSEmployee wLoginUser, int wModeID);

	ServiceResult<SendNCRTask> NCR_QuerySendTaskByID(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<Integer>> NCR_GetSendIPTItenList(BMSEmployee wLoginUser, int wTaskStepID);

	ServiceResult<List<NCRTask>> NCR_UndoTaskList(BMSEmployee wLoginUser, int wOrderID, int wType, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<SendNCRTaskShow>> NCR_QuerySendNCRTaskShow(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<NCRTaskShow>> NCR_QueryNCRTaskShowList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<SendNCRTask>> NCR_UndoSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID, int wType,
			Calendar wStartTime, Calendar wEndTime);

	// 不合格评审(工位)
	ServiceResult<List<NCRPartTaskShow>> RRO_QueryPartNCRTaskList(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<NCRTask>> RRO_PartUndoNCRTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wType,
			Calendar wStartTime, Calendar wEndTime);

	// 不合格评审申请(工位)
	ServiceResult<List<SendNCRPartTaskShow>> RRO_QueryPartSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<SendNCRTask>> RRO_PartUndoSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wType, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 导出不合格评审pdf
	 */
	ServiceResult<String> ExportPdf(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 导出不合格评审pdf(新版)
	 */
	ServiceResult<String> ExportPdfNew(BMSEmployee wLoginUser, int wTaskID);

	ServiceResult<List<BMSEmployee>> NCR_QueryManagerList(BMSEmployee wLoginUser, int wPartID);

	ServiceResult<List<BMSEmployee>> NCR_QuerySameClassMembers(BMSEmployee wLoginUser, int wPersonID);

	/**
	 * 人员查询不合格申请和评审单
	 */
	ServiceResult<List<NCRTaskPro>> NCR_QueryNCRTaskProListByTagType(BMSEmployee wBMSEmployee, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询评审单和申请单历史记录
	 */
	ServiceResult<List<NCRTaskPro>> NCR_QueryTaskProList(BMSEmployee wBMSEmployee, int wLevel, int wCarTypeID,
			String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID, int wSenderID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList);

	/**
	 * 获取发起任务
	 */
	ServiceResult<List<SendNCRTask>> NCR_QuerySendTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	ServiceResult<List<SendNCRTask>> RRO_PartUndoSendNCRTaskListNew(BMSEmployee wBMSEmployee, int wOrderID, int wPartID,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<NCRTask>> RRO_PartUndoNCRTaskListNew(BMSEmployee wBMSEmployee, int wOrderID, int wPartID,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 知会
	 */
	ServiceResult<Integer> NCR_Inform(BMSEmployee wLoginUser, int wNCRTaskID, List<Integer> wUserIDList);

	ServiceResult<List<BMSEmployee>> NCR_LeaderList(BMSEmployee wLoginUser, int wPartID);

	/**
	 * 车号分类数据
	 */
	ServiceResult<List<NCRCarInfo>> NCR_QueryTimeAllCar(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<NCRTask>> NCR_QueryTimeAllCarSub(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			String wCarType, String wCarNumber);

	/**
	 * 获取后续工位集合
	 */
	ServiceResult<List<FPCPart>> NCR_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	ServiceResult<Integer> NCR_UpdateSendNCRTask(BMSEmployee wLoginUser, SendNCRTask wData);

	void SendMessageToChecker(BMSEmployee wLoginUser, SendNCRTask wSendNCRTask);

	ServiceResult<Integer> NCR_ClearMessage(BMSEmployee wLoginUser, int wMessageID);

	ServiceResult<List<NCRCarInfo>> NCR_QueryLetGoCarList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<SendNCRTask>> NCR_QueryLetGoCarSubList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导出例外放行单
	 */
	ServiceResult<String> ExportLetGo(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 导出所有例外放行单(Excel)
	 */
	ServiceResult<String> NCR_ExportLetGoList(BMSEmployee wLoginUser);
}
