package com.mes.ncr.server.shristool;

import java.util.Calendar;

public class LoggerSession {
	
	public String ModuleName = "";
	public String FuncName = "";
	public int LoggerMode = 0; // 1:调试日志；2：异常日志
	public Calendar LoggerTime = Calendar.getInstance();
	public boolean LoggerEnable = false;

	public LoggerSession(String wModuleName, String wFuncName, int wLoggerMode) {
		this.ModuleName = wModuleName;
		this.FuncName = wFuncName;
		this.LoggerMode = wLoggerMode;
		this.LoggerTime = Calendar.getInstance();
		this.LoggerEnable = false;
	}
}
