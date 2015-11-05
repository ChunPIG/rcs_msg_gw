package com.suntek.rcs.msg.gw.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.suntek.rcs.msg.gw.common.CommonLogger;
import com.suntek.rcs.msg.gw.common.Constant;
import com.suntek.rcs.msg.gw.rpc.ReceiveSPMessageService;
import com.suntek.rcs.msg.gw.util.Configure;

/**
 * 应用初始化监听器
 * 
 * @desc : InitailizeContextListener.java
 * @author : zcchun
 * @datetime : 2014-10-10 09:47:47
 * @version : 1.0
 */
public class InitailizeContextListener implements ServletContextListener {
	private static final Logger logger = CommonLogger.logger;

	/**
	 * 缓存定时器
	 */
	private List<Timer> timers = new ArrayList<Timer>();

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// 停止定时器
		if (timers != null && timers.size() > 0) {
			/*Iterator<Timer> iterator = timers.iterator();
			while (iterator.hasNext()) {
				Timer timer = iterator.next();
				timer.cancel();
				iterator.remove();
				System.out.println("timers size -" + timers.size());
			}	*/		
			
			for (Iterator<Timer> ite = this.timers.iterator(); ite.hasNext();) {
				Timer timer = ite.next();
				timer.cancel();
				ite.remove();
				System.out.println("timers size -" + timers.size());
			}
		}		
		logger.info("[InitialContextListener]rcsmsggw was destroyed...");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		long start = System.currentTimeMillis();
		int period = 10 * 1000;
		String internel = Configure.getProperty(Constant.REVEIVE_MESSAGE_INTERNEL);
		if (StringUtils.isNotBlank(internel) && Integer.valueOf(internel) > 0) {
			period = Integer.valueOf(internel);
		}

		// 启动定时器
		try {
			//
			Timer messageTimer = new Timer();
			timers.add(messageTimer);
			messageTimer.scheduleAtFixedRate(new ReceiveSPMessageService(), 5 * 1000, period);
		} catch (Exception e) {
			logger.error("InitailizeContextListener Starting Error:" + e.getMessage(), e);
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("[InitialContextListener]rcsmsggw was started... cost time: " + (end - start)
				+ " ms");
	}

}
