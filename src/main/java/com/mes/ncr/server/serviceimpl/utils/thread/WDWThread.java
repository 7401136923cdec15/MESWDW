package com.mes.ncr.server.serviceimpl.utils.thread;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.mes.ncr.server.service.po.OutResult;
import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.po.bms.BMSEmployee;
import com.mes.ncr.server.service.po.mtc.MTCTask;
import com.mes.ncr.server.service.po.ncr.NCRTask;
import com.mes.ncr.server.serviceimpl.dao.BaseDAO;
import com.mes.ncr.server.serviceimpl.dao.mtc.MTCTaskDAO;
import com.mes.ncr.server.serviceimpl.dao.ncr.NCRTaskDAO;
import com.mes.ncr.server.serviceimpl.utils.WDWConstans;

@Component
public class WDWThread implements DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(WDWThread.class);

	private static WDWThread Instance;

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.AdminUser = this.AdminUser;
		Run();
		// 初使化时将已静态化的testService实例化
	}

	private BMSEmployee AdminUser = new BMSEmployee();

	public WDWThread() {
		super();
		AdminUser = BaseDAO.SysAdmin;
	}

	boolean mIsStart = false;

	private void Run() {
		try {
			if (mIsStart)
				return;
			mIsStart = true;
			logger.info("WDW Start!!");
			new Thread(() -> {
				while (mIsStart) {
					try {
						this.MTCBiz();
						this.NCRBiz();
						this.RROBiz();
						Thread.sleep(10000);
					} catch (Exception ex) {
						logger.error(ex.toString());
					}
				}
			}).start();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void MTCBiz() {
		try {
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
				MTCTask wMTCTask = null;
				for (int i = 0; i < WDWConstans.MTCTaskSGAuditResource.size(); i++) {
					wMTCTask = WDWConstans.MTCTaskSGAuditResource.get();
					boolean wIsTrue = MTCTaskDAO.getInstance().AutoSGAudit(this.AdminUser, wMTCTask, wErrorCode);
					if (!wIsTrue)
						WDWConstans.MTCTaskSGAuditResource.add(wMTCTask);
				}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void NCRBiz() {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<Boolean> wServiceResult = null;
			NCRTask wNCRTask = null;
			for (int i = 0; i < WDWConstans.NCRTaskResource.size(); i++) {
				wNCRTask = WDWConstans.NCRTaskResource.get();
				wServiceResult = NCRTaskDAO.getInstance().TriggerOrUpdateTask(this.AdminUser, wNCRTask, wErrorCode);
				if (!wServiceResult.getResult())
					WDWConstans.NCRTaskResource.add(wNCRTask);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
	
	private void RROBiz() {
		try {
			

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void destroy() throws Exception {
		try {
			mIsStart = false;
			WDWConstans.MTCTaskJSAuditResource.Save();
			WDWConstans.MTCTaskSGAuditResource.Save();
			WDWConstans.MTCTaskCompletionResource.Save();
			WDWConstans.RROTaskToConfirmedResource.Save();
			WDWConstans.RROItemTaskItemFailResource.Save();
//			WDWConstans.RROItemTaskConfirmedResource.Save();
			WDWConstans.NCRTaskToCheckWriteResource.Save();
			WDWConstans.NCRTaskRejectedResource.Save();
			WDWConstans.NCRTaskResource.Save();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

}
