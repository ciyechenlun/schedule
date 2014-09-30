// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.Schedule;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;
import com.starit.common.dao.jdbc.NamedParameterJdbcTemplateExt;

/**
 * @author zhangjun
 */
@Repository
public class ScheduleDao extends HibernateBaseDaoImpl<Schedule, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private NamedParameterJdbcTemplateExt namedParameterJdbcTemplateExt;
	/**
	 * 存在时间冲突的日程数
	 * @param start
	 * @param end
	 * @param leaderId
	 * @return
	 */
	public int countByStartOrEnd(Date start,Date end,int leaderId){
		String sql="select count(*) from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? "+
				"and s.type='0' and not(s.start_time>? or s.end_time<?) "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForInt(sql, leaderId,end,start);
	}
	/**
	 * 存在时间冲突的日程数（除去自己，更新日程时用）
	 * @return
	 */
	public int countByStartOrEndExceptOwn(Date start,Date end,String scheduleId,int leaderId){
		String sql="select count(*) from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? "+
				"and s.type='0' and s.schedule_id !=? and not(s.start_time>? or s.end_time<?) "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForInt(sql, leaderId,scheduleId,end,start);
	}
	/**
	 * 秘书加载对应领导的所有日程
	 * @param leaderId
	 * @return
	 */
	public List<Map<String,Object>> getScheduleByLeader(int leaderId,Date dateStart,Date dateEnd){
		String sql="select s.schedule_id as id,s.title as title,s.start_time as start,s.end_time as end, " +
				"false as allDay,true as editable "+
				"from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s  on sl.schedule_id=s.schedule_id " +
				"where sl.leader_id=? "+
				"and s.type='0' and s.start_time>=? and s.end_time<?"+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, leaderId,dateStart,dateEnd);
	}
	/**
	 *将对应领导日程修改为提交状态
	 * @param userId
	 * @param leaderId
	 */
	public void updateScheduleToSubmit(String userId,int leaderId,String weekId){
		String sql = "replace into tb_c_schedule_commit "
				+ "SELECT s.* from tb_c_schedule s join tb_c_schedule_leader sl "
				+ "on s.schedule_id=sl.schedule_id where sl.leader_id=? and s.week_id=?";
		this.jdbcTemplate.update(sql, leaderId,weekId);
		sql = "replace into tb_c_schedule_commit_leader "
				+ "SELECT sl.* from tb_c_schedule s join tb_c_schedule_leader sl "
				+ "on s.schedule_id=sl.schedule_id where sl.leader_id=? and s.week_id=?";
		this.jdbcTemplate.update(sql, leaderId,weekId);
	}

	/**
	 * 获取对应领导的所有待办任务
	 * @param leaderId
	 * @return
	 */
	public List<Map<String,Object>> getToDoList(int leaderId){
		String sql="select s.schedule_id,s.title,s.men,s.details " +
				"from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s  " +
				"on sl.schedule_id=s.schedule_id  where sl.leader_id=?  and s.type='3'"+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, leaderId);
	}

	/**
	 * 获取待审核的领导日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getSubmitSchedule(String weekId){
		String sql = "select l.leader_id,l.leader_name,l.mobile,sl.schedule_leader_id," +
				"s.schedule_id,s.week_id,s.title,s.start_time,s.end_time from tb_c_leader l " +
				"LEFT JOIN tb_c_schedule_commit_leader sl on l.leader_id=sl.leader_id LEFT JOIN " +
				"tb_c_schedule_commit s on sl.schedule_id=s.schedule_id " +
				"where s.week_id=? and s.type ='0' and s.del_flag='0' and sl.del_flag='0' ORDER BY leader_id,start_time";
		return this.jdbcTemplate.queryForList(sql, weekId);
	}
	/**
	 * 获取本周领导日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getThisWeekSchedule(String weekId){
		String sql = "select l.leader_id,l.leader_name,l.mobile,sl.schedule_leader_id," +
				"s.schedule_id,s.week_id,s.title,s.start_time,s.end_time from tb_c_leader l " +
				"LEFT JOIN tb_c_schedule_commit_leader sl on l.leader_id=sl.leader_id LEFT JOIN " +
				"tb_c_schedule_commit s on sl.schedule_id=s.schedule_id " +
				"where s.week_id=? and s.type ='0' and s.del_flag='0' and sl.del_flag='0' ORDER BY leader_id,start_time";
		return this.jdbcTemplate.queryForList(sql, weekId);
	}
	/**
	 * 获取所有领导
	 * @return
	 */
	public List<Map<String,Object>> getAllLeaders(){
		String sql = "select l.leader_id,l.leader_name from tb_c_leader l LEFT JOIN tb_c_system_user u " +
				"on l.leader_id=u.leader_id " +
				"where u.user_name !='s_admin' "+
				"GROUP BY l.leader_id ORDER BY u.dis_order";
		return this.jdbcTemplate.queryForList(sql);
	}

	/**
	 * 与该日程时间冲突的日程
	 * @return
	 */
	public List<Map<String,Object>> getExpectSchedules(Date start,Date end,String scheduleId,int leaderId){
		String sql="select s.schedule_id,s.title,s.start_time from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and s.type='0' " +
				" and s.schedule_id !=? and not(s.start_time>=? or s.end_time<=?) and s.del_flag='0' and sl.del_flag='0' order by s.start_time";
		return this.jdbcTemplate.queryForList(sql, leaderId,scheduleId,end,start);
	}
	/**
	 * 将日程存为待办
	 * @param scheduleId
	 */
	public void updateToToDo(String scheduleId){
		String sql = "update tb_c_schedule set week_id=null,start_time=null,end_time=null,type='3' where schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
	}
	/**
	 * 时间冲突的日程
	 * @return
	 */
	public List<Map<String,Object>> getExpectSchedulesByLeader(Date start,Date end,int leaderId){
		String sql="select s.schedule_id,s.title from tb_c_schedule_leader sl LEFT JOIN tb_c_schedule s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and s.type='0' " +
				" and not(s.start_time>=? or s.end_time<=?) "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, leaderId,end,start);
	}
	/**
	 * 发布单个日程
	 * @param scheduleId
	 */
	public void addReleaseEvent(String scheduleId){
		String sql = "replace into tb_c_schedule_commit "
				+ "SELECT s.* from tb_c_schedule s where s.schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
		sql = "replace into tb_c_schedule_commit_leader "
				+ "SELECT sl.* from tb_c_schedule_leader sl where sl.schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
		sql = "replace into tb_c_schedule_release "
				+ "SELECT s.* from tb_c_schedule s where s.schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
		sql = "replace into tb_c_schedule_release_leader "
				+ "SELECT sl.* from tb_c_schedule_leader sl where sl.schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
	}
	public void delSchedule(String id) {
		String sql ="update tb_c_schedule_temp set del_flag='1' where schedule_id=?";
		this.jdbcTemplate.update(sql, id);
	}
}
