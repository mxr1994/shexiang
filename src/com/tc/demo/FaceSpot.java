package com.tc.demo;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.tc.util.HCNetDeviceConUtil;
import com.tc.util.HCNetSDK;
import com.tc.util.HCNetSDK.FMSGCallBack_V31;
import com.tc.util.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import com.tc.util.HCNetSDK.NET_DVR_XML_CONFIG_INPUT;

public class FaceSpot {

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	// NET_DVR_DEVICEINFO_V40 deviceInfo;
	static int m_lSetCardCfgHandle = -1; // 下发卡长连接句柄
	static int m_lSetFaceCfgHandle = -1; // 下发人脸长连接句柄
	static int dwState = -1; // 下发卡数据状态
	static int dwFaceState = -1; // 下发人脸数据状态
	// ip
	String ip;
	int m_FDID = 0;
	public NativeLong m_lUploadHandle = new NativeLong(-1);
	
	// 用户句柄
	static NativeLong userID;
	int lHandle;
	// 报警监听句柄
	int lListenHandle;
	// 报警回调函数实现
	FMSGCallBackController fMSFCallBack;

	public FaceSpot() {
		this.init();
	}

	/**
	 * 初始化设备信息
	 */
	public void init() {
		fMSFCallBack = null;
		// userID = -1;
		lHandle = -1;
		lListenHandle = -1;
		Boolean login = this.login();
		if (login) {
			this.SetupAlarmChan();
		}
	}

	/**
	 * 用户注册
	 */
	public Boolean login() {
		// 初始化
		boolean initSuc = hCNetSDK.NET_DVR_Init();
		if (!initSuc) {
			System.out.println("初始化失败, 错误代码：" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("接口初始化成功");
		}

		ip = HCNetDeviceConUtil.ip;

		NET_DVR_DEVICEINFO_V30 s30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		userID = hCNetSDK.NET_DVR_Login_V30("192.168.0.64", (short) 8000, "admin", "tc_12369", s30);

		// 设置回调
		hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack, null);

		if (userID.longValue() == -1) {
			System.out.println("注册失败，失败原因为：" + hCNetSDK.NET_DVR_GetLastError());
			return false;
		} else {
			System.out.println("注册成功");
			
			return true;
		}
	}

	/**
	 * 报警布防
	 */
	public void SetupAlarmChan() {
		if (fMSFCallBack == null) {
			fMSFCallBack = new FMSGCallBackController();
			FMSGCallBack_V31 fMessageCallBack = new FMSGCallBackController();
			Pointer pUser = null;
			if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMessageCallBack, pUser)) {
				System.out.println("设置回调函数失败：" + hCNetSDK.NET_DVR_GetLastError());
			}
		}

		// 获取设备能力集
		// hCNetSDK.NET_DVR_GetDeviceAbility(userID, dwAbilityType, pInBuf, dwInLength,
		// pOutBuf, dwOutLength)
		// hCNetSDK.NET_DVR_GetDeviceConfig(userID, dwCommand, dwCount, lpInBuffer,
		// dwInBufferSize, lpStatusList, lpOutBuffer, dwOutBufferSize)
		HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
		m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
		m_strAlarmInfo.byLevel = 1;
		m_strAlarmInfo.byAlarmInfoType = 1;
		m_strAlarmInfo.write();
		// m_strAlarmInfo.byFaceAlarmDetection = 1;
		NativeLong lHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(userID, m_strAlarmInfo);
		if (lHandle.longValue() == -1) {
			System.out.println("布防失败，失败原因：" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("布防成功");
		}
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
