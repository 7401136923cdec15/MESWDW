package com.mes.ncr.server.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.ncr.server.service.MTCService;
import com.mes.ncr.server.service.mesenum.BFCMessageType;
import com.mes.ncr.server.service.mesenum.BMSDutyType;
import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.FPCProductTransport;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.mesenum.MTCStatus;
import com.mes.ncr.server.service.mesenum.OMSOrderStatus;
import com.mes.ncr.server.service.mesenum.TaskQueryType;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bfc.BFCMessage;
import com.mes.ncr.server.service.po.bms.BMSDepartment;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bms.BMSPosition;
import com.mes.ncr.server.service.po.bpm.BPMActivitiHisTask;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.lfs.LFSStoreHouse;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.ncr.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.ncr.server.service.po.mtc.MTCRealTime;
import com.mes.ncr.server.service.po.mtc.MTCSectionInfo;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.mtc.MTCTypeNo;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.ncr.SendNCRTask;
import com.mes.ncr.server.service.po.oms.OMSOrder;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.sfc.SFCBOMTask;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.RemoteInvokeUtils;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.mtc.MTCSectionInfoDAO;
import com.mes.ncr.server.serviceimpl.dao.mtc.MTCTaskDAO;
import com.mes.ncr.server.serviceimpl.dao.ncr.NCRTaskDAO;
import com.mes.ncr.server.serviceimpl.dao.ncr.SendNCRTaskDAO;
import com.mes.ncr.server.serviceimpl.dao.rro.RRORepairItemDAO;
import com.mes.ncr.server.serviceimpl.dao.sfc.SFCBOMTaskDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2021-1-21 14:11:45
 * @LastEditTime 2021-1-21 14:11:49
 *
 */
@Service
public class MTCServiceImpl implements MTCService {

	private static Logger logger = LoggerFactory.getLogger(MTCServiceImpl.class);

	public MTCServiceImpl() {
	}

	private static MTCService Instance;

	public static MTCService getInstance() {
		if (Instance == null)
			Instance = new MTCServiceImpl();
		return Instance;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public ServiceResult<List<MTCTypeNo>> Get_MTCTypeNoList(BMSEmployee wLoginUser) {
		ServiceResult<List<MTCTypeNo>> wResultList = new ServiceResult<List<MTCTypeNo>>();
		try {
			// ??????????????????
			wResultList.Result = new ArrayList<MTCTypeNo>();
			// ????????????????????????
			List<Integer> wStatusList = new ArrayList<Integer>();
			wStatusList.add(OMSOrderStatus.EnterFactoryed.getValue());
			wStatusList.add(OMSOrderStatus.Repairing.getValue());
			wStatusList.add(OMSOrderStatus.FinishedWork.getValue());
			List<OMSOrder> wOMSOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, wStatusList).List(OMSOrder.class);
			// ??????????????????
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);
			if (wFMCWorkspaceList != null && wFMCWorkspaceList.size() > 0) {
				for (FMCWorkspace wItem : wFMCWorkspaceList) {
					// ??????
					LFSStoreHouse wLFSStoreHouse = new LFSStoreHouse();
					// ??????
					OMSOrder wOMSOrder = new OMSOrder();
					if (wItem.ActualPartNoList != null && wItem.ActualPartNoList.size() > 0) {
						for (String wPartNo : wItem.ActualPartNoList) {
							if (StringUtils.isNotEmpty(wPartNo)) {
								FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).getResult();
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue()) {
									String wNewPartNo = StringUtils.Format("{0}#{1}", wFPCProduct.PrevProductNo,
											wPartNo.split("#")[1]);
									Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
											.filter(p -> p.PartNo.equals(wNewPartNo)).findFirst();
									if (wOMSOptional != null && wOMSOptional.isPresent())
										wOMSOrder = wOMSOptional.get();
								} else {
									Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
											.filter(p -> p.PartNo.equals(wPartNo)).findFirst();
									if (wOMSOptional != null && wOMSOptional.isPresent())
										wOMSOrder = wOMSOptional.get();
								}
								Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream()
										.filter(p -> p.ID == wItem.ParentID).findFirst();
								if (wOptional != null && wOptional.isPresent())
									wLFSStoreHouse = wOptional.get();

								MTCTypeNo wMTCTypeNo = new MTCTypeNo();
								wMTCTypeNo.PartNo = wPartNo;
								wMTCTypeNo.ProductNo = wFPCProduct.ProductName;
								wMTCTypeNo.Transport = wFPCProduct.TransportType;
								wMTCTypeNo.StockID = wItem.ParentID;
								wMTCTypeNo.StockName = wLFSStoreHouse.Name;
								wMTCTypeNo.StationID = wItem.ID;
								wMTCTypeNo.StationName = wItem.Name;
								wMTCTypeNo.CustomerID = wOMSOrder.BureauSectionID;
								wMTCTypeNo.CustomerName = wOMSOrder.BureauSection;
								wResultList.Result.add(wMTCTypeNo);
							}
						}
					}
					if (wItem.PartNo != null && StringUtils.isNotEmpty(wItem.PartNo)) {
						FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wItem.PartNo).getResult();
						if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
								|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue()) {
							String wNewPartNo = StringUtils.Format("{0}#{1}", wFPCProduct.PrevProductNo,
									wItem.PartNo.split("#")[1]);
							Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
									.filter(p -> p.PartNo.equals(wNewPartNo)).findFirst();
							if (wOMSOptional != null && wOMSOptional.isPresent())
								wOMSOrder = wOMSOptional.get();
						} else {
							Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
									.filter(p -> p.PartNo.equals(wItem.PartNo)).findFirst();
							if (wOMSOptional != null && wOMSOptional.isPresent())
								wOMSOrder = wOMSOptional.get();
						}
						Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream()
								.filter(p -> p.ID == wItem.ParentID).findFirst();
						if (wOptional != null && wOptional.isPresent())
							wLFSStoreHouse = wOptional.get();

