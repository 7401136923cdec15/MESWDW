package com.mes.ncr.server.shristool;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.mes.ncr.server.service.mesenum.MESDBSource;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.service.utils.XmlTool;
import com.mes.ncr.server.utils.DBHelper;

public class LoggerTool {

	private static LoggerTool Instance;

	public static LoggerTool getInstance() {
		if (Instance == null)
			Instance = new LoggerTool();

		return Instance;
	}

	private static void Start() {
		if (Init)
			return;

		String wProjectName = Configuration.readConfigString("project.name", "application");
		String wLoggerPath = Configuration.readConfigString("logging.path", "application");

		if (StringUtils.isEmpty(wProjectName))
			wProjectName = "MESService";

		if (StringUtils.isEmpty(wLoggerPath))
			wLoggerPath = "corelogs";

		Init_LoggerPath(StringUtils.isEmpty(wProjectName) ? "MESService" : wProjectName, wLoggerPath, 15, 1, 1);

		new Thread(() -> {
			int wTicks = 0;
			while (Init) {
				try {
					Thread.sleep(1000L);
					wTicks++;
					if (wTicks > 1000)
						wTicks -= 1000;

					if (wTicks % 10 == 0) {
						LoggerTool.SaveLoggerFunctionList(MESDBSource.Basic.getDBName());
						LoggerTool.SaveApplicationFaultList(MESDBSource.Basic.getDBName());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LoggerTool.SaveException(MESDBSource.Basic.getDBName(), "MainThread", "Excetion:" + e.toString());
				}
			}

		}).start();
	}

	private static String LoggerProject = "";
	private static String LoggerPath = "";
	private static boolean Init = false;

	public static int LoggerMode = 1;
	public static int ExceptionMode = 1;
	public static int ExpiredDays = 7;

	public static List<LoggerSession> LoggerFileList = new ArrayList<>();
	public static List<LoggerModule> ModuleMonitorList = new ArrayList<>();
	public static List<LoggerFault> ApplicationFaultList = new ArrayList<>();

	private static NamedParameterJdbcTemplate nameJdbcTemplate = null;

	private static NamedParameterJdbcTemplate getNameJdbcTemplate() {
		if (nameJdbcTemplate == null)
			nameJdbcTemplate = DBHelper.getTemplate();
		return nameJdbcTemplate;
	}

	// 初始化
	public static boolean Init_LoggerPath(String wLoggerProject, String wLoggerPath, int wExpiredDays, int wLoggerMode,
			int wExceptionMode) {
		boolean wInit_Result = false;

		try {

			if (wLoggerProject.length() > 1) {
				System.out.println("Core's LoggerTool has inited");
				LoggerTool.Init = true;
				LoggerTool.LoggerProject = wLoggerProject;
				LoggerTool.LoggerMode = wLoggerMode;
				LoggerTool.ExceptionMode = wExceptionMode;
				LoggerTool.ExpiredDays = wExpiredDays;
				System.out.println("LoggerProject is " + LoggerTool.LoggerProject);
				if (wLoggerPath.length() > 1) {
					LoggerTool.LoggerPath = wLoggerPath + "/";
					File dir = new File(wLoggerPath);
					if (!dir.exists()) {
						dir.mkdirs();
					}
				} else {
					LoggerTool.LoggerPath = System.getProperty("catalina.home") + "/";
				}
				LoggerTool.Clear_ExpiredLoggerFile(LoggerTool.ExpiredDays);
				wInit_Result = true;
				LoggerTool.LoadLoggerFunctionList(MESDBSource.Basic.getDBName());
				// LoggerTool Step02
				// LoggerTool.LoadModuleConfiguration(wAPPPath +
				// "Configuration/LoggerContorl.xml");
			}

		} catch (Exception ex) {
			LoggerTool.LoggerPath = System.getProperty("catalina.home") + "/";
			wInit_Result = false;
		}
		return wInit_Result;
	}

	public static synchronized boolean LoadModuleConfiguration(String wConfigurationName) {
		boolean wInit_OK = false;
		try {
			List<LoggerContorl> wLoggerContorlList = new ArrayList<>();
			File wFile = new File(wConfigurationName);
			if (wFile.exists()) {
				wLoggerContorlList = XmlTool.ReadXml(wConfigurationName);

				wInit_OK = true;

				for (LoggerContorl wItem : wLoggerContorlList) {
					if (wItem.OutTimeMS < 1)
						wItem.OutTimeMS = 1;

					if (wItem.OutTimeMS > 50)
						wItem.OutTimeMS = 50;

					LoggerModule wModuleMonitor = new LoggerModule(wItem);

					LoggerTool.ModuleMonitorList.add(wModuleMonitor);
				}
			} else {
				LoggerTool.SaveException("LoggerTool", "LoadModuleConfiguration",
						wConfigurationName + "is not existed ");
			}
		} catch (Exception ex) {
			wInit_OK = false;
			LoggerTool.SaveException("LoggerTool", "LoadModuleConfiguration", "Function Exception:" + ex.toString());
		}
		return wInit_OK;
	}

	public static LoggerModule LoadLoggerModuleMonitorByModuleName(String wModuleName) {
		LoggerModule wModuleMonitor = new LoggerModule();
		try {
			for (LoggerModule wModuleItem : LoggerTool.ModuleMonitorList) {
				if (wModuleItem.ModuleName.equalsIgnoreCase(wModuleName)) {
					wModuleMonitor = wModuleItem;
					break;
				}
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "LoadLoggerModuleMonitorByModuleName",
					"Function Exception:" + ex.toString());
		}
		return wModuleMonitor;
	}

	// File System(Output message to file)
	private static LoggerSession SaveSessionTime(String wModuleName, String wFuncName, int wLoggerMode) {
		LoggerSession wLoggerFile = new LoggerSession(wModuleName, wFuncName, wLoggerMode);
		try {
			boolean wExistLogger = false;
			for (LoggerSession wLoggerFileC : LoggerTool.LoggerFileList) {
				if (wLoggerFileC.ModuleName.equalsIgnoreCase(wModuleName)
						&& wLoggerFileC.FuncName.equalsIgnoreCase(wFuncName)
						&& wLoggerFileC.LoggerMode == wLoggerMode) {
					wExistLogger = true;

					Calendar wCalendarL = Calendar.getInstance();
					if (wLoggerFileC.LoggerTime.getTimeInMillis() < wCalendarL.getTimeInMillis() - 200) {
						wLoggerFile.LoggerEnable = true;
						wLoggerFileC.LoggerTime = Calendar.getInstance();
					}
					break;
				}
			}
			if (!wExistLogger) {
				wLoggerFile.LoggerEnable = true;
				LoggerTool.LoggerFileList.add(wLoggerFile);
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "SaveLoggerTimeSession", "Function Exception:" + ex.toString());
		}
		return wLoggerFile;
	}

	/*
	 * public static boolean SaveException(String wModuleName, String wFunctionName,
	 * String wBugInfo) { try { if (!LoggerTool.Init) return false;
	 * 
	 * LoggerFault wException = new LoggerFault(wModuleName, wFunctionName, 1000);
	 * LoggerTool.LoggerApplicationFault(wException); LoggerSession wSessionTime =
	 * LoggerTool.SaveSessionTime(wModuleName, wFunctionName, 2);
	 * 
	 * if (wSessionTime.LoggerEnable) { String wPath_Log = LoggerTool.LoggerPath +
	 * LoggerEnumFile.Exception.getLable();
	 * 
	 * File wDir_Log = new File(wPath_Log); if (!wDir_Log.exists()) {
	 * wDir_Log.mkdirs(); } // ===获取当前时间的年份============= Calendar wCalendarC =
	 * Calendar.getInstance(); int wYearTime = wCalendarC.get(Calendar.YEAR); String
	 * wPath_NowTime = wPath_Log + "/" + wYearTime; File wDir_Now = new
	 * File(wPath_NowTime); // ======如果以当年命名的文件夹不存在就创建一个======= if
	 * (!wDir_Now.exists()) { wDir_Now.mkdir(); } //
	 * ==============创建保存文件============== String wPath_File = ""; String
	 * wMessageText = "";
	 * 
	 * switch (LoggerTool.ExceptionMode) { case 1: // 分模块打印日志 wPath_File =
	 * StringUtils.Format("{0}/{1}_{2}.out", wPath_NowTime, wModuleName,
	 * StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd")); wMessageText =
	 * StringUtils.Format("Time={0} {1}:{2}",
	 * StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName,
	 * wBugInfo); break; case 2: // 集中打印日志 wPath_File =
	 * StringUtils.Format("{0}/{1}.out", wPath_NowTime,
	 * StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd")); wMessageText =
	 * StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
	 * StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName,
	 * wBugInfo); break; default: wPath_File = StringUtils.Format("{0}/{1}.out",
	 * wPath_NowTime, StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
	 * wMessageText = StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
	 * StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName,
	 * wBugInfo); break; } // ==============创建保存文件==============
	 * WriteFile(wPath_File, wMessageText); } return true; } catch (Exception ex) {
	 * return false; } }
	 * 
	 */
	public static synchronized boolean SaveLogger(String wModuleName, String wFunctionName, String wBugInfo) {
		try {
			if (!LoggerTool.Init)
				return false;

			LoggerFault wException = new LoggerFault(wModuleName, wFunctionName, 1000);
			LoggerTool.LoggerApplicationFault(wException);
			LoggerSession wSessionTime = LoggerTool.SaveSessionTime(wModuleName, wFunctionName, 2);

			if (wSessionTime.LoggerEnable) {
				String wPath_Log = LoggerTool.LoggerPath + LoggerEnumFile.Logger.getLable();

				File wDir_Log = new File(wPath_Log);
				if (!wDir_Log.exists()) {
					wDir_Log.mkdirs();
				}
				// ===获取当前时间的年份=============
				Calendar wCalendarC = Calendar.getInstance();
				int wYearTime = wCalendarC.get(Calendar.YEAR);
				String wPath_NowTime = wPath_Log + "/" + wYearTime;
				File wDir_Now = new File(wPath_NowTime);
				// ======如果以当年命名的文件夹不存在就创建一个=======
				if (!wDir_Now.exists()) {
					wDir_Now.mkdir();
				}
				// ==============创建保存文件==============
				String wPath_File = "";
				String wMessageText = "";

				switch (LoggerTool.LoggerMode) {
				case 1: // 分模块打印日志
					wPath_File = StringUtils.Format("{0}/{1}_{2}.out", wPath_NowTime, wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}",
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				case 2: // 集中打印日志
					wPath_File = StringUtils.Format("{0}/{1}.out", wPath_NowTime,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				default:
					wPath_File = StringUtils.Format("{0}/{1}.out", wPath_NowTime,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				}
				// ==============创建保存文件==============
				FileUtils.writeStringToFile(new File(wPath_File), wMessageText, "UTF-8", true);

			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static synchronized boolean SaveException(String wModuleName, String wFunctionName, String wBugInfo) {
		try {
			if (!LoggerTool.Init)
				return false;

			LoggerFault wException = new LoggerFault(wModuleName, wFunctionName, 1000);
			LoggerTool.LoggerApplicationFault(wException);
			LoggerSession wSessionTime = LoggerTool.SaveSessionTime(wModuleName, wFunctionName, 2);

			if (wSessionTime.LoggerEnable) {
				String wPath_Log = LoggerTool.LoggerPath + LoggerEnumFile.Exception.getLable();

				File wDir_Log = new File(wPath_Log);
				if (!wDir_Log.exists()) {
					wDir_Log.mkdirs();
				}
				// ===获取当前时间的年份=============
				Calendar wCalendarC = Calendar.getInstance();
				int wYearTime = wCalendarC.get(Calendar.YEAR);
				String wPath_NowTime = wPath_Log + "/" + wYearTime;
				File wDir_Now = new File(wPath_NowTime);
				// ======如果以当年命名的文件夹不存在就创建一个=======
				if (!wDir_Now.exists()) {
					wDir_Now.mkdir();
				}
				// ==============创建保存文件==============
				String wPath_File = "";
				String wMessageText = "";

				switch (LoggerTool.ExceptionMode) {
				case 1: // 分模块打印日志
					wPath_File = StringUtils.Format("{0}/{1}_{2}.out", wPath_NowTime, wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}",
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				case 2: // 集中打印日志
					wPath_File = StringUtils.Format("{0}/{1}.out", wPath_NowTime,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				default:
					wPath_File = StringUtils.Format("{0}/{1}.out", wPath_NowTime,
							StringUtils.parseCalendarToString(wCalendarC, "yyyyMMdd"));
					wMessageText = StringUtils.Format("Time={0} {1}:{2}:{3}", wModuleName,
							StringUtils.parseCalendarToString(wCalendarC, "HH:mm:ss"), wFunctionName, wBugInfo);
					break;
				}
				// ==============创建保存文件==============

				// FileUtils.WriteFile(wPath_File, wMessageText);
				FileUtils.writeStringToFile(new File(wPath_File), wMessageText, "UTF-8", true);
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static synchronized boolean Clear_ExpiredLoggerFile(int wExpiredDays) {
		try {
			if (wExpiredDays < 7)
				wExpiredDays = 7;

			if (wExpiredDays > 30)
				wExpiredDays = 30;

			// ===获取当前时间的年份=============
			Calendar wCalendarC = Calendar.getInstance();
			Calendar wCalendarE = Calendar.getInstance();
			wCalendarE.add(Calendar.DAY_OF_MONTH, -wExpiredDays);
			int wYearTime = wCalendarC.get(Calendar.YEAR);
			int wYearTimeL = wCalendarE.get(Calendar.YEAR);

			if (wYearTime != wYearTimeL)
				wYearTime = wYearTimeL;

			Calendar wExpiredDate = wCalendarE;
			// ===异常系统日志文件=============
			String wExceptionPath_Log = LoggerTool.LoggerPath + LoggerEnumFile.Exception.getLable();
			String wExceptionPath_NowTime = wExceptionPath_Log + "/" + wYearTime;

			File wDirException_Now = new File(wExceptionPath_NowTime);
			if (wDirException_Now.exists()) {
				File[] wExceptionFiles = wDirException_Now.listFiles();
				String wFileName = "";
				for (File wExceptionFile : wExceptionFiles) {

					wFileName = wExceptionFile.getName().substring(wExceptionFile.getName().indexOf("_"));
					if (wFileName.indexOf(".") >= 0) {
						wFileName = wFileName.substring(0, wFileName.indexOf("."));
					}
					Calendar wCreateDate = StringUtils.parseCalendar(wFileName, "_yyyyMMdd");
					if (wCreateDate.compareTo(wExpiredDate) < 0)
						wExceptionFile.delete();
				}
			}

			// ===系统日志文件=============
			String wLoggerPath_Log = LoggerTool.LoggerPath + LoggerEnumFile.Logger.getLable();
			String wLoggerPath_NowTime = wLoggerPath_Log + "/" + wYearTime;
			File wDirLogger_Now = new File(wLoggerPath_NowTime);
			if (wDirLogger_Now.exists()) {
				File[] wLoggerFiles = wDirLogger_Now.listFiles();
				String wFileName = "";
				for (File wLoggerFile : wLoggerFiles) {
					wFileName = wLoggerFile.getName().substring(wLoggerFile.getName().indexOf("_"));
					if (wFileName.indexOf(".") >= 0) {
						wFileName = wFileName.substring(0, wFileName.indexOf("."));
					}
					Calendar wCreateDate = StringUtils.parseCalendar(wFileName, "_yyyyMMdd");
					if (wCreateDate.compareTo(wExpiredDate) < 0)
						wLoggerFile.delete();
				}
			}
			// ===函数监视系统日志文件=============
			String wMonitorPath_Log = LoggerTool.LoggerPath + "Logger";
			String wMonitorPath_NowTime = wMonitorPath_Log + "/" + wYearTime;
			File wDirMonitor_Now = new File(wMonitorPath_NowTime);
			if (wDirMonitor_Now.exists()) {
				File[] wMonitorFiles = wDirMonitor_Now.listFiles();
				String wFileName = "";
				for (File wMonitorFile : wMonitorFiles) {
					wFileName = wMonitorFile.getName().substring(wMonitorFile.getName().indexOf("_"));
					if (wFileName.indexOf(".") >= 0) {
						wFileName.substring(0, wFileName.indexOf("."));
					}
					Calendar wCreateDate = StringUtils.parseCalendar(wFileName, "_yyyyMMdd");
					// new SimpleDateFormat("yyyy-MM-dd").parse(wMonitorFile.getName());
					if (wCreateDate.compareTo(wExpiredDate) < 0)
						wMonitorFile.delete();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerTool.SaveException("LoggerTool", "clear_ExpiredLoggerFile", "Function Exception:" + ex.toString());
			return false;
		}
		return true;
	}

	public static synchronized int MonitorFunction(String wModuleName, String wFuncName, int wCallMS) {
		int wErrorCode = 0;
		try {

			boolean wExistModule = false;
			for (LoggerModule wModuleItem : LoggerTool.ModuleMonitorList) {
				if (wModuleItem.ModuleName.equalsIgnoreCase(wModuleName)) {
					boolean wExistFunction = false;
					for (LoggerFunction wFunctionItem : wModuleItem.FunctionList) {
						if (wFunctionItem.FuncName.equalsIgnoreCase(wFuncName)) {
							wFunctionItem.AvgCallTimes++;
							wFunctionItem.CallTimes++;
							// Step01:计算平均值
							if (wFunctionItem.AvgCallTimes > 100000000) {
								wFunctionItem.AvgCallTimes = 1;
								wFunctionItem.AvgMS = wCallMS;
								wFunctionItem.SumMS = wCallMS;
							} else {
								wFunctionItem.SumMS += wCallMS;
								wFunctionItem.AvgMS = (int) (wFunctionItem.SumMS / wFunctionItem.AvgCallTimes);
							}
							// Step02:计算最大最小值
							wFunctionItem.MaxMS = wFunctionItem.MaxMS < wCallMS ? wCallMS : wFunctionItem.MaxMS;
							wFunctionItem.MinMS = wFunctionItem.MinMS < wCallMS ? wFunctionItem.MinMS : wCallMS;

							// Step03:判断是否超时
							if (wCallMS > wModuleItem.OutTimeMS)
								wFunctionItem.CallOutTimes++;

							wFunctionItem.CallActive = true;
							wFunctionItem.CallTime = Calendar.getInstance();
							wExistFunction = true;
							break;
						}
					}
					if (!wExistFunction) {
						LoggerFunction wLoggerFunction = new LoggerFunction();
						wLoggerFunction.ModuleName = wModuleName;
						wLoggerFunction.FuncName = wFuncName;
						wLoggerFunction.AvgCallTimes = 1;
						wLoggerFunction.CallTimes = 1;
						wLoggerFunction.AvgMS = wCallMS;
						wLoggerFunction.SumMS = wCallMS;
						wLoggerFunction.MaxMS = wCallMS;
						wLoggerFunction.MinMS = wCallMS;
						wLoggerFunction.OutTimeMS = wModuleItem.OutTimeMS;
						wLoggerFunction.MonitorMode = wModuleItem.MonitorMode;
						if (wCallMS > wModuleItem.OutTimeMS)
							wLoggerFunction.CallOutTimes = 1;

						wLoggerFunction.CallActive = true;
						wLoggerFunction.CallTime = Calendar.getInstance();
						wModuleItem.FunctionList.add(wLoggerFunction);
					}
					wExistModule = true;
					break;
				}
			}
			if (!wExistModule) {
				LoggerModule wModuleItem = new LoggerModule();
				wModuleItem.ModuleName = wModuleName;

				LoggerFunction wLoggerFunction = new LoggerFunction();
				wLoggerFunction.ModuleName = wModuleItem.ModuleName;
				wLoggerFunction.FuncName = wFuncName;
				wLoggerFunction.AvgCallTimes = 1;
				wLoggerFunction.CallTimes = 1;
				wLoggerFunction.AvgMS = wCallMS;
				wLoggerFunction.SumMS = wCallMS;
				wLoggerFunction.MaxMS = wCallMS;
				wLoggerFunction.MinMS = wCallMS;
				wLoggerFunction.OutTimeMS = wModuleItem.OutTimeMS;
				wLoggerFunction.MonitorMode = wModuleItem.MonitorMode;
				if (wCallMS > wModuleItem.OutTimeMS)
					wLoggerFunction.CallOutTimes = 1;

				wLoggerFunction.CallActive = true;
				wLoggerFunction.CallTime = Calendar.getInstance();
				wModuleItem.FunctionList.add(wLoggerFunction);

				LoggerTool.ModuleMonitorList.add(wModuleItem);

			}
		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "MonitorFunction", "Function Exception" + ex.toString());
			wErrorCode = 3;
		}
		return wErrorCode;
	}

	public static synchronized int LoggerApplicationFault(LoggerFault wException) {
		int wErrorCode = 0;
		try {
			boolean wExistException = false;
			for (LoggerFault wItem : LoggerTool.ApplicationFaultList) {
				if (wItem.ModuleName.equalsIgnoreCase(wException.ModuleName)
						&& wItem.FuncName.equalsIgnoreCase(wException.FuncName)
						&& wItem.ExceptionCode == wException.ExceptionCode) {
					wItem.ExceptionTimes++;
					wItem.EndTime = Calendar.getInstance();
					wItem.Alive = true;
					wItem.CallActive = true;
					wExistException = true;

					break;
				}
			}
			if (!wExistException) {
				wException.ExceptionTimes = 1;
				wException.StartTime = Calendar.getInstance();
				wException.EndTime = Calendar.getInstance();
				wException.Alive = true;
				wException.CallActive = true;
				LoggerTool.ApplicationFaultList.add(wException);
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "LoggerApplicationFault", "Function Exception" + ex.toString());
		}
		return wErrorCode;
	}

	// Logger DB
	public static synchronized int SaveApplicationFaultList(String wDBInstamce) {
		int wErrorCode = 0;

		try {
			if (LoggerTool.LoggerProject.length() < 1)
				wErrorCode = 1;

			if (wErrorCode == 0) {
				NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();

				String wSQL = "";
				//
				for (LoggerFault wModule : LoggerTool.ApplicationFaultList) {
					if (wModule.CallActive) {

						// Step01：保存任务到数据库
						if (wDBInstamce.length() > 0)
							wSQL = StringUtils.Format("Update {0}.mbs_functionexception", wDBInstamce)
									+ " set ExceptionTimes= :ExceptionTimes,StartTime= :StartTime,EndTime= :EndTime,Alive= :Alive"
									+ " where ProjectName= :ProjectName and ModuleName= :ModuleName and FuncName= :FuncName and ExceptionCode= :ExceptionCode";
						else
							wSQL = "Update mbs_functionexception set ExceptionTimes= :ExceptionTimes,StartTime= :StartTime,EndTime= :EndTime,Alive= :Alive"
									+ " where ProjectName= :ProjectName and ModuleName= :ModuleName and FuncName= :FuncName and ExceptionCode= :ExceptionCode";

						Map<String, Object> wParms = new HashMap<String, Object>();

						wParms.put("ProjectName", LoggerTool.LoggerProject);
						wParms.put("ModuleName", wModule.ModuleName);
						wParms.put("FuncName", wModule.FuncName);
						wParms.put("ExceptionCode", wModule.ExceptionCode);

						wParms.put("ExceptionTimes", wModule.ExceptionTimes);
						wParms.put("StartTime", wModule.StartTime);
						wParms.put("EndTime", wModule.EndTime);
						wParms.put("Alive", wModule.Alive);

						int wSQL_Result = nameJdbcTemplate.update(wSQL, wParms);

						if (wSQL_Result < 1) {
							if (wDBInstamce.length() > 0)
								wSQL = StringUtils.Format("INSERT INTO {0}.mbs_functionexception", wDBInstamce)
										+ "(ProjectName,ModuleName,FuncName,ExceptionCode,ExceptionTimes,StartTime,EndTime,Alive) "
										+ "VALUES(:ProjectName,:ModuleName,:FuncName,:ExceptionCode,:ExceptionTimes,:StartTime,:EndTime,:Alive)";
							else
								wSQL = "INSERT INTO mbs_functionexception(ProjectName,ModuleName,FuncName,ExceptionCode,ExceptionTimes,StartTime,EndTime,Alive) "
										+ "VALUES(:ProjectName,:ModuleName,:FuncName,:ExceptionCode,:ExceptionTimes,:StartTime,:EndTime,:Alive)";
							wParms.clear();
							wParms.put("ProjectName", LoggerTool.LoggerProject);
							wParms.put("ModuleName", wModule.ModuleName);
							wParms.put("FuncName", wModule.FuncName);
							wParms.put("ExceptionCode", wModule.ExceptionCode);

							wParms.put("ExceptionTimes", wModule.ExceptionTimes);
							wParms.put("StartTime", wModule.StartTime);
							wParms.put("EndTime", wModule.EndTime);
							wParms.put("Alive", wModule.Alive);
							wSQL_Result = nameJdbcTemplate.update(wSQL, wParms);
						}
						wModule.CallActive = false;
					}
				}
			}

		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "SaveApplicationExceptionList",
					"Function Exception: " + ex.toString());
			wErrorCode = 2;
		}
		return wErrorCode;
	}

	public static synchronized int SaveLoggerFunctionList(String wDBInstamce) {
		int wErrorCode = 0;
		if (LoggerTool.LoggerProject.length() < 1)
			wErrorCode = 1;

		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		try {

			String wSQL = "";

			for (LoggerModule wModule : LoggerTool.ModuleMonitorList) {
				for (LoggerFunction wFunction : wModule.FunctionList) {
					if (wFunction.CallActive) {
						// Step01：保存任务到数据库
						if (wDBInstamce.length() > 0)
							wSQL = StringUtils.Format("Update {0}.mbs_functionmonitor", wDBInstamce)
									+ " set CallTimes= :CallTimes,AvgCallTimes= :AvgCallTimes,CallOutTimes= :CallOutTimes,AvgMS= :AvgMS,MaxMS= :MaxMS,MinMS= :MinMS,"
									+ "OutTimeMS= :OutTimeMS,SumMS= :SumMS,CallTime= :CallTime,MonitorMode= :MonitorMode where ProjectName= :ProjectName and ModuleName= :ModuleName and FuncName= :FuncName";
						else
							wSQL = "Update mbs_functionmonitor set CallTimes= :CallTimes,AvgCallTimes= :AvgCallTimes,CallOutTimes= :CallOutTimes,AvgMS= :AvgMS,MaxMS= :MaxMS,MinMS= :MinMS,"
									+ "OutTimeMS= :OutTimeMS,SumMS= :SumMS,CallTime= :CallTime,MonitorMode= :MonitorMode where ProjectName= :ProjectName and ModuleName= :ModuleName and FuncName= :FuncName";

						Map<String, Object> wParms = new HashMap<String, Object>();
						wParms.put("ProjectName", LoggerTool.LoggerProject);
						wParms.put("ModuleName", wFunction.ModuleName);
						wParms.put("FuncName", wFunction.FuncName);
						wParms.put("CallTimes", wFunction.CallTimes);
						wParms.put("AvgCallTimes", wFunction.AvgCallTimes);
						wParms.put("CallOutTimes", wFunction.CallOutTimes);

						wParms.put("AvgMS", wFunction.AvgMS);
						wParms.put("MaxMS", wFunction.MaxMS);
						wParms.put("MinMS", wFunction.MinMS);
						wParms.put("OutTimeMS", wFunction.OutTimeMS);
						wParms.put("SumMS", wFunction.SumMS);

						wParms.put("CallTime", wFunction.CallTime);
						wParms.put("MonitorMode", wFunction.MonitorMode);
						int wSQL_Result = nameJdbcTemplate.update(wSQL, wParms);

						if (wSQL_Result < 1) {
							if (wDBInstamce.length() > 0)
								wSQL = StringUtils.Format("INSERT INTO {0}.mbs_functionmonitor", wDBInstamce)
										+ "(ProjectName,ModuleName,FuncName,CallTimes,AvgCallTimes,CallOutTimes,AvgMS,MaxMS,MinMS,OutTimeMS,SumMS,CallTime,MonitorMode) "
										+ "VALUES(:ProjectName,:ModuleName,:FuncName,:CallTimes,:AvgCallTimes,:CallOutTimes,:AvgMS,:MaxMS,:MinMS,:OutTimeMS,:SumMS,:CallTime,:MonitorMode)";
							else
								wSQL = "INSERT INTO mbs_functionmonitor(ProjectName,ModuleName,FuncName,CallTimes,AvgCallTimes,CallOutTimes,AvgMS,MaxMS,MinMS,OutTimeMS,SumMS,CallTime,MonitorMode) "
										+ "VALUES(:ProjectName,:ModuleName,:FuncName,:CallTimes,:AvgCallTimes,:CallOutTimes,:AvgMS,:MaxMS,:MinMS,:OutTimeMS,:SumMS,:CallTime,:MonitorMode)";
							wParms.clear();
							wParms.put("ProjectName", LoggerTool.LoggerProject);
							wParms.put("ModuleName", wFunction.ModuleName);
							wParms.put("FuncName", wFunction.FuncName);
							wParms.put("CallTimes", wFunction.CallTimes);
							wParms.put("AvgCallTimes", wFunction.AvgCallTimes);
							wParms.put("CallOutTimes", wFunction.CallOutTimes);

							wParms.put("AvgMS", wFunction.AvgMS);
							wParms.put("MaxMS", wFunction.MaxMS);
							wParms.put("MinMS", wFunction.MinMS);
							wParms.put("OutTimeMS", wFunction.OutTimeMS);
							wParms.put("SumMS", wFunction.SumMS);

							wParms.put("CallTime", wFunction.CallTime);
							wParms.put("MonitorMode", wFunction.MonitorMode);
							wSQL_Result = nameJdbcTemplate.update(wSQL, wParms);
						}
						wFunction.CallActive = false;
					}
				}
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "SaveLoggerFunctionList", "Function Exception: " + ex.toString());
			wErrorCode = 2;
		}
		return wErrorCode;
	}

	public static int LoadLoggerFunctionList(String wDBInstamce) {
		int wErrorCode = 0;
		if (LoggerTool.LoggerProject.length() < 1)
			wErrorCode = 1;
		try {
			List<LoggerFunction> wLoggerFunctionList = new ArrayList<LoggerFunction>();
			NamedParameterJdbcTemplate nameJdbcTemplate = LoggerTool.getNameJdbcTemplate();
			String wSQL = "";

			// Step01：查询数据库
			if (wDBInstamce.length() > 0)
				wSQL = StringUtils.Format("Select * from {0}.mbs_functionmonitor", wDBInstamce)
						+ " where ProjectName= :ProjectName";
			else
				wSQL = "Select * from mbs_functionmonitor where ProjectName= :ProjectName";
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ProjectName", LoggerTool.LoggerProject);
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQL, wParms);
			for (Map<String, Object> wSqlDataReader : wQueryResultList) {
				// wSqlDataReader\[(\"\w+\")\] wSqlDataReader.get($1)
				LoggerFunction wFunction = new LoggerFunction();
				wFunction.ModuleName = StringUtils.parseString(wSqlDataReader.get("ModuleName"));
				wFunction.FuncName = StringUtils.parseString(wSqlDataReader.get("FuncName"));
				wFunction.CallTimes = StringUtils.parseLong(wSqlDataReader.get("CallTimes"));
				wFunction.AvgCallTimes = StringUtils.parseLong(wSqlDataReader.get("AvgCallTimes"));
				wFunction.CallOutTimes = StringUtils.parseLong(wSqlDataReader.get("CallOutTimes"));
				wFunction.AvgMS = StringUtils.parseInt(wSqlDataReader.get("AvgMS"));
				wFunction.MaxMS = StringUtils.parseInt(wSqlDataReader.get("MaxMS"));
				wFunction.MinMS = StringUtils.parseInt(wSqlDataReader.get("MinMS"));
				wFunction.SumMS = StringUtils.parseLong(wSqlDataReader.get("SumMS"));
				wFunction.OutTimeMS = StringUtils.parseInt(wSqlDataReader.get("OutTimeMS"));
				wFunction.CallTime = StringUtils.parseCalendar(wSqlDataReader.get("CallTime"));
				wFunction.MonitorMode = StringUtils.parseBoolean(wSqlDataReader.get("MonitorMode"));
				wFunction.CallFunction = true;
				wLoggerFunctionList.add(wFunction);
			}

			for (LoggerFunction wFunctionItem : wLoggerFunctionList) {
				boolean wFunctionExist = false;

				for (LoggerModule wModuleItem : LoggerTool.ModuleMonitorList) {
					if (wModuleItem.ModuleName.equalsIgnoreCase(wFunctionItem.ModuleName)) {
						for (LoggerFunction wFunctionItemM : wModuleItem.FunctionList) {
							if (wFunctionItemM.FuncName.equalsIgnoreCase(wFunctionItem.FuncName)) {
								wFunctionItemM.ModuleName = wFunctionItem.ModuleName;
								wFunctionItemM.FuncName = wFunctionItem.FuncName;
								wFunctionItemM.CallTimes = wFunctionItem.CallTimes;
								wFunctionItemM.CallOutTimes = wFunctionItem.CallOutTimes;
								wFunctionItemM.AvgMS = wFunctionItem.AvgMS;
								wFunctionItemM.MaxMS = wFunctionItem.MaxMS;
								wFunctionItemM.MinMS = wFunctionItem.MinMS;
								wFunctionItemM.OutTimeMS = wFunctionItem.OutTimeMS;
								wFunctionItemM.CallTime = wFunctionItem.CallTime;
								wFunctionItemM.CallFunction = wFunctionItem.CallFunction;
								wFunctionExist = true;
							}
						}
						if (!wFunctionExist) {
							wFunctionItem.OutTimeMS = wModuleItem.OutTimeMS;
							wModuleItem.FunctionList.add(wFunctionItem);
						}
						break;
					}
				}
			}
		} catch (Exception ex) {
			wErrorCode = -3;
			LoggerTool.SaveException("LoggerTool", "LoadLoggerFunctionList ", "Function Exception: " + ex.toString());
		}
		return wErrorCode;
	}

	public static synchronized int ResetLoggerFunctionList(String wDBInstamce) {
		int wErrorCode = 0;
		if (LoggerTool.LoggerProject.length() < 1)
			wErrorCode = 1;

		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		try {
			String wSQL = "";
			// Step01：查询数据库
			if (wDBInstamce.length() > 0)
				wSQL = StringUtils.Format("delete from {0}.mbs_functionmonitor", wDBInstamce)
						+ " where ProjectName= :ProjectName";
			else
				wSQL = "delete from mbs_functionmonitor where ProjectName= :ProjectName";

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ProjectName", LoggerTool.LoggerProject);

			nameJdbcTemplate.update(wSQL, wParms);

			for (LoggerModule wModuleItem : LoggerTool.ModuleMonitorList) {
				wModuleItem.FunctionList.clear();
			}
		} catch (Exception ex) {
			wErrorCode = -3;
			LoggerTool.SaveException("LoggerTool", "ResetLoggerFunctionList ", "Function Exception: " + ex.toString());
		}
		return wErrorCode;
	}

	public static void SaveApiLog(int wCompanyID, int wLoginID, String wProjectName, String wURI, String wMethod,
			String wParams, String wRequestBody, String wResult, Calendar wRequestTime, Calendar wResponseTime,
			long wInterval, int wStatus) {
		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		try {
			if (wParams == null)
				wParams = "";
			if (wResult == null)
				wResult = "";
			if (wProjectName == null)
				wProjectName = "";
			if (wURI == null || wMethod == null)
				return;

			if (wParams.length() > 200) {
				wParams = wParams.substring(0, 200) + "...";
			}
			if (wRequestBody.length() > 200) {
				wRequestBody = wRequestBody.substring(0, 200) + "...";
			}
			if (wResult.length() > 200) {
				wResult = wResult.substring(0, 200) + "...";
			}

			String wSQL = StringUtils.Format(
					"INSERT INTO {0}.mbs_apilog (CompanyID,LoginID,ProjectName,URI,Method,Params,RequestBody,Result,RequestTime,ResponseTime,IntervalTime,ResponseStatus)"
							+ " Values (:CompanyID, :LoginID,:ProjectName,:URI,:Method,:Params,:RequestBody,:Result,:RequestTime,:ResponseTime,:IntervalTime,:ResponseStatus)",
					MESDBSource.Basic.getDBName());

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("CompanyID", wCompanyID);
			wParms.put("LoginID", wLoginID);
			wParms.put("ProjectName", wProjectName);
			wParms.put("URI", wURI);
			wParms.put("Method", wMethod);
			wParms.put("Params", wParams);
			wParms.put("Result", wResult);
			wParms.put("RequestTime", wRequestTime);
			wParms.put("ResponseTime", wResponseTime);
			wParms.put("RequestBody", wRequestBody);
			wParms.put("IntervalTime", wInterval);
			wParms.put("ResponseStatus", wStatus);
			nameJdbcTemplate.update(wSQL, wParms);

		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "SaveApiLog", "Function Exception: " + ex.toString());
		}
	}

	public static List<Map<String, Object>> GetApiLog(int wCompanyID, int wLoginID, String wProjectName,
			Calendar wStartTime, Calendar wEndTime) {
		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		List<Map<String, Object>> wResult = new ArrayList<>();
		try {
			if (wProjectName == null)
				wProjectName = "";

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResult;

			String wSQL = StringUtils.Format("SELECT * FROM {0}.mbs_apilog WHERE 1=1 "
					+ "AND (:CompanyID <= 0  OR CompanyID = :CompanyID) "
					+ "AND (:LoginID <= 0  OR LoginID = :LoginID) "
					+ "AND (:ProjectName =''''  OR  ProjectName = :ProjectName) "
					+ "AND ( :StartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :StartTime <= mbs_apilog.ResponseTime) "
					+ "AND ( :EndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :EndTime >= mbs_apilog.RequestTime) ",
					MESDBSource.Basic.getDBName());

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("CompanyID", wCompanyID);
			wParms.put("LoginID", wLoginID);
			wParms.put("ProjectName", wProjectName);
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			wResult = nameJdbcTemplate.queryForList(wSQL, wParms);

		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "GetApiLog", "Function Exception: " + ex.toString());
		}
		return wResult;
	}

	public static List<Map<String, Object>> GetLoggerFunction(String wProjectName) {
		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		List<Map<String, Object>> wResult = new ArrayList<>();
		try {
			if (wProjectName == null)
				wProjectName = "";

			String wSQL = StringUtils.Format("Select * from {0}.mbs_functionmonitor where ProjectName= :ProjectName",
					MESDBSource.Basic.getDBName());

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ProjectName", LoggerTool.LoggerProject);
			wResult = nameJdbcTemplate.queryForList(wSQL, wParms);

		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "GetLoggerFunction", "Function Exception: " + ex.toString());
		}
		return wResult;
	}

	public static List<Map<String, Object>> GetExceptionFunction(String wProjectName) {
		NamedParameterJdbcTemplate nameJdbcTemplate = getNameJdbcTemplate();
		List<Map<String, Object>> wResult = new ArrayList<>();
		try {
			if (wProjectName == null)
				wProjectName = "";

			String wSQL = StringUtils.Format("Select * from {0}.mbs_functionexception where ProjectName= :ProjectName",
					MESDBSource.Basic.getDBName());

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ProjectName", LoggerTool.LoggerProject);
			wResult = nameJdbcTemplate.queryForList(wSQL, wParms);

		} catch (Exception ex) {
			LoggerTool.SaveException("LoggerTool", "GetExceptionFunction", "Function Exception: " + ex.toString());
		}
		return wResult;
	}

	static {
		Start();
	}
}
