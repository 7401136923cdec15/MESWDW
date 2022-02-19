package com.mes.ncr.server.controller.ncr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
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
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCRoute;
import com.mes.ncr.server.service.po.fpc.FPCRoutePart;
import com.mes.ncr.server.service.po.ncr.NCRCarInfo;
import com.mes.ncr.server.service.po.ncr.NCRFrequency;
import com.mes.ncr.server.service.po.ncr.NCRPartTaskShow;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.NCRTaskPro;
import com.mes.ncr.server.service.po.ncr.NCRTaskShow;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.sfc.SFCTaskStep;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.APSLOCOServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.NCRServiceImpl;
import com.mes.ncr.server.utils.RetCode;

@RestController
@RequestMapping("/api/NCR")
public class NCRController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(NCRController.class);
	@Autowired
	NCRService wNCRService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	APSLOCOService wAPSLOCOService;
	@Autowired
	FMCService wFMCService;
	@Autowired
	LFSService wLFSService;

	/**
	 * 根据登录人获取所在工区的工区主管
	 */
	@GetMapping("/GetDepartment")
	public Object GetDepartment(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<UserWorkArea>> wServiceResult = wNCRService.NCR_GetDepartment(wLoginUser);

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
	 * 工序任务ID查询已发起不合格评审的检验项
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

			ServiceResult<List<Integer>> wServiceResult = wNCRService.NCR_GetSendIPTItenList(wLoginUser, wTaskStepID);

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
	 * 通过派工任务ID获取当前工位之后的所有工位
	 */
	@GetMapping("/GetStationList")
	public Object GetStationList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wTaskStepID = StringUtils.parseInt(request.getParameter("TaskStepID"));

			if (wTaskStepID == 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			SFCTaskStep wServiceResult = APSLOCOServiceImpl.getInstance().SFC_QueryTaskStepByID(wLoginUser, wTaskStepID)
					.Info(SFCTaskStep.class);
			FPCRoute wFPCRoute = new FPCRoute();
			ServiceResult<List<Integer>> wPartIDList = new ServiceResult<List<Integer>>();
			if (wServiceResult != null && wServiceResult.PartID > 0) {

				OMSOrder wOMSOrder = APSLOCOServiceImpl.getInstance().SFC_OMSOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				if (wOMSOrder != null) {
					wFPCRoute = FMCServiceImpl.getInstance().FPC_QueryRouteByID(wLoginUser, wOMSOrder.RouteID)
							.Info(FPCRoute.class);

					List<FPCRoutePart> wFPCRoutePartList = FMCServiceImpl.getInstance()
							.FPC_QueryRouteByID(wLoginUser, wFPCRoute.getID()).List(FPCRoutePart.class);
					// 获取当前工位后续工位列表
					wPartIDList = NCRServiceImpl.getInstance().NCR_GetStationList(wLoginUser, wFPCRoutePartList,
							wServiceResult.PartID);
				}
			}
			if (wPartIDList.Result != null && wPartIDList.Result.size() > 0) {
				// 获取当前工位的后续工位
				List<FPCPart> wFPCPartList = wFMCService.FPC_QueryPartList(wLoginUser, -1, -1, -1).List(FPCPart.class);
				List<FPCPart> wDonList = new ArrayList<FPCPart>();
				if (wPartIDList.Result != null && wPartIDList.Result.size() > 0 && wFPCPartList != null
						&& wFPCPartList.size() > 0) {
					for (Integer wItem : wPartIDList.Result) {
						if (wItem == wServiceResult.PartID)
							continue;
						Optional<FPCPart> wOptional = wFPCPartList.stream().filter(p -> p.ID == wItem).findFirst();
						if (wOptional != null && wOptional.isPresent())
							wDonList.add(wOptional.get());
					}
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wDonList, wServiceResult);
				}
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "未查到下级工位！");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 用人拿任务
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

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<NCRTaskShow>> wServiceResult = wNCRService.NCR_QueryNCRTaskShowList(wBMSEmployee,
					wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRTaskShow>();
			else {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(
										() -> new TreeSet<>(Comparator.comparing(NCRTaskShow::getOrderID))),
								ArrayList::new));

			}
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
	 * 待做任务列表，已做任务列表
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

			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.NCR_UndoTaskList(wBMSEmployee, wOrderID,
					wTaskStepID, wStartTime, wEndTime);

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
	 * 工位待做已做任务
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

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<NCRPartTaskShow>> wServiceResult = wNCRService.RRO_QueryPartNCRTaskList(wBMSEmployee,
					wOrderID, wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRPartTaskShow>();
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
	 * 工位下待做任务列表，已做任务列表
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

			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.RRO_PartUndoNCRTaskList(wBMSEmployee, wOrderID,
					wPartID, wTaskStepID, wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRTask>();
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
	 * 工位下待做任务列表，已做任务列表、发起列表(整合版)
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

			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.RRO_PartUndoNCRTaskListNew(wBMSEmployee, wOrderID,
					wPartID, wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRTask>();
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
	 * 用人拿任务
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

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);

			if (wEndTime.compareTo(wBaseTime) <= 0)
				wEndTime = Calendar.getInstance();

			if (wStartTime.compareTo(wBaseTime) <= 0) {
				wStartTime = (Calendar) wEndTime.clone();
				wStartTime.add(Calendar.DAY_OF_MONTH, -3);
			}
			if (wStartTime.compareTo(wEndTime) >= 0) {
				wStartTime = (Calendar) wEndTime.clone();
				wStartTime.add(Calendar.DAY_OF_MONTH, -3);
			}

			if (wTagTypes == 0)
				return GetResult(RetCode.SERVER_CODE_SUC, "参数错误!");

			ServiceResult<List<BPMTaskBase>> wServiceResult = wNCRService.NCR_QueryTaskListByTagType(wBMSEmployee,
					wTagTypes, wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<BPMTaskBase>();
			else {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
								ArrayList::new));
				wServiceResult.Result = wServiceResult.Result.stream()
						.filter(p -> p.Status != NCRStatus.Default.getValue()).collect(Collectors.toList());
			}
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
	 * 用人拿任务
	 */
	@GetMapping("/EmployeeAllWeb")
	public Object EmployeeAllWeb(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wFlowType = StringUtils.parseInt(request.getParameter("FlowType"));
			int wSendType = StringUtils.parseInt(request.getParameter("SendType"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wCustomerID = StringUtils.parseInt(request.getParameter("CustomerID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wLevel = StringUtils.parseInt(request.getParameter("Level"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wCarNumber = StringUtils.parseString(request.getParameter("CarNumber"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));
			String wIsRelease = StringUtils.parseString(request.getParameter("IsRelease"));

			ServiceResult<List<NCRTask>> wServiceResult = null;
			if (wSendType > 0) {
				wServiceResult = wNCRService.NCR_QueryTaskListBySendType(wBMSEmployee, wSendType, wOrderID, wLineID,
						wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, wStatus,
						wIsRelease);
			} else {
				wServiceResult = wNCRService.NCR_QueryTaskList(wBMSEmployee, wFlowType, wOrderID, wLineID, wCustomerID,
						wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, wStatus, wIsRelease);
			}

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
	 * 条件查询任务
	 */
	@PostMapping("/TimeAll")
	public Object TimeAll(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(wParam.get("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(wParam.get("EndTime"));
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			int wCustomerID = StringUtils.parseInt(wParam.get("CustomerID"));
			int wLineID = StringUtils.parseInt(wParam.get("LineID"));
			int wStationID = StringUtils.parseInt(wParam.get("StationID"));
			int wSenderID = StringUtils.parseInt(wParam.get("SenderID"));
			int wLevel = StringUtils.parseInt(wParam.get("Level"));
			int wCarTypeID = StringUtils.parseInt(wParam.get("CarTypeID"));
			String wCarNumber = StringUtils.parseString(wParam.get("CarNumber"));
			List<Integer> wStatusIDList = CloneTool.CloneArray(wParam.get("StatusIDList"), Integer.class);

			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.NCR_QueryTaskList(wBMSEmployee, null, -1, -1,
					wLevel, -1, -1, "", wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wSenderID,
					wStartTime, wEndTime, wStatusIDList);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRTask>();
			else {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(NCRTask::getID))),
								ArrayList::new));
				wServiceResult.Result = wServiceResult.Result.stream()
						.filter(p -> p.Status != NCRStatus.Default.getValue()).collect(Collectors.toList());
			}
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
	 * 历史记录车号分类
	 */
	@GetMapping("/TimeAllCar")
	public Object TimeAllCar(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<NCRCarInfo>> wServiceResult = wNCRService.NCR_QueryTimeAllCar(wLoginUser, wStartTime,
					wEndTime);

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
	 * 历史记录车号分类子项列表
	 */
	@GetMapping("/TimeAllCarSub")
	public Object TimeAllCarSub(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			String wCarType = StringUtils.parseString(request.getParameter("CarType"));
			String wCarNumber = StringUtils.parseString(request.getParameter("CarNumber"));

			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.NCR_QueryTimeAllCarSub(wLoginUser, wStartTime,
					wEndTime, wCarType, wCarNumber);

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
	 * 条件查询任务(整合版)
	 */
	@PostMapping("/TimeAllPro")
	public Object TimeAllPro(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(wParam.get("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(wParam.get("EndTime"));
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			int wCustomerID = StringUtils.parseInt(wParam.get("CustomerID"));
			int wLineID = StringUtils.parseInt(wParam.get("LineID"));
			int wStationID = StringUtils.parseInt(wParam.get("StationID"));
			int wSenderID = StringUtils.parseInt(wParam.get("SenderID"));
			int wLevel = StringUtils.parseInt(wParam.get("Level"));
			int wCarTypeID = StringUtils.parseInt(wParam.get("CarTypeID"));
			String wCarNumber = StringUtils.parseString(wParam.get("CarNumber"));
			List<Integer> wStatusIDList = CloneTool.CloneArray(wParam.get("StatusIDList"), Integer.class);

			ServiceResult<List<NCRTaskPro>> wServiceResult = wNCRService.NCR_QueryTaskProList(wBMSEmployee, wLevel,
					wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wSenderID, wStartTime, wEndTime,
					wStatusIDList);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<NCRTaskPro>();
			else {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(NCRTaskPro::getID))),
								ArrayList::new));
				wServiceResult.Result = wServiceResult.Result.stream()
						.filter(p -> p.Status != NCRStatus.Default.getValue()).collect(Collectors.toList());
			}
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
	 * 查单条
	 */
	@GetMapping("/Info")
	public Object Info(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 1, 1);
			List<Integer> wIDList = new ArrayList<Integer>();
			wIDList.add(wID);
			ServiceResult<List<NCRTask>> wServiceResult = wNCRService.NCR_QueryTaskList(wLoginUser, wIDList, -1, -1, -1,
					-1, -1, "", -1, "", -1, -1, -1, -1, -1, wCalendar, wCalendar, null);

			String wMsg = wServiceResult.FaultCode;
			NCRTask wTemp = new NCRTask();
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0)
				wTemp = wServiceResult.Result.get(0);
			else
				return GetResult(RetCode.SERVER_CODE_SUC, "未查到该条任务!");

			if (StringUtils.isEmpty(wMsg)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wMsg, null, wTemp);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wMsg, null, wTemp);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 通过订单ID与登录人查询车辆所在工位列表
	 */
	@GetMapping("/QueryStationByOrderID")
	public Object QueryStationByOrderID(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<Integer>> wOrderStationIDList = wNCRService.NCR_PartIDListByOrderID(wLoginUser,
					wOrderID);
			ServiceResult<List<Integer>> wUserStationIDList = wNCRService.NCR_PartIDListByUser(wLoginUser);
			ServiceResult<List<FPCPart>> wFPCPartList = wNCRService.NCR_ReturnPartIDList(wLoginUser,
					wOrderStationIDList.Result, wUserStationIDList.Result);

			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wFPCPartList.Result, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 时间段查询各等级下不合格评审发生频次
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
			ServiceResult<List<NCRFrequency>> wReultList = wNCRService.NCR_SelectFrequency(wLoginUser, wStartTime,
					wEndTime);
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wReultList.Result, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 导出不合格评审PDF
	 */
	@ResponseBody
	@GetMapping("/ExportPdf")
	public Object ExportPdf(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wTaskID = StringUtils.parseInt(request.getParameter("TaskID"));

			ServiceResult<String> wServiceResult = wNCRService.ExportPdf(wLoginUser, wTaskID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
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
	 * 导出不合格评审PDF(新版)
	 */
	@ResponseBody
	@GetMapping("/ExportPdfNew")
	public Object ExportPdfNew(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wTaskID = StringUtils.parseInt(request.getParameter("TaskID"));

			ServiceResult<String> wServiceResult = wNCRService.ExportPdfNew(wLoginUser, wTaskID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
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
	 * 导出例外放行单
	 */
	@ResponseBody
	@GetMapping("/ExportLetGo")
	public Object ExportLetGo(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wTaskID = StringUtils.parseInt(request.getParameter("TaskID"));

			ServiceResult<String> wServiceResult = wNCRService.ExportLetGo(wLoginUser, wTaskID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
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
	 * 根据工位ID获取工区主管
	 */
	@GetMapping("/ManagerList")
	public Object ManagerList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<List<BMSEmployee>> wServiceResult = wNCRService.NCR_QueryManagerList(wLoginUser, wPartID);

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
	 * 知会-不合格评审
	 */
	@PostMapping("/Inform")
	public Object Inform(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wNCRTaskID = StringUtils.parseInt(wParam.get("NCRTaskID"));
			List<Integer> wUserIDList = CloneTool.CloneArray(wParam.get("UserIDList"), Integer.class);

			ServiceResult<Integer> wServiceResult = wNCRService.NCR_Inform(wLoginUser, wNCRTaskID, wUserIDList);

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
	 * 更新不合格评审主单据
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

			// 获取参数
			NCRTask wData = CloneTool.Clone(wParam.get("data"), NCRTask.class);

			ServiceResult<NCRTask> wServiceResult = wNCRService.NCR_UpdateTask(wLoginUser, wData);

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
	 * 根据工位获取工区主管和工区调度
	 */
	@GetMapping("/LeaderList")
	public Object LeaderList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			if (wPartID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "提示：参数错误，工位不能小于或等于0!");
			}

			ServiceResult<List<BMSEmployee>> wServiceResult = wNCRService.NCR_LeaderList(wLoginUser, wPartID);

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
	 * 清除消息
	 */
	@GetMapping("/ClearMessage")
	public Object ClearMessage(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wMessageID = StringUtils.parseInt(request.getParameter("MessageID"));

			ServiceResult<Integer> wServiceResult = wNCRService.NCR_ClearMessage(wLoginUser, wMessageID);

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
}
