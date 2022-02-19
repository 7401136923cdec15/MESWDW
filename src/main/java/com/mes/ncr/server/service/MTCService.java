package com.mes.ncr.server.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.lfs.LFSStoreHouse;
import com.mes.ncr.server.service.po.mtc.MTCRealTime;
import com.mes.ncr.server.service.po.mtc.MTCSectionInfo;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.mtc.MTCTypeNo;

public interface MTCService {

	/**
	 * 根据时间查询所有移车单 查询记录使用
	 * 
	 * @param wLoginUser
	 * @param wStartTime 查询开始时间
	 * @param wEndTime   查询结束时间
	 * @return
	 */
	ServiceResult<List<MTCTask>> MTC_GetTaskList(BMSEmployee wLoginUser, int wFlowID, int wFlowType, int wPlaceID,
			int wTargetID, int wShiftID, int wOrderID, int wType, int wCarTypeID, String wCarNumber,
			Calendar wStartTime, Calendar wEndTime, int wUpFlowID);

	/**
	 * 个人相关移车单
	 * 
	 * @param wLoginUser
	 * @param wTagType
	 * @param wStartTime
	 * @param wEndTime
	 * @return
	 */
	ServiceResult<List<BPMTaskBase>> MTC_GetTaskListByEmployee(BMSEmployee wLoginUser, TagTypes wTagType,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 提交移车单 可不用
	 * 
	 * @param wLoginUser
	 * @param wTask
	 * @return
	 */
	ServiceResult<MTCTask> MTC_SubmitTask(BMSEmployee wLoginUser, MTCTask wTask);

	ServiceResult<MTCTask> MTC_GetTask(BMSEmployee wLoginUser, int wID, String wCode);

	/**
	 * 创建移车单
	 * 
	 * @param wLoginUser
	 */
	ServiceResult<MTCTask> MTC_CreateTask(BMSEmployee wBMSEmployee, BPMEventModule wModule);

	/**
	 * 获取所有可移车库位
	 * 
	 * @param wMTCRealTime 车辆信息
	 */
	ServiceResult<List<LFSStoreHouse>> Get_StoreHouseList(BMSEmployee wLoginUser, String wPartNo, int wIsPreMove,
			int wStockID);

	/**
	 * 获取所有可移车台位
	 * 
	 */
	ServiceResult<List<FMCWorkspace>> Get_WorkspaceList(BMSEmployee wBMSEmployee, int wStokID, String wPartNo,
			int wTargetID, int wIsPreMove);

	/**
	 * 获取所有车号
	 * 
	 */
	ServiceResult<List<MTCTypeNo>> Get_MTCTypeNoList(BMSEmployee wBMSEmployee);

	ServiceResult<FPCProduct> MTC_GetProductByPartNo(BMSEmployee wBMSEmployee, String wPartNo);

	ServiceResult<List<MTCRealTime>> MTC_SelectRealListByStore(BMSEmployee wBMSEmployee, LFSStoreHouse wLFSStoreHouse);

	ServiceResult<MTCTask> MTC_QueryDefaultTask(BMSEmployee wBMSEmployee, int wModuleID);

	ServiceResult<List<MTCTypeNo>> MTC_GetMTCTypeNoAll(BMSEmployee wLoginUser);

	ServiceResult<String> MTC_SavePartNo(BMSEmployee wLoginUser, String wPartNo);

	ServiceResult<String> MTC_AddPartNo(BMSEmployee wLoginUser, int wStockID, String wPartNo);

	/**
	 * 获取所有可进行绑定的转向架车号列表
	 * 
	 */
	ServiceResult<List<String>> MTC_QueryBindingBogies(BMSEmployee wLoginUser);

	/**
	 * focus移车
	 */
	ServiceResult<Integer> MTC_SendForcas(BMSEmployee wLoginUser, Map<String, Object> wParam);

	/**
	 * 更新台位信息
	 */
	ServiceResult<Integer> MTC_UpdateWorkSpace(BMSEmployee wLoginUser, Map<String, Object> wParam);

	/**
	 * 通过库位查询工区主管
	 */
	ServiceResult<List<BMSEmployee>> MTC_QueryLeaderByStoreHouse(BMSEmployee wLoginUser, int wStoreHouseID);

	/**
	 * 更新台车节信息
	 */
	ServiceResult<Integer> MCT_UpdateSectionInfo(BMSEmployee wLoginUser, MTCSectionInfo wData);

	/**
	 * 根据车型获取台车节信息(自动创建)
	 */
	ServiceResult<List<MTCSectionInfo>> MTC_QuerySectionList(BMSEmployee wLoginUser, int wProductID);

	/**
	 * 预移车检查
	 */
	ServiceResult<String> MTC_PreMoveCheck(BMSEmployee wLoginUser, MTCTask wMTCTask);

	/**
	 * 获取台位获取班组长和工区主管和工区调度
	 */
	ServiceResult<List<BMSEmployee>> MCT_QueryAuditorList(BMSEmployee wLoginUser, int wPlaceID);

	/**
	 * 创建评论消息
	 */
	ServiceResult<Integer> MTC_CreateCommentMessage(BMSEmployee wLoginUser, List<Integer> wPersonIDList,
			String wMsgTigle, String wMsgContent, int wFlowType, int wTaskID);

	ServiceResult<List<MTCTask>> MTC_GetTaskListByEmployeeNew(BMSEmployee wBMSEmployee, Calendar wStartTime,
			Calendar wEndTime, int wProductID, String wPartNo, Integer wStatus);

	ServiceResult<List<MTCTask>> MTC_GetTaskList(BMSEmployee wLoginUser, int wProductID, String wPartNo, int wStatus,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 判断目标台位的责任班组和登陆人所在班组是否一致
	 */
	ServiceResult<Integer> MTC_JudgeClassIsSame(BMSEmployee wLoginUser, int wTargetPlaceID);

	ServiceResult<List<String>> MTC_QueryMTCPartNoList(BMSEmployee wLoginUser);

	ServiceResult<Object> BPM_RejectTo(BMSEmployee wLoginUser, String wTaskID, String targetActivityId, Object wData,
			String targetActivityName, int wFlowID);

	ServiceResult<List<BMSEmployee>> MCT_QueryAuditorListByStock(BMSEmployee wLoginUser, int wTargetStockID);
}
