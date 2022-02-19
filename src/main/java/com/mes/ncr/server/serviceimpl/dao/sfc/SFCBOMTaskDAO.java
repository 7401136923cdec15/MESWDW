package com.mes.ncr.server.serviceimpl.dao.sfc;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.sfc.SFCBOMTask;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;

/**
 * 偶换件不合格评审
 */
public class SFCBOMTaskDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCBOMTaskDAO.class);

	private static SFCBOMTaskDAO Instance = null;

	private SFCBOMTaskDAO() {
		super();
	}

	public static SFCBOMTaskDAO getInstance() {
		if (Instance == null)
			Instance = new SFCBOMTaskDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wSFCBOMTask
	 * @return
	 */
	public SFCBOMTask Update(BMSEmployee wLoginUser, SFCBOMTask wSFCBOMTask, OutResult<Integer> wErrorCode) {
		SFCBOMTask wResult = new SFCBOMTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCBOMTask == null)
				return wResult;

			if (wSFCBOMTask.FollowerID == null) {
				wSFCBOMTask.FollowerID = new ArrayList<Integer>();
			}

			String wSQL = "";
			if (wSFCBOMTask.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.sfc_bomtask(Code,FlowType,FlowID,UpFlowID,FollowerID,Status,"
						+ "StatusText,CreateTime,SubmitTime,OrderID,PartID,PartPointID,BOMID,"
						+ "BOMItemID,MaterialID,MaterialNumber,UnitID,Level,Disposal,ReviewComments,Responsibility,"
						+ "CraftsmanIDs,TechnicalEngineerIDs,SapType,SAPStatus,SAPStatusText,SRPartID,SRPartName,SRProductNo,ConfirmedLevels,ImageUrl,IsLGL) VALUES(:Code,:FlowType,:FlowID,"
						+ ":UpFlowID,:FollowerID,:Status,:StatusText,:CreateTime,:SubmitTime,:OrderID,"
						+ ":PartID,:PartPointID,:BOMID,:BOMItemID,:MaterialID,:MaterialNumber,:UnitID,:Level,"
						+ ":Disposal,:ReviewComments,:Responsibility,:CraftsmanIDs,:TechnicalEngineerIDs,:SapType,:SAPStatus,:SAPStatusText,:SRPartID,:SRPartName,:SRProductNo,:ConfirmedLevels,:ImageUrl,:IsLGL);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.sfc_bomtask SET Code = :Code,FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,"
						+ "Status = :Status,StatusText = :StatusText,CreateTime = :CreateTime,"
						+ "SubmitTime = now(),OrderID = :OrderID,PartID = :PartID,"
						+ "PartPointID = :PartPointID,BOMID = :BOMID,BOMItemID = :BOMItemID,"
						+ "MaterialID = :MaterialID,MaterialNumber = :MaterialNumber,UnitID = :UnitID,"
						+ "Level = :Level,Disposal=:Disposal,ReviewComments=:ReviewComments,"
						+ "Responsibility=:Responsibility,CraftsmanIDs=:CraftsmanIDs,"
						+ "TechnicalEngineerIDs=:TechnicalEngineerIDs,SapType=:SapType,"
						+ "SAPStatus=:SAPStatus,SAPStatusText=:SAPStatusText,SRPartID=:SRPartID,"
						+ "SRPartName=:SRPartName,SRProductNo=:SRProductNo,ConfirmedLevels=:ConfirmedLevels,ImageUrl=:ImageUrl,IsLGL=:IsLGL WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCBOMTask.ID);
			wParamMap.put("Code", wSFCBOMTask.Code);
			wParamMap.put("FlowType", wSFCBOMTask.FlowType);
			wParamMap.put("FlowID", wSFCBOMTask.FlowID);
			wParamMap.put("UpFlowID", wSFCBOMTask.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wSFCBOMTask.FollowerID));
			wParamMap.put("Status", wSFCBOMTask.Status);
			wParamMap.put("StatusText", wSFCBOMTask.StatusText);
			wParamMap.put("CreateTime", wSFCBOMTask.CreateTime);
			wParamMap.put("SubmitTime", wSFCBOMTask.SubmitTime);
			wParamMap.put("OrderID", wSFCBOMTask.OrderID);
			wParamMap.put("PartID", wSFCBOMTask.PartID);
			wParamMap.put("PartPointID", wSFCBOMTask.PartPointID);
			wParamMap.put("BOMID", wSFCBOMTask.BOMID);
			wParamMap.put("BOMItemID", wSFCBOMTask.BOMItemID);
			wParamMap.put("MaterialID", wSFCBOMTask.MaterialID);
			wParamMap.put("MaterialNumber", wSFCBOMTask.MaterialNumber);
			wParamMap.put("UnitID", wSFCBOMTask.UnitID);
			wParamMap.put("Level", wSFCBOMTask.Level);
			wParamMap.put("Disposal", wSFCBOMTask.Disposal);
			wParamMap.put("ReviewComments", wSFCBOMTask.ReviewComments);
			wParamMap.put("Responsibility", wSFCBOMTask.Responsibility);
			wParamMap.put("CraftsmanIDs", wSFCBOMTask.CraftsmanIDs);
			wParamMap.put("TechnicalEngineerIDs", wSFCBOMTask.TechnicalEngineerIDs);
			wParamMap.put("SapType", wSFCBOMTask.SapType);
			wParamMap.put("SAPStatus", wSFCBOMTask.SAPStatus);
			wParamMap.put("SAPStatusText", wSFCBOMTask.SAPStatusText);
			wParamMap.put("SRPartID", wSFCBOMTask.SRPartID);
			wParamMap.put("SRPartName", wSFCBOMTask.SRPartName);
			wParamMap.put("SRProductNo", wSFCBOMTask.SRProductNo);
			wParamMap.put("ConfirmedLevels", wSFCBOMTask.ConfirmedLevels);
			wParamMap.put("ImageUrl", wSFCBOMTask.ImageUrl);
			wParamMap.put("IsLGL", wSFCBOMTask.IsLGL);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCBOMTask.getID() <= 0) {
				wSFCBOMTask.setID(keyHolder.getKey().intValue());
			}
			wResult = wSFCBOMTask;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

}
