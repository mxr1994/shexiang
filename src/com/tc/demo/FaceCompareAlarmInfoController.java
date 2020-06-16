package com.tc.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.tc.util.HCNetDeviceConUtil;
import com.tc.util.HCNetSDK;
import com.tc.util.HCNetSDK.FMSGCallBack_V31;
import com.tc.util.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import com.tc.util.ImageUtil;
import com.tc.util.RWProperties;

import sun.misc.BASE64Decoder;
@Service
public class FaceCompareAlarmInfoController{
	Logger log = Logger.getLogger(FaceCompareAlarmInfoController.class); 
	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	static int m_lSetCardCfgHandle = -1; // 下发卡长连接句柄
	static int m_lSetFaceCfgHandle = -1; // 下发人脸长连接句柄
	static int dwState = -1; // 下发卡数据状态
	static int dwFaceState = -1; // 下发人脸数据状态
	static String xmlName = "";
	
	static String filePath = RWProperties.getFilePath();
	static String workStatus = RWProperties.getWorkStatus();
	
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
	static FMSGCallBackController fMSFCallBack = new FMSGCallBackController();
	

//	public FaceCompareAlarmInfoController() {
//		this.init();
//	}

	/**
	 * 初始化设备信息
	 */
	public void init() {
		// fMSFCallBack = null;
		// userID = -1;
		lHandle = -1;
		lListenHandle = -1;
		Boolean login = this.login();
		if (login) {
			// 注册成功，进行布防
			 this.SetupAlarmChan();
			//FaceSpot.set
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
			log.info("初始化失败, 错误代码：" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("接口初始化成功");
			log.info("初始化接口成功");
			// 开启日志
			boolean file = hCNetSDK.NET_DVR_SetLogToFile(3, "D:\\SdkLog\\", true);
			System.out.println("开启日志：" + file);
			hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack, null);
		}

		ip = HCNetDeviceConUtil.ip;

		NET_DVR_DEVICEINFO_V30 s30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		Map<String, String> map = RWProperties.getCameraInfo();
		String port2 = map.get("port");
		Short s = new Short(port2);
		short myPort = s.shortValue();
		
		userID = hCNetSDK.NET_DVR_Login_V30(map.get("ip"), myPort, map.get("username"), map.get("password"), s30);

		// 设置回调

		if (userID.longValue() == -1) {
			System.out.println("注册失败，失败原因为：" + hCNetSDK.NET_DVR_GetLastError());
			log.info("注册失败，失败原因为：" + hCNetSDK.NET_DVR_GetLastError());
			return false;
		} else {
			System.out.println("注册成功");
			log.info("注册成功：" + userID);
			
//			findFDLib("1");
//			delFDLib("1");
//			CreateFDLib("test2");
//			UploadFile(1);
//			getFDLib("1");
//			UploadSend();
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
				log.info("设置回调函数失败：" + hCNetSDK.NET_DVR_GetLastError());
			}
		}

		HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
		m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
		m_strAlarmInfo.byLevel = 1;
		m_strAlarmInfo.byAlarmInfoType = 1;
		m_strAlarmInfo.write();
		// m_strAlarmInfo.byFaceAlarmDetection = 1;

