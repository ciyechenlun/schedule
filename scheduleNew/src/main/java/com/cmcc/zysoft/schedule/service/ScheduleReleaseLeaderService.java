// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleReleaseLeaderDao;
import com.cmcc.zysoft.schedule.model.ScheduleReleaseLeader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleReleaseLeaderService extends BaseServiceImpl<ScheduleReleaseLeader, String> {

	/**
	 * 属性名称：ScheduleReleaseLeaderDao 类型：ScheduleReleaseLeaderDao
	 */
	@Resource
	private ScheduleReleaseLeaderDao scheduleReleaseLeaderDao;
	
	@Override
	public HibernateBaseDao<ScheduleReleaseLeader, String> getHibernateBaseDao() {
		return scheduleReleaseLeaderDao;
	}
	
}
