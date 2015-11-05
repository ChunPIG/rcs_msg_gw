package com.suntek.rcs.msg.gw.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.common.Constant;
import com.suntek.rcs.msg.gw.util.Configure;
import com.suntek.rcs.msg.gw.web.service.MessageSendService;

/**
 * 短信发送服务类
 * 
 * @desc : SendMessageServlet.java
 * @author : zcchun
 * @datetime : 2014-10-10 下午2:56:16
 * @version : 1.0
 */
public class RepeatSendMessageServlet extends HttpServlet {

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

		request.setCharacterEncoding("utf-8");

		try {
			String destnumbers = request.getParameter("destnumbers");
			String msg = request.getParameter("msg");
			int numberPerSecond = Integer.valueOf(request.getParameter("numberPerSecond"));
			
			java.util.List<String> numbers = new ArrayList<String>();
			
			if (StringUtils.isNotEmpty(destnumbers)) {
				if (destnumbers.contains("|")) {
					String[] arr = destnumbers.split("|");
					numbers = Arrays.asList(arr);
				} else if (destnumbers.contains(",")) {
					String[] arr = destnumbers.split("|");
					numbers = Arrays.asList(arr);
				} else {
					numbers.add(destnumbers);
				}
			} else {
				logger.error("");
				return;
			}
			
			int sendTotal = 0;
			
			for (String number : numbers) {
				Map<String, Object> params = new HashMap<String, Object>(); 
				
				params.put("destnumbers", number);			
				params.put("msg", System.currentTimeMillis() + ":" + msg);
				params.put("MsgFormat", 15);
				params.put("sendtime", "");
				
				String url = Configure.getProperty(Constant.SP_URL) + Constant.SP_SEND_MESSAGE;
				messageSendService.sendMessage(url, params);
				sendTotal ++;
				if (sendTotal / numberPerSecond == 0)
					Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			logger.error("Request content format is not correct" + e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
}
