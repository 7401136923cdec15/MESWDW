package com.mes.ncr.server.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
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

import com.mes.ncr.server.service.RROService;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.OMSOrderStatus;
import com.mes.ncr.server.service.mesenum.TaskQueryType;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fmc.FMCLineUnit;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCPartPoint;
import com.mes.ncr.server.service.po.fpc.FPCPartTypes;
import com.mes.ncr.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.UserWorkArea;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.rro.RROFrequency;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RROItemTaskShow;
import com.mes.ncr.server.service.po.rro.RROPart;
import com.mes.ncr.server.service.po.rro.RROPartNo;
import com.mes.ncr.server.service.po.rro.RROPartTaskShow;
import com.mes.ncr.server.service.po.rro.RRORepairStatus;
import com.mes.ncr.server.service.po.rro.RRORepairTable;
import com.mes.ncr.server.service.po.rro.RROSeleteType;
import com.mes.ncr.server.service.po.rro.RROStatus;
import com.mes.ncr.server.service.po.rro.RROTableBody;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.po.rro.RROTaskTypes;
import com.mes.ncr.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.ncr.server.service.po.rsm.RSMTurnOrderTaskStatus;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.rro.RRORepairItemDAO;
import com.mes.ncr.server.serviceimpl.dao.rro.RRORepairTableDAO;
import com.mes.ncr.server.serviceimpl.dao.rro.RROTaskDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;
import com.mes.ncr.server.serviceimpl.utils.wdw.NewCreditReportUtil;
import com.mes.ncr.server.utils.Constants;

@Service
public class RROServiceImpl implements RROService {

	private static Logger logger = LoggerFactory.getLogger(RROServiceImpl.class);

	public RROServiceImpl() {
	}

	private static RROService Instance;

	public static RROService getInstance() {
		if (Instance == null)
			Instance = new RROServiceImpl();
		return Instance;
	}

	@Override
	public ServiceResult<List<RROTask>> RRO_QueryTaskList(BMSEmployee wLoginUser, List<Integer> wIDList,
			int wIsDelivery, String wCarNumber, int wCarTypeID, int wSenderID, int wStationID, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wLineID, int wCustomerID) {
		ServiceResult<List<RROTask>> wResult = new ServiceResult<List<RROTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = RROTaskDAO.getInstance().SelectList(wLoginUser, wIDList, wIsDelivery, wCarNumber,
					wCarTypeID, wSenderID, wStationID, wOrderID, wStartTime, wEndTime, wLineID, wCustomerID,
					wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, int wOrderID, int wLineID,
			int wCustomerID, int wProductID, String wCarNumber, int wIsDelivery, int wPartID, Calendar wStartTime,
			Calendar wEndTime, int wStatus) {
		ServiceResult<List<RROItemTask>> wResult = new ServiceResult<List<RROItemTask>>();
		wResult.Result = new ArrayList<RROItemTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (wStatus) {
			case 1:
				wResult.Result
						.addAll(RRORepairItemDAO.getInstance()
								.SelectList(wLoginUser, -1, wOrderID, wLineID, wCustomerID, wProductID, wCarNumber,
										wIsDelivery, wPartID, -1, wStartTime, wEndTime,
										StringUtils.parseListArgs(RROStatus.Confirmed.getValue(),
												RROStatus.Done.getValue(), RROStatus.Confirm.getValue()),
										null, wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(RRORepairItemDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
						wCustomerID, wProductID, wCarNumber, wIsDelivery, wPartID, -1, wStartTime, wEndTime, null,
						StringUtils.parseListArgs(RROStatus.Done.getValue(), RROStatus.Confirm.getValue(),
								RROStatus.Confirmed.getValue(), RROStatus.Cancle.getValue(),
								RROStatus.Default.getValue()),
						wErrorCode));
				break;
			default:
				wResult.Result.addAll(RRORepairItemDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wLineID,
						wCustomerID, wProductID, wCarNumber, wIsDelivery, wPartID, -1, wStartTime, wEndTime, null, null,
						wErrorCode));

			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			wResult.Result.removeIf(p -> p.Status <= 0);

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = RRORepairItemDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
					wErrorCode);

			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {

				if (wTaskBase instanceof RROItemTask) {
					RROItemTask wRROTask = (RROItemTask) wTaskBase;
					wRROTask.TagTypes = TaskQueryType.ToHandle.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wRROTask.ID)
							wResult.Result.set(i, wRROTask);
					}
				}
			}

			// ??????????????????????????????????????????????????????
//			wResult.Result = RRORepairItemDAO.getInstance().FilterByPosition(wLoginUser, wResult.Result);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RROTask> RRO_SaveTask(BMSEmployee wLoginUser, RROTask wTask) {
		ServiceResult<RROTask> wResult = new ServiceResult<RROTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wTask.ID = RROTaskDAO.getInstance().Update(wLoginUser, wTask, wErrorCode);
			wResult.setResult(wTask);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RROTask> RRO_QueryTaskByID(BMSEmployee wLoginUser, int wID) {
		ServiceResult<RROTask> wResult = new ServiceResult<RROTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = RROTaskDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> RRO_QueryApplicantTaskList(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResultList = new ServiceResult<List<BPMTaskBase>>();
		wResultList.Result = new ArrayList<BPMTaskBase>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();

//			List<BPMTaskBase> wRRORepairItemList = new ArrayList<BPMTaskBase>();
			// ????????????ID??????
//			List<Integer> wAllTaskID = new ArrayList<Integer>();
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// ??????
				wResultList.Result = RRORepairItemDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}
			if (wResultList.Result != null && wResultList.Result.size() > 0)
				wResultList.Result = wResultList.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
								ArrayList::new));
