// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleReleaseDao;
import com.cmcc.zysoft.schedule.model.ScheduleRelease;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleReleaseService extends BaseServiceImpl<ScheduleRelease, String> {

	/**
	 * 属性名称：ScheduleReleaseDao 类型：ScheduleReleaseDao
	 */
	@Resource
	private ScheduleReleaseDao scheduleReleaseDao;

	@Override
	public HibernateBaseDao<ScheduleRelease, String> getHibernateBaseDao() {
		return scheduleReleaseDao;
	}

	
}
