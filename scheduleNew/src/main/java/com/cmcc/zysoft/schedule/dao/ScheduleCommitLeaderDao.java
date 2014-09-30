// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;



import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.ScheduleCommitLeader;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class ScheduleCommitLeaderDao extends HibernateBaseDaoImpl<ScheduleCommitLeader, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	/**
	 * 获取日程对应的领导
	 * @param scheduleId
	 * @return
	 */
	public List<Map<String,Object>> getBySchedule(String scheduleId){
		String sql="SELECT sl.*,l.leader_name " +
				"from tb_c_schedule_commit_leader sl LEFT JOIN tb_c_leader l " +
				"on sl.leader_id=l.leader_id where sl.schedule_id=? and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, scheduleId);
	}

}
