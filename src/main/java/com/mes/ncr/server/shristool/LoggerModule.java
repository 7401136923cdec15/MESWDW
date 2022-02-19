package com.mes.ncr.server.shristool;

import java.util.ArrayList;
import java.util.List;
public class LoggerModule {

	public String ModuleName = "";
	public boolean MonitorMode = false;
	public int OutTimeMS = 10;
	public List<LoggerFunction> FunctionList = new ArrayList<>();

	public LoggerModule() {
		this.ModuleName = "";
		this.MonitorMode = false;
		this.OutTimeMS = 10;
		this.FunctionList = new ArrayList<>();
	}

	public LoggerModule(LoggerContorl wLoggerContorl) {
		this.ModuleName = wLoggerContorl.ModuleName;
		this.MonitorMode = wLoggerContorl.MonitorMode;
		this.OutTimeMS = wLoggerContorl.OutTimeMS;
		this.FunctionList = new ArrayList<>();
	}
}
