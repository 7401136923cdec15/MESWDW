package com.mes.ncr.server.controller.bpm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import com.mes.ncr.server.service.BPMService;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.LFSService;
import com.mes.ncr.server.service.MTCService;
import com.mes.ncr.server.service.NCRService;
import com.mes.ncr.server.service.RROService;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MTCStatus;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMActivitiProcessInstance;
import com.mes.ncr.server.service.po.bpm.BPMActivitiTask;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.mtc.MTCTaskType;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RRORepairStatus;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;
import com.mes.ncr.server.utils.RetCode;

@RestController
@RequestMapping("/api/Runtime")
public class BPMRuntimeController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(BPMRuntimeController.class);

	@Autowired
	RROService wRROService;
	@Autowired
	MTCService wMTCService;
	@Autowired
	LFSService wLFSService;
	@Autowired
	NCRService wNCRService;
	@Autowired
	BPMService wBPMService;
	@Autowired
	CoreService wCoreService;

	/**
	 * 创建流程任务
	 */
	@PostMapping("/startProcessByProcessDefinitionKey")
	public Object startProcessByProcessDefinitionKey(HttpServletRequest request,
			@RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("processDefinitionKey")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			String wModuleIDString = StringUtils.parseString(wParam.get("processDefinitionKey"));
			if (wModuleIDString.startsWith("_")) {
				wModuleIDString = wModuleIDString.substring(1);
			}
			int wModuleID = StringUtils.parseInt(wModuleIDString);

			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);

			String wMsg = "";

			BPMTaskBase wData = null;
			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = null;
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			switch (wEventID) {
			case SCMovePart:
				// 创建移车单(先查询默认状态单据)
				wServiceResult = wMTCService.MTC_QueryDefaultTask(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.getResult() == null
						|| ((BPMTaskBase) wServiceResult.getResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.getResult()).FlowID <= 0)
					wServiceResult = wMTCService.MTC_CreateTask(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode))
					wMsg += wServiceResult.getFaultCode();
				wData = (MTCTask) wServiceResult.getResult();
				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {

						wServiceResult = wMTCService.MTC_SubmitTask(wLoginUser, (MTCTask) wData);
						if (wServiceResult.ErrorCode != 0)
							wMsg += wServiceResult.getFaultCode();

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case QTRepair:
			case SCRepair:
			case TechRepair:
			case CKRepair:
				// 创建返修单
				wServiceResult = wRROService.RRO_QueryDefaultTask(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.getResult() == null
						|| ((BPMTaskBase) wServiceResult.getResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.getResult()).FlowID <= 0)
					wServiceResult = wRROService.RRO_CreateItemTask(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.getFaultCode()))
					wMsg += wServiceResult.getFaultCode();
				wData = (RROItemTask) wServiceResult.getResult();
				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wRROService.RRO_UpdateItemTask(wLoginUser, (RROItemTask) wData);
						if (wServiceResult.ErrorCode != 0)
							wMsg += wServiceResult.getFaultCode();

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case SCNCR: // 创建不合格评审申请单
				// 查询这个人发起的这个类型的单据Status==0
				// 如果没有 就创建
				wServiceResult = wNCRService.NCR_QuerySendTask(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.getResult() == null
						|| ((BPMTaskBase) wServiceResult.getResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.getResult()).FlowID <= 0)
					wServiceResult = wNCRService.NCR_CreateSendTask(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.getFaultCode()))
					wMsg += wServiceResult.getFaultCode();
				wData = (SendNCRTask) wServiceResult.getResult();
				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wNCRService.NCR_UpdateSendTask(wLoginUser, (SendNCRTask) wData);
						if (wServiceResult.ErrorCode != 0)
							wMsg += wServiceResult.getFaultCode();

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case TechNCR:
			case QTNCR:
				// 创建不合格评审单
				wServiceResult = wNCRService.NCR_QueryDefaultTask(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.getResult() == null
						|| ((BPMTaskBase) wServiceResult.getResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.getResult()).FlowID <= 0)
					wServiceResult = wNCRService.NCR_CreateTask(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.getFaultCode()))
					wMsg += wServiceResult.getFaultCode();
				wData = (NCRTask) wServiceResult.getResult();
				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
					NCRTask wNCRTask = (NCRTask) wData;
					if (wNCRTask.SendType == 0)// 不合格评审任务未给发起类型
					{
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
							.Info(Integer.class);
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {

						wServiceResult = wNCRService.NCR_UpdateTask(wLoginUser, (NCRTask) wData);
						if (wServiceResult.ErrorCode != 0)
							wMsg += wServiceResult.getFaultCode();

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			default:
				break;
			}
			if (wData == null) {
				wMsg += "该流程暂不支持";
			}
			if (StringUtils.isEmpty(wMsg)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wData.FlowID);
				wData.CreateTime = Calendar.getInstance();
				SetResult(wResult, "data", wData);
			} else {
				if (wMsg.equals("无授权")) {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "无授权", null, null);
				} else {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wMsg);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 提交待办任务
	 */
	@PostMapping("/CompleteMyPersonalTask")
	public Object CompleteMyPersonalTask(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("TaskID") || !wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			int wTaskID = CloneTool.Clone(wParam.get("TaskID"), Integer.class);
			BPMTaskBase wBPMTaskBase = CloneTool.Clone(wParam.get("data"), BPMTaskBase.class);
			int wLocalScope = wParam.containsKey("localScope") ? StringUtils.parseInt(wParam.get("localScope")) : 0;
			if (wTaskID <= 0 || wBPMTaskBase == null || wBPMTaskBase.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			int wModuleID = wBPMTaskBase.getFlowType();
			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);
			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = null;
			BPMActivitiProcessInstance wBPMActivitiProcessInstance = null;

			ServiceResult<Boolean> wServiceResultBool = new ServiceResult<Boolean>(false);
			switch (wEventID) {
			case SCMovePart:
				// 提交移车单
				MTCTask wMTCTask = CloneTool.Clone(wParam.get("data"), MTCTask.class);

				ServiceResult<String> wMsgResult = wMTCService.MTC_PreMoveCheck(wLoginUser, wMTCTask);
				if (StringUtils.isNotEmpty(wMsgResult.Result)) {
					return GetResult(RetCode.SERVER_CODE_ERR, wMsgResult.Result);
				}

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wMTCTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wMTCService.MTC_SubmitTask(wLoginUser, wMTCTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wMTCTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				if (wBPMActivitiProcessInstance != null && wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& wMTCTask.Status != MTCStatus.Completion.getValue()
						&& wMTCTask.Type == MTCTaskType.Translation.getValue()) {
					wMTCTask.Status = MTCStatus.Completion.getValue();
					wMTCTask.StatusText = MTCStatus.Completion.getLable();
					wServiceResult = wMTCService.MTC_SubmitTask(wLoginUser, wMTCTask);
				}

				break;
			case QTRepair:
			case SCRepair:
			case TechRepair:
			case CKRepair:
				// 提交返修项
				RROItemTask wRROItemTask = CloneTool.Clone(wParam.get("data"), RROItemTask.class);
				if (wRROItemTask.ID > 0 && wRROItemTask.TaskID <= 0)
					wRROService.RRO_SetItemTaskCode(wLoginUser, wRROItemTask);

				// 若发起不合格评审则先判断当前工位是否已设置工艺师
				if (wRROItemTask.IsSendNCR == 1) {
					FPCPart wFPCPart = WDWConstans.GetFPCPart(wRROItemTask.StationID);
					if (wFPCPart == null || wFPCPart.TechnicianList == null || wFPCPart.TechnicianList.size() <= 0) {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, wFPCPart.Name + "工艺师未设置！");
						return wResult;
					}
				}

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wRROItemTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}

				wServiceResult = wRROService.RRO_UpdateItemTask(wLoginUser, wRROItemTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wRROItemTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				if (wBPMActivitiProcessInstance != null && wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& wRROItemTask.Status != RRORepairStatus.Confirmed.getValue()) {
					wRROItemTask.Status = RRORepairStatus.Confirmed.getValue();
					wRROItemTask.StatusText = RRORepairStatus.Confirmed.getLable();
					wServiceResult = wRROService.RRO_UpdateItemTask(wLoginUser, wRROItemTask);
				}
				break;
			case SCNCR:// 提交不合格评审申请单

				SendNCRTask wSendNCRTask = CloneTool.Clone(wParam.get("data"), SendNCRTask.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wSendNCRTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}

				wServiceResult = wNCRService.NCR_UpdateSendTask(wLoginUser, wSendNCRTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wSendNCRTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);
				if (wBPMActivitiProcessInstance != null && wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& wSendNCRTask.Status != NCRStatus.ToCheckWrite.getValue()
						&& wSendNCRTask.Status != NCRStatus.Rejected.getValue()) {
					wSendNCRTask.Status = NCRStatus.Confirmed.getValue();
					wSendNCRTask.StatusText = NCRStatus.Confirmed.getLable();
					wServiceResult = wNCRService.NCR_UpdateSendTask(wLoginUser, wSendNCRTask);

					// 不合格申请例外放行流程结束后，自动知会质检班长和对应的工位检验员
					if (wSendNCRTask.Status == NCRStatus.Confirmed.getValue() && wSendNCRTask.IsRelease == 1) {
						wNCRService.SendMessageToChecker(wLoginUser, wSendNCRTask);
					}
				}

				break;
			case QTNCR:
			case TechNCR:// 不合格评审
				// 提交不合格评审单
				NCRTask wNCRTask = CloneTool.Clone(wParam.get("data"), NCRTask.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wNCRTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {

					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wNCRService.NCR_UpdateTask(wLoginUser, wNCRTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wNCRTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);
				if (wBPMActivitiProcessInstance != null && wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& wNCRTask.Status != NCRStatus.Confirmed.getValue()
						&& wNCRTask.Status != NCRStatus.Rejected.getValue()) {
					wNCRTask.Status = NCRStatus.Confirmed.getValue();
					wNCRTask.StatusText = NCRStatus.Confirmed.getLable();
					wServiceResult = wNCRService.NCR_UpdateTask(wLoginUser, wNCRTask);
				}

				break;
			default:
				break;
			}
			if (wServiceResult == null) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "流程未配置!");
				return wResult;
			}
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			if (wServiceResult.Result != null && ((BPMTaskBase) wServiceResult.Result).FlowID > 0) {
				wBPMActivitiTask = wBPMService
						.BPM_GetTaskListByInstance(wLoginUser, ((BPMTaskBase) wServiceResult.Result).FlowID)
						.List(BPMActivitiTask.class);
				if (wBPMActivitiTask != null) {
					wBPMActivitiTask.removeIf(
							p -> !StringUtils.parseIntList(p.Assignee.split(",")).contains(wLoginUser.getID()));
				}
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wServiceResult.Result);
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
	 * 撤销待办任务
	 */
	@GetMapping("/deleteProcessInstance")
	public Object DelectInstance(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wFlowID = StringUtils.parseInt(request.getParameter("processInstanceId"));
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			int wFlowType = StringUtils.parseInt(request.getParameter("FlowType"));
			String wReason = StringUtils.parseString(request.getParameter("deleteReason"));

			if (StringUtils.isEmpty(wReason))
				wReason = "撤销";

			if (wFlowID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			APIResult wAPIResult = wBPMService.BPM_DeleteInstanceByID(wLoginUser, wFlowID, wReason);
			if (wAPIResult.getResultCode() != RetCode.SERVER_CODE_SUC)
				return wAPIResult;

			if (wFlowType > 0 && wID > 0) {
				BPMEventModule wEventID = BPMEventModule.getEnumType(wFlowType);
				switch (wEventID) {
				case SCMovePart:
					// 提交移车单
					ServiceResult<MTCTask> wMTCTaskResult = wMTCService.MTC_GetTask(wLoginUser, wID, "");
					if (wMTCTaskResult.Result != null && wMTCTaskResult.Result.ID > 0) {
						wMTCTaskResult.Result.Status = MTCStatus.TaskCancel.getValue();
						wMTCTaskResult.Result.StatusText = "已撤销";
						wMTCTaskResult.Result.FollowerID = null;

						wMTCService.MTC_SubmitTask(wLoginUser, wMTCTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}

					break;
				case QTRepair:
				case SCRepair:
				case TechRepair:
				case CKRepair:
					// 提交返修项
					ServiceResult<RROItemTask> wRROItemTaskResult = wRROService.RRO_GetTaskInfo(wLoginUser, wID, "");
					if (wRROItemTaskResult.Result != null && wRROItemTaskResult.Result.ID > 0) {
						wRROItemTaskResult.Result.Status = MTCStatus.TaskCancel.getValue();
						wRROItemTaskResult.Result.StatusText = "已撤销";
						wRROItemTaskResult.Result.FollowerID = null;
						wRROService.RRO_UpdateItemTask(wLoginUser, wRROItemTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
					break;
				case SCNCR:
					// 提交不合格评审单
					ServiceResult<SendNCRTask> wSendNCRTaskResult = wNCRService.NCR_QuerySendTaskByID(wLoginUser, wID);
					if (wSendNCRTaskResult.Result != null && wSendNCRTaskResult.Result.ID > 0) {
						wSendNCRTaskResult.Result.Status = MTCStatus.TaskCancel.getValue();
						wSendNCRTaskResult.Result.StatusText = "已撤销";
						wSendNCRTaskResult.Result.FollowerID = null;
						wNCRService.NCR_UpdateSendTask(wLoginUser, wSendNCRTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
					break;
				case QTNCR:
				case TechNCR:// 不合格评审
					// 提交不合格评审单
					ServiceResult<NCRTask> wNCRTaskResult = wNCRService.NCR_GetTaskInfo(wLoginUser, wID, "");
					if (wNCRTaskResult.Result != null && wNCRTaskResult.Result.ID > 0) {
						wNCRTaskResult.Result.Status = MTCStatus.TaskCancel.getValue();
						wNCRTaskResult.Result.StatusText = "已撤销";
						wNCRTaskResult.Result.FollowerID = null;
						wNCRService.NCR_UpdateTask(wLoginUser, wNCRTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
					break;
				default:
					break;
				}
			}
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "撤销成功！", null, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 创建评论消息
	 */
	@PostMapping("/CreateCommentMessage")
	public Object CreateCommentMessage(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<Integer> wPersonIDList = CloneTool.CloneArray(wParam.get("PersonIDList"), Integer.class);
			String wMsgTigle = StringUtils.parseString(wParam.get("MsgTigle"));
			String wMsgContent = StringUtils.parseString(wParam.get("MsgContent"));
			int wFlowType = StringUtils.parseInt(wParam.get("FlowType"));
			int wTaskID = StringUtils.parseInt(wParam.get("TaskID"));

			ServiceResult<Integer> wServiceResult = wMTCService.MTC_CreateCommentMessage(wLoginUser, wPersonIDList,
					wMsgTigle, wMsgContent, wFlowType, wTaskID);

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
	 * 驳回指定节点
	 */
	@PostMapping("/RejectTo")
	public Object RejectTo(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wTaskID = StringUtils.parseString(wParam.get("TaskID"));
			String targetActivityId = StringUtils.parseString(wParam.get("targetActivityId"));
			Object wData = wParam.get("data");
			String targetActivityName = StringUtils.parseString(wParam.get("targetActivityName"));
			int wFlowType = StringUtils.parseInt(wParam.get("FlowType"));

			ServiceResult<Object> wServiceResult = wMTCService.BPM_RejectTo(wLoginUser, wTaskID, targetActivityId,
					wData, targetActivityName, wFlowType);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", new ArrayList<Integer>(), wData);
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