						MTCTypeNo wMTCTypeNo = new MTCTypeNo();
						wMTCTypeNo.PartNo = wItem.PartNo;
						wMTCTypeNo.ProductNo = wFPCProduct.ProductName;
						wMTCTypeNo.Transport = wFPCProduct.TransportType;
						wMTCTypeNo.StockID = wItem.ParentID;
						wMTCTypeNo.StockName = wLFSStoreHouse.Name;
						wMTCTypeNo.StationID = wItem.ID;
						wMTCTypeNo.StationName = wItem.Name;
						wMTCTypeNo.CustomerID = wOMSOrder.BureauSectionID;
						wMTCTypeNo.CustomerName = wOMSOrder.BureauSection;
						wResultList.Result.add(wMTCTypeNo);
					}
				}
			}
			// ??????????????????
			if (wResultList.Result != null && wResultList.Result.size() > 0)
				wResultList.Result = wResultList.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors
										.toCollection(() -> new TreeSet<>(Comparator.comparing(MTCTypeNo::getPartNo))),
								ArrayList::new));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResultList;
	}

	/**
	 * ??????????????????
	 */
	@Override
	public ServiceResult<MTCTask> MTC_CreateTask(BMSEmployee wBMSEmployee, BPMEventModule wModule) {
		ServiceResult<MTCTask> wResult = new ServiceResult<MTCTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = MTCTaskDAO.getInstance().MTC_CreateTask(wBMSEmployee, wModule, wErrorCode);

			if (wBMSEmployee.DepartmentID == 69) {
				wResult.Result.IsMoveClass = 1;
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	@Override
	public ServiceResult<MTCTask> MTC_QueryDefaultTask(BMSEmployee wLoginUser, int wModuleID) {
		ServiceResult<MTCTask> wResult = new ServiceResult<MTCTask>();
		try {
			wResult.Result = new MTCTask();
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MTCTask> wMTCTaskList = MTCTaskDAO.getInstance().SelectList(wLoginUser, wModuleID, -1, -1, -1,
					wLoginUser.getID(), StringUtils.parseList(new Integer[] { NCRStatus.Default.getValue() }), null, -1,
					-1, -1, -1, "", null, null, wErrorCode);

			if (wMTCTaskList != null && wMTCTaskList.size() > 0) {
				wResult.Result = wMTCTaskList.get(0);

				// ?????????????????????-??????
				if (wLoginUser.DepartmentID == 69) {
					wResult.Result.IsMoveClass = 1;
				}

				// ???????????????????????????????????????????????????ID???Name
				BMSPosition wBMSPosition = WDWConstans.GetBMSPosition(wLoginUser.Position);
				if (wBMSPosition != null && wBMSPosition.DutyID == BMSDutyType.Member.getValue()) {

					List<BMSEmployee> wPList = WDWConstans.GetBMSEmployeeList().values().stream()
							.filter(p -> p.Active == 1 && p.DepartmentID == wBMSPosition.DepartmentID
									&& WDWConstans.GetBMSPosition(p.Position).DutyID == 1)
							.collect(Collectors.toList());
					if (wPList != null && wPList.size() > 0) {
						List<Integer> wIDList = new ArrayList<Integer>();
						List<String> wNameList = new ArrayList<String>();
						for (BMSEmployee wBMSEmployee : wPList) {
							wIDList.add(wBMSEmployee.ID);
							wNameList.add(wBMSEmployee.Name);
						}
						wResult.Result.MonitorID = StringUtils.Join(",", wIDList);
						wResult.Result.MonitorName = StringUtils.Join(",", wNameList);
					}
				}
			}

		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	@Override
	public ServiceResult<List<LFSStoreHouse>> Get_StoreHouseList(BMSEmployee wLoginUser, String wPartNo, int wIsPreMove,
			int wStockID) {
		ServiceResult<List<LFSStoreHouse>> wResult = new ServiceResult<List<LFSStoreHouse>>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ??????????????????????????????
			wResult.Result = new ArrayList<LFSStoreHouse>();
			// ??????????????????
			LFSStoreHouse wNewStoreHouse = new LFSStoreHouse();
			FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).getResult();
			// ??????????????????
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// ??????????????????
			AssignAreaInfo(wLoginUser, wLFSStoreHouseList);

			// ??????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			// A2/A3?????????????????????????????????????????????
			if (wFMCWorkspaceList.stream().anyMatch(p -> p.PartNo.equals(wPartNo) && p.OrderID != 1
					&& wLFSStoreHouseList.stream().anyMatch(q -> q.ID == p.ParentID && q.Name.equals("A2")))) {
				return wResult;
			} else if (wFMCWorkspaceList.stream().anyMatch(p -> p.PartNo.equals(wPartNo) && p.OrderID != 2
					&& wLFSStoreHouseList.stream().anyMatch(q -> q.ID == p.ParentID && q.Name.equals("A3")))) {
				return wResult;
			}

			// ?????????????????????
			if (wIsPreMove == 1) {
				wResult.Result = wLFSStoreHouseList;
				return wResult;
			}

			// ????????????????????????????????????????????????????????????
			boolean wIsCXKW = false;
			if (wLFSStoreHouseList.stream().anyMatch(p -> p.ID == wStockID && p.Type == 2)) {
				wIsCXKW = true;
			}
			if (wLFSStoreHouseList.stream()
					.anyMatch(p -> p.ID == wStockID && (p.Name.equals("A2") || p.Name.equals("A3")))) {
				wIsCXKW = true;
			}
			if (wIsPreMove == 0 && wIsCXKW == false) {
//				boolean wFlag = wIsCanMove(wLoginUser, wPartNo, wFMCWorkspaceList);
//				if (!wFlag) {
//					wResult.Result = new ArrayList<LFSStoreHouse>();
//					return wResult;
//				}

//				if (wFMCWorkspaceList.stream().anyMatch(p -> p.PartNo.equals(wPartNo)
//						|| p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo)))) {
//					FMCWorkspace wSourceSpace = wFMCWorkspaceList.stream().filter(p -> p.PartNo.equals(wPartNo)
//							|| p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo))).findFirst().get();
//					if (wFMCWorkspaceList.stream().anyMatch(p -> p.ParentID == wSourceSpace.ParentID
//							&& p.OrderID - wSourceSpace.OrderID == -1 && StringUtils.isNotEmpty(p.PartNo))) {
//						wResult.Result = new ArrayList<LFSStoreHouse>();
//						return wResult;
//					}
//				}
			}

			// ??????????????????????????????
			wNewStoreHouse = GetCarCurStoreHouse(wPartNo, wNewStoreHouse, wLFSStoreHouseList, wFMCWorkspaceList);
			// ?????????????????????????????????????????????
			boolean wIsLetGo = false;
			wIsLetGo = JudgeDateDoorWorkSpace(wNewStoreHouse, wFMCWorkspaceList, wIsLetGo);
			if (!wIsLetGo)
				return wResult;
			// ?????????????????????????????????
			List<MTCTask> wMTCTaskList = MTCTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, null,
					StringUtils
							.parseList(new Integer[] { MTCStatus.TaskCancel.getValue(), MTCStatus.Completion.getValue(),
									MTCStatus.ToSendTask.getValue(), MTCStatus.TaskReject.getValue() }),
					-1, -1, -1, -1, "", null, null, wErrorCode);
			// ?????????????????????????????????????????????
			if (wLFSStoreHouseList != null && wLFSStoreHouseList.size() > 0 && wFPCProduct != null
					&& wFPCProduct.ID > 0) {
				for (LFSStoreHouse wItem : wLFSStoreHouseList) {
					boolean wIsTrue = false;
					// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
					if (wNewStoreHouse != null && wNewStoreHouse.ID > 0) {
						wIsTrue = JudgeDateDoorWorkSpace(wItem, wFMCWorkspaceList, wIsTrue);
						if (!wIsTrue)
							continue;
					}
					// ?????????????????????????????????????????????
					if (wItem.Type == 1 && (wFPCProduct.TransportType == 1 || wFPCProduct.TransportType == 2))
						continue;
					// ???????????????????????????
					int wDonLenght = 0;
					// ???????????????????????????????????????????????????(?????????????????????)
					wDonLenght = CalcMoveTaskDoneLength(wLoginUser, wMTCTaskList, wItem, wDonLenght);
					// ????????????????????????????????????????????????
					List<FMCWorkspace> wAllWorkspaceList = wFMCWorkspaceList.stream()
							.filter(p -> p.ParentID == wItem.ID).collect(Collectors.toList());
					// ??????????????????????????????????????????????????????
					wDonLenght = CalcAllWorkSpaceDoneLength(wLoginUser, wDonLenght, wAllWorkspaceList);
					// ????????????
					if (wFPCProduct.Length <= (wItem.getLength() - wDonLenght))
						wResult.Result.add(wItem);
					else {
						// ???????????????????????????????????????????????????????????????????????????????????????????????????
						switch (FPCProductTransport.getEnumType(wFPCProduct.TransportType)) {
						case Body:// ??????
							JudgeBody(wLoginUser, wPartNo, wResult, wFPCProduct, wItem, wDonLenght, wAllWorkspaceList);
							break;
						case Bottom: // ?????????
							JudgeBottom(wLoginUser, wPartNo, wResult, wFPCProduct, wItem, wDonLenght,
									wAllWorkspaceList);
							break;
						case Bottom_T: // ?????????
							JudgeBottom_T(wLoginUser, wResult, wFPCProduct, wItem, wDonLenght, wAllWorkspaceList);
							break;
						default:
							break;
						}
					}
				}
			}
			// ??????
			if (wResult.Result != null && wResult.Result.size() > 0) {
				wResult.Result = new ArrayList<LFSStoreHouse>(wResult.Result.stream()
						.collect(Collectors.toMap(LFSStoreHouse::getID, account -> account, (k1, k2) -> k2)).values());
				// ???????????????????????????????????????????????????????????????
				wResult.Result = RemoveFirst(wLoginUser, wResult.Result, wFMCWorkspaceList, wPartNo);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????
	 */
	@SuppressWarnings("unused")
	private boolean wIsCanMove(BMSEmployee wLoginUser, String wPartNo, List<FMCWorkspace> wFMCWorkspaceList) {
		boolean wResult = false;
		try {
			if (wFMCWorkspaceList.stream().anyMatch(
					p -> p.PartNo.equals(wPartNo) || p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo)))) {
				FMCWorkspace wSourceSpace = wFMCWorkspaceList.stream().filter(
						p -> p.PartNo.equals(wPartNo) || p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo)))
						.findFirst().get();
				// ??????????????????
				if (wFMCWorkspaceList.stream().filter(p -> p.ParentID == wSourceSpace.ParentID).count() <= 1) {
					return true;
				}
				// ??????????????????
				if (wFMCWorkspaceList.stream()
						.anyMatch(p -> p.OrderID == wSourceSpace.OrderID + 1 && p.ParentID == wSourceSpace.ParentID)) {
					FMCWorkspace wNextSpace = wFMCWorkspaceList.stream()
							.filter(p -> p.OrderID == wSourceSpace.OrderID + 1 && p.ParentID == wSourceSpace.ParentID)
							.findFirst().get();
					if (StringUtils.isEmpty(wNextSpace.PartNo)) {
						return true;
					} else {
						FPCProduct wSourceProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).Result;
						FPCProduct wTargetProduct = this.MTC_GetProductByPartNo(wLoginUser, wNextSpace.PartNo).Result;
						switch (FPCProductTransport.getEnumType(wSourceProduct.TransportType)) {
						case Whole:
							break;
						case Body:
							if ((wPartNo.split("#")[1].equals(wNextSpace.PartNo.split("#")[1])
									&& wTargetProduct.TransportType == FPCProductTransport.Bottom.getValue())
									|| wTargetProduct.TransportType == FPCProductTransport.Bottom_T.getValue()) {
								return true;
							}
							break;
						case Bottom_T:
						case Bottom:
							if ((wPartNo.split("#")[1].equals(wNextSpace.PartNo.split("#")[1])
									&& wTargetProduct.TransportType == FPCProductTransport.Body.getValue())) {
								return true;
							}
							break;
						default:
							break;
						}
					}
				}
				// ??????????????????
				if (wFMCWorkspaceList.stream()
						.anyMatch(p -> p.OrderID == wSourceSpace.OrderID - 1 && p.ParentID == wSourceSpace.ParentID)) {
					FMCWorkspace wPreSpace = wFMCWorkspaceList.stream()
							.filter(p -> p.OrderID == wSourceSpace.OrderID - 1 && p.ParentID == wSourceSpace.ParentID)
							.findFirst().get();
					if (StringUtils.isEmpty(wPreSpace.PartNo)) {
						return true;
					} else {
						FPCProduct wSourceProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).Result;
						FPCProduct wTargetProduct = this.MTC_GetProductByPartNo(wLoginUser, wPreSpace.PartNo).Result;
						switch (FPCProductTransport.getEnumType(wSourceProduct.TransportType)) {
						case Whole:
							break;
						case Body:
							if ((wPartNo.split("#")[1].equals(wPreSpace.PartNo.split("#")[1])
									&& wTargetProduct.TransportType == FPCProductTransport.Bottom.getValue())
									|| wTargetProduct.TransportType == FPCProductTransport.Bottom_T.getValue()) {
								return true;
							}
							break;
						case Bottom_T:
						case Bottom:
							if ((wPartNo.split("#")[1].equals(wPreSpace.PartNo.split("#")[1])
									&& wTargetProduct.TransportType == FPCProductTransport.Body.getValue())) {
								return true;
							}
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 */
	private List<LFSStoreHouse> RemoveFirst(BMSEmployee wLoginUser, List<LFSStoreHouse> wList,
			List<FMCWorkspace> wFMCWorkspaceList, String wPartNo) {
		List<LFSStoreHouse> wResult = new ArrayList<LFSStoreHouse>();
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			// ?????????????????????
			FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).getResult();

			for (LFSStoreHouse wLFSStoreHouse : wList) {
				// ????????????????????????????????????
				if (wLFSStoreHouse.Type == 2) {
					wResult.add(wLFSStoreHouse);
					continue;
				}

				// ?????????????????????????????????
				List<FMCWorkspace> wSpaceList = wFMCWorkspaceList.stream().filter(p -> p.ParentID == wLFSStoreHouse.ID)
						.collect(Collectors.toList());
				if (wSpaceList == null || wSpaceList.size() <= 0) {
					continue;
				}

				// ???????????????????????????
				if (wSpaceList.stream().anyMatch(p -> p.PartNo.equals(wPartNo)
						|| p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo)))) {
					FMCWorkspace wSpace = wSpaceList.stream().filter(p -> p.PartNo.equals(wPartNo)
							|| p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo))).findFirst().get();
					if (wSpace.ParentID == wLFSStoreHouse.ID) {
						wResult.add(wLFSStoreHouse);
						continue;
					}
				}

				// ????????????????????????
				FMCWorkspace wSpace = null;
				if (wLFSStoreHouse.Name.equals("A3")) {
					if (wSpaceList.stream().anyMatch(p -> p.OrderID == 2)) {
						wSpace = wSpaceList.stream().filter(p -> p.OrderID == 2).findFirst().get();
					}
				} else {
					if (wSpaceList.stream().anyMatch(p -> p.OrderID == 1)) {
						wSpace = wSpaceList.stream().filter(p -> p.OrderID == 1).findFirst().get();
					}
				}
				if (wSpace == null) {
					continue;
				}

				// ??????????????????
				if (wPartNo.equals(wSpace.PartNo)) {
					wResult.add(wLFSStoreHouse);
					continue;
				}

				// ???????????????
				if (wSpace.AlowTransType == 1) {
					if (StringUtils.isNotEmpty(wSpace.PartNo)) {
						FPCProduct wItemProduct = this.MTC_GetProductByPartNo(wLoginUser, wSpace.PartNo).getResult();

						switch (FPCProductTransport.getEnumType(wFPCProduct.TransportType)) {
						case Whole:
							continue;
						case Body:
							if ((wItemProduct.TransportType != FPCProductTransport.Bottom.getValue()
									&& wItemProduct.TransportType != FPCProductTransport.Bottom_T.getValue())
									|| !wPartNo.split("#")[1].equals(wSpace.PartNo.split("#")[1])) {
								continue;
							} else {
								break;
							}
						case Bottom:
							if (wItemProduct.TransportType != FPCProductTransport.Body.getValue()
									|| !wPartNo.split("#")[1].equals(wSpace.PartNo.split("#")[1])) {
								continue;
							}
							break;
						case Bottom_T:
							if (wItemProduct.TransportType != FPCProductTransport.Body.getValue()) {
								continue;
							}
							break;
						default:
							break;
						}
					}
				}
				// ??????????????????
				else {
					if (StringUtils.isNotEmpty(wSpace.PartNo)) {
						continue;
					}
				}
				// ?????????????????????
				wResult.add(wLFSStoreHouse);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ????????????
	 */
	@SuppressWarnings("unused")
	private void JudgeBody(BMSEmployee wLoginUser, String wPartNo, ServiceResult<List<LFSStoreHouse>> wStoreHouseResult,
			FPCProduct wFPCProduct, LFSStoreHouse wItem, int wDonLenght, List<FMCWorkspace> wAllWorkspaceList) {
		try {
			if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
				for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
					if (wFMCWorkspace.PartNo != null && StringUtils.isNotEmpty(wFMCWorkspace.PartNo)) {
						String[] wList = wPartNo.split("#");
						String wNumber = wList[1];
						String[] wNewList = wFMCWorkspace.PartNo.split("#");
						String wNewNumber = wNewList[1];
						// ????????????????????????????????????????????????????????????
						FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser, wFMCWorkspace.PartNo).getResult();
						if (wProduct != null && wProduct.ID > 0) {
							switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
							case Bottom: // ?????????
								// ???????????????????????????
								if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo)
										&& wNumber.equals(wNewNumber)) {
									// ????????????????????????
									if (wFPCProduct.Length < (wItem.getLength() + wProduct.Length - wDonLenght))
										wStoreHouseResult.Result.add(wItem);
								}
//								if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo)) {
//									// ????????????????????????
//									if (wFPCProduct.Length < (wItem.getLength() + wProduct.Length - wDonLenght))
//										wStoreHouseResult.Result.add(wItem);
//								}
								break;
							case Bottom_T: // ?????????
								// ???????????????????????????????????????????????????
								if (wFPCProduct.Length < (wItem.getLength() + wProduct.Length - wDonLenght))
									wStoreHouseResult.Result.add(wItem);
								break;
							default:
								break;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 */
	@SuppressWarnings("unused")
	private void JudgeBottom(BMSEmployee wLoginUser, String wPartNo,
			ServiceResult<List<LFSStoreHouse>> wStoreHouseResult, FPCProduct wFPCProduct, LFSStoreHouse wItem,
			int wDonLenght, List<FMCWorkspace> wAllWorkspaceList) {
		try {
			if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
				for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
					if (wFMCWorkspace.PartNo != null && StringUtils.isNotEmpty(wFMCWorkspace.PartNo)) {
						String[] wList = wPartNo.split("#");
						String wNumber = wList[1];
						String[] wNewList = wFMCWorkspace.PartNo.split("#");
						String wNewNumber = wNewList[1];
						// ????????????????????????????????????????????????????????????
						FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser, wFMCWorkspace.PartNo).getResult();
						if (wProduct != null && wProduct.ID > 0) {
							switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
							case Body: // ??????
								// ???????????????????????????
								if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo)
										&& wNumber.equals(wNewNumber)) {
									// ????????????????????????
									if (wFPCProduct.Length < (wItem.getLength() + wProduct.Length - wDonLenght))
										wStoreHouseResult.Result.add(wItem);
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 */
	private void JudgeBottom_T(BMSEmployee wLoginUser, ServiceResult<List<LFSStoreHouse>> wStoreHouseResult,
			FPCProduct wFPCProduct, LFSStoreHouse wItem, int wDonLenght, List<FMCWorkspace> wAllWorkspaceList) {
		try {
			if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
				for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
					if (wFMCWorkspace.PartNo != null && StringUtils.isNotEmpty(wFMCWorkspace.PartNo)) {
						// ????????????????????????????????????????????????????????????
						FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser, wFMCWorkspace.PartNo).getResult();
						if (wProduct != null && wProduct.ID > 0) {
							switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
							case Body: // ??????
										// ????????????????????????
								if (wFPCProduct.Length < (wItem.getLength() + wProduct.Length - wDonLenght))
									wStoreHouseResult.Result.add(wItem);
								break;
							default:
								break;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 */
	private int CalcAllWorkSpaceDoneLength(BMSEmployee wLoginUser, int wDonLenght,
			List<FMCWorkspace> wAllWorkspaceList) {
		if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
			for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
				if (wFMCWorkspace.PartNo != null && StringUtils.isNotEmpty(wFMCWorkspace.PartNo)) {
					// ????????????????????????????????????????????????????????????
					FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser, wFMCWorkspace.PartNo).getResult();
					if (wProduct != null && wProduct.ID > 0)
						wDonLenght += wProduct.getLength();
				}
			}
		}
		return wDonLenght;
	}

	/**
	 * ???????????????????????????????????????????????????
	 */
	private int CalcMoveTaskDoneLength(BMSEmployee wLoginUser, List<MTCTask> wMTCTaskList, LFSStoreHouse wItem,
			int wDonLenght) {
		try {
			if (wMTCTaskList != null && wMTCTaskList.size() > 0) {
				List<MTCTask> wOccupyList = wMTCTaskList.stream().filter(p -> p.TargetStockID == wItem.ID)
						.collect(Collectors.toList());
				if (wOccupyList != null && wOccupyList.size() > 0) {
					for (MTCTask wMTCTask : wOccupyList) {
						FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser,
								StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo)).getResult();
						if (wProduct != null && wProduct.ID > 0)
							wDonLenght += wProduct.getLength();

					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wDonLenght;
	}

	/**
	 * ??????????????????????????????
	 */
	private LFSStoreHouse GetCarCurStoreHouse(String wPartNo, LFSStoreHouse wNewStoreHouse,
			List<LFSStoreHouse> wLFSStoreHouseList, List<FMCWorkspace> wFMCWorkspaceList) {
		try {
			Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream()
					.filter(p -> p.PartNo.equals(wPartNo) || p.ActualPartNoList.contains(wPartNo)).findFirst();
			if (wOptional != null && wOptional.isPresent()) {
				Optional<LFSStoreHouse> wStoreOptional = wLFSStoreHouseList.stream()
						.filter(p -> p.ID == wOptional.get().getParentID()).findFirst();
				if (wStoreOptional != null && wStoreOptional.isPresent())
					wNewStoreHouse = wStoreOptional.get();
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wNewStoreHouse;
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	private boolean JudgeDateDoorWorkSpace(LFSStoreHouse wNewStoreHouse, List<FMCWorkspace> wFMCWorkspaceList,
			boolean wIsLetGo) {
		if (wNewStoreHouse.GateDoorWorkSpaceIDList != null && wNewStoreHouse.GateDoorWorkSpaceIDList.size() > 0) {
			for (Integer wInteger : wNewStoreHouse.GateDoorWorkSpaceIDList) {
				if (wInteger == 0) {
					wIsLetGo = true;
					continue;
				}
				Optional<FMCWorkspace> wWorkOptional = wFMCWorkspaceList.stream().filter(p -> p.ID == wInteger)
						.findFirst();
				if (wWorkOptional != null && wWorkOptional.isPresent()) {
					if (wWorkOptional.get().PartNo == null || StringUtils.isEmpty(wWorkOptional.get().PartNo)) {
						wIsLetGo = true;
						continue;
					}
				}
			}
		} else
			wIsLetGo = true;
		return wIsLetGo;
	}

	/**
	 * ??????????????????
	 */
	private void AssignAreaInfo(BMSEmployee wLoginUser, List<LFSStoreHouse> wLFSStoreHouseList) {
		try {
			for (LFSStoreHouse wLFSStoreHouse : wLFSStoreHouseList) {
				if (wLFSStoreHouse.AreaID == 0)
					continue;
				List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList = LFSServiceImpl.getInstance()
						.LFS_QueryWorkAreaCheckerList(wLoginUser, wLFSStoreHouse.AreaID).List(LFSWorkAreaChecker.class);
				if (wLFSWorkAreaCheckerList.get(0).LeaderIDList == null
						|| wLFSWorkAreaCheckerList.get(0).LeaderIDList.size() <= 0)
					continue;
				List<String> wNameList = new ArrayList<String>();
				for (Integer wInteger : wLFSWorkAreaCheckerList.get(0).LeaderIDList) {
					wNameList.add(WDWConstans.GetBMSEmployeeName(wInteger));
				}
				wLFSStoreHouse.LeaderIDList = wLFSWorkAreaCheckerList.get(0).LeaderIDList;
				wLFSStoreHouse.AreaDis = StringUtils.Format("{0}({1})", StringUtils.Join(",", wNameList),
						WDWConstans.GetBMSDepartmentName(wLFSStoreHouse.AreaID));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????????????????
	 */
	@Override
	public ServiceResult<List<FMCWorkspace>> Get_WorkspaceList(BMSEmployee wLoginUser, int wStokID, String wPartNo,
			int wTargetID, int wIsPreMove) {
		ServiceResult<List<FMCWorkspace>> wWorkspaceResult = new ServiceResult<List<FMCWorkspace>>();
		try {
			wWorkspaceResult.Result = new ArrayList<FMCWorkspace>();

			FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).getResult();
			// ??????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			// ?????????????????????????????????????????????
			if (wIsPreMove == 1) {
				wWorkspaceResult.Result = wFMCWorkspaceList.stream().filter(p -> p.ParentID == wStokID)
						.collect(Collectors.toList());

				List<LFSStoreHouse> wStoreList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
						.List(LFSStoreHouse.class);

				// ????????????????????????????????????????????????
				if (wStoreList.stream().anyMatch(p -> p.ID == wStokID && p.Type == 2)) {
					wWorkspaceResult.Result = new ArrayList<FMCWorkspace>();
					return wWorkspaceResult;
				}

				if (wStoreList.stream().anyMatch(p -> p.ID == wStokID)) {
					LFSStoreHouse wLFSStoreHouse = wStoreList.stream().filter(p -> p.ID == wStokID).findFirst().get();
					if (wLFSStoreHouse.Name.equals("A2")) {
						wWorkspaceResult.Result.removeIf(p -> p.OrderID != 1);
					} else if (wLFSStoreHouse.Name.equals("A3")) {
						wWorkspaceResult.Result.removeIf(p -> p.OrderID != 2);
					}
				}

				// ??????????????????
				if (wStoreList.stream().anyMatch(p -> p.ID == wStokID)) {
					wWorkspaceResult.Result.removeIf(p -> p.PlaceType == 2);
				}

				return wWorkspaceResult;
			}

			// ?????????????????????????????????
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MTCTask> wMTCTaskList = MTCTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, null,
					StringUtils.parseList(new Integer[] { MTCStatus.TaskCancel.getValue(),
							MTCStatus.Completion.getValue(), MTCStatus.Default.getValue(),
							MTCStatus.ToSendTask.getValue(), MTCStatus.TaskReject.getValue() }),
					-1, -1, -1, -1, "", null, null, wErrorCode);

			// ????????????ID??????
			List<Integer> wTargetIDList = new ArrayList<Integer>();
			if (wMTCTaskList != null && wMTCTaskList.size() > 0) {
				for (MTCTask wMTCTask : wMTCTaskList)
					wTargetIDList.add(wMTCTask.TargetID);
			}
			// ???????????????????????????(PlaceType??????1???????????????????????????)
			List<FMCWorkspace> wAllWorkspaceList = wFMCWorkspaceList.stream()
					.filter(p -> p.ParentID == wStokID && p.PlaceType == 1).collect(Collectors.toList());
			if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
				for (FMCWorkspace wItem : wAllWorkspaceList) {
					if (wItem.PartNo != null && StringUtils.isNotEmpty(wItem.PartNo))// ??????????????????
					{
						FPCProduct wProduct = this.MTC_GetProductByPartNo(wLoginUser, wItem.PartNo).getResult();
//						String[] wList = wPartNo.split("#");
//						String wNumber = wList[1];
//						String[] wNewList = wItem.PartNo.split("#");
//						String wNewNumber = wNewList[1];
						switch (FPCProductTransport.getEnumType(wFPCProduct.TransportType)) {
						case Body:// ??????
							if (wProduct != null && wProduct.ID > 0) {
								switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
								case Bottom: // ?????????
									// ????????????????????????????????????????????????
//									if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo)
//											&& wNumber.equals(wNewNumber))
//										wWorkspaceResult.Result.add(wItem);
									if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo))
										wWorkspaceResult.Result.add(wItem);
									break;
								case Bottom_T: // ?????????
									wWorkspaceResult.Result.add(wItem);
									break;
								default:
									break;
								}
							}
							break;
						case Bottom: // ?????????
							switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
							case Body: // ??????
								// ???????????????????????????
//								if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo)
//										&& wNumber.equals(wNewNumber))
//									wWorkspaceResult.Result.add(wItem);
								if (wProduct.PrevProductNo.equals(wFPCProduct.PrevProductNo))
									wWorkspaceResult.Result.add(wItem);
								break;
							default:
								break;
							}
							break;
						case Bottom_T: // ?????????
							switch (FPCProductTransport.getEnumType(wProduct.TransportType)) {
							case Body: // ??????
								wWorkspaceResult.Result.add(wItem);
								break;
							default:
								break;
							}
							break;
						default:
							break;
						}
						continue;
					}
					if (wTargetIDList.contains(wItem.ID))// ??????????????????????????????
						continue;
					wWorkspaceResult.Result.add(wItem);
				}
			}
			if (wTargetID > 0) {
				List<FMCWorkspace> wWorkspaceList = wFMCWorkspaceList.stream().filter(p -> p.ID == wTargetID)
						.collect(Collectors.toList());
				if (wWorkspaceList != null && wWorkspaceList.size() > 0) {
					wWorkspaceResult.Result.add(wWorkspaceList.get(0));
				}
			}

			// ??????????????????(1???A2???A3??????????????????????????????????????? 2????????????????????????????????????????????????????????????????????????????????????)
			List<LFSStoreHouse> wStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			if (wStoreHouseList.stream().anyMatch(p -> p.ID == wStokID)) {
				LFSStoreHouse wLFSStoreHouse = wStoreHouseList.stream().filter(p -> p.ID == wStokID).findFirst().get();

				FMCWorkspace wSourceSpace = wFMCWorkspaceList.stream().filter(
						p -> p.PartNo.equals(wPartNo) || p.ActualPartNoList.stream().anyMatch(q -> q.equals(wPartNo)))
						.findFirst().get();

				if (wLFSStoreHouse.Name.equals("A2")) {
					wWorkspaceResult.Result.removeIf(p -> p.ParentID != wSourceSpace.ParentID && p.OrderID != 1);
				} else if (wLFSStoreHouse.Name.equals("A3")) {
					wWorkspaceResult.Result.removeIf(p -> p.ParentID != wSourceSpace.ParentID && p.OrderID != 2);
				} else {
					List<FMCWorkspace> wList = wFMCWorkspaceList.stream().filter(p -> p.ParentID == wStokID)
							.collect(Collectors.toList());

					wWorkspaceResult.Result.removeIf(p -> wList.stream()
							.anyMatch(q -> q.ParentID != wSourceSpace.ParentID && !wPartNo.equals(q.PartNo)
									&& StringUtils.isNotEmpty(q.PartNo) && q.OrderID < p.OrderID));
				}
			}

			// ??????????????????
			if (wStoreHouseList.stream().anyMatch(p -> p.ID == wStokID && p.Type != 2)) {
				wWorkspaceResult.Result.removeIf(p -> p.PlaceType == 2);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wWorkspaceResult;
	}

	/**
	 * ID??????
	 */
	@Override
	public ServiceResult<MTCTask> MTC_GetTask(BMSEmployee wLoginUser, int wID, String wCode) {
		ServiceResult<MTCTask> wResult = new ServiceResult<MTCTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = (MTCTask) MTCTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, wCode, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????? ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wStartTime ??????????????????
	 * @param wEndTime   ??????????????????
	 * @return
	 */
	@Override
	public ServiceResult<List<MTCTask>> MTC_GetTaskList(BMSEmployee wLoginUser, int wFlowID, int wFlowType,
			int wPlaceID, int wTargetID, int wShiftID, int wOrderID, int wType, int wCarTypeID, String wCarNumber,
			Calendar wStartTime, Calendar wEndTime, int wUpFlowID) {
		ServiceResult<List<MTCTask>> wResult = new ServiceResult<List<MTCTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = MTCTaskDAO.getInstance().SelectList(wLoginUser, wFlowType, wFlowID, wPlaceID, wTargetID,
					wUpFlowID, -1, wShiftID, wOrderID, wType, wCarTypeID, wCarNumber, wStartTime, wEndTime, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser
	 * @param wTagType
	 * @param wStartTime
	 * @param wEndTime
	 * @return
	 */
	@Override
	public ServiceResult<List<BPMTaskBase>> MTC_GetTaskListByEmployee(BMSEmployee wLoginUser, TagTypes wTagType,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			switch (wTagType) {
			case Applicant:

				wResult.Result = MTCTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);

//				wResult = this.MTC_GetSendTaskListByEmployee(wLoginUser, wStartTime, wEndTime);
				break;
			case Dispatcher:
				wResult.Result = MTCTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
						wErrorCode);
//				wResult = this.MTC_GetUndoTaskListByEmployee(wLoginUser);
				break;
			case Confirmer:
			case Approver:
				wResult.Result = MTCTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
						wStartTime, wEndTime, wErrorCode);
//				wResult = this.MTC_GetDoneTaskListByEmployee(wLoginUser, wStartTime, wEndTime);
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

	/**
	 * ??????????????? ?????????
	 * 
	 * @param wLoginUser
	 * @param wTask
	 * @return
	 */
	@Override
	public ServiceResult<MTCTask> MTC_SubmitTask(BMSEmployee wLoginUser, MTCTask wTask) {
		ServiceResult<MTCTask> wResult = new ServiceResult<MTCTask>();
		wResult.Result = new MTCTask();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wTask.Status == MTCStatus.Completion.getValue()
					|| (wTask.Status == 2 && wTask.InformShift == 0 && wTask.Type == 2)) {
				if (wTask.ID > 0) {
//					MTCTask wMTCTask = (MTCTask) MTCTaskDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wTask.ID, "",
//							wErrorCode);
//					wMTCTask.TargetStockID = wTask.TargetStockID;
//					wMTCTask.TargetID = wTask.TargetID;
					if (wTask.Status == 2 && wTask.InformShift == 0 && wTask.Type == 2) {
						wTask.TargetID = wTask.TargetSID;
						wTask.TargetStockID = wTask.TargetSStockID;
					}
					MTCTaskDAO.getInstance().AutoCompletTargetID(wLoginUser, wTask, wErrorCode);
				}
				wTask.Status = 5;
				wTask.StatusText = MTCStatus.Completion.getLable();
			}
			if (wTask.Status == MTCStatus.TaskReject.getValue()) {
				wTask.StatusText = MTCStatus.TaskReject.getLable();
			}
			if (wTask.TargetStockID > 0 && wTask.DepartmentID <= 0) {// ??????ID??????
				Map<Integer, LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance()
						.LFS_QueryStoreHouseList(wLoginUser).List(LFSStoreHouse.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
				if (wLFSStoreHouseList.containsKey(wTask.TargetStockID)) {
					wTask.DepartmentID = wLFSStoreHouseList.get(wTask.TargetStockID).AreaID;
				}
			}

			// ??????????????????????????????0?????????????????????????????????
			if (wTask.Status == MTCStatus.SendTask.getValue() && wTask.TargetSStockID > 0 && wTask.TargetSID <= 0) {
				List<FMCWorkspace> wAllFMCWorkspaceList = FMCServiceImpl.getInstance()
						.FMC_QueryWorkspaceList(wLoginUser).List(FMCWorkspace.class);
				List<FMCWorkspace> wList = wAllFMCWorkspaceList.stream().filter(p -> p.ParentID == wTask.TargetSStockID)
						.collect(Collectors.toList());

				FMCWorkspace wFMCWorkspace = new FMCWorkspace();
				wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wTask.TargetSStockName, wList.size() + 1);
				wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}", String.format("%03d", wTask.TargetSStockID),
						String.format("%04d", wList.size() + 1));
				wFMCWorkspace.Active = 1;
				wFMCWorkspace.CreatorID = wLoginUser.ID;
				wFMCWorkspace.CreateTime = Calendar.getInstance();
				wFMCWorkspace.ParentID = wTask.TargetSStockID;
				wFMCWorkspace.PlaceType = 2;
				wFMCWorkspace = FMCServiceImpl.getInstance().FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
						.Info(FMCWorkspace.class);
				wTask.TargetSID = wFMCWorkspace.ID;
			}

			wResult.Result = (MTCTask) MTCTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wTask, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<FPCProduct> MTC_GetProductByPartNo(BMSEmployee wLoginUser, String wPartNo) {
		ServiceResult<FPCProduct> wResult = new ServiceResult<FPCProduct>();
		try {
			String[] wList = wPartNo.split("#");
			String wCarType = wList[0];
			wResult.Result = WDWConstans.GetFPCProductDic().containsKey(wCarType)
					? WDWConstans.GetFPCProductDic().get(wCarType)
					: new FPCProduct();

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MTCRealTime>> MTC_SelectRealListByStore(BMSEmployee wLoginUser,
			LFSStoreHouse wLFSStoreHouse) {
		ServiceResult<List<MTCRealTime>> wResult = new ServiceResult<List<MTCRealTime>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MTCRealTime> wResultList = new ArrayList<MTCRealTime>();
			// ??????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);
			if (wFMCWorkspaceList != null && wFMCWorkspaceList.size() > 0) {
				List<FMCWorkspace> wAllWorkspace = wFMCWorkspaceList.stream()
						.filter(p -> p.ParentID == wLFSStoreHouse.ID).collect(Collectors.toList());
				// ???????????????????????????
				if (wAllWorkspace != null && wAllWorkspace.size() > 0) {
					for (FMCWorkspace wItem : wAllWorkspace) {
						if (wItem.PartNo != null && !wItem.PartNo.isEmpty()) {

							FPCProduct wFPCProduct = MTCServiceImpl.getInstance()
									.MTC_GetProductByPartNo(wLoginUser, wItem.PartNo).getResult();
							if (wFPCProduct != null && wFPCProduct.ID > 0) {
								MTCRealTime wMTCRealTime = new MTCRealTime();
								wMTCRealTime.CarTypeName = wFPCProduct.ProductNo;
								wMTCRealTime.Length = wFPCProduct.Length;
								wMTCRealTime.PartNo = wItem.PartNo;
								wMTCRealTime.StationName = wItem.Name;
								wMTCRealTime.StockName = wLFSStoreHouse.Name;
								wResultList.add(wMTCRealTime);
							}
						}
					}
				}
			}

			wResult.Result = wResultList;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private boolean MTC_CheckOrderOnPlace(List<FMCWorkspace> wFMCWorkspaceList, OMSOrder wOMSOrder,
			Map<String, FPCProduct> wFPCProductDic) {
		boolean wResult = false;
		try {

			if (wFMCWorkspaceList == null || wFMCWorkspaceList.size() < 0)
				return wResult;

			if (wFPCProductDic == null || wFPCProductDic.size() <= 0)
				return wResult;

			for (FMCWorkspace fmcWorkspace : wFMCWorkspaceList) {
				if (fmcWorkspace.PartNo.equals(wOMSOrder.PartNo)) {
					wResult = true;
					break;
				}
				if (fmcWorkspace.ActualPartNoList == null || fmcWorkspace.ActualPartNoList.size() <= 0)
					continue;
				fmcWorkspace.ActualPartNoList.removeIf(p -> StringUtils.isEmpty(p) || p.indexOf("#") <= 0);
				if (fmcWorkspace.ActualPartNoList == null || fmcWorkspace.ActualPartNoList.size() <= 0)
					continue;

				if (fmcWorkspace.ActualPartNoList.contains(wOMSOrder.PartNo)) {
					wResult = true;
					break;
				}
				String[] wOrderPartArray = null;
				String[] wPlacePartArray = null;
				for (String wPartNoTemp : fmcWorkspace.ActualPartNoList) {
					if (wPartNoTemp.equals(wOMSOrder.PartNo)) {
						wResult = true;
						break;
					}
					wPlacePartArray = wPartNoTemp.split("#");
					wOrderPartArray = wOMSOrder.PartNo.split("#");
					if (wPlacePartArray.length != 2 || wOrderPartArray.length != 2)
						continue;

					if (!wFPCProductDic.containsKey(wPartNoTemp.split("#")[0]))
						continue;

					if (wFPCProductDic.get(wPlacePartArray[0]).PrevProductNo.equals(wOrderPartArray[0])) {
						wResult = true;
						break;
					}
				}

				if (wResult)
					break;

			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MTCTypeNo>> MTC_GetMTCTypeNoAll(BMSEmployee wLoginUser) {
		ServiceResult<List<MTCTypeNo>> wResult = new ServiceResult<List<MTCTypeNo>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<MTCTypeNo> wMTCTypeNoList = new ArrayList<MTCTypeNo>();

			// ??????????????????
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// ???????????????????????????????????????????????????????????????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			Map<Integer, List<FMCWorkspace>> wFMCWorkspaceListMap = wFMCWorkspaceList.stream()
					.collect(Collectors.groupingBy(p -> p.ParentID));

			// ????????????????????????
			List<Integer> wStatusList = new ArrayList<Integer>();
			wStatusList.add(OMSOrderStatus.EnterFactoryed.getValue());
			wStatusList.add(OMSOrderStatus.Repairing.getValue());
			wStatusList.add(OMSOrderStatus.FinishedWork.getValue());
			wStatusList.add(OMSOrderStatus.ToOutChcek.getValue());
			wStatusList.add(OMSOrderStatus.ToOutConfirm.getValue());
			List<OMSOrder> wOMSOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, wStatusList).List(OMSOrder.class);

			Integer wNumber = 1;
			// ?????????????????????????????????????????????????????????????????????
			Optional<LFSStoreHouse> wOptional = null;
			LFSStoreHouse wLFSStoreHouse = null;
			List<FMCWorkspace> wAllWorkspaceList = null;
			for (OMSOrder wOMSOrder : wOMSOrderList) {

				// ???????????????
				if (this.MTC_CheckOrderOnPlace(wFMCWorkspaceList, wOMSOrder, WDWConstans.GetFPCProductDic())) {
					continue;
				}
				wOptional = wLFSStoreHouseList.stream().filter(p -> p.Type == 2).findFirst(); // ??????????????????
				if (wOptional == null || !wOptional.isPresent()) {
					continue;
				}
				wLFSStoreHouse = wOptional.get();
				wAllWorkspaceList = wFMCWorkspaceListMap.containsKey(wLFSStoreHouse.ID)
						? wFMCWorkspaceListMap.get(wLFSStoreHouse.ID)
						: new ArrayList<FMCWorkspace>();

				if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
					boolean wIsTrue = true;
					for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
						if (wFMCWorkspace.PartNo == null || StringUtils.isEmpty(wFMCWorkspace.PartNo)) {
							wFMCWorkspace.PartNo = wOMSOrder.PartNo;
							FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
									.Info(FMCWorkspace.class);
							wIsTrue = false;
							break;
						}
					}
					if (wIsTrue) {
						FMCWorkspace wFMCWorkspace = new FMCWorkspace();
						wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
								wAllWorkspaceList.size() + wNumber);
						wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}",
								String.format("%03d", wOptional.get().getID()),
								String.format("%04d", wAllWorkspaceList.size() + wNumber));
						wFMCWorkspace.Active = 1;
						wFMCWorkspace.CreatorID = wLoginUser.ID;
						wFMCWorkspace.CreateTime = Calendar.getInstance();
						wFMCWorkspace.ParentID = wOptional.get().getID();
						wFMCWorkspace.PlaceType = 2;
						wFMCWorkspace = FMCServiceImpl.getInstance()
								.FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace).Info(FMCWorkspace.class);
						wFMCWorkspace.PartNo = wOMSOrder.PartNo;
						FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
								.Info(FMCWorkspace.class);
						wNumber++;
					}
				} else {
					FMCWorkspace wFMCWorkspace = new FMCWorkspace();
					wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
							wAllWorkspaceList.size() + wNumber);
					wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}", String.format("%03d", wOptional.get().getID()),
							String.format("%04d", wAllWorkspaceList.size() + wNumber));
					wFMCWorkspace.Active = 1;
					wFMCWorkspace.CreatorID = wLoginUser.ID;
					wFMCWorkspace.CreateTime = Calendar.getInstance();
					wFMCWorkspace.ParentID = wOptional.get().getID();
					wFMCWorkspace.PlaceType = 2;
					wFMCWorkspace = FMCServiceImpl.getInstance().FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wFMCWorkspace.PartNo = wOMSOrder.PartNo;
					FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wNumber++;
				}
			}

			wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);
			if (wFMCWorkspaceList != null && wFMCWorkspaceList.size() > 0) {
				for (FMCWorkspace wItem : wFMCWorkspaceList) {
					// ??????
					wLFSStoreHouse = new LFSStoreHouse();
					// ??????
					OMSOrder wOMSOrder = new OMSOrder();
					if (wItem.ActualPartNoList != null && wItem.ActualPartNoList.size() > 0) {
						for (String wPartNo : wItem.ActualPartNoList) {
							if (StringUtils.isNotEmpty(wPartNo) && wPartNo.contains("#")) {
								FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wPartNo).getResult();
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue()) {
									String wNewPartNo = StringUtils.Format("{0}#{1}", wFPCProduct.PrevProductNo,
											wPartNo.split("#")[1]);
									Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
											.filter(p -> p.PartNo.equals(wNewPartNo.split("-")[0])).findFirst();
									if (wOMSOptional != null && wOMSOptional.isPresent())
										wOMSOrder = wOMSOptional.get();
									else
										wOMSOrder = new OMSOrder();
								} else {
									Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
											.filter(p -> p.PartNo.equals(wPartNo.split("-")[0])).findFirst();
									if (wOMSOptional != null && wOMSOptional.isPresent())
										wOMSOrder = wOMSOptional.get();
									else
										wOMSOrder = new OMSOrder();
								}
								wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wItem.ParentID).findFirst();
								if (wOptional != null && wOptional.isPresent())
									wLFSStoreHouse = wOptional.get();

								if (wLFSStoreHouse.Type == 2)
									continue;

								MTCTypeNo wMTCTypeNo = new MTCTypeNo();
								wMTCTypeNo.ProductID = wFPCProduct.ID;
								wMTCTypeNo.PartNo = wPartNo;
								wMTCTypeNo.Number = wPartNo.split("#")[1];
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
									wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
								else
									wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
								wMTCTypeNo.Transport = wFPCProduct.TransportType;
								wMTCTypeNo.StockID = wItem.ParentID;
								wMTCTypeNo.StockName = wLFSStoreHouse.Name;
								wMTCTypeNo.StationID = wItem.ID;
								wMTCTypeNo.StationName = wItem.Name;
								wMTCTypeNo.OrderID = wOMSOrder.ID;
								wMTCTypeNo.CustomerID = wOMSOrder.BureauSectionID;
								wMTCTypeNo.CustomerName = wOMSOrder.BureauSection;
								wMTCTypeNoList.add(wMTCTypeNo);
							}
						}
					}
					if (wItem.PartNo != null && StringUtils.isNotEmpty(wItem.PartNo) && wItem.PartNo.contains("#")) {
						FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wItem.PartNo).getResult();
						if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
								|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue()) {
							String wNewPartNo = StringUtils.Format("{0}#{1}", wFPCProduct.PrevProductNo,
									wItem.PartNo.split("#")[1]);
							Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
									.filter(p -> p.PartNo.equals(wNewPartNo.split("-")[0])).findFirst();
							if (wOMSOptional != null && wOMSOptional.isPresent())
								wOMSOrder = wOMSOptional.get();
							else
								wOMSOrder = new OMSOrder();
						} else {
							Optional<OMSOrder> wOMSOptional = wOMSOrderList.stream()
									.filter(p -> p.PartNo.equals(wItem.PartNo.split("-")[0])).findFirst();
							if (wOMSOptional != null && wOMSOptional.isPresent())
								wOMSOrder = wOMSOptional.get();
							else
								wOMSOrder = new OMSOrder();
						}
						wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wItem.ParentID).findFirst();
						if (wOptional != null && wOptional.isPresent())
							wLFSStoreHouse = wOptional.get();

						MTCTypeNo wMTCTypeNo = new MTCTypeNo();
						wMTCTypeNo.ProductID = wFPCProduct.ID;
						wMTCTypeNo.OrderID = wOMSOrder.ID;
						wMTCTypeNo.PartNo = wItem.PartNo;
						wMTCTypeNo.Number = wItem.PartNo.split("#")[1];
						if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
								|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
							wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
						else
							wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
						wMTCTypeNo.Transport = wFPCProduct.TransportType;
						wMTCTypeNo.StockID = wItem.ParentID;
						wMTCTypeNo.StockName = wLFSStoreHouse.Name;
						wMTCTypeNo.StationID = wItem.ID;
						wMTCTypeNo.StationName = wItem.Name;
						wMTCTypeNo.CustomerID = wOMSOrder.BureauSectionID;
						wMTCTypeNo.CustomerName = wOMSOrder.BureauSection;
						wMTCTypeNoList.add(wMTCTypeNo);
					}
				}
			}
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(2001, 1, 1);

			// ??????????????????
			wMTCTypeNoList = new ArrayList<MTCTypeNo>(
					wMTCTypeNoList.stream().collect(Collectors.toMap(p -> p.PartNo, p -> p, (o1, o2) -> o1)).values());

			List<MTCTask> wMTCTaskList = MTCTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, null,
					StringUtils.parseList(new Integer[] { MTCStatus.TaskCancel.getValue(),
							MTCStatus.Completion.getValue(), MTCStatus.Default.getValue(),
							MTCStatus.ToSendTask.getValue(), MTCStatus.TaskReject.getValue() }),
					-1, -1, -1, -1, "", null, null, wErrorCode);

			for (MTCTask wMTCTask : wMTCTaskList) {
				if (StringUtils.isNotEmpty(wMTCTask.CarType) && StringUtils.isNotEmpty(wMTCTask.PartNo)) {
					String wPartNo = StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo);
					if (wPartNo.contains("[A]") || wPartNo.contains("[B]")) {
						String wVehicleNo;
						if (wPartNo.contains("[A]")) {
							wVehicleNo = wPartNo.replace("[A]", "");
							wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wVehicleNo));
						} else {
							wVehicleNo = wPartNo.replace("[B]", "");
							wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wVehicleNo));
						}
						wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wPartNo));
					} else {
						String wAPartNo = StringUtils.Format("{0}[A]#{1}", wMTCTask.CarType, wMTCTask.PartNo);
						String wBPartNo = StringUtils.Format("{0}[B]#{1}", wMTCTask.CarType, wMTCTask.PartNo);
						wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wPartNo));
						wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wAPartNo));
						wMTCTypeNoList.removeIf(p -> p.PartNo.equals(wBPartNo));
					}
				}
			}

			// ?????????????????????????????????????????????
			List<MTCTask> wToSendList = MTCTaskDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, 1, -1, -1,
					-1, -1, "", null, null, wErrorCode);
			// ??????????????????????????????????????????????????????????????????????????????
			if (wToSendList.size() > 0) {
				wMTCTypeNoList.removeIf(p -> wToSendList.stream()
						.anyMatch(q -> StringUtils.Format("{0}#{1}", q.CarType, q.PartNo).equals(p.PartNo)
								&& q.UpFlowID != wLoginUser.ID));
			}

			wResult.Result = wMTCTypeNoList;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> MTC_SavePartNo(BMSEmployee wLoginUser, String wPartNo) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// ???????????????????????????????????????????????????????????????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			Integer wNumber = 1;
			Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream().filter(p -> p.Type == 2).findFirst(); // ??????????????????
			if (wOptional != null && wOptional.isPresent()) {
				List<FMCWorkspace> wAllWorkspaceList = wFMCWorkspaceList.stream()
						.filter(p -> p.ParentID == wOptional.get().getID()).collect(Collectors.toList());
				if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
					boolean wIsTrue = true;
					for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
						if (wFMCWorkspace.PartNo == null || StringUtils.isEmpty(wFMCWorkspace.PartNo)) {
							wFMCWorkspace.PartNo = wPartNo;
							FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
									.Info(FMCWorkspace.class);
							wIsTrue = false;
							break;
						}
					}
					if (wIsTrue) {
						FMCWorkspace wFMCWorkspace = new FMCWorkspace();
						wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
								wAllWorkspaceList.size() + wNumber);
						wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}",
								String.format("%03d", wOptional.get().getID()),
								String.format("%04d", wAllWorkspaceList.size() + wNumber));
						wFMCWorkspace.Active = 1;
						wFMCWorkspace.CreatorID = wLoginUser.ID;
						wFMCWorkspace.CreateTime = Calendar.getInstance();
						wFMCWorkspace.ParentID = wOptional.get().getID();
						wFMCWorkspace.PlaceType = 2;
						wFMCWorkspace = FMCServiceImpl.getInstance()
								.FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace).Info(FMCWorkspace.class);
						wFMCWorkspace.PartNo = wPartNo;
						FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
								.Info(FMCWorkspace.class);
						wNumber++;
					}
				} else {
					FMCWorkspace wFMCWorkspace = new FMCWorkspace();
					wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
							wAllWorkspaceList.size() + wNumber);
					wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}", String.format("%03d", wOptional.get().getID()),
							String.format("%04d", wAllWorkspaceList.size() + wNumber));
					wFMCWorkspace.Active = 1;
					wFMCWorkspace.CreatorID = wLoginUser.ID;
					wFMCWorkspace.CreateTime = Calendar.getInstance();
					wFMCWorkspace.ParentID = wOptional.get().getID();
					wFMCWorkspace.PlaceType = 2;
					wFMCWorkspace = FMCServiceImpl.getInstance().FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wFMCWorkspace.PartNo = wPartNo;
					FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wNumber++;
				}
			} else {
				wResult.Result = "??????????????????????????????";
				return wResult;
			}

			wResult.Result = "";
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????????????????
	 * 
	 */
	@Override
	public ServiceResult<List<String>> MTC_QueryBindingBogies(BMSEmployee wLoginUser) {
		ServiceResult<List<String>> wResultList = new ServiceResult<List<String>>();
		try {
			wResultList.Result = new ArrayList<String>();
			Map<String, FMCWorkspace> wWorkspaceListMap = FMCServiceImpl.getInstance()
					.FMC_QueryWorkspaceList(wLoginUser).List(FMCWorkspace.class).stream()
					.collect(Collectors.toMap(p -> p.PartNo, p -> p, (o1, o2) -> o1));
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);
			List<FMCWorkspace> wWorkspaceListA = wFMCWorkspaceList.stream().filter(p -> p.PartNo.contains("[A]"))
					.collect(Collectors.toList());
			if (wWorkspaceListA != null && wWorkspaceListA.size() > 0) {
				for (FMCWorkspace wFMCWorkspace : wWorkspaceListA) {
					wFMCWorkspace.PartNo = wFMCWorkspace.PartNo.replace("[A]", "[B]");
					if (wWorkspaceListMap.containsKey(wFMCWorkspace.PartNo))
						continue;
					wResultList.Result.add(wFMCWorkspace.PartNo);
				}
			}

			List<FMCWorkspace> wWorkspaceListB = wFMCWorkspaceList.stream().filter(
					p -> !p.PartNo.contains("[A]") && !p.PartNo.contains("[B]") && StringUtils.isNotEmpty(p.PartNo))
					.collect(Collectors.toList());
			for (FMCWorkspace wFMCWorkspace : wWorkspaceListB) {
				if (wFMCWorkspace.ActualPartNoList != null && wFMCWorkspace.ActualPartNoList.size() == 2) {
					for (String wPartNo : wFMCWorkspace.ActualPartNoList) {
						if (wPartNo.contains("[A]") || wPartNo.contains("[B]"))
							continue;
						String[] wName = wFMCWorkspace.PartNo.split("#");
						if (wName == null || wName.length != 2)
							continue;
						wResultList.Result.add(StringUtils.Format("{0}[B]#{1}", wName[0], wName[1]));
					}
				}
			}

			// ?????????[B]????????????
			for (int i = 0; i < wResultList.Result.size(); i++) {
				String[] wStr = wResultList.Result.get(i).split("#");
				if (!wStr[0].contains("[B]")) {
					continue;
				}

				String wProductNo = wStr[0].replace("[B]", "");
				if (WDWConstans.GetFPCProductList().values().stream().anyMatch(p -> p.ProductNo.equals(wProductNo)
						&& p.TransportType == FPCProductTransport.Bottom_T.getValue())) {
					wResultList.Result.set(i, wResultList.Result.get(i).replace("[B]", ""));
				}
			}

			// ??????
			wResultList.Result = wResultList.Result.stream().distinct().collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResultList;
	}

	@Override
	public ServiceResult<String> MTC_AddPartNo(BMSEmployee wLoginUser, int wStockID, String wPartNo) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????
			List<LFSStoreHouse> wLFSStoreHouseList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// ???????????????????????????????????????????????????????????????????????????
			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			Integer wNumber = 1;
			Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wStockID).findFirst(); // ????????????
			if (wOptional != null && wOptional.isPresent()) {
				List<FMCWorkspace> wAllWorkspaceList = wFMCWorkspaceList.stream()
						.filter(p -> p.ParentID == wOptional.get().getID()).collect(Collectors.toList());
				if (wAllWorkspaceList != null && wAllWorkspaceList.size() > 0) {
					boolean wIsTrue = true;
					for (FMCWorkspace wFMCWorkspace : wAllWorkspaceList) {
						if (wFMCWorkspace.PartNo == null || StringUtils.isEmpty(wFMCWorkspace.PartNo)) {
							wFMCWorkspace.PartNo = wPartNo;
							FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
									.Info(FMCWorkspace.class);
							wIsTrue = false;
							break;
						}
					}
					if (wIsTrue) {
						FMCWorkspace wFMCWorkspace = new FMCWorkspace();
						wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
								wAllWorkspaceList.size() + wNumber);
						wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}",
								String.format("%03d", wOptional.get().getID()),
								String.format("%04d", wAllWorkspaceList.size() + wNumber));
						wFMCWorkspace.Active = 1;
						wFMCWorkspace.CreatorID = wLoginUser.ID;
						wFMCWorkspace.CreateTime = Calendar.getInstance();
						wFMCWorkspace.ParentID = wOptional.get().getID();
						wFMCWorkspace.PlaceType = 2;
						wFMCWorkspace = FMCServiceImpl.getInstance()
								.FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace).Info(FMCWorkspace.class);
						wFMCWorkspace.PartNo = wPartNo;
						FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
								.Info(FMCWorkspace.class);
						wNumber++;
					}
				} else {
					FMCWorkspace wFMCWorkspace = new FMCWorkspace();
					wFMCWorkspace.Name = StringUtils.Format("{0}????????????{1}", wOptional.get().getName(),
							wAllWorkspaceList.size() + wNumber);
					wFMCWorkspace.Code = StringUtils.Format("WP-{0}{1}", String.format("%03d", wOptional.get().getID()),
							String.format("%04d", wAllWorkspaceList.size() + wNumber));
					wFMCWorkspace.Active = 1;
					wFMCWorkspace.CreatorID = wLoginUser.ID;
					wFMCWorkspace.CreateTime = Calendar.getInstance();
					wFMCWorkspace.ParentID = wOptional.get().getID();
					wFMCWorkspace.PlaceType = 2;
					wFMCWorkspace = FMCServiceImpl.getInstance().FMC_UpdateFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wFMCWorkspace.PartNo = wPartNo;
					FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wFMCWorkspace)
							.Info(FMCWorkspace.class);
					wNumber++;
				}
			} else {
				wResult.Result = "???????????????????????????";
				return wResult;
			}
			wResult.Result = "";
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MTC_SendForcas(BMSEmployee wLoginUser, Map<String, Object> wParam) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			String wUri = "http://10.200.3.16:8080/api/api/runtime/form/start_workflow";
			RemoteInvokeUtils.getInstance().HttpInvokeAPI("", "", wUri, wParam, HttpMethod.POST);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MTC_UpdateWorkSpace(BMSEmployee wLoginUser, Map<String, Object> wParam) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			int wTargetID = StringUtils.parseInt(wParam.get("TargetID"));
			String wPartNo = StringUtils.parseString(wParam.get("PartNo"));

			if (wTargetID <= 0) {
				wResult.FaultCode += "????????????????????????????????????!";
				return wResult;
			}

			List<OMSOrder> wOrderList = APSLOCOServiceImpl.getInstance()
					.OMS_QueryOrderListByStatus(wLoginUser, new ArrayList<Integer>()).List(OMSOrder.class);
			if (!wOrderList.stream().anyMatch(p -> p.PartNo.equals(wPartNo))) {
				wResult.FaultCode += "????????????????????????!";
				return wResult;
			}

			List<FMCWorkspace> wFMCWorkspaceList = FMCServiceImpl.getInstance().FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			FMCWorkspace wWorkStock = new FMCWorkspace();
			Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream().filter(p -> p.ID == wTargetID).findFirst();
			if (wOptional != null && wOptional.isPresent())
				wWorkStock = wOptional.get();
			// ??????ID??????????????????????????????????????????
			if (wWorkStock != null && wWorkStock.ID > 0) {
				// ??????????????????????????????
				wWorkStock.PartNo = wPartNo;
				FMCServiceImpl.getInstance().FMC_BindFMCWorkspace(BaseDAO.SysAdmin, wWorkStock);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> MTC_QueryLeaderByStoreHouse(BMSEmployee wLoginUser, int wStoreHouseID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<LFSStoreHouse> wSHList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			if (wSHList == null || wSHList.size() <= 0 || !wSHList.stream().anyMatch(p -> p.ID == wStoreHouseID)) {
				return wResult;
			}

			LFSStoreHouse wSH = wSHList.stream().filter(p -> p.ID == wStoreHouseID).findFirst().get();
			if (wSH.AreaID <= 0) {
				return wResult;
			}

			List<LFSWorkAreaChecker> wCheckList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, wSH.AreaID).List(LFSWorkAreaChecker.class);
			if (wCheckList == null || wCheckList.size() <= 0) {
				return wResult;
			}

			wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
					.filter(p -> wCheckList.stream()
							.anyMatch(q -> (q.ScheduleIDList != null && q.ScheduleIDList.size() > 0
									&& q.ScheduleIDList.stream().anyMatch(r -> r == p.ID))
									|| (q.LeaderIDList != null && q.LeaderIDList.size() > 0
											&& q.LeaderIDList.stream().anyMatch(r -> r == p.ID))))
					.collect(Collectors.toList());

			// ?????????????????????
			List<LFSWorkAreaStation> wWSList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaStationList(wLoginUser, wSH.AreaID).List(LFSWorkAreaStation.class);
			wWSList = wWSList.stream().filter(p -> p.WorkAreaID == wSH.AreaID).collect(Collectors.toList());
			if (wWSList.size() > 0) {
				List<Integer> wStationIDList = wWSList.stream().map(p -> p.StationID).distinct()
						.collect(Collectors.toList());
				List<Integer> wUserIDList = MTCTaskDAO.getInstance().SelectMonitorListByStationIDList(wLoginUser,
						wStationIDList, wErrorCode);
				List<BMSEmployee> wMonitorList = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wUserIDList.stream().anyMatch(q -> q == p.ID)).collect(Collectors.toList());
				wResult.Result.addAll(wMonitorList);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MCT_UpdateSectionInfo(BMSEmployee wLoginUser, MTCSectionInfo wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = MTCSectionInfoDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MTCSectionInfo>> MTC_QuerySectionList(BMSEmployee wLoginUser, int wProductID) {
		ServiceResult<List<MTCSectionInfo>> wResult = new ServiceResult<List<MTCSectionInfo>>();
		wResult.Result = new ArrayList<MTCSectionInfo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<FPCProduct> wPList = FMCServiceImpl.getInstance().FPC_QueryProductList(BaseDAO.SysAdmin, 0, 0)
					.List(FPCProduct.class);
			if (wPList == null || wPList.size() <= 0 || !wPList.stream().anyMatch(p -> p.ID == wProductID)) {
				return wResult;
			}

			FPCProduct wProduct = wPList.stream().filter(p -> p.ID == wProductID).findFirst().get();

			List<MTCSectionInfo> wList = MTCSectionInfoDAO.getInstance().SelectList(wLoginUser, -1, wProductID,
					wErrorCode);

			if (wProduct.ProductCount != wList.size()) {
				// ??????????????????????????????????????????
				if (wProduct.ProductCount == 0) {
					MTCSectionInfoDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
				}
				// ????????????????????????????????????????????????
				else if (wProduct.ProductCount > wList.size()) {
					for (int i = 0; i < wProduct.ProductCount - wList.size(); i++) {
						MTCSectionInfo wMTCSectionInfo = new MTCSectionInfo();
						wMTCSectionInfo.CreateID = wLoginUser.ID;
						wMTCSectionInfo.CreateTime = Calendar.getInstance();
						wMTCSectionInfo.ID = 0;
						wMTCSectionInfo.Name = "$";
						wMTCSectionInfo.ProductID = wProductID;
						wMTCSectionInfo.ProductNo = wProduct.ProductNo;
						MTCSectionInfoDAO.getInstance().Update(wLoginUser, wMTCSectionInfo, wErrorCode);
					}
				}
				// ?????????????????????????????????????????????????????????
				else if (wProduct.ProductCount < wList.size()) {
					List<MTCSectionInfo> wDeleteList = new ArrayList<MTCSectionInfo>();
					for (int i = 0; i < wList.size(); i++) {
						if (i >= wProduct.ProductCount) {
							wDeleteList.add(wList.get(i));
						}
					}
					MTCSectionInfoDAO.getInstance().DeleteList(wLoginUser, wDeleteList, wErrorCode);
				}
				// ???????????????
				wResult.Result = MTCSectionInfoDAO.getInstance().SelectList(wLoginUser, -1, wProductID, wErrorCode);
			} else {
				wResult.Result = wList;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> MTC_PreMoveCheck(BMSEmployee wLoginUser, MTCTask wMTCTask) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wMTCTask.IsPreMove != 1) {
				return wResult;
			}

			if ((wMTCTask.Type == 2 && wMTCTask.InformShift == 0) || wMTCTask.Status == 5) {
				List<FMCWorkspace> wList = FMCServiceImpl.getInstance()
						.FMC_GetFMCWorkspaceList(wLoginUser, -1, -1, "", -1, 1).List(FMCWorkspace.class);

				if (wList.stream().anyMatch(p -> p.ID == wMTCTask.TargetID)) {
					FMCWorkspace wSpace = wList.stream().filter(p -> p.ID == wMTCTask.TargetID).findFirst().get();

					if (StringUtils.isEmpty(wSpace.PartNo)) {
						return wResult;
					}

					// ??????????????????
					FPCProduct wTargetProduct = this.MTC_GetProductByPartNo(wLoginUser,
							StringUtils.Format("{0}#{1}", wMTCTask.CarType, wMTCTask.PartNo)).getResult();
					// ??????????????????
					FPCProduct wFPCProduct = this.MTC_GetProductByPartNo(wLoginUser, wSpace.PartNo).getResult();

					if (wSpace.AlowTransType == 0) {
						wResult.Result = StringUtils.Format("???{0}???????????????????????????{1}????????????????????????!", wSpace.Name, wSpace.PartNo);
						return wResult;
					}

					switch (FPCProductTransport.getEnumType(wFPCProduct.TransportType)) {
					case Whole:// ??????
						wResult.Result = StringUtils.Format("???{0}???????????????????????????{1}????????????????????????!", wSpace.Name, wSpace.PartNo);
						return wResult;
					case Body:// ??????
						if (!(wTargetProduct.TransportType == FPCProductTransport.Bottom.getValue()
								|| wTargetProduct.TransportType == FPCProductTransport.Bottom_T.getValue())) {
							wResult.Result = StringUtils.Format("???{0}???????????????????????????{1}????????????????????????!", wSpace.Name,
									wSpace.PartNo);
							return wResult;
						}
						break;
					case Bottom_T:// ?????????
					case Bottom:// ?????????
						if (!(wTargetProduct.TransportType == FPCProductTransport.Body.getValue())) {
							wResult.Result = StringUtils.Format("???{0}???????????????????????????{1}????????????????????????!", wSpace.Name,
									wSpace.PartNo);
							return wResult;
						}
						break;
					default:
						break;
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
	public ServiceResult<List<BMSEmployee>> MCT_QueryAuditorList(BMSEmployee wLoginUser, int wPlaceID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????
			List<FMCWorkspace> wList = FMCServiceImpl.getInstance()
					.FMC_GetFMCWorkspaceList(wLoginUser, -1, -1, "", -1, 1).List(FMCWorkspace.class);
			// ?????????????????????????????????
			if (!wList.stream().anyMatch(p -> p.ID == wPlaceID)) {
				return wResult;
			}

			FMCWorkspace wWorkspace = wList.stream().filter(p -> p.ID == wPlaceID).findFirst().get();

			// ???????????????
			List<Integer> wClassIDList = wWorkspace.TeamIDList;
			// ??????????????????
			List<BMSEmployee> wEList = WDWConstans.GetBMSEmployeeList().values().stream()
					.filter(p -> wClassIDList.stream().anyMatch(q -> q == p.DepartmentID)
							&& WDWConstans.GetBMSPosition(p.Position).DutyID == 1)
					.collect(Collectors.toList());
			if (wEList != null && wEList.size() > 0) {
				wResult.Result.addAll(wEList);
			}
			// ?????????????????????????????????
			List<Integer> wAreaIDList = new ArrayList<Integer>();
			for (Integer wClassID : wClassIDList) {
				if (!WDWConstans.GetBMSDepartmentList().values().stream()
						.anyMatch(p -> p.ID == WDWConstans.GetBMSDepartment(wClassID).ParentID)) {
					continue;
				}

				BMSDepartment wArea = WDWConstans.GetBMSDepartmentList().values().stream()
						.filter(p -> p.ID == WDWConstans.GetBMSDepartment(wClassID).ParentID).findFirst().get();
				if (!wAreaIDList.stream().anyMatch(p -> p == wArea.ID)) {
					wAreaIDList.add(wArea.ID);
				}
			}
			// ??????????????????????????????
			List<LFSWorkAreaChecker> wCheckList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
			for (Integer wAreaID : wAreaIDList) {
				if (!wCheckList.stream().anyMatch(p -> p.WorkAreaID == wAreaID)) {
					continue;
				}

				LFSWorkAreaChecker wChecker = wCheckList.stream().filter(p -> p.WorkAreaID == wAreaID).findFirst()
						.get();
				List<BMSEmployee> wLList = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wChecker.LeaderIDList.stream().anyMatch(q -> q == p.ID)
								|| wChecker.ScheduleIDList.stream().anyMatch(q -> q == p.ID))
						.collect(Collectors.toList());
				if (wLList != null && wLList.size() > 0) {
					wResult.Result.addAll(wLList);
				}
			}

			if (wResult.Result.size() <= 0) {
				// ????????????????????????????????????
				wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> p.DepartmentID == 69 && WDWConstans.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				// ?????????????????????????????????????????????????????????????????????
				if (WDWConstans.GetBMSDepartmentList().values().stream()
						.anyMatch(p -> p.ID == WDWConstans.GetBMSDepartment(69).ParentID)) {
					BMSDepartment wAreaDep = WDWConstans.GetBMSDepartmentList().values().stream()
							.filter(p -> p.ID == WDWConstans.GetBMSDepartment(69).ParentID).findFirst().get();

					if (wCheckList.stream().anyMatch(p -> p.WorkAreaID == wAreaDep.ID)) {
						LFSWorkAreaChecker wItem = wCheckList.stream().filter(p -> p.WorkAreaID == wAreaDep.ID)
								.findFirst().get();
						wResult.Result.addAll(WDWConstans.GetBMSEmployeeList().values().stream()
								.filter(p -> wItem.LeaderIDList.stream().anyMatch(q -> q == p.ID)
										|| wItem.ScheduleIDList.stream().anyMatch(q -> q == p.ID))
								.collect(Collectors.toList()));
					}
				}
			}

			// ?????????
			wResult.Result = new ArrayList<BMSEmployee>(wResult.Result.stream()
					.collect(Collectors.toMap(BMSEmployee::getID, account -> account, (k1, k2) -> k2)).values());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MTC_CreateCommentMessage(BMSEmployee wLoginUser, List<Integer> wPersonIDList,
			String wMsgTigle, String wMsgContent, int wFlowType, int wTaskID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			if (wPersonIDList == null || wPersonIDList.size() <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");

			SimpleDateFormat wSDFTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String wTimeStr = wSDFTime.format(Calendar.getInstance().getTime());

			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (Integer wUserID : wPersonIDList) {
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wTaskID;
				wMessage.Title = StringUtils.Format("{0}??????", wShiftID);
				wMessage.MessageText = StringUtils.Format("???{0}???{1}??????{2}????????????-{3}",
						BPMEventModule.getEnumType(wFlowType).getLable(), wLoginUser.Name,
						WDWConstans.GetBMSEmployeeName(wUserID), wTimeStr);
				wMessage.ModuleID = wFlowType;
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
	public ServiceResult<List<MTCTask>> MTC_GetTaskListByEmployeeNew(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, int wProductID, String wPartNo, Integer wStatus) {
		ServiceResult<List<MTCTask>> wResult = new ServiceResult<List<MTCTask>>();
		wResult.Result = new ArrayList<MTCTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<MTCTask> wSendList = new ArrayList<MTCTask>();
			List<MTCTask> wToDoList = new ArrayList<MTCTask>();
			List<MTCTask> wDoneList = new ArrayList<MTCTask>();

			List<BPMTaskBase> wBaseList = MTCTaskDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
					wStartTime, wEndTime, wErrorCode);
			wSendList = CloneTool.CloneArray(wBaseList, MTCTask.class);

			wBaseList = MTCTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(), wErrorCode);
			wToDoList = CloneTool.CloneArray(wBaseList, MTCTask.class);

			wBaseList = MTCTaskDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(), wStartTime,
					wEndTime, wErrorCode);
			wDoneList = CloneTool.CloneArray(wBaseList, MTCTask.class);

			List<Integer> wIDList = new ArrayList<Integer>();

			for (MTCTask wMTCTask : wToDoList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wIDList.add(wMTCTask.ID);
				wMTCTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResult.Result.add(wMTCTask);

			}

			for (MTCTask wMTCTask : wDoneList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Handled.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			for (MTCTask wMTCTask : wSendList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Sended.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			wResult.Result.removeIf(p -> p.Status == 0);

			// ????????????
			if (wProductID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.CarTypeID == wProductID)
						.collect(Collectors.toList());
			}
			// ????????????
			if (StringUtils.isNotEmpty(wPartNo)) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartNo.equals(wPartNo))
						.collect(Collectors.toList());
			}
			// ????????????
			if (wStatus >= 0) {
				if (wStatus == 0) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status != 5 && p.Status != 21 && p.Status != 22)
							.collect(Collectors.toList());
				} else if (wStatus == 1) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status == 5 || p.Status == 21 || p.Status == 22)
							.collect(Collectors.toList());
				}
			}

			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));

			wResult.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MTCTask>> MTC_GetTaskList(BMSEmployee wLoginUser, int wProductID, String wPartNo,
			int wStatus, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<MTCTask>> wResult = new ServiceResult<List<MTCTask>>();
		wResult.Result = new ArrayList<MTCTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (wStatus) {
			case 1:
				wResult.Result
						.addAll(MTCTaskDAO.getInstance().SelectList(wLoginUser, BPMEventModule.SCMovePart.getValue(),
								-1, -1, -1, -1, StringUtils.parseListArgs(MTCStatus.Completion.getValue()), null, -1,
								-1, -1, wProductID, wPartNo, wStartTime, wEndTime, wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(MTCTaskDAO.getInstance().SelectList(wLoginUser,
						BPMEventModule.SCMovePart.getValue(), -1, -1, -1, -1, null,
						StringUtils.parseListArgs(MTCStatus.Completion.getValue(), MTCStatus.TaskCancel.getValue(),
								MTCStatus.Default.getValue(), MTCStatus.TaskReject.getValue()),
						-1, -1, -1, wProductID, wPartNo, wStartTime, wEndTime, wErrorCode));
				break;
			default:
				wResult.Result.addAll(
						MTCTaskDAO.getInstance().SelectList(wLoginUser, BPMEventModule.SCMovePart.getValue(), -1, -1,
								-1, -1, null, null, -1, -1, -1, wProductID, wPartNo, wStartTime, wEndTime, wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = MTCTaskDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
					wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof MTCTask))
					continue;
				MTCTask wMTCTask = (MTCTask) wTaskBase;
				wMTCTask.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wMTCTask.ID)
						wResult.Result.set(i, wMTCTask);
				}
			}

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MTC_JudgeClassIsSame(BMSEmployee wLoginUser, int wTargetPlaceID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ????????????????????????????????????
			if (wTargetPlaceID == 0) {
				if (wLoginUser.DepartmentID == 69) {
					wResult.Result = 1;
				} else {
					wResult.Result = 2;
				}
				return wResult;
			}

			FMCWorkspace wFMCWorkSpace = FMCServiceImpl.getInstance()
					.FMC_GetFMCWorkspace(wLoginUser, wTargetPlaceID, "").Info(FMCWorkspace.class);
			if (wFMCWorkSpace == null || wFMCWorkSpace.ID <= 0) {
				return wResult;
			}

			if (wFMCWorkSpace.TeamIDList.stream().anyMatch(p -> p == wLoginUser.DepartmentID)) {
				wResult.Result = 1;
			} else {
				wResult.Result = 2;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<String>> MTC_QueryMTCPartNoList(BMSEmployee wLoginUser) {
		ServiceResult<List<String>> wResult = new ServiceResult<List<String>>();
		wResult.Result = new ArrayList<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = MTCTaskDAO.getInstance().MTC_QueryMTCPartNoList(wLoginUser, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Object> BPM_RejectTo(BMSEmployee wLoginUser, String wTaskID, String targetActivityId,
			Object wData, String targetActivityName, int wFlowType) {
		ServiceResult<Object> wResult = new ServiceResult<Object>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ?????????bpm??????????????????
			APIResult wAPIResult = BPMServiceImpl.getInstance().BPM_RejectedSpecifiedActivity(wLoginUser, wTaskID,
					targetActivityId, wData);
			if (wAPIResult == null) {
				wResult.FaultCode += "????????????????????????BPM???????????????!";
				return wResult;
			}
			if (wAPIResult.getResultCode() != 1000) {
				wResult.FaultCode += "????????????????????????" + wAPIResult.getMsg();
				return wResult;
			}
			// ??????????????????????????????
			int wMessageID = 0;
			int wFlowID = 0;
			String wUpFlowName = "";
			String wCode = "";
			Calendar wCreateTime = Calendar.getInstance();

			switch (BPMEventModule.getEnumType(wFlowType)) {
			case QTNCR:
			case TechNCR:// ???????????????
				NCRTask wNCRTask = CloneTool.Clone(wData, NCRTask.class);
				if (wNCRTask == null || wNCRTask.ID <= 0) {
					wResult.FaultCode += "?????????data??????????????????!";
					return wResult;
				}
				wNCRTask.StatusText = "???" + targetActivityName;
				NCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wNCRTask, wErrorCode);

				// ???????????????
				wMessageID = wNCRTask.ID;
				wFlowID = wNCRTask.FlowID;
				wUpFlowName = wNCRTask.UpFlowName;
				wCode = wNCRTask.Code;
				wCreateTime = wNCRTask.CreateTime;
				break;
			case OccasionNCR:
				SFCBOMTask wSFCBOMTask = CloneTool.Clone(wData, SFCBOMTask.class);
				if (wSFCBOMTask == null || wSFCBOMTask.ID <= 0) {
					wResult.FaultCode += "?????????data??????????????????!";
					return wResult;
				}
				wSFCBOMTask.StatusText = "???" + targetActivityName;
				SFCBOMTaskDAO.getInstance().Update(wLoginUser, wSFCBOMTask, wErrorCode);

				// ???????????????
				wMessageID = wSFCBOMTask.ID;
				wFlowID = wSFCBOMTask.FlowID;
				wUpFlowName = wSFCBOMTask.UpFlowName;
				wCode = wSFCBOMTask.Code;
				wCreateTime = wSFCBOMTask.CreateTime;
				break;
			case SCRepair:
			case QTRepair:
			case TechRepair:
			case CKRepair:
				RROItemTask wRROItemTask = CloneTool.Clone(wData, RROItemTask.class);
				if (wRROItemTask == null || wRROItemTask.ID <= 0) {
					wResult.FaultCode += "?????????data??????????????????!";
					return wResult;
				}
				wRROItemTask.StatusText = "???" + targetActivityName;
				RRORepairItemDAO.getInstance().BPM_UpdateTask(wLoginUser, wRROItemTask, wErrorCode);

				// ???????????????
				wMessageID = wRROItemTask.ID;
				wFlowID = wRROItemTask.FlowID;
				wUpFlowName = wRROItemTask.UpFlowName;
				wCode = wRROItemTask.Code;
				wCreateTime = wRROItemTask.CreateTime;
				break;
			case SCNCR:
				SendNCRTask wSendNCRTask = CloneTool.Clone(wData, SendNCRTask.class);
				if (wSendNCRTask == null || wSendNCRTask.ID <= 0) {
					wResult.FaultCode += "?????????data??????????????????!";
					return wResult;
				}
				wSendNCRTask.StatusText = "???" + targetActivityName;
				SendNCRTaskDAO.getInstance().BPM_UpdateTask(wLoginUser, wSendNCRTask, wErrorCode);

				// ???????????????
				wMessageID = wSendNCRTask.ID;
				wFlowID = wSendNCRTask.FlowID;
				wUpFlowName = wSendNCRTask.UpFlowName;
				wCode = wSendNCRTask.Code;
				wCreateTime = wSendNCRTask.CreateTime;
				break;
			default:
				break;
			}

			// ?????????????????????
			List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
					.BPM_GetHistoryInstanceByID(wLoginUser, wFlowID).List(BPMActivitiHisTask.class);
			wHisList = wHisList.stream().filter(p -> p.Status == 0).collect(Collectors.toList());
			if (wHisList.size() > 0) {
				BPMActivitiHisTask wHis = wHisList.get(0);
				String[] wStrs = wHis.Assignee.split(",");

				List<BFCMessage> wBFCMessageList = new ArrayList<>();
				BFCMessage wMessage = null;
				SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
				int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
				for (String wStr : wStrs) {
					int wUserID = StringUtils.parseInt(wStr);
					// ???????????????????????????
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wMessageID;
					wMessage.StepID = StringUtils.parseInt(wHis.ID).intValue();
					wMessage.Title = StringUtils.Format("{0}", wCode);
					wMessage.MessageText = StringUtils.Format("?????????{0} ????????????{1}  ???????????????{2} ???{3}",
							new Object[] { BPMEventModule.getEnumType(wFlowType).getLable(), wUpFlowName,
									StringUtils.parseCalendarToString(wCreateTime, "yyyy-MM-dd HH:mm"), wHis.Name })
							.trim();
					wMessage.ModuleID = wFlowType;
					wMessage.ResponsorID = wUserID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0;
					wMessage.Type = BFCMessageType.Task.getValue();
					wBFCMessageList.add(wMessage);
				}
				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> MCT_QueryAuditorListByStock(BMSEmployee wLoginUser, int wTargetStockID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????????????????
			List<Integer> wAreaIDList = new ArrayList<Integer>();

			List<LFSStoreHouse> wSHList = LFSServiceImpl.getInstance().LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			if (wSHList.stream().anyMatch(p -> p.ID == wTargetStockID)) {
				int wAreaID = wSHList.stream().filter(p -> p.ID == wTargetStockID).findFirst().get().AreaID;
				if (wAreaID > 0) {
					wAreaIDList.add(wAreaID);
				}
			}

			// ??????????????????????????????
			List<LFSWorkAreaChecker> wCheckList = LFSServiceImpl.getInstance()
					.LFS_QueryWorkAreaCheckerList(wLoginUser, -1).List(LFSWorkAreaChecker.class);
			for (Integer wAreaID : wAreaIDList) {
				if (!wCheckList.stream().anyMatch(p -> p.WorkAreaID == wAreaID)) {
					continue;
				}

				LFSWorkAreaChecker wChecker = wCheckList.stream().filter(p -> p.WorkAreaID == wAreaID).findFirst()
						.get();
				List<BMSEmployee> wLList = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> wChecker.LeaderIDList.stream().anyMatch(q -> q == p.ID)
								|| wChecker.ScheduleIDList.stream().anyMatch(q -> q == p.ID))
						.collect(Collectors.toList());
				if (wLList != null && wLList.size() > 0) {
					wResult.Result.addAll(wLList);
				}
			}

			if (wResult.Result.size() <= 0) {
				// ????????????????????????????????????
				wResult.Result = WDWConstans.GetBMSEmployeeList().values().stream()
						.filter(p -> p.DepartmentID == 69 && WDWConstans.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				// ?????????????????????????????????????????????????????????????????????
				if (WDWConstans.GetBMSDepartmentList().values().stream()
						.anyMatch(p -> p.ID == WDWConstans.GetBMSDepartment(69).ParentID)) {
					BMSDepartment wAreaDep = WDWConstans.GetBMSDepartmentList().values().stream()
							.filter(p -> p.ID == WDWConstans.GetBMSDepartment(69).ParentID).findFirst().get();

					if (wCheckList.stream().anyMatch(p -> p.WorkAreaID == wAreaDep.ID)) {
						LFSWorkAreaChecker wItem = wCheckList.stream().filter(p -> p.WorkAreaID == wAreaDep.ID)
								.findFirst().get();
						wResult.Result.addAll(WDWConstans.GetBMSEmployeeList().values().stream()
								.filter(p -> wItem.LeaderIDList.stream().anyMatch(q -> q == p.ID)
										|| wItem.ScheduleIDList.stream().anyMatch(q -> q == p.ID))
								.collect(Collectors.toList()));
					}
				}
			}

			// ?????????
			wResult.Result = new ArrayList<BMSEmployee>(wResult.Result.stream()
					.collect(Collectors.toMap(BMSEmployee::getID, account -> account, (k1, k2) -> k2)).values());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
