package com.mes.ncr.server.serviceimpl.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.mes.ncr.server.service.mesenum.APSShiftPeriod;
import com.mes.ncr.server.service.mesenum.FMCShiftLevel;
import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.mesenum.MESException;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.CoreServiceImpl;
import com.mes.ncr.server.shristool.LoggerTool;
import com.mes.ncr.server.utils.DBHelper;
import com.mes.ncr.server.utils.RetCode;

public class MESServer {

	public static List<MESInstance> MESInstanceList = new ArrayList<MESInstance>();

	public static int Instance = 0; // 0:单机版,1000,网络版

	public static boolean ERPEnable = false;

	public static int LoggerDays = 120;
	public static int ExceptionDays = 120;
	public static Calendar ExpiredTime = Calendar.getInstance();

	private static NamedParameterJdbcTemplate namedJdbcTemplate;

	private static NamedParameterJdbcTemplate getNameJdbcTemplate() {
		if (namedJdbcTemplate == null)
			namedJdbcTemplate = DBHelper.getTemplate();
		return namedJdbcTemplate;
	}

	// 数据库实例&文件系统实例
	public static int DB_QueryMaxID(String wInstanceName, String wTableName) {
		int wMaxID = 1;
		// 判断客户信息是否存在(中国：统一社会信用代码，国外:提醒是否有重复）
		try {
			NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();

			String wSQLText = StringUtils.Format("Select ifnull(max(ID),0) as ID from {0}.{1}", wInstanceName,
					wTableName);
			Map<String, Object> wParms = new HashMap<String, Object>();
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
			for (Map<String, Object> wSqlDataReader : wQueryResultList) {
				int wID = StringUtils.parseInt(wSqlDataReader.get("ID"));
				wMaxID = wID + 1;
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "DB_QueryMaxID",
					StringUtils.Format("Table={0} Function Exception:", wTableName) + ex.toString());
		}
		return wMaxID;
	}

	public static int MES_LoadDatabaseList() {
		OutResult<Integer> wErrorCode = new OutResult<Integer>();

		try {
			wErrorCode.set(0);
			NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();

			String wSQLText = "";
			wSQLText = StringUtils.Format("Select * from {0}.mes_company", MESDBSource.Instance.getDBName());

			Map<String, Object> wParms = new HashMap<String, Object>();
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
			for (Map<String, Object> wSqlDataReader : wQueryResultList) {
				// wSqlDataReader\[(\"\w+\")\] wSqlDataReader.get($1)
				MESInstance wFactory = new MESInstance();
				wFactory.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
				wFactory.Name = StringUtils.parseString(wSqlDataReader.get("Name"));
				wFactory.BasicDB = String.format("mesbasic%6d", wFactory.ID);
				wFactory.DFSSheetDB = String.format("mesdfssheet%6d", wFactory.ID);
				wFactory.FactoryDB = String.format("mesfactory%6d", wFactory.ID);

				MESServer.MESInstanceList.add(wFactory);
			}

		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_LoadDatabaseList", "Function Exception:" + ex.toString());
		}
		return wErrorCode.Result;
	}

	public static ServiceResult<String> MES_GetDatabaseName(int wID, MESDBSource wDBSource) {
		ServiceResult<String> wInstance = new ServiceResult<String>();

		wInstance.Result = MESDBSource.Basic.getDBName();
		try {
			if (wID < 1) {
				wInstance.Result = wDBSource.getDBName();
			}
			for (MESInstance wItem : MESServer.MESInstanceList) {
				if (wItem.ID == wID) {
					switch (wDBSource) {
					case Instance:
						wInstance.Result = wDBSource.getDBName();
						break;
					case Basic:
						wInstance.Result = wItem.BasicDB;
						break;
					default:
						break;
					}
					break;
				}
			}

			 

		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_GetDatabaseName", "Function Exception:" + ex.toString());
			wInstance.ErrorCode = MESException.DBInstance.getValue();
		}
		return wInstance;
	}

