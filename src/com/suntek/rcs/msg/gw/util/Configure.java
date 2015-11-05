package com.suntek.rcs.msg.gw.util;

import org.apache.log4j.Logger;

import com.suntek.eap.core.app.AppHandle;
import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.common.Constant;

public class Configure {
	private static Logger logger =  CommonLogger.logger;
	 public Configure() {
	  }

	  /**
	   * 根据key值取得配置文件中相应的内容(应用默认配置文件)
	   *
	   * @param key
	   *            String key
	   * @return String value
	   */
	  public static String getProperty(String key) {
	          try {
	                  String property = AppHandle.getHandle(Constant.APP_NAME)
	                                  .getProperty(key);
	                  if (property == null) {
	                          logger.error("没有找到" + key + "对应的值!");
	                          return "";
	                  } else {
	                          return property;
	                  }
	          } catch (Exception e) {
	                  logger.error("没有找到" + key + "对应的值!异常：" + e.getMessage());
	                  return "";
	          }
	  }
	  
	  public static String getProperty(String key, String defaultValue) {
          String v = getProperty(key);
          if("".equals(v)){
        	  return defaultValue;
          }
          return v;
  }
}
