package com.mes.ncr.server.service.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mes.ncr.server.service.po.APIResult; 

@Component
public class RemoteInvokeUtils {
	private static Logger logger = LoggerFactory.getLogger(RemoteInvokeUtils.class);

	@Autowired
	RestTemplate restTemplate;

	public RemoteInvokeUtils() {

	}

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.restTemplate = this.restTemplate;
	}

	private static RemoteInvokeUtils Instance = null;

	public static RemoteInvokeUtils getInstance() {
		if (Instance == null)
			Instance = new RemoteInvokeUtils();

		return Instance;
	}

	public String HttpInvokeString(String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod) {
		String wResult = "";
		try {
			// 拼接url
			wResult = HttpInvoke(wUrl, wParms, wHttpMethod, String.class);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	private String HandleUrl(String wServerUrl, String wUrl) {
		if (wServerUrl == null || wServerUrl.length() < 1) {
			return wUrl;
		}
		if (wUrl == null || wUrl.length() < 1)
			return "";

		if (wServerUrl.endsWith("/")) {
			if (wUrl.startsWith("/")) {
				return StringUtils.Format("{0}{1}", wServerUrl, wUrl.substring(1));
			} else {
				return StringUtils.Format("{0}{1}", wServerUrl, wUrl);
			}
		} else {
			if (wUrl.startsWith("/")) {

				return StringUtils.Format("{0}{1}", wServerUrl, wUrl);
			} else {
				return StringUtils.Format("{0}/{1}", wServerUrl, wUrl);
			}
		}

	}

	public String HttpInvokeString(String wServerUrl, String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(wServerUrl))
				wServerUrl = "";
			// 拼接url
			wResult = HttpInvoke(HandleUrl(wServerUrl, wUrl), wParms, wHttpMethod, String.class);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public <T> T HttpInvoke(String wServerUrl, String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod,
			Class<T> clazz) {
		T wResult = null;
		try {
			if (StringUtils.isEmpty(wServerUrl))
				wServerUrl = "";
			wResult = clazz.newInstance();
			wResult = HttpInvoke(HandleUrl(wServerUrl, wUrl), wParms, wHttpMethod, clazz);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public <T> T HttpInvoke(String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod, Class<T> clazz) {
		T wResult = null;
		try {
			wResult = clazz.newInstance();
			if (wParms == null)
				wParms = new HashMap<String, Object>();
			// 拼接url

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONStringWithDateFormat(wParms,
	          "yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect), headers);
			ResponseEntity<T> wResponseEntity = new ResponseEntity<T>(HttpStatus.OK);
			switch (wHttpMethod) {
			case DELETE:
				break;
			case HEAD:
				break;
			case OPTIONS:
				break;
			case PATCH:
				break;
			case PUT:
				break;
			case TRACE:
				break;
			case GET:

				String wParmsString = "";
				for (String wKey : wParms.keySet()) {
					wParmsString += StringUtils.Format("{0}={1}&", wKey, wParms.get(wKey));
				}

				if (wUrl.indexOf('?') > 5) {
					wUrl = StringUtils.Format("{0}{1}{2}", wUrl.substring(0, wUrl.indexOf('?') + 1), wParmsString,
							wUrl.substring(wUrl.indexOf('?') + 1));
				} else {
					wUrl = StringUtils.Format("{0}?{1}aipd={2}", wUrl, wParmsString, Math.random());
				}

				wResponseEntity = restTemplate.getForEntity(wUrl, clazz);

				break;
			case POST:
				wResponseEntity = restTemplate.exchange(wUrl, wHttpMethod, entity, clazz);
				break;
			default:
				break;
			}

			wResult = wResponseEntity.getBody();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public APIResult HttpInvokeAPI(String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod) {
		APIResult wResult = new APIResult();
		try {
			wResult = HttpInvoke(wUrl, wParms, wHttpMethod, APIResult.class);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 
	 * @param wServerUrl   http://IP:端口/
	 * @param wProjectName 项目名称
	 * @param wUrl
	 * @param wParms
	 * @param wHttpMethod
	 * @return
	 */
	public APIResult HttpInvokeAPI(String wServerUrl, String wProjectName, String wUrl, Map<String, Object> wParms,
			HttpMethod wHttpMethod) {

		if (StringUtils.isEmpty(wProjectName))
			wProjectName = "";
		if (wUrl != null && wUrl.startsWith("/"))
			wUrl = wUrl.substring(1);
		return HttpInvokeAPI(wServerUrl, StringUtils.Format("{0}/{1}", wProjectName, wUrl), wParms, wHttpMethod);
	}

	public APIResult HttpInvokeAPI(String wServerUrl, String wUrl, Map<String, Object> wParms, HttpMethod wHttpMethod) {
		APIResult wResult = new APIResult();
		try {
			if (StringUtils.isEmpty(wServerUrl))
				wServerUrl = "";
			wResult = HttpInvoke(HandleUrl(wServerUrl, wUrl), wParms, wHttpMethod, APIResult.class);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 发送 post请求
	 * 
	 * @param httpUrl 地址
	 */
	/*
	 * public String sendHttpPost(String httpUrl) { HttpPost httpPost = new
	 * HttpPost(httpUrl);// 创建httpPost return sendHttpPost(httpPost); }
	 * 
	 *//**
		 * 发送 post请求
		 * 
		 * @param httpUrl 地址
		 * @param params  参数(格式:key1=value1&key2=value2)
		 */
	/*
	 * public String sendHttpPost(String httpUrl, String params) { HttpPost httpPost
	 * = new HttpPost(httpUrl); try { StringEntity stringEntity = new
	 * StringEntity(params, "UTF-9=8");
	 * stringEntity.setContentType("application/x-www-form-urlencoded");
	 * httpPost.setEntity(stringEntity); } catch (Exception e) {
	 * e.printStackTrace(); } return sendHttpPost(httpPost); }
	 * 
	 *//**
		 * 发送 post请求
		 * 
		 * @param httpUrl 地址
		 * @param maps    参数
		 */
	/*
	 * public String sendHttpPost(String httpUrl, Map<String, String> maps) {
	 * HttpPost httpPost = new HttpPost(httpUrl); // 创建参数队列 List<NameValuePair>
	 * nameValuePairs = new ArrayList<NameValuePair>(); for (String key :
	 * maps.keySet()) { nameValuePairs.add(new BasicNameValuePair(key,
	 * maps.get(key))); } try { httpPost.setEntity(new
	 * UrlEncodedFormEntity(nameValuePairs, "UTF-8")); } catch (Exception e) {
	 * e.printStackTrace(); } return sendHttpPost(httpPost); }
	 * 
	 *//**
		 * 发送 post请求（带文件）
		 * 
		 * @param httpUrl   地址
		 * @param maps      参数
		 * @param fileLists 附件
		 */
	/*
	 * public String sendHttpPost(String httpUrl, Map<String, String> maps,
	 * List<File> fileLists) { HttpPost httpPost = new HttpPost(httpUrl);
	 * MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create(); for
	 * (String key : maps.keySet()) { meBuilder.addPart(key, new
	 * StringBody(maps.get(key), ContentType.TEXT_PLAIN)); } for (File file :
	 * fileLists) { FileBody fileBody = new FileBody(file);
	 * meBuilder.addPart("files", fileBody); } HttpEntity reqEntity =
	 * meBuilder.build(); httpPost.setEntity(reqEntity); return
	 * sendHttpPost(httpPost); }
	 * 
	 *//**
		 * 发送Post请求
		 * 
		 * @param httpPost
		 * @return
		 */
	/*
	 * private String sendHttpPost(HttpPost httpPost) { CloseableHttpClient
	 * httpClient = null; CloseableHttpResponse response = null; HttpEntity entity =
	 * null; String responseContent = null; try { // 创建默认的httpClient实例 httpClient =
	 * HttpClients.createDefault(); httpPost.setConfig(requestConfig); // 执行请求
	 * response = httpClient.execute(httpPost); entity = response.getEntity();
	 * responseContent = EntityUtils.toString(entity, "UTF-8"); } catch (Exception
	 * e) { e.printStackTrace(); } finally { try { if (response != null) {
	 * response.close(); } if (httpClient != null) { httpClient.close(); } } catch
	 * (Exception e2) { e2.printStackTrace(); } } return responseContent; }
	 * 
	 *//**
		 * 发送 get请求
		 * 
		 * @param httpUrl
		 */
	/*
	 * public String sendHttpGet(String httpUrl) { HttpGet httpGet = new
	 * HttpGet(httpUrl); return sendHttpGet(httpGet); }
	 * 
	 *//**
		 * 发送 get请求Https
		 * 
		 * @param httpUrl
		 */
	/*
	 * public String sendHttpsGet(String httpUrl) { HttpGet httpGet = new
	 * HttpGet(httpUrl); return sendHttpGet(httpGet); }
	 * 
	 *//**
		 * 发送Get请求
		 * 
		 * @param httpPost
		 * @return
		 */
	/*
	 * public String sendHttpGet(HttpGet httpGet) { CloseableHttpClient httpClient =
	 * null; CloseableHttpResponse response = null; HttpEntity entity = null; String
	 * responseContent = null; try { // 创建默认的httpClient实例 httpClient =
	 * HttpClients.createDefault(); httpGet.setConfig(requestConfig); // 执行请求
	 * response = httpClient.execute(httpGet); entity = response.getEntity();
	 * responseContent = EntityUtils.toString(entity, "UTF-8"); } catch (Exception
	 * e) { e.printStackTrace(); } finally { try { if (response != null) {
	 * response.close(); } if (httpClient != null) { httpClient.close(); } } catch
	 * (Exception e2) { e2.printStackTrace(); } } return responseContent; }
	 * 
	 *//**
		 * 发送Get请求Https
		 * 
		 * @param httpPost
		 * @return
		 *//*
			 * private String sendHttpsGet(HttpGet httpGet) { CloseableHttpClient httpClient
			 * = null; CloseableHttpResponse response = null; HttpEntity entity = null;
			 * String responseContent = null; try { // 创建默认的httpClient实例 PublicSuffixMatcher
			 * publicSuffixMatcher = PublicSuffixMatcherLoader .load(new
			 * URL(httpGet.getURI().toString())); DefaultHostnameVerifier hostnameVerifier =
			 * new DefaultHostnameVerifier(publicSuffixMatcher); httpClient =
			 * HttpClients.custom().setSSLHostnameVerifier(hostnameVerifier).build();
			 * httpGet.setConfig(requestConfig); // 执行请求 response =
			 * httpClient.execute(httpGet); entity = response.getEntity(); responseContent =
			 * EntityUtils.toString(entity, "UTF-8"); } catch (Exception e) {
			 * e.printStackTrace(); } finally { try { if (response != null) {
			 * response.close(); } if (httpClient != null) { httpClient.close(); } } catch
			 * (Exception e2) { e2.printStackTrace(); } } return responseContent; }
			 */

}
