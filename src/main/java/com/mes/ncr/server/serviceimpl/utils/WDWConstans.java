package com.mes.ncr.server.serviceimpl.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.ncr.server.service.mesenum.BPMEventModule;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.po.BPMResource;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.bms.BMSDepartment;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.bms.BMSPosition;
import com.mes.ncr.server.service.po.bpm.BPMTaskBase;
import com.mes.ncr.server.service.po.crm.CRMCustomer;
import com.mes.ncr.server.service.po.fmc.FMCBusinessUnit;
import com.mes.ncr.server.service.po.fmc.FMCFactory;
import com.mes.ncr.server.service.po.fmc.FMCLine;
import com.mes.ncr.server.service.po.fmc.FMCLineUnit;
import com.mes.ncr.server.service.po.fmc.FMCStation;
import com.mes.ncr.server.service.po.fmc.FMCWorkShop;
import com.mes.ncr.server.service.po.fpc.FPCPart;
import com.mes.ncr.server.service.po.fpc.FPCPartPoint;
import com.mes.ncr.server.service.po.fpc.FPCProduct;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.service.po.rro.RROItemTask;
import com.mes.ncr.server.service.po.rro.RRORepairStatus;
import com.mes.ncr.server.service.po.rro.RROStatus;
import com.mes.ncr.server.service.po.rro.RROTask;
import com.mes.ncr.server.service.po.scm.SCMSupplier;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.serviceimpl.FMCServiceImpl;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.rro.RROTaskDAO;

public class WDWConstans {
	private static Logger logger = LoggerFactory.getLogger(WDWConstans.class);

	// region 用户全局数据
	private static Calendar RefreshEmployeeTime = Calendar.getInstance();

	private static Map<Integer, BMSEmployee> BMSEmployeeDic = new HashMap<Integer, BMSEmployee>();

