package com.suntek.rcs.msg.gw.common;

import org.apache.log4j.Logger;
import com.suntek.eap.core.log.ILogger;
import com.suntek.eap.core.log.IVersion;

public class CommonLogger extends ILogger {
	public static Logger logger = new CommonLogger().getLogger();

	public CommonLogger() {
	}

	public String getLoggerName() {
		return "rcsmsggw";
	}

	public IVersion getModuleVersion() {
		return new CommonVersion();
	}
}
