// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleCommitLeaderDao;
import com.cmcc.zysoft.schedule.model.ScheduleCommitLeader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleCommitLeaderService extends BaseServiceImpl<ScheduleCommitLeader, String> {

	/**
	 * 属性名称：ScheduleCommitLeaderDao 类型：ScheduleCommitLeaderDao
	 */
	@Resource
	private ScheduleCommitLeaderDao scheduleCommitLeaderDao;
	
	@Override
	public HibernateBaseDao<ScheduleCommitLeader, String> getHibernateBaseDao() {
		return scheduleCommitLeaderDao;
	}
	/**
	 * 获取日程对应的领导
	 * @param scheduleId
	 * @return
	 */
	public List<Map<String,Object>> getBySchedule(String scheduleId){
		return this.scheduleCommitLeaderDao.getBySchedule(scheduleId);
	}
}