	public static synchronized Map<Integer, BMSEmployee> GetBMSEmployeeList() {
		if (BMSEmployeeDic == null || BMSEmployeeDic.size() <= 0
				|| RefreshEmployeeTime.compareTo(Calendar.getInstance()) < 0) {
			List<BMSEmployee> wBMSEmployeeList = CoreServiceImpl.getInstance()
					.BMS_GetEmployeeAll(BaseDAO.SysAdmin, 0, 0, -1).List(BMSEmployee.class);
			if (wBMSEmployeeList != null && wBMSEmployeeList.size() > 0) {
				BMSEmployeeDic = wBMSEmployeeList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshEmployeeTime = Calendar.getInstance();
			RefreshEmployeeTime.add(Calendar.MINUTE, 3);
		}
		return BMSEmployeeDic;
	}

	public static BMSEmployee GetBMSEmployee(int wID) {
		BMSEmployee wResult = new BMSEmployee();
		if (WDWConstans.GetBMSEmployeeList().containsKey(wID)) {
			if (WDWConstans.GetBMSEmployeeList().get(wID) != null) {
				wResult = WDWConstans.GetBMSEmployeeList().get(wID);
			}
		}
		return wResult;
	}

	public static String GetBMSEmployeeName(int wID) {
		String wResult = "";
		if (WDWConstans.GetBMSEmployeeList().containsKey(wID)) {
			if (WDWConstans.GetBMSEmployeeList().get(wID) != null) {
				wResult = WDWConstans.GetBMSEmployeeList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static String GetBMSEmployeeName(List<Integer> wIDList) {
		String wResult = "";
		if (wIDList == null || wIDList.size() <= 0)
			return wResult;

		List<String> wNames = new ArrayList<String>();
		for (Integer integer : wIDList) {
			if (integer <= 0)
				continue;

			if (WDWConstans.GetBMSEmployeeList().containsKey(integer)) {
				if (WDWConstans.GetBMSEmployeeList().get(integer) != null) {
					wNames.add(WDWConstans.GetBMSEmployeeList().get(integer).getName());
				}
			}

		}
		wResult = StringUtils.Join(",", wNames);

		return wResult;
	}
	// endRegion

	// region 产线全局数据
	private static Calendar RefreshLineTime = Calendar.getInstance();

	private static Map<Integer, FMCLine> FMCLineDic = new HashMap<Integer, FMCLine>();

	public static synchronized Map<Integer, FMCLine> GetFMCLineList() {
		if (FMCLineDic == null || FMCLineDic.size() <= 0 || RefreshLineTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCLine> wFMCLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(BaseDAO.SysAdmin, 0, 0, 0)
					.List(FMCLine.class);
			if (wFMCLineList != null && wFMCLineList.size() > 0) {
				FMCLineDic = wFMCLineList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshLineTime = Calendar.getInstance();
			RefreshLineTime.add(Calendar.MINUTE, 3);
		}
		return FMCLineDic;
	}

	public static String GetFMCLineName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFMCLineList().containsKey(wID)) {
			if (WDWConstans.GetFMCLineList().get(wID) != null) {
				wResult = WDWConstans.GetFMCLineList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FMCLine GetFMCLine(int wID) {
		FMCLine wResult = new FMCLine();
		if (WDWConstans.GetFMCLineList().containsKey(wID)) {
			if (WDWConstans.GetFMCLineList().get(wID) != null) {
				wResult = WDWConstans.GetFMCLineList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 供应商全局数据
	private static Calendar RefreshSupplierTime = Calendar.getInstance();

	private static Map<Integer, SCMSupplier> SCMSupplierDic = new HashMap<Integer, SCMSupplier>();

	public static synchronized Map<Integer, SCMSupplier> GetSCMSupplierList() {
		if (SCMSupplierDic == null || SCMSupplierDic.size() <= 0
				|| RefreshSupplierTime.compareTo(Calendar.getInstance()) < 0) {
			List<SCMSupplier> wSCMSupplierList = CoreServiceImpl.getInstance().SCM_QuerySupplierList(BaseDAO.SysAdmin)
					.List(SCMSupplier.class);
			if (wSCMSupplierList != null && wSCMSupplierList.size() > 0) {
				SCMSupplierDic = wSCMSupplierList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshSupplierTime = Calendar.getInstance();
			RefreshSupplierTime.add(Calendar.MINUTE, 3);
		}
		return SCMSupplierDic;
	}

	public static String GetSCMSupplierName(int wID) {
		String wResult = "";
		if (WDWConstans.GetSCMSupplierList().containsKey(wID)) {
			if (WDWConstans.GetSCMSupplierList().get(wID) != null) {
				wResult = WDWConstans.GetSCMSupplierList().get(wID).getSupplierName();
			}
		}
		return wResult;
	}

	public static SCMSupplier GetSCMSupplier(int wID) {
		SCMSupplier wResult = new SCMSupplier();
		if (WDWConstans.GetSCMSupplierList().containsKey(wID)) {
			if (WDWConstans.GetSCMSupplierList().get(wID) != null) {
				wResult = WDWConstans.GetSCMSupplierList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 车间全局数据
	private static Calendar RefreshWorkShopTime = Calendar.getInstance();

	private static Map<Integer, FMCWorkShop> FMCWorkShopDic = new HashMap<Integer, FMCWorkShop>();

	public static synchronized Map<Integer, FMCWorkShop> GetFMCWorkShopList() {
		if (FMCWorkShopDic == null || FMCWorkShopDic.size() <= 0
				|| RefreshWorkShopTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCWorkShop> wFMCWorkShopList = FMCServiceImpl.getInstance()
					.FMC_QueryWorkShopList(BaseDAO.SysAdmin, 0, 0).List(FMCWorkShop.class);
			if (wFMCWorkShopList != null && wFMCWorkShopList.size() > 0) {
				FMCWorkShopDic = wFMCWorkShopList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshWorkShopTime = Calendar.getInstance();
			RefreshWorkShopTime.add(Calendar.MINUTE, 3);
		}
		return FMCWorkShopDic;
	}

	public static String GetFMCWorkShopName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFMCWorkShopList().containsKey(wID)) {
			if (WDWConstans.GetFMCWorkShopList().get(wID) != null) {
				wResult = WDWConstans.GetFMCWorkShopList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FMCWorkShop GetFMCWorkShop(int wID) {
		FMCWorkShop wResult = new FMCWorkShop();
		if (WDWConstans.GetFMCWorkShopList().containsKey(wID)) {
			if (WDWConstans.GetFMCWorkShopList().get(wID) != null) {
				wResult = WDWConstans.GetFMCWorkShopList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 工段全局数据
	private static Calendar RefreshPartTime = Calendar.getInstance();

	private static Map<Integer, FPCPart> FPCPartDic = new HashMap<Integer, FPCPart>();

	public static synchronized Map<Integer, FPCPart> GetFPCPartList() {
		if (FPCPartDic == null || FPCPartDic.size() <= 0 || RefreshPartTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCPart> wFPCPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(BaseDAO.SysAdmin, 0, 0, 0)
					.List(FPCPart.class);
			if (wFPCPartList != null && wFPCPartList.size() > 0) {
				FPCPartDic = wFPCPartList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshPartTime = Calendar.getInstance();
			RefreshPartTime.add(Calendar.MINUTE, 3);
		}
		return FPCPartDic;
	}

	public static FPCPart GetFPCPart(int wID) {
		FPCPart wResult = new FPCPart();
		if (WDWConstans.GetFPCPartList().containsKey(wID)) {
			if (WDWConstans.GetFPCPartList().get(wID) != null) {
				wResult = WDWConstans.GetFPCPartList().get(wID);
			}
		}
		return wResult;
	}

	public static String GetFPCPartName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFPCPartList().containsKey(wID)) {
			if (WDWConstans.GetFPCPartList().get(wID) != null) {
				wResult = WDWConstans.GetFPCPartList().get(wID).getName();
			}
		}
		return wResult;
	}

	// endRegion

	// region 工序全局数据
	private static Calendar RefreshStepTime = Calendar.getInstance();

	private static Map<Integer, FPCPartPoint> FPCStepDic = new HashMap<Integer, FPCPartPoint>();

	public static synchronized Map<Integer, FPCPartPoint> GetFPCStepList() {
		if (FPCStepDic == null || FPCStepDic.size() <= 0 || RefreshStepTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCPartPoint> wFPCStepList = FMCServiceImpl.getInstance()
					.FPC_QueryPartPointList(BaseDAO.SysAdmin, 0, 0, 0).List(FPCPartPoint.class);
			if (wFPCStepList != null && wFPCStepList.size() > 0) {
				FPCStepDic = wFPCStepList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshStepTime = Calendar.getInstance();
			RefreshStepTime.add(Calendar.MINUTE, 3);
		}
		return FPCStepDic;
	}

	public static String GetFPCStepName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFPCStepList().containsKey(wID)) {
			if (WDWConstans.GetFPCStepList().get(wID) != null) {
				wResult = WDWConstans.GetFPCStepList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FPCPartPoint GetFPCStep(int wID) {
		FPCPartPoint wResult = new FPCPartPoint();
		if (WDWConstans.GetFPCStepList().containsKey(wID)) {
			if (WDWConstans.GetFPCStepList().get(wID) != null) {
				wResult = WDWConstans.GetFPCStepList().get(wID);
			}
		}
		return wResult;
	}
	// endRegion

	// region 工位全局数据
	private static Calendar RefreshStationTime = Calendar.getInstance();

	private static Map<Integer, FMCStation> FMCStationDic = new HashMap<Integer, FMCStation>();

	public static synchronized Map<Integer, FMCStation> GetFMCStationList() {
		if (FMCStationDic == null || FMCStationDic.size() <= 0
				|| RefreshStationTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCStation> wFMCStationList = FMCServiceImpl.getInstance().FMC_QueryStationList(BaseDAO.SysAdmin, 0, 0)
					.List(FMCStation.class);
			if (wFMCStationList != null && wFMCStationList.size() > 0) {
				FMCStationDic = wFMCStationList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshStationTime = Calendar.getInstance();
			RefreshStationTime.add(Calendar.MINUTE, 3);
		}
		return FMCStationDic;
	}

	public static String GetFMCStationName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFMCStationList().containsKey(wID)) {
			if (WDWConstans.GetFMCStationList().get(wID) != null) {
				wResult = WDWConstans.GetFMCStationList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FMCStation GetFMCStation(int wID) {
		FMCStation wResult = new FMCStation();
		if (WDWConstans.GetFMCStationList().containsKey(wID)) {
			if (WDWConstans.GetFMCStationList().get(wID) != null) {
				wResult = WDWConstans.GetFMCStationList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 产品全局数据
	private static Calendar RefreshProductTime = Calendar.getInstance();

	private static Map<Integer, FPCProduct> FPCProductDic = new HashMap<Integer, FPCProduct>();

	public static synchronized Map<Integer, FPCProduct> GetFPCProductList() {
		if (FPCProductDic == null || FPCProductDic.size() <= 0
				|| RefreshProductTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCProduct> wFPCProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(BaseDAO.SysAdmin, 0, 0)
					.List(FPCProduct.class);
			if (wFPCProductList != null && wFPCProductList.size() > 0) {
				FPCProductDic = wFPCProductList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshProductTime = Calendar.getInstance();
			RefreshProductTime.add(Calendar.MINUTE, 3);
		}
		return FPCProductDic;
	}

	public static synchronized Map<String, FPCProduct> GetFPCProductDic() {
		if (FPCProductDic == null || FPCProductDic.size() <= 0
				|| RefreshProductTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCProduct> wFPCProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(BaseDAO.SysAdmin, 0, 0)
					.List(FPCProduct.class);
			if (wFPCProductList != null && wFPCProductList.size() > 0) {
				FPCProductDic = wFPCProductList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshProductTime = Calendar.getInstance();
			RefreshProductTime.add(Calendar.MINUTE, 3);
		}

		if (FPCProductDic != null) {
			return FPCProductDic.values().stream().collect(Collectors.toMap(p -> p.ProductNo, p -> p, (o1, o2) -> o1));
		}

		return new HashMap<String, FPCProduct>();
	}

	public static String GetFPCProductName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFPCProductList().containsKey(wID)) {
			if (WDWConstans.GetFPCProductList().get(wID) != null) {
				wResult = WDWConstans.GetFPCProductList().get(wID).getProductName();
			}
		}
		return wResult;
	}

	public static String GetFPCProductNo(int wID) {
		String wResult = "";
		if (WDWConstans.GetFPCProductList().containsKey(wID)) {
			if (WDWConstans.GetFPCProductList().get(wID) != null) {
				wResult = WDWConstans.GetFPCProductList().get(wID).getProductNo();
			}
		}
		return wResult;
	}

	public static FPCProduct GetFPCProduct(int wID) {
		FPCProduct wResult = new FPCProduct();
		if (WDWConstans.GetFPCProductList().containsKey(wID)) {
			if (WDWConstans.GetFPCProductList().get(wID) != null) {
				wResult = WDWConstans.GetFPCProductList().get(wID);
			}
		}
		return wResult;
	}

	public static FPCProduct GetFPCProduct(String wCarType) {
		FPCProduct wResult = new FPCProduct();
		if (WDWConstans.GetFPCProductList().values().stream().anyMatch(p -> p.ProductNo.equals(wCarType))) {
			wResult = WDWConstans.GetFPCProductList().values().stream().filter(p -> p.ProductNo.equals(wCarType))
					.findFirst().get();
		}
		return wResult;
	}

	// endRegion

	// region 部门全局数据
	private static Calendar RefreshDeptTime = Calendar.getInstance();

	private static Map<Integer, BMSDepartment> BMSDepartmentDic = new HashMap<Integer, BMSDepartment>();

	public static synchronized Map<Integer, BMSDepartment> GetBMSDepartmentList() {
		if (BMSDepartmentDic == null || BMSDepartmentDic.size() <= 0
				|| RefreshDeptTime.compareTo(Calendar.getInstance()) < 0) {
			List<BMSDepartment> wBMSDepartmentList = CoreServiceImpl.getInstance()
					.BMS_QueryDepartmentList(BaseDAO.SysAdmin).List(BMSDepartment.class);
			if (wBMSDepartmentList != null && wBMSDepartmentList.size() > 0) {
				BMSDepartmentDic = wBMSDepartmentList.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshDeptTime = Calendar.getInstance();
			RefreshDeptTime.add(Calendar.MINUTE, 3);
		}
		return BMSDepartmentDic;
	}

	public static String GetBMSDepartmentName(List<Integer> wIDList) {
		String wResult = "";
		if (wIDList == null || wIDList.size() <= 0)
			return wResult;

		List<String> wNames = new ArrayList<String>();
		for (Integer integer : wIDList) {
			if (integer <= 0)
				continue;

			if (WDWConstans.GetBMSDepartmentList().containsKey(integer)) {
				if (WDWConstans.GetBMSDepartmentList().get(integer) != null) {
					wNames.add(WDWConstans.GetBMSDepartmentList().get(integer).getName());
				}
			}

		}
		wResult = StringUtils.Join(",", wNames);

		return wResult;
	}

	public static String GetBMSDepartmentName(int wID) {
		String wResult = "";
		if (WDWConstans.GetBMSDepartmentList().containsKey(wID)) {
			if (WDWConstans.GetBMSDepartmentList().get(wID) != null) {
				wResult = WDWConstans.GetBMSDepartmentList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static BMSDepartment GetBMSDepartment(int wID) {
		BMSDepartment wResult = new BMSDepartment();
		if (WDWConstans.GetBMSDepartmentList().containsKey(wID)) {
			if (WDWConstans.GetBMSDepartmentList().get(wID) != null) {
				wResult = WDWConstans.GetBMSDepartmentList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 岗位全局数据
	private static Calendar RefreshPosiTime = Calendar.getInstance();
	private static Map<Integer, BMSPosition> BMSPositionDic = new HashMap<Integer, BMSPosition>();

	public static synchronized Map<Integer, BMSPosition> GetBMSPositionList() {
		if (BMSPositionDic == null || BMSPositionDic.size() <= 0
				|| RefreshPosiTime.compareTo(Calendar.getInstance()) < 0) {
			List<BMSPosition> wBMSPositionList = CoreServiceImpl.getInstance().BMS_QueryPositionList(BaseDAO.SysAdmin)
					.List(BMSPosition.class);
			if (wBMSPositionList != null && wBMSPositionList.size() > 0) {
				BMSPositionDic = wBMSPositionList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshPosiTime = Calendar.getInstance();
			RefreshPosiTime.add(Calendar.MINUTE, 3);
		}
		return BMSPositionDic;
	}

	public static String GetBMSPositionName(List<Integer> wIDList) {
		String wResult = "";
		if (wIDList == null || wIDList.size() <= 0)
			return wResult;

		List<String> wNames = new ArrayList<String>();
		for (Integer integer : wIDList) {
			if (integer <= 0)
				continue;

			if (WDWConstans.GetBMSPositionList().containsKey(integer)) {
				if (WDWConstans.GetBMSPositionList().get(integer) != null) {
					wNames.add(WDWConstans.GetBMSPositionList().get(integer).getName());
				}
			}

		}
		wResult = StringUtils.Join(",", wNames);

		return wResult;
	}

	public static String GetBMSPositionName(int wID) {
		String wResult = "";
		if (WDWConstans.GetBMSPositionList().containsKey(wID)) {
			if (WDWConstans.GetBMSPositionList().get(wID) != null) {
				wResult = WDWConstans.GetBMSPositionList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static BMSPosition GetBMSPosition(int wID) {
		BMSPosition wResult = new BMSPosition();
		if (WDWConstans.GetBMSPositionList().containsKey(wID)) {
			if (WDWConstans.GetBMSPositionList().get(wID) != null) {
				wResult = WDWConstans.GetBMSPositionList().get(wID);
			}
		}
		return wResult;
	}
	// endRegion

	// region 局段全局数据
	private static Calendar RefreshCustomerTime = Calendar.getInstance();

	private static Map<Integer, CRMCustomer> CRMCustomerDic = new HashMap<Integer, CRMCustomer>();

	public static synchronized Map<Integer, CRMCustomer> GetCRMCustomerList() {
		if (CRMCustomerDic == null || CRMCustomerDic.size() <= 0
				|| RefreshCustomerTime.compareTo(Calendar.getInstance()) < 0) {
			List<CRMCustomer> wCRMCustomerList = CoreServiceImpl.getInstance()
					.CRM_QueryCustomerList(BaseDAO.SysAdmin, "", -1, -1, -1, -1).List(CRMCustomer.class);
			if (wCRMCustomerList != null && wCRMCustomerList.size() > 0) {
				CRMCustomerDic = wCRMCustomerList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshCustomerTime = Calendar.getInstance();
			RefreshCustomerTime.add(Calendar.MINUTE, 3);
		}
		return CRMCustomerDic;
	}

	public static String GetCRMCustomerName(int wID) {
		String wResult = "";
		if (WDWConstans.GetCRMCustomerList().containsKey(wID)) {
			if (WDWConstans.GetCRMCustomerList().get(wID) != null) {
				wResult = WDWConstans.GetCRMCustomerList().get(wID).getCustomerName();
			}
		}
		return wResult;
	}

	public static String GetCRMCustomerCode(int wID) {
		String wResult = "";
		if (WDWConstans.GetCRMCustomerList().containsKey(wID)) {
			if (WDWConstans.GetCRMCustomerList().get(wID) != null) {
				wResult = WDWConstans.GetCRMCustomerList().get(wID).getCustomerCode();
			}
		}
		return wResult;
	}

	public static CRMCustomer GetCRMCustomer(int wID) {
		CRMCustomer wResult = new CRMCustomer();
		if (WDWConstans.GetCRMCustomerList().containsKey(wID)) {
			if (WDWConstans.GetCRMCustomerList().get(wID) != null) {
				wResult = WDWConstans.GetCRMCustomerList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	// region 工厂全局数据
	private static Calendar RefreshFactoryTime = Calendar.getInstance();

	private static Map<Integer, FMCFactory> FMCFactoryDic = new HashMap<Integer, FMCFactory>();

	public static synchronized Map<Integer, FMCFactory> GetFMCFactoryList() {
		if (FMCFactoryDic == null || FMCFactoryDic.size() <= 0
				|| RefreshFactoryTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCFactory> wFMCFactoryList = FMCServiceImpl.getInstance().FMC_QueryFactoryList(BaseDAO.SysAdmin)
					.List(FMCFactory.class);
			if (wFMCFactoryList != null && wFMCFactoryList.size() > 0) {
				FMCFactoryDic = wFMCFactoryList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshFactoryTime = Calendar.getInstance();
			RefreshFactoryTime.add(Calendar.MINUTE, 3);
		}
		return FMCFactoryDic;
	}

	public static String GetFMCFactoryName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFMCFactoryList().containsKey(wID)) {
			if (WDWConstans.GetFMCFactoryList().get(wID) != null) {
				wResult = WDWConstans.GetFMCFactoryList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FMCFactory GetFMCFactory(int wID) {
		FMCFactory wResult = new FMCFactory();
		if (WDWConstans.GetFMCFactoryList().containsKey(wID)) {
			if (WDWConstans.GetFMCFactoryList().get(wID) != null) {
				wResult = WDWConstans.GetFMCFactoryList().get(wID);
			}
		}
		return wResult;
	}
	// endRegion

	// region 事业部全局数据
	private static Calendar RefreshBusinessUnitTime = Calendar.getInstance();

	private static Map<Integer, FMCBusinessUnit> FMCBusinessUnitDic = new HashMap<Integer, FMCBusinessUnit>();

	public static synchronized Map<Integer, FMCBusinessUnit> GetFMCBusinessUnitList() {
		if (FMCBusinessUnitDic == null || FMCBusinessUnitDic.size() <= 0
				|| RefreshBusinessUnitTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCBusinessUnit> wFMCBusinessUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryBusinessUnitList(BaseDAO.SysAdmin).List(FMCBusinessUnit.class);
			if (wFMCBusinessUnitList != null && wFMCBusinessUnitList.size() > 0) {
				FMCBusinessUnitDic = wFMCBusinessUnitList.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshBusinessUnitTime = Calendar.getInstance();
			RefreshBusinessUnitTime.add(Calendar.MINUTE, 3);
		}
		return FMCBusinessUnitDic;
	}

	public static String GetFMCBusinessUnitName(int wID) {
		String wResult = "";
		if (WDWConstans.GetFMCBusinessUnitList().containsKey(wID)) {
			if (WDWConstans.GetFMCBusinessUnitList().get(wID) != null) {
				wResult = WDWConstans.GetFMCBusinessUnitList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FMCBusinessUnit GetFMCBusinessUnit(int wID) {
		FMCBusinessUnit wResult = new FMCBusinessUnit();
		if (WDWConstans.GetFMCBusinessUnitList().containsKey(wID)) {
			if (WDWConstans.GetFMCBusinessUnitList().get(wID) != null) {
				wResult = WDWConstans.GetFMCBusinessUnitList().get(wID);
			}
		}
		return wResult;
	}
	// endRegion

	public static boolean IsLineContainStation(BMSEmployee wLoginUser, int wLineID, int wPartID, int wStepID,
			int wStationID, OutResult<Integer> wErrorCode) {
		boolean wIsContain = false;
		try {

			List<FMCLineUnit> wFMCLineUnit = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wLineID, 0, -1, false).List(FMCLineUnit.class);

			wIsContain = IsLineContainStation(wLoginUser, wFMCLineUnit, wPartID, wStepID, wStationID, wErrorCode);

		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wIsContain;
	}

	public static boolean IsLineContainStation(BMSEmployee wLoginUser, List<FMCLineUnit> wFMCLineUnit, int wPartID,
			int wStepID, int wStationID, OutResult<Integer> wErrorCode) {
		boolean wIsContain = false;
		try {

			for (FMCLineUnit wPartUnit : wFMCLineUnit) {
				if (wPartID > 0 && wPartUnit.UnitID != wPartID)
					continue;

				for (FMCLineUnit wStepUnit : wPartUnit.UnitList) {
					if (wStepID > 0 && wStepUnit.UnitID != wStepID)
						continue;

					for (FMCLineUnit wStationUnit : wStepUnit.UnitList) {
						if (wStationUnit.UnitID == wStationID) {
							wIsContain = true;
							break;
						}
					}
					if (wIsContain)
						break;
				}
				if (wIsContain)
					break;
			}

		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(ex.toString());
		}
		return wIsContain;
	}

	// region 业务缓存数据

	public synchronized static void SetBizTask(BPMTaskBase wBPMTaskBase) {
		try {
			switch (BPMEventModule.getEnumType(wBPMTaskBase.FlowType)) {
			case SCMovePart:

				MTCTaskSGAuditResource.add((MTCTask) wBPMTaskBase);

				break;
			case QTNCR:

				NCRTaskResource.add((NCRTask) wBPMTaskBase);

			case CKRepair:
				switch (RROStatus.getEnumType(wBPMTaskBase.Status)) {
				case ToPictures:
					OutResult<Integer> wErrorCode = new OutResult<Integer>();
					RROTask wRROTask = RROTaskDAO.getInstance().SelectByID(BaseDAO.SysAdmin, wBPMTaskBase.ID,
							wErrorCode);
					if (wRROTask != null && wRROTask.ID > 0) {
						// 根据工位判断返修任务类型
//						if (wRROTask.Steps == RROSteps.Supplier.getValue())
//							wRROTask.IsDelivery = RROTaskTypes.Supplier.getValue();
//						RROTaskDAO.getInstance().Update(BaseDAO.SysAdmin, wRROTask, wErrorCode);
					}
					break;
				case ToConfirmed:// 待确认触发返修项开始流程
//					RROTask wRROTask = (RROTask) wBPMTaskBase;
//					if (wRROTask.IsDelivery != RROTaskTypes.Supplier.getValue())
					RROTaskToConfirmedResource.add(wBPMTaskBase.ID);

					break;
				default:
					break;
				}
				break;
			case QTRepair:
				switch (RRORepairStatus.getEnumType(wBPMTaskBase.Status)) {
				case ItemFail: // 操作员通过返修项发起不合格评审
					RROItemTaskItemFailResource.add((RROItemTask) wBPMTaskBase);
					break;
				case Confirmed:
//					RROItemTaskConfirmedResource.add(wBPMTaskBase.ID);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public static BPMResource<Integer> MTCTaskJSAuditResource = new BPMResource<Integer>();

	public static BPMResource<MTCTask> MTCTaskSGAuditResource = new BPMResource<MTCTask>();

	public static BPMResource<Integer> MTCTaskCompletionResource = new BPMResource<Integer>();

	public static BPMResource<NCRTask> NCRTaskResource = new BPMResource<NCRTask>();

	public static BPMResource<Integer> NCRTaskRejectedResource = new BPMResource<Integer>();

	public static BPMResource<Integer> NCRTaskToCheckWriteResource = new BPMResource<Integer>();

	public static BPMResource<Integer> RROTaskToConfirmedResource = new BPMResource<Integer>();

	public static BPMResource<RROItemTask> RROItemTaskItemFailResource = new BPMResource<RROItemTask>();

//	public static BPMResource<Integer> RROItemTaskConfirmedResource = new BPMResource<Integer>();

	// 后续工位列表
	public static List<Integer> AllFPCPart = new ArrayList<Integer>();

	// 节点图Wid赋值
	public static Integer NodeNumber = 0;

//	public static BPMResource<Integer> RROItemTaskIsSendNCRResource = new BPMResource<Integer>();

//	static {
//
//		MTCTaskJSAuditResource = new BPMResource<Integer>(
//				Configuration.readConfigString("mtc.task.jsaudit.path", "config/config"), Integer.class);
//
//		MTCTaskSGAuditResource = new BPMResource<Integer>(
//				Configuration.readConfigString("mtc.task.sgaudit.path", "config/config"), Integer.class);
//
//		MTCTaskCompletionResource = new BPMResource<Integer>(
//				Configuration.readConfigString("mtc.task.completion.path", "config/config"), Integer.class);
//
//		NCRTaskResource = new BPMResource<Integer>(Configuration.readConfigString("ncr.task.all.path", "config/config"),
//				Integer.class);
//
//		NCRTaskRejectedResource = new BPMResource<Integer>(
//				Configuration.readConfigString("ncr.task.rejected.path", "config/config"), Integer.class);
//
//		NCRTaskToCheckWriteResource = new BPMResource<Integer>(
//				Configuration.readConfigString("ncr.task.tocheckwrite.path", "config/config"), Integer.class);
//
//		RROTaskToConfirmedResource = new BPMResource<Integer>(
//				Configuration.readConfigString("rro.task.toconfirme.path", "config/config"), Integer.class);
//
//		RROItemTaskItemFailResource = new BPMResource<Integer>(
//				Configuration.readConfigString("rroitem.task.itemfail.path", "config/config"), Integer.class);
//
//		RROItemTaskConfirmedResource = new BPMResource<Integer>(
//				Configuration.readConfigString("rroitem.task.Confirmed.path", "config/config"), Integer.class);
//
//	}

	// endRegion

}
