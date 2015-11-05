package com.suntek.rcs.msg.gw.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Test;

import com.suntek.rcs.msg.gw.rpc.ReceiveSPMessageService;
import com.suntek.rcs.msg.gw.util.DOM4jXMLHelper;

import junit.framework.TestCase;

/**
 * @desc		: ReceiveMessageServiceTest.java
 * @author 		: zcchun
 * @datetime	: 2014-10-11 上午9:58:00
 * @version 	: 1.0
 */
public class ReceiveMessageServiceTest extends TestCase {
	@Test
	public void testReceiveMessage() {
		new ReceiveSPMessageService().run();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testXml() {
		String xmlStr = "<root return=\"0\" info=\"成功\"><row no=\"1\"><sender>13119557226</sender><target>111</target><uptime>2014-10-11 09:36:10</uptime><msg>回复一次</msg><mt>江山如此多娇，引无数英雄尽脱光~~[D2C]</mt></row></root>";
		Document doc = DOM4jXMLHelper.getDocument(xmlStr);
		Element root = doc.getRootElement();
		Map<String, Object> rootAttrs = new HashMap<String, Object>();
		// 遍历root的属性
		for (Iterator<Attribute> attrs = root.attributeIterator(); attrs.hasNext();) {
			Attribute attr = (Attribute) attrs.next();
			rootAttrs.put(attr.getName(), attr.getText());
		}
		// root return 0 请求 接收短息你成功
		if (rootAttrs.keySet().contains("return")
				&& Integer.valueOf(rootAttrs.get("return").toString()) == 0) {
			for (Iterator<Element> nodes = root.elementIterator(); nodes.hasNext();) {
				Element node = nodes.next();
				if (node.getName().equals("row") && node.elements().size() > 0) {
					Map<String, Object> pushParams = new HashMap<String, Object>(); 
					
					String sender = node.element("sender").getText();
					String msg = node.element("msg").getText();
					String target = node.element("target").getText();
					pushParams.put("Caller", sender);
					pushParams.put("Called", target);			
					pushParams.put("MsgContent", sender + ":" + msg);
					pushParams.put("MsgFormat", 15);
					
					System.out.println("Caller:" + sender + "; Called:" + target + ";MsgContent:" + sender + ":" + msg + ";MsgFormat:" + 15);
				}
			}
		}
	}
	@Test
	public void testPost() {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod("http://183.61.109.140:9801/CASServer/SmsAPI/ReceiveMessage.jsp");
		NameValuePair nuser = new NameValuePair("userid", "92081");
		NameValuePair npwd = new NameValuePair("password", "a123456");
		postMethod.setRequestBody(new NameValuePair[]{nuser, npwd});
		try {
			httpClient.executeMethod(postMethod);
			int statusCode = postMethod.getStatusCode();
			System.out.println(statusCode);
			System.out.println(postMethod.getResponseBodyAsString());
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		
		
		
	}
}
