package com.tc.time;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tc.demo.FaceCompareAlarmInfoController;

@Component
@EnableScheduling//可以在启动类上注解也可以在当前文件
public class TaskCool {
    /**
     * 第一个定时器测试方法
     */
	@Scheduled(cron = "0 0 22 ? * SUN")
    public void testJob(){
		FaceCompareAlarmInfoController controller = new FaceCompareAlarmInfoController();
		controller.delFDLib("1");
		controller.CreateFDLib("test2");
		controller.UploadFile(1);
		controller.getFDLib("1");
		controller.UploadSend();
    }
}
