package com.mes.ncr.server.service;

import java.util.List;

import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.Configuration;



/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-11 19:56:50
 * @LastEditTime 2020-4-11 19:56:54
 *
 */
public interface QMSService {
	static String ServerUrl = Configuration.readConfigString("qms.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("qms.server.project.name", "config/config");
	
	/**
	 * 条件查询所有转序单
	 * 
	 * @param wLoginUser
	 * @return
	 */
	APIResult RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOrderID, int wApplyStationID,
			int wTargetStationID, List<Integer> wStateIDList);
	
	APIResult SFC_QuerySpecialTaskListByLogin(BMSEmployee wLoginUser);
	APIResult SFC_QueryTaskByID(BMSEmployee wLoginUser, int wID);
	APIResult SFC_QueryItemListByID(BMSEmployee wLoginUser, int wStepID);
	
	APIResult SFC_TaskIPTHandCheckItem(BMSEmployee wLoginUser, int wTaskIPTID,int wIPTItemID,int wAssessResult);
}
