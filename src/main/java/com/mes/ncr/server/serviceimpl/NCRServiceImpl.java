package com.mes.ncr.server.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.NCRService;
import com.mes.ncr.server.service.mesenum.APSShiftPeriod;
import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.FMCShiftLevel;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.TaskQueryType;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSDepartment;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bms.BMSRoleItem;
import com.mes.ncr.server.service.po.bpm.BPMActivitiHisTask;
import com.mes.ncr.server.service.po.bpm.BPMActivitiHisTaskVarinst;
import com.mes.ncr.server.service.po.bpm.BPMOperationStep;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.excel.MyExcelSheet;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.fpc.FPCRoutePart;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.ncr.server.service.po.ncr.NCRCarInfo;
import com.mes.ncr.server.service.po.ncr.NCRFrequency;
import com.mes.ncr.server.service.po.ncr.NCRHandleResult;
import com.mes.ncr.server.service.po.ncr.NCRLevel;
import com.mes.ncr.server.service.po.ncr.NCRPartTaskShow;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.NCRTaskPro;
import com.mes.ncr.server.service.po.ncr.NCRTaskShow;
import com.mes.ncr.server.service.po.ncr.NCRType;
import com.mes.ncr.server.service.po.ncr.SendNCRPartTaskShow;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.po.ncr.SendNCRTaskShow;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.ncr.NCRTaskDAO;
import com.mes.ncr.server.serviceimpl.dao.ncr.SendNCRTaskDAO;
import com.mes.ncr.server.serviceimpl.utils.MESServer;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;
import com.mes.ncr.server.serviceimpl.utils.wdw.ExcelUtil;
import com.mes.ncr.server.serviceimpl.utils.wdw.PdfUtils;
import com.mes.ncr.server.utils.Constants;

@Service
public class NCRServiceImpl implements NCRService {

	private static Logger logger = LoggerFactory.getLogger(NCRServiceImpl.class);

	public NCRServiceImpl() {
	}

	private static NCRService Instance;

