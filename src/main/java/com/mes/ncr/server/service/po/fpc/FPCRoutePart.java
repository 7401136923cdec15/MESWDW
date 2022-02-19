package com.mes.ncr.server.service.po.fpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPCRoutePart implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int RouteID = 0; // 工艺版本ID

	public int PartID = 0;

	public String Code = "";

	/**
	 * 主线层级
	 */
	public int OrderID = 0;

	public String Name = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID = 0;

	public String Creator = "";

	public String VersionNo = ""; // 工艺版本

	public String RouteName = ""; // 工艺名称

	public int PrevPartID = 0;// 上工段

	/**
	 * 标准工时
	 */
	public Double StandardPeriod = 0.0;

	/**
	 * 调整工时 新增字段
	 */
	public Double ActualPeriod = 0.0;

	/**
	 * key PartID Value : Condition value: 0 主线也是下个模块
	 */
	public Map<String, String> NextPartIDMap = new HashMap<String, String>();// 上工段路线ID

	public List<FPCRoutePartPoint> PartPointList = new ArrayList<>(); // 工序列表

	/**
	 * 版本信息
	 */
	public String Version = "";

	public FPCRoutePart() {
		this.Name = "";
		this.Creator = "";
		this.CreateTime = Calendar.getInstance();
		this.PartPointList = new ArrayList<>();
	}

	public FPCRoutePart Clone() {
		FPCRoutePart wItem = new FPCRoutePart();
		wItem.ID = this.ID;
		wItem.RouteID = this.RouteID;
		wItem.PartID = this.PartID;
		wItem.PartID = this.PartID;

		wItem.CreatorID = this.CreatorID;
		wItem.CreateTime = this.CreateTime;
		wItem.Creator = this.Creator;

		wItem.PartPointList = new ArrayList<FPCRoutePartPoint>(this.PartPointList);
		return wItem;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRouteID() {
		return RouteID;
	}

	public Double getStandardPeriod() {
		return StandardPeriod;
	}

	public Double getActualPeriod() {
		return ActualPeriod;
	}

	public String getVersion() {
		return Version;
	}

	public void setStandardPeriod(Double standardPeriod) {
		StandardPeriod = standardPeriod;
	}

	public void setActualPeriod(Double actualPeriod) {
		ActualPeriod = actualPeriod;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getVersionNo() {
		return VersionNo;
	}

	public void setVersionNo(String versionNo) {
		VersionNo = versionNo;
	}

	public String getRouteName() {
		return RouteName;
	}

	public void setRouteName(String routeName) {
		RouteName = routeName;
	}

	public List<FPCRoutePartPoint> getPartPointList() {
		return PartPointList;
	}

	public void setPartPointList(List<FPCRoutePartPoint> partPointList) {
		PartPointList = partPointList;
	}

	public int getPrevPartID() {
		return PrevPartID;
	}

	public void setPrevPartID(int prevPartID) {
		PrevPartID = prevPartID;
	}

	public Map<String, String> getNextPartIDMap() {
		return NextPartIDMap;
	}

	public void setNextPartIDMap(Map<String, String> nextPartIDMap) {
		NextPartIDMap = nextPartIDMap;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

}
