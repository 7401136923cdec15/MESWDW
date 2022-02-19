package com.mes.ncr.server.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.QMSService;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.RemoteInvokeUtils;
import com.mes.ncr.server.service.utils.StringUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-31 19:01:59
 * @LastEditTime 2020-1-8 10:29:23
 *
 */
@Service
public class QMSServiceImpl implements QMSService {
	private static Logger logger = LoggerFactory.getLogger(QMSServiceImpl.class);

	public QMSServiceImpl() {
	}

	private static QMSService Instance;

	public static QMSService getInstance() {
		if (Instance == null)
			Instance = new QMSServiceImpl();
		return Instance;
	}

	/**
	 * 查询转序单列表
	 */
	@Override
	public APIResult RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOrderID, int wApplyStationID,
			int wTargetStationID, List<Integer> wStateIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OrderID", wOrderID);
			wParms.put("ApplyStationID", wApplyStationID);
			wParms.put("TargetStationID", wTargetStationID);
			wParms.put("StateIDList", wStateIDList);
			String wUri = StringUtils.Format("api/RSMTurnOrderTask/All?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 用人获取获取专检任务列表
	 */
	public APIResult SFC_QuerySpecialTaskListByLogin(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("api/SFCTaskIPT/ZuanJTaskList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 用ID取获取单条派工任务
	 */
	public APIResult SFC_QueryTaskByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			String wUri = StringUtils.Format("api/SFCTaskIPT/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 用任务获取所有检验项
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public APIResult SFC_QueryItemListByID(BMSEmployee wLoginUser, int wStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("SFCTaskStepID", wStepID);
			String wUri = StringUtils.Format("api/SFCTaskIPTItem/TaskItemAll?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_TaskIPTHandCheckItem(BMSEmployee wLoginUser, int wTaskIPTID, int wIPTItemID,
			int wAssessResult) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("TaskIPTID", wTaskIPTID);
			wParms.put("IPTItemID", wIPTItemID);
			wParms.put("AssessResult", wAssessResult);
			String wUri = StringUtils.Format("api/SFCTaskIPT/HandleSelfCheckItem?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
