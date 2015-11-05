package com.suntek.rcs.msg.gw.util;

/**
 * HttpClient请求后返回的数据模型
 * 
 * @author zcchun
 * @createDate 2014-6-25
 * @version 1.0
 */
public class ResponseModel {
	private boolean success = false;
	private Integer statusCode;
	private String responseBody;

	public ResponseModel() {
		super();
	}

	public ResponseModel(boolean success, Integer statusCode,
			String responseBody) {
		super();
		this.success = success;
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "success:" + success + "; statusCode:" + statusCode
				+ "; responseBodyStr:" + responseBody;
	}
}
