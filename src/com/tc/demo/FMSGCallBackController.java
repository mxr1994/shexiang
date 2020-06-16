package com.tc.demo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sun.jna.Pointer;
import com.tc.util.HCNetSDK;
import com.tc.util.HCNetSDK.NET_DVR_ALARMER;
import com.tc.util.HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM;
import com.tc.util.ImageUtil;
import com.tc.util.PostSend;
import com.tc.util.RWProperties;

public class FMSGCallBackController implements HCNetSDK.FMSGCallBack_V31 {

	Logger log = Logger.getLogger(FMSGCallBackController.class);
	
	// 获取图片的本地保存路径
	String imageSavePath = RWProperties.getImageSavePath();
	String sendURL = RWProperties.getSendURL();
	
	@Override
	public boolean invoke(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		log.info("进入回调函数");
		System.out.println("-----开始监听-----");
		String sAlarmType = new String();
		// 报警时间
		// lCommand是传的报警类型
		 sAlarmType = new String("lCommand=") + lCommand;
		switch (lCommand) {
			// 4730 人脸抓拍结果信息
			case HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT:
				// 人脸库按图搜索
				System.out.println("检测到了人脸");
				HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
		         strFaceSnapInfo.write();
		         Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
		         pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
		         strFaceSnapInfo.read();
		         System.out.println("人脸评分："+strFaceSnapInfo.dwFaceScore);
				 if(strFaceSnapInfo.dwFaceScore>80) {
					 SimpleDateFormat df = new SimpleDateFormat ("yyyyMMddHHmmss");//设置日期格式
					 String time = df.format (new Date ( ));// new Date()为获取当前系统时间
					 System.out.println("face score is " + strFaceSnapInfo.dwFaceScore+" point");

					 try {
						 //设置图片保存路径
						 FileOutputStream small = new FileOutputStream (imageSavePath + time + "small.jpg");
						 FileOutputStream big = new FileOutputStream (imageSavePath + time + "big.jpg");
						 try {
							 small.write (strFaceSnapInfo.pBuffer1.getByteArray (0, strFaceSnapInfo.dwFacePicLen), 0, strFaceSnapInfo.dwFacePicLen);
							 small.close ( );
						 } catch (IOException ex) {
							 ex.printStackTrace ( );
						 }
						 try {
							 big.write (strFaceSnapInfo.pBuffer2.getByteArray (0, strFaceSnapInfo.dwBackgroundPicLen), 0, strFaceSnapInfo.dwBackgroundPicLen);
							 big.close ( );
						 } catch (IOException ex) {
							 ex.printStackTrace ( );
						 }

					 } catch (FileNotFoundException ex) {
						 ex.printStackTrace ( );
					 }
				 }
		
				break;
			// 8000报警

			// ATM DVR transaction information
			case HCNetSDK.COMM_TRADEINFO:
				System.out.println("处理交易信息报警");
				break;

			// IPC接入配置改变报警
			case HCNetSDK.COMM_IPCCFG:
				System.out.println("处理IPC报警");
				break;
				
//			case HCNetSDK.NET_DVR_CAPTURE_FACE_INFO:
//				System.out.println("采集人脸信息");
//				break;
				
			case HCNetSDK.COMM_SNAP_MATCH_ALARM:
				System.out.println("进入人脸识别");
				String employeeName ="";
				NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
				strFaceSnapMatch.write();
			 	Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
			 	pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
			 	strFaceSnapMatch.read();
			 	// 获取人脸的 x y 的坐标及坐标的宽和高 并进行四舍五入
			 	float fX = strFaceSnapMatch.struRegion.fX;
			 	float fY = strFaceSnapMatch.struRegion.fY;
			 	float fWidth = strFaceSnapMatch.struRegion.fWidth;
			 	float fHeight = strFaceSnapMatch.struRegion.fHeight;
			 	int x = Math.round(fX * 1920);
			 	int y = Math.round(fY *1080);
			 	int width = Math.round(fWidth * 1920);
			 	int height = Math.round(fHeight * 1080);
//			 	strFaceSnapMatch.dwS
			 	System.out.println("图片长度："+strFaceSnapMatch.dwSnapPicLen);
			 	System.out.println("图片数据传输方式 0-二进制；1-url："+strFaceSnapMatch.byPicTransType);
			 	SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		 		String newName = sf.format(new Date());
		 		FileOutputStream fout;
//		 		System.out.println("这是什么："+strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
		 		try {
					sAlarmType = sAlarmType + "：人脸比对报警，相识度：" + strFaceSnapMatch.fSimilarity + "，姓名：" +
	                        new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byName, "GBK").trim() + "，证件信息：" +
	                        new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byCertificateNumber).trim();
//					System.out.println(sAlarmType+"ccccc");
					//相识度
					float fSimilarity = strFaceSnapMatch.fSimilarity;
//				 	System.out.println("相识度："+fSimilarity);
				 	if(fSimilarity<0.6) {
				 		System.out.println("未在人脸库查找到人脸");
			 			// 将照片进行保存并发送
				 		// 初始化陌生人的相关信息
				 		employeeName = new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byName, "GBK").trim();
				 		String name = "陌生人";
				 		String jszh = "-1";
				 		String classroom = "-1";
				 		String filename = imageSavePath + newName + "_pSnapPicBuffer" + ".jpg";
						String filename2 = imageSavePath + newName + "_pSnapPicBuffer_small" + ".jpg";
				 		fout = new FileOutputStream(filename);
				 		//将字节写入文件
			 			long offset = 0;
//			 			ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
			 			ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
			 			byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
			 			buffers.rewind();
			 			buffers.get(bytes);
			 			fout.write(bytes);
			 			fout.close();
			 			ImageUtil.TrimImage(x, y, width, height, filename, filename2);
			 			String base64 = PostSend.ImagetoBase64(filename2);// 使用截取出来的小图片进行传送
						// 获取该学校对应的 数据库 ID
						String schoolDBId = RWProperties.getSchoolDBId();
						if ( null == schoolDBId && "".equals( schoolDBId ) ) {
							System.out.println("配置文件有误，请检查配置信息！");
							break;
						}
			 			PostSend.sendPost(sendURL, "db=" + schoolDBId + "&xm="+ name + 
			 					"&JSZH=" + jszh + "&dw=" + classroom + "&img=" + base64 );
				 	}else {
				 		System.out.println("识别到了人脸");
				 		//姓名
						employeeName = new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byName, "GBK").trim();
						String[] employeeName1 = employeeName.split("-");
						// System.out.println("姓名："+employeeName1[0] + "------" +employeeName1[1] +"-----"+employeeName1[2] );
						String filename = imageSavePath + newName + "_pSnapPicBuffer" + ".jpg";
						String filename2 = imageSavePath + newName + "_pSnapPicBuffer_small" + ".jpg";
			 			fout = new FileOutputStream(filename);
						//将字节写入文件
			 			long offset = 0;
//			 			ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
			 			ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
			 			byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
			 			buffers.rewind();
			 			buffers.get(bytes);
			 			fout.write(bytes);
			 			fout.close();
			 			ImageUtil.TrimImage(x, y, width, height, filename, filename2);
						// 发送数据
			 			// 将图片转为 base64 
			 		// 将图片转为 base 64
						String base64 = PostSend.ImagetoBase64(filename2);// 使用截取出来的小图片进行传送
						// 获取该学校对应的 数据库 ID
						String schoolDBId = RWProperties.getSchoolDBId();
						if ( null == schoolDBId && "".equals( schoolDBId ) ) {
							System.out.println("配置文件有误，请检查配置信息！");
							break;
						}
			 			PostSend.sendPost(sendURL, "db=" + schoolDBId + "&xm="+employeeName1[0] + 
			 					"&JSZH=" + employeeName1[1] + "&dw=" + employeeName1[2] + "&img=" + base64 );
				 	}
				 	
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			default:
				System.out.println("未知报警类型");
				break;
		}
		return false;
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
