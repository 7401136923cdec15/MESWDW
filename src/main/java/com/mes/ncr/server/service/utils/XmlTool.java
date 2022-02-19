package com.mes.ncr.server.service.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML工具类 可置于service中
 * 
 * @author ShrisJava
 *
 */
public class XmlTool {

	private static Logger logger = LoggerFactory.getLogger(XmlTool.class);

	private XmlTool() {
	}

	private static <T> void serializeSingleObject(OutputStream os, T obj) // 序列化单个java对象
	{
		// XMLEncoder xe = new XMLEncoder(os);
		XMLEncoder xe = new XMLEncoder(os, "UTF-8", true, 0); // 仅用于Java SE 7
		xe.writeObject(obj); // 序列化成XML字符串
		xe.flush();
		xe.close();
	}

	private static <T> T deserializeSingleObject(InputStream is) // 反序列化单个Java对象
	{
		XMLDecoder xd = new XMLDecoder(is);
		@SuppressWarnings("unchecked")
		T obj = (T) xd.readObject(); // 从XML序列中解码为Java对象
		xd.close();
		return obj;
	}

	public static synchronized <T> void SaveXml(String wFileFullName, T wT) {

		File xmlFile = new File(wFileFullName);

		if (!xmlFile.getParentFile().exists()) { // 判断父目录路径是否存在，即test.txt前的I:\a\b\
			try {
				xmlFile.getParentFile().mkdirs(); // 不存在则创建父目录
				xmlFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.toString());

			}
		}
		if (!xmlFile.exists()) {
			try {
				xmlFile.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				logger.error(e.toString());
			}
		}
		try {
			FileOutputStream ofs = new FileOutputStream(xmlFile); // 创建文件输出流对象

			serializeSingleObject(ofs, wT);
			ofs.flush();
			ofs.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	}

	public static synchronized <T> T ReadXml (String wFileFullName) {
		T wT = null;
		try {

			File xmlFile = new File(wFileFullName);

			if (!xmlFile.exists()) {
				return wT;
			}
			FileInputStream ifs = null;
			try {
				ifs = new FileInputStream(xmlFile); // 创建文件输出流对象

				wT = deserializeSingleObject(ifs);
				ifs.close();

			} catch (Exception e) {
				try {
					ifs.close();
				} catch (Exception e2) {

				}
				e.printStackTrace();
				logger.error(e.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}

		return wT;
	}

}
