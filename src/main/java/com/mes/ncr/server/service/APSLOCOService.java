package com.mes.ncr.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.Configuration;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月31日23:52:51
 * @LastEditTime 2019年12月31日23:52:55
 *
 */
public interface APSLOCOService {
	static String ServerUrl = Configuration.readConfigString("aps.server.url", "config/config");
	static String ServerName = Configuration.readConfigString("aps.server.project.name", "config/config");

	APIResult SFC_QueryTaskListByLogin(BMSEmployee wLoginUser);

	APIResult SFC_QueryTaskStepByID(BMSEmployee wLoginUser, int wTaskStepID);

	APIResult SFC_QuerySFCTaskIPTByID(BMSEmployee wLoginUser, int wTaskStepID);

	APIResult APS_QueryWorkTimeZone(BMSEmployee wLoginUser, Calendar wCheckTime);

	APIResult SFC_OMSOrderByID(BMSEmployee wLoginUser, int wOrderID);

	APIResult SCH_QueryTaskListByID(BMSEmployee wLoginUser, int wID, int wSecondAuditID, int wBeSecondAuditID,
			int wSecondDepartmentID, int wSecondPersonID, Calendar wStartTime, Calendar wEndTime);

	APIResult OMS_QueryOrderByID(BMSEmployee wLoginUser, int wID);

	APIResult OMS_QueryOrderListByStatus(BMSEmployee wLoginUser, List<Integer> wStatusList);

	APIResult OMS_QueryTurnOrderListByIDList(BMSEmployee wLoginUser, List<Integer> wIDList);

	APIResult OMS_QueryCarfListByStationID(BMSEmployee wLoginUser, int wStationID);
}
