package com.suntek.rcs.msg.gw.rpc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.common.Constant;
import com.suntek.rcs.msg.gw.util.Configure;
import com.suntek.rcs.msg.gw.util.DOM4jXMLHelper;
import com.suntek.rcs.msg.gw.util.HttpClientHelper;
import com.suntek.rcs.msg.gw.util.ResponseModel;
import com.suntek.rcs.msg.gw.web.service.MessageSendService;

/**
 * 定时向SP接口获取短信内容并IM推送服务类
 * @desc : ReceiveSPMessageService.java
 * @author : zcchun
 * @datetime : 2014-10-10 10:22:30
 * @version : 1.0
 */
public class ReceiveSPMessageService extends TimerTask {
	protected final static Logger logger = CommonLogger.logger;
	
	MessageSendService messageSendService = new MessageSendService();

	@Override
	public void run() {
		receiveMessage();
	}

	@SuppressWarnings("rawtypes")
	private void receiveMessage() {		
		String spUser = Configure.getProperty(Constant.SP_USERID); 
		String spPwd = Configure.getProperty(Constant.SP_PASSWORD);
		//http://183.61.109.140:9801/CASServer/SmsAPI/ReceiveMessage.jsp
		String receUrl = Configure.getProperty(Constant.SP_URL) + Constant.SP_RECEIVE_MESSAGE;
		 

		/*String spUser = "92081";
		String spPwd = "a123456";
		String receUrl = "http://183.61.109.140:9801/CASServer/SmsAPI/ReceiveMessage.jsp";*/

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", spUser);
		params.put("password", spPwd);

		ResponseModel resModel = HttpClientHelper.getInstance().post(receUrl, params);
		if (resModel.isSuccess()) {
			String respBody = resModel.getResponseBody();
			logger.debug("[SERV]ReceiveSPMessageService.receiveMessage() Response Body:" + respBody);
			logger.info("[SERV]ReceiveSPMessageService.receiveMessage() Response Body:" + respBody);
			/*if (respBody.contains("sender")) {
				System.out.println("[SERV]ReceiveSPMessageService.receiveMessage() Response Body" + respBody);
			}*/
			Document doc = DOM4jXMLHelper.getDocument(respBody);
			Element root = doc.getRootElement();
			Map<String, Object> rootAttrs = new HashMap<String, Object>();
			// 遍历root的属性
			for (Iterator attrs = root.attributeIterator(); attrs.hasNext();) {
				Attribute attr = (Attribute) attrs.next();
				rootAttrs.put(attr.getName(), attr.getText());
			}
			// root return 0 请求 接收短息你成功
			if (rootAttrs.keySet().contains("return")
					&& Integer.valueOf(rootAttrs.get("return").toString()) == 0) {
				for (Iterator nodes = root.elementIterator(); nodes.hasNext();) {
					Element node = (Element) nodes.next();
					if (node.getName().equals("row") && node.elements().size() > 0) {
						Map<String, Object> pushParams = new HashMap<String, Object>(); 
						
						String sender = node.element("sender").getText();
						String[] msgBody = node.element("msg").getText().split(":");
						String called = "";
						String msgContent = "";
						if (msgBody != null && msgBody.length > 1) {
							called = msgBody[0];
							msgContent = msgBody[1];
						}
						
						pushParams.put("Caller", sender);
						pushParams.put("Called", called);			
						pushParams.put("MsgContent",msgContent);
						pushParams.put("MsgFormat", 15);
						
						messageSendService.pushMessageToIM(pushParams);
					} else {
						logger.debug("[SERV]ReceiveSPMessageService.receiveMessage()向SP接口获取接收的短信条数为0，不需要向IM发送推送消息，接口返回信息："
								+ rootAttrs.get("info").toString() + ";请求地址：" + receUrl);
						logger.info("[SERV]ReceiveSPMessageService.receiveMessage()向SP接口获取接收的短信条数为0，不需要向IM发送推送消息，接口返回信息："
								+ rootAttrs.get("info").toString() + ";请求地址：" + receUrl);
					}
				}
			} else { // root return != 0 表示 有错误,需要记录错误的信息
				logger.error("[SERV]ReceiveSPMessageService.receiveMessage()无法向SP接口获取接收的短信，接口返回信息："
						+ rootAttrs.get("info").toString() + ";请求地址：" + receUrl);
			}
		} else {
			logger.error("[SERV]ReceiveSPMessageService.receiveMessage()向SP接口请求获取短信内容时发生错误！URL:"
					+ receUrl);
		}
	}

}
