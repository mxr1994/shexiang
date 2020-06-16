package com.tc.controller.admin;

import javax.servlet.http.HttpServlet;

import com.tc.demo.FaceCompareAlarmInfoController;

public class SdkServlet extends HttpServlet{
	
public SdkServlet(){
		
    	Thread t = new Thread(new Runnable() {
    		@Override
			public void run() {
				FaceCompareAlarmInfoController infoController = new FaceCompareAlarmInfoController();
				infoController.init();
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
    	t.start();
	}

}
