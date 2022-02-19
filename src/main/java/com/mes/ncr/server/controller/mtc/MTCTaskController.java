package com.mes.ncr.server.controller.mtc;

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
import org.springframework.web.bind.annotation.RestController;
import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.APSLOCOService;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.FMCService;
import com.mes.ncr.server.service.LFSService;
import com.mes.ncr.server.service.MTCService;
import com.mes.ncr.server.service.mesenum.FPCProductTransport;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.TagTypes;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.lfs.LFSStoreHouse;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.mtc.MTCTypeNo;
import com.mes.ncr.server.service.po.ncr.NCRStatus;
import com.mes.ncr.server.service.utils.CloneTool;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.RetCode;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2021-1-11 09:39:17
 */
@RestController
@RequestMapping("/api/MTC")
public class MTCTaskController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(MTCTaskController.class);

	@Autowired
	MTCService wMTCService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	LFSService wLFSService;
	@Autowired
	FMCService wFMCService;
	@Autowired
	APSLOCOService wAPSLOCOService;

	/**
	 * 获取所有可进行绑定的转向架车号列表
	 */
	@GetMapping("/QueryBindingBogies")
	public Object QueryBindingBogies(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<String>> wServiceResult = wMTCService.MTC_QueryBindingBogies(wLoginUser);

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
	 * 拿到所有可移车车号
	 */
	@GetMapping("/QueryPartNoList")
	public Object QueryPartNoList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<MTCTypeNo>> wServiceResult = wMTCService.MTC_GetMTCTypeNoAll(wLoginUser);
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
	 * 通过车号与库位ID查询可移车库位
	 */
	@PostMapping("/QueryPartNo")
	public Object QueryPartNo(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wStockID = CloneTool.Clone(wParam.get("StockID"), Integer.class);
			int wTargetStockID = CloneTool.Clone(wParam.get("TargetStockID"), Integer.class);
			String wPratNo = CloneTool.Clone(wParam.get("PartNo"), String.class);
			int wIsPreMove = wParam.containsKey("IsPreMove") ? StringUtils.parseInt(wParam.get("IsPreMove")) : 0;

			if (wStockID <= 0 || !wPratNo.contains("#")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误！");
				return wResult;
			}

			// 查询可移车库位列表
			ServiceResult<List<LFSStoreHouse>> wServiceResult = wMTCService.Get_StoreHouseList(wLoginUser, wPratNo,
					wIsPreMove, wStockID);

			LFSStoreHouse wLFSStoreHouse = new LFSStoreHouse();
			// 获取库位列表
			List<LFSStoreHouse> wLFSStoreHouseList = wLFSService.LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			if (wLFSStoreHouseList != null && wLFSStoreHouseList.size() > 0) {
				Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wStockID)
						.findFirst();
				if (wOptional != null && wOptional.isPresent())
					wLFSStoreHouse = wOptional.get();
			}
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0 && wLFSStoreHouse.ID > 0
					&& wLFSStoreHouse.MoveStoreHouseIDList != null && wLFSStoreHouse.MoveStoreHouseIDList.size() > 0) {
				for (LFSStoreHouse wItem : wServiceResult.Result) {
					if (wLFSStoreHouse.MoveStoreHouseIDList.contains(wItem.ID) || wLFSStoreHouse.ID == wItem.ID)
						wItem.AsMove = 1;
				}
			} else {
				if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
					for (LFSStoreHouse wItem : wServiceResult.Result) {
						if (wLFSStoreHouse.ID == wItem.ID)
							wItem.AsMove = 1;
					}
				}
			}
			if (wTargetStockID > 0) {
				Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wTargetStockID)
						.findFirst();
				if (wOptional != null && wOptional.isPresent())
					wLFSStoreHouse = wOptional.get();
				if (wLFSStoreHouse.ID > 0 && wLFSStoreHouse.AreaID > 0) {
					int wAreaID = wLFSStoreHouse.AreaID;
					wServiceResult.Result = wServiceResult.Result.stream().filter(p -> p.AreaID == wAreaID)
							.collect(Collectors.toList());
					wServiceResult.Result.add(wLFSStoreHouse);
				}
			}
			// 去重
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors
										.toCollection(() -> new TreeSet<>(Comparator.comparing(LFSStoreHouse::getID))),
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
	 * 通过目标库位ID与车号查询该库位下可移车台位列表(TargetID大于0时为目标台位)
	 */
	@PostMapping("/QueryMoveByStockID")
	public Object QueryMoveByStockID(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wStockID = CloneTool.Clone(wParam.get("StockID"), Integer.class);
			String wPratNo = CloneTool.Clone(wParam.get("PartNo"), String.class);
			int wTargetID = CloneTool.Clone(wParam.get("TargetID"), Integer.class);
			int wIsPreMove = wParam.containsKey("IsPreMove") ? StringUtils.parseInt(wParam.get("IsPreMove")) : 0;

			if (wStockID <= 0 || !wPratNo.contains("#")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误！");
				return wResult;
			}

			// 查询该库位下可移车台位列表
			ServiceResult<List<FMCWorkspace>> wServiceResult = wMTCService.Get_WorkspaceList(wLoginUser, wStockID,
					wPratNo, wTargetID, wIsPreMove);

			// 去重
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
				wServiceResult.Result = wServiceResult.Result.stream()
						.collect(Collectors.collectingAndThen(
								Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FMCWorkspace::getID))),
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
	 * 通过库位台位ID查询可进行直移车辆列表
	 */
	@GetMapping("/QueryByStationID")
	public Object QueryByStationID(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wStockID = StringUtils.parseInt(request.getParameter("StockID"));
			int wPlaceID = StringUtils.parseInt(request.getParameter("PlaceID"));
			// 返回可移车车辆列表
			List<MTCTypeNo> wMTCTypeNoList = new ArrayList<MTCTypeNo>();
			// 当前库位
			LFSStoreHouse wNewStoreHouse = new LFSStoreHouse();
			List<FPCProduct> wFPCProductList = wFMCService.FPC_QueryProductList(wLoginUser, 0, 0)
					.List(FPCProduct.class);
			Map<String, FPCProduct> wFPCProductMap = new HashMap<String, FPCProduct>();
			if (wFPCProductList != null && wFPCProductList.size() > 0) {
				wFPCProductMap = wFPCProductList.stream().filter(p -> p.Active == 1)
						.collect(Collectors.toMap(p -> p.ProductNo, p -> p, (o1, o2) -> o1));
			}
			// 获取库位列表
			List<LFSStoreHouse> wLFSStoreHouseList = wLFSService.LFS_QueryStoreHouseList(wLoginUser)
					.List(LFSStoreHouse.class);
			// 获取台位列表
			List<FMCWorkspace> wFMCWorkspaceList = wFMCService.FMC_QueryWorkspaceList(wLoginUser)
					.List(FMCWorkspace.class);

			List<Integer> wStockList = new ArrayList<Integer>();
			// 初始台位
			FMCWorkspace wFMCWorkspace = new FMCWorkspace();
			Optional<FMCWorkspace> wOpWorkspace = wFMCWorkspaceList.stream().filter(p -> p.ID == wPlaceID).findFirst();
			if (wOpWorkspace.isPresent() && wOpWorkspace.get() != null && wOpWorkspace.get().ID > 0) {
				wFMCWorkspace = wOpWorkspace.get();
			}
			if (wStockID <= 0 && wPlaceID > 0) {

				Optional<FMCWorkspace> wOptional = wFMCWorkspaceList.stream().filter(p -> p.ID == wPlaceID).findFirst();
				if (wOptional.isPresent() && wOptional.get() != null && wOptional.get().ID > 0) {
					wStockID = wOptional.get().ParentID;

				} else {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "台位不存在");
					return wResult;
				}

			}
			wStockList.add(wStockID);
			int wStockIDFinal = wStockID;
			Optional<LFSStoreHouse> wStoreOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wStockIDFinal)
					.findFirst();
			if (wStoreOptional != null && wStoreOptional.isPresent())
				wNewStoreHouse = wStoreOptional.get();
			// 查询直达库位下所有列表，并判断直达库位车辆是否能移出

			if (wNewStoreHouse.MoveStoreHouseIDList != null && wNewStoreHouse.MoveStoreHouseIDList.size() > 0) {
				wStockList.addAll(wNewStoreHouse.MoveStoreHouseIDList);
				wStockList = wStockList.stream().distinct().collect(Collectors.toList());
			}
			Map<Object, List<FMCWorkspace>> wFMCWorkspaceListMap = wFMCWorkspaceList.stream()
					.collect(Collectors.groupingBy(p -> p.ParentID));
			// 判断选择台位上是否有车辆
			if (!StringUtils.isEmpty(wFMCWorkspace.PartNo)) {
				String[] wLastList = wFMCWorkspace.PartNo.split("#");
				if (wLastList == null || wLastList.length != 2)
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "未查找到该车型");
				FPCProduct wLastProduct = wFPCProductMap.get(wLastList[0]);
				if (wLastProduct.TransportType == FPCProductTransport.Whole.getValue())// 选择台位上为整车时无法进行移车
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "无可移台车");
			}
			for (Integer wInteger : wStockList) {
				Optional<LFSStoreHouse> wOptional = wLFSStoreHouseList.stream().filter(p -> p.ID == wInteger)
						.findFirst();
				if (wOptional == null || !wOptional.isPresent()) {
					continue;
				}

				if (!wFMCWorkspaceListMap.containsKey(wInteger))
					continue;

				List<FMCWorkspace> wAllFMCWorkspace = wFMCWorkspaceListMap.get(wInteger);

				for (FMCWorkspace wItem : wAllFMCWorkspace) {
					// 台位
					if (wItem.ID == wPlaceID)
						continue;
					if (StringUtils.isEmpty(wItem.PartNo)) {
						continue;
					}
					if (StringUtils.isEmpty(wFMCWorkspace.PartNo)) // 需绑定台位车号为空
					{
						if (wItem.ActualPartNoList != null && wItem.ActualPartNoList.size() > 0) {
							for (String wPartNo : wItem.ActualPartNoList) {
								if (StringUtils.isEmpty(wPartNo) || wFPCProductMap.containsKey(wPartNo)) {
									continue;
								}
								String[] wList = wPartNo.split("#");
								if (wList == null || wList.length != 2)
									continue;
								FPCProduct wFPCProduct = wFPCProductMap.get(wList[0]);
								if (wFPCProduct == null)
									continue;

								MTCTypeNo wMTCTypeNo = new MTCTypeNo();
								wMTCTypeNo.PartNo = wPartNo;
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
									wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
								else
									wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
								wMTCTypeNo.Transport = wFPCProduct.TransportType;
								wMTCTypeNo.StockID = wItem.ParentID;
								wMTCTypeNo.StockName = "";
								wMTCTypeNo.StationID = wItem.ID;
								wMTCTypeNo.StationName = wItem.Name;
								wMTCTypeNoList.add(wMTCTypeNo);

							}
						}
						if (StringUtils.isEmpty(wItem.PartNo) || wFPCProductMap.containsKey(wItem.PartNo)) {
							continue;
						}
						String[] wList = wItem.PartNo.split("#");
						if (wList == null || wList.length != 2)
							continue;

						FPCProduct wProduct = new FPCProduct();
						Optional<FPCProduct> wOPProduct = wFPCProductList.stream()
								.filter(p -> p.ProductNo.equals(wList[0])).findFirst();
						if (wOPProduct != null && wOPProduct.isPresent())
							wProduct = wOPProduct.get();
						if (wProduct.ID <= 0)
							continue;

						MTCTypeNo wMTCTypeNo = new MTCTypeNo();
						wMTCTypeNo.PartNo = wItem.PartNo;
						if (wProduct.TransportType == FPCProductTransport.Body.getValue()
								|| wProduct.TransportType == FPCProductTransport.Bottom.getValue())
							wMTCTypeNo.ProductNo = wProduct.PrevProductNo;
						else
							wMTCTypeNo.ProductNo = wProduct.ProductNo;
						wMTCTypeNo.Transport = wProduct.TransportType;
						wMTCTypeNo.StockID = wItem.ParentID;
						wMTCTypeNo.StockName = "";
						wMTCTypeNo.StationID = wItem.ID;
						wMTCTypeNo.StationName = wItem.Name;
						wMTCTypeNoList.add(wMTCTypeNo);
					} else // 需绑定台位车号不为空
					{
						String[] wList = wFMCWorkspace.PartNo.split("#");
						if (wList == null || wList.length != 2)
							continue;
						FPCProduct wFPCProduct = wFPCProductMap.get(wList[0]);
						switch (FPCProductTransport.getEnumType(wFPCProduct.TransportType)) {
						case Body: // 车体
							String[] wSonList = wItem.PartNo.split("#");
							if (wSonList == null || wSonList.length != 2)
								continue;
							FPCProduct wSonProduct = wFPCProductMap.get(wSonList[0]);
							switch (FPCProductTransport.getEnumType(wSonProduct.TransportType)) {
							case Bottom:// 车底
								if (wList[1].equals(wSonList[1])) {
									MTCTypeNo wMTCTypeNo = new MTCTypeNo();
									wMTCTypeNo.PartNo = wItem.PartNo;
									if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
											|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
										wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
									else
										wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
									wMTCTypeNo.Transport = wSonProduct.TransportType;
									wMTCTypeNo.StockID = wItem.ParentID;
									wMTCTypeNo.StockName = "";
									wMTCTypeNo.StationID = wItem.ID;
									wMTCTypeNo.StationName = wItem.Name;
									wMTCTypeNoList.add(wMTCTypeNo);
								}
								break;
							case Bottom_T:// 假台车
								MTCTypeNo wMTCTypeNo = new MTCTypeNo();
								wMTCTypeNo.PartNo = wItem.PartNo;
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
									wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
								else
									wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
								wMTCTypeNo.Transport = wSonProduct.TransportType;
								wMTCTypeNo.StockID = wItem.ParentID;
								wMTCTypeNo.StockName = "";
								wMTCTypeNo.StationID = wItem.ID;
								wMTCTypeNo.StationName = wItem.Name;
								wMTCTypeNoList.add(wMTCTypeNo);

								break;
							default:
								break;
							}
							break;
						case Bottom:// 车底
							String[] wBottomList = wItem.PartNo.split("#");
							if (wBottomList == null || wBottomList.length != 2)
								continue;
							FPCProduct wBottomProduct = wFPCProductMap.get(wBottomList[0]);
							if (wBottomProduct.TransportType == FPCProductTransport.Body.getValue()) {
								if (wList[1].equals(wBottomList[1])) {
									MTCTypeNo wMTCTypeNo = new MTCTypeNo();
									wMTCTypeNo.PartNo = wItem.PartNo;
									if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
											|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
										wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
									else
										wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
									wMTCTypeNo.Transport = wBottomProduct.TransportType;
									wMTCTypeNo.StockID = wItem.ParentID;
									wMTCTypeNo.StockName = "";
									wMTCTypeNo.StationID = wItem.ID;
									wMTCTypeNo.StationName = wItem.Name;
									wMTCTypeNoList.add(wMTCTypeNo);
								}
							}
							break;
						case Bottom_T:// 假台车
							String[] wBottom_TList = wItem.PartNo.split("#");
							if (wBottom_TList == null || wBottom_TList.length != 2)
								continue;
							FPCProduct wBottom_TProduct = wFPCProductMap.get(wBottom_TList[0]);
							if (wBottom_TProduct.TransportType == FPCProductTransport.Body.getValue()) {
								MTCTypeNo wMTCTypeNo = new MTCTypeNo();
								wMTCTypeNo.PartNo = wItem.PartNo;
								if (wFPCProduct.TransportType == FPCProductTransport.Body.getValue()
										|| wFPCProduct.TransportType == FPCProductTransport.Bottom.getValue())
									wMTCTypeNo.ProductNo = wFPCProduct.PrevProductNo;
								else
									wMTCTypeNo.ProductNo = wFPCProduct.ProductNo;
								wMTCTypeNo.Transport = wBottom_TProduct.TransportType;
								wMTCTypeNo.StockID = wItem.ParentID;
								wMTCTypeNo.StockName = "";
								wMTCTypeNo.StationID = wItem.ID;
								wMTCTypeNo.StationName = wItem.Name;
								wMTCTypeNoList.add(wMTCTypeNo);
							}
							break;
						case Whole: // 整车
						default:
							break;
						}
					}
				}
			}
			// 去重
			if (wMTCTypeNoList != null && wMTCTypeNoList.size() > 0) {
				wMTCTypeNoList = wMTCTypeNoList.stream()
						.collect(Collectors.toMap(p -> p.PartNo, p -> p, (o1, o2) -> o1)).values().stream()
						.collect(Collectors.toList());
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wMTCTypeNoList, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "无可移台车");
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

			int wTagType = StringUtils.parseInt(request.getParameter("TagTypes"));

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

			ServiceResult<List<BPMTaskBase>> wServiceResult = wMTCService.MTC_GetTaskListByEmployee(wBMSEmployee,
					TagTypes.getEnumType(wTagType), wStartTime, wEndTime);
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
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
	 * 用人拿任务(待办、已办、发起)移车任务
	 */
	@GetMapping("/EmployeeAllNew")
	public Object EmployeeAllNew(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wBMSEmployee = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			Integer wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<MTCTask>> wServiceResult = wMTCService.MTC_GetTaskListByEmployeeNew(wBMSEmployee,
					wStartTime, wEndTime, wProductID, wPartNo, wStatus);

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
	 * 整体列表
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

			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<MTCTask>> wServiceResult = wMTCService.MTC_GetTaskList(wBMSEmployee, wProductID, wPartNo,
					wStatus, wStartTime, wEndTime);

			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
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
	 * 查单条、并返回所有操作记录
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

			ServiceResult<MTCTask> wServiceResult = wMTCService.MTC_GetTask(wLoginUser, wID, "");

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.getResult());
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null,
						wServiceResult.getResult());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 将车辆绑定至厂线上
	 */
	@PostMapping("/SavePartNo")
	public Object SavePartNo(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			String wPratNo = CloneTool.Clone(wParam.get("PartNo"), String.class);
			ServiceResult<String> wServiceResult = wMTCService.MTC_SavePartNo(wLoginUser, wPratNo);
			if (StringUtils.isEmpty(wServiceResult.Result)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.Result, null, wServiceResult.Result);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 库位下新增平车或假台车
	 */
	@PostMapping("/AddTrolley")
	public Object AddPartNo(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);
			int wStockID = CloneTool.Clone(wParam.get("StockID"), Integer.class);
			String wPratNo = CloneTool.Clone(wParam.get("PartNo"), String.class);
			ServiceResult<String> wServiceResult = wMTCService.MTC_AddPartNo(wLoginUser, wStockID, wPratNo);
			if (StringUtils.isEmpty(wServiceResult.Result)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.Result, null, wServiceResult.Result);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * focas移车发起
	 */
	@PostMapping("/SendForcas")
	public Object SendForcas(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			ServiceResult<Integer> wServiceResult = wMTCService.MTC_SendForcas(wLoginUser, wParam);

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
	 * 更新台位数据
	 */
	@PostMapping("/UpdateWorkSpace")
	public Object UpdateWorkSpace(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wMTCService.MTC_UpdateWorkSpace(wLoginUser, wParam);

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
	 * 通过库位获取工区主管
	 */
	@GetMapping("/LeaderByStoreHouse")
	public Object LeaderByStoreHouse(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wStoreHouseID = StringUtils.parseInt(request.getParameter("StoreHouseID"));

			ServiceResult<List<BMSEmployee>> wServiceResult = wMTCService.MTC_QueryLeaderByStoreHouse(wLoginUser,
					wStoreHouseID);

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
	 * 获取台位获取班组长和工区主管和工区调度
	 */
	@GetMapping("/AuditorList")
	public Object AuditorList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPlaceID = StringUtils.parseInt(request.getParameter("PlaceID"));

			ServiceResult<List<BMSEmployee>> wServiceResult = wMTCService.MCT_QueryAuditorList(wLoginUser, wPlaceID);

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
	 * 获取台位获取班组长和工区主管和工区调度
	 */
	@GetMapping("/AuditorListByStock")
	public Object AuditorListByStock(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wTargetStockID = StringUtils.parseInt(request.getParameter("TargetStockID"));

			ServiceResult<List<BMSEmployee>> wServiceResult = wMTCService.MCT_QueryAuditorListByStock(wLoginUser, wTargetStockID);

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
	 * 判断目标台位的责任班组和登陆人所在班组是否一致
	 */
	@GetMapping("/ClassIsSame")
	public Object ClassIsSame(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wTargetPlaceID = StringUtils.parseInt(request.getParameter("TargetPlaceID"));

			ServiceResult<Integer> wServiceResult = wMTCService.MTC_JudgeClassIsSame(wLoginUser, wTargetPlaceID);

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
	 * 获取移车单所有车号列表
	 */
	@GetMapping("/MTCPartNoList")
	public Object MTCPartNoList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<String>> wServiceResult = wMTCService.MTC_QueryMTCPartNoList(wLoginUser);

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
}
