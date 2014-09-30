// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;



import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.ScheduleReleaseLeader;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class ScheduleReleaseLeaderDao extends HibernateBaseDaoImpl<ScheduleReleaseLeader, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	

}
