package com.mes.ncr.server.serviceimpl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.RemoteInvokeUtils;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月27日12:45:42
 * @LastEditTime 2019年12月27日12:45:47
 *
 */
@Service
public class CoreServiceImpl implements CoreService {
	private static Logger logger = LoggerFactory.getLogger(CoreServiceImpl.class);

	public CoreServiceImpl() {
	}

	private static CoreService Instance;

	public static CoreService getInstance() {
		if (Instance == null)
			Instance = new CoreServiceImpl();
		return Instance;
	}

	@Override
	public APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_id", wLoginName);
			wParms.put("passWord", wPassword);
			wParms.put("token", wToken);
			wParms.put("PhoneMac", wMac);
			wParms.put("netJS", wMac);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, "api/User/Login", wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("active", wActive);
			wParms.put("DepartmentID", wDepartmentID);
			wParms.put("Position", wPosition);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/User/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_info", wID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/User/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 权限编码判断登录人是否有权限
	 * 
	 * @param wLoginUser
	 * @return
	 */
	@Override
	public APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID,
			int wTypeID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("AuthortyID", wFunctionID);
			wParms.put("RangeID", wRangeID);
			wParms.put("TypeID", wTypeID);
			wParms.put("CompanyID", wCompanyID);
			wParms.put("UserID", wUserID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, "api/Role/Check", wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询权限范围
	 * 
	 * @param wLoginUser
	 * @return
	 */
	@Override
	public APIResult BMS_FunctionRangeAll(BMSEmployee wLoginUser, int wOperatorID, int wFunctionID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OperatorID", wOperatorID);
			wParms.put("FunctionID", wFunctionID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Role/FunctionRangeAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询拥有权限列表
	 * 
	 * @param wLoginUser
	 * @return
	 */
	@Override
	public APIResult BMS_FunctionAll(BMSEmployee wLoginUser, int wOperatorID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OperatorID", wOperatorID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Role/FunctionAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_UserAllByFunction(BMSEmployee wLoginUser, int wFunctionID) {

		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FunctionID", wFunctionID);
			wResult = RemoteInvokeUtils.getInstance()
					.HttpInvokeAPI(ServerUrl, ServerName,
							StringUtils.Format("api/Role/UserAllByFunctionID?cadv_ao={0}&cade_po={1}",
									BaseDAO.SysAdmin.getLoginName(), BaseDAO.SysAdmin.getPassword()),
							wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_UserAllByRoleID(BMSEmployee wLoginUser, int wRoleID) {

		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("role_id", wRoleID);

			wResult = RemoteInvokeUtils.getInstance()
					.HttpInvokeAPI(ServerUrl, ServerName,
							StringUtils.Format("api/Role/UserAll?cadv_ao={0}&cade_po={1}",
									BaseDAO.SysAdmin.getLoginName(), BaseDAO.SysAdmin.getPassword()),
							wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取岗位列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @return
	 */
	@Override
	public APIResult BMS_QueryPositionList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("api/Department/AllPosition?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryPositionByID(BMSEmployee wLoginUser, int wPositionID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("Code", 1);
			wParms.put("ID", wPositionID);
			String wUri = StringUtils.Format("/api/Department/InfoPosition?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取部门列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @param wLoginID
	 * @return
	 */
	public APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("/api/Department/AllDepartment?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wYear, int wWorkShopID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("year", wYear);
			wParms.put("WorkShopID", wWorkShopID);
			String wUri = StringUtils.Format("/api/Holiday/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryRegionList(BMSEmployee wLoginUser) {

		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("/api/Area/All?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 根据部门ID获取部门实体
	 * 
	 * @param wDepartmentID
	 * @return
	 */
	public APIResult BMS_QueryDepartmentByID(BMSEmployee wLoginUser, int wDepartmentID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wDepartmentID);
			String wUri = StringUtils.Format("/api/Department/InfoDepartment?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCMessageList);
			wParms.put("Send", 0);
			String wUri = StringUtils.Format("/api/HomePage/MsgUpdate?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_SendMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCMessageList);
			wParms.put("Send", 1);
			String wUri = StringUtils.Format("/api/HomePage/MsgUpdate?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser, String wCustomerName, int wCountryID,
			int wProvinceID, int wCityID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("customer_name", wCustomerName);
			wParms.put("country_id", wCountryID);
			wParms.put("province_id", wProvinceID);
			wParms.put("city_id", wCityID);
			wParms.put("active", wActive);

			String wUri = StringUtils.Format("/api/Customer/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID,
			int wType, int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("MessageID", wMessageID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StairtTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_HandleMessage(BMSEmployee wLoginUser, int wResponsorID, List<Long> wMsgIDList, int wModuleID,
			int wStatus) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("MessageIDList", wMsgIDList);
			wParms.put("ModuleID", wModuleID);
			wParms.put("Status", wStatus);
			wParms.put("ResponsorID", wResponsorID);
			String wUri = StringUtils.Format("/api/HomePage/MsgHandle?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_MsgHandleTask(BMSEmployee wLoginUser, int wModuleID, int wStatus, List<Long> wTaskIDList,
			int wResponsorID, int wIsAuto, int wStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ModuleID", wModuleID);
			wParms.put("Status", wStatus);
			wParms.put("TaskIDList", wTaskIDList);
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("IsAuto", wIsAuto);
			wParms.put("StepID", wStepID);
			String wUri = StringUtils.Format("/api/HomePage/MsgHandleTask?cadv_ao={0}&cade_po={1}",
					wLoginUser.LoginName, wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID,
			List<Integer> wMessageID, int wType, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", -1);
			wParms.put("MessageID", StringUtils.Join(",", wMessageID));
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryWorkChargeList(BMSEmployee wLoginUser, int wStationID, int wClassID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StationID", wStationID);
			wParms.put("Active", wActive);
			wParms.put("ClassID", wClassID);

			String wUri = StringUtils.Format("api/WorkCharge/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QuerySupplierList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("supplier_name", "");
			wParms.put("country_id", 0);
			wParms.put("province_id", 0);
			wParms.put("city_id", 0);
			wParms.put("active", -1);

			String wUri = StringUtils.Format("api/Supplier/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult GetMonitorList(BMSEmployee wLoginUser, int departmentID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("PositionType", 1);
			wParms.put("DepartmentID", departmentID);

			String wUri = StringUtils.Format("api/User/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
