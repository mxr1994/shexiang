package com.tc.util;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;


@Service
public class QuartzInit implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = Logger.getLogger(QuartzInit.class);

	private QuartZUtil quartZUtil = null;

	// spring所有bean加载成功后 执行该方法
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		logger.info("准备就绪");
		quartZUtil = new QuartZUtil();
		quartZUtil.userJob();
		quartZUtil.addJob("brake", "0 15 10 * * ?", QuartZJob.class);
	}

}