//			// ??????????????????????????????????????????????????????
//			if (wRRORepairItemList != null && wRRORepairItemList.size() > 0) {
//				for (BPMTaskBase wItem : wRRORepairItemList) {
//					if (wAllTaskID.contains(((RROItemTask) wItem).TaskID))
//						continue;
//					wAllTaskID.add(((RROItemTask) wItem).TaskID);
//				}
//			}
//			if (wAllTaskID != null && wAllTaskID.size() > 0) {
//				Calendar wCalendar = Calendar.getInstance();
//				wCalendar.set(2000, 0, 1);
//				// ????????????ID??????
//				wResult.Result = RROTaskDAO.getInstance().SelectList(wLoginUser, wAllTaskID, -1, "", -1, -1, -1, -1,
//						wCalendar, wCalendar, -1, -1, wErrorCode);
//				wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
//				// ??????
//				if (wResult.Result != null && wResult.Result.size() > 0)
//					wResult.Result = wResult.Result.stream()
//							.collect(Collectors.collectingAndThen(
//									Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RROTask::getID))),
//									ArrayList::new));
//			}
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<RROItemTaskShow>> RRO_QueryRROItemTaskShowList(BMSEmployee wLoginUser, int wIsSend,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROItemTaskShow>> wResultList = new ServiceResult<List<RROItemTaskShow>>();
		wResultList.Result = new ArrayList<RROItemTaskShow>();
		try {
			List<BPMTaskBase> wBPMTaskBaseList = new ArrayList<BPMTaskBase>();

			List<BPMTaskBase> wToDoTaskBaseList = new ArrayList<BPMTaskBase>();

			List<BPMTaskBase> wDoneTaskBaseList = new ArrayList<BPMTaskBase>();

			List<BPMTaskBase> wSendTaskBaseList = new ArrayList<BPMTaskBase>();

			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			if (wIsSend == 1) {
				wSendTaskBaseList = RRORepairItemDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				wBPMTaskBaseList = wSendTaskBaseList;
			} else {
				wDoneTaskBaseList = RRORepairItemDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);

				wToDoTaskBaseList = RRORepairItemDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);

				wSendTaskBaseList = RRORepairItemDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);

				wBPMTaskBaseList.addAll(wToDoTaskBaseList);
				wBPMTaskBaseList.addAll(wDoneTaskBaseList);
				wBPMTaskBaseList.addAll(wSendTaskBaseList);
			}

			// ??????
			wBPMTaskBaseList = wBPMTaskBaseList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
							ArrayList::new));
			wBPMTaskBaseList = wBPMTaskBaseList.stream().filter(p -> p.Status != NCRStatus.Default.getValue())
					.collect(Collectors.toList());

			List<RROItemTask> wAllNCRTaskList = CloneTool.CloneArray(wBPMTaskBaseList, RROItemTask.class);
			List<RROItemTask> wDoneTaskList = CloneTool.CloneArray(wDoneTaskBaseList, RROItemTask.class);
			List<RROItemTask> wTodoTaskList = CloneTool.CloneArray(wToDoTaskBaseList, RROItemTask.class);
			List<RROItemTask> wSendTaskList = CloneTool.CloneArray(wSendTaskBaseList, RROItemTask.class);

			for (RROItemTask wSendNCRTask : wAllNCRTaskList) {
				if (wResultList.Result.stream().anyMatch(p -> p.OrderID == wSendNCRTask.OrderID)) {
					continue;
				} else {
					RROItemTaskShow wTaskShow = new RROItemTaskShow();

					wTaskShow.OrderID = wSendNCRTask.OrderID;
					wTaskShow.PartNo = StringUtils.Format("{0}#{1}", wSendNCRTask.CarTypeName, wSendNCRTask.CarNumber);
					wTaskShow.DoList = wDoneTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
							.collect(Collectors.toList());
					if (wIsSend == 1) {
						wTaskShow.DoList = wAllNCRTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
								.collect(Collectors.toList());
					}

					wTaskShow.UndoList = wTodoTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
							.collect(Collectors.toList());
					wTaskShow.SendList = wSendTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
							.collect(Collectors.toList());

					wTaskShow.StationNum = (int) wAllNCRTaskList.stream().filter(p -> p.OrderID == wSendNCRTask.OrderID)
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
	public ServiceResult<List<RROItemTask>> RRO_ItemTaskList(BMSEmployee wLoginUser, int wOrderID, int wType,
			int wIsSend, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROItemTask>> wResultList = new ServiceResult<List<RROItemTask>>();
		try {
			ServiceResult<List<RROItemTaskShow>> wServiceResultList = RROServiceImpl.getInstance()
					.RRO_QueryRROItemTaskShowList(wLoginUser, wIsSend, wStartTime, wEndTime);

			if (wServiceResultList.Result != null && wServiceResultList.Result.size() > 0) {
				RROItemTaskShow wNCRTaskShow = wServiceResultList.Result.stream().filter(p -> p.OrderID == wOrderID)
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
	public ServiceResult<List<BPMTaskBase>> RRO_QueryTaskListByTagTypes(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResultList = new ServiceResult<List<BPMTaskBase>>();
		wResultList.Result = new ArrayList<BPMTaskBase>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2000, 0, 1);
			// ????????????ID??????
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Approver:// ??????
			case Confirmer:// ??????
				// ?????????????????????????????????
				// --??????--
				// ????????????
				wResultList.Result = RRORepairItemDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// ??????
				// ??????????????????????????????????????????
				// --??????--
				// ??????????????????????????????

				wResultList.Result = RRORepairItemDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);

				break;
			default:
				break;
			}
			if (wResultList.Result != null && wResultList.Result.size() > 0) {
				// ??????
				if (wResultList.Result != null && wResultList.Result.size() > 0)
					wResultList.Result = wResultList.Result.stream()
							.collect(Collectors.collectingAndThen(
									Collectors.toCollection(
											() -> new TreeSet<>(Comparator.comparing(BPMTaskBase::getID))),
									ArrayList::new));
			}
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ?????????ID??????
	 * 
	 * @param wRROItemTask
	 */
	@Override
	public ServiceResult<RROItemTask> RRO_QueryItemTaskByID(BMSEmployee wLoginUser, int wID) {
		ServiceResult<RROItemTask> wResult = new ServiceResult<RROItemTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = (RROItemTask) RRORepairItemDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "",
					wErrorCode);
			// RRORepairItemDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RRORepairTable> RRO_SaveRRORepairTable(BMSEmployee wLoginUser, RRORepairTable wTask) {
		ServiceResult<RRORepairTable> wResult = new ServiceResult<RRORepairTable>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wTask.ID = RRORepairTableDAO.getInstance().Update(wLoginUser, wTask, wErrorCode);
			wResult.setResult(wTask);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RRORepairTable>> RRO_QueryTableList(BMSEmployee wLoginUser, List<Integer> wIDList,
			int wCarTypeID, String wCarNumber, int wLineID, int wType, int wCustomerID, int wSenderID, int wApprovalID,
			int wStatus, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RRORepairTable>> wResultList = new ServiceResult<List<RRORepairTable>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = RRORepairTableDAO.getInstance().SelectList(wLoginUser, wIDList, wCarTypeID, wCarNumber,
					wLineID, wType, wCustomerID, wSenderID, wApprovalID, wStatus, wStartTime, wEndTime, wErrorCode);
			wResultList.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResultList.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<RROTableBody>> RRO_QueryBodyByType(BMSEmployee wLoginUser, int wCarTypeID, String wPartNo,
			int wType) {
		ServiceResult<List<RROTableBody>> wResultList = new ServiceResult<List<RROTableBody>>();
		try {
			wResultList.Result = RRORepairTableDAO.getInstance().QueryBody(wLoginUser, wCarTypeID, wPartNo, wType);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<RROTableBody>> RRO_QueryBodyList(BMSEmployee wLoginUser, RROTask wRROTask) {
		ServiceResult<List<RROTableBody>> wResultList = new ServiceResult<List<RROTableBody>>();
		try {
			wResultList.Result = new ArrayList<RROTableBody>();
			if (wRROTask.RepairItemList != null && wRROTask.RepairItemList.size() > 0) {
				for (RROItemTask wRROItemTask : wRROTask.RepairItemList) {
					RROTableBody wRROTableBody = new RROTableBody();
					wRROTableBody.Describe = wRROItemTask.Content;
					wRROTableBody.WorkAreaName = wRROItemTask.WorkAreaName;
					wRROTableBody.TeamName = StringUtils.Format("{0}/{1}", wRROItemTask.TeamName,
							wRROItemTask.StationName);
					wRROTableBody.PersonLiable = wRROItemTask.OperatorName;
//					wRROTableBody.InspectorName = wRROTask.SenderName;
//					wRROTableBody.Date = StringUtils.parseCalendarToString(wRROTask.ConfirmTaskTime,
//							wRROTableBody.Date);
					wRROTableBody.ProcessName = wRROItemTask.ProcesName;
					wRROTableBody.Signature = wRROItemTask.OperatorName;
					wRROTableBody.Remark = wRROItemTask.Remark;
					wRROTableBody.Result = wRROItemTask.IsStatus;// 1?????????2????????????
					wResultList.Result.add(wRROTableBody);
				}
			}
		} catch (Exception e) {
			wResultList.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<Map<String, Integer>> RRO_SelectItemTaskList(BMSEmployee wLoginUser, int wCarType, int wLineID,
			int wPartID, int wLimit) {
		ServiceResult<Map<String, Integer>> wResultList = new ServiceResult<Map<String, Integer>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = RROTaskDAO.getInstance().SelectItemTaskList(wLoginUser, wCarType, wLineID, wPartID,
					wLimit, wErrorCode);
			wResultList.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResultList.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<RROFrequency>> RRO_SelectFrequency(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<RROFrequency>> wResultList = new ServiceResult<List<RROFrequency>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = RROTaskDAO.getInstance().SelectFrequency(wLoginUser, wStartTime, wEndTime, wErrorCode);

			wResultList.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResultList.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, List<Integer> wIDList,
			int wFlowType, int wUpFlowID, int wTaskID, List<Integer> wStatusList, int wWorkAreaID, int wNCRID,
			int wSpecialTaskID, int wOrderID, int wPartID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROItemTask>> wResultList = new ServiceResult<List<RROItemTask>>();
		try {
			wResultList.Result = new ArrayList<RROItemTask>();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = RRORepairItemDAO.getInstance().SelectList(wLoginUser, wIDList, wFlowType, wUpFlowID,
					wTaskID, wStatusList, wWorkAreaID, wNCRID, wSpecialTaskID, wOrderID, wPartID, wStartTime, wEndTime,
					-1, wErrorCode);
			wResultList.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResultList.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public ServiceResult<RROItemTask> RRO_CreateItemTask(BMSEmployee wBMSEmployee, BPMEventModule wModule) {
		ServiceResult<RROItemTask> wResult = new ServiceResult<RROItemTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = RRORepairItemDAO.getInstance().RRO_CreateItemTask(wBMSEmployee, wModule, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public synchronized ServiceResult<RROItemTask> RRO_QueryDefaultTask(BMSEmployee wLoginUser, int wModuleID) {
		ServiceResult<RROItemTask> wResult = new ServiceResult<RROItemTask>();
		try {
			wResult.Result = new RROItemTask();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<RROItemTask> wRROItemTaskList = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, wModuleID,
					wLoginUser.getID(), -1, StringUtils.parseList(new Integer[] { RRORepairStatus.Default.getValue() }),
					-1, -1, -1, -1, -1, null, null, -1, wErrorCode);
			if (wRROItemTaskList != null && wRROItemTaskList.size() > 0) {
				wResult.Result = wRROItemTaskList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				// ??????????????????????????????????????????????????????????????????????????????
				List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
				if (wLFSWorkAreaCheckerList != null && wLFSWorkAreaCheckerList.size() > 0) {
					for (LFSWorkAreaChecker wItem : wLFSWorkAreaCheckerList) {
						if (wItem.CheckerIDList != null && wItem.CheckerIDList.contains(wLoginUser.ID)) {
							wResult.Result.FollowerID.addAll(wItem.CheckerIDList);
						}
					}
				}
				// ???????????????????????????ID??????????????????????????????????????????
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
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> RRO_SetItemTaskCode(BMSEmployee wLoginUser, RROItemTask wRROItemTask) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
//			RRORepairItemDAO.getInstance().RRO_SetItemTaskCode(wLoginUser, wRROItemTask, wErrorCode);
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RROItemTask> RRO_UpdateItemTask(BMSEmployee wLoginUser, RROItemTask wRROItemTask) {
		ServiceResult<RROItemTask> wResult = new ServiceResult<RROItemTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wRROItemTask != null && wRROItemTask.ID > 0 && wRROItemTask.TaskID <= 0
					&& wRROItemTask.Status != RROStatus.Default.getValue()) {
				// ??????????????????????????????????????????????????????
//				RRORepairItemDAO.getInstance().RRO_SetItemTaskCode(wLoginUser, wRROItemTask, wErrorCode);
				wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
			}
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			if (wRROItemTask.IsSendNCR == 1) {
				RRORepairItemDAO.getInstance().RRO_TaskSendNCR(wLoginUser, wRROItemTask, wErrorCode);

				wRROItemTask.Status = RRORepairStatus.Confirmed.getValue();
				wRROItemTask.StatusText = RRORepairStatus.Confirmed.getLable();
			}

			// ??????????????????????????????
			if (wRROItemTask.FlowType == 3008 && wRROItemTask.Status == 2) {
				RROTask wInfo = RROTaskDAO.getInstance().SelectByID(wLoginUser, wRROItemTask.TaskID, wErrorCode);
				if (wInfo.ID > 0) {
					wInfo.StationID = wRROItemTask.StationID;
					RROTaskDAO.getInstance().Update(wLoginUser, wInfo, wErrorCode);
				}
			} else if (wRROItemTask.FlowType == 1018 && wRROItemTask.Status == 4) {
				RROTask wInfo = RROTaskDAO.getInstance().SelectByID(wLoginUser, wRROItemTask.TaskID, wErrorCode);
				if (wInfo.ID > 0) {
					wInfo.StationID = wRROItemTask.StationID;
					RROTaskDAO.getInstance().Update(wLoginUser, wInfo, wErrorCode);
				}
			} else if (wRROItemTask.FlowType == 5010 && wRROItemTask.Status == 4) {
				RROTask wInfo = RROTaskDAO.getInstance().SelectByID(wLoginUser, wRROItemTask.TaskID, wErrorCode);
				if (wInfo.ID > 0) {
					wInfo.StationID = wRROItemTask.StationID;
					RROTaskDAO.getInstance().Update(wLoginUser, wInfo, wErrorCode);
				}
			}

			RRORepairItemDAO.getInstance().BPM_UpdateTask(wLoginUser, wRROItemTask, wErrorCode);
			wResult.setResult(wRROItemTask);
			wResult.Result.ID = wRROItemTask.ID;
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> RRO_QueryTaskListByTagType(BMSEmployee wLoginUser, int wTagType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			switch (TagTypes.getEnumType(wTagType)) {
			case Applicant:
				// ??????
				wResult.Result = RRORepairItemDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// ??????
				// ??????????????????????????????????????????
				// --??????--
				wResult.Result = RRORepairItemDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);
			case Confirmer:// ??????
			case Approver:// ??????
				// ??????
				wResult.Result = RRORepairItemDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RROItemTask> RRO_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode) {
		ServiceResult<RROItemTask> wResult = new ServiceResult<RROItemTask>();
		wResult.Result = new RROItemTask();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = (RROItemTask) RRORepairItemDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wTaskID, wCode,
					wErrorCode);
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public ServiceResult<Map<Integer, String>> RRO_QuerySendType(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<Map<Integer, String>> wResultList = new ServiceResult<Map<Integer, String>>();
		try {
			wResultList.Result = new HashMap<Integer, String>();
//			wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
//			wResultList.Result.put(RROTaskTypes.Delivery.getValue(), RROTaskTypes.Delivery.getLable());
//			wResultList.Result.put(RROTaskTypes.PilotRun.getValue(), RROTaskTypes.PilotRun.getLable());

			// 1????????????????????????
			List<RSMTurnOrderTask> wRSMTurnOrderTaskList = QMSServiceImpl.getInstance()
					.RSM_QueryTurnOrderTaskList(wLoginUser, wOrderID, -1, -1, null).List(RSMTurnOrderTask.class);
			if (wRSMTurnOrderTaskList == null || wRSMTurnOrderTaskList.size() <= 0) {

				wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
				return wResultList;
			}
			// 2??????????????????????????????????????????????????????????????????(??????????????????????????????)

			// 3??? ???????????????????????????????????????(???????????????????????????????????????)
			List<FPCPart> wFPCPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(BaseDAO.SysAdmin, 0, 0, 0)
					.List(FPCPart.class);
			FPCPart wFPCPart = new FPCPart();
			Optional<FPCPart> wOptional = wFPCPartList.stream()
					.filter(p -> p.PartType == FPCPartTypes.QTFinally.getValue()).findFirst();
			if (wOptional != null && wOptional.isPresent())
				wFPCPart = wOptional.get();
			if (wFPCPart.QTPartID < 0) {
				wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
				return wResultList;
			}

			FPCPart wZJFPCPart = WDWConstans.GetFPCPart(wFPCPart.QTPartID);
			for (RSMTurnOrderTask wRSMTurnOrderTask : wRSMTurnOrderTaskList) {
				if (wZJFPCPart.ID == wRSMTurnOrderTask.TargetStationID
						&& wRSMTurnOrderTask.Status == RSMTurnOrderTaskStatus.Passed.getValue()) {
					wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
					wResultList.Result.put(RROTaskTypes.Delivery.getValue(), RROTaskTypes.Delivery.getLable());
					wResultList.Result.put(RROTaskTypes.PilotRun.getValue(), RROTaskTypes.PilotRun.getLable());
					return wResultList;
				}
			}

			if (wResultList.Result.size() <= 0) {
				wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ????????????????????????
	 */
	@SuppressWarnings("unused")
	@Override
	public ServiceResult<List<RROSeleteType>> RRO_QueryAllSendType(BMSEmployee wLoginUser) {
		ServiceResult<List<RROSeleteType>> wResultList = new ServiceResult<List<RROSeleteType>>();
		try {
			wResultList.Result = new ArrayList<RROSeleteType>();
			// ????????????????????????
			List<Integer> wStatusList = new ArrayList<Integer>();
			wStatusList.add(OMSOrderStatus.EnterFactoryed.getValue());
			wStatusList.add(OMSOrderStatus.Repairing.getValue());
			wStatusList.add(OMSOrderStatus.FinishedWork.getValue());
			wStatusList.add(OMSOrderStatus.ToOutChcek.getValue());
			wStatusList.add(OMSOrderStatus.ToOutConfirm.getValue());
			List<OMSOrder> wOMSOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, wStatusList).List(OMSOrder.class);
			if (wOMSOrderList == null || wOMSOrderList.size() <= 0)
				return wResultList;
			List<Integer> wIDList = new ArrayList<Integer>();
			for (OMSOrder wOMSOrder : wOMSOrderList) {
				wIDList.add(wOMSOrder.ID);
			}
			List<RSMTurnOrderTask> wRSMTurnOrderTaskList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryTurnOrderListByIDList(wLoginUser, wIDList).List(RSMTurnOrderTask.class);

//			wResultList.Result.put(RROTaskTypes.Plication.getValue(), RROTaskTypes.Plication.getLable());
//			wResultList.Result.put(RROTaskTypes.Delivery.getValue(), RROTaskTypes.Delivery.getLable());
//			wResultList.Result.put(RROTaskTypes.PilotRun.getValue(), RROTaskTypes.PilotRun.getLable());
//			List<RSMTurnOrderTask> wRSMTurnOrderTaskList = QMSServiceImpl.getInstance()
//					.RSM_QueryTurnOrderTaskList(wLoginUser, wOrderID, -1, -1, null).List(RSMTurnOrderTask.class);
//			if (wRSMTurnOrderTaskList == null || wRSMTurnOrderTaskList.size() <= 0) {
//				wResultList.Result.put(RROTaskTypes.Plication.getValue(),RROTaskTypes.Plication.getLable());
//				return wResultList;
//			}

//			for (RSMTurnOrderTask wRSMTurnOrderTask : wRSMTurnOrderTaskList) {
//				FPCPart wFPCPart = WDWConstans.GetFPCPart(wRSMTurnOrderTask.TargetStationID);
//				if (wFPCPart != null && wFPCPart.PartType == FPCPartTypes.QTFinally.getValue()
//						&& wRSMTurnOrderTask.Status == RSMTurnOrderTaskStatus.Passed.getValue()) {
//					wResultList.Result.put(RROTaskTypes.Plication.getValue(),RROTaskTypes.Plication.getLable());
//					wResultList.Result.put(RROTaskTypes.Delivery.getValue(),RROTaskTypes.Delivery.getLable());
//					wResultList.Result.put(RROTaskTypes.PilotRun.getValue(),RROTaskTypes.PilotRun.getLable());
//					return wResultList;
//				}
//			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ??????ID??????ID????????????????????????????????????????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param OrderID
	 * @param wPartID
	 * @return
	 */
	@Override
	public ServiceResult<Boolean> RRO_JugdeItemClose(BMSEmployee wLoginUser, int OrderID, int wPartID) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = true;
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<RROItemTask> wRROItemTaskList = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1,
					null, -1, -1, -1, OrderID, wPartID, null, null, -1, wErrorCode);
			if (wRROItemTaskList == null || wRROItemTaskList.size() <= 0) {
				return wResult;
			}

			wRROItemTaskList.removeIf(p -> p.Status == 21 || p.Status == 22);

			for (RROItemTask wItem : wRROItemTaskList) {
				if (wItem.Status != RRORepairStatus.Confirmed.getValue()) {
					wResult.Result = false;
					return wResult;
				}
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROPartTaskShow>> RRO_QueryPartTaskList(BMSEmployee wLoginUser, int wOrderID, int wIsSend,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROPartTaskShow>> wResultList = new ServiceResult<List<RROPartTaskShow>>();
		try {
			wResultList.Result = new ArrayList<RROPartTaskShow>();
			ServiceResult<List<RROItemTaskShow>> wTaskShowList = this.RRO_QueryRROItemTaskShowList(wLoginUser, wIsSend,
					wStartTime, wEndTime);

			if (wTaskShowList.Result == null || wTaskShowList.Result.size() <= 0)
				return wResultList;
			Optional<RROItemTaskShow> wOptional = wTaskShowList.Result.stream().filter(p -> p.OrderID == wOrderID)
					.findFirst();
			if (!wOptional.isPresent())
				return wResultList;
			RROItemTaskShow wRROItemTaskShow = wOptional.get();

			List<RROItemTask> wAllList = new ArrayList<RROItemTask>();
			wAllList.addAll(wRROItemTaskShow.DoList);
			wAllList.addAll(wRROItemTaskShow.UndoList);
			wAllList.addAll(wRROItemTaskShow.SendList);

			for (RROItemTask wRROItemTask : wAllList) {
				if (wResultList.Result.stream().anyMatch(p -> p.PartID == wRROItemTask.StationID)) {
					continue;
				} else {
					RROPartTaskShow wTaskShow = new RROPartTaskShow();
					wTaskShow.OrderID = wRROItemTask.OrderID;
					wTaskShow.PartNo = StringUtils.Format("{0}#{1}", wRROItemTask.CarTypeName, wRROItemTask.CarNumber);
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

	@Override
	public ServiceResult<List<RROItemTask>> RRO_PartUndoTaskList(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wType, int wIsSend, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROItemTask>> wResultList = new ServiceResult<List<RROItemTask>>();
		try {
			ServiceResult<List<RROPartTaskShow>> wServiceResult = RRO_QueryPartTaskList(wLoginUser, wOrderID, wIsSend,
					wStartTime, wEndTime);
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<RROPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();

			if (!wOptional.isPresent())
				return wResultList;

			RROPartTaskShow wRROPartTaskShow = wOptional.get();

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
	public ServiceResult<List<RROItemTask>> RRO_QueryNotFinishItemList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<RROItemTask>> wResultList = new ServiceResult<List<RROItemTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResultList.Result = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1, null, -1, -1,
					-1, wOrderID, -1, null, null, -1, wErrorCode);
			if (wResultList.Result != null && wResultList.Result.size() > 0) {
				wResultList.Result = wResultList.Result.stream()
						.filter(p -> p.Status != RRORepairStatus.Confirmed.getValue()).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<String> BatchExportPdf(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("????????????????????????{0}.pdf", new Object[] { wCurTime, });

			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			Map<String, String> wMapValue = GetMapValue(wLoginUser, wOrderID);
			List<List<String>> wValueList = GetValueList(wLoginUser, wOrderID);
			NewCreditReportUtil.outputBatchRepairPdf(wMapValue, wValueList, wFileOutputStream);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<List<String>> GetValueList(BMSEmployee wLoginUser, int wOrderID) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.add(new ArrayList<String>(Arrays.asList("??????", "??????????????????", "????????????", "????????????(??????)", "??????", "?????????", "????????????",
					"????????????", "?????????(??????)", "??????", "??????(??????????????????)")));

			List<RROItemTask> wList = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1, null, -1,
					-1, -1, wOrderID, -1, null, null, -1, wErrorCode);

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");
			int wIndex = 1;
			for (RROItemTask wRROItemTask : wList) {
				wResult.add(new ArrayList<String>(Arrays.asList(StringUtils.Format("{0}.", wIndex),
						StringUtils.Format("???{0}???{1}", wRROItemTask.ItemLogo, wRROItemTask.Content),
						wRROItemTask.WorkAreaName, wRROItemTask.StationName, wRROItemTask.ProcesName,
						wRROItemTask.UpFlowName, "",
						wRROItemTask.IsStatus == 1 ? "??????" : wRROItemTask.IsStatus == 2 ? "?????????" : "",
						wRROItemTask.OperatorName, wSDF.format(wRROItemTask.CreateTime.getTime()),
						wRROItemTask.Remark)));
				wIndex++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private Map<String, String> GetMapValue(BMSEmployee wLoginUser, int wOrderID) {
		Map<String, String> wResult = new HashMap<String, String>();
		try {
			OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			wResult.put("ProductNo", wOrder.ProductNo);
			wResult.put("LineName", wOrder.LineName);
			wResult.put("PartNo", wOrder.PartNo);
			wResult.put("CustomerName", wOrder.Customer);
			wResult.put("TableMaker", wLoginUser.Name);
			wResult.put("Leader", "");
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCPartPoint>> RRO_QueryProcess(BMSEmployee wLoginUser, int wOrderID, int wPartID) {
		ServiceResult<List<FPCPartPoint>> wResult = new ServiceResult<List<FPCPartPoint>>();
		wResult.Result = new ArrayList<FPCPartPoint>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}

			FPCPart wPart = WDWConstans.GetFPCPart(wPartID);
			if (wPart == null || wPart.ID <= 0) {
				return wResult;
			}

			if (wPart.PartType == FPCPartTypes.QTFinally.getValue()
					|| wPart.PartType == FPCPartTypes.OutFactory.getValue()) {
				List<Integer> wIDList = FMC_QueryStepIDList(wLoginUser, wOrder.LineID, wPart.ID, wOrder.ProductID);
				for (Integer wStepID : wIDList) {
					FPCPartPoint wStep = WDWConstans.GetFPCStep(wStepID);
					if (wStep.ID > 0) {
						wResult.Result.add(wStep);
					}
				}
			} else {
				List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
						.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wOrder.RouteID, wPartID)
						.List(FPCRoutePartPoint.class);
				if (wRoutePartPointList != null && wRoutePartPointList.size() > 0) {
					for (FPCRoutePartPoint wFPCRoutePartPoint : wRoutePartPointList) {
						FPCPartPoint wStep = WDWConstans.GetFPCStep(wFPCRoutePartPoint.PartPointID);
						if (wStep.ID > 0) {
							wResult.Result.add(wStep);
						}
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

	/**
	 * ???????????????????????????????????????
	 * 
	 * @return
	 */
	private List<Integer> FMC_QueryStepIDList(BMSEmployee wLoginUser, int wLineID, int wPartID, int wProductID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wLineID, -1, wProductID, false).List(FMCLineUnit.class);
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			// ??????????????????????????????
			wLineUnitList = wLineUnitList.stream().filter(p -> p.LevelID == 3 && p.ParentUnitID == wPartID)
					.collect(Collectors.toList());
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			wResult = wLineUnitList.stream().map(p -> p.UnitID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROItemTask>> RRO_QueryItemTaskList(BMSEmployee wLoginUser, int wOrdreID) {
		ServiceResult<List<RROItemTask>> wResult = new ServiceResult<List<RROItemTask>>();
		wResult.Result = new ArrayList<RROItemTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, -1, -1, null, -1, -1, -1,
					wOrdreID, -1, null, null, -1, wErrorCode);
			wResult.Result.removeIf(p -> p.Status == 0);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROItemTask>> RRO_QueryItemTaskAll(BMSEmployee wLoginUser, int wUpFlowID, int wOrderID,
			int wStationID, Calendar wStartTime, Calendar wEndTime, int wCustomerID) {
		ServiceResult<List<RROItemTask>> wResult = new ServiceResult<List<RROItemTask>>();
		wResult.Result = new ArrayList<RROItemTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = RRORepairItemDAO.getInstance().SelectList(wLoginUser, null, -1, wUpFlowID, -1, null, -1,
					-1, -1, wOrderID, wStationID, wStartTime, wEndTime, wCustomerID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROTask>> RRO_QueryTaskProList(BMSEmployee wLoginUser, Object object, int wIsDelivery,
			String wPartNo, int wCarTypeID, int wSendID, int wStationID, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, int wLineID, int wCustomerID) {
		ServiceResult<List<RROTask>> wResult = new ServiceResult<List<RROTask>>();
		wResult.Result = new ArrayList<RROTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<RROTask> wAll = RROTaskDAO.getInstance().SelectList(wLoginUser, null, wIsDelivery, wPartNo, wCarTypeID,
					wSendID, wStationID, wOrderID, wStartTime, wEndTime, wLineID, wCustomerID, wErrorCode);

			wResult.Result = new ArrayList<RROTask>(wAll.stream()
					.collect(Collectors.toMap(RROTask::getOrderID, account -> account, (k1, k2) -> k2)).values());

			// ?????????????????????
			for (RROTask wRROTask : wResult.Result) {
				wRROTask.StationNum = (int) wAll.stream().filter(p -> p.OrderID == wRROTask.OrderID)
						.map(p -> p.StationID).distinct().count();
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROItemTask>> RRO_PartUndoTaskListNew(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wIsSend, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RROItemTask>> wResultList = new ServiceResult<List<RROItemTask>>();
		wResultList.Result = new ArrayList<RROItemTask>();
		try {
			ServiceResult<List<RROPartTaskShow>> wServiceResult = RRO_QueryPartTaskList(wLoginUser, wOrderID, wIsSend,
					wStartTime, wEndTime);
			wResultList.FaultCode += wServiceResult.FaultCode;
			if (wServiceResult.Result == null || wServiceResult.Result.size() <= 0)
				return wResultList;
			Optional<RROPartTaskShow> wOptional = wServiceResult.Result.stream().filter(p -> p.PartID == wPartID)
					.findFirst();

			if (!wOptional.isPresent())
				return wResultList;

			RROPartTaskShow wRROPartTaskShow = wOptional.get();

			List<Integer> wIDList = new ArrayList<Integer>();
			for (RROItemTask wRROItemTask : wRROPartTaskShow.UndoList) {
				if (wIDList.contains(wRROItemTask.ID))
					continue;
				wIDList.add(wRROItemTask.ID);
				wRROItemTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResultList.Result.add(wRROItemTask);
			}
			for (RROItemTask wRROItemTask : wRROPartTaskShow.DoList) {
				if (wIDList.contains(wRROItemTask.ID))
					continue;
				wIDList.add(wRROItemTask.ID);
				wRROItemTask.TagTypes = TaskQueryType.Handled.getValue();
				wResultList.Result.add(wRROItemTask);
			}

			for (RROItemTask wRROItemTask : wRROPartTaskShow.SendList) {
				if (wIDList.contains(wRROItemTask.ID))
					continue;
				wIDList.add(wRROItemTask.ID);
				wRROItemTask.TagTypes = TaskQueryType.Sended.getValue();
				wResultList.Result.add(wRROItemTask);
			}

			// ??????????????????0?????????
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
			wResultList.FaultCode += e.toString();
		}
		return wResultList;
	}

	@Override
	public ServiceResult<List<RROPartNo>> RRO_QueryPartNoRepairList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<RROPartNo>> wResult = new ServiceResult<List<RROPartNo>>();
		wResult.Result = new ArrayList<RROPartNo>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<Integer> wOrderIDList = RRORepairItemDAO.getInstance().GetOrderIDList(wLoginUser, wStartTime, wEndTime,
					wErrorCode);
			if (wOrderIDList == null || wOrderIDList.size() <= 0) {
				return wResult;
			}
			List<OMSOrder> wOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryTurnOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class);
			// ??????
			wOrderList.sort(Comparator.comparing(OMSOrder::getPartNo));
			for (OMSOrder wOMSOrder : wOrderList) {
				RROPartNo wRROPartNo = RRORepairItemDAO.getInstance().GetRROPartNo(wLoginUser, wOMSOrder, wErrorCode);
				wResult.Result.add(wRROPartNo);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROPart>> RRO_QueryPartRepairList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<RROPart>> wResult = new ServiceResult<List<RROPart>>();
		wResult.Result = new ArrayList<RROPart>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			OMSOrder wOrder = APSLOCOServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);

			List<Integer> wPartIDList = RRORepairItemDAO.getInstance().GetPartListByOrder(wLoginUser, wOrderID,
					wErrorCode);

			for (int wPartID : wPartIDList) {
				RROPart wRROPartNo = RRORepairItemDAO.getInstance().GetRROPart(wLoginUser, wOrder, wPartID, wErrorCode);
				wResult.Result.add(wRROPartNo);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RROItemTask>> RRO_QueryItemListByOrder(BMSEmployee wLoginUser, int wOrderID,
			int wPartID) {
		ServiceResult<List<RROItemTask>> wResult = new ServiceResult<List<RROItemTask>>();
		wResult.Result = new ArrayList<RROItemTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = RRORepairItemDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, -1, -1, -1, "", -1,
					wPartID, -1, null, null, null, null, wErrorCode);

			// ??????
			wResult.Result.sort(Comparator.comparing(RROItemTask::getStatus));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> RRO_GetAreaID(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<LFSWorkAreaChecker> wCheckerList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
			if (wCheckerList == null || wCheckerList.size() <= 0) {
				wResult.Result = 48;
				return wResult;
			}

			if (wCheckerList.stream().anyMatch(p -> p.CheckerIDList != null
					&& p.CheckerIDList.stream().anyMatch(q -> q.intValue() == wLoginUser.ID))) {
				wResult.Result = wCheckerList.stream()
						.filter(p -> p.CheckerIDList != null
								&& p.CheckerIDList.stream().anyMatch(q -> q.intValue() == wLoginUser.ID))
						.findFirst().get().WorkAreaID;
			} else {
				wResult.Result = 48;
				return wResult;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> RRO_CodeReset(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			for (int i = 1; i <= 12; i++) {
				int wNumber = 1;
				// ????????????
				Calendar wSTime = Calendar.getInstance();
				wSTime.set(wYear, i - 1, 1, 0, 0, 0);
				// ????????????
				Calendar wETime = Calendar.getInstance();
				wETime.set(wYear, i, 1, 0, 0, 0);

				// ???????????????????????????????????????ID??????
				List<Integer> wIDList = RRORepairItemDAO.getInstance().GetTaskIDListByTime(wLoginUser, wSTime, wETime,
						wErrorCode);
				for (int wTaskID : wIDList) {

					RROItemTask wTask = (RROItemTask) RRORepairItemDAO.getInstance().BPM_GetTaskInfo(wLoginUser,
							wTaskID, "", wErrorCode);
					wTask.Code = StringUtils.Format("RO{0}{1}{2}", String.valueOf(wYear), String.format("%02d", i),
							String.format("%04d", wNumber));

					RRORepairItemDAO.getInstance().UpdateCode(wLoginUser, wTask, wErrorCode);

					wNumber++;
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
	public ServiceResult<Integer> RRO_QueryLoginInfo(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<LFSWorkAreaChecker> wList = LFSServiceImpl.getInstance().LFS_QueryWorkAreaCheckerList(wLoginUser, 48)
					.List(LFSWorkAreaChecker.class);
			wResult.Result = wList.get(0).WorkAreaID;
			wResult.CustomResult.put("AuditorList", GetAuditorList(wList.get(0).LeaderIDList));
			wResult.CustomResult.put("AreaName", WDWConstans.GetBMSDepartmentName(wList.get(0).WorkAreaID));
			wResult.CustomResult.put("MonitorList", CoreServiceImpl.getInstance()
					.GetMonitorList(wLoginUser, wLoginUser.DepartmentID).List(BMSEmployee.class));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<BMSEmployee> GetAuditorList(List<Integer> leaderIDList) {
		List<BMSEmployee> wResult = new ArrayList<BMSEmployee>();
		try {
			for (Integer wInteger : leaderIDList) {
				BMSEmployee wUser = WDWConstans.GetBMSEmployee(wInteger);
				if (wUser == null || wUser.ID <= 0) {
					continue;
				}
				wResult.add(wUser);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

}
