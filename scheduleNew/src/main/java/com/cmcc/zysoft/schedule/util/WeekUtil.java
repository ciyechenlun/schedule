/**
 * @author AMCC
 * <br /> 邮箱:zhouyusgs@ahmobile.com
 * <br /> 描述:CacheFileUtil.java
 * <br /> 版本：1.0.0
 * <br /> 日期：2013-10-12
 */
package com.cmcc.zysoft.schedule.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author AMCC
 *
 */
public class WeekUtil {

	public WeekUtil() {
		super();
	}

	/**
	 * 获取下周一
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextMonday(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		if (week >= 2) {
			cal.add(Calendar.DAY_OF_MONTH, -(week - 2) + 7);
		} else {
			cal.add(Calendar.DAY_OF_MONTH, week);
		}
		return cal.getTime();
	}

	/**
	 * 获取本周一
	 * 
	 * @param date
	 * @return
	 */
	public static Date getThisMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0) {
			dayofweek = 7;
		}
		c.add(Calendar.DATE, -dayofweek + 1);
		return c.getTime();
	}
	/**
	 * 获取周一到周日
	 * @param date
	 * @return
	 */
	 public static Date[] getWeekDay(Date date) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);  
	        Date[] dates = new Date[7];
	        for (int i = 0; i < 7; i++) {
	            dates[i] = calendar.getTime();
	            calendar.add(Calendar.DATE, 1);
	        }
	        return dates;
	    }
}
