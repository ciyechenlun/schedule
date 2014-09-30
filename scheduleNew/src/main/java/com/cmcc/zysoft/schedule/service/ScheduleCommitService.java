// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleCommitDao;
import com.cmcc.zysoft.schedule.model.ScheduleCommit;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleCommitService extends BaseServiceImpl<ScheduleCommit, String> {

	/**
	 * 属性名称：ScheduleCommitDao 类型：ScheduleCommitDao
	 */
	@Resource
	private ScheduleCommitDao scheduleCommitDao;

	@Override
	public HibernateBaseDao<ScheduleCommit, String> getHibernateBaseDao() {
		return scheduleCommitDao;
	}

	
}
