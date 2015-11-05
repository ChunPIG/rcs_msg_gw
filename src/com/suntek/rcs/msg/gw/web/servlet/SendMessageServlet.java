package com.suntek.rcs.msg.gw.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.util.DOM4jXMLHelper;
import com.suntek.rcs.msg.gw.web.service.MessageSendService;

/**
 * 短信发送服务类
 * 
 * @desc : SendMessageServlet.java
 * @author : zcchun
 * @datetime : 2014-10-10 下午2:56:16
 * @version : 1.0
 */
public class SendMessageServlet extends HttpServlet {

	private static final long serialVersionUID = -6938530646631863073L;

	public static Logger logger = new CommonLogger().getLogger();

	private MessageSendService messageSendService = new MessageSendService();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String requestBody = this.getRequestBody(request);
		logger.debug("SendMessageServlet.doPost()1======rcsmsggw接收到的requestBody -> " + requestBody);
		logger.info("SendMessageServlet.doPost()1======rcsmsggw接收到的requestBody -> " + requestBody);
		if (requestBody == null || StringUtils.isBlank(requestBody)) {
			CommonLogger.logger.error("Request content is empty");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		try {
			Document doc = DOM4jXMLHelper.getDocument(requestBody);
			Element root = doc.getRootElement();
			if (root.getName().equals("eRequestService") && root.elements().size() > 0) {
				Map<String, Object> params = new HashMap<String, Object>(); 
				
				String caller = root.element("Caller").getText();
				String called = root.element("Called").getText();
				String msgContent = root.element("MsgContent").getText();
				
				if (caller.length() > 11 && caller.contains("+86")) {
					caller = caller.substring(3);
				}
				
				if (called.length() > 11 && called.contains("+86")) {
					called = called.substring(3);
				}
				
				params.put("destnumbers", called);			
				params.put("msg",caller + ":" + msgContent);
				params.put("MsgFormat", 15);
				params.put("sendtime", "");
				
				messageSendService.sendMessageToSP(params);
			}
			
		} catch (Exception e) {
			logger.error("Request content format is not correct" + e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
	/**
	 * 获取请求数据
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String getRequestBody(HttpServletRequest request) throws IOException {
		StringBuilder body = new StringBuilder(100);
		String data = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(),
				"utf-8"));
		while ((data = reader.readLine()) != null) {
			body.append(data);
		}
		if (reader != null) {
			reader.close();
		}
		return body.toString();
	}
}