	public static NCRService getInstance() {
		if (Instance == null)
			Instance = new NCRServiceImpl();
		return Instance;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_QueryTaskList(BMSEmployee wLoginUser, List<Integer> wIDList, int wFlowType,
			int wFlowID, int wLevel, int wSendType, int wTaskStepID, String wCode, int wCarTypeID, String wCarNumber,
			int wOrderID, int wCustomerID, int wLineID, int wStationID, int wSenderID, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStateIDList) {
		ServiceResult<List<NCRTask>> wResult = new ServiceResult<List<NCRTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = NCRTaskDAO.getInstance().SelectList(wLoginUser, wIDList, wFlowType, wFlowID, wLevel,
					wSendType, wTaskStepID, wCode, wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID,
					wSenderID, wStartTime, wEndTime, wStateIDList, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_QueryTaskList(BMSEmployee wLoginUser, int wFlowType, int wOrderID,
			int wLineID, int wCustomerID, int wProductID, int wLevel, String wCarNumber, int wPartID,
			Calendar wStartTime, Calendar wEndTime, int wStatus, String wIsRelease) {
		ServiceResult<List<NCRTask>> wResult = new ServiceResult<List<NCRTask>>();
		wResult.Result = new ArrayList<NCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (wStatus) {
			case 1:

				switch (BPMEventModule.getEnumType(wFlowType)) {
				case SCNCR:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							wFlowType, wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, wStartTime,
							wEndTime, StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue()), null, wErrorCode)));
					break;
				case QTNCR:
				case TechNCR:

					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue()), null, wErrorCode));
					break;
				case Default:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							wFlowType, wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, wStartTime,
							wEndTime, StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue()), null, wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue()), null, wErrorCode));
					break;
				default:
					break;
				}

				break;
			case 0:
				switch (BPMEventModule.getEnumType(wFlowType)) {
				case SCNCR:
					wResult.Result.addAll(
							NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType,
									wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, null, null, null,
									StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue(),
											NCRStatus.Default.getValue(), NCRStatus.Rejected.getValue(),
											NCRStatus.Cancle.getValue()),
									wErrorCode)));
					break;
				case QTNCR:
				case TechNCR:
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, null, null, null,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue(), NCRStatus.Default.getValue(),
									NCRStatus.Rejected.getValue(), NCRStatus.Cancle.getValue()),
							wErrorCode));
					break;
				case Default:
					wResult.Result.addAll(
							NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType,
									wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, null, null, null,
									StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue(),
											NCRStatus.Default.getValue(), NCRStatus.Rejected.getValue(),
											NCRStatus.Cancle.getValue()),
									wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, null, null, null,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue(), NCRStatus.Default.getValue(),
									NCRStatus.Rejected.getValue(), NCRStatus.Cancle.getValue()),
							wErrorCode));
					break;
				default:
					break;
				}

				break;
			default:
				switch (BPMEventModule.getEnumType(wFlowType)) {
				case SCNCR:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							wFlowType, wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, wStartTime,
							wEndTime, null, null, wErrorCode)));
					break;
				case QTNCR:
				case TechNCR:

					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null, null,
							wErrorCode));
					break;
				case Default:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							wFlowType, wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, wStartTime,
							wEndTime, null, null, wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null, null,
							wErrorCode));
					break;
				default:
					break;
				}
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			// 例外放行筛选
			if (StringUtils.isNotEmpty(wIsRelease)) {
				String[] wStrs = wIsRelease.split(",");
				List<Integer> wIDList = new ArrayList<Integer>();
				for (String wStr : wStrs) {
					int wIntValue = StringUtils.parseInt(wStr);
					if (wIntValue != -1) {
						wIDList.add(wIntValue);
					}
				}
				if (wIDList.size() > 0) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> wIDList.stream().anyMatch(q -> q.intValue() == p.IsRelease))
							.collect(Collectors.toList());
				}
			}

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = new ArrayList<BPMTaskBase>();
			switch (BPMEventModule.getEnumType(wFlowType)) {
			case SCNCR:
				wBaseList.addAll(
						SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				break;
			case QTNCR:
				wBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				wBaseList.removeIf(p -> p.FlowType != wFlowType);
				break;
			case TechNCR:

				wBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				wBaseList.removeIf(p -> p.FlowType != wFlowType);
				break;
			case Default:
				wBaseList.addAll(
						SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				wBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				break;
			default:
				break;
			}

			// 未完成的去除已发起评审的数据
			if (wStatus == 0) {
				wResult.Result.removeIf(p -> p.FlowType == 1005 && (p.Status == 13 || p.Status == 12));
			}

			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {

				if (wTaskBase instanceof NCRTask) {
					NCRTask wNCRTask = (NCRTask) wTaskBase;
					wNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wNCRTask.ID)
							wResult.Result.set(i, wNCRTask);
					}
				}
				if (wTaskBase instanceof SendNCRTask) {
					NCRTask wNCRTask = new NCRTask((SendNCRTask) wTaskBase);
					wNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wNCRTask.ID)
							wResult.Result.set(i, wNCRTask);
					}
				}
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_QueryTaskListBySendType(BMSEmployee wLoginUser, int wSendType, int wOrderID,
			int wLineID, int wCustomerID, int wProductID, int wLevel, String wCarNumber, int wPartID,
			Calendar wStartTime, Calendar wEndTime, int wStatus, String wIsRelease) {
		ServiceResult<List<NCRTask>> wResult = new ServiceResult<List<NCRTask>>();
		wResult.Result = new ArrayList<NCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (wStatus) {
			case 1:

				switch (wSendType) {
				case 1:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							BPMEventModule.SCNCR.getValue(), wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
							wPartID, wStartTime, wEndTime, StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue()),
							null, wErrorCode)));
					break;
				case 2:

					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue()), null, wErrorCode));
					break;
				default:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							BPMEventModule.SCNCR.getValue(), wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
							wPartID, wStartTime, wEndTime, StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue()),
							null, wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue()), null, wErrorCode));
					break;
				}

				break;
			case 0:
				switch (wSendType) {
				case 1:
					wResult.Result.addAll(NCRTask.SendListToTaskList(
							SendNCRTaskDAO.getInstance().SelectList(wLoginUser, BPMEventModule.SCNCR.getValue(),
									wProductID, wCarNumber, wOrderID, wCustomerID, wLineID, wPartID, wStartTime,
									wEndTime, null, StringUtils.parseListArgs(0, 21, 22, 23, 12, 13), wErrorCode)));
					break;
				case 2:
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue(), NCRStatus.Default.getValue(),
									NCRStatus.Rejected.getValue(), NCRStatus.Cancle.getValue()),
							wErrorCode));
					break;
				default:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							BPMEventModule.SCNCR.getValue(), wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
							wPartID, wStartTime, wEndTime, null,
							StringUtils.parseListArgs(NCRStatus.ToCheckWrite.getValue(), NCRStatus.Default.getValue(),
									NCRStatus.Rejected.getValue(), NCRStatus.Cancle.getValue()),
							wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null,
							StringUtils.parseListArgs(NCRStatus.Confirmed.getValue(), NCRStatus.Default.getValue(),
									NCRStatus.Rejected.getValue(), NCRStatus.Cancle.getValue()),
							wErrorCode));
					break;
				}

				break;
			default:
				switch (wSendType) {
				case 1:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							BPMEventModule.SCNCR.getValue(), wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
							wPartID, wStartTime, wEndTime, null, null, wErrorCode)));
					break;
				case 2:

					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null, null,
							wErrorCode));
					break;
				default:
					wResult.Result.addAll(NCRTask.SendListToTaskList(SendNCRTaskDAO.getInstance().SelectList(wLoginUser,
							BPMEventModule.SCNCR.getValue(), wProductID, wCarNumber, wOrderID, wCustomerID, wLineID,
							wPartID, wStartTime, wEndTime, null, null, wErrorCode)));
					wResult.Result.addAll(NCRTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
							wCustomerID, wProductID, wLevel, wCarNumber, wPartID, wStartTime, wEndTime, null, null,
							wErrorCode));
					break;
				}
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			// 例外放行筛选
			if (StringUtils.isNotEmpty(wIsRelease)) {
				String[] wStrs = wIsRelease.split(",");
				List<Integer> wIDList = new ArrayList<Integer>();
				for (String wStr : wStrs) {
					int wIntValue = StringUtils.parseInt(wStr);
					if (wIntValue != -1) {
						wIDList.add(wIntValue);
					}
				}
				if (wIDList.size() > 0) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> wIDList.stream().anyMatch(q -> q.intValue() == p.IsRelease))
							.collect(Collectors.toList());
				}
			}

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = new ArrayList<BPMTaskBase>();
			switch (wSendType) {
			case 1:
				wBaseList.addAll(
						SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				break;
			case 2:
				wBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));

				break;
			default:
				wBaseList.addAll(
						SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				wBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode));
				break;
			}

			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {

				if (wTaskBase instanceof NCRTask) {
					NCRTask wNCRTask = (NCRTask) wTaskBase;
					wNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wNCRTask.ID)
							wResult.Result.set(i, wNCRTask);
					}
				}
				if (wTaskBase instanceof SendNCRTask) {
					NCRTask wNCRTask = new NCRTask((SendNCRTask) wTaskBase);
					wNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wNCRTask.ID)
							wResult.Result.set(i, wNCRTask);
					}
				}

			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SendNCRTask> NCR_QuerySendTask(BMSEmployee wLoginUser, int wModeID) {
		ServiceResult<SendNCRTask> wResult = new ServiceResult<SendNCRTask>();
		try {
			wResult.Result = new SendNCRTask();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<SendNCRTask> wSendNCRTaskList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, null, wModeID, -1,
					"", -1, "", -1, -1, -1, -1, wLoginUser.getID(), null, null,
					StringUtils.parseList(new Integer[] { NCRStatus.Default.getValue() }), wErrorCode);
			if (wSendNCRTaskList != null && wSendNCRTaskList.size() > 0) {
				wResult.Result = wSendNCRTaskList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.DepartmentID = wLoginUser.DepartmentID;
				wResult.Result.Department = WDWConstans.GetBMSDepartmentName(wLoginUser.DepartmentID);
			}
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<NCRTask> NCR_QueryDefaultTask(BMSEmployee wLoginUser, int wModeID) {
		ServiceResult<NCRTask> wResult = new ServiceResult<NCRTask>();
		try {
			wResult.Result = new NCRTask();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<NCRTask> wNCRTaskList = NCRTaskDAO.getInstance().SelectList(wLoginUser, null, wModeID, -1, -1, -1, -1,
					"", -1, "", -1, -1, -1, -1, wLoginUser.getID(), null, null,
					StringUtils.parseList(new Integer[] { NCRStatus.Default.getValue() }), wErrorCode);
			if (wNCRTaskList != null && wNCRTaskList.size() > 0) {
				wResult.Result = wNCRTaskList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				// 查询所有工区检验员并返回（前提是发起人为工区检验员）
				List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
				if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
					for (LFSWorkAreaChecker wItem : wLFSWorkAreaCheckerList) {
						if (wItem.CheckerIDList != null && wItem.CheckerIDList.contains(wLoginUser.ID)) {
							wResult.Result.FollowerID.addAll(wItem.CheckerIDList);
						}
					}
				}
				// 获取当前登录人工区ID并将该工区工区检验员进行赋值
				ServiceResult<List<UserWorkArea>> wServiceResult = NCRServiceImpl.getInstance()
						.NCR_GetDepartment(wLoginUser);
				if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
					List<LFSWorkAreaChecker> wAreaList = LFSServiceImpl.getInstance()
							.LFS_QueryWorkAreaCheckerList(wLoginUser, wServiceResult.Result.get(0).getWorkID())
							.List(LFSWorkAreaChecker.class);
					if (wAreaList != null && wAreaList.size() > 0) {
						wResult.Result.FollowerID.addAll(wAreaList.get(0).CheckerIDList);
					}
				}
				if (wResult.Result.FollowerID != null && wResult.Result.FollowerID.size() > 0) {
					wResult.Result.FollowerID = wResult.Result.FollowerID.stream().distinct()
							.collect(Collectors.toList());
				}
			}
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> NCR_QuerySendTaskList(BMSEmployee wLoginUser, List<Integer> wIDList,
			int wFlowType, int wFlowID, int wLevel, int wSendType, int wTaskStepID, int wTaskID, int wRelaID,
			String wCode, int wCarTypeID, String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID,
			int wUpFlowID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList) {
		ServiceResult<List<SendNCRTask>> wResult = new ServiceResult<List<SendNCRTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, wIDList, wFlowType, wFlowID, wCode,
					wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wUpFlowID, wStartTime, wEndTime,
					wStatusIDList, wErrorCode);

			// 查询待后续操作的工位检验员，方便流程跟踪
//			for (SendNCRTask wSendNCRTask : wResult.Result) {
//				if (wSendNCRTask.Status == 3) {
//					List<Integer> wCheckerIDList = SendNCRTaskDAO.getInstance().GetCheckerIDList(wLoginUser,
//							wSendNCRTask.DepartmentID, wSendNCRTask.StationID, wErrorCode);
//					List<String> wNames = new ArrayList<String>();
//					for (Integer wUserID : wCheckerIDList) {
//						BMSEmployee wUser = WDWConstans.GetBMSEmployee(wUserID);
//						wNames.add(StringUtils.Format("{0}({1})", wUser.Name,
//								wUser.LoginID.substring(wUser.LoginID.length() - 6)));
//					}
//					wSendNCRTask.Checkers = StringUtils.Join(",", wNames);
//				}
//
//				if (wSendNCRTask.NCRID > 0) {
//					int wUpflowID = NCRTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wSendNCRTask.NCRID, "",
//							wErrorCode).UpFlowID;
//					BMSEmployee wUser = WDWConstans.GetBMSEmployee(wUpflowID);
//					if (wUser.ID > 0) {
//						wSendNCRTask.Checkers = StringUtils.Format("{0}({1})", wUser.Name,
//								wUser.LoginID.substring(wUser.LoginID.length() - 6));
//					}
//				}
//			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SendNCRTask> NCR_QuerySendTaskByID(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SendNCRTask> wResult = new ServiceResult<SendNCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SendNCRTaskDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<NCRTask> NCR_UpdateTask(BMSEmployee wLoginUser, NCRTask wNCRTask) {
		ServiceResult<NCRTask> wResult = new ServiceResult<NCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// 判断是否为申请单发起的不合格评审
			NCRTaskDAO.getInstance().IsSendNCR(wLoginUser, wNCRTask);

			if (wNCRTask.Status == 22) {
				wNCRTask.StatusText = "已驳回";
			} else if (wNCRTask.Status == 12) {
				wNCRTask.StatusText = "已确认";
			}

			NCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wNCRTask, wErrorCode);
			wResult.setResult(wNCRTask);
			wResult.Result.ID = wNCRTask.ID;
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SendNCRTask> NCR_UpdateSendTask(BMSEmployee wLoginUser, SendNCRTask wSendNCRTask) {
		ServiceResult<SendNCRTask> wResult = new ServiceResult<SendNCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 更新创建时间
			if (wSendNCRTask.Status == 1) {
				wSendNCRTask.CreateTime = Calendar.getInstance();
			}

			if (wSendNCRTask.ID > 0 && wSendNCRTask.Status == NCRStatus.ToCheckWrite.getValue()
					&& wSendNCRTask.IsRelease != 1) {
				List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = new ArrayList<LFSWorkAreaChecker>();
				// ①找工位检验员
				List<Integer> wCheckerList = SendNCRTaskDAO.getInstance().GetCheckerIDList(wLoginUser,
						wSendNCRTask.DepartmentID, wSendNCRTask.StationID, wErrorCode);

				if (wCheckerList.size() > 0) {
					LFSWorkAreaChecker wC = new LFSWorkAreaChecker();
					wC.CheckerIDList = wCheckerList;
					wLFSWorkAreaCheckerList.add(wC);
				}

				if (StringUtils.isNotEmpty(wSendNCRTask.CheckerID)) {
					List<Integer> wUserIDList = StringUtils.parseIntList(wSendNCRTask.CheckerID.split(","));
					wLFSWorkAreaCheckerList.forEach(p -> p.CheckerIDList = wUserIDList);
				}

				// ①添加入库检查员
				if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
					List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
					for (LFSWorkAreaChecker wLFSWorkAreaChecker : wLFSWorkAreaCheckerList) {
						if (wLFSWorkAreaChecker.CheckerIDList != null && wLFSWorkAreaChecker.CheckerIDList.size() > 0) {
							for (Integer wInteger : wLFSWorkAreaChecker.CheckerIDList) {
								BFCMessage wBFCMessage = new BFCMessage();
								wBFCMessage.Active = 1;
								wBFCMessage.CompanyID = wLoginUser.getCompanyID();
								wBFCMessage.MessageID = wSendNCRTask.getID();
								wBFCMessage.ModuleID = BPMEventModule.SCNCR.getValue();
								wBFCMessage.Type = BFCMessageType.Task.getValue();
								wBFCMessage.ShiftID = 0;
								wBFCMessage.StepID = 0;
								wBFCMessage.Title = StringUtils.Format("{0}待发起评审", new Object[] { wSendNCRTask.Code });
								wBFCMessage.MessageText = StringUtils.Format("模块：{0} 处理人：{1} 处理时刻：{2}  已审批通过",
										new Object[] {
												BPMEventModule.getEnumType(wSendNCRTask.getFlowType()).getLable(),
												wLoginUser.Name, StringUtils.parseCalendarToString(
														wSendNCRTask.CreateTime, "yyyy-MM-dd HH:mm") });
								wBFCMessage.ResponsorID = wInteger.intValue();
								wBFCMessage.CreateTime = Calendar.getInstance();
								wBFCMessageList.add(wBFCMessage);
							}
						}
					}
					CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
				}
				wSendNCRTask.StatusText = NCRStatus.ToCheckWrite.getLable();
			}

			if (wSendNCRTask.Status == NCRStatus.Rejected.getValue()) {
				wSendNCRTask.StatusText = NCRStatus.Rejected.getLable();
			}

			// ①判断是否为自检发起，触发自检不合格申请通过或驳回事件
			if (wSendNCRTask.Status == NCRStatus.Rejected.getValue() && wSendNCRTask.IPTItemID > 0
					&& wSendNCRTask.TaskIPTID > 0 && wSendNCRTask.IsRelease != 1) {
				QMSServiceImpl.getInstance().SFC_TaskIPTHandCheckItem(wLoginUser, wSendNCRTask.TaskIPTID,
						wSendNCRTask.IPTItemID, 0);
			}

			if (wSendNCRTask.Status == NCRStatus.ToCheckWrite.getValue() && wSendNCRTask.IPTItemID > 0
					&& wSendNCRTask.TaskIPTID > 0 && wSendNCRTask.IsRelease != 1) {
				QMSServiceImpl.getInstance().SFC_TaskIPTHandCheckItem(wLoginUser, wSendNCRTask.TaskIPTID,
						wSendNCRTask.IPTItemID, 1);
			}

			SendNCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wSendNCRTask, wErrorCode);
			wResult.setResult(wSendNCRTask);
			wResult.Result.ID = wSendNCRTask.ID;
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 不合格申请例外放行流程结束后，自动知会质检班长和对应的工位检验员
	 */
	@Override
	public void SendMessageToChecker(BMSEmployee wLoginUser, SendNCRTask wSendNCRTask) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// 质检班班长
			List<Integer> wMoList = SendNCRTaskDAO.getInstance().GetCheckMonitorIDList(wLoginUser, wErrorCode);
			// 工位检验员
			List<Integer> wCheckerList = SendNCRTaskDAO.getInstance().GetCheckerIDList(wLoginUser,
					wSendNCRTask.DepartmentID, wSendNCRTask.StationID, wErrorCode);
			wCheckerList = wCheckerList.stream().distinct().collect(Collectors.toList());
			for (Integer wUserID : wMoList) {
				if (wCheckerList.stream().anyMatch(p -> p.intValue() == wUserID.intValue())) {
					continue;
				}
				wCheckerList.add(wUserID);
			}
			// 发送消息
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			for (int wUserID : wCheckerList) {
				BFCMessage wBFCMessage = new BFCMessage();
				wBFCMessage.Active = 1;
				wBFCMessage.CompanyID = wLoginUser.getCompanyID();
				wBFCMessage.MessageID = wSendNCRTask.getID();
				wBFCMessage.ModuleID = BPMEventModule.SCNCR.getValue();
				wBFCMessage.Type = BFCMessageType.Notify.getValue();
				wBFCMessage.ShiftID = 0;
				wBFCMessage.StepID = 0;
				wBFCMessage.Title = StringUtils.Format("{0} 例外放行单知会", new Object[] { wSendNCRTask.Code });
				wBFCMessage.MessageText = StringUtils.Format("模块：{0} 处理人：{1} 处理时刻：{2}  已审批通过",
						new Object[] { BPMEventModule.getEnumType(wSendNCRTask.getFlowType()).getLable(),
								wLoginUser.Name,
								StringUtils.parseCalendarToString(wSendNCRTask.CreateTime, "yyyy-MM-dd HH:mm") });
				wBFCMessage.ResponsorID = wUserID;
				wBFCMessage.CreateTime = Calendar.getInstance();
				wBFCMessageList.add(wBFCMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取入库检验员
	 */
	@SuppressWarnings("unused")
	private List<LFSWorkAreaChecker> GetRKLFSWorkAreaCheckerList(BMSEmployee wLoginUser) {
		List<LFSWorkAreaChecker> wResult = new ArrayList<LFSWorkAreaChecker>();
		try {
//			if (!(WDWConstans.GetBMSEmployee(wLoginUser.ID).Position == 16)) {
//				return wResult;
//			}

			List<FPCPart> wPartList = WDWConstans.GetFPCPartList().values().stream().filter(p -> p.PartType == 6)
					.collect(Collectors.toList());
			List<Integer> wCheckIDList = new ArrayList<Integer>();
			for (FPCPart wFPCPart : wPartList) {
				if (wFPCPart.CheckerList == null || wFPCPart.CheckerList.size() <= 0) {
					continue;
				}

				wCheckIDList.addAll(wFPCPart.CheckerList);
			}

			wCheckIDList = wCheckIDList.stream().distinct().collect(Collectors.toList());

			LFSWorkAreaChecker wLFSWorkAreaChecker = new LFSWorkAreaChecker();
			wLFSWorkAreaChecker.CheckerIDList = wCheckIDList;
			wResult.add(wLFSWorkAreaChecker);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_QueryTaskListByTime(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<NCRTask>> wResult = new ServiceResult<List<NCRTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = NCRTaskDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1, -1, -1, "", -1, "", -1,
					-1, -1, -1, -1, wStartTime, wEndTime, null, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTaskShow>> NCR_QueryNCRTaskShowList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<NCRTaskShow>> wResultList = new ServiceResult<List<NCRTaskShow>>();
		wResultList.Result = new ArrayList<NCRTaskShow>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 发起任务列表
			List<BPMTaskBase> wSendBaseList = NCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser,
					wLoginUser.getID(), wStartTime, wEndTime, wErrorCode);

			List<NCRTask> wSendTaskList = CloneTool.CloneArray(wSendBaseList, NCRTask.class);
			wSendTaskList = wSendTaskList.stream().filter(p -> p.Status != NCRStatus.Default.getValue())
					.collect(Collectors.toList());

			// 待办、已办任务列表
			List<BPMTaskBase> wBPMTaskBaseList = new ArrayList<BPMTaskBase>();
			wBPMTaskBaseList
					.addAll(NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(), wErrorCode));

			wBPMTaskBaseList.addAll(NCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
					wStartTime, wEndTime, wErrorCode));

			// 去重
			wBPMTaskBaseList = wBPMTaskBaseList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
							ArrayList::new));
			wBPMTaskBaseList = wBPMTaskBaseList.stream().filter(p -> p.Status != NCRStatus.Default.getValue())
					.collect(Collectors.toList());

			List<NCRTask> wAllNCRTaskList = new ArrayList<NCRTask>();
			if (wBPMTaskBaseList != null && wBPMTaskBaseList.size() > 0) {
				List<BPMTaskBase> wStepBaseList = NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
						wLoginUser.getID(), wErrorCode);
				for (BPMTaskBase wBPMTaskBase : wBPMTaskBaseList) {
					if (wStepBaseList != null && wStepBaseList.size() > 0) {
						Optional<BPMTaskBase> wTaskBase = wStepBaseList.stream().filter(p -> p.ID == wBPMTaskBase.ID)
								.findFirst();
						if (wTaskBase != null && wTaskBase.isPresent()) {
							wAllNCRTaskList.add((NCRTask) wTaskBase.get());
							continue;
						}
					}
					wAllNCRTaskList.add((NCRTask) wBPMTaskBase);
				}
			}

			List<NCRTask> wAllList = new ArrayList<NCRTask>();
			wAllList.addAll(wAllNCRTaskList);
			wAllList.addAll(wSendTaskList);

			// ①查询所有订单
			List<OMSOrder> wOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, new ArrayList<Integer>()).List(OMSOrder.class);
			wOrderList.removeIf(p -> StringUtils.isEmpty(p.PartNo));
			// ②查询所有车
			List<String> wStrs = wAllList.stream().map(p -> p.PartNos).collect(Collectors.toList());
			List<String> wPartNoList = new ArrayList<String>();
			for (String wPartNos : wStrs) {
				String[] wList = wPartNos.split(",");
				for (String wPartNo : wList) {
					wPartNoList.add(wPartNo);
				}
			}
			// ②获取老单据车型加车号
			if (wAllList.size() > 0) {
				wPartNoList.addAll(wAllList.stream().map(p -> StringUtils.Format("{0}#{1}", p.CarType, p.CarNumber))
						.collect(Collectors.toList()));
			}
			// ③去重
			if (wPartNoList.size() > 0) {
				wPartNoList = wPartNoList.stream().distinct().collect(Collectors.toList());
			}
			// ③筛选所有订单
			List<String> wPartNoListClone = wPartNoList;
			wOrderList = wOrderList.stream().filter(p -> wPartNoListClone.stream().anyMatch(q -> q.equals(p.PartNo)))
					.collect(Collectors.toList());
			// ④遍历赋值
			for (OMSOrder wOrder : wOrderList) {
				NCRTaskShow wTaskShow = new NCRTaskShow();

				wTaskShow.OrderID = wOrder.ID;
				wTaskShow.PartNo = wOrder.PartNo;
				wTaskShow.DoList = wAllNCRTaskList.stream()
						.filter(p -> (StringUtils.Format("{0}#{1}", p.CarType, p.CarNumber).equals(wOrder.PartNo)
								|| p.PartNos.contains(wOrder.PartNo)) && p.StepID <= 0)
						.collect(Collectors.toList());
				wTaskShow.UndoList = wAllNCRTaskList.stream()
						.filter(p -> (StringUtils.Format("{0}#{1}", p.CarType, p.CarNumber).equals(wOrder.PartNo)
								|| p.PartNos.contains(wOrder.PartNo)) && p.StepID > 0)
						.collect(Collectors.toList());
				wTaskShow.SendList = wSendTaskList.stream()
						.filter(p -> (StringUtils.Format("{0}#{1}", p.CarType, p.CarNumber).equals(wOrder.PartNo)
								|| p.PartNos.contains(wOrder.PartNo)))
						.collect(Collectors.toList());
				wTaskShow.StationNum = (int) wAllList.stream()
						.filter(p -> (StringUtils.Format("{0}#{1}", p.CarType, p.CarNumber).equals(wOrder.PartNo)
								|| p.PartNos.contains(wOrder.PartNo)))
						.map(p -> p.StationID).distinct().count();
				wResultList.Result.add(wTaskShow);
			}

