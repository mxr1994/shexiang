package com.tc.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tc.demo.FaceCompareAlarmInfoController;

@Controller
@RequestMapping("/sdk/")
public class SDKStart {
	
	@RequestMapping("start")
	@ResponseBody
	public Object start() {
		FaceCompareAlarmInfoController controller = new FaceCompareAlarmInfoController();
		controller.delFDLib("1");
		controller.CreateFDLib("test2");
		controller.UploadFile(1);
		controller.getFDLib("1");
		controller.UploadSend();
		return "ok";
	}
	
	
	public static void main(String[] args) {
		FaceCompareAlarmInfoController infoController = new FaceCompareAlarmInfoController();
		infoController.init();
	}

}
