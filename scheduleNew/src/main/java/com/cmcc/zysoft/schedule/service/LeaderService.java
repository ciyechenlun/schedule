// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.LeaderDao;
import com.cmcc.zysoft.schedule.model.Leader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class LeaderService extends BaseServiceImpl<Leader, Integer> {

	/**
	 * 属性名称：LeaderDao 类型：LeaderDao
	 */
	@Resource
	private LeaderDao leaderDao;
	
	@Override
	public HibernateBaseDao<Leader, Integer> getHibernateBaseDao() {
		return leaderDao;
	}

}
