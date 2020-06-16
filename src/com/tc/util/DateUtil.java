package com.tc.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * 关于java.util.Date、java.sql.Timestamp和String之间的互相转换的方法
 * 
 * @Description: TODO
 * @CreateTime: 2018年03月23日 上午10:35
 * @version V1.0
 * @author 
 */
public class DateUtil {

	/**
	 * 将String字符串转换为java.util.Date格式日期
	 * 
	 * @param strDate
	 *            表示日期的字符串
	 * @param dateFormat
	 *            传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
	 * @return java.util.Date类型日期对象（如果转换失败则返回null）
	 */
	public static java.util.Date strToUtilDate(String strDate, String dateFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
		java.util.Date date = null;
		try {
			date = sf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getCurrTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(System.currentTimeMillis());
	}

	/**
	 * 将String字符串转换为java.sql.Timestamp格式日期,用于数据库保存
	 * 
	 * @param strDate
	 *            表示日期的字符串
	 * @param dateFormat
	 *            传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
	 * @return java.sql.Timestamp类型日期对象（如果转换失败则返回null）
	 */
	public static java.sql.Timestamp strToSqlDate(String strDate,
			String dateFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
		java.util.Date date = null;
		try {
			date = sf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		java.sql.Timestamp dateSQL = new java.sql.Timestamp(date.getTime());
		return dateSQL;
	}

	/**
	 * 将java.util.Date对象转化为String字符串
	 * 
	 * @param date
	 *            要格式的java.util.Date对象
	 * @param strFormat
	 *            输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
	 * @return 表示日期的字符串
	 */
	public static String dateToStr(java.util.Date date, String strFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(strFormat);
		String str = sf.format(date);
		return str;
	}

	/**
	 * 将java.sql.Timestamp对象转化为String字符串
	 * 
	 * @param time
	 *            要格式的java.sql.Timestamp对象
	 * @param strFormat
	 *            输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
	 * @return 表示日期的字符串
	 */
	public static String dateToStr(java.sql.Timestamp time, String strFormat) {
		DateFormat df = new SimpleDateFormat(strFormat);
		String str = df.format(time);
		return str;
	}

	public static java.util.Date getDateAfter(java.util.Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	/**
	 * 将java.sql.Timestamp对象转化为java.util.Date对象
	 * 
	 * @param time
	 *            要转化的java.sql.Timestamp对象
	 * @return 转化后的java.util.Date对象
	 */
	public static java.util.Date timeToDate(java.sql.Timestamp time) {
		return time;
	}

	/**
	 * 将java.util.Date对象转化为java.sql.Timestamp对象
	 * 
	 * @param date
	 *            要转化的java.util.Date对象
	 * @return 转化后的java.sql.Timestamp对象
	 */
	public static java.sql.Timestamp dateToTime(java.util.Date date) {
		String strDate = dateToStr(date, "yyyy-MM-dd HH:mm:ss SSS");
		return strToSqlDate(strDate, "yyyy-MM-dd HH:mm:ss SSS");
	}

	/**
	 * 返回表示系统当前时间的java.util.Date对象
	 * 
	 * @return 返回表示系统当前时间的java.util.Date对象
	 */
	public static java.util.Date nowDate() {
		return new java.util.Date();
	}

	/**
	 * 返回表示系统当前时间的java.sql.Timestamp对象
	 * 
	 * @return 返回表示系统当前时间的java.sql.Timestamp对象
	 */
	public static java.sql.Timestamp nowTime() {
		return dateToTime(new java.util.Date());
	}

	/**
	 * 
	 * @return 返回前一天的java.sql.Date对象
	 */
	// Date是java.sql.Date类型
	public static java.sql.Date getPreDoneScore(Date holdDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(holdDate);
		calendar.add(calendar.DATE, -7);
		// calendar的time转成java.util.Date格式日期
		java.util.Date utilDate = (java.util.Date) calendar.getTime();
		calendar.add(calendar.DATE, 8);
		utilDate = (java.util.Date) calendar.getTime();
		// java.util.Date日期转换成转成java.sql.Date格式
		Date newDate = new Date(utilDate.getTime());
		return newDate;
	}

	/**
	 * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
	 * 
	 * @param sformat
	 *            yyyyMMddhhmmss
	 * @return
	 */
	public static String getUserDate(String sformat) {
		java.util.Date currentTime = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	// 获取月份天数
	public static int GetMonthDayCount(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12: {
			return 31;
		}
		case 4:
		case 6:
		case 9:
		case 11: {
			return 30;
		}
		case 2: {
			if (year % 4 == 0) {
				return 28;
			} else {
				if (year % 100 == 0) {
					return 29;
				} else {
					if (year % 400 == 0) {
						return 28;
					} else {
						return 29;
					}
				}
			}
		}
		default: {
			return 0;
		}
		}
	}

	// 判断这个日期是否时当月

	/**
	 * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
	 * 
	 * @param k
	 *            表示是取几位随机数，可以自己定
	 */

	public static String getNo() {

		return getUserDate("yyyyMMdd");
	}

	public static String testForDate() {
		// 规定返回日期格式
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		java.util.Date theDate = calendar.getTime();
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		// 设置为第一天
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = sf.format(gcLast.getTime());
		// 打印本月第一天
		return day_first;
	}

	public static java.util.Date nextMonthFirstDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	// 获取上个月的第一天
	public static String lastMonthFristDay() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal_1 = Calendar.getInstance();// 获取当前日期
		cal_1.add(Calendar.MONTH, -1);
		cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String firstDay = format.format(cal_1.getTime());
		return firstDay;

	}

	// 获取上个月最后一天
	public static String lastMonthLastDay() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);// 设置为1号,当前日期既为本月第一天
		String lastDay = format.format(cale.getTime());
		return lastDay;

	}

	public static boolean compare(String time1, String time2)
			throws ParseException {
		// 如果想比较日期则写成"yyyy-MM-dd"就可以了
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// 将字符串形式的时间转化为Date类型的时间
		java.util.Date a = sdf.parse(time1);
		java.util.Date b = sdf.parse(time2);
		// Date类的一个方法，如果a早于b返回true，否则返回false
		if (a.before(b))
			return true;
		else
			return false;
	}

	// String转sql
	public static java.sql.Date strToDate(String strDate) {
		String str = strDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d = null;
		try {
			d = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.sql.Date date = new java.sql.Date(d.getTime());
		return date;
	}

	public static String lastMonthLastDayTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);// 设置为1号,当前日期既为本月第一天
		String lastDay = format.format(cale.getTime());
		return lastDay;
	}

	/**
	 * 产生4位随机数
	 * 
	 * @return
	 */
	public static long generateRandomNumber(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("随机数位数必须大于0");
		}
		return (long) (Math.random() * 9 * Math.pow(10, n - 1))
				+ (long) Math.pow(10, n - 1);
	}

	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(strToUtilDate(df.format(new Date(0)), "yyyy-MM-dd HH:mm:ss"));
		System.out.println(getUserDate("yyyyMMddHHmmss")+generateRandomNumber(4));
		System.out.println(GetMonthDayCount(2020,2));
	}
}