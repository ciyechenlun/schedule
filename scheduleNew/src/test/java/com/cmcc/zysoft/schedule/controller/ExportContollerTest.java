package com.cmcc.zysoft.schedule.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.cmcc.zysoft.schedule.model.Week;
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@ContextConfiguration(locations={"/META-INF/spring/schedule-context.xml"})
public class ExportContollerTest {
	@Resource
	private ExportContoller exp;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testExport() {
		Week week = new Week();
		week.setWeekId("4028812b479e856f01479e86efe80000");
		week.setWeekStart(new Date());
		try {
		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream("D://workspace//scheduleNew//src//main//webapp//resources//excel//template.xls"));
		exp.createWorkbook(week,workbook);
		  File file = new File("D://test01.xls");  
	       OutputStream out = new FileOutputStream(file);  
	       workbook.write(out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
