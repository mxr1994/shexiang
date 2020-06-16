package com.tc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class RWProperties {

	public static String getSchoolDBId () {
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			String schoolId =properties.getProperty("schoolId");
			return schoolId;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, String> getCameraInfo () {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			String ip =properties.getProperty("ip");
			String username =properties.getProperty("username");
			String password =properties.getProperty("password");
			String port =properties.getProperty("port");
			map.put("ip", ip);
			map.put("username", username);
			map.put("password", password);
			map.put("port", port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String, String> getURL () {
		Map<String, String> map = new HashMap<String, String> ();
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			String projectURL =properties.getProperty("projectURL");
			String getFaceURL =properties.getProperty("getFaceURL");
			String db =properties.getProperty("db");
			map.put("projectURL", projectURL);
			map.put("getFaceURL", getFaceURL);
			map.put("db", db);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static String getFilePath() {
		String filePath = "";
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			filePath =properties.getProperty("filePath");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	public static String getImageSavePath() {
		String filePath = "";
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			filePath =properties.getProperty("imageFile");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	public static String getSendURL () {
		String sendURL = "";
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			sendURL =properties.getProperty("sendURL");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sendURL;
	}
	
	public static String getWorkStatus () {
		String workStatus = "";
		try {
			Properties properties = new Properties();
			properties = PropertiesLoaderUtils.loadAllProperties("schoolConfig.properties");
			workStatus =properties.getProperty("workStatus");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workStatus;
	}
}
