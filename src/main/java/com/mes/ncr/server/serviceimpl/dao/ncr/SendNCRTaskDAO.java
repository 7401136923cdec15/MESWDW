package com.mes.ncr.server.serviceimpl.dao.ncr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.ncr.MESStatusDictionary;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

public class SendNCRTaskDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SendNCRTaskDAO.class);

	private static SendNCRTaskDAO Instance = null;

	/**
	 * 条件查询NCR任务集合
	 * 
	 * @param wID
	 * @return
	 */
	public SendNCRTask SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SendNCRTask wResult = new SendNCRTask();
		try {
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<SendNCRTask> wResultList = SelectList(wLoginUser, wIDList, null, null, wErrorCode);
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
	public List<SendNCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SendNCRTask> wResultList = new ArrayList<SendNCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) > 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ncr_sendtask WHERE  1=1 "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ " and (:wID is null or :wID = '''' or ID in ({1}));", wInstance.Result,
					wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<SendNCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wFlowID,
			String wCode, int wCarTypeID, String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID,
			int wUpFlowID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList,
			OutResult<Integer> wErrorCode) {
		return SelectList(wLoginUser, wIDList, wFlowType, wFlowID, wCode, wCarTypeID, wCarNumber, wOrderID, wCustomerID,
				wLineID, wStationID, wUpFlowID, wStartTime, wEndTime, wStatusIDList, null, wErrorCode);
	}

	public List<SendNCRTask> SelectList(BMSEmployee wLoginUser, int wFlowType, int wProductID, String wCarNumber,
			int wOrderID, int wCustomerID, int wLineID, int wPartID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStatusIDList, List<Integer> wNoStatusIDList, OutResult<Integer> wErrorCode) {
		return SelectList(wLoginUser, null, wFlowType, -1, "", wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
				wPartID, -1, wStartTime, wEndTime, wStatusIDList, wNoStatusIDList, wErrorCode);
	}

	// Level SendType
	/**
	 * 条件查询NCR任务集合
	 * 
	 * @param wIDList
	 * @param wTaskID      子流程ID
	 * @param wCode
	 * @param wCarTypeID
	 * @param wCarNumber
	 * @param wStartTime
	 * @param wEndTime
	 * @param wStateIDList
	 * @return
	 */
	private List<SendNCRTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wFlowID,
			String wCode, int wCarTypeID, String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID,
			int wUpFlowID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList,
			List<Integer> wNoStatusIDList, OutResult<Integer> wErrorCode) {
		List<SendNCRTask> wResultList = new ArrayList<SendNCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wStatusIDList == null)
				wStatusIDList = new ArrayList<Integer>();
			if (wNoStatusIDList == null)
				wNoStatusIDList = new ArrayList<Integer>();
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
			wStatusIDList.removeIf(p -> p < 0);
			wNoStatusIDList.removeIf(p -> p < 0);
			String wSQL = StringUtils.Format("SELECT * FROM {0}.ncr_sendtask WHERE  1=1 "
					+ " and ( :wFlowType <= 0 or :wFlowType = FlowType )"
					+ " and ( :wFlowID <= 0 or :wFlowID = FlowID ) " + "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ " and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
					+ " and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ " and ( :wStationID <= 0 or :wStationID = StationID ) "
					+ " and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
					+ " and ( :wCarTypeID <= 0 or :wCarTypeID = CarTypeID ) "
					+ " and (:wID is null or :wID = '''' or ID in ({1}))"
					+ " and ( :wCarNumber is null or :wCarNumber = '''' or :wCarNumber = CarNumber ) "
					+ " and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  CreateTime ) "
					+ " and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ " and ( :wStatus = '''' or Status in ({2})) and ( :wNoStatus = '''' or Status not in ({3}));",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0",
					wStatusIDList.size() > 0 ? StringUtils.Join(",", wStatusIDList) : "0",
					wNoStatusIDList.size() > 0 ? StringUtils.Join(",", wNoStatusIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wFlowType", wFlowType);
			wParamMap.put("wFlowID", wFlowID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wCarTypeID", wCarTypeID);
			wParamMap.put("wCarNumber", wCarNumber);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatusIDList));
			wParamMap.put("wNoStatus", StringUtils.Join(",", wNoStatusIDList));
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wUpFlowID", wUpFlowID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			wResultList = GetResultList(wLoginUser, wQueryResult, wErrorCode);

			// ①根据严工要求，处理状态文本(简短显示)
			HandleStatusText(wResultList);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 处理状态文本
	 */
	private void HandleStatusText(List<SendNCRTask> wResultList) {
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
			wMap.put("待工区主管审批", "待审批");
			wMap.put("待提交不合格评审申请", "待提交");

			// 查询状态字典
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MESStatusDictionary> wList = MESStatusDictionaryDAO.getInstance().SelectList(BaseDAO.SysAdmin, -1,
					1003, wErrorCode);
			for (MESStatusDictionary wMESStatusDictionary : wList) {
				if (!wMap.containsKey(wMESStatusDictionary.Key)) {
					wMap.put(wMESStatusDictionary.Key, wMESStatusDictionary.Value);
				}
			}

			for (SendNCRTask wSendNCRTask : wResultList) {
				if (wMap.containsKey(wSendNCRTask.StatusText)) {
					wSendNCRTask.StatusText = wMap.get(wSendNCRTask.StatusText);
				}
				if (wSendNCRTask.StatusText.contains("待审批,")) {
					wSendNCRTask.StatusText = "待审批";
				}
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 解析结果
	 * 
	 * @param wResultList
	 * @param wQueryResult
	 */
	private List<SendNCRTask> GetResultList(BMSEmployee wLoginUser, List<Map<String, Object>> wQueryResult,
			OutResult<Integer> wErrorCode) {
		List<SendNCRTask> wResultList = new ArrayList<SendNCRTask>();
		try {
			if (wQueryResult == null || wQueryResult.size() <= 0)
				return wResultList;

			for (Map<String, Object> wReader : wQueryResult) {
				SendNCRTask wNCRTask = new SendNCRTask();
				wNCRTask.ID = StringUtils.parseInt(wReader.get("ID"));
				wNCRTask.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wNCRTask.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wNCRTask.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wNCRTask.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wNCRTask.Code = StringUtils.parseString(wReader.get("Code"));
				wNCRTask.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				wNCRTask.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
				wNCRTask.Number = StringUtils.parseInt(wReader.get("Number"));
				wNCRTask.DepartmentID = StringUtils.parseInt(wReader.get("DepartmentID"));
				wNCRTask.DescribeInfo = StringUtils.parseString(wReader.get("DescribeInfo"));
				wNCRTask.Status = StringUtils.parseInt(wReader.get("Status"));
				wNCRTask.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wNCRTask.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wNCRTask.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wNCRTask.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wNCRTask.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wNCRTask.TaskIPTID = StringUtils.parseInt(wReader.get("TaskIPTID"));
				wNCRTask.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wNCRTask.IPTItemID = StringUtils.parseInt(wReader.get("IPTItemID"));
				wNCRTask.NCRID = StringUtils.parseInt(wReader.get("NCRID"));
				wNCRTask.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wNCRTask.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wNCRTask.ImageList = StringUtils.parseString(wReader.get("ImageList"));
				wNCRTask.ProductName = StringUtils.parseString(wReader.get("ProductName"));
				wNCRTask.ModelNo = StringUtils.parseString(wReader.get("ModelNo"));
				wNCRTask.ImageUrl = StringUtils.parseString(wReader.get("ImageUrl"));
				wNCRTask.LogisticsID = StringUtils.parseString(wReader.get("LogisticsID"));
				wNCRTask.AreaID = StringUtils.parseString(wReader.get("AreaID"));
				wNCRTask.CheckerID = StringUtils.parseString(wReader.get("CheckerID"));
				wNCRTask.Checkers = GetNames(StringUtils.parseIntList(wNCRTask.CheckerID.split(",")));

				wNCRTask.LogisticsName = WDWConstans.GetBMSEmployeeName(StringUtils.parseInt(wNCRTask.LogisticsID));

				String[] wItems = wNCRTask.AreaID.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wItem : wItems) {
					int wName = StringUtils.parseInt(wItem);
					String wMyName = WDWConstans.GetBMSEmployeeName(wName);
					if (StringUtils.isNotEmpty(wMyName))
						wNames.add(wMyName);
				}
				wNCRTask.AreaName = StringUtils.Join(",", wNames);

				wNCRTask.UpFlowName = WDWConstans.GetBMSEmployeeName(wNCRTask.UpFlowID);
				wNCRTask.Department = WDWConstans.GetBMSDepartmentName(wNCRTask.DepartmentID);
				wNCRTask.CarType = WDWConstans.GetFPCProductNo(wNCRTask.CarTypeID);
				wNCRTask.CustomerName = WDWConstans.GetCRMCustomerName(wNCRTask.CustomerID);
				wNCRTask.StationName = WDWConstans.GetFPCPartName(wNCRTask.StationID);
				wNCRTask.LineName = WDWConstans.GetFMCLineName(wNCRTask.LineID);

				wNCRTask.IsRelease = StringUtils.parseInt(wReader.get("IsRelease"));
				wNCRTask.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
				wNCRTask.ClosePartID = StringUtils.parseInt(wReader.get("ClosePartID"));
				wNCRTask.ClosePartName = WDWConstans.GetFPCPartName(wNCRTask.ClosePartID);
				wNCRTask.StepNames = GetStepNames(wNCRTask.StepIDs);

				wNCRTask.IsReleaseName = wNCRTask.IsRelease == 1 ? "是" : "否";

				wResultList.add(wNCRTask);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 获取人名
	 */
	private String GetNames(List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.forEach(p -> {
				if (StringUtils.isNotEmpty(WDWConstans.GetBMSEmployeeName(p))) {
					wNames.add(WDWConstans.GetBMSEmployeeName(p));
				}
			});

			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
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
					"select count(*)+1 as Number from {0}.ncr_sendtask where CreateTime > :wSTime and CreateTime < :wETime;",
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

			wResult = StringUtils.Format("SE{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 创建申请任务
	 * 
	 * @param wSenderID
	 * @return
	 */
	public SendNCRTask NCR_CreateTask(BMSEmployee wLoginUser, BPMEventModule wModuleID, OutResult<Integer> wErrorCode) {
		SendNCRTask wResult = new SendNCRTask();
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
			this.BPM_UpdateTask(wLoginUser, wResult, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	private SendNCRTaskDAO() {
		super();
	}

	public static SendNCRTaskDAO getInstance() {
		if (Instance == null)
			Instance = new SendNCRTaskDAO();
		return Instance;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<SendNCRTask> wResult = new ArrayList<SendNCRTask>();
		try {

			// 获取所有任务消息 模块为移车的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCNCR.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.SCNCR.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.SCNCR.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			// List<> 查询消息

			Map<Integer, SendNCRTask> wNCRTaskMap = new HashMap<Integer, SendNCRTask>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				Calendar wCalendar = Calendar.getInstance();
				wCalendar.set(2010, 1, 1);
				List<SendNCRTask> wNCRTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null, wErrorCode);

				wNCRTaskMap = wNCRTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			SendNCRTask wNCRTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wNCRTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wNCRTaskTemp = CloneTool.Clone(wNCRTaskMap.get((int) wBFCMessage.getMessageID()), SendNCRTask.class);
				wNCRTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wNCRTaskTemp);
			}

			wResult.sort(Comparator.comparing(SendNCRTask::getSubmitTime).reversed());
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
		List<SendNCRTask> wResult = new ArrayList<SendNCRTask>();
		wErrorCode.set(0);
		try {

			List<SendNCRTask> wNCRTaskList = new ArrayList<SendNCRTask>();
			// 获取所有任务消息 模块为不合格评审的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCNCR.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCNCR.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			if (wTaskIDList != null && wTaskIDList.size() > 0)
				wNCRTaskList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime,
						wErrorCode);

			// 所有未完成的任务
			// List<> 查询消息

			List<SendNCRTask> wNCRTaskUndoneList = new ArrayList<SendNCRTask>();

			List<Integer> wTaskIDUndoneList = wNCRTaskUndoneList.stream().map(p -> (int) p.ID).distinct()
					.collect(Collectors.toList());
			wTaskIDUndoneList.removeAll(wTaskIDList);
			wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.SCNCR.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class);

			wTaskIDUndoneList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());
			for (SendNCRTask wNCRTask : wNCRTaskUndoneList) {
				if (wTaskIDUndoneList.contains(wNCRTask.ID))
					wNCRTaskList.add(wNCRTask);
			}

			wNCRTaskList.sort(Comparator.comparing(SendNCRTask::getSubmitTime).reversed());

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
			wResult = new ArrayList<BPMTaskBase>(SendNCRTaskDAO.getInstance().SelectList(wLoginUser, null, -1, -1, "",
					-1, "", -1, -1, -1, -1, wResponsorID, wStartTime, wEndTime, null, wErrorCode));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		SendNCRTask wResult = new SendNCRTask();
		try {

			if (wTask == null) {
				wErrorCode.set(MESException.Parameter.getValue());
				return wResult;
			}
			ServiceResult<String> wInstance = null;

			if (wTask.getID() <= 0) {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 500801);
			} else {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			}
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			SendNCRTask wSendNCRTask = (SendNCRTask) wTask;
			String wSQL = "";
			if (wSendNCRTask.getID() <= 0) {
				wSendNCRTask.Code = GetNextCode(wLoginUser, wErrorCode);
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ncr_sendtask(FlowType,FlowID,UpFlowID,FollowerID,Code,Status,StatusText,"
								+ "CreateTime,SubmitTime,OrderID,CustomerID,LineID,StationID,CarTypeID,CarNumber,Number,DepartmentID,"
								+ "DescribeInfo,NCRID,ImageList,TaskStepID,IPTItemID,TaskIPTID,ProductName,ModelNo,ImageUrl,"
								+ "IsRelease,StepIDs,ClosePartID,LogisticsID,AreaID,CheckerID)"
								+ "VALUES(:FlowType,:FlowID,:UpFlowID,:FollowerID,:Code,:Status,:StatusText,now(),now()"
								+ ",:OrderID,:CustomerID,:LineID,:StationID,:CarTypeID,:CarNumber,"
								+ ":Number,:DepartmentID,:DescribeInfo,:NCRID,:ImageList,:TaskStepID,"
								+ ":IPTItemID,:TaskIPTID,:ProductName,:ModelNo,:ImageUrl,:IsRelease,:StepIDs,:ClosePartID,:LogisticsID,:AreaID,:CheckerID);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.ncr_sendtask  SET  FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,"
						+ "Code = :Code,Status = :Status,StatusText = :StatusText,CreateTime = :CreateTime,"
						+ "SubmitTime=now(),OrderID=:OrderID, CustomerID = :CustomerID,LineID = :LineID,"
						+ "StationID = :StationID,CarTypeID = :CarTypeID,CarNumber = :CarNumber,Number = :Number,"
						+ "DepartmentID = :DepartmentID,DescribeInfo = :DescribeInfo,NCRID = :NCRID,"
						+ "ImageList = :ImageList,TaskStepID = :TaskStepID," + "IPTItemID = :IPTItemID,"
						+ "TaskIPTID = :TaskIPTID,ProductName=:ProductName,ModelNo=:ModelNo,ImageUrl=:ImageUrl,"
						+ "IsRelease=:IsRelease,StepIDs=:StepIDs,ClosePartID=:ClosePartID,LogisticsID=:LogisticsID,AreaID=:AreaID,CheckerID=:CheckerID WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("ID", wSendNCRTask.ID);
			wParamMap.put("FlowType", wSendNCRTask.FlowType);
			wParamMap.put("FlowID", wSendNCRTask.FlowID);
			wParamMap.put("UpFlowID", wSendNCRTask.UpFlowID);
			if (wSendNCRTask.FollowerID == null)
				wSendNCRTask.FollowerID = new ArrayList<Integer>();
			wParamMap.put("FollowerID", StringUtils.Join(",", wSendNCRTask.FollowerID));
			wParamMap.put("Code", wSendNCRTask.Code);
			wParamMap.put("CarTypeID", wSendNCRTask.CarTypeID);
			wParamMap.put("CarNumber", wSendNCRTask.CarNumber);
			wParamMap.put("Number", wSendNCRTask.Number);
			wParamMap.put("DepartmentID", wSendNCRTask.DepartmentID);
			wParamMap.put("DescribeInfo", wSendNCRTask.DescribeInfo);
			wParamMap.put("Status", wSendNCRTask.Status);
			wParamMap.put("StatusText", wSendNCRTask.StatusText);
			wParamMap.put("CustomerID", wSendNCRTask.CustomerID);
			wParamMap.put("LineID", wSendNCRTask.LineID);
			wParamMap.put("StationID", wSendNCRTask.StationID);
			wParamMap.put("OrderID", wSendNCRTask.OrderID);
			wParamMap.put("NCRID", wSendNCRTask.NCRID);
			wParamMap.put("TaskIPTID", wSendNCRTask.TaskIPTID);
			wParamMap.put("TaskStepID", wSendNCRTask.TaskStepID);
			wParamMap.put("IPTItemID", wSendNCRTask.IPTItemID);
			wParamMap.put("ImageList", wSendNCRTask.ImageList);
			wParamMap.put("ProductName", wSendNCRTask.ProductName);
			wParamMap.put("ModelNo", wSendNCRTask.ModelNo);
			wParamMap.put("ImageUrl", wSendNCRTask.ImageUrl);
			wParamMap.put("IsRelease", wSendNCRTask.IsRelease);
			wParamMap.put("StepIDs", wSendNCRTask.StepIDs);
			wParamMap.put("ClosePartID", wSendNCRTask.ClosePartID);
			wParamMap.put("LogisticsID", wSendNCRTask.LogisticsID);
			wParamMap.put("AreaID", wSendNCRTask.AreaID);
			wParamMap.put("CreateTime", wSendNCRTask.CreateTime);
			wParamMap.put("CheckerID", wSendNCRTask.CheckerID);

			KeyHolder keyHolder = new GeneratedKeyHolder();

			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSendNCRTask.getID() <= 0) {
				wSendNCRTask.setID(keyHolder.getKey().intValue());
			}
			wResult = wSendNCRTask;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		SendNCRTask wResult = new SendNCRTask();
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

	public int SelectOrderID(BMSEmployee wLoginUser, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT OrderID FROM {0}.fpc_routepart where RouteID "
							+ "in (SELECT RouteID FROM {1}.oms_order where ID=:wOrderID) and PartID=:wPartID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("OrderID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> SelectPartIDList(BMSEmployee wLoginUser, int wOrderID, int wOrderNum,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT PartID FROM {0}.fpc_routepart where RouteID "
					+ "in (SELECT RouteID FROM {1}.oms_order where ID=:wOrderID) "
					+ "and OrderID > :wOrderNum order by OrderID asc;", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wOrderNum", wOrderNum);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wPartID = StringUtils.parseInt(wReader.get("PartID"));
				wResult.add(wPartID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据班组、工位获取检验员
	 */
	public List<Integer> GetCheckerIDList(BMSEmployee wLoginUser, int departmentID, int stationID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT CheckerList FROM {0}.bms_workcharge where ClassID=:ClassID and StationID=:StationID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ClassID", departmentID);
			wParamMap.put("StationID", stationID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				String wCheckerList = StringUtils.parseString(wReader.get("CheckerList"));

				String[] wStrs = wCheckerList.split(",");
				for (String wStr : wStrs) {
					int wUserID = StringUtils.parseInt(wStr);
					if (wUserID > 0) {
						wResult.add(wUserID);
					}
				}

			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取质检班班长
	 */
	public List<Integer> GetCheckMonitorIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.mbs_user where Position=46 and Active=1;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				wResult.add(wID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据消息ID清除消息
	 */
	public int ClearMessage(BMSEmployee wLoginUser, int wMessageID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("update {0}.bfc_message set Active=3 where ID=:ID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMessageID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
