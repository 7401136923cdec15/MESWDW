package com.mes.ncr.server.serviceimpl.dao.ncr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.ncr.server.service.mesenum.BFCMessageStatus;
import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.NCRQuestionType;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSDepartment;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bms.BMSWorkCharge;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCRoute;
import com.mes.ncr.server.service.po.fpc.FPCRoutePart;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.ncr.server.service.po.ncr.MESStatusDictionary;
import com.mes.ncr.server.service.po.ncr.NCRCarInfo;
import com.mes.ncr.server.service.po.ncr.NCRFrequency;
import com.mes.ncr.server.service.po.ncr.NCRHandleResult;
import com.mes.ncr.server.service.po.ncr.NCRLevel;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.NCRType;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.ncr.server.service.po.sch.SCHSecondStatus;
import com.mes.ncr.server.service.po.sch.SCHSecondment;
import com.mes.ncr.server.service.po.sfc.SFCTaskStep;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.APSLOCOServiceImpl;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.LFSServiceImpl;
import com.mes.ncr.server.serviceimpl.NCRServiceImpl;
import com.mes.ncr.server.serviceimpl.QMSServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

public class NCRTaskDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(NCRTaskDAO.class);

	private static NCRTaskDAO Instance = null;

	/**
	 * 根据登录人获取所在工区工区主管
	 * 
	 * @param wID
	 * @return
	 */
	public BMSDepartment Get_Department(BMSEmployee wLoginUser, int wDepartmentID) {
		BMSDepartment wResult = new BMSDepartment();
		try {
			if (wDepartmentID <= 0) {
				return wResult;
			}
			BMSDepartment wBMSDepartment = CoreServiceImpl.getInstance()
					.BMS_QueryDepartmentByID(wLoginUser, wDepartmentID).Info(BMSDepartment.class);

			if (wBMSDepartment.ID <= 0) {
				return wResult;
			}
			if (wBMSDepartment.Type != 2) {
				wResult = Get_Department(wLoginUser, wBMSDepartment.ParentID);
			} else {
				wResult = wBMSDepartment;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询NCR任务集合
	 * 
	 * @param wID
	 * @return
	 */
	public NCRTask SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		NCRTask wResult = new NCRTask();
		try {
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<NCRTask> wResultList = SelectList(wLoginUser, wIDList, -1, -1, -1, -1, -1, "", -1, "", -1, -1, -1, -1,
					-1, wCalendar, wCalendar, null, wErrorCode);
			if (wResultList != null && wResultList.size() > 0)
				wResult = wResultList.get(0);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询NCR任务集合
	 * 
	 * @param wIDList
	 */
	public List<NCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ncr_task WHERE  1=1 " + " and (:wID is null or :wID = '''' or ID in ({1}));",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", StringUtils.Join(",", wIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<NCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wFlowID,
			int wLevel, int wSendType, int wTaskStepID, String wCode, int wCarTypeID, String wCarNumber, int wOrderID,
			int wCustomerID, int wLineID, int wStationID, int wUpFlowID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {

		return this.SelectList(wLoginUser, wIDList, wFlowType, wFlowID, wLevel, wSendType, wTaskStepID, wCode,
				wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wUpFlowID, wStartTime, wEndTime,
				wStateIDList, null, wErrorCode);
	}

	public List<NCRTask> SelectList(BMSEmployee wLoginUser, int wFlowType, int wOrderID, int wLineID, int wCustomerID,
			int wProductID, int wLevel, String wCarNumber, int wPartID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateIDList, List<Integer> wNoStateIDList, OutResult<Integer> wErrorCode) {

		return this.SelectList(wLoginUser, null, wFlowType, -1, wLevel, -1, -1, "", wProductID, wCarNumber, wOrderID,
				wCustomerID, wLineID, wPartID, -1, wStartTime, wEndTime, wStateIDList, wNoStateIDList, wErrorCode);
	}

	// Level SendType`
	/**
	 * 条件查询NCR任务集合
	 * 
	 * @param wIDList
	 * @param wCode
	 * @param wCarTypeID
	 * @param wCarNumber
	 * @param wStartTime
	 * @param wEndTime
	 * @param wStateIDList
	 * @return
	 */
	public List<NCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wFlowID,
			int wLevel, int wSendType, int wTaskStepID, String wCode, int wCarTypeID, String wCarNumber, int wOrderID,
			int wCustomerID, int wLineID, int wStationID, int wUpFlowID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateIDList, List<Integer> wNoStateIDList, OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wStateIDList == null)
				wStateIDList = new ArrayList<Integer>();
			if (wNoStateIDList == null)
				wNoStateIDList = new ArrayList<Integer>();
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResultList;

			wIDList.removeIf(p -> p <= 0);
			wStateIDList.removeIf(p -> p < 0);
			wNoStateIDList.removeIf(p -> p < 0);

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ncr_task WHERE  1=1 "
					+ " and ( :wFlowType <= 0 or :wFlowType = FlowType )"
					+ " and ( :wFlowID <= 0 or :wFlowID = FlowID )and ( :wTaskStepID <= 0 or :wTaskStepID = TaskStepID ) "
					+ " and ( :wLevel <= 0 or :wLevel = Level ) " + "and ( :wSendType <= 0 or :wSendType = SendType ) "
					+ " and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ " and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
					+ " and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ " and ( :wStationID <= 0 or :wStationID = StationID ) "
					+ " and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
					+ " and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
					+ " and ( :wCarTypeID <= 0 or :wCarTypeID = CarTypeID ) "
					+ " and (:wID is null or :wID = '''' or ID in ({1}))"
					+ " and ( :wCarNumber is null or :wCarNumber = '''' or :wCarNumber = CarNumber ) "
					+ " and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ " and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  SubmitTime ) "
					+ " and ( :wStatus = '''' or Status in ({2})) and ( :wNoStatus = '''' or Status not in ({3}));",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0",
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0",
					wNoStateIDList.size() > 0 ? StringUtils.Join(",", wNoStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wFlowType", wFlowType);
			wParamMap.put("wFlowID", wFlowID);
			wParamMap.put("wTaskStepID", wTaskStepID);
			wParamMap.put("wLevel", wLevel);
			wParamMap.put("wSendType", wSendType);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wCarTypeID", wCarTypeID);
			wParamMap.put("wCarNumber", wCarNumber);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));
			wParamMap.put("wNoStatus", StringUtils.Join(",", wNoStateIDList));
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wUpFlowID", wUpFlowID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 解析结果
	 * 
	 * @param wResultList
	 * @param wQueryResult
	 */
	private List<NCRTask> GetResultList(BMSEmployee wLoginUser, List<Map<String, Object>> wQueryResult,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			if (wQueryResult == null || wQueryResult.size() <= 0)
				return wResultList;

			for (Map<String, Object> wReader : wQueryResult) {
				NCRTask wNCRTask = new NCRTask();
				wNCRTask.ID = StringUtils.parseInt(wReader.get("ID"));
				wNCRTask.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wNCRTask.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wNCRTask.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wNCRTask.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wNCRTask.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wNCRTask.Code = StringUtils.parseString(wReader.get("Code"));
				wNCRTask.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				wNCRTask.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
				wNCRTask.Number = StringUtils.parseInt(wReader.get("Number"));
				wNCRTask.DepartmentID = StringUtils.parseInt(wReader.get("DepartmentID"));
				wNCRTask.DescribeInfo = StringUtils.parseString(wReader.get("DescribeInfo"));
				wNCRTask.Level = StringUtils.parseInt(wReader.get("Level"));
				wNCRTask.Type = StringUtils.parseInt(wReader.get("Type"));
				wNCRTask.Result = StringUtils.parseInt(wReader.get("Result"));
				wNCRTask.LevelName = (NCRLevel.getEnumType(wNCRTask.Level)).getLable();
				wNCRTask.TypeName = NCRType.getEnumType(wNCRTask.Type).getLable();
				wNCRTask.ResultName = NCRHandleResult.getEnumType(wNCRTask.Result).getLable();
				wNCRTask.OtherResult = StringUtils.parseString(wReader.get("OtherResult"));
				wNCRTask.CloseStationID = StringUtils.parseInt(wReader.get("CloseStationID"));
				wNCRTask.Status = StringUtils.parseInt(wReader.get("Status"));
				wNCRTask.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wNCRTask.SendType = StringUtils.parseInt(wReader.get("SendType"));
				wNCRTask.CloseTime = StringUtils.parseCalendar(wReader.get("CloseTime"));
				wNCRTask.IPTItemID = StringUtils.parseInt(wReader.get("IPTItemID"));
				wNCRTask.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wNCRTask.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wNCRTask.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wNCRTask.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wNCRTask.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wNCRTask.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wNCRTask.SendNCRID = StringUtils.parseInt(wReader.get("SendNCRID"));
				wNCRTask.ImageList = StringUtils.parseString(wReader.get("ImageList"));
				wNCRTask.DutyCarfID = StringUtils.parseString(wReader.get("DutyCarfID"));
				wNCRTask.DutyDepartmentID = StringUtils.parseString(wReader.get("DutyDepartmentID"));
				// 新增三属性2020-11-27 09:46:05
				wNCRTask.ProductName = StringUtils.parseString(wReader.get("ProductName"));
				wNCRTask.PartNos = StringUtils.parseString(wReader.get("PartNos"));
				wNCRTask.ImageUrl = StringUtils.parseString(wReader.get("ImageUrl"));
				wNCRTask.StationStaff = StringUtils.parseInt(wReader.get("StationStaff"));

				// 新增两属性2021-11-24 15:03:38
				wNCRTask.IsRelease = StringUtils.parseInt(wReader.get("IsRelease"));
				wNCRTask.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));

				if (wNCRTask.Result == NCRHandleResult.LetGoStation.getValue()
						|| wNCRTask.Result == NCRHandleResult.LetGoTime.getValue()) {
					wNCRTask.IsRelease = 1;
				} else {
					wNCRTask.IsRelease = 0;
				}

				wNCRTask.IsReleaseName = wNCRTask.IsRelease == 1 ? "是" : "否";
				wNCRTask.StepNames = GetStepNames(wNCRTask.StepIDs);

				wNCRTask.StationStaffName = WDWConstans.GetBMSEmployeeName(wNCRTask.StationStaff);

				wNCRTask.AuditApproval = StringUtils.parseString(wReader.get("AuditApproval"));
				wNCRTask.AuditApprovalName = GetNames(wLoginUser, wNCRTask.AuditApproval);

				wNCRTask.ModelNo = StringUtils.parseString(wReader.get("ModelNo"));
				wNCRTask.QuestionType = StringUtils.parseInt(wReader.get("QuestionType"));
				wNCRTask.QuestionTypeText = NCRQuestionType.getEnumType(wNCRTask.QuestionType).getLable();
				wNCRTask.ProcessID = StringUtils.parseInt(wReader.get("ProcessID"));
				wNCRTask.ProcessName = WDWConstans.GetFPCStepName(wNCRTask.ProcessID);

				wNCRTask.DutyCarfName = WDWConstans
						.GetBMSEmployeeName(StringUtils.parseIntList(wNCRTask.DutyCarfID.split(",|;")));
				wNCRTask.DutyDepartmentName = WDWConstans
						.GetBMSDepartmentName(StringUtils.parseIntList(wNCRTask.DutyDepartmentID.split(",|;")));

				wNCRTask.UpFlowName = WDWConstans.GetBMSEmployeeName(wNCRTask.UpFlowID);
				wNCRTask.Department = WDWConstans.GetBMSDepartmentName(wNCRTask.DepartmentID);
				wNCRTask.CarType = WDWConstans.GetFPCProductNo(wNCRTask.CarTypeID);
				wNCRTask.CustomerName = WDWConstans.GetCRMCustomerName(wNCRTask.CustomerID);
				wNCRTask.StationName = WDWConstans.GetFPCPartName(wNCRTask.StationID);
				wNCRTask.LineName = WDWConstans.GetFMCLineName(wNCRTask.LineID);
				wNCRTask.CloseStationName = WDWConstans.GetFPCPartName(wNCRTask.CloseStationID);
				if (wNCRTask.SendNCRID > 0) {
					wNCRTask.SendNCRName = SendNCRTaskDAO.getInstance().SelectByID(wLoginUser, wNCRTask.SendNCRID,
							wErrorCode).UpFlowName;
				}
				wResultList.add(wNCRTask);
			}

			HandleStatusText(wResultList);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 获取工序列表
	 */
	private String GetStepNames(String stepIDs) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(stepIDs)) {
				return wResult;
			}

			String[] wStrs = stepIDs.split(",");
			List<String> wNames = new ArrayList<String>();
			for (String wStr : wStrs) {
				int wStepID = StringUtils.parseInt(wStr);
				if (wStepID <= 0) {
					continue;
				}
				String wName = WDWConstans.GetFPCStepName(wStepID);
				if (StringUtils.isEmpty(wName)) {
					continue;
				}
				wNames.add(wName);
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 处理状态文本
	 */
	private void HandleStatusText(List<NCRTask> wResultList) {
		try {
			Map<String, String> wMap = new HashMap<String, String>();

			wMap.put("待质量管理部部长 填写审核意见", "待审核");
			wMap.put("待工艺师 填写处理意见", "待填写");
			wMap.put("已相关部门人员 填写审核意见", "待审核");
			wMap.put("待采购员 填写意见", "待填写");
			wMap.put("待分配执行人", "待分配");
			wMap.put("已相关部门人员 填写审核意见,待专职检验员 确认", "待确认");
			wMap.put("待质量工程师填写审核意见", "待审核");
			wMap.put("待专职检验员 确认", "待确认");
			wMap.put("待制造中心总经理填写审核意见", "待审核");
			wMap.put("待触发子流程,待执行人处理,待是否超时", "待处理");
			wMap.put("待执行人处理,待触发子流程,待是否超时,待分配执行人", "待处理");
			wMap.put("待触发子流程,待是否超时,待专职检验员 确认", "待确认");
			wMap.put("已相关部门人员 填写审核意见,待触发子流程,待执行人处理,待是否超时", "待处理");
			wMap.put("已发起评审", "已发起");
			wMap.put("待发起不合格评审", "待发起");
			wMap.put("待主管审批", "待审批");
			wMap.put("待质量工程师 评审定级", "待定级");
			wMap.put("专职检验员已驳回", "已驳回");
			wMap.put("待专职检验员完善描述", "待完善");
			wMap.put("已相关部门人员 填写审核意见,待质量工程师填写审核意见", "待审核");
			wMap.put("已相关部门人员 填写审核意见,待触发子流程,待是否超时,待专职检验员 确认", "待确认");
			wMap.put("待物流采购部部长审核意见", "待审核");
			wMap.put("已相关部门人员 填写审核意见,待执行人处理", "待处理");
			wMap.put("已相关部门人员 填写审核意见,待执行人处理,待是否超时,待触发子流程", "待处理");
			wMap.put("待是否超时,待触发子流程,待专职检验员 确认", "待确认");
			wMap.put("待执行人处理", "待处理");
			wMap.put("已相关部门人员 填写审核意见,待触发子流程,待是否超时,待执行人处理", "待处理");
			wMap.put("待相关部门人员 填写审核意见", "待审核");
			wMap.put("待审核,待质量管理部部长 填写审核意见", "待审核");
			wMap.put("待定级,待工艺师 填写处理意见", "待处理");
			wMap.put("待填写,待相关部门人员 填写审核意见", "待审核");
			wMap.put("待填写,待分配执行人", "待分配");
			wMap.put("待定级,待专职检验员 确认", "待确认");
			wMap.put("待定级,待分配执行人", "待分配");
			wMap.put("待处理,待执行人处理", "待处理");
			wMap.put("待处理,待副总（分管质量部）", "待审核");
			wMap.put("待处理,待质量管理部部长 填写审核意见", "待审核");
			wMap.put("待处理,待相关部门人员 填写审核意见", "待审核");
			wMap.put("待处理,待专职检验员 确认", "待确认");
			wMap.put("待审核,待质量工程师填写审核意见", "待审核");
			wMap.put("待审核,待执行人处理", "待处理");
			wMap.put("待审核,待分配执行人", "待分配");
			wMap.put("待审核,待触发子流程,待执行人处理,待是否超时", "待处理");
			wMap.put("待分配,待执行人处理", "待处理");
			wMap.put("待专职检验员 发起评审", "待发起");
			wMap.put("待审核,待专职检验员 确认", "待确认");
			wMap.put("待审核,待工艺师 填写处理意见", "待处理");
			wMap.put("待定级,待采购员 填写意见", "待填写");
			wMap.put("待审核,待质量管理部部长填写审核意见", "待审核");
			wMap.put("待分配,待执行人处理,待触发子流程,待是否超时", "待处理");
			wMap.put("待完善,待执行人处理,待是否超时,待触发子流程", "待处理");
			wMap.put("待完善,待执行人处理", "待处理");
			wMap.put("待分配,待触发子流程,待执行人处理,待是否超时", "待处理");
			wMap.put("待完善,待质量工程师 评审定级", "待定级");
			wMap.put("待审核,待物流采购部部长填写审核意见", "待审核");
			wMap.put("待完善,待触发子流程,待执行人处理,待是否超时", "待处理");
			wMap.put("待填写,待工艺师 填写处理意见", "待处理");
			wMap.put("待确认,待执行人处理", "待处理");
			wMap.put("待发起,待工艺师 填写处理意见", "待处理");
			wMap.put("待审核,待副总（分管质量部）", "待审批");
			// 查询状态字典
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MESStatusDictionary> wList = MESStatusDictionaryDAO.getInstance().SelectList(BaseDAO.SysAdmin, -1,
					2006, wErrorCode);
			for (MESStatusDictionary wMESStatusDictionary : wList) {
				if (!wMap.containsKey(wMESStatusDictionary.Key)) {
					wMap.put(wMESStatusDictionary.Key, wMESStatusDictionary.Value);
				}
			}

			for (NCRTask wSendNCRTask : wResultList) {
				if (wMap.containsKey(wSendNCRTask.StatusText)) {
					wSendNCRTask.StatusText = wMap.get(wSendNCRTask.StatusText);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取名称集合
	 */
	private String GetNames(BMSEmployee wLoginUser, String auditApproval) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(auditApproval)) {
				return wResult;
			}

			String[] wStrs = auditApproval.split(",");
			List<String> wNames = new ArrayList<String>();
			for (String wStr : wStrs) {
				int wUserID = StringUtils.parseInt(wStr);

				String wName = WDWConstans.GetBMSEmployeeName(wUserID);
				if (StringUtils.isNotEmpty(wName)) {
					wNames.add(wName);
				}
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取下一个编号
	 */
	public String GetNextCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			// 本月时间
			int wYear = Calendar.getInstance().get(Calendar.YEAR);
			int wMonth = Calendar.getInstance().get(Calendar.MONTH);
			Calendar wSTime = Calendar.getInstance();
			wSTime.set(wYear, wMonth, 1, 0, 0, 0);
			Calendar wETime = Calendar.getInstance();
			wETime.set(wYear, wMonth + 1, 1, 23, 59, 59);
			wETime.add(Calendar.DATE, -1);

			String wSQL = StringUtils.Format(
					"select count(*)+1 as Number from {0}.ncr_task where CreateTime > :wSTime and CreateTime < :wETime;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wSTime", wSTime);
			wParamMap.put("wETime", wETime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wNumber = 0;
			for (Map<String, Object> wReader : wQueryResult) {
				if (wReader.containsKey("Number")) {
					wNumber = StringUtils.parseInt(wReader.get("Number"));
					break;
				}
			}

			wResult = StringUtils.Format("NCR{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断是否触发子流程或为子流程任务时，进行实时更新主任务状态
	 */
	public ServiceResult<Boolean> TriggerOrUpdateTask(BMSEmployee wLoginUser, NCRTask wNCRTask,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>(true);
		try {
			if (wNCRTask == null || wNCRTask.ID <= 0 || wNCRTask.StepID <= 0) {
				return wResult;
			}
			wResult.setResult(false);
			// 若超过关闭时间或关闭工位任务还未确认则新增子流程

			Calendar wDate = Calendar.getInstance();
			wDate.set(2010, 1, 1, 0, 0, 0);
			int wIsTrue = wNCRTask.CloseTime.compareTo(wDate);
			if (wIsTrue != -1) {
				Calendar wCalendar = Calendar.getInstance();
				int wNumber = wNCRTask.CloseTime.compareTo(wCalendar);
				if (wNumber != 1) {
					wResult = NCRServiceImpl.getInstance().NCR_AddSonFolw(wLoginUser, wNCRTask);

				}
			}

			// 生产任务当前工位大于关闭工位则触发子流程
			if (wNCRTask.CloseStationID > 0) {
				int wTaskStepID = wNCRTask.TaskStepID;
				SFCTaskStep wServiceResult = APSLOCOServiceImpl.getInstance()
						.SFC_QueryTaskStepByID(wLoginUser, wTaskStepID).Info(SFCTaskStep.class);
				if (wServiceResult != null && wServiceResult.PartID > 0) {

					OMSOrder wOMSOrder = APSLOCOServiceImpl.getInstance().SFC_OMSOrderByID(wLoginUser, wNCRTask.OrderID)
							.Info(OMSOrder.class);
					FPCRoute wFPCRoute = new FPCRoute();
					if (wOMSOrder != null) {
						wFPCRoute = FMCServiceImpl.getInstance().FPC_QueryRouteByID(wLoginUser, wOMSOrder.RouteID)
								.Info(FPCRoute.class);
					}

					List<FPCRoutePart> wFPCRoutePartList = FMCServiceImpl.getInstance()
							.FPC_QueryRouteByID(wLoginUser, wFPCRoute.getID()).List(FPCRoutePart.class);
					// 获取当前工位后续工位列表
					ServiceResult<List<Integer>> wPartIDList = NCRServiceImpl.getInstance()
							.NCR_GetStationList(wLoginUser, wFPCRoutePartList, wServiceResult.PartID);
					// 后续工位不包含关闭工位则触发子流程
					if (wPartIDList.Result != null && wPartIDList.Result.size() > 0
							&& !wPartIDList.Result.contains(wNCRTask.CloseStationID)) {
						wResult = NCRServiceImpl.getInstance().NCR_AddSonFolw(wLoginUser, wNCRTask);

					}
				}
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 通过车号与登录人对应岗位返回工位列表
	 * 
	 * @return
	 */
	public List<FPCPart> Get_ReturnPartIDList(BMSEmployee wLoginUser, List<Integer> wOrderStationIDList,
			List<Integer> wUserStationIDList) {
		List<FPCPart> wResultList = new ArrayList<FPCPart>();
		try {
			List<Integer> wStationIDList = new ArrayList<Integer>();
			if (wOrderStationIDList == null || wOrderStationIDList.size() <= 0)
				return wResultList;
			if (wUserStationIDList == null || wUserStationIDList.size() <= 0) {
				if (wOrderStationIDList != null && wOrderStationIDList.size() > 0)
					wStationIDList.addAll(wOrderStationIDList);
			}
			if (wOrderStationIDList.size() > 0 && wUserStationIDList.size() > 0) {
				// 取车号工位与人员工位的交集工位返回
				for (Integer wItem : wOrderStationIDList) {
					if (wUserStationIDList.contains(wItem)) {
						if (!wStationIDList.contains(wItem))
							wStationIDList.add(wItem);
					}
				}
			}
			// 若未查询到共同工位，则返回车辆工位列表
			if (wStationIDList == null || wStationIDList.size() <= 0)
				wStationIDList.addAll(wOrderStationIDList);

			if (wStationIDList != null && wStationIDList.size() > 0) {
				List<FPCPart> wFPCPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1)
						.List(FPCPart.class);
				if (wFPCPartList != null && wFPCPartList.size() > 0) {
					wResultList = wFPCPartList.stream().filter(p -> wStationIDList.contains(p.ID))
							.collect(Collectors.toList());
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 通过订单号获取（转序单）车辆当前工位IDList
	 * 
	 * @return
	 */
	public List<Integer> Get_PartIDListByOrderID(BMSEmployee wLoginUser, int wOrderID) {
		List<Integer> wResultList = new ArrayList<Integer>();
		try {
			List<RSMTurnOrderTask> wRSMTurnOrderTaskList = QMSServiceImpl.getInstance()
					.RSM_QueryTurnOrderTaskList(wLoginUser, wOrderID, -1, -1, null).List(RSMTurnOrderTask.class);
			if (wRSMTurnOrderTaskList != null && wRSMTurnOrderTaskList.size() > 0) {
				boolean wIsContain = false;
				for (RSMTurnOrderTask wItem : wRSMTurnOrderTaskList) {
					wIsContain = false;
					for (RSMTurnOrderTask wItem1 : wRSMTurnOrderTaskList) {
						if (wItem1.ID == wItem.ID)
							continue;
						if (wItem.TargetStationID == wItem1.ApplyStationID) {
							wIsContain = true;
							break;
						}
					}
					if (!wIsContain) {
						wResultList.add(wItem.TargetStationID);
					}
				}
			} else {// 未查询到转序单，则返回第一个工位
				OMSOrder wOMSOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				List<FPCRoutePart> wFPCRoutePartList = FMCServiceImpl.getInstance()
						.FPC_QueryRoutePartListByRouteID(wLoginUser, wOMSOrder.RouteID).List(FPCRoutePart.class);
				if (wFPCRoutePartList != null && wFPCRoutePartList.size() > 0) {
					Optional<FPCRoutePart> wOptional = wFPCRoutePartList.stream()
							.filter(p -> p.PrevPartID == 0 && p.OrderID == 1).findFirst();
					if (wOptional != null && wOptional.isPresent())
						wResultList.add(wOptional.get().PartID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 通过登录人获取当前工位IDList
	 * 
	 * @return
	 */
	public List<Integer> Get_PartIDListByUser(BMSEmployee wLoginUser) {
		List<Integer> wResultList = new ArrayList<Integer>();
		try {
			// 判断是否借调,若在借调时间内则拉出借调班组的班组工位
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<SCHSecondment> wSCHSecondmentList = APSLOCOServiceImpl.getInstance()
					.SCH_QueryTaskListByID(wLoginUser, -1, -1, -1, -1, wLoginUser.ID, wCalendar, wCalendar)
					.List(SCHSecondment.class);
			if (wSCHSecondmentList != null && wSCHSecondmentList.size() > 0) {
				Calendar wNowTime = Calendar.getInstance();
				List<SCHSecondment> wAllList = wSCHSecondmentList.stream()
						.filter(p -> p.Status == SCHSecondStatus.Seconded.getValue()).collect(Collectors.toList());
				if (wAllList != null && wAllList.size() > 0) {
					for (SCHSecondment wItem : wAllList) {
						if (wItem.ValidDate.compareTo(wNowTime) > 1) {
							// 借调班组，查询班组工位列表
							List<BMSWorkCharge> wBMSWorkChargeList = CoreServiceImpl.getInstance()
									.BMS_QueryWorkChargeList(wLoginUser, -1, wItem.SecondDepartmentID, 1)
									.List(BMSWorkCharge.class);
							if (wBMSWorkChargeList != null && wBMSWorkChargeList.size() > 0) {
								for (BMSWorkCharge wBMSWorkCharge : wBMSWorkChargeList) {
									if (!wResultList.contains(wBMSWorkCharge.StationID))
										wResultList.add(wBMSWorkCharge.StationID);
								}
							}
							break;
						}
					}
				}
			}
			// 获取登录人岗位，查询工位列表
			BMSDepartment wBMSDepartment = CoreServiceImpl.getInstance()
					.BMS_QueryDepartmentByID(wLoginUser, wLoginUser.DepartmentID).Info(BMSDepartment.class);
			if (wBMSDepartment != null && wBMSDepartment.ID != 0) {
				switch (wBMSDepartment.Type) {
				case 2:// 工区
					List<LFSWorkAreaStation> wLFSWorkAreaStationList = LFSServiceImpl.getInstance()
							.LFS_QueryWorkAreaStationList(wLoginUser, wBMSDepartment.ID).List(LFSWorkAreaStation.class);
					if (wLFSWorkAreaStationList != null && wLFSWorkAreaStationList.size() > 0) {
						for (LFSWorkAreaStation wItem : wLFSWorkAreaStationList) {
							if (!wResultList.contains(wItem.StationID))
								wResultList.add(wItem.StationID);
						}
					}
					break;
				case 3:// 班组
					List<BMSWorkCharge> wBMSWorkChargeList = CoreServiceImpl.getInstance()
							.BMS_QueryWorkChargeList(wLoginUser, -1, wBMSDepartment.ID, 1).List(BMSWorkCharge.class);
					if (wBMSWorkChargeList != null && wBMSWorkChargeList.size() > 0) {
						for (BMSWorkCharge wItem : wBMSWorkChargeList) {
							if (!wResultList.contains(wItem.StationID))
								wResultList.add(wItem.StationID);
						}
					}
					break;
				default:
					break;
				}
			}
			// 查询是否为工区检验员，若为工区检验员则获取该工区所有工位
			List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
			if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
				for (LFSWorkAreaChecker wItem : wLFSWorkAreaCheckerList) {
					if (wItem.CheckerIDList != null && wItem.CheckerIDList.size() > 0) {
						if (wItem.CheckerIDList.contains(wLoginUser.ID)) {
							List<LFSWorkAreaStation> wLFSWorkAreaStationList = LFSServiceImpl.getInstance()
									.LFS_QueryWorkAreaStationList(wLoginUser, wItem.WorkAreaID)
									.List(LFSWorkAreaStation.class);
							if (wLFSWorkAreaStationList != null && wLFSWorkAreaStationList.size() > 0) {
								for (LFSWorkAreaStation wLFSWorkAreaStation : wLFSWorkAreaStationList) {
									if (!wResultList.contains(wLFSWorkAreaStation.StationID))
										wResultList.add(wLFSWorkAreaStation.StationID);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 时间段查询不同任务等级不合格评审发生频次
	 * 
	 * @return
	 */
	public List<NCRFrequency> SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<NCRFrequency> wResultList = new ArrayList<NCRFrequency>();

		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			String wSQL = StringUtils.Format(
					" select Level ,count(ID) FROM  {0}.ncr_task where 1=1 and (:wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') "
							+ "or SubmitTime>= :wStartTime) and (:wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or CreateTime<= :wEndTime)"
							+ " group by Level ;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			if (wQueryResult != null && wQueryResult.size() > 0) {
				for (Map<String, Object> wReader : wQueryResult) {
					NCRFrequency wItem = new NCRFrequency();
					wItem.Level = StringUtils.parseInt(wReader.get("Level"));
					if (wItem.Level == 0)
						continue;
					wItem.LevelName = NCRLevel.getEnumType(wItem.Level).getLable();
					wItem.Frequency = StringUtils.parseInt(wReader.get("count(ID)"));
					wResultList.add(wItem);
				}
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 创建NCR任务
	 * 
	 * @param wSenderID
	 * @return
	 */
	public NCRTask NCR_CreateTask(BMSEmployee wLoginUser, BPMEventModule wModuleID, OutResult<Integer> wErrorCode) {
		NCRTask wResult = new NCRTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			wResult.FlowType = wModuleID.getValue();
			wResult.FlowID = 0;
			wResult.ID = 0;
			wResult.UpFlowID = wLoginUser.ID;
			wResult.UpFlowName = wLoginUser.Name;
			wResult.DepartmentID = wLoginUser.DepartmentID;
			wResult.Department = WDWConstans.GetBMSDepartmentName(wLoginUser.DepartmentID);
			wResult.CreateTime = Calendar.getInstance();
			wResult.SubmitTime = Calendar.getInstance();
			wResult.Code = this.GetNextCode(wLoginUser, wErrorCode);
			wResult.Status = 0;
			// 查询所有工区检验员并返回（前提是发起人为工区检验员）
			List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
			if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
				for (LFSWorkAreaChecker wItem : wLFSWorkAreaCheckerList) {
					if (wItem.CheckerIDList != null && wItem.CheckerIDList.contains(wLoginUser.ID)) {
						wResult.FollowerID.addAll(wItem.CheckerIDList);
					}
				}
			}
			// 获取当前登录人工区ID并将该工区工区检验员进行赋值
			ServiceResult<List<UserWorkArea>> wServiceResult = NCRServiceImpl.getInstance()
					.NCR_GetDepartment(wLoginUser);
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
				List<LFSWorkAreaChecker> wAreaList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, wServiceResult.Result.get(0).getWorkID())
						.List(LFSWorkAreaChecker.class);
				if (wAreaList != null && wAreaList.size() > 0) {
					wResult.FollowerID.addAll(wAreaList.get(0).CheckerIDList);
				}
			}
			if (wResult.FollowerID != null && wResult.FollowerID.size() > 0) {
				wResult.FollowerID = wResult.FollowerID.stream().distinct().collect(Collectors.toList());
			}
			this.BPM_UpdateTask(wLoginUser, wResult, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断是否为申请单发起的不合格评审
	 * 
	 * @param wSenderID
	 * @return
	 */
	public void IsSendNCR(BMSEmployee wLoginUser, NCRTask wNCRTask) {
		try {

			if (wNCRTask.ID > 0 && wNCRTask.SendNCRID > 0 && wNCRTask.Status == NCRStatus.CarfFill.getValue()) {// 提交时判断是否为申请单发起的不合格评审任务
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
				List<Integer> wMessageIDList = new ArrayList<Integer>();
				wMessageIDList.add(wNCRTask.SendNCRID);
				List<BFCMessage> wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, -1,
						BPMEventModule.SCNCR.getValue(), wMessageIDList, BFCMessageType.Task.getValue(), -1)
						.List(BFCMessage.class);
				if (wMessageList != null && wMessageList.size() > 0) {
					for (BFCMessage wBFCMessage : wMessageList) {
						wBFCMessage.Active = BFCMessageStatus.Finished.getValue();
					}
					CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
				}
				SendNCRTask wSendNCRTask = SendNCRTaskDAO.getInstance().SelectByID(wLoginUser, wNCRTask.SendNCRID,
						wErrorCode);
				if (wSendNCRTask != null && wSendNCRTask.ID > 0) {
					wSendNCRTask.Status = NCRStatus.SendNCR.getValue();
					wSendNCRTask.StatusText = "已发起评审";
					wSendNCRTask.NCRID = wNCRTask.ID;
					SendNCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wSendNCRTask, wErrorCode);
				}
			}
			// 专职检验员驳回
			else if (wNCRTask.ID > 0 && wNCRTask.SendNCRID > 0 && wNCRTask.Status == 22) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
				List<Integer> wMessageIDList = new ArrayList<Integer>();
				wMessageIDList.add(wNCRTask.SendNCRID);
				List<BFCMessage> wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, -1,
						BPMEventModule.SCNCR.getValue(), wMessageIDList, BFCMessageType.Task.getValue(), -1)
						.List(BFCMessage.class);
				if (wMessageList != null && wMessageList.size() > 0) {
					for (BFCMessage wBFCMessage : wMessageList) {
						wBFCMessage.Active = BFCMessageStatus.Finished.getValue();
					}
					CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
				}
				SendNCRTask wSendNCRTask = SendNCRTaskDAO.getInstance().SelectByID(wLoginUser, wNCRTask.SendNCRID,
						wErrorCode);
				if (wSendNCRTask != null && wSendNCRTask.ID > 0) {
					wSendNCRTask.Status = 22;
					wSendNCRTask.StatusText = "专职检验员已驳回";
					wSendNCRTask.NCRID = wNCRTask.ID;
					SendNCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wSendNCRTask, wErrorCode);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private NCRTaskDAO() {
		super();
	}

	public static NCRTaskDAO getInstance() {
		if (Instance == null)
			Instance = new NCRTaskDAO();
		return Instance;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResult = new ArrayList<NCRTask>();
		try {

			// 获取所有任务消息 模块为不合格评审的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.QTNCR.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.QTNCR.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.QTNCR.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechNCR.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechNCR.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechNCR.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			// List<> 查询消息

			Map<Integer, NCRTask> wNCRTaskMap = new HashMap<Integer, NCRTask>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {

				List<NCRTask> wNCRTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, wErrorCode);

				wNCRTaskMap = wNCRTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			NCRTask wNCRTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wNCRTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wNCRTaskTemp = CloneTool.Clone(wNCRTaskMap.get((int) wBFCMessage.getMessageID()), NCRTask.class);
				wNCRTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wNCRTaskTemp);
			}

			wResult.sort(Comparator.comparing(NCRTask::getSubmitTime).reversed());
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0)
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetDoneTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<NCRTask> wResult = new ArrayList<NCRTask>();
		wErrorCode.set(0);
		try {

			List<NCRTask> wNCRTaskList = new ArrayList<NCRTask>();
			// 获取所有任务消息 模块为不合格评审的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.QTNCR.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			if (wMessageList == null)
				wMessageList = new ArrayList<BFCMessage>();
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.QTNCR.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechNCR.getValue(), -1,
									BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechNCR.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			if (wTaskIDList != null && wTaskIDList.size() > 0)
				wNCRTaskList = NCRTaskDAO.getInstance().SelectList(wLoginUser, wTaskIDList, -1, -1, -1, -1, -1, "", -1,
						"", -1, -1, -1, -1, -1, wStartTime, wEndTime, null, wErrorCode);

			List<NCRTask> wNCRTaskUndoneList = new ArrayList<NCRTask>();

			List<Integer> wTaskIDUndoneList = wNCRTaskUndoneList.stream().map(p -> (int) p.ID).distinct()
					.collect(Collectors.toList());
			wTaskIDUndoneList.removeAll(wTaskIDList);
			wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.QTNCR.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class);
			if (wMessageList == null)
				wMessageList = new ArrayList<BFCMessage>();
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechNCR.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class));

			wTaskIDUndoneList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());
			for (NCRTask wNCRTask : wNCRTaskUndoneList) {
				if (wTaskIDUndoneList.contains(wNCRTask.ID))
					wNCRTaskList.add(wNCRTask);
			}

			wNCRTaskList.sort(Comparator.comparing(NCRTask::getSubmitTime).reversed());

			wResult = wNCRTaskList;
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0)
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetSendTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<BPMTaskBase> wResult = new ArrayList<BPMTaskBase>();
		wErrorCode.set(0);
		try {
			wResult = new ArrayList<BPMTaskBase>(NCRTaskDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1, -1,
					-1, "", -1, "", -1, -1, -1, -1, wLoginUser.getID(), wStartTime, wEndTime, null, wErrorCode));

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		NCRTask wResult = new NCRTask();
		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//			if (wTask == null)
//				return wResult;
			if (wTask == null) {
				wErrorCode.set(MESException.Parameter.getValue());
				return wResult;
			}
			ServiceResult<String> wInstance = null;

			if (wTask.getID() <= 0) {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 500802);
			} else {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			}
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			NCRTask wNCRTask = (NCRTask) wTask;
			// 判断是否为特殊状态，若为特殊状态给状态文本赋值

			if (wNCRTask.SendNCRID > 0) {
				SendNCRTask wItem = SendNCRTaskDAO.getInstance().SelectByID(wLoginUser, wResult.SendNCRID, wErrorCode);
				if (wItem.ID > 0) {
					wNCRTask.ProductName = wItem.ProductName;
					wNCRTask.ModelNo = wItem.ModelNo;
				}
			}

			String wSQL = "";
			if (wNCRTask.getID() <= 0) {
				wNCRTask.Code = GetNextCode(wLoginUser, wErrorCode);
				wSQL = StringUtils.Format("INSERT INTO {0}.ncr_task(FlowType,FlowID,UpFlowID,FollowerID,TaskStepID,"
						+ "Code,CarTypeID,CarNumber,Number,DepartmentID,DescribeInfo,Level,Type,"
						+ "Result,OtherResult,CloseStationID,"
						+ "Status,StatusText,SendType,CloseTime,IPTItemID,CustomerID,LineID,CreateTime,StationID,"
						+ "SubmitTime,OrderID,SendNCRID,ImageList,DutyCarfID,DutyDepartmentID,ProductName,"
						+ "ModelNo,QuestionType,PartNos,StationStaff,AuditApproval,ImageUrl,ProcessID,IsRelease,StepIDs)"
						+ "VALUES(:FlowType,:FlowID,:UpFlowID,:FollowerID,:TaskStepID,:Code,:CarTypeID,:CarNumber,"
						+ ":Number,:DepartmentID,:DescribeInfo," + ":Level,:Type,:Result,:OtherResult,"
						+ ":CloseStationID,:Status,:StatusText,:SendType,:CloseTime,:IPTItemID,:CustomerID,"
						+ ":LineID,now(),:StationID,now(),:OrderID,:SendNCRID,:ImageList,"
						+ ":DutyCarfID,:DutyDepartmentID,:ProductName,:ModelNo,:QuestionType,"
						+ ":PartNos,:StationStaff,:AuditApproval,:ImageUrl,:ProcessID,:IsRelease,:StepIDs);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ncr_task  SET  FlowType = :FlowType,"
								+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,"
								+ "TaskStepID = :TaskStepID,IPTItemID=:IPTItemID," + "CustomerID=:CustomerID,"
								+ "Code = :Code,CarTypeID = :CarTypeID,CarNumber = :CarNumber,Number = :Number,"
								+ "DepartmentID = :DepartmentID,DescribeInfo = :DescribeInfo,Level = :Level,"
								+ "Type = :Type,Result = :Result,OtherResult=:OtherResult," + "SubmitTime=now(),"
								+ "CloseStationID = :CloseStationID,"
								+ "Status = :Status,StatusText = :StatusText,SendType=:SendType,"
								+ "LineID=:LineID,StationID=:StationID,OrderID=:OrderID,"
								+ "CloseTime=:CloseTime,SendNCRID=:SendNCRID,ImageList=:ImageList,"
								+ "DutyCarfID=:DutyCarfID," + "DutyDepartmentID=:DutyDepartmentID,"
								+ "ProductName=:ProductName,ModelNo=:ModelNo,QuestionType=:QuestionType,"
								+ "PartNos=:PartNos,StationStaff=:StationStaff,AuditApproval=:AuditApproval,"
								+ "ImageUrl=:ImageUrl,CreateTime=:CreateTime,"
								+ "ProcessID=:ProcessID,IsRelease=:IsRelease,StepIDs=:StepIDs WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("ID", wNCRTask.ID);
			wParamMap.put("FlowType", wNCRTask.FlowType);
			wParamMap.put("FlowID", wNCRTask.FlowID);
			wParamMap.put("UpFlowID", wNCRTask.UpFlowID);
			if (wNCRTask.FollowerID == null)
				wNCRTask.FollowerID = new ArrayList<Integer>();
			wParamMap.put("FollowerID", StringUtils.Join(",", wNCRTask.FollowerID));
			wParamMap.put("TaskStepID", wNCRTask.TaskStepID);
			wParamMap.put("Code", wNCRTask.Code);
			wParamMap.put("CarTypeID", wNCRTask.CarTypeID);
			wParamMap.put("CarNumber", wNCRTask.CarNumber);
			wParamMap.put("Number", wNCRTask.Number);
			wParamMap.put("DepartmentID", wNCRTask.DepartmentID);
			wParamMap.put("DescribeInfo", wNCRTask.DescribeInfo);
			wParamMap.put("Level", wNCRTask.Level);
			wParamMap.put("Type", wNCRTask.Type);
			wParamMap.put("Result", wNCRTask.Result);
			wParamMap.put("OtherResult", wNCRTask.OtherResult);

			wParamMap.put("CloseStationID", wNCRTask.CloseStationID);
			wParamMap.put("Status", wNCRTask.Status);
			wParamMap.put("StatusText", wNCRTask.StatusText);
			wParamMap.put("SendType", wNCRTask.SendType);
			wParamMap.put("CloseTime", wNCRTask.CloseTime);
			wParamMap.put("IPTItemID", wNCRTask.IPTItemID);
			wParamMap.put("CustomerID", wNCRTask.CustomerID);
			wParamMap.put("LineID", wNCRTask.LineID);
			wParamMap.put("StationID", wNCRTask.StationID);
			wParamMap.put("OrderID", wNCRTask.OrderID);
			wParamMap.put("SendNCRID", wNCRTask.SendNCRID);
			wParamMap.put("ImageList", wNCRTask.ImageList);
			wParamMap.put("DutyCarfID", wNCRTask.DutyCarfID);
			wParamMap.put("DutyDepartmentID", wNCRTask.DutyDepartmentID);

			wParamMap.put("ProductName", wNCRTask.ProductName);
			wParamMap.put("ModelNo", wNCRTask.ModelNo);
			wParamMap.put("QuestionType", wNCRTask.QuestionType);
			wParamMap.put("PartNos", wNCRTask.PartNos);

			wParamMap.put("StationStaff", wNCRTask.StationStaff);
			wParamMap.put("AuditApproval", wNCRTask.AuditApproval);

			wParamMap.put("ImageUrl", wNCRTask.ImageUrl);
			wParamMap.put("CreateTime", wNCRTask.CreateTime);
			wParamMap.put("ProcessID", wNCRTask.ProcessID);

			wParamMap.put("IsRelease", wNCRTask.IsRelease);
			wParamMap.put("StepIDs", wNCRTask.StepIDs);

			KeyHolder keyHolder = new GeneratedKeyHolder();

			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wNCRTask.getID() <= 0) {
				wNCRTask.setID(keyHolder.getKey().intValue());
			}
			wResult = wNCRTask;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		NCRTask wResult = new NCRTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			wResult = this.SelectByID(wLoginUser, wTaskID, wErrorCode);
			if (wResult == null || wResult.ID == 0)
				return wResult;
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<Integer> NCR_GetItemListByProcessID(BMSEmployee wLoginUser, int wTaskStepID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResultList = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = null;
			wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			String wSQL = StringUtils.Format(" SELECT IPTItemID FROM {0}.ncr_task WHERE  TaskStepID={1} "
					+ "UNION ALL (SELECT IPTItemID FROM {0}.ncr_sendtask where TaskStepID={1}  and Status in ({2}));",
					wInstance.Result, wTaskStepID,
					NCRStatus.ToCheckWrite.getValue() + "," + NCRStatus.ToWorkAreaAudit.getValue());

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wSQL = this.DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wReader : wQueryResult) {
				int wItemID = StringUtils.parseInt(wReader.get("IPTItemID"));
				wResultList.add(wItemID);
			}
			wResultList = wResultList.stream().distinct().collect(Collectors.toList());
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * 根据工位获取工区主管和调度集合
	 */
	public List<Integer> GetLeaderIDList(BMSEmployee wLoginUser, int wPartID, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT LeaderIDList,ScheduleIDList FROM {0}.lfs_workareachecker "
					+ "where WorkAreaID in (SELECT WorkAreaID FROM {0}.lfs_workareastation where StationID=:StationID);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				String wLeaderIDList = StringUtils.parseString(wReader.get("LeaderIDList"));
				String wScheduleIDList = StringUtils.parseString(wReader.get("ScheduleIDList"));
				if (StringUtils.isNotEmpty(wLeaderIDList)) {
					String[] wStrs = wLeaderIDList.split(";");
					for (String wStr : wStrs) {
						int wUserID = StringUtils.parseInt(wStr);
						if (wUserID <= 0) {
							continue;
						}
						wResult.add(wUserID);
					}
				}
				if (StringUtils.isNotEmpty(wScheduleIDList)) {
					String[] wStrs = wScheduleIDList.split(";");
					for (String wStr : wStrs) {
						int wUserID = StringUtils.parseInt(wStr);
						if (wUserID <= 0) {
							continue;
						}
						wResult.add(wUserID);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<NCRTask> SelectListByCarInfo(BMSEmployee wLoginUser, OMSOrder wOrder, OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (StringUtils.isEmpty(wOrder.PartNo) || !wOrder.PartNo.contains("#")) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ncr_task WHERE  1=1 "
							+ " and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
							+ " and ( :wLineID <= 0 or :wLineID = LineID ) "
							+ " and ( :wCarTypeID <= 0 or :wCarTypeID = CarTypeID ) "
							+ " and ( CarNumber like ''%{1}%'' ) " + " and ( Status >0 );",
					wInstance.Result, wOrder.PartNo.split("#")[1]);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCustomerID", wOrder.CustomerID);
			wParamMap.put("wLineID", wOrder.LineID);
			wParamMap.put("wCarTypeID", wOrder.ProductID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<NCRTask> SelectListByCarInfo(BMSEmployee wLoginUser, int wProductID, String wCarNumber,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ncr_task WHERE  1=1 " + " and ( :wCarTypeID <= 0 or :wCarTypeID = CarTypeID ) "
							+ " and ( CarNumber like ''%{1}%'' ) " + " and ( Status >0 );",
					wInstance.Result, wCarNumber);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCarTypeID", wProductID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public NCRCarInfo SelectCarInfo(BMSEmployee wLoginUser, int productID, String partNo,
			OutResult<Integer> wErrorCode) {
		NCRCarInfo wResult = new NCRCarInfo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (StringUtils.isEmpty(partNo) || !partNo.contains("#"))
				return wResult;

			String wSQL = StringUtils.Format(
					"select (SELECT count(*) FROM {0}.ncr_task where "
							+ "CarTypeID=:CarTypeID and Status in (12,21,22) and CarNumber like ''%{1}%'') FQTYDone,"
							+ "	   (SELECT count(*) FROM {0}.ncr_task where CarTypeID=:CarTypeID "
							+ "and Status not in (0,12,21,22) and CarNumber like ''%{1}%'') FQTYToDo;",
					wInstance.Result, partNo.split("#")[1]);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("CarTypeID", productID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.FQTYDone = StringUtils.parseInt(wReader.get("FQTYDone"));
				wResult.FQTYToDo = StringUtils.parseInt(wReader.get("FQTYToDo"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
