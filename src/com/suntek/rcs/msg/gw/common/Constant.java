package com.suntek.rcs.msg.gw.common;

import java.util.HashMap;
import java.util.Map;

import com.suntek.eap.core.app.AppHandle;

public abstract class Constant {
	public static final String APP_NAME = "rcsmsggw";
	
	public static final String DEFAULT_DS = AppHandle.getHandle(APP_NAME).getDatasourceName();
	
	/**
	 * SP认证信息
	 */
	public static final String SP_USERID = "SP_USERID";
	
	public static final String SP_PASSWORD = "SP_PASSWORD";
	
	public static final String SP_URL = "SP_URL";
	
	public static final String SP_SEND_MESSAGE = "/CASServer/SmsAPI/SendMessage.jsp";
	
	public static final String SP_RECEIVE_MESSAGE = "/CASServer/SmsAPI/ReceiveMessage.jsp";
	
	/**
	 * IM下发短信的地址
	 */
	public static final String IM_URL = "IM_URL";
	
	public static final String IM_REQUEST_SERVICE = "/eRequestService";
	
	/**
	 * 向SP接收短信的时间间隔
	 */
	public static final String REVEIVE_MESSAGE_INTERNEL = "REVEIVE_MESSAGE_INTERNEL";
	/**
	 * HTTP版短信接口错误码表
	 */
	public static final Map<Integer, String> HTTP_MESSAGE_CODE = getHttpMessageCode();
	
	private static Map<Integer, String> getHttpMessageCode() {
		Map<Integer, String> map = new HashMap<Integer,String>();
		map.put(0,"成功");
		map.put(-1,"未知错误");
		map.put(-2,"不支持此功能");
		map.put(-3,"此功能未开通");
		map.put(1,"服务器故障");
		map.put(2,"账号不存在");
		map.put(3,"密码不正确");
		map.put(4,"无可用通道");
		map.put(5,"号码错误");
		map.put(6,"不能多线程访问");
		map.put(7,"账号已冻结");
		map.put(8,"短信内容不能为空");
		map.put(9,"短信内容过长");
		map.put(10,"发现非法字符");
		map.put(11,"账户余额不足");
		map.put(12,"发送时间格式错误");
		map.put(13,"号码过多");
		map.put(14,"扩展参数格式不对");
		map.put(15,"密码过于简单);不可以小于6位并由纯数字组成");
		map.put(16,"连续提交相同短信数据");
		map.put(17,"连续10次单发相同内容短信");
		map.put(18,"发现重复提交号码");
		map.put(19,"必须有指定的签名");
		map.put(20,"必须有签名");
		map.put(21,"内容带签名时不能发长短信");
		
		return map;
	}
}
