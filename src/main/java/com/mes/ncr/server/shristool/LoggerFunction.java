package com.mes.ncr.server.shristool;

import java.io.Serializable;
import java.util.Calendar;

public class LoggerFunction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String ModuleName = "";
	public String FuncName = "";
	public long CallTimes = 0; // 调用次数
	public long AvgCallTimes = 0; // 调用次数(平均值)
	public long CallOutTimes = 0; // 函数超时次数
	public int AvgMS = 0; // 函数平均耗时时长
	public int MaxMS = 0; // 最大耗时时长
	public int MinMS = 0; // 最小耗时时长
	public long SumMS = 0; // 累计耗时时长
	public int OutTimeMS = 10; // 超时时长阀值
	public Calendar CallTime = Calendar.getInstance();
	public boolean CallActive = false; // 是否被调用
	public boolean CallFunction = false; // 是否被保存过
	public boolean MonitorMode = false; // 是否被监控

	public LoggerFunction() {
		this.ModuleName = "";
		this.FuncName = "";
		this.CallTimes = 0;
		this.CallOutTimes = 0;
		this.AvgMS = 0;
		this.MaxMS = 0;
		this.MinMS = 10000000;
		this.SumMS = 0;
		this.OutTimeMS = 10;
		this.CallTime = Calendar.getInstance();
		this.CallTime.add(Calendar.YEAR, -1);
		this.CallActive = false;
		this.MonitorMode = false;
	}

	public String getModuleName() {
		return ModuleName;
	}

	public void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}

	public String getFuncName() {
		return FuncName;
	}

	public void setFuncName(String funcName) {
		FuncName = funcName;
	}

	public long getCallTimes() {
		return CallTimes;
	}

	public void setCallTimes(long callTimes) {
		CallTimes = callTimes;
	}

	public long getAvgCallTimes() {
		return AvgCallTimes;
	}

	public void setAvgCallTimes(long avgCallTimes) {
		AvgCallTimes = avgCallTimes;
	}

	public long getCallOutTimes() {
		return CallOutTimes;
	}

	public void setCallOutTimes(long callOutTimes) {
		CallOutTimes = callOutTimes;
	}

	public int getAvgMS() {
		return AvgMS;
	}

	public void setAvgMS(int avgMS) {
		AvgMS = avgMS;
	}

	public int getMaxMS() {
		return MaxMS;
	}

	public void setMaxMS(int maxMS) {
		MaxMS = maxMS;
	}

	public int getMinMS() {
		return MinMS;
	}

	public void setMinMS(int minMS) {
		MinMS = minMS;
	}

	public long getSumMS() {
		return SumMS;
	}

	public void setSumMS(long sumMS) {
		SumMS = sumMS;
	}

	public int getOutTimeMS() {
		return OutTimeMS;
	}

	public void setOutTimeMS(int outTimeMS) {
		OutTimeMS = outTimeMS;
	}

	public Calendar getCallTime() {
		return CallTime;
	}

	public void setCallTime(Calendar callTime) {
		CallTime = callTime;
	}

	public boolean isCallActive() {
		return CallActive;
	}

	public void setCallActive(boolean callActive) {
		CallActive = callActive;
	}

	public boolean isCallFunction() {
		return CallFunction;
	}

	public void setCallFunction(boolean callFunction) {
		CallFunction = callFunction;
	}

	public boolean isMonitorMode() {
		return MonitorMode;
	}

	public void setMonitorMode(boolean monitorMode) {
		MonitorMode = monitorMode;
	}
}
