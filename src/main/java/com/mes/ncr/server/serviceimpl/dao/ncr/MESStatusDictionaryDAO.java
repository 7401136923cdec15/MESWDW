package com.mes.ncr.server.serviceimpl.dao.ncr;

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
import com.mes.ncr.server.service.po.ncr.MESStatusDictionary;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;

public class MESStatusDictionaryDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MESStatusDictionaryDAO.class);

	private static MESStatusDictionaryDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wMESStatusDictionary
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MESStatusDictionary wMESStatusDictionary, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMESStatusDictionary == null)
				return 0;

			String wSQL = "";
			if (wMESStatusDictionary.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.mes_statusdictionary(Key,Value,ModuleID) VALUES(:Key,:Value,:ModuleID);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.mes_statusdictionary SET Key = :Key,Value = :Value,ModuleID = :ModuleID WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMESStatusDictionary.ID);
			wParamMap.put("Key", wMESStatusDictionary.Key);
			wParamMap.put("Value", wMESStatusDictionary.Value);
			wParamMap.put("ModuleID", wMESStatusDictionary.ModuleID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMESStatusDictionary.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMESStatusDictionary.setID(wResult);
			} else {
				wResult = wMESStatusDictionary.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MESStatusDictionary> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (MESStatusDictionary wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mes_statusdictionary WHERE ID IN({0}) ;",
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
	public MESStatusDictionary SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MESStatusDictionary wResult = new MESStatusDictionary();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MESStatusDictionary> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<MESStatusDictionary> SelectList(BMSEmployee wLoginUser, int wID, int wModuleID,
			OutResult<Integer> wErrorCode) {
		List<MESStatusDictionary> wResultList = new ArrayList<MESStatusDictionary>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mes_statusdictionary WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wModuleID <= 0 or :wModuleID = ModuleID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wModuleID", wModuleID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MESStatusDictionary wItem = new MESStatusDictionary();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Key = StringUtils.parseString(wReader.get("Key"));
				wItem.Value = StringUtils.parseString(wReader.get("Value"));
				wItem.ModuleID = StringUtils.parseInt(wReader.get("ModuleID"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<MESStatusDictionary> SelectList(BMSEmployee wLoginUser, List<Integer> wModuleIDList,
			OutResult<Integer> wErrorCode) {
		List<MESStatusDictionary> wResultList = new ArrayList<MESStatusDictionary>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wModuleIDList == null || wModuleIDList.size() <= 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.mes_statusdictionary WHERE  1=1  "
							+ "and ( :wModuleID is null or :wModuleID = '''' or ModuleID in ({1}));",
					wInstance.Result, StringUtils.Join(",", wModuleIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wModuleID", StringUtils.Join(",", wModuleIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MESStatusDictionary wItem = new MESStatusDictionary();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Key = StringUtils.parseString(wReader.get("Key"));
				wItem.Value = StringUtils.parseString(wReader.get("Value"));
				wItem.ModuleID = StringUtils.parseInt(wReader.get("ModuleID"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private MESStatusDictionaryDAO() {
		super();
	}

	public static MESStatusDictionaryDAO getInstance() {
		if (Instance == null)
			Instance = new MESStatusDictionaryDAO();
		return Instance;
	}
}
