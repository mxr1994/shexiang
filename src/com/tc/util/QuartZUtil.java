package com.tc.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


public class QuartZUtil {
	private static Logger	logger	= Logger.getLogger(QuartZUtil.class);
	private static Scheduler	sched	= null;

	/**
	 * 启动Schedule
	 * 
	 * @throws SchedulerException
	 */
	public void userJob() {
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			logger.info("启动Scheduler");
			sched = sf.getScheduler();
			sched.start();
		}
		catch (SchedulerException e) {
			logger.error(e);
		}

	}

	/**
	 * 任务中添加作业
	 * 
	 * @param jobName
	 *            任务名称，可以自己命名
	 * @param spaceTime
	 *            corn表达式，调用的时间，例如“5 * * ？ * *”
	 * @param clazz
	 *            job实现类
	 */
	public void addJob(String jobName, String spaceTime, Class clazz) {
		String groupName = jobName;
		JobDetail jobs = newJob(clazz).withIdentity(jobName, groupName).build();
		CronTrigger trigger;
		try {
			if (sched.isShutdown()) {
				sched.start();
			}
			trigger = newTrigger().withIdentity(jobName + groupName, groupName).withSchedule(cronSchedule(spaceTime)).build();
			sched.scheduleJob(jobs, trigger);
		}
		catch (Exception e) {
			logger.error(e);
		}

	}

	/**
	 * 在任务中删除改作业
	 * 
	 * @param jobName
	 */
	public void deleteJob(String jobName) {
		String groupName = jobName;
		try {
			if (sched.isShutdown()) {
				sched.start();
			}
			else {
				JobKey key = new JobKey(jobName, groupName);
				sched.deleteJob(key);
			}
		}
		catch (SchedulerException e) {
			logger.error(e);
		}
	}
}
