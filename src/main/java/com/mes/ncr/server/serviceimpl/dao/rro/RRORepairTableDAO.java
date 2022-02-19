package com.mes.ncr.server.serviceimpl.dao.rro;

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
import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.crm.CRMCustomer;
import com.mes.ncr.server.service.po.fmc.FMCLine;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RRORepairTable;
import com.mes.ncr.server.service.po.rro.RROTableBody;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.po.rro.RROTaskTypes;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;

public class RRORepairTableDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(RRORepairTableDAO.class);

	private static RRORepairTableDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wRRORepairTable
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, RRORepairTable wRRORepairTable, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			if (wRRORepairTable == null)
				return 0;
			String wSQL = "";
			if (wRRORepairTable.getID() <= 0) {
				wSQL = MessageFormat
						.format("INSERT INTO {0}.rro_repairtable(CarTypeID,CarNumber,LineID,Type,CustomerID,"
								+ "SenderID,SendTime,ApprovalID,ApprovalTime,Status)"
								+ "VALUES(:CarTypeID,:CarNumber,:LineID,:Type,:CustomerID,:SenderID,"
								+ ":SendTime,:ApprovalID,:ApprovalTime,:Status);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.rro_repairtable SET CarTypeID = :CarTypeID,CarNumber = :CarNumber,LineID = :LineID,Type = :Type,CustomerID = :CustomerID,SenderID = :SenderID,"
								+ "SendTime = :SendTime,ApprovalID = :ApprovalID,ApprovalTime=:ApprovalTime,"
								+ "Status=:Status " + "WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("ID", wRRORepairTable.ID);
			wParamMap.put("CarTypeID", wRRORepairTable.CarTypeID);
			wParamMap.put("CarNumber", wRRORepairTable.CarNumber);
			wParamMap.put("LineID", wRRORepairTable.LineID);
			wParamMap.put("Type", wRRORepairTable.Type);
			wParamMap.put("CustomerID", wRRORepairTable.CustomerID);
			wParamMap.put("SenderID", wRRORepairTable.SenderID);
			wParamMap.put("SendTime", wRRORepairTable.SendTime);
			wParamMap.put("ApprovalID", wRRORepairTable.ApprovalID);
			wParamMap.put("ApprovalTime", wRRORepairTable.ApprovalTime);
			wParamMap.put("Status", wRRORepairTable.Status);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wRRORepairTable.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wRRORepairTable.setID(wResult);
			} else {
				wResult = wRRORepairTable.getID();
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
	 * @param wList
	 */
	public void DeleteList(BMSEmployee wLoginUser, List<RRORepairTable> wList, OutResult<Integer> wErrorCode) {
		try {
			if (wList == null || wList.size() <= 0)
				return;

			List<String> wIDList = new ArrayList<String>();
			for (RRORepairTable wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from rro_repairtable WHERE ID IN({0}) ;",
					String.join(",", wIDList));
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
	}

	/**
	 * ID查单条
	 * 
	 * @return
	 */
	public RRORepairTable SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		RRORepairTable wResult = new RRORepairTable();
		try {
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			List<RRORepairTable> wResultList = SelectList(wLoginUser, wIDList, -1, "", -1, -1, -1, -1, -1, -1,
					wCalendar, wCalendar, wErrorCode);
			if (wResultList != null && wResultList.size() > 0)
				wResult = wResultList.get(0);
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
	public List<RRORepairTable> SelectList(BMSEmployee wLoginUser, List<Integer> wIDList, int wCarTypeID,
			String wCarNumber, int wLineID, int wType, int wCustomerID, int wSenderID, int wApprovalID, int wStatus,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<RRORepairTable> wResultList = new ArrayList<RRORepairTable>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wIDList == null)
				wIDList = new ArrayList<Integer>();

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.rro_repairtable WHERE  1=1  "
							+ "and ( :wCarTypeID <= 0 or :wCarTypeID = CarTypeID ) "
							+ " and (:wID is null or :wID = '''' or ID in ({1}))"
							+ " and ( :wCarNumber is null or :wCarNumber = '''' or :wCarNumber = CarNumber )"
							+ "and ( :wLineID <= 0 or :wLineID = LineID ) " + "and ( :wType <= 0 or :wType = Type ) "
							+ "and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
							+ "and ( :wSenderID <= 0 or :wSenderID = SenderID ) "
							+ "and ( :wApprovalID <= 0 or :wApprovalID = ApprovalID ) "
							+ "and ( :wStatus <= 0 or :wStatus = Status )"
							+ " and(:wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or SendTime>= :wStartTime)"
							+ " and(:wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or SendTime<= :wEndTime);",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wIDList));
			wParamMap.put("wCarTypeID", wCarTypeID);
			wParamMap.put("wCarNumber", wCarNumber);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wType", wType);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wSenderID", wSenderID);
			wParamMap.put("wApprovalID", wApprovalID);
			wParamMap.put("wStatus", wStatus);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				RRORepairTable wItem = new RRORepairTable();
				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.CarTypeID = StringUtils.parseInt(wReader.get("CarTypeID"));
				wItem.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.SenderID = StringUtils.parseInt(wReader.get("SenderID"));
				wItem.SendTime = StringUtils.parseCalendar(wReader.get("SendTime"));
				wItem.ApprovalID = StringUtils.parseInt(wReader.get("ApprovalID"));
				wItem.ApprovalTime = StringUtils.parseCalendar(wReader.get("ApprovalTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wResultList.add(wItem);
			}

			if (wResultList != null && wResultList.size() > 0) {
				// 获取所有修程
				Map<Object, FMCLine> wFMCLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(wLoginUser, -1, -1, -1)
						.List(FMCLine.class).stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				// 获取所有车型
				Map<Object, FPCProduct> wFPCProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
						.List(FPCProduct.class).stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				// 获取所有人员
				Map<Object, BMSEmployee> wEmployeeList = CoreServiceImpl.getInstance()
						.BMS_GetEmployeeAll(wLoginUser, -1, -1, 1).List(BMSEmployee.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				// 获取所有局段
				Map<Object, CRMCustomer> wCRMCustomerList = CoreServiceImpl.getInstance()
						.CRM_QueryCustomerList(wLoginUser, "", -1, -1, -1, -1).List(CRMCustomer.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				for (RRORepairTable wRRORepairTable : wResultList) {
					// 查询表中间部分
					wRRORepairTable.RROTableBodyList = QueryBody(wLoginUser,wRRORepairTable.CarTypeID, wRRORepairTable.CarNumber,
							wRRORepairTable.Type);
					if (wFMCLineList.containsKey(wRRORepairTable.LineID)) {
						wRRORepairTable.LineName = wFMCLineList.get(wRRORepairTable.LineID).Name;
					}
					if (wCRMCustomerList.containsKey(wRRORepairTable.CustomerID)) {
						wRRORepairTable.CustomerName = wCRMCustomerList.get(wRRORepairTable.CustomerID).CustomerName;
					}
					if (wFPCProductList.containsKey(wRRORepairTable.CarTypeID)) {
						wRRORepairTable.CarTypeName = wFPCProductList.get(wRRORepairTable.CarTypeID).ProductNo;
					}
					if (wEmployeeList.containsKey(wRRORepairTable.SenderID)) {
						wRRORepairTable.SenderName = wEmployeeList.get(wRRORepairTable.SenderID).Name;
					}
					if (wEmployeeList.containsKey(wRRORepairTable.ApprovalID)) {
						wRRORepairTable.ApprovalName = wEmployeeList.get(wRRORepairTable.ApprovalID).Name;
					}
					// 任务类型
					switch (RROTaskTypes.getEnumType(wRRORepairTable.Type)) {
					case CheckRepair:
						wRRORepairTable.TypeName = "验收返修";
						break;
					case Delivery:
						wRRORepairTable.TypeName = "试运前返修";
						break;
					case IsDelivery:
						wRRORepairTable.TypeName = "过程检返修";
						break;
					case PilotRun:
						wRRORepairTable.TypeName = "试运后返修";
						break;
					case Supplier:
						wRRORepairTable.TypeName = "供应商返修";
						break;
					default:
						break;
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
	 * 车号与任务类型查询表单内容
	 * 
	 * @return
	 */
	public List<RROTableBody> QueryBody(BMSEmployee wLoginUser,int wCarTypeID, String wPartNo, int wType) {
		List<RROTableBody> wResultList = new ArrayList<RROTableBody>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<RROTask> wRROTaskList = RROTaskDAO.getInstance().SelectList(wLoginUser, null, wType, wPartNo,wCarTypeID, 
					-1, -1, -1,wCalendar, wCalendar, -1,-1, wErrorCode);
			if (wRROTaskList != null && wRROTaskList.size() > 0) {
				for (RROTask wItem : wRROTaskList) {
					if (wItem.RepairItemList != null && wItem.RepairItemList.size() > 0) {
						for (RROItemTask wRROItemTask : wItem.RepairItemList) {
							RROTableBody wRROTableBody = new RROTableBody();
							wRROTableBody.Describe = wRROItemTask.Content;
							wRROTableBody.WorkAreaName = wRROItemTask.WorkAreaName;
							wRROTableBody.TeamName = StringUtils.Format("{0}/{1}", wRROItemTask.TeamName,wRROItemTask.StationName);
							wRROTableBody.PersonLiable = wRROItemTask.OperatorName;
//							wRROTableBody.InspectorName = wItem.SenderName;
//							wRROTableBody.Date = StringUtils.parseCalendarToString(wItem.ConfirmTaskTime,
//									wRROTableBody.Date);
							wRROTableBody.ProcessName = wRROItemTask.ProcesName;
							wRROTableBody.Signature = wRROItemTask.OperatorName;
							wRROTableBody.Remark =wRROItemTask.Remark;
							wRROTableBody.Result = wRROItemTask.IsStatus;//1：合格2：不合格
							wResultList.add(wRROTableBody);
						}
					}
				}
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private RRORepairTableDAO() {
		super();
	}

	public static RRORepairTableDAO getInstance() {
		if (Instance == null)
			Instance = new RRORepairTableDAO();
		return Instance;
	}
}
