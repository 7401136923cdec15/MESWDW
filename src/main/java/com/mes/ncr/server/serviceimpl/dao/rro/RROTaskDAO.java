package com.mes.ncr.server.serviceimpl.dao.rro;

import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.rro.RROFrequency;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RRORepairStatus;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.po.rro.RROTaskTypes;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.APSLOCOServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

/**
 * 返修任务数据操作类
 * 
 * @author ShrisJava
 *
 */
public class RROTaskDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(RROTaskDAO.class);

	private static RROTaskDAO Instance = null;

	private RROTaskDAO() {
		super();
	}

	public static RROTaskDAO getInstance() {
		if (Instance == null)
			Instance = new RROTaskDAO();
		return Instance;
	}

	/**
	 * 创建返工单任务
	 * 
	 * @param wSenderID
	 * @return
	 */
	public RROTask RRO_CreateTask(BMSEmployee wLoginUser, RROItemTask wRROItemTask) {
		RROTask wResult = new RROTask();
		try {
			if (wRROItemTask.OrderID <= 0)
				return wResult;
			OMSOrder wOMSOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wRROItemTask.OrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0)
				return wResult;
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.StationID = wRROItemTask.StationID;
			wResult.CarTypeID = wOMSOrder.ProductID;
			wResult.CarNumber = wRROItemTask.CarNumber;
			wResult.LineID = wOMSOrder.LineID;
			wResult.CustomerID = wOMSOrder.CustomerID;
			wResult.OrderID = wOMSOrder.ID;
			wResult.IsDelivery = wRROItemTask.IsDelivery;
			wResult.CheckName = wRROItemTask.CheckName;
			wResult.ID = 0;
			wResult.UpFlowID = wLoginUser.ID;
			wResult.UpFlowName = wLoginUser.Name;
			wResult.ID = this.Update(wLoginUser, wResult, wErrorCode);
		} catch (Exception ex) {

			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 更新或添加
	 * 
	 * @param wRROTask
	 * @return
	 */
	public Integer Update(BMSEmployee wLoginUser, RROTask wRROTask, OutResult<Integer> wErrorCode) {
		Integer wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			if (wRROTask == null)
				return wResult;
			String wSQL = "";
			if (wRROTask.ID <= 0) {
				// 生成新的返修编码
				wRROTask.Code = GetRepairCode(wLoginUser, wErrorCode);
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.rro_task(OrderID,StationID,IsDelivery,UpFlowID,Code,CarTypeID,CarNumber,UpFlowTime,CustomerID,"
								+ "LineID,CheckName) "
								+ "VALUES(:OrderID,:StationID,:IsDelivery,:UpFlowID,:Code,:CarTypeID,:CarNumber,:UpFlowTime,:CustomerID,"
								+ ":LineID,:CheckName);",
						wInstance.Result);
			} else if (wRROTask.ID > 0) {
				wSQL = MessageFormat.format(
						"UPDATE {0}.rro_task SET OrderID = :OrderID,StationID = :StationID,IsDelivery = :IsDelivery,"
								+ "UpFlowID = :UpFlowID,Code = :Code, CarTypeID=:CarTypeID,CarNumber=:CarNumber,UpFlowTime=:UpFlowTime,"
								+ "CustomerID=:CustomerID,LineID=:LineID,CheckName=:CheckName WHERE ID=:wID",
						wInstance.Result);
			}
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wRROTask.ID);
			wParamMap.put("OrderID", wRROTask.OrderID);
			wParamMap.put("StationID", wRROTask.StationID);
			wParamMap.put("IsDelivery", wRROTask.IsDelivery);
			wParamMap.put("UpFlowID", wRROTask.UpFlowID);
			wParamMap.put("Code", wRROTask.Code);
			wParamMap.put("CarTypeID", wRROTask.CarTypeID);
			wParamMap.put("CarNumber", wRROTask.CarNumber);
			wParamMap.put("UpFlowTime", wRROTask.UpFlowTime);
			wParamMap.put("CustomerID", wRROTask.CustomerID);
			wParamMap.put("LineID", wRROTask.LineID);
			wParamMap.put("CheckName", wRROTask.CheckName);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);
			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wRROTask.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wRROTask.setID(wResult);
			} else {
				wResult = wRROTask.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除集合
	 * 
	 * @param wRROTaskList
	 * @return
	 */
	public int DeleteList(BMSEmployee wLoginUser, List<RROTask> wRROTaskList, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			if (wRROTaskList != null && wRROTaskList.size() > 0) {
				StringBuilder wStringBuilder = new StringBuilder();
				for (int i = 0; i < wRROTaskList.size(); i++) {
					if (i == wRROTaskList.size() - 1)
						wStringBuilder.append(wRROTaskList.get(i).ID);
					else
						wStringBuilder.append(wRROTaskList.get(i).ID + ",");
				}
				String wSQL = MessageFormat.format("DELETE From rro_task WHERE ID in({0});", wStringBuilder.toString());
				Map<String, Object> wParamMap = new HashMap<String, Object>();
				nameJdbcTemplate.update(wSQL, wParamMap);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查单条返修任务
	 * 
	 * @return
	 */
	public RROTask SelectByID(BMSEmployee wBMSEmployee, int wID, OutResult<Integer> wErrorCode) {
		RROTask wResult = new RROTask();
		try {
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<RROTask> wRROTaskList = SelectList(wBMSEmployee, wIDList, -1, "", -1, -1, -1, -1, wCalendar, wCalendar,
					-1, -1, wErrorCode);
			if (wRROTaskList != null && wRROTaskList.size() > 0)
				wResult = wRROTaskList.get(0);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查单条返修任务
	 * 
	 * @return
	 */
	public RROTask SelectTask(BMSEmployee wLoginUser, int wOrderID, int wStationID, int wIsDelivery, int wUpFlowID,
			int wCarTypeID, String wCarNumber, OutResult<Integer> wErrorCode) {
		RROTask wResult = new RROTask();
		try {
			if (wOrderID <= 0)
				wOrderID = -1;
			if (wStationID <= 0)
				wStationID = -1;
			if (wUpFlowID <= 0)
				wUpFlowID = -1;
			List<RROTask> wRROTaskList = this.SelectList(wLoginUser, null, wIsDelivery, wCarNumber, wCarTypeID,
					wUpFlowID, wStationID, wOrderID, null, null, -1, -1, wErrorCode);
			if (wRROTaskList != null && wRROTaskList.size() > 0)
				wResult = wRROTaskList.get(0);
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
	public List<RROTask> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wIsDelivery, String wCarNumber,
			int wCarTypeID, int wUpFlowID, int wStationID, int wOrderID, Calendar wStartTime, Calendar wEndTime,
			int wLineID, int wCustomerID, OutResult<Integer> wErrorCode) {
		List<RROTask> wResultList = new ArrayList<RROTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wIDList == null)
				wIDList = new ArrayList<Integer>();

			wIDList.removeIf(p -> p <= 0);

			if (wCarNumber == null)
				wCarNumber = "";

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResultList;

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.rro_task WHERE 1=1 and(:wCarNumber = '''' or CarNumber= :wCarNumber)"
							+ " and(:wIsDelivery <=0 or IsDelivery= :wIsDelivery)"
							+ " and(:wCarTypeID <=0 or CarTypeID= :wCarTypeID)"
							+ " and(:wUpFlowID <=0 or UpFlowID= :wUpFlowID)"
							+ " and(:wStationID <=0 or StationID= :wStationID)"
							+ " and(:wLineID <=0 or LineID= :wLineID)" + " and(:wOrderID <=0 or OrderID= :wOrderID)"
							+ " and(:wCustomerID <=0 or CustomerID= :wCustomerID)"
							+ " and (:wID is null or :wID = '''' or ID in ({1}))"
							+ " and(:wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or UpFlowTime>= :wStartTime)"
							+ " and(:wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or UpFlowTime<= :wEndTime)",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wIsDelivery", wIsDelivery);
			wParamMap.put("wCarNumber", wCarNumber);
			wParamMap.put("wCarTypeID", wCarTypeID);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wOrderID", wOrderID);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			if (wQueryResult != null && wQueryResult.size() > 0) {

				for (Map<String, Object> wReader : wQueryResult) {
					RROTask wRROTask = new RROTask();
					wRROTask.ID = StringUtils.parseInt(wReader.get("ID"));
					wRROTask.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
					wRROTask.Code = StringUtils.parseString(wReader.get("Code"));
					wRROTask.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
					wRROTask.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
					wRROTask.StationID = StringUtils.parseInt(wReader.get("StationID"));
					wRROTask.CheckName = StringUtils.parseString(wReader.get("CheckName"));
					wRROTask.IsDelivery = StringUtils.parseInt(wReader.get("IsDelivery"));
					wRROTask.IsDeliveryName = RROTaskTypes.getEnumType(wRROTask.IsDelivery).getLable();
					wRROTask.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
					wRROTask.LineID = StringUtils.parseInt(wReader.get("LineID"));
					wRROTask.OrderID = StringUtils.parseInt(wReader.get("OrderID"));

					wRROTask.UpFlowName = WDWConstans.GetBMSEmployeeName(wRROTask.UpFlowID);
					wRROTask.CarTypeName = WDWConstans.GetFPCProductName(wRROTask.CarTypeID);
					wRROTask.CustomerName = WDWConstans.GetCRMCustomerName(wRROTask.CustomerID);
					wRROTask.LineName = WDWConstans.GetFMCLineName(wRROTask.LineID);
					wRROTask.StationName = WDWConstans.GetFPCPartName(wRROTask.StationID);

					// 获取返修项
					wRROTask.RepairItemList = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1,
							(int) wRROTask.ID, null, -1, -1, -1, -1, -1, null, null, -1, wErrorCode);

					wResultList.add(wRROTask);
				}
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 条件查询返修项
	 * 
	 * @return
	 */
	public Map<String, Integer> SelectItemTaskList(BMSEmployee wLoginUser, int wCarType, int wLineID, int wPartID,
			int wLimit, OutResult<Integer> wErrorCode) {
		Map<String, Integer> wResult = new HashMap<String, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select o.Content ,count(t.Content) as I_Count  FROM  (  "
					+ " select distinct t.Content FROM   {0}.rro_repairitem t , {0}.rro_task t1  "
					+ " where t1.ID=t.TaskID and t1.CarTypeID= :wCarType AND t1.LineID=:wLineID AND t.StationID= :wPartID and t.Status=:wStatus ) o , "
					+ "  {0}.rro_repairitem t , {0}.rro_task t1  "
					+ " where t1.ID=t.TaskID and t1.CarTypeID= :wCarType AND t1.LineID=:wLineID AND t.StationID= :wPartID and t.Status=:wStatus "
					+ "  and o.Content=t.Content group by o.Content order by I_Count desc limit {1};", wInstance.Result,
					wLimit);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wCarType", wCarType);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wStatus", RRORepairStatus.Confirmed.getValue());

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			if (wQueryResult != null && wQueryResult.size() > 0) {
				for (Map<String, Object> wReader : wQueryResult) {
					wResult.put(StringUtils.parseString(wReader.get("Content")),
							StringUtils.parseInt(wReader.get("I_Count")));
				}
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 时间段查询工位返修发生频次
	 * 
	 * @return
	 */
	public List<RROFrequency> SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<RROFrequency> wResultList = new ArrayList<RROFrequency>();

		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					" select LineID,StationID ,count(ID) AS Frequency FROM  {0}.rro_task where 1=1 and(:wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or UpFlowTime>= :wStartTime) and(:wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or UpFlowTime<= :wEndTime) group by LineID,StationID ;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			if (wQueryResult != null && wQueryResult.size() > 0) {
				for (Map<String, Object> wReader : wQueryResult) {
					RROFrequency wItem = new RROFrequency();
					wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
					if (wItem.LineID <= 0)
						continue;
					wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));

					if (wItem.StationID <= 0)
						continue;
					wItem.Frequency = StringUtils.parseInt(wReader.get("Frequency"));
					wResultList.add(wItem);
				}
			}
			// 获取所有工位
			Map<Object, FPCPart> wFPCPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1)
					.List(FPCPart.class).stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			if (wResultList != null && wResultList.size() > 0) {
				if (wFPCPartList != null && wFPCPartList.size() > 0) {
					for (RROFrequency wItem : wResultList) {
						if (wFPCPartList.containsKey(wItem.StationID)) {
							wItem.Station = wFPCPartList.get(wItem.StationID).Name;
						}
					}
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 获取返修编号
	 */
//	public String GetRepairCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
//		String wResult = "";
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//			String wSQL = MessageFormat.format(
//					"select Code from {0}.rro_task where id in( select Max(ID) from {0}.rro_task);", wInstance.Result);
//			wSQL = this.DMLChange(wSQL);
//			Map<String, Object> wParamMap = new HashMap<String, Object>();
//			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
//			String wCode = "";
//			for (Map<String, Object> wReader : wQueryResult) {
//				wCode = StringUtils.parseString(wReader.get("Code"));
//			}
//			if (wCode.equals("")) {
//				String wNumber = String.format("%04d", 1);
//				wResult = StringUtils.Format("RO-{0}{1}-{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
//						wResult = StringUtils.Format("RO-{0}{1}-{2}",
//								String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
//								String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
//								String.format("%04d", Integer.parseInt(wNumber)));
//					} else {
//						String wNumber = String.format("%04d", 1);
//						wResult = StringUtils.Format("RO-{0}{1}-{2}",
//								String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
//								String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
//								String.format("%04d", Integer.parseInt(wNumber)));
//					}
//				}
//			}
//		} catch (Exception e) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			logger.error(e.toString());
//		}
//		return wResult;
//	}
	
	/**
	 * 获取下一个编号
	 */
	public String GetRepairCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
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
					"select count(*)+1 as Number from {0}.rro_task where UpFlowTime > :wSTime and UpFlowTime < :wETime;",
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

			wResult = StringUtils.Format("RO{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
