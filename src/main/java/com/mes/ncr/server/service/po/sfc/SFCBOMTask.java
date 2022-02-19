package com.mes.ncr.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.ncr.server.service.po.bpm.BPMTaskBase;

/**
 * 偶换件不合格评审任务
 * 
 * @author ShrisJava
 *
 */
public class SFCBOMTask extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID 存储
	 */
	public int OrderID = 0;
	/**
	 * 订单对应的WBS号
	 */
	public String WBSNo = "";
	/**
	 * 订单对应的车型ID
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 订单对应的局段ID
	 */
	public int CustomerID; // 局段
	public String CustomerCode = "";
	public String CustomerName = "";
	/**
	 * 订单对应的车号
	 */
	public String PartNo = "";

	/**
	 * 工位ID 存储
	 */
	public int PartID = 0;
	/**
	 * 工位名称
	 */
	public String PartName = "";
	/**
	 * 工位编号
	 */
	public String PartCode = "";

	/**
	 * 工序ID 存储
	 */
	public int PartPointID = 0;
	/**
	 * 工序名称
	 */
	public String PartPointName = "";

	/**
	 * 对应的BOMID 存储
	 */
	public int BOMID = 0;
	/**
	 * 对应的BOM子项ID
	 */
	public int BOMItemID = 0;

	/**
	 * 物料ID 存储
	 */
	public int MaterialID = 0;

	public String MaterialNo = "";
	public String MaterialName = "";

	/**
	 * 物料数量 存储
	 */
	public Double MaterialNumber = 0.0;
	/**
	 * 物料单位 存储
	 */
	public int UnitID = 0;
	public String UnitText = "";

	/**
	 * 等级 存储
	 */
	public int Level = 0;
	public String LevelName = "";

	/**
	 * 处理意见 存储
	 */
	public String Disposal = "";

	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";

	/**
	 * 工艺路线
	 */
	public int RouteID = 0;

	/**
	 * 评审意见
	 */
	public int ReviewComments = 0;

	/**
	 * 责任
	 */
	public int Responsibility = 0;

	/**
	 * 工艺师
	 */
	public String CraftsmanIDs = "";
	/**
	 * 工艺师
	 */
	public String CraftsmanNames = "";
	/**
	 * 技术工程师
	 */
	public String TechnicalEngineerIDs = "";
	/**
	 * 技术工程师
	 */
	public String TechnicalEngineerNames = "";

	// 赋值属性
	public int TagTypes = 0;

	/**
	 * 1常规新件 2修复旧件
	 */
	public int SapType = 0;
	public String SapTypeName = "";

	/**
	 * 物料子项
	 */
	public List<SFCBOMTaskItem> SFCBOMTaskItemList = new ArrayList<SFCBOMTaskItem>();

	/**
	 * 辅助属性
	 */
	public String AreaCharge = "";
	public String Remark = "";
	public String ImageList = "";
	public String ReviewCommentsName = "";
	public String ResponsibilityName = "";

	/**
	 * A等级物料集合
	 */
	public List<SFCBOMTaskItem> SFCBOMTaskItemAList = new ArrayList<SFCBOMTaskItem>();
	/**
	 * B等级物料集合
	 */
	public List<SFCBOMTaskItem> SFCBOMTaskItemBList = new ArrayList<SFCBOMTaskItem>();
	/**
	 * C等级物料集合
	 */
	public List<SFCBOMTaskItem> SFCBOMTaskItemCList = new ArrayList<SFCBOMTaskItem>();

	/**
	 * SAP推送状态 0未推送 1成功 2失败
	 */
	public int SAPStatus = 0;
	/**
	 * SAP推送失败文本内容
	 */
	public String SAPStatusText = "";

	/**
	 * 自修件ID
	 */
	public int SRPartID = 0;
	/**
	 * 自修件名称
	 */
	public String SRPartName = "";
	/**
	 * 自修件产品编号
	 */
	public String SRProductNo = "";

	/**
	 * 已确认的等级
	 */
	public String ConfirmedLevels = "";

	public String ImageUrl = "";

	/**
	 * 是否零公里准备 0否 1是
	 */
	public int IsLGL = 0;

	public SFCBOMTask() {
		super();
	}

	public SFCBOMTask(int orderID, int partID, int partPointID, int bOMID, int bOMItemID, int materialID,
			Double materialNumber, int unitID, int level) {
		super();
		OrderID = orderID;
		PartID = partID;
		PartPointID = partPointID;
		BOMID = bOMID;
		BOMItemID = bOMItemID;
		MaterialID = materialID;
		MaterialNumber = materialNumber;
		UnitID = unitID;
		Level = level;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getSapTypeName() {
		return SapTypeName;
	}

	public String getRemark() {
		return Remark;
	}

	public String getImageList() {
		return ImageList;
	}

	public String getReviewCommentsName() {
		return ReviewCommentsName;
	}

	public String getConfirmedLevels() {
		return ConfirmedLevels;
	}

	public void setConfirmedLevels(String confirmedLevels) {
		ConfirmedLevels = confirmedLevels;
	}

	public String getResponsibilityName() {
		return ResponsibilityName;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public void setImageList(String imageList) {
		ImageList = imageList;
	}

	public void setReviewCommentsName(String reviewCommentsName) {
		ReviewCommentsName = reviewCommentsName;
	}

	public void setResponsibilityName(String responsibilityName) {
		ResponsibilityName = responsibilityName;
	}

	public List<SFCBOMTaskItem> getSFCBOMTaskItemList() {
		return SFCBOMTaskItemList;
	}

	public List<SFCBOMTaskItem> getSFCBOMTaskItemAList() {
		return SFCBOMTaskItemAList;
	}

	public List<SFCBOMTaskItem> getSFCBOMTaskItemBList() {
		return SFCBOMTaskItemBList;
	}

	public List<SFCBOMTaskItem> getSFCBOMTaskItemCList() {
		return SFCBOMTaskItemCList;
	}

	public void setSFCBOMTaskItemAList(List<SFCBOMTaskItem> sFCBOMTaskItemAList) {
		SFCBOMTaskItemAList = sFCBOMTaskItemAList;
	}

	public void setSFCBOMTaskItemBList(List<SFCBOMTaskItem> sFCBOMTaskItemBList) {
		SFCBOMTaskItemBList = sFCBOMTaskItemBList;
	}

	public void setSFCBOMTaskItemCList(List<SFCBOMTaskItem> sFCBOMTaskItemCList) {
		SFCBOMTaskItemCList = sFCBOMTaskItemCList;
	}

	public String getAreaCharge() {
		return AreaCharge;
	}

	public void setAreaCharge(String areaCharge) {
		AreaCharge = areaCharge;
	}

	public void setSFCBOMTaskItemList(List<SFCBOMTaskItem> sFCBOMTaskItemList) {
		SFCBOMTaskItemList = sFCBOMTaskItemList;
	}

	public void setSapTypeName(String sapTypeName) {
		SapTypeName = sapTypeName;
	}

	public int getSapType() {
		return SapType;
	}

	public void setSapType(int sapType) {
		SapType = sapType;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public String getWBSNo() {
		return WBSNo;
	}

	public void setWBSNo(String wBSNo) {
		WBSNo = wBSNo;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getPartCode() {
		return PartCode;
	}

	public void setPartCode(String partCode) {
		PartCode = partCode;
	}

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public int getBOMID() {
		return BOMID;
	}

	public void setBOMID(int bOMID) {
		BOMID = bOMID;
	}

	public int getBOMItemID() {
		return BOMItemID;
	}

	public void setBOMItemID(int bOMItemID) {
		BOMItemID = bOMItemID;
	}

	public int getMaterialID() {
		return MaterialID;
	}

	public void setMaterialID(int materialID) {
		MaterialID = materialID;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public Double getMaterialNumber() {
		return MaterialNumber;
	}

	public void setMaterialNumber(Double materialNumber) {
		MaterialNumber = materialNumber;
	}

	public int getUnitID() {
		return UnitID;
	}

	public void setUnitID(int unitID) {
		UnitID = unitID;
	}

	public String getUnitText() {
		return UnitText;
	}

	public void setUnitText(String unitText) {
		UnitText = unitText;
	}

	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public String getLevelName() {
		return LevelName;
	}

	public void setLevelName(String levelName) {
		LevelName = levelName;
	}

	public String getDisposal() {
		return Disposal;
	}

	public void setDisposal(String disposal) {
		Disposal = disposal;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getRouteID() {
		return RouteID;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}

	public int getReviewComments() {
		return ReviewComments;
	}

	public void setReviewComments(int reviewComments) {
		ReviewComments = reviewComments;
	}

	public int getResponsibility() {
		return Responsibility;
	}

	public void setResponsibility(int responsibility) {
		Responsibility = responsibility;
	}

	public String getCraftsmanIDs() {
		return CraftsmanIDs;
	}

	public void setCraftsmanIDs(String craftsmanIDs) {
		CraftsmanIDs = craftsmanIDs;
	}

	public String getCraftsmanNames() {
		return CraftsmanNames;
	}

	public void setCraftsmanNames(String craftsmanNames) {
		CraftsmanNames = craftsmanNames;
	}

	public String getTechnicalEngineerIDs() {
		return TechnicalEngineerIDs;
	}

	public void setTechnicalEngineerIDs(String technicalEngineerIDs) {
		TechnicalEngineerIDs = technicalEngineerIDs;
	}

	public String getTechnicalEngineerNames() {
		return TechnicalEngineerNames;
	}

	public void setTechnicalEngineerNames(String technicalEngineerNames) {
		TechnicalEngineerNames = technicalEngineerNames;
	}

	public int getSAPStatus() {
		return SAPStatus;
	}

	public String getSAPStatusText() {
		return SAPStatusText;
	}

	public void setSAPStatus(int sAPStatus) {
		SAPStatus = sAPStatus;
	}

	public void setSAPStatusText(String sAPStatusText) {
		SAPStatusText = sAPStatusText;
	}

	public int getSRPartID() {
		return SRPartID;
	}

	public String getSRPartName() {
		return SRPartName;
	}

	public String getSRProductNo() {
		return SRProductNo;
	}

	public void setSRPartID(int sRPartID) {
		SRPartID = sRPartID;
	}

	public void setSRPartName(String sRPartName) {
		SRPartName = sRPartName;
	}

	public void setSRProductNo(String sRProductNo) {
		SRProductNo = sRProductNo;
	}

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	public int getIsLGL() {
		return IsLGL;
	}

	public void setIsLGL(int isLGL) {
		IsLGL = isLGL;
	}
}
