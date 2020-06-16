package com.tc.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.tc.util.HCNetDeviceConUtil;
import com.tc.util.HCNetSDK;
import com.tc.util.HCNetSDK.FMSGCallBack_V31;
import com.tc.util.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import com.tc.util.HCNetSDK.NET_DVR_XML_CONFIG_INPUT;

public class SDKInit {

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	static int m_lSetCardCfgHandle = -1; // 下发卡长连接句柄
	static int m_lSetFaceCfgHandle = -1; // 下发人脸长连接句柄
	static int dwState = -1; // 下发卡数据状态
	static int dwFaceState = -1; // 下发人脸数据状态
	// ip
	String ip;
	int m_FDID = 0;
	public NativeLong m_lUploadHandle = new NativeLong(-1);
	public NativeLong m_UploadStatus = new NativeLong(-1);

	// 用户句柄
	static NativeLong userID;
	int lHandle;
	// 报警监听句柄
	int lListenHandle;
	// 报警回调函数实现
	FMSGCallBackController fMSFCallBack;
	

	public SDKInit() {
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
			// 注册成功，进行布防
			 this.SetupAlarmChan();
			//FaceSpot.set
		}
		NET_DVR_XML_CONFIG_INPUT lpInputParam;
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

		int port = HCNetDeviceConUtil.port;

		NET_DVR_DEVICEINFO_V30 s30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		userID = hCNetSDK.NET_DVR_Login_V30("192.168.0.64", (short) 8000, "admin", "tc_12369", s30);

		// 设置回调
		//hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack, null);

		if (userID.longValue() == -1) {
			System.out.println("注册失败，失败原因为：" + hCNetSDK.NET_DVR_GetLastError());
			return false;
		} else {
			System.out.println("注册成功");
			findFDLib("1");
//			try {
//				Thread.sleep(6000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
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

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	// 查询人脸库
	public void findFDLib(String FDID) {
		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		String str = "GET /ISAPI/Intelligent/FDLib/" + FDID + "\r\n";
		HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
		ptrUrl.write();
		struInput.lpRequestUrl = ptrUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();

		String strInBuffer = new String("<CreateFDLibList><CreateFDLib><id>1</id><name></name><thresholdValue>1</thresholdValue><customInfo /></CreateFDLib></CreateFDLibList>");
		HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(10 * HCNetSDK.BYTE_ARRAY_LEN);
		ptrByte.byValue = strInBuffer.getBytes();
		ptrByte.write();
		struInput.lpInBuffer = ptrByte.getPointer();
		struInput.dwInBufferSize = strInBuffer.length();
		struInput.write();

		HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();

		HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_DATA_LEN);
		struOutput.lpOutBuffer = ptrOutByte.getPointer();
		struOutput.dwOutBufferSize = HCNetSDK.ISAPI_DATA_LEN;

		HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
		struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
		struOutput.write();
		if (hCNetSDK.NET_DVR_STDXMLConfig(userID, struInput, struOutput)) {
			System.out.println("11111111true");
		} else {
			System.out.println("22222222false");
		}
	}

	// 创建人脸库
	public boolean CreateFDLib(String FDLibName) {
		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		String str = "POST /ISAPI/Intelligent/FDLib\r\n";
		HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
		ptrUrl.write();
		struInput.lpRequestUrl = ptrUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();

		String strInBuffer = new String("<CreateFDLibList><CreateFDLib><id>1</id><name>" + FDLibName
				+ "</name><thresholdValue>1</thresholdValue><customInfo /></CreateFDLib></CreateFDLibList>");
		HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(10 * HCNetSDK.BYTE_ARRAY_LEN);
		ptrByte.byValue = strInBuffer.getBytes();
		ptrByte.write();
		struInput.lpInBuffer = ptrByte.getPointer();
		struInput.dwInBufferSize = strInBuffer.length();
		struInput.write();

		HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();

		HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_DATA_LEN);
		struOutput.lpOutBuffer = ptrOutByte.getPointer();
		struOutput.dwOutBufferSize = HCNetSDK.ISAPI_DATA_LEN;

		HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
		struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
		struOutput.write();
		if (hCNetSDK.NET_DVR_STDXMLConfig(userID, struInput, struOutput)) {
			String xmlStr = struOutput.lpOutBuffer.getString(0);
			// dom4j解析xml
			try {
				Document document;
				document = DocumentHelper.parseText(xmlStr);
				Element FDLibInfoList = document.getRootElement();

				// 同时迭代当前节点下面的所有子节点
				Iterator<Element> iterator = FDLibInfoList.elementIterator();
				Element FDLibInfo = iterator.next();
				Iterator<Element> iterator2 = FDLibInfo.elementIterator();
				while (iterator2.hasNext()) {
					Element e = iterator2.next();
					if (e.getName().equals("FDID")) {
						String id = e.getText();
						m_FDID = Integer.parseInt(id);
						System.out.println("m_FDID: " + m_FDID);
					}
					System.out.println(e.getName() + "：" + e.getText());
				}
			} catch (DocumentException e1) {
				e1.printStackTrace();
				return false;
			}
			return true;
			// 获取根节点元素对象
		} else {
			int code = hCNetSDK.NET_DVR_GetLastError();
			JOptionPane.showMessageDialog(null, "创建人脸库失败: " + code);
			return false;
		}
	}
	
	// 删除人脸库
	public void delFDLib (String FDID) {
		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		String str = "DELETE /ISAPI/Intelligent/FDLib/" + FDID + "\r\n";
		HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
		ptrUrl.write();
		struInput.lpRequestUrl = ptrUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();

		String strInBuffer = new String("<CreateFDLibList><CreateFDLib><id>1</id><name></name><thresholdValue>1</thresholdValue><customInfo /></CreateFDLib></CreateFDLibList>");
		HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(10 * HCNetSDK.BYTE_ARRAY_LEN);
		ptrByte.byValue = strInBuffer.getBytes();
		ptrByte.write();
		struInput.lpInBuffer = ptrByte.getPointer();
		struInput.dwInBufferSize = strInBuffer.length();
		struInput.write();

		HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();

		HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_DATA_LEN);
		struOutput.lpOutBuffer = ptrOutByte.getPointer();
		struOutput.dwOutBufferSize = HCNetSDK.ISAPI_DATA_LEN;

		HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
		struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
		struOutput.write();
		if (hCNetSDK.NET_DVR_STDXMLConfig(userID, struInput, struOutput)) {
			System.out.println("11111111true");
		} else {
			System.out.println("22222222false");
		}
	}

	/**
	 * 建立长连接
	 * 
	 * @param index
	 * @return
	 */
	public boolean UploadFile(int index) {
		// 返回true，说明支持人脸
		HCNetSDK.NET_DVR_FACELIB_COND struInput = new HCNetSDK.NET_DVR_FACELIB_COND();
		struInput.dwSize = struInput.size();
		struInput.szFDID = String.valueOf(index).getBytes();
		struInput.byConcurrent = 0;
		struInput.byCover = 1;
		struInput.byCustomFaceLibID = 0;

		struInput.write();
		Pointer lpInput = struInput.getPointer();

		NativeLong ret = hCNetSDK.NET_DVR_UploadFile_V40(userID, HCNetSDK.IMPORT_DATA_TO_FACELIB, lpInput,
				struInput.size(), null, null, 0);
		if (ret.longValue() == -1) {
			int code = hCNetSDK.NET_DVR_GetLastError();
			JOptionPane.showMessageDialog(null, "上传图片文件失败: " + code);
			return false;
		} else {
			m_lUploadHandle = ret;
			return true;
		}
	}

	/**
	 * 上传人脸数据
	 */
	public void UploadSend() {
		FileInputStream picfile = null;
		FileInputStream xmlfile = null;
		int picdataLength = 0;
		int xmldataLength = 0;

		try {
			picfile = new FileInputStream(new File("F:\\HKDate\\facePic.jpg"));
			xmlfile = new FileInputStream(new File("F:\\HKDate\\data.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			picdataLength = picfile.available();
			xmldataLength = xmlfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (picdataLength < 0 || xmldataLength < 0) {
			System.out.println("input file/xml dataSize < 0");
			return;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		HCNetSDK.BYTE_ARRAY ptrxmlByte = new HCNetSDK.BYTE_ARRAY(xmldataLength);

		try {
			picfile.read(ptrpicByte.byValue);
			xmlfile.read(ptrxmlByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ptrpicByte.write();
		ptrxmlByte.write();

		HCNetSDK.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDK.NET_DVR_SEND_PARAM_IN();

		struSendParam.pSendData = ptrpicByte.getPointer();
		struSendParam.dwSendDataLen = picdataLength;
		struSendParam.pSendAppendData = ptrxmlByte.getPointer();
		struSendParam.dwSendAppendDataLen = xmldataLength;
		if (struSendParam.pSendData == null || struSendParam.pSendAppendData == null || struSendParam.dwSendDataLen == 0
				|| struSendParam.dwSendAppendDataLen == 0) {
			System.out.println("input file/xml data err");
			return;
		}

		struSendParam.byPicType = 1;
		struSendParam.dwPicMangeNo = 0;
		struSendParam.write();

		NativeLong iRet = hCNetSDK.NET_DVR_UploadSend(m_lUploadHandle, struSendParam.getPointer(), null);
		System.out.println("iRet=" + iRet);
		if (iRet.longValue() < 0) {
			System.out.println("NET_DVR_UploadSend fail,error=" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("NET_DVR_UploadSend success");
			System.out.println("dwSendDataLen =" + struSendParam.dwSendDataLen);
			System.out.println("dwSendAppendDataLen =" + struSendParam.dwSendAppendDataLen);
		}

		try {
			picfile.close();
			xmlfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public NativeLong getUploadState() {
		IntByReference pInt = new IntByReference(0);
		m_UploadStatus = hCNetSDK.NET_DVR_GetUploadState(m_lUploadHandle, pInt);
		if(m_UploadStatus.longValue() == -1)
		{
			System.out.println("NET_DVR_GetUploadState fail,error=" + hCNetSDK.NET_DVR_GetLastError());
		}
		else if(m_UploadStatus.longValue() == 2)
		{
			System.out.println("is uploading!!!!  progress = " + pInt.getValue());
		}
		else if(m_UploadStatus.longValue()  == 1)
		{
			System.out.println("progress = " + pInt.getValue());
			System.out.println("Uploading Succ!!!!!");
		}
		else
		{
			System.out.println("NET_DVR_GetUploadState fail  m_UploadStatus=" + m_UploadStatus);
			System.out.println("NET_DVR_GetUploadState fail,error=" + hCNetSDK.NET_DVR_GetLastError());
		}
		return m_UploadStatus;
	}
	
	/**
	 * 停止文件上传，释放长连接
	 * @return 0 没有错误 
	 */
	public int UploadClose() {
		if (hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle)) {
			return hCNetSDK.NET_DVR_GetLastError();
		} else {
			return hCNetSDK.NET_DVR_GetLastError();
		}
		
	}
	
	/**
	 * 用户注销
	 * @return 0 没有错误
	 */
	public int Logout () {
		if (hCNetSDK.NET_DVR_Logout(userID)) {
			return hCNetSDK.NET_DVR_GetLastError();
		} else {
			return hCNetSDK.NET_DVR_GetLastError();
		}
	}
	
	/**
	 * 释放 SDK 资源，在程序执行之前调用
	 * @return 0 没有错误;
	 */
	public int Cleanup () {
		if (hCNetSDK.NET_DVR_Cleanup()) {
			return hCNetSDK.NET_DVR_GetLastError();
		} else {
			return hCNetSDK.NET_DVR_GetLastError();
		}
	}
	
}
