package com.mes.ncr.server.controller.mtc;

import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RestController;
import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.FMCService;
import com.mes.ncr.server.service.LFSService;
import com.mes.ncr.server.service.MTCService;
import com.mes.ncr.server.service.mesenum.MTCStatus;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.lfs.LFSStoreHouse;
import com.mes.ncr.server.service.po.mtc.MTCRealTime;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.RetCode;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2021-6-10 11:37:27
 */
@RestController
@RequestMapping("/api/Capacity")
public class CapacityController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(CapacityController.class);

	@Autowired
	MTCService wMTCService;
	@Autowired
	LFSService wLFSService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	FMCService wFMCService;

	/**
	 * 查询库位可用容量
	 * 
	 * @param QueryStoreCapacity
	 * @return
	 */
	@GetMapping("/QueryStoreCapacity")
	public Object QueryStoreCapacity(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			// 返回前库位名称台位名称赋值
			List<LFSStoreHouse> wLFSStoreHouseList = wLFSService.LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// 获取所有台位，台位上车辆
			// 获取台位列表
			List<FMCWorkspace> wFMCWorkspaceList = wFMCService.FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);
			// 查询库位下所有车辆并计算出库位的可用长度
			if (wLFSStoreHouseList != null && wLFSStoreHouseList.size() > 0) {
				for (LFSStoreHouse wItem : wLFSStoreHouseList) {
					// 获取当前库位下所有台位
					List<FMCWorkspace> wAllWorkspace = wFMCWorkspaceList.stream().filter(p -> p.ParentID == wItem.ID)
							.collect(Collectors.toList());
					if (wAllWorkspace != null && wAllWorkspace.size() > 0) {
						// 查询该库位所有台位上车辆计算可用长度
						int wAllLength = 0;
						for (FMCWorkspace wFMCWorkspace : wAllWorkspace) {
							if (wFMCWorkspace.PartNo != null && !wFMCWorkspace.PartNo.isEmpty()) {
								FPCProduct wFPCProduct = wFMCService
										.FPC_QueryProductByID(wLoginUser, 0, wFMCWorkspace.PartNo.split("\\#")[0])
										.Info(FPCProduct.class);
								if (wFPCProduct != null && wFPCProduct.ID > 0)
									wAllLength += wFPCProduct.Length;
							}
						}
						wItem.UsableLength = wItem.Length - wAllLength;
					} else// 该库位无台位 ，可用长度为库位长度
						wItem.UsableLength = wItem.Length;
				}
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wLFSStoreHouseList, null);
			} else
				return GetResult(RetCode.SERVER_CODE_SUC, "未查询到库位列表!");

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 查询库位下所有车辆列表
	 */
	@PostMapping("/PostStoreAllTrain")
	public Object PostStoreAllTrain(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			LFSStoreHouse wLFSStoreHouse = CloneTool.Clone(wParam.get("data"), LFSStoreHouse.class);
			if (wLFSStoreHouse == null) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			ServiceResult<List<MTCRealTime>> wServiceResult = wMTCService.MTC_SelectRealListByStore(wLoginUser,
					wLFSStoreHouse);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0) {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "该库位暂无机车");
				} else {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				}
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
	 * 条件查询移车单
	 */
	@GetMapping("/All")
	public Object All(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wCarTypeID = StringUtils.parseInt(request.getParameter("CarTypeID"));
			String wCarNumber = StringUtils.parseString(request.getParameter("CarNumber"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wUpFlowID = StringUtils.parseInt(request.getParameter("UpFlowID"));

			if (wCarTypeID == 0)
				wCarTypeID = -1;
			ServiceResult<List<MTCTask>> wResultList = wMTCService.MTC_GetTaskList(wLoginUser, -1, -1, -1, -1, -1,
					wOrderID, -1, wCarTypeID, wCarNumber, wStartTime, wEndTime, wUpFlowID);

			if (wResultList.Result != null && wResultList.Result.size() > 0) {
				wResultList.Result = wResultList.Result.stream().filter(p -> p.Status != MTCStatus.Default.getValue())
						.collect(Collectors.toList());
			}
			if (StringUtils.isEmpty(wResultList.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wResultList.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wResultList.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

}
