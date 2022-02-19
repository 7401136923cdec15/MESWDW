package com.mes.ncr.server.shristool;

import java.util.Calendar;
  
public class CalendarDAO {
	private static CalendarDAO Instance ;

	private CalendarDAO() {
		super();
	}

	public static CalendarDAO getInstance() {
		if (Instance == null)
			Instance = new CalendarDAO();
		return Instance;
	}
	//
	public Calendar getDate(Calendar wCalendar,int wDays) 
	{
		Calendar wDate=(Calendar)wCalendar.clone();
		wDate.add(Calendar.DATE, wDays);
		wDate.set(Calendar.HOUR_OF_DAY,0);
		wDate.set(Calendar.MINUTE,0);
		wDate.set(Calendar.SECOND,0);
		wDate.set(Calendar.MILLISECOND,0);
		return wDate;
	}
	//new DateTime(2016, 1, 1) CalendarDAO.getInstance().setDate(2016, 1, 1)
	public Calendar setDate(int Year,int wMonth,int wDays) 
	{
		Calendar wDate=Calendar.getInstance();

		wDate.set(Year, wMonth, wDays,0,0,0);
		wDate.set(Calendar.MILLISECOND,0);
		return wDate;
	}
	public Calendar getDate() 
	{
		Calendar wDate=Calendar.getInstance();
		wDate.set(Calendar.HOUR_OF_DAY,0);
		wDate.set(Calendar.MINUTE,0);
		wDate.set(Calendar.SECOND,0);
		wDate.set(Calendar.MILLISECOND,0);
		return wDate;
	}
	public int subBySecond(Calendar wCalendar1,Calendar wCalendar2) 
	{
		int wSeconds=0;
		long wMillis1=wCalendar1.getTimeInMillis();
		long wMillis2=wCalendar2.getTimeInMillis();
		
		wSeconds= (int)((wMillis1- wMillis2)/1000);
		
		return wSeconds;
	}
	//wErrorCode = wInstance.ErrorCode;
	//ServiceResult<String> wInstance = MESServer.MES_GetDatabaseName(wCompanyID, MESDBSource.Basic);
	//Map<String, Object> wParms = new HashMap<String, Object>();
	//SCMSupplierDAO.getInstance().SCM_QuerySupplierByID
	//BMSEmployeeDAO.getInstance().BMS_QueryEmployeeNameByID
	//FMCFactoryDAO.getInstance().FMC_QueryFactoryNameByID
	//FMCFactoryDAO.getInstance().FMC_QueryWorkShopNameByID
	//FMCLineDAO.getInstance().FMC_QueryLineNameByID
	//CRMCustomerDAO.getInstance().CRM_QueryCustomerByID
	//wSqlDataReader\[(\"\w+\")\] wSqlDataReader.get($1)
	
	//wSQLText = this.DMLChange(wSQLText);List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);for (Map<String, Object> wSqlDataReader : wQueryResultList) {
    //wSQLText = this.DMLChange(wSQLText);KeyHolder keyHolder = new GeneratedKeyHolder();SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParms);nameJdbcTemplate.update(wSQLText, wSqlParameterSource, keyHolder);wID = keyHolder.getKey().intValue();
}
