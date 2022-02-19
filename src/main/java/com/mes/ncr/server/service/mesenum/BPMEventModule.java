package com.mes.ncr.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.cfg.CFGItem;

public enum BPMEventModule {
	/**
	 * SCH
	 */
	Default(0, "默认"), SCLogin(1001, "上岗打卡"), SCDJ(1002, "点检"), SCZJ(1003, "自检"), SCSJ(1004, "首检"),
	SCNCR(1005, "NCR申请单"), SCLL(1006, "领料"), SCBG(1007, "报工"), SCReady(1008, "SCReady"), SCSCBL(1009, "补料"),
	SCSCTL(1010, "退料"), SCLock(1011, "叫停"), SCCall(1012, "呼叫"), SCMsg(1013, "消息"), SCXJ(1014, "生产巡检"),
	SCBeforeDJ(1015, "班前点检"), SCAfterDJ(1016, "班后点检"), SCLayout(1017, "下岗打卡"), SCRepair(1018, "试运前后返修"),
	SCMovePart(1020, "移车"),

	QTLLJY(2001, "来料检验"), QTSJ(2002, "首检"), QTXJ(2003, "巡检"), QTRKJ(2004, "入库检"), QTCKJ(2005, "出库检"),
	QTNCR(2006, "NCR报告"), QTLock(2007, "叫停"), QTCall(2008, "呼叫"), QTMsg(2009, "消息"), QTLogin(2010, "打卡"),
	QTRepair(2011, "供应商返修"), QTJLXJ(2012, "计量巡检"), QTLayout(2013, "下岗打卡"), QTReCheck(2014, "复测"),

	TechXJ(3001, "巡检"), TechNCR(3002, "NCR报告"), TechLock(3003, "叫停"), TechCall(3004, "呼叫"), TechMsg(3005, "消息"),
	TechLogin(3006, "上岗打卡"), TechLayout(3007, "下岗打卡"), TechRepair(3008, "过程检返修"),

	DeviceDJ(4001, "不合格评审申请"), DeviceBY(4002, "维保"), DeviceWX(4003, "维修"), DeviceCall(4004, "呼叫"),
	DeviceMsg(4005, "消息"), DeviceLogin(4006, "上岗打卡"), DeviceLayout(4007, "下岗打卡"),

	CKSCM(5001, "采购入库"), CKSCPL(5002, "生产配料"), CKSL(5003, "送料"), CKSCRK(5004, "生产入库"), CKFHCK(5005, "发货出库"),
	CKCall(5006, "呼叫"), CKMsg(5007, "消息"), CKLogin(5008, "上岗打卡"), CKLayout(5009, "下岗打卡"), CKRepair(5010, "验收返修"),

	/**
	 * 部门审批流程设置 暂不需审批
	 */
	ATDepartment(8001, "部门设置"),
	/**
	 * 岗位设置 暂不需审批
	 */
	ATPosition(8002, "岗位设置"),
	/**
	 * 工位班组设置 暂不需审批
	 */
	ATWorkCharge(8003, "班组工位设置"),
	/**
	 * 工厂日历设置 暂不需审批
	 */
	ATCalendar(8004, "日历设置"),
	/**
	 * 广机为局段 其他为客户 暂不需审批
	 */
	ATCustomer(8005, "局段设置"),

	/**
	 * 设备资产 需要审批
	 */
	ATDevice(8006, "设备资产"),
	/**
	 * 事业部设置 暂不需审批
	 */
	ATBusinessUnit(8007, "事业部设置"),

	/**
	 * 车间设置 暂不需审批
	 */
	ATWorkShop(8008, "车间设置"),
	/**
	 * 产线设置 暂不需审批
	 */
	ATLine(8009, "产线设置"),
	/**
	 * 产线单元设置 需要审批
	 */
	ATLineUnit(8010, "产线单元设置"),

	/**
	 * 台位设置 暂不需审批
	 */
	ATWorkspace(8011, "台位设置"),
	/**
	 * 工段设置 广机为工位
	 */
	ATPart(8012, "工位库设置"),
	/**
	 * 工序设置
	 */
	ATStep(8013, "工序库设置"),
	/**
	 * 产品类型 广机为车辆类型
	 */
	ATProductType(8014, "车辆类型设置"),
	/**
	 * 产品设置 广机为车型设置
	 */
	ATProduct(8015, "车型设置"),

	/**
	 * 产品工艺路线设置 广机为一级流程设置
	 */
	ATProductRoute(8016, "标准流程方案"),

	ATRoute(8017, "流程方案"),

	/**
	 * 供应商设置
	 */
	ATSupplier(8019, "供应商设置"),

	/**
	 * 物料设置
	 */
	ATMaterial(8030, "物料设置"),

	ATUnit(8031, "单位设置"),

	ATBOM(8032, "BOM设置"),

	ATAPSBOM(8033, "台车BOM"),

	ATInStock(8035, "部件入库"),

	/**
	 * 生产命令票 广机为生产订单
	 */
	ATCommand(8040, "生产订单"),

	/**
	 * 生产订单 广机为台车订单
	 */
	ATOrder(8041, "台车订单"),
	/**
	 * 月计划审批
	 */
	SCMonthAudit(8100, "月计划审批"),
	/**
	 * 周计划审批
	 */
	SCWeekAudit(8101, "周计划审批"),
	/**
	 * 日计划审批
	 */
	SCDayAudit(8102, "日计划审批"),
	/**
	 * 派工
	 */
	SCDispatching(8103, "派工"),

	/*
	 * 预检报告审批
	 */
	YJReport(8107, "预检报告审批"),
	/**
	 * 标准审批
	 */
	StandardAudit(8207, "标准审批"),
	/**
	 * 偶换件不合格评审
	 */
	OccasionNCR(8201, "偶换件不合格评审"),
	/**
	 * 知会-不合格评审
	 */
	NCRInform(8212, "知会-不合格评审");

	private int value;
	private String lable;

	private BPMEventModule(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMEventModule getEnumType(int val) {
		for (BPMEventModule type : BPMEventModule.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		CFGItem wItem = null;
		for (BPMEventModule type : BPMEventModule.values()) {
			wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.name();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public static List<CFGItem> getEnumList(int min) {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		CFGItem wItem = null;
		for (BPMEventModule type : BPMEventModule.values()) {
			if (type.getValue() < min)
				continue;
			wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
