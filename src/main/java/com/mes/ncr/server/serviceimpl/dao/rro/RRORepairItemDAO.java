package com.mes.ncr.server.serviceimpl.dao.rro;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.mes.ncr.server.service.po.bms.BMSRoleItem;
import com.mes.ncr.server.service.po.bms.BMSWorkCharge;
import com.mes.ncr.server.service.po.bpm.BPMActivitiProcessInstance;
import com.mes.ncr.server.service.po.bpm.BPMActivitiTask;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.ncr.server.service.po.ncr.MESStatusDictionary;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RROPart;
import com.mes.ncr.server.service.po.rro.RROPartNo;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.po.rro.RROTaskTypes;
import com.mes.ncr.server.service.po.sfc.SFCStationPerson;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.APSLOCOServiceImpl;
import com.mes.ncr.server.serviceimpl.BPMServiceImpl;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.LFSServiceImpl;
import com.mes.ncr.server.serviceimpl.NCRServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.ncr.server.serviceimpl.dao.ncr.MESStatusDictionaryDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

public class RRORepairItemDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(RRORepairItemDAO.class);

	private static RRORepairItemDAO Instance = null;

	/**
	 * 创建返工单任务
	 * 
	 * @param wSenderID
	 * @return
	 */
	public synchronized RROItemTask RRO_CreateItemTask(BMSEmployee wLoginUser, BPMEventModule wModuleID,
			OutResult<Integer> wErrorCode) {
		RROItemTask wResult = new RROItemTask();
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
			wResult.CreateTime = Calendar.getInstance();
			wResult.SubmitTime = Calendar.getInstance();
			wResult.Status = 0;
			wResult.Code = RRORepairItemDAO.getInstance().GetNextCode(wLoginUser, wErrorCode);
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

	public void RRO_SetItemTaskCode1(BMSEmployee wLoginUser, RROItemTask wRROItemTask, OutResult<Integer> wErrorCode) {

		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}
			if (wRROItemTask.TaskID > 0) {
				return;
			}
			RROTask wRROTask = RROTaskDAO.getInstance().SelectTask(wLoginUser, wRROItemTask.OrderID,
					wRROItemTask.StationID, wRROItemTask.IsDelivery, wLoginUser.getID(), wRROItemTask.CarTypeID,
					wRROItemTask.CarNumber, wErrorCode);
			if (wRROTask != null && wRROTask.ID > 0) {
				wRROItemTask.TaskID = wRROTask.ID;
				List<RROItemTask> wRROItemTaskList = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1,
						wRROTask.ID, null, -1, -1, -1, -1, -1, null, null, -1, wErrorCode);
				if (wRROItemTaskList != null && wRROItemTaskList.size() <= 9) {
					wRROItemTask.Code = StringUtils.Format("{0}.0{1}", wRROTask.Code, wRROItemTaskList.size() + 1);
				} else {
					wRROItemTask.Code = StringUtils.Format("{0}.{1}", wRROTask.Code, wRROItemTaskList.size() + 1);
				}
				wRROTask.UpFlowTime = Calendar.getInstance();

				RROTaskDAO.getInstance().Update(wLoginUser, wRROTask, wErrorCode);
			} else {
				RROTask wNewRROTask = RROTaskDAO.getInstance().RRO_CreateTask(wLoginUser, wRROItemTask);
				if (wNewRROTask != null && wNewRROTask.ID > 0) {
					wRROItemTask.TaskID = wNewRROTask.ID;
					wRROItemTask.Code = wNewRROTask.Code + ".01";

				}
			}

			this.BPM_UpdateTask(wLoginUser, wRROItemTask, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
	}

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public void DeleteList(BMSEmployee wLoginUser, List<RROItemTask> wList, OutResult<Integer> wErrorCode) {
		try {
			if (wList == null || wList.size() <= 0)
				return;

			List<String> wIDList = new ArrayList<String>();
			for (RROItemTask wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from rro_repairitem WHERE ID IN({0}) ;",
					String.join(",", wIDList));
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public RROItemTask SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		RROItemTask wResult = new RROItemTask();
		try {
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			List<RROItemTask> wResultList = SelectList(wLoginUser, wIDList, -1, -1, -1, null, -1, -1, -1, -1, -1, null,
					null, -1, wErrorCode);
			if (wResultList != null && wResultList.size() > 0)
				wResult = wResultList.get(0);

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<RROItemTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wUpFlowID,
			int wTaskID, List<Integer> wStatusList, int wWorkAreaID, int wNCRID, int wTaskStepID, int wOrderID,
			int wStationID, Calendar wStartTime, Calendar wEndTime, int wCustomerID, OutResult<Integer> wErrorCode) {

		return SelectList(wLoginUser, wIDList, wFlowType, wUpFlowID, wTaskID, wStatusList, null, wWorkAreaID, wNCRID,
				wTaskStepID, wOrderID, -1, wCustomerID, -1, "", -1, wStationID, wStartTime, wEndTime, wErrorCode);
	}

	public List<RROItemTask> SelectList(BMSEmployee wLoginUser, int wFlowType, int wOrderID, int wLineID,
			int wCustomerID, int wProductID, String wCarNumber, int wIsDelivery, int wStationID, int wWorkAreaID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusList, List<Integer> wNoStatusList,
			OutResult<Integer> wErrorCode) {

		return this.SelectList(wLoginUser, null, wFlowType, -1, -1, wStatusList, wNoStatusList, wWorkAreaID, -1, -1,
				wOrderID, wLineID, wCustomerID, wProductID, wCarNumber, wIsDelivery, wStationID, wStartTime, wEndTime,
				wErrorCode);
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	private List<RROItemTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wUpFlowID,
			int wTaskID, List<Integer> wStatusList, List<Integer> wNoStatusList, int wWorkAreaID, int wNCRID,
			int wTaskStepID, int wOrderID, int wLineID, int wCustomerID, int wProductID, String wCarNumber,
			int wIsDelivery, int wStationID, Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<RROItemTask> wResultList = new ArrayList<RROItemTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResultList;
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();

			if (wStatusList == null)
				wStatusList = new ArrayList<Integer>();
			if (wNoStatusList == null)
				wNoStatusList = new ArrayList<Integer>();

			wIDList.removeIf(p -> p <= 0);
			wStatusList.removeIf(p -> p < 0);
			wNoStatusList.removeIf(p -> p < 0);

			String wSQL = MessageFormat.format(
					"SELECT t.*,t1.Code,t1.CarTypeID, t1.CarNumber,t1.CustomerID,t1.LineID,t1.CheckName,t1.IsDelivery "
							+ " FROM {0}.rro_repairitem t left join {0}.rro_task t1 on t1.ID=t.TaskID  "
							+ " WHERE  1=1  " + "and ( :wTaskID <= 0 or :wTaskID = t.TaskID ) "
							+ " and ( :wID = '''' or t.ID in ({1}))"
							+ " and ( :wWorkAreaID <= 0 or :wWorkAreaID = t.WorkAreaID ) "
							+ " and ( :wFlowType <= 0 or :wFlowType = t.FlowType ) "
							+ " and ( :wOrderID <= 0 or :wOrderID = t.OrderID ) "
							+ " and ( :wLineID <= 0 or :wLineID = t1.LineID ) "
							+ " and ( :wProductID <= 0 or :wProductID = t1.CarTypeID ) "
							+ " and ( :wCarNumber ='''' or :wCarNumber = t1.CarNumber ) "
							+ " and ( :wIsDelivery <=0 or :wIsDelivery = t1.IsDelivery ) "
							+ " and ( :wStationID <= 0 or :wStationID = t.StationID ) "
							+ " and ( :wUpFlowID <= 0 or :wUpFlowID = t.UpFlowID ) "
							+ " and ( :wCustomerID <= 0 or :wCustomerID = t.CustomerID ) "
							+ " and ( :wTaskStepID <= 0 or :wTaskStepID = t.TaskStepID ) "
							+ " and ( :wStatus = '''' or t.Status in ({2}) ) "
							+ " and ( :wNoStatus = '''' or t.Status not in ({3}) ) "
							+ " and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or t.CreateTime>= :wStartTime)"
							+ " and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or t.CreateTime<= :wEndTime)"
							+ " and ( :wNCRID <= 0 or :wNCRID = t.NCRID );",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0",
					wStatusList.size() > 0 ? StringUtils.Join(",", wStatusList) : "0",
					wNoStatusList.size() > 0 ? StringUtils.Join(",", wNoStatusList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wTaskID", wTaskID);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatusList));
			wParamMap.put("wNoStatus", StringUtils.Join(",", wNoStatusList));
			wParamMap.put("wWorkAreaID", wWorkAreaID);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wCarNumber", wCarNumber);
			wParamMap.put("wIsDelivery", wIsDelivery);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wNCRID", wNCRID);
			wParamMap.put("wFlowType", wFlowType);
			wParamMap.put("wTaskStepID", wTaskStepID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wCustomerID", wCustomerID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				RROItemTask wItem = new RROItemTask();
				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.TaskID = StringUtils.parseInt(wReader.get("TaskID"));
				wItem.IsDelivery = StringUtils.parseInt(wReader.get("IsDelivery"));
				wItem.IsDeliveryName = RROTaskTypes.getEnumType(wItem.IsDelivery).getLable();
				wItem.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				wItem.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.Content = StringUtils.parseString(wReader.get("Content"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wItem.WorkAreaID = StringUtils.parseInt(wReader.get("WorkAreaID"));
				wItem.TeamID = StringUtils.parseInt(wReader.get("TeamID"));

				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.IsStatus = StringUtils.parseInt(wReader.get("IsStatus"));
				wItem.IsSendNCR = StringUtils.parseInt(wReader.get("IsSendNCR"));
				wItem.NCRID = StringUtils.parseInt(wReader.get("NCRID"));
				wItem.ItemLogo = StringUtils.parseString(wReader.get("ItemLogo"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.IPTItemID = StringUtils.parseInt(wReader.get("IPTItemID"));

				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.ProcessID = StringUtils.parseInt(wReader.get("ProcessID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.CheckName = StringUtils.parseString(wReader.get("CheckName"));
				wItem.ImageUrl = StringUtils.parseString(wReader.get("ImageUrl"));
				wItem.IsDelivery = StringUtils.parseInt(wReader.get("IsDelivery"));
				wItem.SupplierID = StringUtils.parseInt(wReader.get("SupplierID"));

				wItem.UpFlowName = WDWConstans.GetBMSEmployeeName(wItem.UpFlowID);
				wItem.CarTypeName = WDWConstans.GetFPCProductNo(wItem.CarTypeID);
				wItem.CustomerName = WDWConstans.GetCRMCustomerName(wItem.CustomerID);
				wItem.LineName = WDWConstans.GetFMCLineName(wItem.LineID);
				wItem.StationName = WDWConstans.GetFPCPartName(wItem.StationID);
				wItem.WorkAreaName = WDWConstans.GetBMSDepartmentName(wItem.WorkAreaID);
				wItem.TeamName = WDWConstans.GetBMSDepartmentName(wItem.TeamID);
				wItem.OperatorName = WDWConstans.GetBMSEmployeeName(wItem.OperatorID);
				wItem.ProcesName = WDWConstans.GetFPCStepName(wItem.ProcessID);
				wItem.SupplierName = WDWConstans.GetSCMSupplierName(wItem.SupplierID);

				if (wItem.TaskID <= 0 && wItem.OrderID > 0) {
					OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.OrderID)
							.Info(OMSOrder.class);
					if (wOrder != null && wOrder.ID > 0) {
						wItem.CarTypeID = wOrder.ProductID;
						wItem.CarTypeName = wOrder.ProductNo;
						wItem.LineID = wOrder.LineID;
						wItem.LineName = wOrder.LineName;
						wItem.CarNumber = wOrder.PartNo.contains("#") ? wOrder.PartNo.split("#")[1] : "";
					}
				}

				wResultList.add(wItem);
			}

			// 处理状态文本
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
	private void HandleStatusText(List<RROItemTask> wResultList) {
		try {
			Map<String, String> wMap = new HashMap<String, String>();

			// 查询状态字典
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<MESStatusDictionary> wList = MESStatusDictionaryDAO.getInstance().SelectList(BaseDAO.SysAdmin,
					new ArrayList<Integer>(Arrays.asList(3008, 5010, 1010, 2011)), wErrorCode);

			for (MESStatusDictionary wMESStatusDictionary : wList) {
				if (!wMap.containsKey(wMESStatusDictionary.Key)) {
					wMap.put(wMESStatusDictionary.Key, wMESStatusDictionary.Value);
				}
			}

			for (RROItemTask wSendNCRTask : wResultList) {
				if (wMap.containsKey(wSendNCRTask.StatusText)) {
					wSendNCRTask.StatusText = wMap.get(wSendNCRTask.StatusText);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private RRORepairItemDAO() {
		super();
	}

	public static RRORepairItemDAO getInstance() {
		if (Instance == null)
			Instance = new RRORepairItemDAO();
		return Instance;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<RROItemTask> wResult = new ArrayList<RROItemTask>();
		try {

			// 获取所有任务消息 模块为返工的
			List<BFCMessage> wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCRepair.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCRepair.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCRepair.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.QTRepair.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.QTRepair.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.QTRepair.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.TechRepair.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.TechRepair.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.TechRepair.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.CKRepair.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.CKRepair.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.CKRepair.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			// List<> 查询消息

			Map<Integer, RROItemTask> wNCRTaskMap = new HashMap<Integer, RROItemTask>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				Calendar wCalendar = Calendar.getInstance();
				wCalendar.set(2001, 1, 1);
				List<RROItemTask> wRROItemTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, -1, -1, -1, null, -1,
						-1, -1, -1, -1, null, null, -1, wErrorCode);

				wNCRTaskMap = wRROItemTaskListTemp.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RROItemTask wRROItemTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wNCRTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wRROItemTaskTemp = CloneTool.Clone(wNCRTaskMap.get((int) wBFCMessage.getMessageID()),
						RROItemTask.class);

				wRROItemTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wRROItemTaskTemp);
			}

			wResult.sort(Comparator.comparing(RROItemTask::getSubmitTime).reversed());
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
		List<RROItemTask> wResult = new ArrayList<RROItemTask>();
		wErrorCode.set(0);
		try {
			List<RROItemTask> wRROItemTaskList = new ArrayList<RROItemTask>();
			// 获取所有任务消息 模块为返工的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCRepair.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCRepair.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.QTRepair.getValue(), -1,
									BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.QTRepair.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechRepair.getValue(),
									-1, BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechRepair.getValue(),
									-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.CKRepair.getValue(), -1,
									BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.CKRepair.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wRROItemTaskList = this.SelectList(wLoginUser, wTaskIDList, -1, -1, -1, null, -1, -1, -1, -1, -1,
					wStartTime, wEndTime, -1, wErrorCode);

			// 所有未完成的任务
			// List<> 查询消息

			List<RROItemTask> wRROItemTaskUndoneList = new ArrayList<RROItemTask>();

			List<Integer> wTaskIDUndoneList = wRROItemTaskUndoneList.stream().map(p -> (int) p.ID).distinct()
					.collect(Collectors.toList());
			wTaskIDUndoneList.removeAll(wTaskIDList);
			wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCRepair.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class);

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.QTRepair.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.TechRepair.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.CKRepair.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class));

			wTaskIDUndoneList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());
			for (RROItemTask wRROItemTask : wRROItemTaskUndoneList) {
				if (wTaskIDUndoneList.contains(wRROItemTask.ID)) {
					wRROItemTaskList.add(wRROItemTask);
				}

			}
			wRROItemTaskList.sort(Comparator.comparing(RROItemTask::getSubmitTime).reversed());

			wResult = wRROItemTaskList;
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
			wResult = new ArrayList<BPMTaskBase>(this.SelectList(wLoginUser, null, -1, wLoginUser.getID(), -1, null, -1,
					-1, -1, -1, -1, wStartTime, wEndTime, -1, wErrorCode));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		RROItemTask wResult = new RROItemTask();
		try {
			if (wTask == null) {
				wErrorCode.set(MESException.Parameter.getValue());
				return wResult;
			}
			ServiceResult<String> wInstance = null;

			if (wTask.getID() <= 0) {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 500701);
			} else {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			}
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			RROItemTask wRRORepairItem = (RROItemTask) wTask;

			String wSQL = "";
			if (wRRORepairItem.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.rro_repairitem(FlowType,FlowID,UpFlowID,FollowerID,"
								+ "TaskID,Content,Status,StatusText,WorkAreaID,TeamID,OperatorID,"
								+ "IsStatus,IsSendNCR,NCRID,ItemLogo,"
								+ "TaskStepID,IPTItemID,CustomerID,Remark,StationID,ProcessID,OrderID,CreateTime,"
								+ "SubmitTime,Code,IsDelivery,ImageUrl,SupplierID) "
								+ "VALUES(:FlowType,:FlowID,:UpFlowID,:FollowerID,:TaskID,"
								+ ":Content,:Status,:StatusText,:WorkAreaID,:TeamID,:OperatorID,:IsStatus,"
								+ ":IsSendNCR,:NCRID,:ItemLogo,:TaskStepID,:IPTItemID,:CustomerID,:Remark,:StationID,"
								+ ":ProcessID,:OrderID,now(),now(),:Code,:IsDelivery,:ImageUrl,:SupplierID);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat
						.format("UPDATE {0}.rro_repairitem SET TaskID = :TaskID,FlowType = :FlowType,FlowID = :FlowID,"
								+ "UpFlowID = :UpFlowID,FollowerID = :FollowerID,"
								+ "Content = :Content,Status = :Status,StatusText = :StatusText,CustomerID=:CustomerID,"
								+ "WorkAreaID=:WorkAreaID," + "TeamID=:TeamID,"
								+ "OperatorID=:OperatorID,IsStatus=:IsStatus,"
								+ "IsSendNCR=:IsSendNCR,NCRID=:NCRID,ItemLogo=:ItemLogo,TaskStepID=:TaskStepID,"
								+ "IPTItemID=:IPTItemID," + "OrderID=:OrderID,SubmitTime=now(),"
								+ "CustomerID=:CustomerID ,Remark=:Remark,StationID=:StationID,ProcessID=:ProcessID,"
								+ "Code=:Code,"
								+ "IsDelivery=:IsDelivery,CreateTime=:CreateTime,ImageUrl=:ImageUrl,SupplierID=:SupplierID "
								+ " WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("ID", wRRORepairItem.ID);
			wParamMap.put("FlowType", wRRORepairItem.FlowType);
			wParamMap.put("FlowID", wRRORepairItem.FlowID);
			wParamMap.put("UpFlowID", wRRORepairItem.UpFlowID);
			if (wRRORepairItem.FollowerID == null)
				wRRORepairItem.FollowerID = new ArrayList<Integer>();
			wParamMap.put("FollowerID", StringUtils.Join(",", wRRORepairItem.FollowerID));
			wParamMap.put("OrderID", wRRORepairItem.OrderID);
			wParamMap.put("TaskID", wRRORepairItem.TaskID);
			wParamMap.put("Content", wRRORepairItem.Content);
			wParamMap.put("Status", wRRORepairItem.Status);
			wParamMap.put("StatusText", wRRORepairItem.StatusText);
			wParamMap.put("WorkAreaID", wRRORepairItem.WorkAreaID);
			wParamMap.put("TeamID", wRRORepairItem.TeamID);
			wParamMap.put("OperatorID", wRRORepairItem.OperatorID);
			wParamMap.put("IsStatus", wRRORepairItem.IsStatus);
			wParamMap.put("IsSendNCR", wRRORepairItem.IsSendNCR);
			wParamMap.put("NCRID", wRRORepairItem.NCRID);
			wParamMap.put("ItemLogo", wRRORepairItem.ItemLogo);
			wParamMap.put("TaskStepID", wRRORepairItem.TaskStepID);
			wParamMap.put("IPTItemID", wRRORepairItem.IPTItemID);

			wParamMap.put("CustomerID", wRRORepairItem.CustomerID);
			wParamMap.put("Remark", wRRORepairItem.Remark);
			wParamMap.put("StationID", wRRORepairItem.StationID);
			wParamMap.put("ProcessID", wRRORepairItem.ProcessID);
			wParamMap.put("Code", wRRORepairItem.Code);
			wParamMap.put("IsDelivery", wRRORepairItem.IsDelivery);
			wParamMap.put("CreateTime", wRRORepairItem.CreateTime);
			wParamMap.put("ImageUrl", wRRORepairItem.ImageUrl);
			wParamMap.put("SupplierID", wRRORepairItem.SupplierID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wRRORepairItem.getID() <= 0) {
				wRRORepairItem.setID(keyHolder.getKey().intValue());
			}
			wResult = wRRORepairItem;
		} catch (Exception e) {
			logger.error(e.toString());
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
//			int wYear = Calendar.getInstance().get(Calendar.YEAR);
//			int wMonth = Calendar.getInstance().get(Calendar.MONTH);
//			Calendar wSTime = Calendar.getInstance();
//			wSTime.set(wYear, wMonth, 1, 0, 0, 0);
//			Calendar wETime = Calendar.getInstance();
//			wETime.set(wYear, wMonth + 1, 1, 23, 59, 59);
//			wETime.add(Calendar.DATE, -1);

//			String wSQL = StringUtils.Format(
//					"select count(*)+1 as Number from {0}.rro_repairitem where CreateTime > :wSTime and CreateTime < :wETime;",
//					wInstance.Result);

			String wSQL = StringUtils.Format(
					"SELECT Code from {0}.rro_repairitem where ID in (SELECT Max(ID) FROM {0}.rro_repairitem);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
//			wParamMap.put("wSTime", wSTime);
//			wParamMap.put("wETime", wETime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wNumber = 1;
			for (Map<String, Object> wReader : wQueryResult) {
//				if (wReader.containsKey("Number")) {
//					wNumber = StringUtils.parseInt(wReader.get("Number"));
//					break;
//				}

				String wCode = StringUtils.parseString(wReader.get("Code"));
				// 截取流水号
				String wSerialNumber = wCode.substring(wCode.length() - 4);
				int wSN = StringUtils.parseInt(wSerialNumber);
				// 截取月份
				String wMonthStr = wCode.substring(wCode.length() - 6, wCode.length() - 4);
				int wMonth = StringUtils.parseInt(wMonthStr);
				if (Calendar.getInstance().get(Calendar.MONTH) != wMonth - 1) {
					wNumber = 1;
				} else {
					wNumber = wSN + 1;
				}
			}

			wResult = StringUtils.Format("RO{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		RROItemTask wResult = new RROItemTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			wResult = this.SelectByID(wLoginUser, wTaskID, wErrorCode);

			if (wResult.TaskID > 0) {
				RROTask wRROTask = RROTaskDAO.getInstance().SelectByID(wLoginUser, wResult.TaskID, wErrorCode);
				if (wRROTask != null && wRROTask.ID > 0) {
					wResult.CarTypeName = wRROTask.CarTypeName;
					wResult.CarNumber = wRROTask.CarNumber;
					wResult.LineName = wRROTask.LineName;
					wResult.CustomerName = wRROTask.CustomerName;
				}
			}
			if (wResult == null || wResult.ID == 0)
				return wResult;
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 返修项不合格质检员发起不合格评审
	 */
	@SuppressWarnings("unchecked")
	public void RRO_TaskSendNCR(BMSEmployee wLoginUser, RROItemTask wRROItemTask, OutResult<Integer> wErrorCode) {
		try {

			FPCPart wFPCPart = WDWConstans.GetFPCPart(wRROItemTask.StationID);

			// 创建NCR任务
			BPMEventModule wEventID = BPMEventModule.getEnumType(BPMEventModule.QTNCR.getValue());
			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = NCRServiceImpl.getInstance().NCR_CreateTask(wLoginUser, wEventID);
			BPMTaskBase wData = (NCRTask) wServiceResult.getResult();
			if (wData.ID > 0) {
				wData.FlowID = BPMServiceImpl.getInstance()
						.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData).Info(Integer.class);
				wServiceResult = NCRServiceImpl.getInstance().NCR_UpdateTask(wLoginUser, (NCRTask) wData);
				List<BPMActivitiTask> wBPMActivitiTask = BPMServiceImpl.getInstance()
						.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID).List(BPMActivitiTask.class);

				NCRTask wNCRTask = (NCRTask) wData;
				// 赋值
				wNCRTask.CarTypeID = wRROItemTask.CarTypeID;
				wNCRTask.CarNumber = wRROItemTask.CarNumber;
				wNCRTask.Number = 1;
				wNCRTask.StationID = wRROItemTask.StationID;

				wNCRTask.DutyCarfID = StringUtils.Join(",", wFPCPart.TechnicianList);
				wNCRTask.DutyCarfName = wFPCPart.TechnicianName;

				wNCRTask.LineID = wRROItemTask.LineID;
				wNCRTask.CustomerID = wRROItemTask.CustomerID;

				wNCRTask.DepartmentID = wLoginUser.DepartmentID;
				wNCRTask.DutyDepartmentID = wLoginUser.DepartmentID + "";

				wNCRTask.DescribeInfo = wRROItemTask.Content;
				wNCRTask.OrderID = wRROItemTask.OrderID;
				wNCRTask.Status = 4;
				wNCRTask.TaskStepID = wRROItemTask.TaskStepID;
				wNCRTask.IPTItemID = wRROItemTask.IPTItemID;
				wNCRTask.ProcessID = wRROItemTask.ProcessID;
				wNCRTask.UpFlowID = wRROItemTask.UpFlowID;
				wNCRTask.SubmitTime = Calendar.getInstance();
				wNCRTask.CreateTime = Calendar.getInstance();
				wNCRTask.ImageUrl = wRROItemTask.ImageUrl;
				wNCRTask.StationID = wRROItemTask.StationID;

				Map<String, Object> wDate = new HashMap<String, Object>();

				wDate = CloneTool.Clone(wNCRTask, Map.class);

				wDate.put("StationID_txt_", wRROItemTask.StationName);
				wDate.put("LineID_txt_", wRROItemTask.LineName);
				wDate.put("CustomerID_txt_", wRROItemTask.CustomerName);
				wDate.put("DescribeInfo_txt_", wRROItemTask.Content);
				wDate.put("Number_txt_", "1");
				wDate.put("CarNumber_txt_", wRROItemTask.CarTypeName + wRROItemTask.CarNumber);
				wDate.put("CarTypeID_txt_", wRROItemTask.CarTypeName);
				wDate.put("ProcessID_txt_", wRROItemTask.ProcesName);
				wDate.put("StationID_txt_", wRROItemTask.StationName);
				wDate.put("UpFlowID_txt_", wRROItemTask.UpFlowName);
				wDate.put("TransferPerson", "");
				wDate.put("Applier", wLoginUser.ID + "");
				wDate.put("UpFlowID", String.valueOf(wRROItemTask.UpFlowID));

				// 责任工艺师
				SetCraft(wLoginUser, wNCRTask, wDate);
				// 质量工程师
				SetQualityEngineer(wLoginUser, wNCRTask, wDate);
				// 分配执行人
				SetDoingUser(wLoginUser, wNCRTask, wDate);
				// 工区主管、调度
				SetAuditor(wLoginUser, wNCRTask, wDate);

				// 提交
				BPMServiceImpl.getInstance().BPM_MsgUpdate(wLoginUser,
						StringUtils.parseInt(wBPMActivitiTask.get(0).getID()), 0, wNCRTask, wDate);
				NCRServiceImpl.getInstance().NCR_UpdateTask(wLoginUser, wNCRTask);
				BPMServiceImpl.getInstance().BPM_GetInstanceByID(wLoginUser, wNCRTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 工区主管、调度
	 */
	private void SetAuditor(BMSEmployee wLoginUser, NCRTask wNCRTask, Map<String, Object> wDate) {
		try {
			List<BMSEmployee> wList = NCRServiceImpl.getInstance().NCR_LeaderList(wLoginUser,
					wNCRTask.StationID).Result;
			wDate.put("KeynoteID",
					StringUtils.Join(",", wList.stream().map(p -> p.ID).distinct().collect(Collectors.toList())));
			wDate.put("KeynoteID_txt_",
					StringUtils.Join(",", wList.stream().map(p -> p.Name).distinct().collect(Collectors.toList())));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 分配执行人
	 */
	private void SetDoingUser(BMSEmployee wLoginUser, NCRTask wNCRTask, Map<String, Object> wDate) {
		try {
			List<BMSEmployee> wUserList = NCRServiceImpl.getInstance().NCR_QuerySameClassMembers(wLoginUser,
					wLoginUser.ID).Result;
			wDate.put("Implementor",
					StringUtils.Join(",", wUserList.stream().map(p -> p.ID).distinct().collect(Collectors.toList())));
			wDate.put("Implementor_txt_",
					StringUtils.Join(",", wUserList.stream().map(p -> p.Name).distinct().collect(Collectors.toList())));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 质量工程师
	 */
	private void SetQualityEngineer(BMSEmployee wLoginUser, NCRTask wNCRTask, Map<String, Object> wDate) {
		try {
			List<BMSRoleItem> wList = CoreServiceImpl.getInstance().BMS_UserAllByRoleID(wLoginUser, 13)
					.List(BMSRoleItem.class);
			if (wList.size() > 0) {
				wNCRTask.StationStaff = wList.get(0).FunctionID;
				wNCRTask.StationStaffName = WDWConstans.GetBMSEmployeeName(wList.get(0).FunctionID);
				wDate.put("StationStaff", String.valueOf(wNCRTask.StationStaff));
				wDate.put("StationStaff_txt_", wNCRTask.StationStaffName);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 责任工艺师
	 */
	private void SetCraft(BMSEmployee wLoginUser, NCRTask wNCRTask, Map<String, Object> wDate) {
		try {
			SFCStationPerson wStationPerson = APSLOCOServiceImpl.getInstance()
					.OMS_QueryCarfListByStationID(wLoginUser, wNCRTask.StationID).Info(SFCStationPerson.class);
			wNCRTask.DutyCarfID = StringUtils.Join(",", wStationPerson.TechnicianList);
			wNCRTask.DutyCarfName = wStationPerson.Technicians;
			wDate.put("DutyCarfID", wNCRTask.DutyCarfID);
			wDate.put("DutyCarfID_txt_", wNCRTask.DutyCarfName);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 根据登陆者的岗位职能筛选对应工位的返修任务
	 */
	public List<RROItemTask> FilterByPosition(BMSEmployee wLoginUser, List<RROItemTask> wList) {
		List<RROItemTask> wResult = new ArrayList<RROItemTask>();
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			List<Integer> wPartIDList = new ArrayList<Integer>();
			// ①查询班组工位列表
			List<BMSWorkCharge> wWorkChargeList = CoreServiceImpl.getInstance()
					.BMS_QueryWorkChargeList(wLoginUser, -1, wLoginUser.DepartmentID, 1).List(BMSWorkCharge.class);
			if (wWorkChargeList != null && wWorkChargeList.size() > 0) {
				wPartIDList = wWorkChargeList.stream().map(p -> p.StationID).collect(Collectors.toList());
			}
			// ②查询工区检验员列表
			if (wPartIDList.size() <= 0) {
				int wAreaID = 0;
				List<LFSWorkAreaChecker> wAreaCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
				if (wAreaCheckerList != null && wAreaCheckerList.size() > 0
						&& wAreaCheckerList.stream()
								.anyMatch(p -> p.LeaderIDList.stream().anyMatch(q -> q == wLoginUser.ID)
										|| p.ScheduleIDList.stream().anyMatch(q -> q == wLoginUser.ID))) {
					wAreaID = wAreaCheckerList.stream()
							.filter(p -> p.LeaderIDList.stream().anyMatch(q -> q == wLoginUser.ID)
									|| p.ScheduleIDList.stream().anyMatch(q -> q == wLoginUser.ID))
							.findFirst().get().WorkAreaID;
				}
				if (wAreaID > 0) {
					// ③查询工区工位列表
					List<LFSWorkAreaStation> wAreaStationList = LFSServiceImpl.getInstance()
							.LFS_QueryWorkAreaStationList(wLoginUser, wAreaID).List(LFSWorkAreaStation.class);
					if (wAreaStationList != null && wAreaStationList.size() > 0) {
						int wCloneAreaID = wAreaID;
						wAreaStationList = wAreaStationList.stream().filter(p -> p.WorkAreaID == wCloneAreaID)
								.collect(Collectors.toList());
						wPartIDList = wAreaStationList.stream().map(p -> p.StationID).collect(Collectors.toList());
					}
				}
			}
			// ⑤剔除质量工位
			wPartIDList.removeIf(p -> WDWConstans.GetFPCPartList().values().stream()
					.anyMatch(q -> q.ID == p && (q.PartType == 3 || q.PartType == 4)));
			// ④若都不是，全数返回
			if (wPartIDList.size() <= 0) {
				return wList;
			}
			// ⑤根据工位列表筛选返修任务
			List<Integer> wClonePartIDList = wPartIDList;
			wResult = wList.stream().filter(p -> wClonePartIDList.stream().anyMatch(q -> q == p.StationID))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取时间段内的返修订单集合
	 */
	public List<Integer> GetOrderIDList(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct OrderID FROM {0}.rro_repairitem "
							+ "where :StartTime < SubmitTime and :EndTime > CreateTime and Status > 0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wResult.add(wOrderID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取车分类返修统计信息
	 * 
	 * @param wErrorCode
	 */
	public RROPartNo GetRROPartNo(BMSEmployee wLoginUser, OMSOrder wOMSOrder, OutResult<Integer> wErrorCode) {
		RROPartNo wResult = new RROPartNo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (SELECT count(*) FROM {0}.rro_repairitem where OrderID=:OrderID and Status > 0) FQTY,"
							+ "	(SELECT count(*) FROM {0}.rro_repairitem where OrderID=:OrderID and Status in (25,22,21)) Done;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOMSOrder.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.FQTY = StringUtils.parseInt(wReader.get("FQTY"));
				wResult.Done = StringUtils.parseInt(wReader.get("Done"));
				wResult.ToDo = wResult.FQTY - wResult.Done;
				wResult.Customer = wOMSOrder.Customer;
				wResult.LineName = wOMSOrder.LineName;
				wResult.OrderID = wOMSOrder.ID;
				wResult.PartNo = wOMSOrder.PartNo;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单获取工位集合
	 */
	public List<Integer> GetPartListByOrder(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct StationID FROM {0}.rro_repairitem where OrderID=:OrderID and Status>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStation = StringUtils.parseInt(wReader.get("StationID"));
				wResult.add(wStation);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位分类的返修统计数据
	 */
	public RROPart GetRROPart(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, OutResult<Integer> wErrorCode) {
		RROPart wResult = new RROPart();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (SELECT count(*) FROM {0}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and Status > 0) FQTY,"
							+ "	(SELECT count(*) FROM {0}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and Status in (25,22,21)) Done;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrder.ID);
			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.FQTY = StringUtils.parseInt(wReader.get("FQTY"));
				wResult.Done = StringUtils.parseInt(wReader.get("Done"));
				wResult.ToDo = wResult.FQTY - wResult.Done;
				wResult.Customer = wOrder.Customer;
				wResult.LineName = wOrder.LineName;
				wResult.OrderID = wOrder.ID;
				wResult.PartNo = wOrder.PartNo;
				wResult.PartID = wPartID;
				wResult.PartName = WDWConstans.GetFPCPartName(wPartID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 时间段查询单据
	 */
	public List<Integer> GetTaskIDListByTime(BMSEmployee wLoginUser, Calendar wSTime, Calendar wETime,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT ID FROM {0}.rro_repairitem where CreateTime>:wSTime and CreateTime<:wETime;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wSTime", wSTime);
			wParamMap.put("wETime", wETime);

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
	 * 删除默认单据
	 */
	public void DeleteDefaultTask(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("Delete from {0}.rro_repairitem where Status=0 and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public void UpdateCode(BMSEmployee wLoginUser, RROItemTask wTask, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("Update {0}.rro_repairitem set Code=:Code where ID = :ID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("Code", wTask.Code);
			wParamMap.put("ID", wTask.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