//			for (NCRTask wNCRTask : wAllNCRTaskList) {
//				if (wResultList.Result.stream().anyMatch(p -> p.OrderID == wNCRTask.OrderID)) {
//					NCRTaskShow wNCRTaskShow = wResultList.Result.stream().filter(p -> p.OrderID == wNCRTask.OrderID)
//							.findFirst().get();
//					wNCRTaskShow.DoList = wAllNCRTaskList.stream()
//							.filter(p -> p.OrderID == wNCRTask.OrderID && p.StepID <= 0).collect(Collectors.toList());
//					wNCRTaskShow.UndoList = wAllNCRTaskList.stream()
//							.filter(p -> p.OrderID == wNCRTask.OrderID && p.StepID > 0).collect(Collectors.toList());
//					wNCRTaskShow.StationNum = (int) wAllNCRTaskList.stream().filter(p -> p.OrderID == wNCRTask.OrderID)
//							.map(p -> p.StationID).distinct().count();
//				} else {
//					NCRTaskShow wTaskShow = new NCRTaskShow();
//					wTaskShow.OrderID = wNCRTask.OrderID;
//					wTaskShow.PartNo = StringUtils.Format("{0}#{1}", wNCRTask.CarType, wNCRTask.CarNumber);
//					wTaskShow.DoList = wAllNCRTaskList.stream()
//							.filter(p -> p.OrderID == wNCRTask.OrderID && p.StepID <= 0).collect(Collectors.toList());
//					wTaskShow.UndoList = wAllNCRTaskList.stream()
//							.filter(p -> p.OrderID == wNCRTask.OrderID && p.StepID > 0).collect(Collectors.toList());
//					wTaskShow.StationNum = (int) wAllNCRTaskList.stream().filter(p -> p.OrderID == wNCRTask.OrderID)
//							.map(p -> p.StationID).distinct().count();
//					wResultList.Result.add(wTaskShow);
//				}
//			}
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> NCR_QueryTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResultList = new ServiceResult<List<BPMTaskBase>>();
		wResultList.Result = new ArrayList<BPMTaskBase>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			switch (TagTypes.getEnumType(wTagType)) {
			case Applicant:
				// 发起
				wResultList.Result = NCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 接收
				// 根据自身权限获取所有待做任务
				// --待办--
				wResultList.Result = NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);
				break;
			case Confirmer:// 确认
			case Approver:// 审批
				// 已办
				wResultList.Result = NCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			// ③排序
			if (wResultList.Result.size() > 0) {
				wResultList.Result.sort(Comparator.comparing(BPMTaskBase::getStatus)
						.thenComparing(BPMTaskBase::getSubmitTime, Comparator.reverseOrder()));
			}

		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> NCR_QuerySendTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResultList = new ServiceResult<List<BPMTaskBase>>();
		wResultList.Result = new ArrayList<BPMTaskBase>();
		try {

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (TagTypes.getEnumType(wTagType)) {
			case Applicant:
				// 发起
				wResultList.Result = SendNCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 接收
				// 根据自身权限获取所有待做任务
				// --待办--
				wResultList.Result = SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);
				break;
			case Confirmer:// 确认
			case Approver:// 审批
				// 已办
				wResultList.Result = SendNCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			// 查询待后续操作的工位检验员，方便流程跟踪
//			for (BPMTaskBase wBPMTaskBase : wResultList.Result) {
//				SendNCRTask wSendNCRTask = (SendNCRTask) wBPMTaskBase;
//				if (wSendNCRTask.Status == 3) {
//					List<Integer> wCheckerIDList = WDWConstans.GetFPCPart(wSendNCRTask.StationID).CheckerList;
//					List<String> wNames = new ArrayList<String>();
//					for (Integer wUserID : wCheckerIDList) {
//						BMSEmployee wUser = WDWConstans.GetBMSEmployee(wUserID);
//						wNames.add(StringUtils.Format("{0}({1})", wUser.Name,
//								wUser.LoginID.substring(wUser.LoginID.length() - 6)));
//					}
//					wSendNCRTask.Checkers = StringUtils.Join(",", wNames);
//				}
//
//				if (wSendNCRTask.NCRID > 0) {
//					int wUpflowID = NCRTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wSendNCRTask.NCRID, "",
//							wErrorCode).UpFlowID;
//					BMSEmployee wUser = WDWConstans.GetBMSEmployee(wUpflowID);
//					if (wUser.ID > 0) {
//						wSendNCRTask.Checkers = StringUtils.Format("{0}({1})", wUser.Name,
//								wUser.LoginID.substring(wUser.LoginID.length() - 6));
//					}
//				}
//			}
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<SendNCRTaskShow>> NCR_QuerySendNCRTaskShow(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<SendNCRTaskShow>> wResultList = new ServiceResult<List<SendNCRTaskShow>>();
		wResultList.Result = new ArrayList<SendNCRTaskShow>();
		try {
			List<BPMTaskBase> wBPMTaskBaseList = new ArrayList<BPMTaskBase>();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wBPMTaskBaseList.addAll(
					SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(), wErrorCode));
			wBPMTaskBaseList.addAll(SendNCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
					wStartTime, wEndTime, wErrorCode));

			List<BPMTaskBase> wSendBaseList = SendNCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser,
					wLoginUser.getID(), wStartTime, wEndTime, wErrorCode);

			List<SendNCRTask> wSendTaskList = CloneTool.CloneArray(wSendBaseList, SendNCRTask.class);

			wSendTaskList = wSendTaskList.stream().filter(p -> p.Status != NCRStatus.Default.getValue())
					.collect(Collectors.toList());

			// 去重
			wBPMTaskBaseList = wBPMTaskBaseList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
							ArrayList::new));
			wBPMTaskBaseList = wBPMTaskBaseList.stream().filter(p -> p.Status != NCRStatus.Default.getValue())
					.collect(Collectors.toList());

			List<SendNCRTask> wAllNCRTaskList = new ArrayList<SendNCRTask>();
			if (wBPMTaskBaseList != null && wBPMTaskBaseList.size() > 0) {
//				List<BPMTaskBase> wStepBaseList = SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
//						wLoginUser.getID(), wErrorCode);
				for (BPMTaskBase wBPMTaskBase : wBPMTaskBaseList) {
//					if (wStepBaseList != null && wStepBaseList.size() > 0) {
//						Optional<BPMTaskBase> wTaskBase = wStepBaseList.stream().filter(p -> p.ID == wBPMTaskBase.ID)
//								.findFirst();
//						if (wTaskBase != null && wTaskBase.isPresent()) {
//							wAllNCRTaskList.add((SendNCRTask) wTaskBase.get());
//							continue;
//						}
//					}
					wAllNCRTaskList.add((SendNCRTask) wBPMTaskBase);
				}
			}

			List<SendNCRTask> wAllList = new ArrayList<SendNCRTask>();
			wAllList.addAll(wAllNCRTaskList);
			wAllList.addAll(wSendTaskList);

			for (SendNCRTask wSendNCRTask : wAllList) {
				if (wResultList.Result.stream().anyMatch(p -> p.OrderID == wSendNCRTask.OrderID)) {
					continue;
				} else {
					SendNCRTaskShow wTaskShow = new SendNCRTaskShow();

					wTaskShow.OrderID = wSendNCRTask.OrderID;
					wTaskShow.PartNo = StringUtils.Format("{0}#{1}", wSendNCRTask.CarType, wSendNCRTask.CarNumber);
					wTaskShow.DoList = wAllNCRTaskList.stream()
							.filter(p -> p.OrderID == wSendNCRTask.OrderID && p.StepID <= 0)
							.collect(Collectors.toList());
					wTaskShow.UndoList = wAllNCRTaskList.stream()
							.filter(p -> p.OrderID == wSendNCRTask.OrderID && p.StepID > 0)
							.collect(Collectors.toList());
					wTaskShow.SendList = wSendTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
							.collect(Collectors.toList());
					wTaskShow.StationNum = (int) wAllList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
							.map(p -> p.StationID).distinct().count();
					wResultList.Result.add(wTaskShow);
				}
			}
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<Integer>> NCR_GetStationList(BMSEmployee wBMSEmployee,
			List<FPCRoutePart> wFPCRoutePartList, int wPartID) {
		ServiceResult<List<Integer>> wResultList = new ServiceResult<List<Integer>>();
		try {
			if (wFPCRoutePartList != null && wFPCRoutePartList.size() > 0) {
				if (wPartID > 0) {
					List<FPCRoutePart> wSonList = wFPCRoutePartList.stream().filter(p -> p.PrevPartID == wPartID)
							.collect(Collectors.toList());
					if (wSonList != null && wSonList.size() > 0) {
						for (FPCRoutePart wItem : wSonList) {
							wResultList.Result.add(wItem.PartID);
							if ((NCR_GetStationList(wBMSEmployee, wFPCRoutePartList, wItem.PartID)).Result != null
									&& (NCR_GetStationList(wBMSEmployee, wFPCRoutePartList, wItem.PartID)).Result
											.size() > 0)
								wResultList.Result.addAll(
										(NCR_GetStationList(wBMSEmployee, wFPCRoutePartList, wItem.PartID)).Result);
						}
					}
				}
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<NCRFrequency>> NCR_SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<NCRFrequency>> wResultList = new ServiceResult<List<NCRFrequency>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = NCRTaskDAO.getInstance().SelectFrequency(wLoginUser, wStartTime, wEndTime, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;

	}

	@Override
	public ServiceResult<List<Integer>> NCR_PartIDListByOrderID(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<Integer>> wResultList = new ServiceResult<List<Integer>>();
		try {
			wResultList.Result = new ArrayList<Integer>();
//			wResultList.Result = NCRTaskDAO.getInstance().Get_PartIDListByOrderID(wLoginUser, wOrderID);

			OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder.RouteID > 0) {
				List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
						.FPC_QueryRoutePartListByRouteID(wLoginUser, wOrder.RouteID).List(FPCRoutePart.class);
				// 排序
				wRoutePartList.sort(Comparator.comparing(FPCRoutePart::getOrderID));
				wResultList.Result = wRoutePartList.stream().map(p -> p.PartID).distinct().collect(Collectors.toList());
			} else {
				return wResultList;
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<Integer>> NCR_PartIDListByUser(BMSEmployee wLoginUser) {
		ServiceResult<List<Integer>> wResultList = new ServiceResult<List<Integer>>();
		try {
			wResultList.Result = new ArrayList<Integer>();
			wResultList.Result = NCRTaskDAO.getInstance().Get_PartIDListByUser(wLoginUser);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<FPCPart>> NCR_ReturnPartIDList(BMSEmployee wLoginUser, List<Integer> wOrderStationIDList,
			List<Integer> wUserStationIDList) {
		ServiceResult<List<FPCPart>> wResultList = new ServiceResult<List<FPCPart>>();
		try {
			wResultList.Result = new ArrayList<FPCPart>();

			for (Integer wPartID : wOrderStationIDList) {
				FPCPart wPart = WDWConstans.GetFPCPart(wPartID);
				if (wPart != null && wPart.ID > 0) {
					wResultList.Result.add(wPart);
				}
			}

//			wResultList.Result = NCRTaskDAO.getInstance().Get_ReturnPartIDList(wLoginUser, wOrderStationIDList,
//					wUserStationIDList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 创建不合格评审任务
	 */
	@Override
	public ServiceResult<NCRTask> NCR_CreateTask(BMSEmployee wBMSEmployee, BPMEventModule wModule) {
		ServiceResult<NCRTask> wResult = new ServiceResult<NCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = NCRTaskDAO.getInstance().NCR_CreateTask(wBMSEmployee, wModule, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 创建不合格评审申请任务
	 */
	@Override
	public ServiceResult<SendNCRTask> NCR_CreateSendTask(BMSEmployee wLoginUser, BPMEventModule wModule) {
		ServiceResult<SendNCRTask> wResult = new ServiceResult<SendNCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SendNCRTaskDAO.getInstance().NCR_CreateTask(wLoginUser, wModule, wErrorCode);
			wResult.Result.DepartmentID = wLoginUser.DepartmentID;
			wResult.Result.Department = WDWConstans.GetBMSDepartmentName(wLoginUser.DepartmentID);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Boolean> NCR_AddSonFolw(BMSEmployee wLoginUser, NCRTask wNCRTask) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>(true);
		try {
			List<BPMOperationStep> wBPMOperationStepList = BPMServiceImpl.getInstance()
					.BPM_GetOperationByTaskID(wLoginUser, wNCRTask.StepID).List(BPMOperationStep.class);
			if (wBPMOperationStepList != null && wBPMOperationStepList.size() > 0) {
				Field[] fields = wNCRTask.getClass().getFields();
				for (Field wField : fields) {
					if (!wField.getName().equals(wBPMOperationStepList.get(0).Name))
						continue;

					wField.set(wNCRTask, CloneTool.Clone(wBPMOperationStepList.get(0).Value, wField.getType()));
				}
			}
			wResult = BPMServiceImpl.getInstance().BPM_MsgUpdate(wLoginUser, wNCRTask.StepID, 0, wNCRTask, wNCRTask);
			if (!wResult.getResult()) {
				wResult.Result = true;
				return wResult;
			}
			this.NCR_UpdateTask(wLoginUser, wNCRTask);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<NCRTask> NCR_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode) {
		ServiceResult<NCRTask> wResult = new ServiceResult<NCRTask>();
		wResult.Result = new NCRTask();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = (NCRTask) NCRTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wTaskID, wCode, wErrorCode);
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<UserWorkArea>> NCR_GetDepartment(BMSEmployee wLoginUser) {
		ServiceResult<List<UserWorkArea>> wResultList = new ServiceResult<List<UserWorkArea>>();
		try {
			wResultList.Result = new ArrayList<UserWorkArea>();
			BMSDepartment wBMSDepartment = NCRTaskDAO.getInstance().Get_Department(wLoginUser, wLoginUser.DepartmentID);
			List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = new ArrayList<LFSWorkAreaChecker>();
			if (wBMSDepartment != null && wBMSDepartment.Type == 2) {
				wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, wBMSDepartment.ID).List(LFSWorkAreaChecker.class);
			} else {
				wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance().LFS_QueryWorkAreaCheckerList(wLoginUser, -1)
						.List(LFSWorkAreaChecker.class);
			}
			if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
				Map<Object, BMSDepartment> wBMSDeList = CoreServiceImpl.getInstance()
						.BMS_QueryDepartmentList(wLoginUser).List(BMSDepartment.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				Map<Object, BMSEmployee> wEmployeeList = CoreServiceImpl.getInstance()
						.BMS_GetEmployeeAll(wLoginUser, -1, -1, 1).List(BMSEmployee.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				for (LFSWorkAreaChecker wLFSWorkAreaChecker : wLFSWorkAreaCheckerList) {
					if (wLFSWorkAreaChecker.LeaderIDList != null && wLFSWorkAreaChecker.LeaderIDList.size() > 0) {
						for (Integer wInteger : wLFSWorkAreaChecker.LeaderIDList) {
							UserWorkArea wUserWorkArea = new UserWorkArea();
							wUserWorkArea.UserID = wInteger;
							wUserWorkArea.WorkID = wLFSWorkAreaChecker.WorkAreaID;
							String wName = "";
							String wWorkName = "";
							if (wEmployeeList.containsKey(wUserWorkArea.UserID)) {
								wName = wEmployeeList.get(wUserWorkArea.UserID).Name;
							}
							if (wBMSDeList.containsKey(wUserWorkArea.WorkID)) {
								wWorkName = wBMSDeList.get(wUserWorkArea.WorkID).Name;
							}
							wUserWorkArea.Name = StringUtils.Format("{0}({1})", wName, wWorkName);
							wResultList.Result.add(wUserWorkArea);
						}
					}
				}
			}
		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<Integer>> NCR_GetSendIPTItenList(BMSEmployee wLoginUser, int wTaskStepID) {
		ServiceResult<List<Integer>> wResultList = new ServiceResult<List<Integer>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = NCRTaskDAO.getInstance().NCR_GetItemListByProcessID(wLoginUser, wTaskStepID,
					wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_UndoTaskList(BMSEmployee wLoginUser, int wOrderID, int wType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<NCRTask>> wResultList = new ServiceResult<List<NCRTask>>();
		try {
			ServiceResult<List<NCRTaskShow>> wServiceResultList = NCRServiceImpl.getInstance()
					.NCR_QueryNCRTaskShowList(wLoginUser, wStartTime, wEndTime);

			if (wServiceResultList.Result != null && wServiceResultList.Result.size() > 0) {
				NCRTaskShow wNCRTaskShow = wServiceResultList.Result.stream().filter(p -> p.OrderID == wOrderID)
						.findFirst().get();
				if (wType == 0) {
					wResultList.Result = wNCRTaskShow.UndoList;
				} else {
					wResultList.Result = wNCRTaskShow.DoList;
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return wResultList;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> NCR_UndoSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID, int wType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SendNCRTask>> wResultList = new ServiceResult<List<SendNCRTask>>();
		try {
			ServiceResult<List<SendNCRTaskShow>> wServiceResultList = NCRServiceImpl.getInstance()
					.NCR_QuerySendNCRTaskShow(wLoginUser, wStartTime, wEndTime);
			if (wServiceResultList.Result != null && wServiceResultList.Result.size() > 0) {
				SendNCRTaskShow wNCRTaskShow = wServiceResultList.Result.stream().filter(p -> p.OrderID == wOrderID)
						.findFirst().get();
				if (wType == 0) {
					wResultList.Result = wNCRTaskShow.UndoList;
				} else {
					wResultList.Result = wNCRTaskShow.DoList;
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return wResultList;
	}

	/**
	 * 不合格评审工位显示任务
	 */
	@Override
	public ServiceResult<List<NCRPartTaskShow>> RRO_QueryPartNCRTaskList(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<NCRPartTaskShow>> wResultList = new ServiceResult<List<NCRPartTaskShow>>();
		try {
			wResultList.Result = new ArrayList<NCRPartTaskShow>();

			ServiceResult<List<NCRTaskShow>> wServiceResult = NCR_QueryNCRTaskShowList(wLoginUser, wStartTime,
					wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<NCRTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.OrderID == wOrderID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;
			NCRTaskShow wRROItemTaskShow = wOptional.get();

			List<NCRTask> wAllList = new ArrayList<NCRTask>();

			wAllList.addAll(wRROItemTaskShow.DoList);
			wAllList.addAll(wRROItemTaskShow.UndoList);
			wAllList.addAll(wRROItemTaskShow.SendList);

			if (wRROItemTaskShow.DoList != null && wRROItemTaskShow.DoList.size() > 0)
				wRROItemTaskShow.UndoList.addAll(wRROItemTaskShow.DoList);

//			if (wRROItemTaskShow.UndoList == null || wRROItemTaskShow.UndoList.size() <= 0)
//				return wResultList;

			OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);

			for (NCRTask wRROItemTask : wAllList) {
				if (!wResultList.Result.stream().anyMatch(p -> p.PartID == wRROItemTask.StationID)) {
					NCRPartTaskShow wTaskShow = new NCRPartTaskShow();
					wTaskShow.OrderID = wRROItemTask.OrderID;
					wTaskShow.PartNo = wOrder.PartNo;
					wTaskShow.PartID = wRROItemTask.StationID;
					wTaskShow.PartName = wRROItemTask.StationName;
					wTaskShow.DoList = wRROItemTaskShow.DoList.stream()
							.filter(p -> p.StationID == wRROItemTask.StationID && p.StepID <= 0)
							.collect(Collectors.toList());
					wTaskShow.UndoList = wRROItemTaskShow.UndoList.stream()
							.filter(p -> p.StationID == wRROItemTask.StationID && p.StepID > 0)
							.collect(Collectors.toList());
					wTaskShow.SendList = wRROItemTaskShow.SendList.stream()
							.filter(p -> p.StationID == wRROItemTask.StationID).collect(Collectors.toList());
					wResultList.Result.add(wTaskShow);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * 不合格评审工位查询待做已做
	 */
	@Override
	public ServiceResult<List<NCRTask>> RRO_PartUndoNCRTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wType, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<NCRTask>> wResultList = new ServiceResult<List<NCRTask>>();
		try {
			wResultList.Result = new ArrayList<NCRTask>();

			ServiceResult<List<NCRPartTaskShow>> wServiceResult = RRO_QueryPartNCRTaskList(wLoginUser, wOrderID,
					wStartTime, wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<NCRPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;
			NCRPartTaskShow wRROPartTaskShow = wOptional.get();

			if (wType == 0) {
				wResultList.Result = wRROPartTaskShow.UndoList;
			} else {
				wResultList.Result = wRROPartTaskShow.DoList;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<SendNCRPartTaskShow>> RRO_QueryPartSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SendNCRPartTaskShow>> wResultList = new ServiceResult<List<SendNCRPartTaskShow>>();
		try {
			wResultList.Result = new ArrayList<SendNCRPartTaskShow>();
			ServiceResult<List<SendNCRTaskShow>> wServiceResult = NCR_QuerySendNCRTaskShow(wLoginUser, wStartTime,
					wEndTime);

			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<SendNCRTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.OrderID == wOrderID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;

			SendNCRTaskShow wRROItemTaskShow = wOptional.get();

//			if (wRROItemTaskShow.DoList != null && wRROItemTaskShow.DoList.size() > 0) {
//				wRROItemTaskShow.UndoList.addAll(wRROItemTaskShow.DoList);
//			}
//
//			if (wRROItemTaskShow.UndoList == null || wRROItemTaskShow.UndoList.size() <= 0)
//				return wResultList;

			List<SendNCRTask> wAllList = new ArrayList<SendNCRTask>();

			wAllList.addAll(wRROItemTaskShow.UndoList);
			wAllList.addAll(wRROItemTaskShow.DoList);
			wAllList.addAll(wRROItemTaskShow.SendList);

			for (SendNCRTask wSendNCRTask : wAllList) {
				if (wResultList.Result.stream().anyMatch(p -> p.PartID == wSendNCRTask.StationID)) {
					continue;
				} else {
					SendNCRPartTaskShow wTaskShow = new SendNCRPartTaskShow();

					wTaskShow.OrderID = wSendNCRTask.OrderID;
					wTaskShow.PartNo = StringUtils.Format("{0}#{1}", wSendNCRTask.CarType, wSendNCRTask.CarNumber);
					wTaskShow.PartID = wSendNCRTask.StationID;
					wTaskShow.PartName = wSendNCRTask.StationName;
					wTaskShow.DoList = wRROItemTaskShow.DoList.stream()
							.filter(p -> p.StationID == wSendNCRTask.StationID && p.StepID <= 0)
							.collect(Collectors.toList());
					wTaskShow.UndoList = wRROItemTaskShow.UndoList.stream()
							.filter(p -> p.StationID == wSendNCRTask.StationID && p.StepID > 0)
							.collect(Collectors.toList());
					wTaskShow.SendList = wRROItemTaskShow.SendList.stream()
							.filter(p -> p.StationID == wSendNCRTask.StationID).collect(Collectors.toList());
					wResultList.Result.add(wTaskShow);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> RRO_PartUndoSendNCRTaskList(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, int wType, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SendNCRTask>> wResultList = new ServiceResult<List<SendNCRTask>>();
		try {
			wResultList.Result = new ArrayList<SendNCRTask>();
			ServiceResult<List<SendNCRPartTaskShow>> wServiceResult = RRO_QueryPartSendNCRTaskList(wLoginUser, wOrderID,
					wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<SendNCRPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;
			SendNCRPartTaskShow wRROPartTaskShow = wOptional.get();

			if (wType == 0) {
				wResultList.Result = wRROPartTaskShow.UndoList;
			} else {
				wResultList.Result = wRROPartTaskShow.DoList;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<String> ExportPdf(BMSEmployee wLoginUser, int wTaskID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("不合格品评审报告{0}.pdf", new Object[] { wCurTime, });

			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			Map<String, Object> wMap = this.GetMap(wLoginUser, wTaskID);

			String wTemplatePath = Constants.getConfigPath() + "TemplateNcr.pdf";
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			PdfUtils.pdfOut(wMap, wFileOutputStream, wTemplatePath, 1);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取NCR数据源
	 */
	private Map<String, Object> GetMap(BMSEmployee wLoginUser, int wTaskID) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");
			int wYear = Calendar.getInstance().get(Calendar.YEAR);
			int wMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

			Map<String, String> wDateMap = new HashMap<String, String>();

			NCRTask wNCRTask = NCRTaskDAO.getInstance().SelectByID(wLoginUser, wTaskID, wErrorCode);
			List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
					.BPM_GetActivitiHisTaskByPIId(wLoginUser, wNCRTask.FlowID).List(BPMActivitiHisTask.class);

			wDateMap.put("OccurDepartment", wNCRTask.Department);
			wDateMap.put("PartNo", wNCRTask.CarNumber);
			wDateMap.put("DutyDepartment",
					WDWConstans.GetBMSDepartmentName(Integer.parseInt(wNCRTask.DutyDepartmentID)));
			wDateMap.put("Type", NCRType.getEnumType(wNCRTask.Type).getLable());
			wDateMap.put("OccurDate", wSDF.format(wNCRTask.CreateTime.getTime()));
			wDateMap.put("LeverName", GetLeverName(wLoginUser, wHisList, wNCRTask));
			wDateMap.put("Describe", wNCRTask.DescribeInfo);
			wDateMap.put("Reason", GetReason(wLoginUser, wHisList));
			wDateMap.put("Opinions", GetOpinions(wLoginUser, wHisList));
			wDateMap.put("SendName", wNCRTask.UpFlowName);
			wDateMap.put("SendTime", wSDF.format(wNCRTask.CreateTime.getTime()));
			wDateMap.put("OccurName", "");
			wDateMap.put("QualityMName", GetQualityMName(wLoginUser, wHisList));
			wDateMap.put("OtherName", GetOtherName(wLoginUser, wHisList));
			wDateMap.put("DesignName", "");
			wDateMap.put("MakingName", "");
			wDateMap.put("TechName", GetTechName(wLoginUser, wHisList));
			wDateMap.put("MaterialName", "");
			wDateMap.put("OKName", "");
			String wFileCode = StringUtils.Format("GZLOCO-QMPD{0}-{1}-{2}", String.valueOf(wYear).substring(2),
					String.format("%02d", wMonth), String.valueOf(wYear));
			wDateMap.put("FileCode", wFileCode);
			wDateMap.put("Level", NCRLevel.getEnumType(wNCRTask.Level).getLable());
			wDateMap.put("Code", wNCRTask.Code);
			wDateMap.put("ProductType", WDWConstans.GetFPCProductName(wNCRTask.CarTypeID));
			wDateMap.put("Number", String.valueOf(wNCRTask.Number));
			wDateMap.put("Result", NCRHandleResult.getEnumType(wNCRTask.Result).getLable());
			wDateMap.put("CraftOpinion", GetReason(wLoginUser, wHisList));
			wDateMap.put("CraftName", GetTechName(wLoginUser, wHisList));
			wDateMap.put("CraftTime", GetCraftTime(wLoginUser, wHisList));
			wDateMap.put("QualityOpinion", GetQualityOpinion(wLoginUser, wHisList));
			wDateMap.put("QualityName", GetQualityMName(wLoginUser, wHisList));
			wDateMap.put("QualityTime", GetQualityTime(wLoginUser, wHisList));
			wDateMap.put("CompanyOpinion", GetCompanyOpinion(wLoginUser, wHisList));
			wDateMap.put("CompanyName", GetCompanyName(wLoginUser, wHisList));
			wDateMap.put("CompanyDate", GetCompanyDate(wLoginUser, wHisList));
			wDateMap.put("RealOpinion", GetRealOpinion(wLoginUser, wHisList));
			wDateMap.put("VertifyName", GetVertifyName(wLoginUser, wHisList));
			wDateMap.put("VertifyDate", GetVertifyDate(wLoginUser, wHisList));

			wResult.put("datemap", wDateMap);
			wResult.put("imgmap", new HashMap<String, String>());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取NCR数据源
	 */
	private Map<String, Object> GetMapNew(BMSEmployee wLoginUser, int wTaskID) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

			Map<String, String> wDateMap = new HashMap<String, String>();

			NCRTask wNCRTask = NCRTaskDAO.getInstance().SelectByID(wLoginUser, wTaskID, wErrorCode);
			List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
					.BPM_GetActivitiHisTaskByPIId(wLoginUser, wNCRTask.FlowID).List(BPMActivitiHisTask.class);

			wDateMap.put("QualityQuestionType", wNCRTask.QuestionType == 1 ? "√" : "");
			wDateMap.put("ProductQuestionType", wNCRTask.QuestionType == 2 ? "√" : "");
			wDateMap.put("PurchaseQuestionType", wNCRTask.QuestionType == 3 ? "√" : "");
			wDateMap.put("OneType", wNCRTask.Level == NCRLevel.OneLevel.getValue() ? "√" : "");
			wDateMap.put("TwoType", wNCRTask.Level == NCRLevel.TwoLevel.getValue() ? "√" : "");
			wDateMap.put("ThreeType", wNCRTask.Level == NCRLevel.ThreeLevel.getValue() ? "√" : "");
			wDateMap.put("AType", wNCRTask.Type == NCRType.AType.getValue() ? "√" : "");
			wDateMap.put("BType", wNCRTask.Type == NCRType.BType.getValue() ? "√" : "");
			wDateMap.put("CType", wNCRTask.Type == NCRType.CType.getValue() ? "√" : "");
			wDateMap.put("Code", wNCRTask.Code);
			wDateMap.put("ProductNameAndProductNo",
					StringUtils.Format("{0} {1} {2}", wNCRTask.CarType, wNCRTask.ProductName, wNCRTask.ModelNo));
			wDateMap.put("OccurDepartment", wNCRTask.Department);
			wDateMap.put("PartNo", wNCRTask.CarNumber);
			wDateMap.put("DutyDepartment", wNCRTask.DutyDepartmentName);
			wDateMap.put("Number", String.valueOf(wNCRTask.Number));
			wDateMap.put("OccurDate", wSDF.format(wNCRTask.CreateTime.getTime()));
			wDateMap.put("LeverName", GetLeverName(wLoginUser, wHisList, wNCRTask));
			wDateMap.put("DescribeInfo", wNCRTask.DescribeInfo);
			wDateMap.put("CheckName", wNCRTask.UpFlowName);
			wDateMap.put("CheckDate", wSDF.format(wNCRTask.CreateTime.getTime()));
			wDateMap.put("LetGoReason", GetReason(wLoginUser, wHisList));
			wDateMap.put("LetGoTimeOrPartPoint", "");
			wDateMap.put("PurchaseName", "");
			wDateMap.put("PurchaseDate", "");
			wDateMap.put("ReworkType", wNCRTask.Result == NCRHandleResult.Rework.getValue() ? "√" : "");
			wDateMap.put("RepairType", wNCRTask.Result == NCRHandleResult.Repair.getValue() ? "√" : "");
			wDateMap.put("LetGoType", wNCRTask.Result == NCRHandleResult.LetGoStation.getValue()
					|| wNCRTask.Result == NCRHandleResult.LetGoTime.getValue() ? "√" : "");
			wDateMap.put("NoUseType", wNCRTask.Result == NCRHandleResult.sCrapt.getValue() ? "√" : "");
			wDateMap.put("EmergencyType", "");
			wDateMap.put("NoAcceptType", "");
			wDateMap.put("ChangeType", "");
			wDateMap.put("BackType", wNCRTask.Result == NCRHandleResult.BackGYS.getValue() ? "√" : "");
			wDateMap.put("SchemaType", wNCRTask.Result == NCRHandleResult.SendFA.getValue() ? "√" : "");
			wDateMap.put("OtherType", wNCRTask.Result == NCRHandleResult.Others.getValue() ? "√" : "");
			wDateMap.put("OtherExplain", wNCRTask.OtherResult);

			String wName = GetTechCenterName(wLoginUser, wHisList);
			String wRemark = GetTechCenterRemark(wLoginUser, wHisList);
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("TechnologyCenterName", wName);
			wDateMap.put("TechnologyCenterRemark", wRemark);
			wDateMap.put("TechnologyCenterDate", GetTechCenterDate(wLoginUser, wHisList));

			wName = GetRelationDepartmentName(wLoginUser, wHisList, 31, -1);
			wRemark = GetRelationDepartmentRemark(wLoginUser, wHisList, 31, -1);
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("ProductMakingCenterRemark", wRemark);
			wDateMap.put("ProductMakingCenterName", wName);
			wDateMap.put("ProductMakingCenterDate", GetRelationDepartmentDate(wLoginUser, wHisList, 31, -1));

			// Craft_review
			wName = GetNodeName(wLoginUser, wHisList, "Craft_review");
			wRemark = GetNodeValue(wLoginUser, wHisList, "Craft_review", "Remark");

			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("CraftMakingCenterRemark", wRemark);
			wDateMap.put("CraftMakingCenterName", wName);
			wDateMap.put("CraftMakingCenterDate", GetNodeTime(wLoginUser, wHisList, "Craft_review"));

			wName = GetRelationDepartmentName(wLoginUser, wHisList, 9, -1);
			wRemark = GetRelationDepartmentRemark(wLoginUser, wHisList, 9, -1);
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("MaterialPurchaseRemark", wRemark);
			wDateMap.put("MaterialPurchaseName", wName);
			wDateMap.put("MaterialPurchaseDate", GetRelationDepartmentDate(wLoginUser, wHisList, 9, -1));

			wDateMap.put("OtherRemark", "");
			wDateMap.put("OtherName", "");
			wDateMap.put("OtherDate", "");

			wName = GetNodeName(wLoginUser, wHisList, "QEngineeOption_review1");
			wRemark = GetNodeValue(wLoginUser, wHisList, "QEngineeOption_review1", "Remark");
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("QualityManageRemark", wRemark);
			wDateMap.put("QualityManageName", wName);
			wDateMap.put("QualityManageDate", GetNodeTime(wLoginUser, wHisList, "QEngineeOption_review1"));

			wName = GetNodeName(wLoginUser, wHisList, "QualityDirectorApproval_review1");
			wRemark = GetNodeValue(wLoginUser, wHisList, "QualityDirectorApproval_review1", "Remark");
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("AuditOpinion", wRemark);
			wDateMap.put("AuditName", wName);
			wDateMap.put("AuditDate", GetNodeTime(wLoginUser, wHisList, "QualityDirectorApproval_review1"));

			wName = GetNodeName(wLoginUser, wHisList, "ChiefEngineerApproval_review3");
			wRemark = GetNodeValue(wLoginUser, wHisList, "ChiefEngineerApproval_review3", "Remark");
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("ProductCompanyRemark", wRemark);
			wDateMap.put("ProductCompanyName", wName);
			wDateMap.put("ProductCompanyDate", GetNodeTime(wLoginUser, wHisList, "ChiefEngineerApproval_review3"));

			wName = GetNodeName(wLoginUser, wHisList, "ChiefEngineerApproval_review1");
			wRemark = GetNodeValue(wLoginUser, wHisList, "ChiefEngineerApproval_review1", "Remark");
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("TechnologyCompanyRemark", wRemark);
			wDateMap.put("TechnologyCompanyName", wName);
			wDateMap.put("TechnologyCompanyDate", GetNodeTime(wLoginUser, wHisList, "ChiefEngineerApproval_review1"));

			wName = GetVertifyName(wLoginUser, wHisList);
			wRemark = GetRealOpinion(wLoginUser, wHisList);
			if (StringUtils.isNotEmpty(wName) && StringUtils.isEmpty(wRemark)) {
				wRemark = "同意";
			}
			wDateMap.put("ConfirmRemark", wRemark);
			wDateMap.put("ConfirmName", wName);
			wDateMap.put("ConfirmDate", GetVertifyDate(wLoginUser, wHisList));

			wResult.put("datemap", wDateMap);
			wResult.put("imgmap", new HashMap<String, String>());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门的名称
	 */
	public String GetRelationDepartmentName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList,
			int wDepartmentID, int wPositionID) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			if (wPositionID <= 0) {
				if (wDepartmentID == 8) {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
										&& WDWConstans.GetBMSEmployee(
												Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
										&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)
								.findFirst().get();

						wResult = WDWConstans.GetBMSEmployee(Integer.parseInt(wTask.Assignee)).Name;
					}
				} else {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
							.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
										.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)
								.findFirst().get();

						wResult = WDWConstans.GetBMSEmployee(Integer.parseInt(wTask.Assignee)).Name;
					}
				}
			} else {
				if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)) {
					BPMActivitiHisTask wTask = wHisList.stream()
							.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
									&& WDWConstans
											.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
									&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)
							.findFirst().get();

					wResult = WDWConstans.GetBMSEmployee(Integer.parseInt(wTask.Assignee)).Name;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门的日期
	 */
	public String GetRelationDepartmentDate(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList,
			int wDepartmentID, int wPositionID) {
		String wResult = "";
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			if (wPositionID <= 0) {
				if (wDepartmentID == 8) {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
										&& WDWConstans.GetBMSEmployee(
												Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
										&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)
								.findFirst().get();

						wResult = wSDF.format(wTask.EndTime.getTime());
					}
				} else {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
							.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
										.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)
								.findFirst().get();

						wResult = wSDF.format(wTask.EndTime.getTime());
					}
				}
			} else {
				if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)) {
					BPMActivitiHisTask wTask = wHisList.stream()
							.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
									&& WDWConstans
											.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
									&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)
							.findFirst().get();

					wResult = wSDF.format(wTask.EndTime.getTime());
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取技术中心的名称
	 */
	public String GetTechCenterName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			List<Integer> wDIDList = new ArrayList<Integer>(Arrays.asList(12, 27, 25, 26, 28, 29, 30));

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream()
					.anyMatch(q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))) {
				BPMActivitiHisTask wTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream().anyMatch(
								q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))
						.findFirst().get();

				wResult = WDWConstans.GetBMSEmployee(Integer.parseInt(wTask.Assignee)).Name;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取技术中心的日期
	 */
	public String GetTechCenterDate(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			List<Integer> wDIDList = new ArrayList<Integer>(Arrays.asList(12, 27, 25, 26, 28, 29, 30));

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream()
					.anyMatch(q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))) {
				BPMActivitiHisTask wTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream().anyMatch(
								q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))
						.findFirst().get();

				wResult = wSDF.format(wTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取技术中心的备注
	 */
	public String GetTechCenterRemark(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			List<Integer> wDIDList = new ArrayList<Integer>(Arrays.asList(12, 27, 25, 26, 28, 29, 30));

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream()
					.anyMatch(q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))) {
				BPMActivitiHisTask wTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && wDIDList.stream().anyMatch(
								q -> q == WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID))
						.findFirst().get();

				if (wTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
					wResult = (String) wTask.HisTaskVarinstList.stream().filter(p -> p.VariableName.equals("Remark"))
							.findFirst().get().Value;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门的备注
	 */
	public String GetRelationDepartmentRemark(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList,
			int wDepartmentID, int wPositionID) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			if (wPositionID <= 0) {
				if (wDepartmentID == 8) {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
							&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
										&& WDWConstans.GetBMSEmployee(
												Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
										&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position != 65)
								.findFirst().get();

						if (wTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
							wResult = (String) wTask.HisTaskVarinstList.stream()
									.filter(p -> p.VariableName.equals("Remark")).findFirst().get().Value;
						}
					}
				} else {
					if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
							.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)) {
						BPMActivitiHisTask wTask = wHisList.stream()
								.filter(p -> p.ActivitiID.equals("RelevantDepartments_review") && WDWConstans
										.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID)
								.findFirst().get();

						if (wTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
							wResult = (String) wTask.HisTaskVarinstList.stream()
									.filter(p -> p.VariableName.equals("Remark")).findFirst().get().Value;
						}
					}
				}
			} else {
				if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review")
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
						&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)) {
					BPMActivitiHisTask wTask = wHisList.stream()
							.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")
									&& WDWConstans
											.GetBMSEmployee(Integer.parseInt(p.Assignee)).DepartmentID == wDepartmentID
									&& WDWConstans.GetBMSEmployee(Integer.parseInt(p.Assignee)).Position == wPositionID)
							.findFirst().get();
					if (wTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
						wResult = (String) wTask.HisTaskVarinstList.stream()
								.filter(p -> p.VariableName.equals("Remark")).findFirst().get().Value;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门评审意见和操作时刻
	 */
	@SuppressWarnings("unused")
	private String GetRelaOpinion(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");

			List<String> wOList = new ArrayList<String>();
			if (wHisList == null || wHisList.size() <= 0
					|| !wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				return wResult;
			}

			wHisList = wHisList.stream().filter(p -> p.ActivitiID.equals("RelevantDepartments_review"))
					.collect(Collectors.toList());
			for (BPMActivitiHisTask wHisTask : wHisList) {
				String wRemark = "";
				if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
					wRemark = (String) wHisTask.HisTaskVarinstList.stream().filter(p -> p.VariableName.equals("Remark"))
							.findFirst().get().Value;
				}

				String wNodeInfo = StringUtils.Format("{0}({1})\t{2}\t{3}",
						WDWConstans.GetBMSEmployeeName(Integer.parseInt(wHisTask.Assignee)),
						WDWConstans.GetBMSDepartmentName(
								WDWConstans.GetBMSEmployee(Integer.parseInt(wHisTask.Assignee)).DepartmentID),
						wRemark, wSDF.format(wHisTask.EndTime.getTime()));
				wOList.add(wNodeInfo);
			}

			wResult = StringUtils.Join("\r\n", wOList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取确认时刻
	 */
	private String GetVertifyDate(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QualityConfirm_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QualityConfirm_review")).findFirst().get();

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

				wResult = wSDF.format(wHisTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取确认人名称
	 */
	private String GetVertifyName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QualityConfirm_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QualityConfirm_review")).findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取节点操作人名称
	 */
	private String GetNodeName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList, String wNodeName) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals(wNodeName))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals(wNodeName)).findFirst()
						.get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取最后意见
	 */
	private String GetRealOpinion(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QualityConfirm_review"))) {
				List<BPMActivitiHisTask> wList = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QualityConfirm_review")).collect(Collectors.toList());
				List<String> wRemarks = new ArrayList<String>();
				for (BPMActivitiHisTask wHisTask : wList) {
					if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
						continue;
					}

					if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
						BPMActivitiHisTaskVarinst wItem = wHisTask.HisTaskVarinstList.stream()
								.filter(p -> p.VariableName.equals("Remark")).findFirst().get();
						if (StringUtils.isNotEmpty(wItem.Value.toString())) {
							wRemarks.add(wItem.Value.toString());
						}
					}
				}
				wResult = StringUtils.Join(";", wRemarks);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取公司副总时间
	 */
	private String GetCompanyDate(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("ChiefEngineerApproval_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("ChiefEngineerApproval_review")).findFirst().get();

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");

				wResult = wSDF.format(wHisTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取公司副总名称
	 */
	private String GetCompanyName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("ChiefEngineerApproval_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("ChiefEngineerApproval_review")).findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取公司副总意见
	 */
	private String GetCompanyOpinion(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("ChiefEngineerApproval_review"))) {
				List<BPMActivitiHisTask> wList = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("ChiefEngineerApproval_review")).collect(Collectors.toList());
				List<String> wRemarks = new ArrayList<String>();
				for (BPMActivitiHisTask wHisTask : wList) {
					if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
						continue;
					}

					if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
						BPMActivitiHisTaskVarinst wItem = wHisTask.HisTaskVarinstList.stream()
								.filter(p -> p.VariableName.equals("Remark")).findFirst().get();
						if (StringUtils.isNotEmpty(wItem.Value.toString())) {
							wRemarks.add(wItem.Value.toString());
						}
					}
				}
				wResult = StringUtils.Join(";", wRemarks);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取质量工程师时间
	 */
	private String GetQualityTime(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QEngineeOption_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QEngineeOption_review")).findFirst().get();

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");

				wResult = wSDF.format(wHisTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取质量工程师评审意见
	 */
	private String GetQualityOpinion(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QEngineeOption_review"))) {
				List<BPMActivitiHisTask> wList = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QEngineeOption_review")).collect(Collectors.toList());
				List<String> wRemarks = new ArrayList<String>();
				for (BPMActivitiHisTask wHisTask : wList) {
					if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
						continue;
					}

					if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
						BPMActivitiHisTaskVarinst wItem = wHisTask.HisTaskVarinstList.stream()
								.filter(p -> p.VariableName.equals("Remark")).findFirst().get();
						if (StringUtils.isNotEmpty(wItem.Value.toString())) {
							wRemarks.add(wItem.Value.toString());
						}
					}
				}
				wResult = StringUtils.Join(";", wRemarks);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工艺时刻
	 */
	private String GetCraftTime(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("Craft_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals("Craft_review"))
						.findFirst().get();

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");

				wResult = wSDF.format(wHisTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取节点操作时刻
	 */
	private String GetNodeTime(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList, String wNodeName) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals(wNodeName))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals(wNodeName)).findFirst()
						.get();

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

				wResult = wSDF.format(wHisTask.EndTime.getTime());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门人员
	 */
	private String GetOtherName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")).findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取质量工程师
	 */
	private String GetQualityMName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QEngineeOption_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("QEngineeOption_review")).findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工艺师
	 */
	private String GetTechName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("Craft_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals("Craft_review"))
						.findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取相关部门评审意见
	 */
	private String GetOpinions(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("RelevantDepartments_review"))) {
				List<BPMActivitiHisTask> wList = wHisList.stream()
						.filter(p -> p.ActivitiID.equals("RelevantDepartments_review")).collect(Collectors.toList());
				List<String> wRemarks = new ArrayList<String>();
				for (BPMActivitiHisTask wHisTask : wList) {
					if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
						continue;
					}

					if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
						BPMActivitiHisTaskVarinst wItem = wHisTask.HisTaskVarinstList.stream()
								.filter(p -> p.VariableName.equals("Remark")).findFirst().get();
						if (StringUtils.isNotEmpty(wItem.Value.toString())) {
							wRemarks.add(wItem.Value.toString());
						}
					}
				}
				wResult = StringUtils.Join(";", wRemarks);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取让步放行原因
	 */
	private String GetReason(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("Craft_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals("Craft_review"))
						.findFirst().get();
				if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
					return wResult;
				}

				if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals("Remark"))) {
					BPMActivitiHisTaskVarinst wVarinst = wHisTask.HisTaskVarinstList.stream()
							.filter(p -> p.VariableName.equals("Remark")).findFirst().get();
					wResult = wVarinst.Value.toString();
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取某节点填写的属性值(字符串)
	 */
	private String GetNodeValue(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList, String wNodeName,
			String wPropertyName) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals(wNodeName))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals(wNodeName)).findFirst()
						.get();
				if (wHisTask.HisTaskVarinstList == null || wHisTask.HisTaskVarinstList.size() <= 0) {
					return wResult;
				}

				if (wHisTask.HisTaskVarinstList.stream().anyMatch(p -> p.VariableName.equals(wPropertyName))) {
					BPMActivitiHisTaskVarinst wVarinst = wHisTask.HisTaskVarinstList.stream()
							.filter(p -> p.VariableName.equals(wPropertyName)).findFirst().get();
					wResult = wVarinst.Value.toString();
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取评定等级的人
	 */
	private String GetLeverName(BMSEmployee wLoginUser, List<BPMActivitiHisTask> wHisList, NCRTask wNCRTask) {
		String wResult = "";
		try {
			if (wHisList == null || wHisList.size() <= 0) {
				return wResult;
			}

			if (wHisList.stream().anyMatch(p -> p.ActivitiID.equals("QEngineeGrade_review"))) {
				BPMActivitiHisTask wHisTask = wHisList.stream().filter(p -> p.ActivitiID.equals("QEngineeGrade_review"))
						.findFirst().get();
				String[] wIDs = wHisTask.Assignee.split(",");
				List<String> wNames = new ArrayList<String>();
				for (String wID : wIDs) {
					String wName = WDWConstans.GetBMSEmployeeName(Integer.parseInt(wID));
					if (StringUtils.isNotEmpty(wName)) {
						wNames.add(wName);
					}
				}
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportPdfNew(BMSEmployee wLoginUser, int wTaskID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("不合格品评审报告{0}.pdf", new Object[] { wCurTime, });

			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			Map<String, Object> wMap = this.GetMapNew(wLoginUser, wTaskID);

			String wTemplatePath = Constants.getConfigPath() + "TemplateNcrNew.pdf";
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			PdfUtils.pdfOut(wMap, wFileOutputStream, wTemplatePath, 2);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> NCR_QueryManagerList(BMSEmployee wLoginUser, int wPartID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			FPCPart wPart = WDWConstans.GetFPCPart(wPartID);
			if (wPart == null || wPart.ID <= 0) {
				return wResult;
			}

			switch (wPart.PartType) {
			case 1:// 生产工位
				List<LFSWorkAreaStation> wAreaStationList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaStationList(wLoginUser, -1).List(LFSWorkAreaStation.class);
				if (wAreaStationList == null || wAreaStationList.size() <= 0) {
					return wResult;
				}

				wAreaStationList = wAreaStationList.stream().filter(p -> p.Active == 1 && p.StationID == wPartID)
						.collect(Collectors.toList());
				if (wAreaStationList == null || wAreaStationList.size() <= 0) {
					return wResult;
				}

				List<LFSWorkAreaChecker> wCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, wAreaStationList.get(0).WorkAreaID)
						.List(LFSWorkAreaChecker.class);
				if (wCheckerList == null || wCheckerList.size() <= 0) {
					return wResult;
				}

				wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wCheckerList.stream().anyMatch(q -> q.LeaderIDList != null
								&& q.LeaderIDList.size() > 0 && q.LeaderIDList.contains(p.ID)))
						.collect(Collectors.toList());
				break;
			case 6:// 物流工位
					// 权限码24
				List<BMSRoleItem> wUList = CoreServiceImpl.getInstance().BMS_UserAllByRoleID(wLoginUser, 24)
						.List(BMSRoleItem.class);

				wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wUList.stream().anyMatch(q -> q.FunctionID == p.ID)).collect(Collectors.toList());
				break;
			default:
				break;
			}

			if (wResult.Result.size() <= 0) {
				List<LFSWorkAreaChecker> wCList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
				wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wCList.stream().anyMatch(q -> q.LeaderIDList.contains(p.ID)))
						.collect(Collectors.toList());
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> NCR_QuerySameClassMembers(BMSEmployee wLoginUser, int wPersonID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wPersonID <= 0) {
				return wResult;
			}

			int wDID = WDWConstans.GetBMSEmployee(wPersonID).DepartmentID;
			if (wDID <= 0) {
				return wResult;
			}

			wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream().filter(p -> p.DepartmentID == wDID)
					.collect(Collectors.toList());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTaskPro>> NCR_QueryNCRTaskProListByTagType(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<NCRTaskPro>> wResult = new ServiceResult<List<NCRTaskPro>>();
		wResult.Result = new ArrayList<NCRTaskPro>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<BPMTaskBase> wAuditList = null;
			List<BPMTaskBase> wApplyList = null;

			// 今日时间
			Calendar wTodaySTime = Calendar.getInstance();
			wTodaySTime.set(Calendar.HOUR_OF_DAY, 0);
			wTodaySTime.set(Calendar.MINUTE, 0);
			wTodaySTime.set(Calendar.SECOND, 0);
			wStartTime = wTodaySTime;

			Calendar wTodayETime = Calendar.getInstance();
			wTodayETime.set(Calendar.HOUR_OF_DAY, 23);
			wTodayETime.set(Calendar.MINUTE, 59);
			wTodayETime.set(Calendar.SECOND, 59);
			wEndTime = wTodayETime;

			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:
				// 发起
				wAuditList = NCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(), wStartTime,
						wEndTime, wErrorCode);
				wApplyList = SendNCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 接收
				// 根据自身权限获取所有待做任务
				// --待办--
				wAuditList = NCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(), wErrorCode);
				wApplyList = SendNCRTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);
				break;
			case Confirmer:// 确认
			case Approver:// 审批
				// 已办
				wAuditList = NCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(), wStartTime,
						wEndTime, wErrorCode);
				wApplyList = SendNCRTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			// ①评审单
			if (wAuditList != null && wAuditList.size() > 0) {
				List<NCRTaskPro> wList = CloneTool.CloneArray(wAuditList, NCRTaskPro.class);
				wList.forEach(p -> p.FormType = 2);
				wResult.Result.addAll(wList);
			}
			// ②申请单
			if (wApplyList != null && wApplyList.size() > 0) {
				List<NCRTaskPro> wList = CloneTool.CloneArray(wApplyList, NCRTaskPro.class);
				wList.forEach(p -> p.FormType = 1);
				wResult.Result.addAll(wList);
			}
			// ③排序
			if (wResult.Result.size() > 0) {
				wResult.Result.sort(Comparator.comparing(NCRTaskPro::getStatus).thenComparing(NCRTaskPro::getSubmitTime,
						Comparator.reverseOrder()));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTaskPro>> NCR_QueryTaskProList(BMSEmployee wLoginUser, int wLevel, int wCarTypeID,
			String wCarNumber, int wOrderID, int wCustomerID, int wLineID, int wStationID, int wSenderID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStatusIDList) {
		ServiceResult<List<NCRTaskPro>> wResult = new ServiceResult<List<NCRTaskPro>>();
		wResult.Result = new ArrayList<NCRTaskPro>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①申请单
			if (wLevel <= 0) {
				List<SendNCRTask> wApplyList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, null, -1, -1, "",
						wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wSenderID, wStartTime,
						wEndTime, wStatusIDList, wErrorCode);
				if (wApplyList != null && wApplyList.size() > 0) {
					List<NCRTaskPro> wList = CloneTool.CloneArray(wApplyList, NCRTaskPro.class);
					wList.forEach(p -> p.FormType = 1);
					wResult.Result.addAll(wList);
				}
			}
			// ②评审单
			List<NCRTask> wAuditList = NCRTaskDAO.getInstance().SelectList(wLoginUser, null, -1, -1, wLevel, -1, -1, "",
					wCarTypeID, wCarNumber, wOrderID, wCustomerID, wLineID, wStationID, wSenderID, wStartTime, wEndTime,
					wStatusIDList, wErrorCode);
			if (wAuditList != null && wAuditList.size() > 0) {
				List<NCRTaskPro> wList = CloneTool.CloneArray(wAuditList, NCRTaskPro.class);
				wList.forEach(p -> p.FormType = 2);
				wResult.Result.addAll(wList);
			}
			// ③剔除状态为0的单据
			wResult.Result.removeIf(p -> p.Status == 0);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> NCR_QuerySendTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID) {
		ServiceResult<List<SendNCRTask>> wResult = new ServiceResult<List<SendNCRTask>>();
		wResult.Result = new ArrayList<SendNCRTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 今日时间
			Calendar wTodaySTime = Calendar.getInstance();
			wTodaySTime.set(Calendar.HOUR_OF_DAY, 0);
			wTodaySTime.set(Calendar.MINUTE, 0);
			wTodaySTime.set(Calendar.SECOND, 0);

			Calendar wTodayETime = Calendar.getInstance();
			wTodayETime.set(Calendar.HOUR_OF_DAY, 23);
			wTodayETime.set(Calendar.MINUTE, 59);
			wTodayETime.set(Calendar.SECOND, 59);

			List<BPMTaskBase> wList = SendNCRTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
					wTodaySTime, wTodayETime, wErrorCode);

			wResult.Result = CloneTool.CloneArray(wList, SendNCRTask.class);

			wResult.Result = wResult.Result.stream().filter(p -> p.OrderID == wOrderID && p.StationID == wPartID)
					.collect(Collectors.toList());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> RRO_PartUndoSendNCRTaskListNew(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SendNCRTask>> wResultList = new ServiceResult<List<SendNCRTask>>();
		try {
			wResultList.Result = new ArrayList<SendNCRTask>();
			ServiceResult<List<SendNCRPartTaskShow>> wServiceResult = RRO_QueryPartSendNCRTaskList(wLoginUser, wOrderID,
					wStartTime, wEndTime);
			wResultList.FaultCode += wServiceResult.FaultCode;
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<SendNCRPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;
			SendNCRPartTaskShow wRROPartTaskShow = wOptional.get();

			List<Integer> wIDList = new ArrayList<Integer>();

			for (SendNCRTask wSendNCRTask : wRROPartTaskShow.UndoList) {
				if (wIDList.contains(wSendNCRTask.ID))
					continue;
				wIDList.add(wSendNCRTask.ID);
				wSendNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResultList.Result.add(wSendNCRTask);
			}

			for (SendNCRTask wSendNCRTask : wRROPartTaskShow.DoList) {
				if (wIDList.contains(wSendNCRTask.ID))
					continue;
				wIDList.add(wSendNCRTask.ID);
				wSendNCRTask.TagTypes = TaskQueryType.Handled.getValue();
				wResultList.Result.add(wSendNCRTask);
			}

			for (SendNCRTask wSendNCRTask : wRROPartTaskShow.SendList) {
				if (wIDList.contains(wSendNCRTask.ID))
					continue;
				wIDList.add(wSendNCRTask.ID);
				wSendNCRTask.TagTypes = TaskQueryType.Sended.getValue();
				wResultList.Result.add(wSendNCRTask);
			}
			// ③剔除状态为0的单据
			wResultList.Result.removeIf(p -> p.Status == 0);

			wResultList.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));
			wResultList.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<NCRTask>> RRO_PartUndoNCRTaskListNew(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<NCRTask>> wResultList = new ServiceResult<List<NCRTask>>();
		try {
			wResultList.Result = new ArrayList<NCRTask>();

			ServiceResult<List<NCRPartTaskShow>> wServiceResult = RRO_QueryPartNCRTaskList(wLoginUser, wOrderID,
					wStartTime, wEndTime);

			wResultList.FaultCode += wServiceResult.FaultCode;
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;

			Optional<NCRPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();

			if (!wOptional.isPresent())
				return wResultList;

			NCRPartTaskShow wRROPartTaskShow = wOptional.get();

			List<Integer> wIDList = new ArrayList<Integer>();
			for (NCRTask wNCRTask : wRROPartTaskShow.UndoList) {
				if (wIDList.contains(wNCRTask.ID))
					continue;
				wIDList.add(wNCRTask.ID);
				wNCRTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResultList.Result.add(wNCRTask);
			}

			for (NCRTask wNCRTask : wRROPartTaskShow.DoList) {
				if (wIDList.contains(wNCRTask.ID))
					continue;
				wIDList.add(wNCRTask.ID);
				wNCRTask.TagTypes = TaskQueryType.Handled.getValue();
				wResultList.Result.add(wNCRTask);
			}

			for (NCRTask wNCRTask : wRROPartTaskShow.SendList) {
				if (wIDList.contains(wNCRTask.ID))
					continue;
				wIDList.add(wNCRTask.ID);
				wNCRTask.TagTypes = TaskQueryType.Sended.getValue();
				wResultList.Result.add(wNCRTask);
			}

			// ③剔除状态为0的单据
			wResultList.Result.removeIf(p -> p.Status == 0);
			wResultList.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));
			wResultList.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<Integer> NCR_Inform(BMSEmployee wLoginUser, int wNCRTaskID, List<Integer> wUserIDList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			int wShiftID = MESServer.MES_QueryShiftID(0, Calendar.getInstance(), APSShiftPeriod.Day, FMCShiftLevel.Day,
					0);

			NCRTask wNCRTask = NCRTaskDAO.getInstance().SelectByID(wLoginUser, wNCRTaskID, wErrorCode);

			for (int wUserID : wUserIDList) {
				// 发送任务消息到人员
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wNCRTaskID;
				wMessage.Title = StringUtils.Format("{0}", new Object[] { wNCRTask.Code });
				wMessage.MessageText = StringUtils
						.Format("模块：{0} 发起人：{1}  发起时刻：{2} 不合格评审单知会",
								new Object[] { BPMEventModule.NCRInform.getLable(), wNCRTask.UpFlowName,
										StringUtils.parseCalendarToString(wNCRTask.CreateTime, "yyyy-MM-dd HH:mm") })
						.trim();
				wMessage.ModuleID = BPMEventModule.NCRInform.getValue();
				wMessage.ResponsorID = wUserID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Type = BFCMessageType.Notify.getValue();
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> NCR_LeaderList(BMSEmployee wLoginUser, int wPartID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<Integer> wUserIDList = new ArrayList<Integer>();

			List<LFSWorkAreaStation> wWSList = LFSServiceImpl.getInstance().LFS_QueryWorkAreaStationList(wLoginUser, -1)
					.List(LFSWorkAreaStation.class);
			if (!wWSList.stream().anyMatch(p -> p.StationID == wPartID)) {
				return wResult;
			}

			int wWorkAreaID = wWSList.stream().filter(p -> p.StationID == wPartID).findFirst().get().WorkAreaID;

			List<LFSWorkAreaChecker> wCheckerList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, wWorkAreaID).List(LFSWorkAreaChecker.class);

			if (wCheckerList == null || wCheckerList.size() <= 0) {
				return wResult;
			}

			wUserIDList = wCheckerList.get(0).LeaderIDList;
			wUserIDList.addAll(wCheckerList.get(0).ScheduleIDList);

			for (int wUserID : wUserIDList) {
				BMSEmployee wUser = WDWConstans.GetBMSEmployee(wUserID);
				if (wUser == null || wUser.ID <= 0 || wResult.Result.stream().anyMatch(p -> p.ID == wUser.ID)) {
					continue;
				}
				wResult.Result.add(wUser);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRCarInfo>> NCR_QueryTimeAllCar(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<NCRCarInfo>> wResult = new ServiceResult<List<NCRCarInfo>>();
		wResult.Result = new ArrayList<NCRCarInfo>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 获取在厂车辆
			List<OMSOrder> wOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, new ArrayList<Integer>(Arrays.asList(3, 4, 5)))
					.List(OMSOrder.class);

			// 排序
			wOrderList.sort(Comparator.comparing(OMSOrder::getPartNo));

//			List<NCRTask> wList = new ArrayList<NCRTask>();

			// 遍历查询不合格评审单
			for (OMSOrder wOMSOrder : wOrderList) {

				NCRCarInfo wInfo = NCRTaskDAO.getInstance().SelectCarInfo(wLoginUser, wOMSOrder.ProductID,
						wOMSOrder.PartNo, wErrorCode);
				if (wInfo.FQTYDone <= 0 && wInfo.FQTYToDo <= 0)
					continue;

				wInfo.PartNo = wOMSOrder.PartNo;
				wResult.Result.add(wInfo);

//				List<NCRTask> wNCRTaskList = NCRTaskDAO.getInstance().SelectListByCarInfo(wLoginUser, wOMSOrder,
//						wErrorCode);
//				for (NCRTask wNCRTask : wNCRTaskList) {
//					if (wList.stream().anyMatch(p -> p.ID == wNCRTask.ID)) {
//						continue;
//					}
//					wList.add(wNCRTask);
//				}
			}

//			List<NCRTask> wList = NCRServiceImpl.getInstance().NCR_QueryTaskList(wLoginUser, null, -1, -1, -1, -1, -1,
//					"", -1, "", -1, -1, -1, -1, -1, wStartTime, wEndTime, null).Result;
//			wList.removeIf(p -> p.Status == 0);
			// ①遍历获取车号列表
//			for (NCRTask wNCRTask : wList) {
//				String[] wStrs = wNCRTask.CarNumber.split(",");
//				for (String wStr : wStrs) {
//					if (StringUtils.isEmpty(wStr)) {
//						continue;
//					}
//					String wPartNo = StringUtils.Format("{0}#{1}", wNCRTask.CarType, wStr);
//					if (!wResult.Result.stream().anyMatch(p -> p.PartNo.equals(wPartNo))) {
//						NCRCarInfo wInfo = new NCRCarInfo(wPartNo, 0, 0);
//						wResult.Result.add(wInfo);
//					}
//				}
//			}
			// ②遍历计算数量
//			for (NCRCarInfo wNCRCarInfo : wResult.Result) {
//				String wNo = wNCRCarInfo.PartNo.split("#")[1];
//				wNCRCarInfo.FQTYDone = (int) wList.stream().filter(p -> wNCRCarInfo.PartNo.contains(p.CarType)
//						&& p.CarNumber.contains(wNo) && p.StatusText.contains("已")).count();
//				wNCRCarInfo.FQTYToDo = (int) wList.stream().filter(p -> wNCRCarInfo.PartNo.contains(p.CarType)
//						&& p.CarNumber.contains(wNo) && !p.StatusText.contains("已")).count();
//			}

			// 排序
			wResult.Result.sort(Comparator.comparing(NCRCarInfo::getPartNo));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRTask>> NCR_QueryTimeAllCarSub(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, String wCarType, String wCarNumber) {
		ServiceResult<List<NCRTask>> wResult = new ServiceResult<List<NCRTask>>();
		wResult.Result = new ArrayList<NCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			FPCProduct wProduct = WDWConstans.GetFPCProduct(wCarType);
			if (wProduct == null || wProduct.ID <= 0) {
				return wResult;
			}

			wResult.Result = NCRTaskDAO.getInstance().SelectListByCarInfo(wLoginUser, wProduct.ID, wCarNumber,
					wErrorCode);

//			List<NCRTask> wList = NCRServiceImpl.getInstance().NCR_QueryTaskList(wLoginUser, null, -1, -1, -1, -1, -1,
//					"", -1, "", -1, -1, -1, -1, -1, wStartTime, wEndTime, null).Result;
//
//			wResult.Result = wList.stream().filter(p -> p.CarType.equals(wCarType) && p.CarNumber.contains(wCarNumber))
//					.collect(Collectors.toList());

			// 排序
			wResult.Result.sort(Comparator.comparing(NCRTask::getStationID));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCPart>> NCR_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wPartID) {
		ServiceResult<List<FPCPart>> wResult = new ServiceResult<List<FPCPart>>();
		wResult.Result = new ArrayList<FPCPart>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①根据订单、工位查询wOrderNum
			int wOrderNum = SendNCRTaskDAO.getInstance().SelectOrderID(wLoginUser, wOrderID, wPartID, wErrorCode);
			if (wOrderNum <= 0) {

				// 查询订单对应所有工位
				OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
						.FPC_QueryRoutePartListByRouteID(wLoginUser, wOrder.RouteID).List(FPCRoutePart.class);
				for (FPCRoutePart fpcRoutePart : wRoutePartList) {
					FPCPart wPart = WDWConstans.GetFPCPart(fpcRoutePart.PartID);
					if (wPart != null && wPart.ID > 0 && wPart.Active == 1) {
						wResult.Result.add(wPart);
					}
				}

				return wResult;
			}
			// ②根据订单、OrderID查询工位ID集合
			List<Integer> wPartIDList = SendNCRTaskDAO.getInstance().SelectPartIDList(wLoginUser, wOrderID, wOrderNum,
					wErrorCode);
			// ③遍历获取工位集合
			for (int wItemID : wPartIDList) {
				FPCPart wPart = WDWConstans.GetFPCPart(wItemID);
				if (wPart != null && wPart.ID > 0 && wPart.Active == 1) {
					wResult.Result.add(wPart);
				}
			}

			if (wResult.Result.size() <= 0) {
				// 查询订单对应所有工位
				OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
						.FPC_QueryRoutePartListByRouteID(wLoginUser, wOrder.RouteID).List(FPCRoutePart.class);
				for (FPCRoutePart fpcRoutePart : wRoutePartList) {
					FPCPart wPart = WDWConstans.GetFPCPart(fpcRoutePart.PartID);
					if (wPart != null && wPart.ID > 0 && wPart.Active == 1) {
						wResult.Result.add(wPart);
					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> NCR_UpdateSendNCRTask(BMSEmployee wLoginUser, SendNCRTask wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SendNCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> NCR_ClearMessage(BMSEmployee wLoginUser, int wMessageID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SendNCRTaskDAO.getInstance().ClearMessage(wLoginUser, wMessageID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<NCRCarInfo>> NCR_QueryLetGoCarList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<NCRCarInfo>> wResult = new ServiceResult<List<NCRCarInfo>>();
		wResult.Result = new ArrayList<NCRCarInfo>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①时间段查询订单ID集合
			List<SendNCRTask> wList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, "", -1, -1, -1, -1,
					wStartTime, wEndTime, null, null, wErrorCode);
			wList = wList.stream().filter(p -> p.IsRelease == 1).collect(Collectors.toList());
			// ①排序
			wList.sort(Comparator.comparing(SendNCRTask::getCarNumber));
			// ②遍历获取订单
			List<Integer> wOrderIDList = wList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList());
			for (Integer wOrderID : wOrderIDList) {
				OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				if (wOrder == null || wOrder.ID <= 0) {
					continue;
				}

				wList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, "", wOrderID, -1, -1, -1, null,
						null, null, null, wErrorCode);
				wList = wList.stream().filter(p -> p.IsRelease == 1).collect(Collectors.toList());

				// ①已办数
				int wDone = (int) wList.stream().filter(p -> p.StatusText.contains("已")).count();
				int wTodo = wList.size() - wDone;

				// ③遍历获取统计数据
				NCRCarInfo wInfo = new NCRCarInfo(wOrder.PartNo, wTodo, wDone);
				wInfo.OMSOrder = wOrder;
				wInfo.OrderID = wOrder.ID;
				// ④添加到结果集
				wResult.Result.add(wInfo);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SendNCRTask>> NCR_QueryLetGoCarSubList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<SendNCRTask>> wResult = new ServiceResult<List<SendNCRTask>>();
		wResult.Result = new ArrayList<SendNCRTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, "", wOrderID, -1, -1, -1, null,
					null, null, null, wErrorCode);
			wResult.Result = wResult.Result.stream().filter(p -> p.IsRelease == 1).collect(Collectors.toList());
			// 排序
			wResult.Result.sort(Comparator.comparing(SendNCRTask::getStationID));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportLetGo(BMSEmployee wLoginUser, int wTaskID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("例外放行单{0}.pdf", wCurTime);

			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			Map<String, Object> wMap = this.GetLetGoMap(wLoginUser, wTaskID);

			String wTemplatePath = Constants.getConfigPath() + "LetGoNcr.pdf";
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			PdfUtils.pdfOut(wMap, wFileOutputStream, wTemplatePath, 2);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取例外放行单，键值对集合
	 */
	private Map<String, Object> GetLetGoMap(BMSEmployee wLoginUser, int wTaskID) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			Map<String, String> wDateMap = new HashMap<String, String>();

			SendNCRTask wSendNCRTask = (SendNCRTask) SendNCRTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wTaskID,
					"", wErrorCode);
			List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
					.BPM_GetActivitiHisTaskByPIId(wLoginUser, wSendNCRTask.FlowID).List(BPMActivitiHisTask.class);

			String wName = "";
			String wRemark = "";
			String wTime = "";

			wDateMap.put("StatusText", wSendNCRTask.StatusText);
			wDateMap.put("Code", wSendNCRTask.Code);
			wDateMap.put("UpFlowName", wSendNCRTask.UpFlowName);
			wDateMap.put("CreateTime", wSDF.format(wSendNCRTask.CreateTime.getTime()));
			wDateMap.put("SubmitTime", wSDF.format(wSendNCRTask.SubmitTime.getTime()));
			wDateMap.put("Customer", wSendNCRTask.CustomerName);
			wDateMap.put("LineName", wSendNCRTask.LineName);
			wDateMap.put("PartName", wSendNCRTask.StationName);
			wDateMap.put("PartNo", wSendNCRTask.CarType + "#" + wSendNCRTask.CarNumber);
			wDateMap.put("CloseStation", wSendNCRTask.ClosePartName);
			wDateMap.put("Steps", wSendNCRTask.StepNames);
			wDateMap.put("DescribeInfo", wSendNCRTask.DescribeInfo);
			// 主管审批
			wRemark = GetNodeValue(wLoginUser, wHisList, "SupervisorApproval", "Remark");
			wName = GetNodeName(wLoginUser, wHisList, "SupervisorApproval");
			wTime = GetNodeTime(wLoginUser, wHisList, "SupervisorApproval");
			wDateMap.put("ZGRemark", StringUtils.isEmpty(wRemark) ? "同意" : wRemark);
			wDateMap.put("ZGInfo", StringUtils.Format("{0} {1}", wName, wTime));
			// 物流采购部审批
			wRemark = GetNodeValue(wLoginUser, wHisList, "logisticsApproval", "Remark");
			wName = GetNodeName(wLoginUser, wHisList, "logisticsApproval");
			wTime = GetNodeTime(wLoginUser, wHisList, "logisticsApproval");
			wDateMap.put("WLRemark", StringUtils.isEmpty(wRemark) && StringUtils.isNotEmpty(wName) ? "同意" : wRemark);
			wDateMap.put("WLInfo", StringUtils.Format("{0} {1}", wName, wTime));
			// 生产管理室审批
			wRemark = GetNodeValue(wLoginUser, wHisList, "ProductionApproval", "Remark");
			wName = GetNodeName(wLoginUser, wHisList, "ProductionApproval");
			wTime = GetNodeTime(wLoginUser, wHisList, "ProductionApproval");
			wDateMap.put("SCRemark", StringUtils.isEmpty(wRemark) ? "同意" : wRemark);
			wDateMap.put("SCInfo", StringUtils.Format("{0} {1}", wName, wTime));
			// 工艺师审批
			wRemark = GetNodeValue(wLoginUser, wHisList, "CraftApproval", "Remark");
			wName = GetNodeName(wLoginUser, wHisList, "CraftApproval");
			wTime = GetNodeTime(wLoginUser, wHisList, "CraftApproval");
			wDateMap.put("GYSRemark", StringUtils.isEmpty(wRemark) ? "同意" : wRemark);
			wDateMap.put("GYSInfo", StringUtils.Format("{0} {1}", wName, wTime));
			// 质量管理部审批
			wRemark = GetNodeValue(wLoginUser, wHisList, "QualityApproval", "Remark");
			wName = GetNodeName(wLoginUser, wHisList, "QualityApproval");
			wTime = GetNodeTime(wLoginUser, wHisList, "QualityApproval");
			wDateMap.put("ZLRemark", StringUtils.isEmpty(wRemark) ? "同意" : wRemark);
			wDateMap.put("ZLInfo", StringUtils.Format("{0} {1}", wName, wTime));

			wResult.put("datemap", wDateMap);
			wResult.put("imgmap", new HashMap<String, String>());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> NCR_ExportLetGoList(BMSEmployee wLoginUser) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			MyExcelSheet wMyExcelSheet = GetMyExcelSheet_LetGoList(wLoginUser);

			List<MyExcelSheet> wMyExcelSheetList = new ArrayList<MyExcelSheet>(Arrays.asList(wMyExcelSheet));

			wResult.Result = ExcelUtil.ExportData(wMyExcelSheetList, "例外放行清单");

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private MyExcelSheet GetMyExcelSheet_LetGoList(BMSEmployee wLoginUser) {
		MyExcelSheet wResult = new MyExcelSheet();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.TitleName = "例外放行单";
			wResult.SheetName = "例外放行单";
			wResult.HeaderList = new ArrayList<String>(Arrays.asList("序号", "单据编号", "局段", "修程", "车号", "工位", "例外放行工序",
					"关闭工位", "不合格描述", "申请人", "申请时间", "状态", "提交不合格评审申请", "主管审批", "生产管理室审批", "工艺师填写", "质量管理部审批"));
			wResult.DataList = new ArrayList<List<String>>();

			List<SendNCRTask> wList = SendNCRTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, "", -1, -1, -1, -1,
					null, null, null, new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);
			wList = wList.stream().filter(p -> p.IsRelease == 1).collect(Collectors.toList());
			// 排序
			wList.sort(Comparator.comparing(SendNCRTask::getCreateTime));

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int wIndex = 1;
			for (SendNCRTask wSendNCRTask : wList) {
				List<String> wVList = new ArrayList<String>();
				wVList.add(String.valueOf(wIndex));
				wVList.add(wSendNCRTask.Code);
				wVList.add(wSendNCRTask.CustomerName);
				wVList.add(wSendNCRTask.LineName);
				wVList.add(wSendNCRTask.CarType + "#" + wSendNCRTask.CarNumber);
				wVList.add(wSendNCRTask.StationName);
				wVList.add(wSendNCRTask.StepNames);
				wVList.add(wSendNCRTask.ClosePartName);
				wVList.add(wSendNCRTask.DescribeInfo);
				wVList.add(wSendNCRTask.UpFlowName);
				wVList.add(wSDF.format(wSendNCRTask.CreateTime.getTime()));
				wVList.add(wSendNCRTask.StatusText);

				List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
						.BPM_GetActivitiHisTaskByPIId(wLoginUser, wSendNCRTask.FlowID).List(BPMActivitiHisTask.class);
				// 提交不合格评审申请
				String wRemark = GetNodeValue(wLoginUser, wHisList, "submitApply", "Remark");
				String wName = GetNodeName(wLoginUser, wHisList, "submitApply");
				String wTime = GetNodeTime(wLoginUser, wHisList, "submitApply");
				String wValue = GetValue(wRemark, wName, wTime);
				wVList.add(wValue);
				// 主管审批
				wRemark = GetNodeValue(wLoginUser, wHisList, "SupervisorApproval", "Remark");
				wName = GetNodeName(wLoginUser, wHisList, "SupervisorApproval");
				wTime = GetNodeTime(wLoginUser, wHisList, "SupervisorApproval");
				wValue = GetValue(wRemark, wName, wTime);
				wVList.add(wValue);
				// 生产管理室审批
				wRemark = GetNodeValue(wLoginUser, wHisList, "ProductionApproval", "Remark");
				wName = GetNodeName(wLoginUser, wHisList, "ProductionApproval");
				wTime = GetNodeTime(wLoginUser, wHisList, "ProductionApproval");
				wValue = GetValue(wRemark, wName, wTime);
				wVList.add(wValue);
				// 工艺师填写
				wRemark = GetNodeValue(wLoginUser, wHisList, "CraftApproval", "Remark");
				wName = GetNodeName(wLoginUser, wHisList, "CraftApproval");
				wTime = GetNodeTime(wLoginUser, wHisList, "CraftApproval");
				wValue = GetValue(wRemark, wName, wTime);
				wVList.add(wValue);
				// 质量管理部审批
				wRemark = GetNodeValue(wLoginUser, wHisList, "QualityApproval", "Remark");
				wName = GetNodeName(wLoginUser, wHisList, "QualityApproval");
				wTime = GetNodeTime(wLoginUser, wHisList, "QualityApproval");
				wValue = GetValue(wRemark, wName, wTime);
				wVList.add(wValue);
				wResult.DataList.add(wVList);
				wIndex++;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private String GetValue(String wRemark, String wName, String wTime) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(wName)) {
				return wResult;
			}

			wResult = StringUtils.Format("【{0}】-【{1}】-【{2}】", wName, wTime, wRemark);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
