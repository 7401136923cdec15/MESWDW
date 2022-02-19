package com.mes.ncr.server.service;

import java.util.Calendar;

import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.fmc.FMCWorkspace;
import com.mes.ncr.server.service.utils.Configuration;

public interface FMCService {

	static String ServerUrl = Configuration.readConfigString("core.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("core.server.project.name", "config/config");

	// 工厂模型：工厂&事业部&车间&产线&工位
	// 工厂

	APIResult FMC_QueryFactory(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_QueryFactoryList(BMSEmployee wLoginUser);

	// 事业部

	APIResult FMC_QueryBusinessUnitByID(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_QueryBusinessUnitList(BMSEmployee wLoginUser);

	// 车间

	APIResult FMC_QueryWorkShopByID(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_QueryWorkShopList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID);

	// 产线

	APIResult FMC_QueryLineByID(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_QueryLineList(BMSEmployee wLoginUser, int wBusinessUnitID, int wFactoryID, int wWorkShopID);

	// 产线工艺配置
	APIResult FMC_QueryLineUnitListByLineID(BMSEmployee wLoginUser, int wLineID, int wID, int wProductID,
			boolean wIsList);

	APIResult FMC_QueryLineUnitListByProductNo(BMSEmployee wLoginUser, int wLineID, String wProductNo);

	// 制造资源
	APIResult FMC_QueryResourceByID(BMSEmployee wLoginUser, int wID);

	APIResult FMC_QueryResourceList(BMSEmployee wLoginUser, int wBusinessUnitID, int wFactoryID, int wLineID);

	// 工位
	APIResult FMC_QueryStationByID(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_QueryStationList(BMSEmployee wLoginUser, int wLineID, int wWorkShopID);

	APIResult FMC_IsLineContainStation(BMSEmployee wLoginUser, int wLineID, int wPartID, int wStepID, int wStationID);

	// 班次模板管理
	APIResult FMC_QueryWorkDayByID(BMSEmployee wLoginUser, int wID);

	APIResult FMC_QueryActiveWorkDayByWorkShop(BMSEmployee wLoginUser, int wWorkShopID);

	APIResult FMC_QueryWorkDayList(BMSEmployee wLoginUser, int wFactoryID, int wActive);

	APIResult FMC_QueryShiftTimeZoneList(BMSEmployee wLoginUser, int wShiftID);

	APIResult FMC_QueryShiftList(BMSEmployee wLoginUser, int wWorkDayID);

	APIResult FMC_QueryShiftByID(BMSEmployee wLoginUser, int wID);

	APIResult FMC_GetFMCWorkspaceList(BMSEmployee wLoginUser, int wProductID, int wPartID, String wPartNo,
			int wPlaceType, int wActive);

	APIResult FMC_GetFMCWorkspace(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_SaveFMCWorkspace(BMSEmployee wLoginUser, FMCWorkspace wFMCWorkspace);

	APIResult FMC_GetFMCWorkspaceRecordList(BMSEmployee wLoginUser, int wProductID, int wPartID, String wPartNo,
			int wPlaceType, int wActive, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 将车号更新至台位
	 */
	APIResult FMC_UpdateFMCWorkspace(BMSEmployee wLoginUser, FMCWorkspace wFMCWorkspace);

	APIResult FMC_BindFMCWorkspace(BMSEmployee wLoginUser, FMCWorkspace wFMCWorkspace);

	APIResult FPC_QueryProductTypeByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryProductTypeList(BMSEmployee wLoginUser, int wBusinessUnitID);

	// 产品规格
	APIResult FPC_QueryProductByID(BMSEmployee wLoginUser, int wID, String wProductNo);

	APIResult FPC_QueryProductList(BMSEmployee wLoginUser, int wBusinessUnitID, int wProductTypeID);

	// 产品工艺路线版本设置
	APIResult FPC_QueryProductRouteByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryWorkHourByProductCode(BMSEmployee wLoginUser, String wProductCode, int wUnitLevel, int wLineID,
			int wPartID, int wStepID);

	APIResult FPC_QueryProductRouteList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID,
			int wProductTypeID);

	// 产品标准工时管理（工序段&工序）
	// 单次批量，标准工时，半成品失效时间
	APIResult FPC_QueryManuCapacityByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryManuCapacityList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID, int wWorkShopID,
			int wLineID, int wProductTypeID, int wProductID);

	APIResult FPC_GenerateManuCapacityListByLineID(BMSEmployee wLoginUser, int wLineID, int wProductID);

	// 标准工段

	APIResult FPC_QueryPart(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FPC_QueryPartList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID, int wProductTypeID);

	// 标准工步
	APIResult FPC_QueryPartPoint(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FPC_QueryPartPointList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID, int wProductTypeID);

	// 工艺路径
	APIResult FPC_QueryRouteByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryRouteList(BMSEmployee wLoginUser, int wFactoryID, int wBusinessUnitID, int wProductTypeID);

	//
	APIResult FPC_QueryRouteByProduct(BMSEmployee wLoginUser, int wLineID, int wProductID, String wProductCode);

	// 工艺路径--工序段
	APIResult FPC_QueryRoutePartByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryRoutePartListByRouteID(BMSEmployee wLoginUser, int wRouteID);

	// 工艺路径--工序
	APIResult FPC_QueryRoutePartPointByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryRoutePartPointListByRouteID(BMSEmployee wLoginUser, int wRouteID, int wPartID);

	// 自定义参数
	APIResult FPC_QueryProductCustomByID(BMSEmployee wLoginUser, int wID);

	APIResult FPC_QueryProductCustomListByProductID(BMSEmployee wLoginUser, int wProductID, int wRouteID, int wPartID,
			int wPartPointID);

	APIResult FMC_QueryWorkspaceList(BMSEmployee wLoginUser);
}
