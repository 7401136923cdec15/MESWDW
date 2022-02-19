package com.mes.ncr.server.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPartPoint;
import com.mes.ncr.server.service.po.rro.RROFrequency;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RROItemTaskShow;
import com.mes.ncr.server.service.po.rro.RROPart;
import com.mes.ncr.server.service.po.rro.RROPartNo;
import com.mes.ncr.server.service.po.rro.RROPartTaskShow;
import com.mes.ncr.server.service.po.rro.RRORepairTable;
import com.mes.ncr.server.service.po.rro.RROSeleteType;
import com.mes.ncr.server.service.po.rro.RROTableBody;
import com.mes.ncr.server.service.po.rro.RROTask;

public interface RROService {
	/**
	 * 条件查询返修任务集合
	 * 
	 * @param wIDList
	 * @param wCode
	 * @param wCarTypeID
	 * @param wSenderID
	 * @param wSchedulerID
	 * @param wConfirmFinishID
	 * @param wStatus
	 * @param wStartTime
	 * @param wEndTime
	 * @return
	 */
	ServiceResult<List<RROTask>> RRO_QueryTaskList(BMSEmployee wLoginUser, List<Integer> wIDList, int wIsDelivery,
			String wCode, int wCarTypeID, int wSenderID, int wStationID, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, int wLineID, int wCustomerID);

	ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, int wOrderID, int wLineID,
			int wCustomerID, int wProductID, String wCarNumber, int wIsDelivery, int wPartID, Calendar wStartTime,
			Calendar wEndTime, int wStatus);

	/**
	 * 保存或更新返修任务
	 * 
	 * @param wTask
	 * @return
	 */
	ServiceResult<RROTask> RRO_SaveTask(BMSEmployee wLoginUser, RROTask wTask);

	ServiceResult<RROTask> RRO_QueryTaskByID(BMSEmployee wLoginUser, int wID);

	/**
	 * 根据标签获取返修任务集合
	 * 
	 * @param wTagTypes
	 * @param wEndTime
	 * @param wStartTime
	 * @param wBMSEmployee
	 * @return
	 */
	ServiceResult<List<BPMTaskBase>> RRO_QueryTaskListByTagTypes(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 返修项任务ID查询返修项
	 * 
	 * @param wRROTask
	 */
	ServiceResult<RROItemTask> RRO_QueryItemTaskByID(BMSEmployee wLoginUser, int wID);

	/**
	 * 保存或更新返修表
	 * 
	 * @param wTask
	 * @return
	 */
	ServiceResult<RRORepairTable> RRO_SaveRRORepairTable(BMSEmployee wLoginUser, RRORepairTable wTask);

	/**
	 * 条件查询返修表任务集合
	 * 
	 * @return
	 */
	ServiceResult<List<RRORepairTable>> RRO_QueryTableList(BMSEmployee wLoginUser, List<Integer> wIDList,
			int wCarTypeID, String wCarNumber, int wLineID, int wType, int wCustomerID, int wSenderID, int wApprovalID,
			int wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 车号与任务类型查询表单内容
	 * 
	 * @param wTask
	 * @return
	 */
	ServiceResult<List<RROTableBody>> RRO_QueryBodyByType(BMSEmployee wLoginUser, int wCarTypeID, String wPartNo,
			int wType);

	ServiceResult<List<RROTableBody>> RRO_QueryBodyList(BMSEmployee wLoginUser, RROTask wRROTask);

	ServiceResult<Map<String, Integer>> RRO_SelectItemTaskList(BMSEmployee wLoginUser, int wCarType, int wLineID,
			int wPartID, int wLimit);

	ServiceResult<List<RROFrequency>> RRO_SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<RROItemTask> RRO_CreateItemTask(BMSEmployee wBMSEmployee, BPMEventModule wModule);

	ServiceResult<List<BPMTaskBase>> RRO_QueryTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<RROItemTask> RRO_UpdateItemTask(BMSEmployee wLoginUser, RROItemTask wRROItemTask);

	ServiceResult<RROItemTask> RRO_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode);

	ServiceResult<RROItemTask> RRO_QueryDefaultTask(BMSEmployee wLoginUser, int wModuleID);

	ServiceResult<Integer> RRO_SetItemTaskCode(BMSEmployee wLoginUser, RROItemTask wRROItemTask);

	ServiceResult<List<BPMTaskBase>> RRO_QueryApplicantTaskList(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType,
			int wUpFlowID, int wTaskID, List<Integer> wStatusList, int wWorkAreaID, int wNCRID, int wSpecialTaskID,
			int wOrderID, int wPartID, Calendar wStartTime, Calendar wEndTime);
//	ServiceResult<BPMTaskBase> RRO_UpdateItemTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode);

	ServiceResult<Map<Integer, String>> RRO_QuerySendType(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<List<RROSeleteType>> RRO_QueryAllSendType(BMSEmployee wLoginUser);

	ServiceResult<Boolean> RRO_JugdeItemClose(BMSEmployee wLoginUser, int OrderID, int wPartID);

	ServiceResult<List<RROItemTaskShow>> RRO_QueryRROItemTaskShowList(BMSEmployee wLoginUser, int wIsSend,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RROItemTask>> RRO_ItemTaskList(BMSEmployee wLoginUser, int wOrderID, int wType, int wIsSend,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RROPartTaskShow>> RRO_QueryPartTaskList(BMSEmployee wLoginUser, int wOrderID, int wIsSend,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RROItemTask>> RRO_PartUndoTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wType,
			int wIsSend, Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RROItemTask>> RRO_QueryNotFinishItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 批量导出返修PDF记录
	 */
	ServiceResult<String> BatchExportPdf(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询工序列表
	 */
	ServiceResult<List<FPCPartPoint>> RRO_QueryProcess(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	/**
	 * 获取返修项任务集合
	 */
	ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, int wOrdreID);

	/**
	 * 获取返修项列表
	 */
	ServiceResult<List<RROItemTask>> RRO_QueryItemTaskAll(BMSEmployee wLoginUser, int wUpFlowID, int wOrderID,
			int wStationID, Calendar wStartTime, Calendar wEndTime, int wCustomerID);

	/**
	 * 获取车辆列表
	 */
	ServiceResult<List<RROTask>> RRO_QueryTaskProList(BMSEmployee wLoginUser, Object object, int wIsDelivery,
			String wPartNo, int wCarTypeID, int wSendID, int wStationID, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, int wLineID, int wCustomerID);

	ServiceResult<List<RROItemTask>> RRO_PartUndoTaskListNew(BMSEmployee wBMSEmployee, int wOrderID, int wPartID,
			int wIsSend, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 获取车号分类的返修列表
	 */
	ServiceResult<List<RROPartNo>> RRO_QueryPartNoRepairList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	ServiceResult<List<RROPart>> RRO_QueryPartRepairList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 获取返修子项
	 */
	ServiceResult<List<RROItemTask>> RRO_QueryItemListByOrder(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	/**
	 * 获取登录者所在工区
	 */
	ServiceResult<Integer> RRO_GetAreaID(BMSEmployee wLoginUser);

	ServiceResult<Integer> RRO_CodeReset(BMSEmployee wLoginUser);

	ServiceResult<Integer> RRO_QueryLoginInfo(BMSEmployee wLoginUser);
}
