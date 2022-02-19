package com.mes.ncr.server.serviceimpl.dao.mtc;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.mtc.MTCSectionInfo;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

public class MTCSectionInfoDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MTCSectionInfoDAO.class);

	private static MTCSectionInfoDAO Instance = null;

	/**
	 * 车辆节信息更新
	 * 
	 * @param wMTCSectionInfo
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MTCSectionInfo wMTCSectionInfo, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMTCSectionInfo == null)
				return 0;

			String wSQL = "";
			if (wMTCSectionInfo.getID() <= 0) {
				wSQL = MessageFormat
						.format("INSERT INTO {0}.mtc_sectioninfo(Name,ProductID,ProductNo,CreateID,CreateTime) "
								+ "VALUES(:Name,:ProductID,:ProductNo,:CreateID,:CreateTime);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.mtc_sectioninfo SET Name = :Name,ProductID = :ProductID,"
						+ "ProductNo = :ProductNo,CreateID = :CreateID,CreateTime = :CreateTime WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMTCSectionInfo.ID);
			wParamMap.put("Name", wMTCSectionInfo.Name);
			wParamMap.put("ProductID", wMTCSectionInfo.ProductID);
			wParamMap.put("ProductNo", wMTCSectionInfo.ProductNo);
			wParamMap.put("CreateID", wMTCSectionInfo.CreateID);
			wParamMap.put("CreateTime", wMTCSectionInfo.CreateTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMTCSectionInfo.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMTCSectionInfo.setID(wResult);
			} else {
				wResult = wMTCSectionInfo.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除车辆节信息
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MTCSectionInfo> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (MTCSectionInfo wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mtc_sectioninfo WHERE ID IN({0}) ;",
					StringUtils.Join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 主键查询车辆节信息
	 * 
	 * @return
	 */
	public MTCSectionInfo SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MTCSectionInfo wResult = new MTCSectionInfo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MTCSectionInfo> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	 * 条件查询车辆节信息
	 * 
	 * @return
	 */
	public List<MTCSectionInfo> SelectList(BMSEmployee wLoginUser, int wID, int wProductID,
			OutResult<Integer> wErrorCode) {
		List<MTCSectionInfo> wResultList = new ArrayList<MTCSectionInfo>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mtc_sectioninfo WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wProductID <= 0 or :wProductID = ProductID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProductID", wProductID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MTCSectionInfo wItem = new MTCSectionInfo();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Name = StringUtils.parseString(wReader.get("Name"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wItem.Creator = WDWConstans.GetBMSEmployeeName(wItem.CreateID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private MTCSectionInfoDAO() {
		super();
	}

	public static MTCSectionInfoDAO getInstance() {
		if (Instance == null)
			Instance = new MTCSectionInfoDAO();
		return Instance;
	}
}
