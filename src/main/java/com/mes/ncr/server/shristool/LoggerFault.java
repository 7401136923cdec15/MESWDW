package com.mes.ncr.server.shristool;

import java.util.Calendar;

public class LoggerFault {
	public String ModuleName = "";
	public String FuncName = "";
	public int ExceptionCode = 0;
	public long ExceptionTimes = 0;
	public Calendar StartTime = Calendar.getInstance();
	public Calendar EndTime = Calendar.getInstance();
	public boolean Alive = false;
	public boolean CallActive = false;

	public LoggerFault(String wModuleName, String wFuncName, int wExceptionCode) {
		this.ModuleName = wModuleName;
		this.FuncName = wFuncName;
		this.ExceptionCode = wExceptionCode;
		this.ExceptionTimes = 1;
		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
		this.Alive = true;
	}
}
