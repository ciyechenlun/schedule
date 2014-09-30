package com.cmcc.zysoft.schedule.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WeekUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetNextMonday() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date;
		try {
			date = format.parse("2014/6/30");
			Date monday = WeekUtil.getNextMonday(date);
			Calendar c=Calendar.getInstance();
			c.setTime(monday);
			System.out.println(monday);
			assertEquals("周一", 2, c.get(Calendar.DAY_OF_WEEK));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testGetThisMonday() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date;
		try {
			date = format.parse("2014/7/6");
			Date monday = WeekUtil.getThisMonday(date);
			Calendar c=Calendar.getInstance();
			c.setTime(monday);
			System.out.println(monday);
			assertEquals("周一", 2, c.get(Calendar.DAY_OF_WEEK));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
