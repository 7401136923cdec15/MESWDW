package com.mes.ncr.server.serviceimpl.dao.mtc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BMSDutyType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.MTCStatus;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bms.BMSPosition;
import com.mes.ncr.server.service.po.bpm.BPMOperationStep;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.po.lfs.LFSStoreHouse;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.BPMServiceImpl;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.LFSServiceImpl;
import com.mes.ncr.server.serviceimpl.MTCServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

public class MTCTaskDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MTCTaskDAO.class);

	private static MTCTaskDAO Instance = null;

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MTCTask> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (MTCTask wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.mtc_task WHERE ID IN({0}) ;", String.join(",", wIDList),
					wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<MTCTask> SelectList(BMSEmployee wLoginUser, int wFlowType, int wFlowID, int wPlaceID, int wTargetID,
			int wApplierID, List<Integer> wStatusList, List<Integer> wNotStatusList, int wShiftID, int wOrderID,
			int wType, int wCarTypeID, String wCarNumber, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		try {
			wResult = SelectList(wLoginUser, null, wFlowType, wFlowID, wPlaceID, wTargetID, wApplierID, wStatusList,
					wNotStatusList, wShiftID, wOrderID, wType, wCarTypeID, wCarNumber, wStartTime, wEndTime,
					wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<MTCTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		try {
			if (wIDList == null || wIDList.size() <= 0)
				return wResult;

			wIDList.removeIf(p -> p <= 0);

			if (wIDList.size() <= 0)
				return wResult;
			wResult = SelectList(wLoginUser, wIDList, -1, -1, -1, -1, -1, null, null, -1, -1, -1, -1, "", wStartTime,
					wEndTime, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<MTCTask> SelectList(BMSEmployee wLoginUser, int wFlowType, int wFlowID, int wPlaceID, int wTargetID,
			int wUpFlowID, int wStatus, int wShiftID, int wOrderID, int wType, int wCarTypeID, String wCarNumber,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		try {
			wResult = SelectList(wLoginUser, null, wFlowType, wFlowID, wPlaceID, wTargetID, wUpFlowID,
					StringUtils.parseList(new Integer[] { wStatus }), null, wShiftID, wOrderID, wType, wCarTypeID,
					wCarNumber, wStartTime, wEndTime, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	private List<MTCTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType, int wFlowID,
			int wPlaceID, int wTargetID, int wUpFlowID, List<Integer> wStatusList, List<Integer> wNotStatusList,
			int wShiftID, int wOrderID, int wType, int wCarTypeID, String wCarNumber, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResult;

			if (wStatusList == null)
				wStatusList = new ArrayList<Integer>();
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();
			if (wNotStatusList == null)
				wNotStatusList = new ArrayList<Integer>();
			if (wCarNumber == null)
				wCarNumber = "";

			wIDList.removeIf(p -> p <= 0);
			wStatusList.removeIf(p -> p < 0);

			String wSQL = StringUtils.Format("SELECT * FROM {0}.mtc_task WHERE 1=1 "
					+ " and ( :wFlowType <= 0 or :wFlowType = FlowType )"
					+ " and ( :wFlowID <= 0 or :wFlowID = FlowID )" + " and ( :wPlaceID <= 0 or :wPlaceID = PlaceID )"
					+ " and ( :wOrderID <= 0 or :wOrderID = OrderID )" + " and ( :wType <= 0 or :wType = Type )"
					+ " and ( :wTargetID <= 0 or :wTargetID = TargetID )"
					+ " and (:wUpFlowID <=0 or UpFlowID= :wUpFlowID)"
					+ " and (:wCarTypeID <=0 or CarTypeID= :wCarTypeID)"
					+ " and (:wCarNumber = null or :wCarNumber='''' or PartNo= :wCarNumber)"
					+ " and ( :wStatus = '''' or Status in ({1}) ) and ( :wNotStatus = '''' or Status not in ({2}))"
					+ " and (:wID is null or :wID = '''' or ID in ({3}))"
					+ " and (:wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or CreateTime>= :wStartTime)"
					+ " and (:wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or CreateTime<= :wEndTime)",
					wInstance.Result, wStatusList.size() > 0 ? StringUtils.Join(",", wStatusList) : "0",
					wNotStatusList.size() > 0 ? StringUtils.Join(",", wNotStatusList) : "0",
					wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wFlowType", wFlowType);
			wParamMap.put("wFlowID", wFlowID);
			wParamMap.put("wPlaceID", wPlaceID);
			wParamMap.put("wTargetID", wTargetID);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatusList));
			wParamMap.put("wNotStatus", StringUtils.Join(",", wNotStatusList));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wType", wType);
			wParamMap.put("wCarTypeID", wCarTypeID);
			wParamMap.put("wCarNumber", wCarNumber);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MTCTask wMTCTask = new MTCTask();
				wMTCTask.ID = StringUtils.parseInt(wReader.get("ID"));
				wMTCTask.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wMTCTask.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wMTCTask.Code = StringUtils.parseString(wReader.get("Code"));
				wMTCTask.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wMTCTask.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wMTCTask.InformShift = StringUtils.parseInt(wReader.get("InformShift"));
				wMTCTask.TargetID = StringUtils.parseInt(wReader.get("TargetID"));
				wMTCTask.TargetStockID = StringUtils.parseInt(wReader.get("TargetStockID"));

				wMTCTask.TargetSID = StringUtils.parseInt(wReader.get("TargetSID"));
				wMTCTask.TargetSStockID = StringUtils.parseInt(wReader.get("TargetSStockID"));

				wMTCTask.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wMTCTask.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wMTCTask.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wMTCTask.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wMTCTask.Status = StringUtils.parseInt(wReader.get("Status"));
				wMTCTask.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wMTCTask.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wMTCTask.ExpectedTime = StringUtils.parseCalendar(wReader.get("ExpectedTime"));
				wMTCTask.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wMTCTask.Type = StringUtils.parseInt(wReader.get("Type"));
				wMTCTask.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				wMTCTask.DepartmentID = StringUtils.parseInt(wReader.get("DepartmentID"));
				wMTCTask.AreaID = StringUtils.parseString(wReader.get("AreaID"));
				wMTCTask.IsPreMove = StringUtils.parseInt(wReader.get("IsPreMove"));
				wMTCTask.AreaName = WDWConstans
						.GetBMSEmployeeName(StringUtils.parseIntList(wMTCTask.AreaID.split(",|;")));
				wMTCTask.UpFlowName = WDWConstans.GetBMSEmployeeName(wMTCTask.UpFlowID);
				wMTCTask.FollowerName = WDWConstans.GetBMSEmployeeName(wMTCTask.FollowerID);
				wMTCTask.DepartmentName = WDWConstans.GetBMSDepartmentName(wMTCTask.DepartmentID);
				wResult.add(wMTCTask);
			}
			if (wResult.size() <= 0)
				return wResult;

			// 返回前库位名称台位名称赋值
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			if (wLFSStoreHouseList != null && wLFSStoreHouseList.size() > 0 && wFMCWorkspaceList != null
					&& wFMCWorkspaceList.size() > 0)
				for (MTCTask wItem : wResult) {
					wItem.CarType = WDWConstans.GetFPCProductNo(wItem.CarTypeID);
					if (wItem.PlaceID != 0) {
						Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream().filter(p -> p.ID == wItem.PlaceID)
								.findFirst();
						if (wOptional != null && wOptional.isPresent())
							wItem.PlaceName = wOptional.get().getName();
					}

					wItem.CustomerName = WDWConstans.GetCRMCustomerName(wItem.CustomerID);

					if (wItem.TargetID != 0) {
						Optional<FMCWorkspace> wStore = wFMCWorkspaceList.stream().filter(p -> p.ID == wItem.TargetID)
								.findFirst();
						if (wStore != null && wStore.isPresent())
							wItem.TargetName = wStore.get().getName();
					}
					if (wItem.TargetSID != 0) {
						Optional<FMCWorkspace> wStore = wFMCWorkspaceList.stream().filter(p -> p.ID == wItem.TargetSID)
								.findFirst();
						if (wStore != null && wStore.isPresent())
							wItem.TargetSName = wStore.get().getName();
					}
					if (wItem.TargetSStockID != 0) {
						Optional<LFSStoreHouse> wStore = wLFSStoreHouseList.stream()
								.filter(p -> p.ID == wItem.TargetSStockID).findFirst();
						if (wStore != null && wStore.isPresent()) {
							wItem.TargetSStockName = wStore.get().getName();
						}
					}

					if (wItem.PlaceID != 0) {
						int wPlace = wItem.PlaceID;
						FMCWorkspace wPlaceStock = new FMCWorkspace();
						Optional<FMCWorkspace> wFMCWorkspace = wFMCWorkspaceList.stream().filter(p -> p.ID == wPlace)
								.findFirst();
						if (wFMCWorkspace != null && wFMCWorkspace.isPresent())
							wPlaceStock = wFMCWorkspace.get();

						int wTarget = wItem.TargetID;
						FMCWorkspace wTargetStock = new FMCWorkspace();
						Optional<FMCWorkspace> wFMCWorkTarget = wFMCWorkspaceList.stream().filter(p -> p.ID == wTarget)
								.findFirst();
						if (wFMCWorkTarget != null && wFMCWorkTarget.isPresent())
							wTargetStock = wFMCWorkTarget.get();

						if (wPlaceStock != null && wTargetStock != null) {
							if (wPlaceStock.ParentID != 0) {
								int wPlaceParentID = wPlaceStock.ParentID;
								Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream()
										.filter(p -> p.ID == wPlaceParentID).findFirst();
								if (wOptional != null && wOptional.isPresent()) {
									wItem.StockID = wOptional.get().getID();
									wItem.StockName = wOptional.get().getName();
								}

							}
							if (wItem.TargetStockID != 0) {
								Optional<LFSStoreHouse> wStore = wLFSStoreHouseList.stream()
										.filter(p -> p.ID == wItem.TargetStockID).findFirst();
								if (wStore != null && wStore.isPresent()) {
									wItem.TargetStockName = wStore.get().getName();
								}

							}
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
	 * 创建MTC任务
	 * 
	 * @param wSenderID
	 * @return
	 */
	public MTCTask MTC_CreateTask(BMSEmployee wLoginUser, BPMEventModule wModuleID, OutResult<Integer> wErrorCode) {
		MTCTask wResult = new MTCTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			// 创建任务并获取当前操作步骤
			wResult.FlowType = wModuleID.getValue();
			wResult.FlowID = 0;
			wResult.ID = 0;
			wResult.UpFlowID = wLoginUser.ID;
			wResult.UpFlowName = wLoginUser.Name;
			wResult.CreateTime = Calendar.getInstance();
			wResult.SubmitTime = Calendar.getInstance();
			wResult.Code = GetNextCode(wLoginUser, wErrorCode);
			wResult.Status = 0;

			// 查询是否为班组成员，是则返回班组长ID，Name
			BMSPosition wBMSPosition = WDWConstans.GetBMSPosition(wLoginUser.Position);
			if (wBMSPosition != null && wBMSPosition.DutyID == BMSDutyType.Member.getValue()) {

				List<BMSEmployee> wPList = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> p.Active == 1 && p.DepartmentID == wBMSPosition.DepartmentID
								&& WDWConstans.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				if (wPList != null && wPList.size() > 0) {
					List<Integer> wIDList = new ArrayList<Integer>();
					List<String> wNameList = new ArrayList<String>();
					for (BMSEmployee wBMSEmployee : wPList) {
						wIDList.add(wBMSEmployee.ID);
						wNameList.add(wBMSEmployee.Name);
					}
					wResult.MonitorID = StringUtils.Join(",", wIDList);
					wResult.MonitorName = StringUtils.Join(",", wNameList);
				}
			}

			this.BPM_UpdateTask(wLoginUser, wResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取下一个编号
	 */
//	public String GetNextCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
//		String wResult = "";
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//
//			String wSQL = StringUtils.Format(
//					"select * from {0}.mtc_task where id in( select Max(ID) from {0}.mtc_task);", wInstance.Result);
//			
//			Map<String, Object> wParamMap = new HashMap<String, Object>();
//			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
//			String wCode = "";
//			for (Map<String, Object> wReader : wQueryResult) {
//				wCode = StringUtils.parseString(wReader.get("Code"));
//			}
//			if (wCode.equals("")) {
//				String wNumber = String.format("%04d", 1);
//
//				wResult = StringUtils.Format("TT-{0}{1}-{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
//						String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
//						String.format("%04d", Integer.parseInt(wNumber)));
//			} else {
//				String[] wStrs = wCode.split("-");
//				if (wStrs.length == 3) {
//					String wOldYear = wStrs[2];
//					String wCurYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
//					if (wOldYear.equals(wCurYear)) {
//						int wNumber0 = Integer.parseInt(wStrs[1]);
//						wNumber0++;
//						String wNumber = String.format("%04d", wNumber0);
//						wResult = StringUtils.Format("TT-{0}{1}-{2}",
//								String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
//								String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
//								String.format("%04d", Integer.parseInt(wNumber)));
//					} else {
//						String wNumber = String.format("%04d", 1);
//						wResult = StringUtils.Format("TT-{0}{1}-{2}",
//								String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
//								String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
//								String.format("%04d", Integer.parseInt(wNumber)));
//					}
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			logger.error(ex.toString());
//		}
//		return wResult;
//	}

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
					"select count(*)+1 as Number from {0}.mtc_task where CreateTime > :wSTime and CreateTime < :wETime;",
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

			wResult = StringUtils.Format("TT{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 自动更新已完工车辆信息列表
	 * 
	 * @return
	 */
	public void AutoCompletTargetID(BMSEmployee wLoginUser, MTCTask wMTCTask, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}
			if (wMTCTask != null && wMTCTask.ID > 0) {
				List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
						.List(FMCWorkspace.class);
				String wPartNo = StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo);
				// 目标台位与目标库位为0则为出厂
				if (wMTCTask.TargetID == 0 && wMTCTask.TargetStockID == 0) {
					// 车辆出厂则将台位上车辆更新
					Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream().filter(p -> p.PartNo.equals(wPartNo))
							.findFirst();
					if (wOptional != null && wOptional.isPresent()) {
						FMCWorkspace wWorkStock = wOptional.get();
						wWorkStock.PartNo = "";
						FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wWorkStock);
					}
				} else// 移车任务完成后车辆与目标台位绑定
				{
					// 完工时目标台位为0时（自动新增动态台位）
					if (wMTCTask.TargetStockID > 0 && wMTCTask.TargetID <= 0) {
						List<FMCWorkspace> wAllFMCWorkspaceList = FMCServiceImpl.getInstance()
								.FMC_QueryWorkspaceList(wLoginUser).List(FMCWorkspace.class);
						List<FMCWorkspace> wList = wAllFMCWorkspaceList.stream()
								.filter(p -> p.ParentID == wMTCTask.TargetStockID).collect(Collectors.toList());

						LFSStoreHouse wLFSStoreHouse = new LFSStoreHouse();
						// 获取库位名称（流动台位赋值）
						List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance()
								.LFS_QueryStoreHouseList(wLoginUser).List(LFSStoreHouse.class);
						if (wLFSStoreHouseList != null && wLFSStoreHouseList.size() > 0) {
							Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream()
									.filter(p -> p.ID == wMTCTask.TargetStockID).findFirst();
							if (wOptional != null && wOptional.isPresent())
								wLFSStoreHouse = wOptional.get();
						}
						String wTargetStockName = wLFSStoreHouse.Name;

						FMCWorkspace wFMCWorkspace = new FMCWorkspace();
						wFMCWorkspace.Name = StringUtils.Format("{0}流动台位{1}", wTargetStockName, wList.size() + 1);
						wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}",
								String.format("%03d", wMTCTask.TargetStockID), String.format("%04d", wList.size() + 1));
						wFMCWorkspace.Active = 1;
						wFMCWorkspace.CreatorID = wLoginUser.ID;
						wFMCWorkspace.CreateTime = Calendar.getInstance();
						wFMCWorkspace.ParentID = wMTCTask.TargetStockID;
						wFMCWorkspace.PlaceType = 2;
						wFMCWorkspace = FMCServiceImpl.getInstance()
								.FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace).Info(FMCWorkspace.class);
						wFMCWorkspace.PartNo = StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo);
						FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace);
						wMTCTask.TargetID = wFMCWorkspace.ID;
						MTCTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wMTCTask, wErrorCode);
					} else {
						FMCWorkspace wWorkStock = new FMCWorkspace();
						Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream()
								.filter(p -> p.ID == wMTCTask.TargetID).findFirst();
						if (wOptional != null && wOptional.isPresent())
							wWorkStock = wOptional.get();
						// 库位ID赋值，并将车号更新至当前台位
						if (wWorkStock != null && wWorkStock.ID > 0) {
							// 将车号更新至当前台位
							wWorkStock.PartNo = StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo);
							FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wWorkStock);
						}
					}
				}
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
	}

	/**
	 * 判断时间是否在某个时间段内
	 * 
	 * @param wNowTime
	 * @param wStart
	 * @param wEnd
	 * @return
	 */
	public boolean IsInTimeRange(Calendar wNowTime, String wStart, String wEnd) {

		String wDateString = StringUtils.parseCalendarToString(wNowTime, "yyyy-MM-dd");

		Calendar wStartCalendar = StringUtils.parseCalendar(wDateString + " " + wStart, "yyyy-MM-dd HH:mm:ss");
		Calendar wEndCalendar = StringUtils.parseCalendar(wDateString + " " + wEnd, "yyyy-MM-dd HH:mm:ss");

		if (wNowTime.compareTo(wStartCalendar) >= 0 && wNowTime.compareTo(wEndCalendar) <= 0) {
			return true;
		}

		return false;
	}

	private MTCTaskDAO() {
		super();
	}

	public static MTCTaskDAO getInstance() {
		if (Instance == null)
			Instance = new MTCTaskDAO();
		return Instance;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		try {

			// 获取所有任务消息 模块为移车的
			List<BFCMessage> wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCMovePart.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCMovePart.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));

			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCMovePart.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			// List<> 查询消息

			Map<Integer, MTCTask> wMTCTaskMap = new HashMap<Integer, MTCTask>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<MTCTask> wMTCTaskListTemp = MTCTaskDAO.getInstance().SelectList(wLoginUser, wTaskIDList, null,
						null, wErrorCode);

				wMTCTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			MTCTask wMTCTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wMTCTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wMTCTaskTemp = CloneTool.Clone(wMTCTaskMap.get((int) wBFCMessage.getMessageID()), MTCTask.class);
				wMTCTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wMTCTaskTemp);
			}

			wResult.sort(Comparator.comparing(MTCTask::getSubmitTime).reversed());
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
		List<MTCTask> wResult = new ArrayList<MTCTask>();
		wErrorCode.set(0);
		try {

			List<MTCTask> wMTCTaskList = new ArrayList<MTCTask>();
			// 获取所有任务消息 模块为移车的
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCMovePart.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCMovePart.getValue(),
									-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());
			if (wTaskIDList != null && wTaskIDList.size() > 0)
				wMTCTaskList = MTCTaskDAO.getInstance().SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime,
						wErrorCode);

			// 所有未完成的任务
			// List<> 查询消息

			List<MTCTask> wMTCTaskUndoneList = new ArrayList<MTCTask>();

			List<Integer> wTaskIDUndoneList = wMTCTaskUndoneList.stream().map(p -> (int) p.ID).distinct()
					.collect(Collectors.toList());
			wTaskIDUndoneList.removeAll(wTaskIDList);
			wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SCMovePart.getValue(), wTaskIDUndoneList, BFCMessageType.Task.getValue(), 1)
					.List(BFCMessage.class);

			wTaskIDUndoneList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());
			for (MTCTask wMTCTask : wMTCTaskUndoneList) {
				if (wTaskIDUndoneList.contains(wMTCTask.ID))
					wMTCTaskList.add(wMTCTask);
			}

			wMTCTaskList.sort(Comparator.comparing(MTCTask::getSubmitTime).reversed());

			wResult = wMTCTaskList;
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
			wResult = new ArrayList<BPMTaskBase>(MTCTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
					wLoginUser.ID, -1, -1, -1, -1, -1, "", wStartTime, wEndTime, wErrorCode));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @return
	 */
	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		MTCTask wResult = new MTCTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			List<MTCTask> wList = SelectList(wLoginUser, StringUtils.parseList(new Integer[] { wTaskID }), -1, -1, -1,
					-1, -1, null, null, -1, -1, -1, -1, "", null, null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wMTCTask
	 * @return
	 */
	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		MTCTask wResult = new MTCTask();
		try {

			if (wTask == null) {
				wErrorCode.set(MESException.Parameter.getValue());
				return wResult;
			}
			ServiceResult<String> wInstance = null;
			MTCTask wTaskS = null;
			if (wTask.getID() <= 0) {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 500601);
			} else {
				wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);

				wTaskS = (MTCTask) this.BPM_GetTaskInfo(wLoginUser, wTask.ID, "", wErrorCode);

			}
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			MTCTask wMTCTask = (MTCTask) wTask;
			if (wTaskS != null && wTaskS.ID > 0) {
				if (wMTCTask.TargetSID != wTaskS.TargetSID) {
					wMTCTask.TargetID = wMTCTask.TargetSID;
				}
				if (wMTCTask.TargetSStockID != wTaskS.TargetSStockID) {
					wMTCTask.TargetStockID = wMTCTask.TargetSStockID;
				}
			}

			if (wMTCTask.Status == MTCStatus.SendTask.getValue()) {
				wMTCTask.CreateTime = Calendar.getInstance();

			}

			String wSQL = "";
			if (wMTCTask.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.mtc_task(FlowType,FlowID,Code,PartNo,"
						+ "	PlaceID,TargetID,TargetStockID,UpFlowID,CreateTime,FollowerID,SubmitTime,Status,StatusText,"
						+ " CustomerID,ExpectedTime,OrderID,Type,CarTypeID,DepartmentID,AreaID,TargetSID,TargetSStockID,"
						+ "InformShift,IsPreMove) "
						+ "	VALUES(:FlowType,:FlowID,:Code,:PartNo,:PlaceID,:TargetID,:TargetStockID,"
						+ "	:UpFlowID,now(), :FollowerID,now(),:Status,:StatusText,:CustomerID,:ExpectedTime,"
						+ ":OrderID,:Type,:CarTypeID,:DepartmentID,:AreaID,:TargetSID,:TargetSStockID,:InformShift,:IsPreMove);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.mtc_task SET FlowType = :FlowType,"
						+ "FlowID = :FlowID,Code = :Code,PartNo = :PartNo,PlaceID = :PlaceID,"
						+ "TargetID = :TargetID,TargetStockID = :TargetStockID,"
						+ " FollowerID = :FollowerID,SubmitTime=now(),Status = :Status,CreateTime = :CreateTime,"
						+ "StatusText = :StatusText,CustomerID = :CustomerID,"
						+ " ExpectedTime = :ExpectedTime,OrderID = :OrderID,Type = :Type,CarTypeID = :CarTypeID,"
						+ "DepartmentID = :DepartmentID,AreaID = :AreaID,TargetSID = :TargetSID,"
						+ "TargetSStockID = :TargetSStockID,InformShift = :InformShift,IsPreMove=:IsPreMove WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMTCTask.ID);
			wParamMap.put("FlowType", wMTCTask.FlowType);
			wParamMap.put("FlowID", wMTCTask.FlowID);
			wParamMap.put("Code", wMTCTask.Code);
			wParamMap.put("InformShift", wMTCTask.InformShift);
			wParamMap.put("PartNo", wMTCTask.PartNo);
			wParamMap.put("PlaceID", wMTCTask.PlaceID);
			wParamMap.put("TargetID", wMTCTask.TargetID);
			wParamMap.put("TargetStockID", wMTCTask.TargetStockID);
			wParamMap.put("UpFlowID", wMTCTask.UpFlowID);
			wParamMap.put("CreateTime", wMTCTask.CreateTime);
			if (wMTCTask.FollowerID == null)
				wMTCTask.FollowerID = new ArrayList<Integer>();
			wParamMap.put("FollowerID", StringUtils.Join(",", wMTCTask.FollowerID));

			wParamMap.put("Status", wMTCTask.Status);
			wParamMap.put("StatusText", wMTCTask.StatusText);
			wParamMap.put("CustomerID", wMTCTask.CustomerID);
			wParamMap.put("ExpectedTime", wMTCTask.ExpectedTime);
			wParamMap.put("OrderID", wMTCTask.OrderID);
			wParamMap.put("Type", wMTCTask.Type);
			wParamMap.put("CarTypeID", wMTCTask.CarTypeID);
			wParamMap.put("DepartmentID", wMTCTask.DepartmentID);
			wParamMap.put("AreaID", wMTCTask.AreaID);
			wParamMap.put("TargetSID", wMTCTask.TargetSID);
			wParamMap.put("TargetSStockID", wMTCTask.TargetSStockID);
			wParamMap.put("IsPreMove", wMTCTask.IsPreMove);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMTCTask.getID() <= 0) {
				wMTCTask.setID(keyHolder.getKey().intValue());
			}
			wResult = wMTCTask;

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	public Boolean AutoSGAudit(BMSEmployee wLoginUser, MTCTask wMTCTask, OutResult<Integer> wErrorCode) {
		Boolean wResult = true;
		try {

			if (wMTCTask == null || wMTCTask.ID <= 0 || wMTCTask.StepID <= 0
					|| wMTCTask.Status == MTCStatus.Completion.getValue()
					|| wMTCTask.Status == MTCStatus.TaskCancel.getValue()
					|| wMTCTask.Status == MTCStatus.TaskReject.getValue()) {
				return wResult;
			}
			ServiceResult<MTCTask> wNewTask = MTCServiceImpl.getInstance().MTC_GetTask(wLoginUser, wMTCTask.ID, "");
			if (wNewTask.Result != null && (wNewTask.Result.Status == MTCStatus.Completion.getValue()
					|| wNewTask.Result.Status == MTCStatus.TaskReject.getValue()
					|| wNewTask.Result.Status == MTCStatus.TaskCancel.getValue())) {
				return wResult;
			}

			// 是否满足条件 否 return false;
			List<BPMOperationStep> wBPMOperationStepList = BPMServiceImpl.getInstance()
					.BPM_GetOperationByTaskID(wLoginUser, wMTCTask.StepID).List(BPMOperationStep.class);

			if (wBPMOperationStepList == null || wBPMOperationStepList.size() <= 0) {
				wResult = false;
				return wResult;
			}

			BPMOperationStep wBPMOperationStep = wBPMOperationStepList.get(0);
			String wStart = "09:00:00";
			String wEnd = "18:00:00";
			if (StringUtils.isNotEmpty(wBPMOperationStep.Documentation)) {

				List<String> strs = new ArrayList<String>();
				Pattern p = Pattern.compile("\\[(\\d{2}\\:\\d{2}\\:\\d{2})\\]", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(wBPMOperationStep.Documentation);
				while (m.find()) {
					strs.add(m.group(1));
				}
				if (strs != null && strs.size() >= 2) {
					wStart = strs.get(0);
					wEnd = strs.get(1);
				}
			}
			if (this.IsInTimeRange(Calendar.getInstance(), wStart, wEnd)) {
				wResult = false;
				return wResult;
			}

			Field[] fields = wMTCTask.getClass().getFields();
			for (Field wField : fields) {
				if (!wField.getName().equals(wBPMOperationStep.Name))
					continue;

				wField.set(wMTCTask, CloneTool.Clone(wBPMOperationStep.Value, wField.getType()));
			}

			ServiceResult<Boolean> bpm_MsgUpdate = BPMServiceImpl.getInstance().BPM_MsgUpdate(wLoginUser,
					wMTCTask.StepID, 0, wMTCTask, wMTCTask);

			if (!bpm_MsgUpdate.getResult() || StringUtils.isNotEmpty(bpm_MsgUpdate.getFaultCode()))
				MTCServiceImpl.getInstance().MTC_SubmitTask(wLoginUser, wMTCTask);

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工位列表获取班组列表
	 */
	public List<Integer> SelectMonitorListByStationIDList(BMSEmployee wLoginUser, List<Integer> wStationIDList,
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
					"select distinct t1.ID from {0}.mbs_user t1," + "{0}.bms_department t2,{0}.bms_position t3 "
							+ "where t1.Position=t3.ID and t3.DutyID=1 " + "and t1.DepartmentID=t2.ID and t2.ID "
							+ "in(SELECT ClassID FROM {0}.bms_workcharge where Active=1 and StationID in({1}));",
					wInstance.Result, StringUtils.Join(",", wStationIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				if (wID > 0) {
					wResult.add(wID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<String> MTC_QueryMTCPartNoList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<String> wResult = new ArrayList<String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select CarTypeID,PartNo from {0}.mtc_task " + "where CarTypeID>0 and Status !=0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wCarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				String wPartNo = StringUtils.parseString(wReader.get("PartNo"));

				String wCarType = WDWConstans.GetFPCProductNo(wCarTypeID);
				String wWholePartNo = StringUtils.Format("{0}#{1}", wCarType, wPartNo);
				if (!wResult.stream().anyMatch(p -> p.equals(wWholePartNo))) {
					wResult.add(wWholePartNo);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
