package com.mes.ncr.server.serviceimpl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.APSLOCOService;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.RemoteInvokeUtils;
import com.mes.ncr.server.service.utils.StringUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月27日12:44:48
 * @LastEditTime 2020-4-2 16:48:39
 *
 */

@Service
public class APSLOCOServiceImpl implements APSLOCOService {
	private static Logger logger = LoggerFactory.getLogger(APSLOCOServiceImpl.class);

	public APSLOCOServiceImpl() {
	}

	private static APSLOCOService Instance;

	public static APSLOCOService getInstance() {
		if (Instance == null)
			Instance = new APSLOCOServiceImpl();
		return Instance;
	}

	/**
	 * 用人获取获取派工任务
	 * 
	 * @param
	 * @param
	 * @return
	 */
	@Override
	public APIResult SFC_QueryTaskListByLogin(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ShiftDate", Calendar.getInstance());
			String wUri = StringUtils.Format("api/SFCTaskStep/EmployeeAll?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QuerySFCTaskIPTByID(BMSEmployee wLoginUser, int wTaskStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wTaskStepID);
			String wUri = StringUtils.Format("api/SFCTaskIPT/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QueryTaskStepByID(BMSEmployee wLoginUser, int wTaskStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wTaskStepID);
			String wUri = StringUtils.Format("api/SFCTaskStep/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryWorkTimeZone(BMSEmployee wLoginUser, Calendar wCheckTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("CheckTime", wCheckTime);
			String wUri = StringUtils.Format("api/SFCTaskStep/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_OMSOrderByID(BMSEmployee wLoginUser, int wOrderID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wOrderID);
			String wUri = StringUtils.Format("api/OMSOrder/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询借调单
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public APIResult SCH_QueryTaskListByID(BMSEmployee wLoginUser, int wID, int wSecondAuditID, int wBeSecondAuditID,
			int wSecondDepartmentID, int wSecondPersonID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			wParms.put("SecondAuditID", wSecondAuditID);
			wParms.put("BeSecondAuditID", wBeSecondAuditID);
			wParms.put("SecondDepartmentID", wSecondDepartmentID);
			wParms.put("SecondPersonID", wSecondPersonID);
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("api/SCHSecondment/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 订单ID获取订单
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public APIResult OMS_QueryOrderByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			String wUri = StringUtils.Format("api/OMSOrder/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 订单任务状态集合获取订单
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public APIResult OMS_QueryOrderListByStatus(BMSEmployee wLoginUser, List<Integer> wStatusList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StatusList", wStatusList);
			String wUri = StringUtils.Format("api/OMSOrder/StatusAll?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 订单ID集合查询所有转序单
	 */
	public APIResult OMS_QueryTurnOrderListByIDList(BMSEmployee wLoginUser, List<Integer> wIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wIDList);
			String wUri = StringUtils.Format("api/OMSOrder/IDList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 工位ID获取工艺员List
	 */
	public APIResult OMS_QueryCarfListByStationID(BMSEmployee wLoginUser, int wStationID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StationID", wStationID);
			String wUri = StringUtils.Format("api/SFCBOMTask/StationPerson?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
