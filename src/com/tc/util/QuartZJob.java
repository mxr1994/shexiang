package com.tc.util;


import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tc.demo.FaceCompareAlarmInfoController;




public class QuartZJob implements Job {

	private static Logger logger = Logger.getLogger(QuartZJob.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			logger.info("开始");
			ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "springmvc.xml" });
			FaceCompareAlarmInfoController addFaceServiceImpl =  (FaceCompareAlarmInfoController) context
					.getBean("addFaceServiceImpl");
			addFaceServiceImpl.findFDLib("1");

		} catch (Exception e) {
			logger.error(e);
		}
	}

}
