package com.cmcc.zysoft.schedule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.cmcc.zysoft.schedule.model.SystemUser;
import com.cmcc.zysoft.schedule.service.SystemUserPCService;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@ContextConfiguration(locations={"/META-INF/spring/schedule-context.xml"})
public class UserContollerTest{
	@Resource
	private UserContoller userContoller;
	@Resource
	private SystemUserPCService userService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSaveIfAdd() {
		SystemUser su = new SystemUser();
		su.setUserName("15256089300");
		su.setRealName("小婉");
		su.setMobile("15256089300");
		String roleId="1";
		String leaderId="1";
		su.setDelFlag("0");
		su.setDisOrder(1);
		
		this.userContoller.save(su, roleId, leaderId,null);
		assertNotNull("已插入到数据库", this.userService.findByNamedParam("userName", "15256089300"));
	}
	@Test
	public void testSaveIfUpdate() {
		SystemUser su = this.userService.getEntity("1");
		String preOrder = String.valueOf(su.getDisOrder());
		su.setDisOrder(2);
		String roleId="1";
		String leaderId="1";
		this.userContoller.save(su, roleId, leaderId,preOrder);
		assertEquals("顺序已修改", 2, this.userService.getEntity("1").getDisOrder());
	}

}
