package com.suntek.rcs.msg.gw.web.service;

import java.util.Map;

import jm.tools.minijson.JSONObject;

import org.apache.log4j.Logger;

import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.common.Constant;
import com.suntek.rcs.msg.gw.util.Configure;
import com.suntek.rcs.msg.gw.util.DOM4jXMLHelper;
import com.suntek.rcs.msg.gw.util.HttpClientHelper;
import com.suntek.rcs.msg.gw.util.ResponseModel;

/**
 * 接收SP短信处理类
 * @desc		: MessageHandler.java
 * @author 		: zcchun
 * @datetime	: 2014-10-10 下午3:39:38
 * @version 	: 1.0
 */
public class MessageSendService {
	private static final Logger logger = CommonLogger.logger;
	
	/**
	 * 根据定时向SP获取的短信，然后推送到IM
	 * @param responseModel
	 */
	public void pushMessageToIM(Map<String, Object> params) {
		
		if (params == null || params.isEmpty()) {
			return;
		}
		
		// http://IP:Port/eRequestService
		String pushIMUrl = Configure.getProperty(Constant.IM_URL) + Constant.IM_REQUEST_SERVICE;		
		String xmlData = DOM4jXMLHelper.convertMapToXml(params, 0);
		try {			
			ResponseModel model = HttpClientHelper.getInstance().post(pushIMUrl, xmlData);

			if (model.isSuccess()) {
				logger.debug("[SERV]MessageSendService.pushMessageToIM()往IM推送短信成功：" + model.getResponseBody() + "; url:" + pushIMUrl);
				logger.info("[SERV]MessageSendService.pushMessageToIM()往IM推送短信成功：" + model.getResponseBody() + "; url:" + pushIMUrl);	
			} else {
				logger.error("[SERV]MessageSendService.pushMessageToIM()往IM推送时发送错误：" + model.getResponseBody() + "; url:" + pushIMUrl);
				logger.info("[SERV]MessageSendService.pushMessageToIM()往IM推送时发送错误：" + model.getResponseBody() + "; url:" + pushIMUrl);
			}
		} catch (Exception e) {
			logger.error("[SERV]ERROR|MessageSendService.pushMessageToIM()向IM推送短信时发生错误：" + e.getMessage(), e);
		}
	}
	
	/**
	 * 根据IM发送的消息，转发给SP
	 */
	public void sendMessageToSP(Map<String, Object> params) {
		String spUser = Configure.getProperty(Constant.SP_USERID);
		String spPwd = Configure.getProperty(Constant.SP_PASSWORD);
		
		if (!params.keySet().contains("userid")) {
			params.put("userid", spUser);
		}
		if (!params.keySet().contains("password")) {
			params.put("password", spPwd);
		}
		// http://183.61.109.140:9801/CASServer/SmsAPI/ReceiveMessage.jsp
		String sendSPUrl = Configure.getProperty(Constant.SP_URL) + Constant.SP_SEND_MESSAGE;
		
		sendMessage(sendSPUrl, params);		
	}
	
	/**
	 * 发出HTTP请求
	 * @param url
	 * @param params
	 */
	public void sendMessage(String url, Map<String, Object> params) {
		try {
			logger.info("[SERV]请求URL[" + url + "]; params[" + JSONObject.fromMap(params).toString() + "]");
			ResponseModel resp = HttpClientHelper.getInstance().post(url, params);
			
			if (resp.isSuccess()) {
				logger.debug("[SERV]MessageSendService.sendMessage()发送短信成功：" + resp.getResponseBody() + "; url:" + url);
				logger.info("[SERV]MessageSendService.sendMessage()发送短信成功：" + resp.getResponseBody() + "; url:" + url);	
			} else {
				logger.error("[SERV]MessageSendService.sendMessage()发送短信时发送错误：" + resp.getResponseBody() + "; url:" + url);			
			}
		} catch (Exception e) {
			logger.error("[SERV]ERROR|MessageSendService.sendMessage()发生错误：" + e.getMessage(), e);
		}
	}
}
