// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class ScheduleLeaderTempDao extends HibernateBaseDaoImpl<ScheduleLeaderTemp, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 删除日程或待办日程的领导关联记录
	 * @param scheduleId
	 */
	public void delBySchedule(String scheduleId){
		String sql="update tb_c_schedule_leader_temp set del_flag='1' where schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
	}
	/**
	 * 删除一笔关联记录
	 * @param scheduleId
	 * @param leaderId
	 */
	public void delByScheduleLeaderTemp(String scheduleId,int leaderId){
		String sql="update tb_c_schedule_leader_temp set del_flag='1' where schedule_id=? and leader_id=?";
		this.jdbcTemplate.update(sql, scheduleId,leaderId);
	}
	/**
	 * 获取日程对应的领导
	 * @param scheduleId
	 * @return
	 */
	public List<Map<String,Object>> getBySchedule(String scheduleId){
		String sql="SELECT sl.*,l.leader_name " +
				"from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_leader l " +
				"on sl.leader_id=l.leader_id where sl.schedule_id=? and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, scheduleId);
	}
}
