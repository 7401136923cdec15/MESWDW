package com.mes.ncr.server.controller.rro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.APSLOCOService;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.FMCService;
import com.mes.ncr.server.service.LFSService;
import com.mes.ncr.server.service.NCRService;
import com.mes.ncr.server.service.RROService;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPartPoint;
import com.mes.ncr.server.service.po.rro.RROFrequency;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RROItemTaskShow;
import com.mes.ncr.server.service.po.rro.RROPart;
import com.mes.ncr.server.service.po.rro.RROPartNo;
import com.mes.ncr.server.service.po.rro.RROPartTaskShow;
import com.mes.ncr.server.service.po.rro.RROStatus;
import com.mes.ncr.server.service.po.rro.RROTableBody;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.RetCode;

@RestController
@RequestMapping("/api/RRO")
public class RROController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(RROController.class);

	@Autowired
	RROService wRROService;
	@Autowired
	LFSService wLFSService;
	@Autowired
	NCRService wNCRService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	FMCService wFMCService;
	@Autowired
	APSLOCOService wAPSLOCOService;

	/**
	 * ???????????????????????? JugdeItemClose
	 */
	@GetMapping("/QuerySendType")
	public Object QuerySendType(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			ServiceResult<Map<Integer, String>> wServiceResult = wRROService.RRO_QuerySendType(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????
	 */
	@GetMapping("/QueryItemByOrderID")
	public Object QueryItemByOrderID(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryNotFinishItemList(wLoginUser,
					wOrderID);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				if (wServiceResult.Result == null)
					wServiceResult.Result = new ArrayList<RROItemTask>();
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????
	 */
	@GetMapping("/JugdeItemClose")
	public Object JugdeItemClose(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<Boolean> wServiceResult = wRROService.RRO_JugdeItemClose(wLoginUser, wOrderID, wPartID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	@GetMapping("/SpecialItemAll")
	public Object SpecialItemAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wTaskStepID = StringUtils.parseInt(request.getParameter("TaskStepID"));

			List<Integer> AllItemList = new ArrayList<Integer>();
			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemTaskList(wLoginUser, null, -1,
					-1, -1, null, -1, -1, wTaskStepID, -1, -1, null, null);

			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
				for (RROItemTask wRROItemTask : wServiceResult.Result) {
					AllItemList.add(wRROItemTask.IPTItemID);
				}
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, AllItemList);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????ID????????????????????????????????????????????????????????????
	 */
	@GetMapping("/QueryProcess")
	public Object QueryProcess(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<List<FPCPartPoint>> wServiceResult = wRROService.RRO_QueryProcess(wLoginUser, wOrderID,
					wPartID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

//			List<FPCRoutePartPoint> wFPCRoutePartPointList = new ArrayList<FPCRoutePartPoint>();
//			FPCRoute wFPCProductRoute = null;
//			if (wOrderID > 0) {
//				OMSOrder wOMSOrder = wAPSLOCOService.OMS_QueryOrderByID(wLoginUser, wOrderID).Info(OMSOrder.class);
//				wFPCProductRoute = wFMCService.FPC_QueryRouteByID(wLoginUser, wOMSOrder.RouteID).Info(FPCRoute.class);
//				if (wFPCProductRoute != null && wFPCProductRoute.ID > 0) {
//					wFPCRoutePartPointList = wFMCService
//							.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wFPCProductRoute.ID, wPartID)
//							.List(FPCRoutePartPoint.class);
//				}
//			}
//			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wFPCRoutePartPointList, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	@GetMapping("/PartNoAll")
	public Object PartNoAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);

			int wIsSend = StringUtils.parseInt(request.getParameter("IsSend"));

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RROItemTaskShow>> wServiceResult = wRROService.RRO_QueryRROItemTaskShowList(wBMSEmployee,
					wIsSend, wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<RROItemTaskShow>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				return wResult;
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????????????????
	 */
	@GetMapping("/AllItemList")
	public Object AllItemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wTaskStepID = StringUtils.parseInt(request.getParameter("Type"));

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wIsSend = StringUtils.parseInt(request.getParameter("IsSend"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_ItemTaskList(wBMSEmployee, wOrderID,
					wTaskStepID, wIsSend, wStartTime, wEndTime);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 */
	@GetMapping("/PartItemAll")
	public Object PartItemAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			int wIsSend = StringUtils.parseInt(request.getParameter("IsSend"));

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RROPartTaskShow>> wServiceResult = wRROService.RRO_QueryPartTaskList(wBMSEmployee,
					wOrderID, wIsSend, wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<RROPartTaskShow>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				return wResult;
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	@GetMapping("/PartItemList")
	public Object PartItemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wTaskStepID = StringUtils.parseInt(request.getParameter("Type"));

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wIsSend = StringUtils.parseInt(request.getParameter("IsSend"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_PartUndoTaskList(wBMSEmployee, wOrderID,
					wPartID, wTaskStepID, wIsSend, wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<RROItemTask>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????(?????????)
	 */
	@GetMapping("/PartItemListNew")
	public Object PartItemListNew(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wIsSend = StringUtils.parseInt(request.getParameter("IsSend"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_PartUndoTaskListNew(wBMSEmployee,
					wOrderID, wPartID, wIsSend, wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<RROItemTask>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			int wTagTypes = StringUtils.parseInt(request.getParameter("TagTypes"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			Calendar wTempCalendar = Calendar.getInstance();
			wTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);// ??????????????????
			wTempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// ??????
			wStartTime.set(wTempCalendar.get(Calendar.YEAR), wTempCalendar.get(Calendar.MONTH),
					wTempCalendar.get(Calendar.DATE), 0, 0, 0);
			wTempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// ??????
			wEndTime.set(wTempCalendar.get(Calendar.YEAR), wTempCalendar.get(Calendar.MONTH),
					wTempCalendar.get(Calendar.DATE), 23, 59, 59);
//			ServiceResult<List<RROTask>> wServiceSendResult = new ServiceResult<List<RROTask>>();
			ServiceResult<List<BPMTaskBase>> wServiceResult = new ServiceResult<List<BPMTaskBase>>();
			if (wTagTypes == TagTypes.Applicant.getValue()) {
				wServiceResult = wRROService.RRO_QueryApplicantTaskList(wBMSEmployee, wTagTypes, null, null);
				if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
					wServiceResult.Result = wServiceResult.Result.stream()
							.filter(p -> p.Status != RROStatus.Default.getValue()).collect(Collectors.toList());
				}
				if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
					return wResult;
				} else {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
				}
			} else {
				wServiceResult = wRROService.RRO_QueryTaskListByTagTypes(wBMSEmployee, wTagTypes, null, null);
				if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
					wServiceResult.Result = new ArrayList<BPMTaskBase>();
				if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
					return wResult;
				} else {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
				}
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode()))
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			else {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "???????????????????????????");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????ID?????????????????????
	 */
	@GetMapping("/ItemInfo")
	public Object ItemInfo(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			if (wID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			ServiceResult<RROItemTask> wServiceResult = wRROService.RRO_QueryItemTaskByID(wBMSEmployee, wID);
			if (StringUtils.isNotEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
				return wResult;
			}
			RROItemTask wReturnObject = wServiceResult.getResult();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wReturnObject);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	@PostMapping("/TimeAll")
	public Object TimeAll(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wCustomerID = StringUtils.parseInt(request.getParameter("CustomerID"));
			int wCarTypeID = StringUtils.parseInt(request.getParameter("CarTypeID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wSendID = StringUtils.parseInt(request.getParameter("SendID"));
			int wIsDelivery = StringUtils.parseInt(request.getParameter("IsDelivery"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RROTask>> wServiceResult = wRROService.RRO_QueryTaskList(wLoginUser, null, wIsDelivery,
					wPartNo, wCarTypeID, wSendID, wStationID, wOrderID, wStartTime, wEndTime, wLineID, wCustomerID);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 */
	@PostMapping("/TimeAllPro")
	public Object TimeAllPro(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wStationID = StringUtils.parseInt(wParam.get("StationID"));
			int wLineID = StringUtils.parseInt(wParam.get("LineID"));
			int wCustomerID = StringUtils.parseInt(wParam.get("CustomerID"));
			int wCarTypeID = StringUtils.parseInt(wParam.get("CarTypeID"));
			String wPartNo = StringUtils.parseString(wParam.get("PartNo"));
			int wSendID = StringUtils.parseInt(wParam.get("SendID"));
			int wIsDelivery = StringUtils.parseInt(wParam.get("IsDelivery"));
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			Calendar wStartTime = StringUtils.parseCalendar(wParam.get("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(wParam.get("EndTime"));

			ServiceResult<List<RROTask>> wServiceResult = wRROService.RRO_QueryTaskProList(wLoginUser, null,
					wIsDelivery, wPartNo, wCarTypeID, wSendID, wStationID, wOrderID, wStartTime, wEndTime, wLineID,
					wCustomerID);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	@PostMapping("/QueryItemDis")
	public Object QueryItemDis(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			RROTask wRROTask = CloneTool.Clone(wParam.get("data"), RROTask.class);
			ServiceResult<List<RROTableBody>> wServiceResult = wRROService.RRO_QueryBodyList(wLoginUser, wRROTask);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????
	 */
	@GetMapping("/QueryItemTaskList")
	public Object QueryItemTaskList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wCarType = StringUtils.parseInt(request.getParameter("CarType"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wLimit = StringUtils.parseInt(request.getParameter("Limit"));
			ServiceResult<Map<String, Integer>> wMapResult = wRROService.RRO_SelectItemTaskList(wLoginUser, wCarType,
					wLineID, wPartID, wLimit);
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wMapResult.Result);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????
	 */
	@GetMapping("/QueryFrequency")
	public Object QueryFrequency(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RROFrequency>> wRROFrequencyList = wRROService.RRO_SelectFrequency(wLoginUser,
					wStartTime, wEndTime);

			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wRROFrequencyList.Result, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????IDList???????????????
	 */
	@PostMapping("/QueryItemTaskList")
	public Object QueryItemTaskList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			List<Integer> wIDList = CloneTool.CloneArray(wParam.get("IDList"), Integer.class);
			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemTaskList(wLoginUser, wIDList, -1,
					-1, -1, null, -1, -1, -1, -1, -1, null, null);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 */
	@ResponseBody
	@GetMapping("/BatchExportPdf")
	public Object BatchExportPdf(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wRROService.BatchExportPdf(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "?????????????????????!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????
	 */
	@GetMapping("/ItemTaskList")
	public Object ItemTaskList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ????????????
			int wOrdreID = StringUtils.parseInt(request.getParameter("OrdreID"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemTaskList(wLoginUser, wOrdreID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	@GetMapping("/ItemTaskAll")
	public Object ItemTaskAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ????????????
			int wUpFlowID = StringUtils.parseInt(request.getParameter("UpFlowID"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wCustomerID = StringUtils.parseInt(request.getParameter("CustomerID"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemTaskAll(wLoginUser, wUpFlowID,
					wOrderID, wStationID, wStartTime, wEndTime, wCustomerID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	@GetMapping("/EmployeeAllWeb")
	public Object EmployeeAllWeb(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wCustomerID = StringUtils.parseInt(request.getParameter("CustomerID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wIsDelivery = StringUtils.parseInt(request.getParameter("IsDelivery"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemTaskList(wLoginUser, wOrderID,
					wLineID, wCustomerID, wProductID, wPartNo, wIsDelivery, wPartID, wStartTime, wEndTime, wStatus);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	@PostMapping("/Update")
	public Object Update(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ????????????
			RROItemTask wData = CloneTool.Clone(wParam.get("data"), RROItemTask.class);

			ServiceResult<RROItemTask> wServiceResult = wRROService.RRO_UpdateItemTask(wLoginUser, wData);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????
	 */
	@GetMapping("/PartNoRepairList")
	public Object PartNoRepairList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RROPartNo>> wServiceResult = wRROService.RRO_QueryPartNoRepairList(wLoginUser,
					wStartTime, wEndTime);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????
	 */
	@GetMapping("/PartRepairList")
	public Object PartRepairList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<RROPart>> wServiceResult = wRROService.RRO_QueryPartRepairList(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	@GetMapping("/ItemListByOrder")
	public Object ItemListByOrder(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ????????????
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			if (wOrderID <= 0 || wPartID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "?????????????????????!");
			}

			ServiceResult<List<RROItemTask>> wServiceResult = wRROService.RRO_QueryItemListByOrder(wLoginUser, wOrderID,
					wPartID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	@GetMapping("/GetAreaID")
	public Object GetAreaID(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wRROService.RRO_GetAreaID(wLoginUser);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ???????????????????????????(????????????)
	 */
	@GetMapping("/CodeReset")
	public Object CodeReset(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wRROService.RRO_CodeReset(wLoginUser);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 */
	@GetMapping("/LoginInfo")
	public Object LoginInfo(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wRROService.RRO_QueryLoginInfo(wLoginUser);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.CustomResult.get("AuditorList"),
						wServiceResult.Result);
				this.SetResult(wResult, "MonitorList", wServiceResult.CustomResult.get("MonitorList"));
				this.SetResult(wResult, "AreaName", wServiceResult.CustomResult.get("AreaName"));
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}
