package com.mes.ncr.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业指导书
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 13:13:23
 * @LastEditTime 2020-2-12 13:13:27
 *
 */
public class IPTSOP implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 指导书名称
	 */
	public String Name = "";
	/**
	 * 指导书描述
	 */
	public String Detail = "";
	/**
	 * 指导书类型
	 */
	public int Type = 0;
	/**
	 * 指导书地址
	 */
	public List<String> PathList = new ArrayList<String>();

	public IPTSOP() {
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

	public String getDetail() {
		return Detail;
	}

	public void setDetail(String detail) {
		Detail = detail;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public List<String> getPathList() {
		return PathList;
	}

	public void setPathList(List<String> pathList) {
		PathList = pathList;
	}
}
