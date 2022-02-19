package com.mes.ncr.server.service.po.wms;

import java.io.Serializable;

/**
 * WMS返回结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2022-1-5 11:11:05
 */
public class WMSReturn implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 回传标记
	 */
	public String EDISENDFLAG1 = "";
	/**
	 * 回传时间
	 */
	public String EDISENDTIME1 = "";
	/**
	 * 回传描述
	 */
	public String EDISENDMSG1 = "";
}
