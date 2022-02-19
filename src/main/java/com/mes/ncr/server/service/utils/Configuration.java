package com.mes.ncr.server.service.utils;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件读写工具类
 * 
 * @author qfq
 * @date 2012-10-16 15:44:34
 * @version 1.0
 */
public class Configuration {

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	/**
	 * 读取配置文件信息
	 * 
	 * @param name 读取节点名
	 * @param fileName 文件名
	 * @return 读取的节点值
	 */
	public static String readConfigString(String name, String fileName) {
		String result = "";
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			result = rb.getString(name);
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}
	
	/**
	 * 读取配置文件信息
	 * 
	 * @param name 读取节点名
	 * @param fileName 文件名
	 * @return 读取的节点值
	 */
	public static int readConfigInteger(String name, String fileName) {
		int result = 0;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			result = Integer.parseInt(rb.getString(name));
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}
	
	public static long readConfigLong(String name, String fileName){
		long result = 0;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			result = Long.parseLong(rb.getString(name));
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}
	
	public static boolean readConfigBoolean(String name, String fileName){
		boolean result = false;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			String value = rb.getString(name);
			result = "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value);
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}
	
	
	public static double readConfigDouble(String name, String fileName){
		double result = 0;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			result = Double.parseDouble(rb.getString(name));
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}
	
	public static float readConfigFloat(String name, String fileName){
		float result = 0.0000000000f;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(fileName);
			String s = rb.getString(name);
			result = Float.parseFloat(s);
		} catch (Exception e) {
			log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
		}
		return result;
	}

}
