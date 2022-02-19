package com.mes.ncr.server.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 工具类 可置于service中
 * 
 * @author ShrisJava
 *
 */
public class CloneTool {

	public CloneTool() {
		// TODO Auto-generated constructor stub
	}

//	public static <T> T Clone(Object wObject, Class<T> clazz) {
//
//		T wT = null;
//
//		String wJson = JSON.toJSONString(wObject);
//
//		wJson = StringUtils.RepleaceRegx(wJson,
//				"(\\d{4}([-\\/])\\d{1,2}([-\\/])\\d{1,2}([T\\s])\\d{1,2}:\\d{1,2}:\\d{1,2})(\\.\\d{3}\\+\\d{4})?");
//
//		wT = JSON.parseObject(wJson, clazz);
//		return wT;
//	}
//
//	public static <T> List<T> CloneArray(Object wObject, Class<T> clazz) {
//
//		List<T> wTList = new ArrayList<T>();
//
//		String wJson = JSON.toJSONString(wObject);
//
//		Matcher wMatcher = Pattern.compile(
//				"(\\d{4}[-\\/]\\d{1,2}[-\\/]\\d{1,2}[T\\s]\\d{1,2}:\\d{1,2}:\\d{1,2})(\\.\\d{1,3}\\+\\d{1,4})?",
//				Pattern.CASE_INSENSITIVE).matcher(wJson);
//		StringBuffer sb = new StringBuffer();
//		while (wMatcher.find()) {
//			wMatcher.appendReplacement(sb, StringUtils
//					.parseCalendarToString(StringUtils.parseCalendar(wMatcher.group(1)), "yyyy-MM-dd HH:mm:ss"));
//
//		}
//		wMatcher.appendTail(sb);
//
//		wTList = JSON.parseArray(wJson, clazz);
//		return wTList;
//	}

	public static <T> T Clone(Object wObject, Class<T> clazz) {

		T wT = null;

		List<Object> wArray = new ArrayList<Object>();
		wArray.add(wObject);

		List<T> cloneArray = CloneArray(wArray, clazz);
		if (cloneArray != null && cloneArray.size() > 0) {
			wT = cloneArray.get(0);
			return wT;
		}

		String wJson = JSON.toJSONString(wObject, SerializerFeature.DisableCircularReferenceDetect);

		Matcher wMatcher = Pattern.compile(
				"(\\d{4}[-\\/]\\d{1,2}[-\\/]\\d{1,2}([T\\s]\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?)(\\.\\d{1,3}\\+\\d{1,4})?",
				Pattern.CASE_INSENSITIVE).matcher(wJson);

		StringBuffer sb = new StringBuffer();
		while (wMatcher.find()) {
			if (wMatcher.group(0).length() <= 10) {
				wMatcher.appendReplacement(sb, wMatcher.group(0));
			} else {
				wMatcher.appendReplacement(sb, StringUtils
						.parseCalendarToString(StringUtils.parseCalendar(wMatcher.group(0)), "yyyy-MM-dd HH:mm:ss"));
			}
		}
		wMatcher.appendTail(sb);

		wJson = sb.toString();
		wMatcher = Pattern.compile("\\/Date\\((\\d+)(\\+\\d+)?\\)\\/", Pattern.CASE_INSENSITIVE).matcher(wJson);
		sb = new StringBuffer();
		while (wMatcher.find()) {
			wMatcher.appendReplacement(sb, StringUtils.parseCalendarToString(
					StringUtils.parseCalendar(StringUtils.parseLong(wMatcher.group(1))), "yyyy-MM-dd HH:mm:ss"));
		}
		wMatcher.appendTail(sb);

		wT = JSON.parseObject(sb.toString(), clazz);
		return wT;
	}

	public static <T> List<T> CloneArray(Object wObject, Class<T> clazz) {

		List<T> wTList = new ArrayList<T>();
		if (wObject == null)
			wObject = new ArrayList<T>();
		String wJson = "";
		if (StringUtils.isNumeric(wObject.toString())) {
			wJson = "[" + wObject.toString() + "]";
		} else {
			wJson = JSON.toJSONString(wObject, SerializerFeature.DisableCircularReferenceDetect);

		}
		if (wJson.indexOf("[") != 0) {
			wJson = "[" + wJson + "]";
		}

		Matcher wMatcher = Pattern.compile(
				"(\\d{4}[-\\/]\\d{1,2}[-\\/]\\d{1,2}([T\\s]\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?)(\\.\\d{1,3}\\+\\d{1,4})?",
				Pattern.CASE_INSENSITIVE).matcher(wJson);
		StringBuffer sb = new StringBuffer();
		while (wMatcher.find()) {

			if (wMatcher.group(0).length() <= 10) {
				wMatcher.appendReplacement(sb, wMatcher.group(0));
			} else {
				wMatcher.appendReplacement(sb, StringUtils
						.parseCalendarToString(StringUtils.parseCalendar(wMatcher.group(0)), "yyyy-MM-dd HH:mm:ss"));
			}

		}
		wMatcher.appendTail(sb); 
		wJson = sb.toString();
		wMatcher = Pattern.compile("\\/Date\\((\\d+)(\\+\\d+)?\\)\\/", Pattern.CASE_INSENSITIVE).matcher(wJson);

		sb = new StringBuffer();

		while (wMatcher.find()) {

			wMatcher.appendReplacement(sb, StringUtils.parseCalendarToString(
					StringUtils.parseCalendar(StringUtils.parseLong(wMatcher.group(1))), "yyyy-MM-dd HH:mm:ss"));

		}
		wMatcher.appendTail(sb);

		try {
			wTList = JSON.parseArray(sb.toString(), clazz);
		} catch (Exception e) {
			throw e;
		}

		return wTList;
	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> CloneArray(Object wObject) {

		List<Map<String, Object>> wTList = new ArrayList<Map<String, Object>>();

		List<Map> wTListTemp = CloneArray(wObject, Map.class);

		for (Map map : wTListTemp) {
			Map<String, Object> wMap = new HashMap<String, Object>();
			for (Object wKey : map.keySet()) {
				wMap.put(wKey.toString(), map.get(wKey));
			}
			wTList.add(wMap);

		}

		return wTList;
	}


}
