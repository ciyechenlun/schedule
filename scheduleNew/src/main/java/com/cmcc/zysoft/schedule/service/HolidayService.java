// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.HolidayDao;
import com.cmcc.zysoft.schedule.dao.LeaderDao;
import com.cmcc.zysoft.schedule.model.Holiday;
import com.cmcc.zysoft.schedule.model.Leader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class HolidayService extends BaseServiceImpl<Holiday, Integer> {

	/**
	 * 属性名称：holidayDao 类型：HolidayDao
	 */
	@Resource
	private HolidayDao holidayDao;
	
	@Override
	public HibernateBaseDao<Holiday, Integer> getHibernateBaseDao() {
		return holidayDao;
	}

}
