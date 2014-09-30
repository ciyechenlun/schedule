package com.cmcc.zysoft.schedule.service;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.cmcc.zysoft.schedule.dao.ScheduleLeaderDao;
import com.cmcc.zysoft.schedule.model.Schedule;
import com.cmcc.zysoft.schedule.model.ScheduleLeader;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@ContextConfiguration(locations={"/META-INF/spring/schedule-context.xml"})
public class ScheduleServiceTest{

	@Resource
	private ScheduleService scheduleService;
	@Resource
	private ScheduleLeaderDao scheduleLeaderDao;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddNormal(){
		//正常情况
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date start1 = df.parse("2009-06-11 11:40:20");
			Date end1 = df.parse("2009-06-11 14:40:20");
			Schedule sc1 = new Schedule();
			sc1.setStartTime(start1);
			sc1.setEndTime(end1);
			int leaderId1=1;
			
			assertEquals("添加正确", "0", this.scheduleService.add(sc1, leaderId1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testDragSchedule(){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
		Date start1 = df.parse("2000-06-21 11:40:20");
		Date end1 = df.parse("2000-06-21 14:40:20");
		Schedule sc1 = new Schedule();
		sc1.setStartTime(start1);
		sc1.setEndTime(end1);
		sc1.setType("0");//新增
		sc1.setCreateUserType("0");//非领导
		sc1.setWeekId("8a18965846b720de0146b720e3e00000");
		int leaderId1=1;
		this.scheduleService.insertEntity(sc1);
		ScheduleLeader sl = new ScheduleLeader();
		sl.setLeaderId(leaderId1);
		sl.setScheduleId(sc1.getScheduleId());
		this.scheduleLeaderDao.save(sl);
		String result = this.scheduleService.dragSchedule(sc1,leaderId1);
		assertEquals("没有重合", "", result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testDragScheduleRepeat(){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
		Date start1 = df.parse("2011-06-21 11:40:20");
		Date end1 = df.parse("2011-06-21 14:40:20");
		Schedule sc1 = new Schedule();
		sc1.setStartTime(start1);
		sc1.setEndTime(end1);
		sc1.setType("0");//新增
		sc1.setCreateUserType("0");//非领导
		sc1.setWeekId("8a18965846b720de0146b720e3e00000");
		int leaderId1=1;
		String result = this.scheduleService.dragSchedule(sc1,leaderId1);
		assertNotEquals("重合","",result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testUpdate(){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date start1 = df.parse("2011-06-21 11:40:20");
			Date end1 = df.parse("2011-06-21 14:40:20");
			List<Schedule> list = this.scheduleService.findByNamedParam(new String[]{"startTime","endTime"},new Object[]{start1,end1});
			if(list.size()>0){
				String result = this.scheduleService.update(list.get(0), 1);
				assertEquals("修改失败","1",result);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
