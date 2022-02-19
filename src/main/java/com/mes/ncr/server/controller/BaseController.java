package com.mes.ncr.server.controller;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.po.APIResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.DesUtil;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.SessionContants;
 

public class BaseController {

	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

	private static String RESULT_KEY = "resultCode";

	private static String RESULT_MSG = "msg";

	private static String RESULT_RETURN = "returnObject";

	private static String DATA_LIST = "list";

	private static String DATA_INFO = "info";
	
	@Autowired
	CoreService wCoreService;

	/// <summary>
	/// 获取返回ReturnObject
	/// </summary>
	/// <param name="wMsg"></param>
	/// <param name="wReturnObjectList"></param>
	/// <param name="wReturnObjectInfo"></param>
	/// <returns></returns>
	protected Map<String, Object> GetReturnObject(String wMsg, Object wReturnObjectList, Object wReturnObjectInfo) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		wResult.put(RESULT_MSG, wMsg);
		wResult.put(DATA_LIST, wReturnObjectList);
		wResult.put(DATA_INFO, wReturnObjectInfo);
		return wResult;
	}

	public static String GetProjectName(HttpServletRequest request) {
		String wResult = "";

		String wURL = request.getRequestURL().toString();
		if (wURL.indexOf("/api/") < 0)
			return wResult;

		wURL = wURL.substring(0, wURL.indexOf("/api/"));
		if (wURL.indexOf(":") < 0)
			return wResult;

		if (wURL.lastIndexOf("/") < wURL.lastIndexOf(":"))
			return wResult;

		wResult = wURL.substring(wURL.lastIndexOf("/"));

		return wResult;
	}
	
	

	/// <summary>
	/// 特殊处理返回 可修改ReturnObject后返回
	/// </summary>
	/// <param name="wResultCode"></param>
	/// <param name="wReturnObject"></param>
	/// <returns></returns>
	protected Map<String, Object> GetResult(int wResultCode, Map<String, Object> wReturnObject) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		wResult.put(RESULT_KEY, wResultCode);
		wResult.put(RESULT_RETURN, wReturnObject);
		return wResult;
	}

	/// <summary>
	/// 普通返回
	/// </summary>
	/// <param name="wResultCode"></param>
	/// <param name="wMsg"></param>
	/// <param name="wReturnObjectList">返回数组</param>
	/// <param name="wReturnObjectInfo">返回对象</param>
	/// <returns></returns>
	protected Map<String, Object> GetResult(int wResultCode, String wMsg, Object wReturnObjectList,
			Object wReturnObjectInfo) {

		return GetResult(wResultCode, GetReturnObject(wMsg, wReturnObjectList, wReturnObjectInfo));
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> SetResult(Map<String, Object> wResult, String wObjectName, Object wObject) {
		if (wResult.containsKey(RESULT_RETURN)) {
			((Map<String, Object>) wResult.get(RESULT_RETURN)).put(wObjectName, wObject);
		}

		return wResult;
	}
	
	 
	protected void SetResult(APIResult wResult, String wObjectName, Object wObject) {
		if (wResult.ReturnObejct().containsKey(wObjectName)) {
			wResult.ReturnObejct().put(wObjectName, wObject);
		} 
	}

	/// <summary>
	/// 报错返回
	/// </summary>
	/// <param name="wResultCode"></param>
	/// <param name="wMsg"></param>
	/// <returns></returns>
	protected Map<String, Object> GetResult(int wResultCode, String wMsg) {
		return GetResult(wResultCode, GetReturnObject(wMsg, null, null));
	}

	protected boolean CheckCookieEmpty(HttpServletRequest wRequest) {
		Boolean wRst = false;

		HttpSession session = wRequest.getSession();
		BMSEmployee wBMSEmployee = new BMSEmployee();
		if (session.getAttribute(SessionContants.SessionUser) != null) {
			wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
			if (wBMSEmployee == null)
				wBMSEmployee = new BMSEmployee();
		}
		if (wBMSEmployee.getID() <= 0 && wBMSEmployee.getID() != -100) {
			String wLoginName = getCookieValue(SessionContants.CookieUser, wRequest);
			String wID = getCookieValue(SessionContants.CookieUserID, wRequest);
			if (StringUtils.isEmpty(wID)) {

				wRst = true;
			} else {
				wBMSEmployee.ID = StringUtils.parseInt(wID);
			}
			if (StringUtils.isEmpty(wLoginName)) {
				wRst = true;
			} else {
				wBMSEmployee.LoginName = wLoginName;
			}

		}
		if (wBMSEmployee.getID() <= 0 && wBMSEmployee.getID() != -100) {
			wRst = true;
		}
		return wRst;

	}

	protected BMSEmployee GetSession(HttpServletRequest wRequest) {
		BMSEmployee wBMSEmployee = new BMSEmployee();
		HttpSession session = wRequest.getSession();
		if (session.getAttribute(SessionContants.SessionUser) != null) {
			wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
			if (wBMSEmployee == null)
				wBMSEmployee = new BMSEmployee();
		}
		return wBMSEmployee;
	}

	public static void RmoveSession(HttpServletRequest wRequest) {
		HttpSession session = wRequest.getSession();
		if (session.getAttribute(SessionContants.SessionUser) != null) {
			wRequest.getSession().removeAttribute(SessionContants.SessionUser);
		}
	}

//	public static void RmoveCookie(HttpServletRequest wRequest, HttpServletResponse wResponse) {
//		Cookie[] cookies = wRequest.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				cookie.setValue(null);
//				cookie.setMaxAge(0);// 立即销毁cookie
//				cookie.setPath("/");
//				wResponse.addCookie(cookie);
//			}
//		}
//	}
	
	public static void RmoveCookie(HttpServletRequest wRequest, HttpServletResponse wResponse) {
	    Cookie[] cookies = wRequest.getCookies();
	    if (cookies != null) {
	      for (Cookie cookie : cookies) {
	        cookie.setValue(null);
	        cookie.setMaxAge(0);// 立即销毁cookie
	        cookie.setPath("/");
	        wResponse.addCookie(cookie);
	      }
	    }
	  }

	public static void SetSession(HttpServletRequest wRequest, BMSEmployee wBMSEmployee) {
		if (wBMSEmployee == null)
			return;
		if (wRequest.getSession().getAttribute(SessionContants.SessionUser) != null) {
			wRequest.getSession().removeAttribute(SessionContants.SessionUser);
		}
		wRequest.getSession().setAttribute(SessionContants.SessionUser, wBMSEmployee);
		wRequest.getSession().setMaxInactiveInterval(1800);

	}

//	public static void SetCookie(HttpServletResponse wResponse, BMSEmployee wBMSEmployee) {
//		if (wBMSEmployee == null)
//			return;
//
//		Cookie co = new Cookie(SessionContants.CookieUser, wBMSEmployee.getLoginName());
//
//		co.setPath("/");
//		wResponse.addCookie(co);
//
//		Cookie co1 = new Cookie(SessionContants.CookieUserID,
//				DesUtil.encrypt(String.valueOf(wBMSEmployee.getID()), SessionContants.appSecret));
//
//		co1.setPath("/");
//		wResponse.addCookie(co1);
//
//	}

	public static String getCookieValue(String cookie_key, HttpServletRequest request) {

		String cookie_val = null;
		Cookie[] cos = request.getCookies();
		if (cos != null && cos.length > 0) {
			for (Cookie co : cos) {
				if (cookie_key.equalsIgnoreCase(co.getName())) {
					cookie_val = co.getValue();
					break;
				}
			}

		} else {
			cookie_val = StringUtils.parseString(request.getAttribute(cookie_key));
		}

		try {
			cookie_val = DesUtil.decrypt(cookie_val, SessionContants.appSecret);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			logger.error(e.getMessage(), "");
		}

		return cookie_val;
	}

	public static String CreateToken(String account) {
		String wToken = "";

		try {
			String wT4 = account.substring(0, account.length() / 2);
			String wT2 = account.substring(account.length() / 2);
			String wT3 = MessageFormat.format("{0}-{1}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1));
			String wT5 = String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			String wT1 = MessageFormat.format("{0}:{1}:{2}",
					String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE)),
					String.format("%02d", Calendar.getInstance().get(Calendar.SECOND)));

			wToken = MessageFormat.format("{0}+-abc072-+{1}+-abc072-+{2}+-abc072-+{3}+-abc072-+{4}", wT1, wT2, wT3, wT4,
					wT5);

			wToken = DesUtil.encrypt(wToken, SessionContants.appSecret);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return wToken;
	}
	
	public static void SetCookie(HttpServletRequest wRequest,HttpServletResponse wResponse, BMSEmployee wBMSEmployee) {
	    if (wBMSEmployee == null)
	      return;

	    RmoveCookie(wRequest,wResponse);
	    
	    Cookie co = new Cookie(SessionContants.CookieUser, wBMSEmployee.getLoginName());

	    co.setPath("/");
	    wResponse.addCookie(co);

	    Cookie co1 = new Cookie(SessionContants.CookieUserID,
	        DesUtil.encrypt(String.valueOf(wBMSEmployee.getID()), SessionContants.appSecret));

	    co1.setPath("/");
	    wResponse.addCookie(co1);

	  }
 

}
