package com.mes.ncr.server.service.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具类 可置于service中
 * 
 * @author ShrisJava
 *
 */
public class StringUtils {
	private static Logger logger = LoggerFactory.getLogger(StringUtils.class);

	private static Map<String, MessageDigest> digests = new ConcurrentHashMap<String, MessageDigest>();

	public static Integer parseInt(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else {
				try {
					return Integer.valueOf(value.toString());
				} catch (Exception e) {
					return 0;
				}
			}
		}
		return 0;
	}

	public static Short parseShort(Object value) {
		if (value != null) {
			if (value instanceof Short) {
				return (Short) value;
			} else {
				try {
					return Short.valueOf(value.toString());
				} catch (Exception e) {
					return 0;
				}
			}
		}
		return 0;
	}

	public static Byte parseByte(Object value) {
		if (value != null) {
			if (value instanceof Byte) {
				return (Byte) value;
			} else {
				try {
					return Byte.valueOf(value.toString());
				} catch (Exception e) {
					return 0;
				}
			}
		}
		return 0;
	}

	public static Float parseFloat(Object value) {
		if (value != null) {
			if (value instanceof Float) {
				return (Float) value;
			} else if (value instanceof Integer) {
				return ((Integer) value).floatValue();
			} else if (value instanceof Short) {
				return ((Short) value).floatValue();
			} else if (value instanceof Byte) {
				return ((Byte) value).floatValue();
			} else if (value instanceof Double) {
				return ((Double) value).floatValue();
			} else {
				try {
					return Float.valueOf(value.toString());
				} catch (Exception e) {
					return 0f;
				}
			}
		}
		return 0f;
	}

	public static Long parseLong(Object value) {
		if (value != null) {
			if (value instanceof Long) {
				return (Long) value;
			}
			if (value instanceof Integer) {
				return ((Integer) value).longValue();
			} else {
				try {
					return Long.valueOf(value.toString());
				} catch (Exception ex) {
					return 0L;
				}
			}
		}
		return 0L;
	}

	public static Double parseDouble(Object value) {
		if (value != null) {
			if (value instanceof Double) {
				return (Double) value;
			}
			if (value instanceof BigDecimal) {
				return Double.valueOf(value.toString());
			}
			if (value instanceof Integer) {
				return ((Integer) value).doubleValue();
			} else if (value instanceof Float) {
				return ((Float) value).doubleValue();
			} else if (value instanceof String) {
				try {
					return Double.valueOf((String) value);
				} catch (Exception ex) {
					return (double) 0;
				}
			}
		}
		return (double) 0;
	}

	public static String parseString(Object value) {
		if (value != null) {

			if (Calendar.class.isInstance(value)) {
				return parseCalendarToString((Calendar) value, "yyyy-MM-dd HH:mm:ss");
			} else if (Date.class.isInstance(value)) {
				return parseDateToString((Date) value, "yyyy-MM-dd HH:mm:ss");
			} else if (Float.class.isInstance(value)) {
				return value.toString();
			} else if (Double.class.isInstance(value)) {
				return value.toString();
			}
			return String.valueOf(value);
		}
		return "";
	}

	public static <T> List<T> parseList(Object value, Class<T> calzz) {
		if (value != null) {
			try {
				value = CloneTool.CloneArray(value, calzz);
			} catch (Exception ex) {
				return null;
			}
		}
		return null;
	}

	@SafeVarargs
	public static <T> List<T> parseListArgs(T... args) {
		List<T> wResult = new ArrayList<T>();
		if (args != null) {
			for (T ts : args) {
				wResult.add(ts);
			}
		}
		return wResult;
	}

	public static <T> List<T> parseList(T[] value) {
		List<T> wResult = new ArrayList<T>();
		if (value != null) {
			try {
				for (T t : value) {
					if (t == null)
						continue;

					if ((t instanceof String) && isEmpty(t.toString())) {
						continue;
					}
					wResult.add(t);
				}
			} catch (Exception ex) {
				return wResult;
			}

		}
		return wResult;
	}

	public static int[] parseIntArray(List<String> value) {

		int[] wResult = new int[0];
		if (value != null) {
			wResult = new int[value.size()];
			try {
				for (int i = 0; i < value.size(); i++) {
					wResult[i] = Integer.parseInt(value.get(i));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static int[] parseIntArray(String[] value) {

		int[] wResult = new int[0];
		if (value != null) {
			wResult = new int[value.length];
			try {
				for (int i = 0; i < value.length; i++) {
					wResult[i] = Integer.parseInt(value[i]);
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Integer> parseIntList(List<String> value) {

		List<Integer> wResult = new ArrayList<Integer>();
		if (value != null) {

			try {
				for (int i = 0; i < value.size(); i++) {
					wResult.add(Integer.parseInt(value.get(i)));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Integer> parseIntList(String[] value) {

		List<Integer> wResult = new ArrayList<Integer>();
		if (value != null) {

			try {
				for (int i = 0; i < value.length; i++) {
					wResult.add(Integer.parseInt(value[i]));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Integer> parseIntList(String wString, String wDelim) {

		return parseIntList(splitList(wString, wDelim));
	}

	public static Long[] parseLongArray(List<String> value) {

		Long[] wResult = new Long[0];
		if (value != null) {
			wResult = new Long[value.size()];
			try {
				for (int i = 0; i < value.size(); i++) {
					wResult[i] = Long.parseLong(value.get(i));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static Long[] parseLongArray(String[] value) {

		Long[] wResult = new Long[0];
		if (value != null) {
			wResult = new Long[value.length];
			try {
				for (int i = 0; i < value.length; i++) {
					wResult[i] = Long.parseLong(value[i]);
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Long> parseLongList(List<String> value) {

		List<Long> wResult = new ArrayList<Long>();
		if (value != null) {

			try {
				for (int i = 0; i < value.size(); i++) {
					wResult.add(Long.parseLong(value.get(i)));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Long> parseLongList(String[] value) {

		List<Long> wResult = new ArrayList<Long>();
		if (value != null) {

			try {
				for (int i = 0; i < value.length; i++) {
					wResult.add(Long.parseLong(value[i]));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static List<Double> parseDoubleList(String[] value) {

		List<Double> wResult = new ArrayList<Double>();
		if (value != null) {

			try {
				for (int i = 0; i < value.length; i++) {
					wResult.add(Double.parseDouble(value[i]));
				}
			} catch (Exception ex) {
				return wResult;
			}
		}
		return wResult;
	}

	public static Boolean parseBoolean(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return ((Integer) value).intValue() == 1;
			} else if (value instanceof String) {
				return "1".equals(value) || "true".equals(value);
			} else if (value instanceof Boolean) {
				return (Boolean) value;
			}
		}
		return false;
	}

	public static String hash(String data) {
		return hash(data, "MD5");
	}

	public static String hash(String data, String algorithm) {
		try {
			return hash(data.getBytes("utf-8"), algorithm);
		} catch (UnsupportedEncodingException e) {

		}
		return data;
	}

	public static String hash(byte[] bytes, String algorithm) {
		synchronized (algorithm.intern()) {
			MessageDigest digest = digests.get(algorithm);
			if (digest == null) {
				try {
					digest = MessageDigest.getInstance(algorithm);
					digests.put(algorithm, digest);
				} catch (Exception nsae) {
					return null;
				}
			}
			// Now, compute hash.
			digest.update(bytes);
			return encodeHex(digest.digest());
		}
	}

	public static String encodeHex(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	/**
	 * 计算字符串字节长度
	 * 
	 * @param str
	 * @return
	 * @author Femi
	 */
	public static int getStringByteLength(String str) {
		char[] t = str.toCharArray();
		int count = 0;
		for (char c : t) {
			if ((c >= 0x4e00) && (c <= 0x9fbb)) {
				count = count + 2;
			} else {
				count++;
			}
		}
		return count;
	}

	public static String parseInputStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		return sb.toString();

	}

	public static Date parseDate(String value, String format) {

		try {
			format = format.replaceAll("T", "'T'").replaceAll("''T''", "'T'");
			format = format.replaceAll("Z", "'Z'").replaceAll("''Z''", "'Z'");
			SimpleDateFormat DateFormat = new SimpleDateFormat(format);
			if (format.contains("T")) {
				DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			}
			return DateFormat.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return null;
	}

	public static Calendar FormatCalendar(Calendar value, String format) {
		Calendar wResult = Calendar.getInstance();
		wResult.set(2000, 0, 1);
		try {
			SimpleDateFormat DateFormat = new SimpleDateFormat(format);

			wResult.setTime(DateFormat.parse(DateFormat.format(value.getTime())));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return wResult;
	}

	public static Calendar parseCalendar(Object value, String format) {
		Calendar wCalendar = Calendar.getInstance();
		try {
			wCalendar.set(1970, 1, 1);
			if (value instanceof Calendar) {
				wCalendar = (Calendar) value;
			} else if (value instanceof Date) {
				wCalendar.setTime((Date) value);
			} else if (value instanceof Long) {
				wCalendar.setTimeInMillis((long) value);
			} else if (value instanceof Integer) {
				wCalendar.add(Calendar.MILLISECOND, (int) value);
			} else if (value instanceof String) {

				format = format.replaceAll("T", "'T'").replaceAll("''T''", "'T'");
				format = format.replaceAll("Z", "'Z'").replaceAll("''Z''", "'Z'");
				SimpleDateFormat DateFormat = new SimpleDateFormat(format);
				if (format.contains("'T'")) {
					DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				}
				wCalendar.setTime(DateFormat.parse((String) value));
			} else {
				format = format.replaceAll("T", "'T'").replaceAll("''T''", "'T'");
				format = format.replaceAll("Z", "'Z'").replaceAll("''Z''", "'Z'");
				SimpleDateFormat DateFormat = new SimpleDateFormat(format);
				if (format.contains("'T'")) {
					DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				}
				wCalendar.setTime(DateFormat.parse(value.toString()));
			}
		} catch (ParseException e) {
			System.out.println("format:" + format + "  value:" + value.toString());
			logger.error(e.getMessage());
		}
		return wCalendar;
	}

	public static String DencodeURIComponent(String wValue, String wKey) {
		if (wValue == null || wValue.trim().isEmpty() || wKey == null || wKey.trim().isEmpty())
			return "";

		if (!wValue.startsWith("?"))
			wValue = "?" + wValue;

		int wIndex = wValue.indexOf("?" + wKey + "=");
		if (wIndex < 0)
			wIndex = wValue.indexOf("&" + wKey + "=");

		if (wIndex < 0)
			return "";

		String wResut = wValue.substring(wIndex + 1);
		wIndex = wResut.indexOf('&');
		wResut = wResut.substring(wKey.length() + 1, wIndex);

		return wResut;
	}

	public static Calendar parseCalendar(Object value) {

		Calendar wResult = Calendar.getInstance();
		wResult.set(2000, 0, 1);
		try {

			if (value == null)
				return wResult;
			if ((value instanceof String) && value.toString().length() >= 4) {
				value = value.toString().trim();
				Matcher wMatcher = Pattern
						.compile("\\d{4}([\\-\\/])\\d{1,2}([\\-\\/])\\d{1,2}([T\\s])\\d{1,2}:\\d{1,2}:\\d{1,2}",
								Pattern.CASE_INSENSITIVE)
						.matcher(value.toString());

				if (wMatcher.find()) {
					wResult = parseCalendar(value, StringUtils.Format("yyyy{0}MM{1}dd{2}HH:mm:ss", wMatcher.group(1),
							wMatcher.group(2), wMatcher.group(3)));
					return wResult;
				}
				wMatcher = Pattern
						.compile("\\d{4}([\\-\\/\\\\])\\d{1,2}([\\-\\/\\\\])\\d{1,2}([T\\s])\\d{1,2}:\\d{1,2}",
								Pattern.CASE_INSENSITIVE)
						.matcher(value.toString());

				if (wMatcher.find()) {
					wResult = parseCalendar(value, StringUtils.Format("yyyy{0}MM{1}dd{2}HH:mm", wMatcher.group(1),
							wMatcher.group(2), wMatcher.group(3)));
					return wResult;
				}

				wMatcher = Pattern.compile("\\d{4}([\\-\\/])\\d{1,2}([\\-\\/])\\d{1,2}", Pattern.CASE_INSENSITIVE)
						.matcher(value.toString());

				if (wMatcher.find()) {
					wResult = parseCalendar(value,
							StringUtils.Format("yyyy{0}MM{1}dd", wMatcher.group(1), wMatcher.group(2)));
					return wResult;
				}

				wMatcher = Pattern.compile("\\d{1,2}:\\d{1,2}:\\d{1,2}", Pattern.CASE_INSENSITIVE)
						.matcher(value.toString());

				if (wMatcher.find()) {
					wResult = parseCalendar(value, StringUtils.Format("yyyy{0}MM{1}dd{2}HH:mm", wMatcher.group(1),
							wMatcher.group(2), wMatcher.group(3)));
					return wResult;
				}

			} else {
				wResult = parseCalendar(value, "yyyy-MM-dd HH:mm:ss");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return wResult;
	}

	public static String RepleaceRegx(String value, String wPat) {

		Matcher wMatcher = Pattern.compile(wPat, Pattern.CASE_INSENSITIVE).matcher(value);
		StringBuffer sb = new StringBuffer();
		while (wMatcher.find()) {
			wMatcher.appendReplacement(sb, StringUtils
					.parseCalendarToString(StringUtils.parseCalendar(wMatcher.group(1)), "yyyy-MM-dd HH:mm:ss"));

		}
		wMatcher.appendTail(sb);
		value = sb.toString();

		return value;
	}

	public static String parseDateToString(Date value, String format) {
		SimpleDateFormat DateFormat = new SimpleDateFormat(format);
		return DateFormat.format(value);
	}

	public static String parseCalendarToString(Calendar value, String format) {
		SimpleDateFormat DateFormat = new SimpleDateFormat(format);
		return DateFormat.format(value.getTime());
	}

	public static <T> String Join(String delimiter, Collection<T> wValues) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(wValues);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(delimiter);
		for (T cs : wValues) {
			joiner.add(cs.toString());
		}
		return joiner.toString();

	}

	public static <T> String Join(String delimiter, T[] wValues) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(wValues);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(delimiter);
		for (T cs : wValues) {
			joiner.add(cs.toString());
		}
		return joiner.toString();

	}

	public static int CompareDate(Calendar c1, Calendar c2) {
		String fmt = "yyyyMMdd";
		Calendar d1 = parseCalendar(parseCalendarToString(c1, fmt));
		Calendar d2 = parseCalendar(parseCalendarToString(c2, fmt));
		return d1.compareTo(d2);
	}

	public static int CompareCalendar(Calendar c1, Calendar c2, String fmt) {
		Calendar d1 = parseCalendar(parseCalendarToString(c1, fmt));
		Calendar d2 = parseCalendar(parseCalendarToString(c2, fmt));
		return d1.compareTo(d2);
	}

	public static boolean isNumeric(String wValue) {
		boolean wResult = false;
		try {
			if (isEmpty(wValue))
				return wResult;

			Double.parseDouble(wValue);
			wResult = true;
		} catch (Exception e) {
			wResult = false;
		}
		return wResult;
	}

	@SuppressWarnings("unchecked")
	public static <T> T CombineData(T wTask, Object wData) {

		try {
			if (wTask == null || wData == null)
				return wTask;
			Map<String, Object> wDataMap = CloneTool.Clone(wData, Map.class);

			Field[] fields = wTask.getClass().getFields();
			for (String wPropName : wDataMap.keySet()) {
				for (Field wField : fields) {
					if (!wField.getName().equals(wPropName))
						continue;

					wField.set(wTask, CloneTool.Clone(wDataMap.get(wPropName), wField.getType()));
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wTask;
	}

	public static int CompareTime(Calendar c1, Calendar c2) {
		String fmt = "HH:mm:ss";
		Calendar d1 = parseCalendar(parseCalendarToString(c1, fmt));
		Calendar d2 = parseCalendar(parseCalendarToString(c2, fmt));
		return d1.compareTo(d2);
	}

	public static String[] split(String wString, String wDelim) {
		return splitList(wString, wDelim).toArray(new String[0]);
	}

	public static List<String> splitList(String wString, String wDelim) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (wString == null || wString.length() <= 0)
				return wResult;

			if (wDelim != null && wDelim.length() > 1) {
				wDelim = wDelim.replaceAll("\\\\", "\\\\\\\\");
				wDelim = wDelim.replaceAll("\\+", "\\\\+");
				wDelim = wDelim.replaceAll("\\|", "\\\\|");
				wDelim = wDelim.replaceAll("\\*", "\\\\*");
				wDelim = wDelim.replaceAll("\\^", "\\\\^");
				wDelim = wDelim.replaceAll("\\$", "\\\\$");
				wDelim = wDelim.replaceAll("\\/", "\\\\/");
				wDelim = wDelim.replaceAll("\\[", "\\\\[");
				wDelim = wDelim.replaceAll("\\]", "\\\\]");
				wDelim = wDelim.replaceAll("\\(", "\\\\(");
				wDelim = wDelim.replaceAll("\\)", "\\\\)");
				wDelim = wDelim.replaceAll("\\-", "\\\\-");
				wDelim = wDelim.replaceAll("\\.", "\\\\.");

				wResult = StringUtils.parseList(wString.split(wDelim));
				return wResult;
			}
			StringTokenizer wStringTokenizer = new StringTokenizer(wString, wDelim);

			while (wStringTokenizer.hasMoreElements()) {
				wResult.add(wStringTokenizer.nextToken());
			}
		} catch (Exception e) {
			wResult.clear();
			wResult.add(wString);
			logger.error(e.toString());
		}
		return wResult;
	}

	public static String Format(String pattern, Object... arguments) {
		String wResult = pattern;
		try {

			if (arguments == null || arguments.length < 1)
				return wResult;

			int wLength = arguments.length;
			for (int i = 0; i < wLength; i++) {
				arguments[i] = String.valueOf(arguments[i]);
			}

			wResult = MessageFormat.format(pattern, arguments);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 将简单类型已格式化形式排列成字符串
	 * 
	 * @param wMap
	 * @return
	 */
	public static String MapToString(Map<Integer, Integer> wMap) {
		List<String> wResult = new ArrayList<String>();

		for (Integer wT1 : wMap.keySet()) {
			if (wT1 == null)
				continue;
			if (wMap.get(wT1) == null)
				continue;

			wResult.add(StringUtils.Format("{0}+|:|+{1}", wT1, wMap.get(wT1)));
		}
		return StringUtils.Join("+|;|+", wResult);
	}

	/**
	 * 将简单类型已格式化形式排列成字符串
	 * 
	 * @param wMap
	 * @return
	 */
	public static <T1, T2> Map<T1, T2> StringToMap(String wMapString, Class<T1> wT1, Class<T2> wT2) {
		Map<T1, T2> wResult = new HashMap<T1, T2>();

		if (wMapString == null || wT1 == null || wT2 == null)
			return wResult;
		String[] wItemArray = null;
		for (String wItem : split(wMapString, "+|;|+")) {
			wItemArray = split(wItem, "+|:|+");
			if (wItemArray == null || wItemArray.length != 2)
				continue;

			wResult.put(ParseSimple(wItemArray[0], wT1), ParseSimple(wItemArray[1], wT2));
		}
		return wResult;
	}

	/**
	 * 将某个值转为简单类型 仅支持： Integer Long Double Float Short String Byte Boolean
	 * 
	 * @param wValue
	 * @param wClazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T ParseSimple(Object wValue, Class<T> wClazz) {

		try {
			if (wValue == null || wClazz == null)
				return null;

			switch (wClazz.getSimpleName()) {
			case "Integer":
				return (T) parseInt(wValue);
			case "Long":
				return (T) parseLong(wValue);
			case "Double":
				return (T) parseDouble(wValue);
			case "Float":
				return (T) parseFloat(wValue);
			case "Short":
				return (T) parseShort(wValue);
			case "String":
				return (T) parseString(wValue);
			case "Byte":
				return (T) parseByte(wValue);
			case "Boolean":
				return (T) parseBoolean(wValue);
			default:
				return wClazz.newInstance();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
