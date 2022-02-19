package com.mes.ncr.server.controller.test;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.ncr.server.controller.BaseController;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.serviceimpl.MTCServiceImpl;
import com.mes.ncr.server.utils.RetCode;

/**
 * 测试控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-2 16:57:38
 */
@RestController
@RequestMapping("/api/Test")
public class TestController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(TestController.class);

	/**
	 * 接口测试
	 */
	@GetMapping("/Test")
	public Object Test(HttpServletRequest request, HttpServletResponse response) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Export(wLoginUser, response);

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	public void Export(BMSEmployee wLoginUser, HttpServletResponse response) {
		ServiceResult<List<String>> wRst = MTCServiceImpl.getInstance().MTC_QueryMTCPartNoList(wLoginUser);
		System.out.println(wRst.Result);
	}
}
