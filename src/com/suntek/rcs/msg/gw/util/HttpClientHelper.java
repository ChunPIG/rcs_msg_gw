package com.suntek.rcs.msg.gw.util;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * @desc : HttpClientHelper.java
 * @author : zcchun
 * @datetime : 2014-8-4 上午9:15:46
 * @version : 1.0
 */
public class HttpClientHelper {
	private static Logger log = Logger.getLogger(HttpClientHelper.class);

	private static LinkedList<HttpClient> HttpClient_Cache = new LinkedList<HttpClient>();

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private HttpClientHelper() {
	}

	private static final String charset = "UTF-8";
	private static HttpClientHelper httpClientHelper = new HttpClientHelper();

	public static HttpClientHelper getInstance() {
		return httpClientHelper;
	}

	@SuppressWarnings("rawtypes")
	public ResponseModel post(String postUrl, Map params) {
		PostMethod postMethod = newPostMethod(postUrl, params);
		return request(postMethod);
	}

	public ResponseModel post(String postUrl, String xmlData) {
		PostMethod postMethod = newPostMethod(postUrl, xmlData);
		return request(postMethod);
	}

	@SuppressWarnings("rawtypes")
	private PostMethod newPostMethod(String postUrl, Map params) {
		PostMethod postMethod = new PostMethod(postUrl);
		postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		postMethod.addParameter("Content-Type", "application/x-www/form-urlencode; charset="
				+ charset);
		addParamsToPostMethod(params, postMethod);
		return postMethod;
	}

	private PostMethod newPostMethod(String postUrl, String xmlData) {
		PostMethod postMethod = new PostMethod(postUrl);

		try {
			RequestEntity requestEntity = new StringRequestEntity(xmlData, "application/xml",
					charset);
			postMethod.setRequestEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			log.error("[UTIL]HttpClientHelper.newPostMethod[XML] 出现错误：" + e.getMessage(), e);
			e.printStackTrace();
		}

		return postMethod;
	}

	@SuppressWarnings("rawtypes")
	private void addParamsToPostMethod(Map params, PostMethod postMethod) {
		// Groovy代码
		/*
		 * params?.each { String key, value -> if (value == null) return
		 * 
		 * if (value instanceof String) { postMethod.addParameter(key, value) }
		 * else if (value instanceof Date) { postMethod.addParameter(key,
		 * value?.format('yyyy-MM-dd HH:mm:ss')) } else {
		 * postMethod.addParameter(key, value.toString()) } }
		 */
		for (Iterator it = params.keySet().iterator(); it.hasNext();) {
			String key = it.next().toString();
			Object value = params.get(key);

			if (null == value)
				return;

			if (value instanceof String) {
				postMethod.addParameter(key, value.toString());
			} else if (value instanceof Date) {
				postMethod.addParameter(key, dateFormat.format(value));
			} else {
				postMethod.addParameter(key, value.toString());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public ResponseModel get(String url, Map params) {
		GetMethod getMethod = newGetMethod(url, params);
		return request(getMethod);
	}

	@SuppressWarnings("rawtypes")
	private GetMethod newGetMethod(String url, Map params) {
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		addParamsToGetMethod(params, getMethod);
		return getMethod;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void addParamsToGetMethod(Map params, GetMethod getMethod) {
		List nameValuePairs = new ArrayList();

		// Groovy代码
		/*
		 * params?.each { key, value -> if (value == null) return
		 * 
		 * if (value instanceof String) { nameValuePairs << new
		 * NameValuePair(key, value) } else if (value instanceof Date) {
		 * nameValuePairs << new NameValuePair(key, value?.format('yyyy-MM-dd
		 * HH:mm:ss')) } else { nameValuePairs << new NameValuePair(key,
		 * value?.toString()) } }
		 */

		for (Iterator it = params.keySet().iterator(); it.hasNext();) {
			String key = it.next().toString();
			Object value = params.get(key);
			if (null == key)
				return;

			if (value instanceof String) {
				nameValuePairs.add(new NameValuePair(key, value.toString()));
			} else if (value instanceof Date) {
				nameValuePairs.add(new NameValuePair(key, ((Date) value).toLocaleString()));
			} else {
				nameValuePairs.add(new NameValuePair(key, value.toString()));
			}
		}

		if (nameValuePairs.size() > 0) {
			NameValuePair[] nameValuePairsArray = new NameValuePair[nameValuePairs.size()];
			for (int i = 0; i < nameValuePairs.size(); i++) {
				nameValuePairsArray[i] = (NameValuePair) nameValuePairs.get(i);
			}
			getMethod.setQueryString(nameValuePairsArray);
		}
	}

	private ResponseModel request(HttpMethod httpMethod) {
		ResponseModel responseModel = new ResponseModel();
		HttpClient httpClient = this.getActiveHttpClient();

		long startTime = System.currentTimeMillis();
		try {
			int statusCode = httpClient.executeMethod(httpMethod);
			System.out.println("success to execute httpMethod[" + httpMethod.getName()
					+ "],request url[" + httpMethod.getURI().toString() + "],response statusCode["
					+ statusCode + "]");
			log.debug("success to execute httpMethod[" + httpMethod.getName() + "],request url["
					+ httpMethod.getURI().toString() + "],response statusCode[" + statusCode + "]");
			responseModel.setStatusCode(statusCode);

			if (statusCode == HttpStatus.SC_OK) {
				responseModel.setSuccess(true);
			}
			log.debug("[UTIL]HttpClientHelper.request() response body:" + httpMethod.getResponseBodyAsString());
			/*if (StringUtils.isNotBlank(httpMethod.getResponseBodyAsString()) && httpMethod.getResponseBodyAsString().contains("sender")) {
				System.out.println("[UTIL]HttpClientHelper.request() response body:" + httpMethod.getResponseBodyAsString());
			}*/
//			responseModel.setResponseBody(IOUtils.toString(httpMethod.getResponseBodyAsStream()));
			responseModel.setResponseBody(httpMethod.getResponseBodyAsString());
			long endTime = System.currentTimeMillis();

			log.info("execute http request cost time:" + (endTime - startTime) + " ms");
		} catch (Exception e) {
			try {
				log.error("fail to execute httpMethod[" + httpMethod.getName() + "], url["
						+ httpMethod.getURI().toString() + "]", e);
			} catch (URIException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			httpMethod.releaseConnection();
			this.releaseHttpClient(httpClient);
			if (log.isDebugEnabled()) {
				log.debug("执行http请求耗时：" + (System.currentTimeMillis() - startTime) + " 毫秒");
			}
		}
		return responseModel;
	}

	private HttpClient getActiveHttpClient() {
		HttpClient httpClient = null;
		try {
			httpClient = HttpClient_Cache.removeFirst();

		} catch (Throwable e) {

			httpClient = this.newHttpClient();
			if (log.isDebugEnabled()) {
				log.debug("新建一个HTTPCLIENT实例," + httpClient.hashCode());
			}
		}
		return httpClient;
	}

	private void releaseHttpClient(HttpClient httpClientInstance) {
		if (httpClientInstance != null) {
			HttpClient_Cache.addLast(httpClientInstance);
			if (log.isDebugEnabled()) {
				log.debug("释放一个HTTPCLIENT实例, " + httpClientInstance.hashCode());
			}
		}
	}

	private HttpClient newHttpClient() {
		HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		httpClient.getParams().setContentCharset(charset);

		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager()
				.getParams();
		// 设置连接超时时间(单位毫秒)
		managerParams.setConnectionTimeout(10000);
		// 设置读数据超时时间(单位毫秒)
		managerParams.setSoTimeout(10000);

		return httpClient;
	}
}