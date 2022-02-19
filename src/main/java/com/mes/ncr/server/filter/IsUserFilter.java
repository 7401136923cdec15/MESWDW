package com.mes.ncr.server.filter;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.CoreService;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.DesUtil;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.shristool.LoggerTool;
import com.mes.ncr.server.utils.SessionContants;

@Component
@ServletComponentScan
@WebFilter(urlPatterns = { "/api/*" }, filterName = "securityRequestFilter")
public class IsUserFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(IsUserFilter.class);

	private static final String NEED_TRACE_PATH_PREFIX = "/api/";

	private static IsUserFilter Instance;
	@Autowired
	CoreService wCoreService;

	private static final String FILTER_APPLIED = "__spring_security_demoFilter_filterApplied";

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.wCoreService = this.wCoreService;
		// 初使化时将已静态化的testService实例化
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			HttpServletResponse res = (HttpServletResponse) response;
			HttpServletRequest req = (HttpServletRequest) request;

			req.setCharacterEncoding("UTF-8");

			if (request.getAttribute(FILTER_APPLIED) != null) {
				if (chain != null)
					chain.doFilter(request, response);
				return;
			}
			request.setAttribute(FILTER_APPLIED, true);

			String wURI = req.getRequestURI();
			if (!wURI.startsWith(BaseController.GetProjectName(req) + NEED_TRACE_PATH_PREFIX)) {
				if (chain != null) {
					chain.doFilter(request, response);
				}
				return;
			}

			BMSEmployee wBMSEmployee = new BMSEmployee();
			try {

				// 没有判断参数中是否有user字段
				HttpSession session = req.getSession();
				if (session.getAttribute(SessionContants.SessionUser) != null) {
					wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
					if (wBMSEmployee == null)
						wBMSEmployee = new BMSEmployee();
				}

				Map<String, String[]> wParameters = req.getParameterMap();
				if (wParameters != null && wParameters.size() > 0 && Instance != null) {

					String user_info = req.getParameter(SessionContants.USER_INFO);
					String user_password = req.getParameter(SessionContants.USER_PASSWORD);

					if (StringUtils.isNotEmpty(user_info) && StringUtils.isNotEmpty(user_password)) {
						user_info = user_info.replaceAll(" ", "+");
						user_password = user_password.replaceAll(" ", "+");

						user_info = DesUtil.decrypt(user_info, SessionContants.appSecret);
						user_password = DesUtil.decrypt(user_password, SessionContants.appSecret);

						wBMSEmployee = Instance.wCoreService.BMS_LoginEmployee(user_info, user_password, "", 0, 0)
								.Info(BMSEmployee.class);

						if (wBMSEmployee != null && (wBMSEmployee.getID() > 0 || wBMSEmployee.ID == -100)) {
							BaseController.SetSession(req, wBMSEmployee);
							BaseController.SetCookie(req, res, wBMSEmployee);
						}
					}
				}

				String user_info = BaseController.getCookieValue(SessionContants.CookieUser, req);
				if (StringUtils.isNotEmpty(user_info) && !user_info.equalsIgnoreCase(wBMSEmployee.getLoginName())) {
					String wToken = BaseController.CreateToken(user_info);
					wBMSEmployee = Instance.wCoreService.BMS_LoginEmployee(user_info, "", wToken, 0, 0)
							.Info(BMSEmployee.class);

					if (wBMSEmployee != null && (wBMSEmployee.getID() > 0 || wBMSEmployee.ID == -100)) {
						BaseController.SetSession(req, wBMSEmployee);
						BaseController.SetCookie(req, res, wBMSEmployee);
					}
				}

			} catch (Exception e) {
				logger.error("Error Session: " + e.toString());
			}

			// iframe引起的内部cookie丢失
			res.setHeader("P3P", "CP=CAO PSA OUR");
			res.setHeader("Access-Control-Allow-Origin", "*");

			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Methods", "*");
			res.setHeader("Access-Control-Allow-Headers", "Content-Type,Access-Token");
			res.setHeader("Access-Control-Expose-Headers", "*");

			if (chain != null) {

				long wStartMillis = System.currentTimeMillis();

				try {
					chain.doFilter(request, response);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("doFilter Error:" + e.getMessage());
				}

				if (wURI.startsWith("/"))
					wURI = wURI.substring(1);

				int wKeyIndex = wURI.lastIndexOf("/");

				String wModuleName = "";
				String wFuncName = "";
				if (wKeyIndex >= 0) {
					wModuleName = wURI.substring(0, wKeyIndex);
					wFuncName = wURI.substring(wKeyIndex);
					long wEndMillis = System.currentTimeMillis();
					int wCallMS = (int) (wEndMillis - wStartMillis);
					LoggerTool.MonitorFunction(wModuleName, wFuncName, wCallMS);
				} else {
					logger.error("Error URI: " + wURI);

				}
			}
		} catch (Exception e) {
			logger.error("Error UserFilter: " + e.toString());
		}
	}

	@Override
	public void destroy() {

	}

}
