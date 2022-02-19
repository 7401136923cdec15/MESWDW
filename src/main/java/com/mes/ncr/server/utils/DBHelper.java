package com.mes.ncr.server.utils;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mes.ncr.server.service.mesenum.DBEnumType;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;

public class DBHelper {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(DBHelper.class);

	public static DataSource dataSource = new ComboPooledDataSource("Mysql_dataSource");

	private static DBEnumType DBType = DBEnumType
			.getEnumType(StringUtils.parseInt(Configuration.readConfigString("mes.server.sql.type", "config/config")));

	public static NamedParameterJdbcTemplate getTemplate() {

		NamedParameterJdbcTemplate jdbcTemplate = null;

		switch (DBType) {
		case Default:
			break;
		case Access:
			break;
		case MySQL:
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			break;
		case Oracle:
			// jdbcTemplate = new NamedParameterJdbcTemplate(oracleDataSource);
			break;
		case SQLServer:
			break;
		default:
			break;
		}
		if (jdbcTemplate == null)
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		return jdbcTemplate;
	}

	public static NamedParameterJdbcTemplate getTemplate(DBEnumType wDBEnumType) {
		NamedParameterJdbcTemplate jdbcTemplate = null;

		switch (wDBEnumType) {
		case Default:
			break;
		case Access:
			break;
		case MySQL:
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			break;
		case Oracle:
			// jdbcTemplate = new NamedParameterJdbcTemplate(oracleDataSource);
			break;
		case SQLServer:
			break;
		default:
			break;
		}
		if (jdbcTemplate == null)
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		return jdbcTemplate;
	}

}
