package com.mes.ncr.server.controller.rro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.RROService;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.rro.RRORepairTable;
import com.mes.ncr.server.service.po.rro.RROTableBody;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.RetCode;

@RestController
@RequestMapping("/api/RROTable")
public class RROTableController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(RROTableController.class);

	@Autowired
	RROService wRROService;
	@Autowired
	CoreService wCoreService;

	/**
	 * 车号与任务类型查询表内容
	 */
	@GetMapping("/QuerByType")
	public Object QuerByType(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wCarTypeID = StringUtils.parseInt(request.getParameter("CarTypeID"));
			String wCarNumber = StringUtils.parseString(request.getParameter("CarNumber"));
			int wType = StringUtils.parseInt(request.getParameter("Type"));

			ServiceResult<List<RROTableBody>> wServiceResult = wRROService.RRO_QueryBodyByType(wLoginUser, wCarTypeID,
					wCarNumber, wType);
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
	 * 发起人提交返修表
	 */
	@PostMapping("/SubmitTable")
	public Object QueryPartNo(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			RRORepairTable wRRORepairTable = CloneTool.Clone(wParam.get("data"), RRORepairTable.class);
			ServiceResult<RRORepairTable> wServiceResult = wRROService.RRO_SaveRRORepairTable(wLoginUser,
					wRRORepairTable);
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
	 * 条件查询任务
	 */
	@PostMapping("/QueryAll")
	public Object QueryAll(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wCarTypeID = CloneTool.Clone(wParam.get("CarTypeID"), Integer.class);
			String wCarNumber = CloneTool.Clone(wParam.get("CarNumber"), String.class);
			int wType = CloneTool.Clone(wParam.get("Type"), Integer.class);
			int wLineID = CloneTool.Clone(wParam.get("LineID"), Integer.class);
			int wCustomerID = CloneTool.Clone(wParam.get("CustomerID"), Integer.class);
			int wSenderID = CloneTool.Clone(wParam.get("SenderID"), Integer.class);
			int wApprovalID = CloneTool.Clone(wParam.get("ApprovalID"), Integer.class);
			int wStatus = CloneTool.Clone(wParam.get("Status"), Integer.class);
			Calendar wStartTime = CloneTool.Clone(wParam.get("StartTime"), Calendar.class);
			Calendar wEndTime = CloneTool.Clone(wParam.get("EndTime"), Calendar.class);
			ServiceResult<List<RRORepairTable>> wServiceResult = wRROService.RRO_QueryTableList(wLoginUser, null,
					wCarTypeID, wCarNumber, wLineID, wType, wCustomerID, wSenderID, wApprovalID, wStatus, wStartTime,
					wEndTime);
			if (wServiceResult.Result == null && wServiceResult.Result.size() <= 0)
				wServiceResult.Result = new ArrayList<RRORepairTable>();
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
}
