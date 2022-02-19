package com.mes.ncr.server.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.shristool.LoggerTool;
import com.mes.ncr.server.utils.SessionContants;


@Component
@ServletComponentScan
@WebFilter(urlPatterns = { "/api/*" }, filterName = "TraceLogFilter")
public class TraceLogFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
	private static final String NEED_TRACE_PATH_PREFIX = "/api/";
	private static final String IGNORE_CONTENT_TYPE = "multipart/form-data";

	private static final String FILTER_APPLIED = "__spring_security_demo_trace_Filter_filterApplied";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			
			if (request.getAttribute(FILTER_APPLIED) != null) {
				if (filterChain != null)
					filterChain.doFilter(request, response);
				return;
			}
			request.setAttribute(FILTER_APPLIED, true);
			
			String wUserAgent = request.getHeader("User-Agent");

			if (StringUtils.isEmpty(wUserAgent) || !wUserAgent.startsWith("Mozilla")) {
				if (filterChain != null)
					filterChain.doFilter(request, response);
				return;
			}
			
			if (!isRequestValid(request)) {
				filterChain.doFilter(request, response);
				return;
			}
			if (!(request instanceof ContentCachingRequestWrapper)) {
				request = new ContentCachingRequestWrapper(request);
			}
			if (!(response instanceof ContentCachingResponseWrapper)) {
				response = new ContentCachingResponseWrapper(response);
			}
			int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
			long startTime = System.currentTimeMillis();
			Calendar wStartTime = Calendar.getInstance();
			try {
				filterChain.doFilter(request, response);
				status = response.getStatus();
			}finally {
				String path = request.getRequestURI();
				BMSEmployee wBMSEmployee = null;
				HttpSession session = request.getSession();
				if (session.getAttribute(SessionContants.SessionUser) != null) {
					wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
				}
				if (wBMSEmployee == null)
					wBMSEmployee = new BMSEmployee();

				if (path.startsWith(BaseController.GetProjectName(request) + NEED_TRACE_PATH_PREFIX)
						&& !IGNORE_CONTENT_TYPE.equalsIgnoreCase(request.getContentType())
						&& (wBMSEmployee.ID > 0 || wBMSEmployee.ID == -100)) {

					try {
					
						LoggerTool.SaveApiLog(wBMSEmployee.CompanyID, wBMSEmployee.ID,
								BaseController.GetProjectName(request), path, request.getMethod(),
								JSON.toJSONString(request.getParameterMap()), getRequestBody(request),
								getResponseBody(response), wStartTime, Calendar.getInstance(),
								System.currentTimeMillis() - startTime, status); // updateResponse(res);

					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Save API Log Error:" + e.getMessage());
					}

				}
				updateResponse(response);
			}
		} catch (Exception e) {
			logger.error("TraceLog Error:" + e.toString());
		}

	}

	private boolean isRequestValid(HttpServletRequest request) {
		try {
			new URI(request.getRequestURL().toString());
			return true;
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	private String getRequestBody(HttpServletRequest request) {
		String requestBody = "";
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			try {
				requestBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
			} catch (IOException e) {
				// NOOP
			}
		}
		return requestBody;
	}

	private String getResponseBody(HttpServletResponse response) {
		String responseBody = "";
		ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response,
				ContentCachingResponseWrapper.class);
		if (wrapper != null) {
			try {
				responseBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
			} catch (IOException e) {
				// NOOP
			}
		}
		return responseBody;
	}

	private void updateResponse(HttpServletResponse response) throws IOException {
		ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response,
				ContentCachingResponseWrapper.class);
		Objects.requireNonNull(responseWrapper).copyBodyToResponse();
	}

	
}
