package com.mes.ncr.server.service.po;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.mes.ncr.server.service.utils.StringUtils;

public class BPMResource<T> {

	private static final Logger logger = LoggerFactory.getLogger(BPMResource.class);

	private String FilePath = "";

	private List<T> Value = new ArrayList<T>();

	public BPMResource() {
		super();
	}

	public BPMResource(String wPath, Class<T> clazz) {
		super();
		if (StringUtils.isEmpty(wPath))
			return;
		FilePath = wPath;
		try {
			File wFile = new File(FilePath);
			if (wFile.exists()) {
				String wJson = FileUtils.readFileToString(wFile, "UTF-8");
				Value = JSON.parseArray(wJson, clazz);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public synchronized int size() {
		if (Value == null)
			return 0;
		return Value.size();
	}

	public synchronized void add(T wValue) {
		if (wValue == null)
			return;
		Value.add(wValue);
	}

	public synchronized void addAll(Collection<T> wValue) {
		if (wValue == null)
			return;
		Value.addAll(wValue);
	}

	public synchronized T get() {
		T wResult = null;

		if (Value.size() <= 0)
			return wResult;
		wResult = Value.get(0);
		Value.remove(0);
		return wResult;
	}

	public synchronized List<T> getAll() {
		List<T> wResult = new ArrayList<T>(Value);
		Value.clear();
		return wResult;
	}

	/*
	 * 获取后不删除
	 *
	 */
	public synchronized List<T> getNotAll() {
		List<T> wResult = new ArrayList<T>(Value);
		return wResult;
	}

	public synchronized void Save() {
		try {
			if (StringUtils.isEmpty(FilePath))
				return;
			File wFile = new File(FilePath);
			if (!wFile.exists())
				wFile.createNewFile();
			FileUtils.write(wFile, JSON.toJSONString(Value), "UTF-8");
			Value.clear();
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public synchronized void Save(String wPath) {
		try {
			if (StringUtils.isEmpty(wPath))
				return;
			File wFile = new File(wPath);
			if (!wFile.exists())
				wFile.createNewFile();
			FileUtils.write(wFile, JSON.toJSONString(Value), "UTF-8");
			Value.clear();
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
