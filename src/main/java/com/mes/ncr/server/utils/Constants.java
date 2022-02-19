package com.mes.ncr.server.utils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.mes.ncr.server.service.mesenum.DBEnumType;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;

public class Constants {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(Constants.class);
	private static Constants Instance;

	public static Constants getInstance() {
		if (Instance == null)
			Instance = new Constants();
		return Instance;
	}

	private static String StaticPath = null;

	public static synchronized String getStaticPath() {
		if (StaticPath == null) {
			try {
				StaticPath = ResourceUtils.getURL("classpath:static").getPath().replace("%20", " ");
				if (StaticPath != null && StaticPath.length() > 3 && StaticPath.indexOf(":") > 0) {
					if (StaticPath.indexOf("/") == 0)
						StaticPath = StaticPath.substring(1);

					if (!StaticPath.endsWith("/"))
						StaticPath = StaticPath + "/";

				}
			} catch (FileNotFoundException e) {
				return "static/";
			}
		}
		return StaticPath;
	}

	private static String ConfigPath = null;

	public static synchronized String getConfigPath() {
		if (ConfigPath == null) {
			try {
				ConfigPath = ResourceUtils.getURL("classpath:config").getPath().replace("%20", " ");
				if (ConfigPath != null && ConfigPath.length() > 3 && ConfigPath.indexOf(":") > 0) {
					if (ConfigPath.indexOf("/") == 0)
						ConfigPath = ConfigPath.substring(1);
					if (!ConfigPath.endsWith("/"))
						ConfigPath = ConfigPath + "/";

				}
			} catch (FileNotFoundException e) {
				return "config/";
			}
		}
		return ConfigPath;
	}

	public static DBEnumType SQL_TYPE = DBEnumType
			.getEnumType(StringUtils.parseInt(Configuration.readConfigString("mes.server.sql.type", "config/config")));

	public static String CompanyNameTitle = Configuration.readConfigString("company.name.title", "config/config");

	public static String CompanyFaceUrl = Configuration.readConfigString("company.face.url", "config/config");

	public static String MENU_GROUP_ICON = Configuration.readConfigString("menu.group.icon", "config/config");

	public static String MENU_MODULE_ICON = Configuration.readConfigString("menu.module.icon", "config/config");

	public static String AppDomainServer = Configuration.readConfigString("ad.domain.server", "config/config");

	public static String CHECK_AUTHOR_CER = "iPlant";

	public static String iPlant_resEncode_type = "UTF-8";

	public static int iPlant_timeout_seconds = 60;

	public static String IPLANT_RUN_TYPE_CLIENT = "client";
	public static String IPLANT_RUN_TYPE_WEB = "web";
	public static String IPLANT_RUN_TYPE_3TD = "3td";

	public static String UPLOAD_DOWN_FILE_DIR = Configuration.readConfigString("upload.down.file.dir", "config/config");

	public static String UPLOAD_BACK_DOWN_FILE_PATH = Configuration.readConfigString("upload.back.down.file.path",
			"config/config");

	public static String UPLOAD_DOWN_URL = Configuration.readConfigString("upload.down.url", "config/config");

	public static String Client_Upload_Save_Path = Configuration.readConfigString("client.upload.save.path",
			"config/config");
	public static String Client_Upload_Excel_Save_Path = Configuration.readConfigString("client.upload.excel.save.path",
			"config/config");

	// 主页

	public static Map<Integer, Map<Integer, Integer>> Company_Shift_ID_All = new HashMap<Integer, Map<Integer, Integer>>();

	// 邮件

	public static String EMAIL_SENDER = Configuration.readConfigString("mes.mail.sender", "config/config");

	public static String EMAIL_RECEIVER = Configuration.readConfigString("mes.mail.receiver", "config/config");
	public static String EMAIL_SENDER_PASSWORD = Configuration.readConfigString("mes.mail.sender.password",
			"config/config");
	public static String EMAIL_SERVER_HOST = Configuration.readConfigString("mes.mail.host", "config/config");
	public static String EMAIL_SERVER_PORT = Configuration.readConfigString("mes.mail.port", "config/config");

}