	public static boolean BMS_CheckPowerByAuthorityID(int wCompanyID, int wLoginID, int wFunctionID, int wRangeID,
			int wTypeID) {
		try {
			APIResult wAPIResult = CoreServiceImpl.getInstance().BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID,
					wFunctionID, 0, 0);
			if (wAPIResult != null && wAPIResult.getResultCode() == RetCode.SERVER_CODE_SUC) {
				return wAPIResult.Info(Boolean.class);
			}

		} catch (Exception e) {
			LoggerTool.SaveException("MESServer", "BMS_CheckPowerByAuthorityID", "Function Exception:" + e.toString());
		}
		return false;
	}

	public static ServiceResult<String> MES_GetDatabaseName(int wCompanyID, MESDBSource wMESDBSource, int wLoginID,
			int wFunctionID) {
		ServiceResult<String> wInstance = new ServiceResult<String>();
		wInstance.Result = MESDBSource.Basic.getDBName();
		try {
			if (wCompanyID < 1) {
				wInstance.Result = wMESDBSource.getDBName();
			}
			for (MESInstance wItem : MESServer.MESInstanceList) {
				if (wItem.ID == wCompanyID) {
					switch (wMESDBSource) {
					case Instance:
						wInstance.Result = wMESDBSource.getDBName();
						break;
					case Basic:
						wInstance.Result = wItem.BasicDB;
						break;
					default:
						break;
					}
					break;
				}
			}

			 

			if (wInstance.Result.length() > 5 && wFunctionID > 0) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>();
				wErrorCode.set(0);
				boolean wPower = BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID, wFunctionID, 0, 0);
				if (!wPower)
					wInstance.ErrorCode = MESException.UnPower.getValue();
			}
		} catch (Exception ex) {
			wInstance.ErrorCode = MESException.DBInstance.getValue();
			LoggerTool.SaveException("MESServer", "MES_GetDatabaseName", "Function Exception:" + ex.toString());
		}
		return wInstance;
	}

	public static int MES_CheckPowerByFunctionID(int wCompanyID, int wLoginID, int wFunctionID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>();
		try {

			wErrorCode.set(0);
			boolean wPower = BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID, wFunctionID, 0, 0);
			if (!wPower)
				wErrorCode.Result = MESException.UnPower.getValue();
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_CheckPowerByFunctionID", "Function Exception:" + ex.toString());
		}
		return wErrorCode.Result;
	}

	public static Calendar MES_GetShiftTimeByShiftID(int wCompanyID, int wShiftID) {
		Calendar wShiftTime = Calendar.getInstance();
		try {
			int wYear = wShiftID / 1000000;
			int wMonth = (wShiftID / 10000) % 100;
			int wDay = (wShiftID / 100) % 100;
			wShiftTime.set(Calendar.YEAR, wYear);
			wShiftTime.set(Calendar.MONTH, wMonth);
			wShiftTime.set(Calendar.DATE, wDay);
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_GetShiftTimeByShiftID", "Function Exception:" + ex.toString());
		}
		return wShiftTime;
	}

	public static int MES_QueryShiftID(int wCompanyID, Calendar wShiftTime, APSShiftPeriod wShiftPeriod,
			FMCShiftLevel wZoneID, int wShifts) {
		int wShiftID = 0;
		try {
			switch (wShiftPeriod) {
			case Minute:
				wShiftTime.add(Calendar.MINUTE, wShifts);
				break;
			case Hour:
				wShiftTime.add(Calendar.HOUR_OF_DAY, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMddHH"));
				break;
			case Shift:
				if ((wZoneID.getValue() <= FMCShiftLevel.MinValue() && wShifts < 0)
						|| (wZoneID.getValue() >= FMCShiftLevel.MaxValue() && wShifts > 0)) {
					wShiftTime.add(Calendar.DAY_OF_MONTH, wShifts);
					wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd0"));
					wShiftID = wShiftID + wZoneID.getValue();
				} else {
					wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd0"));
					wShiftID = wShiftID + wZoneID.getValue() + wShifts;
				}
				break;
			case Day:
				wShiftTime.add(Calendar.DAY_OF_MONTH, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd"));
				break;
			case Week:

				wShiftTime.add(Calendar.WEEK_OF_MONTH, wShifts);

				wShiftTime = MESServer.MES_QueryMondayByDate(wShiftTime);
				int wWeekOfYear = wShiftTime.get(Calendar.WEEK_OF_YEAR);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM40"));
				wShiftID = wShiftID + wWeekOfYear;
				break;
			case Month:
				wShiftTime.add(Calendar.MONTH, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM"));
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerTool.SaveException("MESServer", "MES_QueryShiftID", "Function Exception:" + ex.toString());
		}
		return wShiftID;
	}

	private static Calendar MES_QueryMondayByDate(Calendar wShiftTime) {
		Calendar wMonday = wShiftTime;
		try {
			if (wMonday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				wMonday.add(Calendar.DATE, -1);
			}
			wMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_QueryMondayByDate", "Function Exception:" + ex.toString());
		}
		return wMonday;
	}
}