		// NativeLong test = new NativeLong(1);
		// userID
		NativeLong lHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(userID, m_strAlarmInfo);
		if (lHandle.longValue() == -1) {
			System.out.println("布防失败，失败原因：" + hCNetSDK.NET_DVR_GetLastError());
			log.info("布防失败，失败原因：" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("布防成功");
			log.info("布防成功");
			
		}
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
	
	/**
	 * 查询人脸库
	 * @param FDID
	 */
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
			System.out.println("true");
		} else {
			System.out.println("false");
		}
	}

	/**
	 * 创建人脸库
	 * @param FDLibName
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
					}
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
	
	/**
	  * 开启人脸库的比对信息
	 * @param FDID
	 */
	public void getFDLib(String FDID) {
		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		String str = "PUT /ISAPI/Intelligent/channels/" + 1 + "/faceContrast";
		HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
		ptrUrl.write();
		struInput.lpRequestUrl = ptrUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();

		String strInBuffer = new String("<FaceContrastList xmlns=\"http://www.hikvision.com/ver20/XMLSchema\" version=\"2.0\">" + 
				"<FaceContrast>" + 
				"<id>1</id>" + 
				"<enable>true</enable>" + 
				"<AttendanceSaveEnable>false</AttendanceSaveEnable>" + 
				"<faceContrastType>faceContrast</faceContrastType>" + 
				"<contrastFailureAlarmUpload>false</contrastFailureAlarmUpload>" + 
				"<QuickContrast>" + 
				"<enabled>false</enabled>" + 
				"<snapTime>5.000</snapTime>" + 
				"<threshold>70</threshold>" + 
				"<quickConfigMode>custom</quickConfigMode>" + 
				"<Custom>" + 
				"<timeOutMode>infinite</timeOutMode>" + 
				"<duplicateContrastMode>success</duplicateContrastMode>" + 
				"</Custom>" + 
				"</QuickContrast>" + 
				"<alarmStorageEnable>false</alarmStorageEnable>" + 
				"<mixedTargetDetectionWithFaceContrast>false</mixedTargetDetectionWithFaceContrast>" + 
				"</FaceContrast>" + 
				"</FaceContrastList>");
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
			System.out.println("true111");
		} else {
			System.out.println("false2222");
		}
	}
	
	/**
	 * 删除人脸库
	 * @param FDID
	 */
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
			System.out.println("true");
		} else {
			System.out.println("false");
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
			// JOptionPane.showMessageDialog(null, "上传图片文件失败: " + code);
			return false;
		} else {
			m_lUploadHandle = ret;
			return true;
		}
	}
	/**
	 * 将图片转为 base64
	 * @param netImgagePath
	 * @return
	 */
	public static String NetImageToBase64(String netImgagePath) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			// 创建URL
			URL url = new URL(netImgagePath);
			byte[] by = new byte[1024];
			// 创建连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(50000);
			InputStream is = conn.getInputStream();
			// 将内容读取内存中
			int len = -1;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
			// 关闭流
			is.close();
			// conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// BASE64Encoder encoder = new BASE64Encoder();
		String encodeBase64String = Base64.encodeBase64String(data.toByteArray());
		return encodeBase64String;
	}
	
	/**
     * base64字符串转化成图片
     *
     * @param imgData
     *            图片编码
     * @param imgFilePath
     *            存放到本地路径
	 * @return 
     * @return
     * @throws IOException
     */
    @SuppressWarnings("finally")
    public static String GenerateImage(String imgData) throws IOException { // 对字节数组字符串进行Base64解码并生成图片
    	String imgFilePath = null;
    	if (imgData == null) // 图像数据为空
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out = null;
        String reFile = "";
        try {
        	SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddhhmmss");
        	xmlName = sd.format(new Date());
        	imgFilePath = filePath + sd.format(new Date())+".jpg";// 待处理的图片
            out = new FileOutputStream(imgFilePath);
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgData);
            
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
            File file = new File(imgFilePath);
            float f = 0.7f;
            File resize = ImageUtil.resize(file, file, 250, f);
            reFile = resize.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            out.flush();
            out.close();
        }
        return reFile;
    }


	/**
	 * 上传人脸数据
	 */
	public void UploadSend() {
		Map<String, String> map = RWProperties.getURL();
		String imgFilePath = null;
		String resJson = sendPost(map.get("getFaceURL"), "db="+map.get("db"));
		try {
			JSONArray ja = new JSONArray(resJson);
			for ( int i = ja.length() - 1; i > 0; i-- ) {
				//String remark = "http://192.168.0.210:8080/FaceServer";
				JSONObject object = ja.getJSONObject(i);
				String xh = object.getString("xh");
				String dw = object.getString("dw");
				String remark = map.get("projectURL") + object.getString("remark");
				String xm = object.getString("xm");
				String updateDate = object.getString("update_date");
				// 将图片转为 base 64
				String base64 = NetImageToBase64(remark);
				// base64 转为图片
				imgFilePath = GenerateImage(base64);
				// **********************
				Map<String,String> rMap = new HashMap<String,String>();
				rMap.put("name", xm + "-" + xh + "-" + dw);
				rMap.put("bornTime", updateDate);
				// rMap.put("sex", "女");
				// rMap.put("province", "123");
				// rMap.put("city", "福州市");
				String xml = "<FaceAppendData>";
		        for (String item : rMap.keySet())
		        {
		            xml += "<" + item + ">";
		            xml += rMap.get(item);
		            xml += "</" + item + ">";
		        }
		        xml += "</FaceAppendData>";
				// 将 xml 写入文件
				String filePathtoXml = filePath + xmlName + ".xml";
				File myFile = new File(filePathtoXml);
				if (!myFile.exists()) {
					myFile.createNewFile();
				}
				FileWriter resultFile = new FileWriter( filePathtoXml );   
			    PrintWriter myFile1 = new PrintWriter( resultFile );   
			    myFile1.println( xml );   
			    resultFile.close();   
				// **********************
	            // parames.add(new BasicNameValuePair("name", "测试"));
	              
	            // ******************************
				Thread.sleep( 5000 );
				FileInputStream picfile = null;
				FileInputStream xmlfile = null;
				int picdataLength = 0;
				int xmldataLength = 0;
				picfile = new FileInputStream( new File( imgFilePath ) );
				xmlfile = new FileInputStream( new File( filePathtoXml ) );
				picdataLength = picfile.available();
				xmldataLength = xmlfile.available();
				if ( picdataLength < 0 || xmldataLength < 0 ) {
					return;
				}
				HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY( picdataLength );
				HCNetSDK.BYTE_ARRAY ptrxmlByte = new HCNetSDK.BYTE_ARRAY( xmldataLength );
				picfile.read( ptrpicByte.byValue );
				xmlfile.read( ptrxmlByte.byValue );
				ptrpicByte.write();
				ptrxmlByte.write();
				Thread.sleep( 5000 );
				HCNetSDK.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDK.NET_DVR_SEND_PARAM_IN();

				struSendParam.pSendData = ptrpicByte.getPointer();
				struSendParam.dwSendDataLen = picdataLength;
				struSendParam.pSendAppendData = ptrxmlByte.getPointer();
				struSendParam.dwSendAppendDataLen = xmldataLength;
				if ( struSendParam.pSendData == null || struSendParam.pSendAppendData == null || 
						struSendParam.dwSendDataLen == 0 || struSendParam.dwSendAppendDataLen == 0 ) {
					return;
				}

				struSendParam.byPicType = 1;
				struSendParam.dwPicMangeNo = 0;
				struSendParam.write();
				//Thread.sleep(1000);
				NativeLong iRet = hCNetSDK.NET_DVR_UploadSend(m_lUploadHandle, struSendParam.getPointer(), null);
				while (true) {
					NativeLong uploadState = getUploadState();
					if (uploadState.toString().equals("1")) {
						System.out.println("上传成功");
						// NET_DVR_GetUploadResult(m_lUploadHandle, HCNetSDK.IMPORT_DATA_TO_FACELIB, 12);
						break;
					} else if (uploadState.toString().equals("2")) {
						System.out.println("正在上传");
					} else if (uploadState.toString().equals("29")) {
						System.out.println("图片未识别到目标");
						break;
					} else {
						System.out.println("其他错误：" + uploadState);
						hCNetSDK.NET_DVR_UploadClose(uploadState);
						UploadFile(m_FDID);
						break;
					}
				}
				//Thread.sleep(1000);
				HCNetSDK.NET_DVR_UPLOAD_FILE_RET  struPicRet = new HCNetSDK.NET_DVR_UPLOAD_FILE_RET();
				Pointer lpPic= struPicRet.getPointer();
	            struPicRet.write();
				boolean bRet = hCNetSDK.NET_DVR_GetUploadResult(m_lUploadHandle, lpPic, struPicRet.size());
				if (bRet) {
					System.out.println("继续下一次上传");
				}
				if (iRet.longValue() < 0) {
					System.out.println("NET_DVR_UploadSend fail,error=" + hCNetSDK.NET_DVR_GetLastError());
				} else {
					System.out.println("NET_DVR_UploadSend success");
					System.out.println("dwSendDataLen =" + struSendParam.dwSendDataLen);
					System.out.println("dwSendAppendDataLen =" + struSendParam.dwSendAppendDataLen);
				}
				//Thread.sleep(5000);
				try {
					picfile.close();
					xmlfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (JSONException e3) {
			e3.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
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
	
	class ThreadImp extends Thread{
		public void run() {
			while (true) {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
