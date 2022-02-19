package com.mes.ncr.server.utils;

public class RetCode
{
	
	public static int    SERVER_CODE_SUC                                        = 1000;
	public static int    SERVER_CODE_ERR                                        = 9999;
	public static String SERVER_CODE_ERR_MSG                                    = "处理异常";
	public static int    SERVER_CODE_UNLOGIN                                    = 9998; 
	public static String SERVER_CODE_UNLOGIN_ALARM_NOUSER                       = "此账号不存在"; 
	public static String SERVER_CODE_UNLOGIN_ALARM_NOPD                         = "密码错误!";  
	public static String SERVER_CODE_UNLOGIN_ALARM_TOKEN                        = "免密验证失败!";  
	
	public static String SERVER_CODE_UNROLE = "无权限！";

	public static String SERVER_CODE_UNLOGIN_ALARM                              = "用户名或密码不正确!";  
	public static String SERVER_CODE_UNLOGIN_ALARM_NOMAC                        = "终端不匹配！"; 
	
	public static int    LOGIN_ERR_CODE_LOGIN_FAIL                         = 9997;
	public static int    PERMISSION_DENIED				                        = 9994;
	
	public static String SERVER_RST_NULL                                        ="服务返回值为空！";
	public static String SERVER_RST_ERROR_IN                                    ="服务器内部错误！";
	public static String SERVER_RST_ERROR_FAILED                                ="操作失败！";
	public static String SERVER_RST_ERROR_OUT                                   ="输入参数错误或参数不合法！";
}
