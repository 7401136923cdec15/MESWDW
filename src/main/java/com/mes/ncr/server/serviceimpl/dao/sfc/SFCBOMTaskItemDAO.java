package com.mes.ncr.server.serviceimpl.dao.sfc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.SFCBOMTaskResponsibility;
import com.mes.ncr.server.service.mesenum.SFCBOMTaskReviewComments;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.ncr.NCRLevel;
import com.mes.ncr.server.service.po.sfc.SFCBOMTaskItem;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;

public class SFCBOMTaskItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCBOMTaskItemDAO.class);

	private static SFCBOMTaskItemDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCBOMTaskItem
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCBOMTaskItem wSFCBOMTaskItem, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCBOMTaskItem == null)
				return 0;

			String wSQL = "";
			if (wSFCBOMTaskItem.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.sfc_bomtaskitem(SFCBOMTaskID,BOMID,BOMItemID,MaterialID,"
						+ "MaterialNumber,UnitID,Level,Responsibility,SapType,ReviewComments,Status,"
						+ "Remark,OperatorID,Operator,OperateTime,Quota,QualityLossBig,QualityLossSmall,ShiftID) "
						+ "VALUES(:SFCBOMTaskID,:BOMID,:BOMItemID,:MaterialID,:MaterialNumber,"
						+ ":UnitID,:Level,:Responsibility,:SapType,:ReviewComments,:Status,:Remark,"
						+ ":OperatorID,:Operator,:OperateTime,:Quota,:QualityLossBig,:QualityLossSmall,:ShiftID);",
						wInstance.Result);
			} else {
				// 虚拟驳回时，修改操作时间
				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2010, 0, 1, 0, 0, 0);
				if (wSFCBOMTaskItem.OperateTime.compareTo(wBaseTime) < 0 && wSFCBOMTaskItem.Status == 2) {
					wSFCBOMTaskItem.OperateTime = Calendar.getInstance();
					wSFCBOMTaskItem.OperatorID = wLoginUser.ID;
					wSFCBOMTaskItem.Operator = wLoginUser.Name;
				}

				wSQL = StringUtils.Format("UPDATE {0}.sfc_bomtaskitem SET SFCBOMTaskID = :SFCBOMTaskID,"
						+ "BOMID = :BOMID,BOMItemID = :BOMItemID,MaterialID = :MaterialID,"
						+ "MaterialNumber = :MaterialNumber,UnitID = :UnitID,Level = :Level,"
						+ "Responsibility = :Responsibility,"
						+ "SapType = :SapType,ReviewComments=:ReviewComments,Status=:Status,Remark=:Remark,"
						+ "OperatorID=:OperatorID,Operator=:Operator,OperateTime=:OperateTime,Quota=:Quota,"
						+ "QualityLossBig=:QualityLossBig,QualityLossSmall=:QualityLossSmall,ShiftID=:ShiftID WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCBOMTaskItem.ID);
			wParamMap.put("SFCBOMTaskID", wSFCBOMTaskItem.SFCBOMTaskID);
			wParamMap.put("BOMID", wSFCBOMTaskItem.BOMID);
			wParamMap.put("BOMItemID", wSFCBOMTaskItem.BOMItemID);
			wParamMap.put("MaterialID", wSFCBOMTaskItem.MaterialID);
			wParamMap.put("MaterialNumber", wSFCBOMTaskItem.MaterialNumber);
			wParamMap.put("UnitID", wSFCBOMTaskItem.UnitID);
			wParamMap.put("Level", wSFCBOMTaskItem.Level);
			wParamMap.put("Responsibility", wSFCBOMTaskItem.Responsibility);
			wParamMap.put("SapType", wSFCBOMTaskItem.SapType);
			wParamMap.put("ReviewComments", wSFCBOMTaskItem.ReviewComments);
			wParamMap.put("Status", wSFCBOMTaskItem.Status);
			wParamMap.put("Remark", wSFCBOMTaskItem.Remark);
			wParamMap.put("OperatorID", wSFCBOMTaskItem.OperatorID);
			wParamMap.put("Operator", wSFCBOMTaskItem.Operator);
			wParamMap.put("OperateTime", wSFCBOMTaskItem.OperateTime);
			wParamMap.put("Quota", wSFCBOMTaskItem.Quota);
			wParamMap.put("QualityLossBig", wSFCBOMTaskItem.QualityLossBig);
			wParamMap.put("QualityLossSmall", wSFCBOMTaskItem.QualityLossSmall);
			wParamMap.put("ShiftID", wSFCBOMTaskItem.ShiftID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCBOMTaskItem.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCBOMTaskItem.setID(wResult);
			} else {
				wResult = wSFCBOMTaskItem.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCBOMTaskItem> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCBOMTaskItem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_bomtaskitem WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @return
	 */
	public SFCBOMTaskItem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCBOMTaskItem wResult = new SFCBOMTaskItem();
		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
//					wLoginUser.getID(), 0);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}

			List<SFCBOMTaskItem> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<SFCBOMTaskItem> SelectList(BMSEmployee wLoginUser, int wID, int wSFCBOMTaskID,
			OutResult<Integer> wErrorCode) {
		List<SFCBOMTaskItem> wResultList = new ArrayList<SFCBOMTaskItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.*,t2.MaterialName,t2.MaterialNo,t3.Name as UnitText "
							+ "FROM {0}.sfc_bomtaskitem t1,{1}.mss_material t2,{1}.cfg_unit t3 WHERE  1=1 "
							+ "and t1.MaterialID=t2.ID and t1.UnitID=t3.ID  " + "and ( :wID <= 0 or :wID = t1.ID ) "
							+ "and ( :wSFCBOMTaskID <= 0 or :wSFCBOMTaskID = t1.SFCBOMTaskID );",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wSFCBOMTaskID", wSFCBOMTaskID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCBOMTaskItem wItem = new SFCBOMTaskItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.SFCBOMTaskID = StringUtils.parseInt(wReader.get("SFCBOMTaskID"));
				wItem.BOMID = StringUtils.parseInt(wReader.get("BOMID"));
				wItem.BOMItemID = StringUtils.parseInt(wReader.get("BOMItemID"));
				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.MaterialNumber = StringUtils.parseDouble(wReader.get("MaterialNumber"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.Level = StringUtils.parseInt(wReader.get("Level"));
				wItem.Responsibility = StringUtils.parseInt(wReader.get("Responsibility"));
				wItem.SapType = StringUtils.parseInt(wReader.get("SapType"));
				wItem.ReviewComments = StringUtils.parseInt(wReader.get("ReviewComments"));

				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.UnitText = StringUtils.parseString(wReader.get("UnitText"));

				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.QualityLossBig = StringUtils.parseString(wReader.get("QualityLossBig"));
				wItem.QualityLossSmall = StringUtils.parseString(wReader.get("QualityLossSmall"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.Operator = StringUtils.parseString(wReader.get("Operator"));
				wItem.OperateTime = StringUtils.parseCalendar(wReader.get("OperateTime"));
				wItem.Quota = StringUtils.parseInt(wReader.get("Quota"));
				wItem.ShiftID = StringUtils.parseString(wReader.get("ShiftID"));

				wItem.LevelName = NCRLevel.getEnumType(wItem.Level).getLable();
				wItem.ResponsibilityName = SFCBOMTaskResponsibility.getEnumType(wItem.Responsibility).getLable();
				wItem.ReviewCommentsName = SFCBOMTaskReviewComments.getEnumType(wItem.ReviewComments).getLable();

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCBOMTaskItemDAO() {
		super();
	}

	public static SFCBOMTaskItemDAO getInstance() {
		if (Instance == null)
			Instance = new SFCBOMTaskItemDAO();
		return Instance;
	}

	/**
	 * 查询数量
	 */
	public double SelectUsedNumber(BMSEmployee wLoginUser, int orderID, int partID, int partPointID, int materialID,
			OutResult<Integer> wErrorCode) {
		double wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT sum(t1.MaterialNumber) Number FROM "
					+ "{0}.sfc_bomtaskitem t1,{0}.sfc_bomtask t2 "
					+ "where t1.SFCBOMTaskID = t2.ID and t1.MaterialID=:MaterialID and t2.OrderID=:OrderID and t1.Status !=2 "
					+ "and t2.PartID=:PartID and t2.PartPointID=:PartPointID and t2.Status not in (0,21,22,23);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("MaterialID", materialID);
			wParamMap.put("OrderID", orderID);
			wParamMap.put("PartID", partID);
			wParamMap.put("PartPointID", partPointID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseDouble(wReader.get("Number"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
