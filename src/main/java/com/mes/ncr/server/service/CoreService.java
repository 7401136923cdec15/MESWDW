package com.mes.ncr.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.Configuration;

public interface CoreService {

	static String ServerUrl = Configuration.readConfigString("core.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("core.server.project.name", "config/config");

	APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS);

	APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive);

	APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID);

	APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID, int wTypeID);

	APIResult BMS_FunctionRangeAll(BMSEmployee wLoginUser, int wOperatorID, int wFunctionID);

	APIResult BMS_FunctionAll(BMSEmployee wLoginUser, int wOperatorID);

	APIResult BMS_UserAllByFunction(BMSEmployee wLoginUser, int wFunctionID);

	APIResult BMS_QueryPositionList(BMSEmployee wLoginUser);

	APIResult BMS_QueryPositionByID(BMSEmployee wLoginUser, int wPositionID);

	APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser);

	APIResult BMS_QueryDepartmentByID(BMSEmployee wLoginUser, int wDepartmentID);

	APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wYear, int wWorkShopID);

	APIResult CFG_QueryRegionList(BMSEmployee wLoginUser);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList);

	APIResult BFC_SendMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList);

	APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser, String wCustomerName, int wCountryID, int wProvinceID,
			int wCityID, int wActive);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, List<Integer> wMessageID,
			int wType, int wActive);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID, int wType,
			int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult BFC_HandleMessage(BMSEmployee wLoginUser, int wResponsorID, List<Long> wMsgIDList, int wModuleID,
			int wStatus);

	APIResult BFC_MsgHandleTask(BMSEmployee wLoginUser, int wModuleID, int wStatus, List<Long> wTskIDList,
			int wResponsorID, int wIsAuto, int wStepID);

	APIResult BMS_QueryWorkChargeList(BMSEmployee wLoginUser, int wStationID, int wClassID, int wActive);

	APIResult BMS_UserAllByRoleID(BMSEmployee wLoginUser, int wRoleID);

	APIResult SCM_QuerySupplierList(BMSEmployee wLoginUser);

	APIResult GetMonitorList(BMSEmployee wLoginUser, int departmentID);
}
