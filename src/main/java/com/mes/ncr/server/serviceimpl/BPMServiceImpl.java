package com.mes.ncr.server.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.BPMService;
import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMActivitiTask;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.RemoteInvokeUtils;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;
import com.mes.ncr.server.utils.RetCode;

/**
 * 
 * @author Femi
 * @CreateTime 2019年12月27日12:46:35
 * @LastEditTime 2019年12月27日16:23:56
 *
 */
@Service
public class BPMServiceImpl implements BPMService {

	private static Logger logger = LoggerFactory.getLogger(BPMServiceImpl.class);

	public BPMServiceImpl() {
	}

	private static BPMService Instance;

	public static BPMService getInstance() {
		if (Instance == null)
			Instance = new BPMServiceImpl();
		return Instance;
	}

	@Override
	public APIResult BPM_CreateProcess(BMSEmployee wLoginUser, BPMEventModule wModule, int wSheetID, Object wData) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processDefinitionKey", "_" + wModule.getValue());
			wParms.put("BusinessKey", wSheetID + "");

			if (wData == null) {
				wData = new HashMap<String, Object>();
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> wMap = CloneTool.Clone(wData, Map.class);
			for (String wKey : wMap.keySet()) {
				if (wMap.get(wKey) == null) {
					wMap.put(wKey, "");
				}
				if (wMap.get(wKey).getClass().isArray()) {
					wMap.put(wKey, StringUtils.Join(",", (Object[]) wMap.get(wKey)));
				}
				if (wMap.get(wKey) instanceof Collection<?>) {
					wMap.put(wKey, StringUtils.Join(",", (Collection<?>) wMap.get(wKey)));
				}
			}

			wParms.put("data", wMap);
			if (wMap.containsKey("Code")) {
				wParms.put("processInstanceCode", wMap.get("Code"));
				if (!wMap.containsKey("processInstanceCode"))
					wMap.put("processInstanceCode", wMap.get("Code"));
			}

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Runtime/startProcessByProcessDefinitionKey?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_CompleteTask(BMSEmployee wLoginUser, int wTaskID, int wLocalScope, Object wData) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("TaskID", wTaskID + "");
			wParms.put("localScope", wLocalScope);

			if (wData == null) {
				wData = new HashMap<String, Object>();
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> wMap = CloneTool.Clone(wData, Map.class);
			for (String wKey : wMap.keySet()) {
				if (wMap.get(wKey) == null) {
					wMap.put(wKey, "");
				}
				if (wMap.get(wKey).getClass().isArray()) {
					wMap.put(wKey, StringUtils.Join(",", (Object[]) wMap.get(wKey)));
				}
				if (wMap.get(wKey) instanceof Collection<?>) {
					wMap.put(wKey, StringUtils.Join(",", (Collection<?>) wMap.get(wKey)));
				}
			}

			wParms.put("data", wMap);
			if (wMap.containsKey("Code"))
				wParms.put("processInstanceCode", wMap.get("Code"));

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Runtime/CompleteMyPersonalTask?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetTask(BMSEmployee wLoginUser, int wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("taskId", wTaskID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/History/getBPMActivitiHisTaskByTaskId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_CurrentTask(BMSEmployee wLoginUser, int wProcessInstanceID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", wProcessInstanceID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Leave/CurrentTask?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetOperationByTaskID(BMSEmployee wLoginUser, int wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("taskId", wTaskID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Repository/getNextSFConditionByTaskId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetTaskListByInstance(BMSEmployee wLoginUser, int wInstanceID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", wInstanceID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Runtime/getUnfinishedTaskByPIId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Boolean> BPM_MsgUpdate(BMSEmployee wLoginUser, int wTaskID, int wLocalScope,
			BPMTaskBase wBPMTaskBase, Object wData) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>(false);
		try {
			APIResult wAPIResult = BPM_CompleteTask(wLoginUser, wTaskID, wLocalScope, wData);

			if (wAPIResult.getResultCode() != RetCode.SERVER_CODE_SUC) {
				wResult.FaultCode += wAPIResult.getMsg();
				return wResult;
			}

			BPMActivitiTask wSelfTask = (BPMActivitiTask) BPM_GetTask(wLoginUser, wTaskID).Info(BPMActivitiTask.class);
			if ((wSelfTask == null) || (StringUtils.isEmpty(wSelfTask.ID))
					|| (wTaskID != StringUtils.parseInt(wSelfTask.ID).intValue())) {
				wResult.Result = Boolean.valueOf(true);
				wResult.FaultCode += "任务不存在！";
				return wResult;
			}

			List<BPMActivitiTask> wBPMActivitiTaskList = wAPIResult.List(BPMActivitiTask.class);

//			if(wBPMActivitiTaskList==null||wBPMActivitiTaskList.size()<=0)
//			{
//				wResult.Result = Boolean.valueOf(true);
//				wResult.FaultCode += "任务已完成！";
//				return wResult;
//			}

			List<Integer> wNotifyUserIDList = new ArrayList<Integer>();
			if (wAPIResult.getReturnObject().containsKey("NotifyList")) {
				wNotifyUserIDList = StringUtils
						.parseIntList(wAPIResult.getReturnObject().get("NotifyList").toString().split(","));
			}

			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wBFCMessage = null;
			BFCMessage wBFCMessageClone = null;

			List<String> wStatusList = StringUtils.parseList(StringUtils.split(wBPMTaskBase.StatusText, ","));
			if (wStatusList.contains("待" + wSelfTask.Name))
				wStatusList.remove("待" + wSelfTask.Name);
			if (wBPMTaskBase.FollowerID.contains(Integer.valueOf(wLoginUser.getID()))) {
				wBPMTaskBase.FollowerID.remove(Integer.valueOf(wLoginUser.getID()));
			}
			List<Integer> wUserIDList = null;
			for (BPMActivitiTask bpmActivitiTask : wBPMActivitiTaskList) {
				if (bpmActivitiTask == null) {
					continue;
				}

				wBFCMessage = new BFCMessage();
				wBFCMessage.Active = 0;
				wBFCMessage.CompanyID = wLoginUser.getCompanyID();
				wBFCMessage.MessageID = wBPMTaskBase.getID();
				wBFCMessage.ModuleID = wBPMTaskBase.getFlowType();
				wBFCMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessage.ShiftID = 0;
				wBFCMessage.StepID = StringUtils.parseInt(bpmActivitiTask.ID).intValue();
				wBFCMessage.Title = StringUtils.Format("{0}", new Object[] { wBPMTaskBase.Code });
				wBFCMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1}  发起时刻：{2} 待{3}",
						new Object[] { BPMEventModule.getEnumType(wBPMTaskBase.getFlowType()).getLable(),
								wBPMTaskBase.UpFlowName,
								StringUtils.parseCalendarToString(wBPMTaskBase.CreateTime, "yyyy-MM-dd HH:mm"),
								bpmActivitiTask.Name })
						.trim();

				// 添加预移车标识
				if (wBPMTaskBase.getFlowType() == BPMEventModule.SCMovePart.getValue()) {
					MTCTask wTask = ((MTCTask) wBPMTaskBase);
					if (wTask.IsPreMove == 1) {
						wBFCMessage.MessageText = StringUtils.Format("模块：{0}[预移车] 发起人：{1}  发起时刻：{2} 待{3}",
								new Object[] { BPMEventModule.getEnumType(wBPMTaskBase.getFlowType()).getLable(),
										wBPMTaskBase.UpFlowName,
										StringUtils.parseCalendarToString(wBPMTaskBase.CreateTime, "yyyy-MM-dd HH:mm"),
										bpmActivitiTask.Name })
								.trim();
					}
				}

				wBFCMessage.CreateTime = Calendar.getInstance();

				if (StringUtils.isEmpty(bpmActivitiTask.Assignee))
					bpmActivitiTask.Assignee = "";
				if (StringUtils.isEmpty(bpmActivitiTask.Recipients))
					bpmActivitiTask.Recipients = "";
				wUserIDList = StringUtils.parseIntList(bpmActivitiTask.Assignee.split(","));
				List<Integer> wRecipientList = StringUtils.parseIntList(bpmActivitiTask.Recipients.split(","));
				wRecipientList.removeAll(wUserIDList);

				if (wUserIDList.size() <= 0) {
					wUserIDList = new ArrayList<Integer>(Arrays.asList(1));
				}

				for (Integer wResponsorID : wUserIDList) {
					if (wResponsorID.intValue() <= 0) {
						if (wResponsorID.intValue() != BaseDAO.SysAdmin.getID())
							continue;
						wBPMTaskBase.setStepID(StringUtils.parseInt(bpmActivitiTask.ID).intValue());
						WDWConstans.SetBizTask(wBPMTaskBase);
					} else {
						wBFCMessageClone = (BFCMessage) CloneTool.Clone(wBFCMessage, BFCMessage.class);
						wBFCMessageClone.ResponsorID = wResponsorID.intValue();
						wBFCMessageList.add(wBFCMessageClone);
//						WDWConstans.SetBizTask(wBPMTaskBase);
					}
				}
				for (Integer wResponsorID : wRecipientList) {
					if (wResponsorID.intValue() <= 0) {
						continue;
					}
					wBFCMessage.Type = BFCMessageType.Notify.getValue();
					wBFCMessageClone = (BFCMessage) CloneTool.Clone(wBFCMessage, BFCMessage.class);
					wBFCMessageClone.ResponsorID = wResponsorID.intValue();
					wBFCMessageList.add(wBFCMessageClone);
				}

				wNotifyUserIDList.removeAll(wUserIDList);
				wNotifyUserIDList.removeAll(wRecipientList);

				wBPMTaskBase.FollowerID.addAll(wUserIDList);
				if (wUserIDList.size() == 0 && wUserIDList.contains(BaseDAO.SysAdmin.getID()))
					continue;
				if (!wStatusList.contains("待" + bpmActivitiTask.Name)) {
					wStatusList.add("待" + bpmActivitiTask.Name);
				}
			}
			wBPMTaskBase.StatusText = StringUtils.Join(",", wStatusList);
			if (StringUtils.isEmpty(wBPMTaskBase.StatusText)) {
				wBPMTaskBase.StatusText = "已" + wSelfTask.Name;
			}
			if (wNotifyUserIDList.size() > 0) {
				for (Integer wResponsorID : wNotifyUserIDList) {
					wBFCMessage = new BFCMessage();
					wBFCMessage.Active = 0;
					wBFCMessage.CompanyID = wLoginUser.getCompanyID();
					wBFCMessage.MessageID = wBPMTaskBase.getID();
					wBFCMessage.ModuleID = wBPMTaskBase.getFlowType();
					wBFCMessage.Type = BFCMessageType.Notify.getValue();
					wBFCMessage.ShiftID = 0;
					wBFCMessage.StepID = StringUtils.parseInt(wSelfTask.ID).intValue();
					wBFCMessage.Title = StringUtils.Format("{0}", new Object[] { wBPMTaskBase.Code });
					wBFCMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1} 发起时刻：{2}  已{3}",
							new Object[] { BPMEventModule.getEnumType(wBPMTaskBase.getFlowType()).getLable(),
									wBPMTaskBase.UpFlowName,
									StringUtils.parseCalendarToString(wBPMTaskBase.CreateTime, "yyyy-MM-dd HH:mm"),
									wSelfTask.Name });
					// 添加预移车标识
					if (wBPMTaskBase.getFlowType() == BPMEventModule.SCMovePart.getValue()) {
						MTCTask wTask = ((MTCTask) wBPMTaskBase);
						if (wTask.IsPreMove == 1) {
							wBFCMessage.MessageText = StringUtils.Format("模块：{0}[预移车] 发起人：{1} 发起时刻：{2}  已{3}",
									new Object[] { BPMEventModule.getEnumType(wBPMTaskBase.getFlowType()).getLable(),
											wBPMTaskBase.UpFlowName, StringUtils.parseCalendarToString(
													wBPMTaskBase.CreateTime, "yyyy-MM-dd HH:mm"),
											wSelfTask.Name });
						}
					}

					wBFCMessage.ResponsorID = wResponsorID.intValue();
					wBFCMessage.CreateTime = Calendar.getInstance();
					wBFCMessageList.add(wBFCMessage);
				}

			}

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);

			// 判断任务是否结束

		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error("BPMService", "BPM_MsgUpdate", e);
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetInstanceByID(BMSEmployee wLoginUser, int wFlowID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", wFlowID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/History/getBPMActivitiProcessInstanceByPIId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_DeleteInstanceByID(BMSEmployee wLoginUser, int wFlowID, String wReason) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", wFlowID + "");
			wParms.put("deleteReason", wReason);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Runtime/deleteProcessInstance?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetActivitiHisTaskByPIId(BMSEmployee wLoginUser, int wFlowID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", wFlowID + "");
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/History/getBPMActivitiHisTaskByPIId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_GetHistoryInstanceByID(BMSEmployee wLoginUser, int processInstanceId) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("processInstanceId", processInstanceId);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/History/getBPMActivitiHisTaskByPIId?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BPM_RejectedSpecifiedActivity(BMSEmployee wLoginUser, String wTaskID, String targetActivityId,
			Object wData) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("TaskID", wTaskID);
			wParms.put("targetActivityId", targetActivityId);
			wParms.put("data", wData);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Runtime/rejectedSpecifiedActivity?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
