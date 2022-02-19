package com.mes.ncr.server.service.po.lfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 库位表
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-31 12:32:15
 * @LastEditTime 2020-1-31 12:32:19
 *
 */
public class LFSStoreHouse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID;
	/**
	 * 库位名称
	 */
	public String Name;
	/**
	 * 库位编码
	 */
	public String Code = "";
	/**
	 * 库位容量
	 */
	public int Capacity;
	/**
	 * 编辑者ID
	 */
	public int CreateID;
	/**
	 * 编辑者
	 */
	public String Creator;
	/**
	 * 编辑时间
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 激活关闭
	 */
	public int Active;
	/**
	 * 库位长度(单位：mm)
	 */
	public int Length;
	/**
	 * 库位可用长度(单位：mm)
	 */
	public int UsableLength;
	/**
	 * 库位类型 0:正常库位 1：不能进车体  2:默认厂线库位
	 */
	public int Type = 0;
	/**
	 *所属工区
	 */
	public int AreaID = 0;
	/**
	 *返回工区名称显示
	 */
	public String  AreaDis= "";
	/**
	 * 出入口台位列表
	 */
	public List<Integer> GateDoorWorkSpaceIDList = new ArrayList<Integer>();
	/**
	 * 可直移库位列表
	 */
	public List<Integer> MoveStoreHouseIDList = new ArrayList<Integer>();
	
	/**
	 * 工区主管IDList
	 */
	public List<Integer> LeaderIDList = new ArrayList<Integer>();
	/**
	 * 是否为可直移 0：不可直移  1：可直移
	 */
	public int AsMove;
	

	public LFSStoreHouse() {
	}

	
	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getCapacity() {
		return Capacity;
	}

	public void setCapacity(int capacity) {
		Capacity = capacity;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getLength() {
		return Length;
	}

	public void setLength(int length) {
		Length = length;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getUsableLength() {
		return UsableLength;
	}

	public void setUsableLength(int usableLength) {
		UsableLength = usableLength;
	}

	public List<Integer> getGateDoorWorkSpaceIDList() {
		return GateDoorWorkSpaceIDList;
	}

	public void setGateDoorWorkSpaceIDList(List<Integer> gateDoorWorkSpaceIDList) {
		GateDoorWorkSpaceIDList = gateDoorWorkSpaceIDList;
	}

	public List<Integer> getMoveStoreHouseIDList() {
		return MoveStoreHouseIDList;
	}

	public void setMoveStoreHouseIDList(List<Integer> moveStoreHouseIDList) {
		MoveStoreHouseIDList = moveStoreHouseIDList;
	}

	public int getAsMove() {
		return AsMove;
	}

	public void setAsMove(int asMove) {
		AsMove = asMove;
	}

	public int getAreaID() {
		return AreaID;
	}

	public void setAreaID(int areaID) {
		AreaID = areaID;
	}

	public String getAreaDis() {
		return AreaDis;
	}

	public void setAreaDis(String areaDis) {
		AreaDis = areaDis;
	}


	public List<Integer> getLeaderIDList() {
		return LeaderIDList;
	}


	public void setLeaderIDList(List<Integer> leaderIDList) {
		LeaderIDList = leaderIDList;
	}
}
