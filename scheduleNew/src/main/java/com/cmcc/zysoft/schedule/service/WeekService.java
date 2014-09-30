// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.WeekDao;
import com.cmcc.zysoft.schedule.model.Week;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class WeekService extends BaseServiceImpl<Week, String> {

	/**
	 * 属性名称：WeekDao 类型：WeekDao
	 */
	@Resource
	private WeekDao weekDao;
	
	@Override
	public HibernateBaseDao<Week, String> getHibernateBaseDao() {
		return weekDao;
	}
	/**
	 * 控制领导下周日程提交状态
	 * @param weekId
	 * @param leaderId
	 * @param state
	 */
	public void saveOrUpdateWeekLeader(String weekId,int leaderId,String state){
		this.weekDao.saveOrUpdateWeekLeader(weekId, leaderId, state);
	}
	/**
	 * 获取当前领导下周日程的提交状态
	 * @param weekId
	 * @param leaderId
	 * @return
	 */
	public String getSate(String weekId,int leaderId ){
		return this.weekDao.getSate(weekId, leaderId);
	}

}
