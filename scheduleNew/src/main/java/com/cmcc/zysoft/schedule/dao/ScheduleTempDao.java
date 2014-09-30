// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.ScheduleTemp;
import com.cmcc.zysoft.schedule.model.Week;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class ScheduleTempDao extends HibernateBaseDaoImpl<ScheduleTemp, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 存在时间冲突的日程数
	 * @param start
	 * @param end
	 * @param leaderId
	 * @return
	 */
	public int countByStartOrEnd(Date start,Date end,int leaderId){
		String sql="select count(*) from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_schedule_temp s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and type='0' " +
				" and not(s.start_time>? or s.end_time<?) "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForInt(sql, leaderId,end,start);
	}
	/**
	 * 存在时间冲突的日程数（除去自己，更新日程时用）
	 * @return
	 */
	public int countByStartOrEndExceptOwn(Date start,Date end,String scheduleId,int leaderId){
		String sql="select count(*) from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_schedule_temp s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and type='0' " +
				" and s.schedule_id !=? and not(s.start_time>? or s.end_time<?) "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForInt(sql, leaderId,scheduleId,end,start);
	}
	/**
	 * 秘书加载对应领导的所有日程
	 * @param leaderId
	 * @return
	 */
	public List<Map<String,Object>> getScheduleTempByLeader(int leaderId){
		String sql="select s.schedule_id as id,s.title as title,s.start_time as start,s.end_time as end, " +
				"false as allDay,true as editable "+
				"from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_schedule_temp s  on sl.schedule_id=s.schedule_id " +
				"where sl.leader_id=? "+
				" and type='0' "+
				 " and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, leaderId);
	}


	/**
	 * 发布日程
	 * @param userId
	 */
	public void updateScheduleTempToRelease(String userId,String weekId){
		//更新秘书查询的总日程表中数据
		String sql = "replace into tb_c_schedule select * from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_leader select * from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//更新副总审核表中的数据
		sql = "replace into tb_c_schedule_commit select * from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_commit_leader select * from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//更新发布表中的数据
		sql = "replace into tb_c_schedule_release select * from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_release_leader select * from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//删除临时表的数据
		sql = "delete from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		sql = "delete from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
	}
	/**
	 * 发布日程
	 * @param userId
	 */
	public void noOperateRelease(String userId,String weekId){
		//更新秘书查询的总日程表中数据
		String sql = "replace into tb_c_schedule select * from tb_c_schedule_commit where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_leader select * from tb_c_schedule_commit_leader " +
				"where schedule_id in (select schedule_id from tb_c_schedule_commit s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//更新发布表中的数据
		sql = "replace into tb_c_schedule_release select * from tb_c_schedule_commit where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_release_leader select * from tb_c_schedule_commit_leader " +
				"where schedule_id in (select schedule_id from tb_c_schedule_commit s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
	}
	/**
	 * 秘书提交日程
	 * @param userId
	 */
	public void updateScheduleTempToCommit(String userId,String weekId){
		//更新秘书查询的总日程表中数据
		String sql = "replace into tb_c_schedule select * from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_leader select * from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//更新副总审核表中的数据
		sql = "replace into tb_c_schedule_commit select * from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
		sql = "replace into tb_c_schedule_commit_leader select * from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		//删除临时表的数据
		sql = "delete from tb_c_schedule_leader_temp " +
				"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
		this.jdbcTemplate.update(sql, weekId);
		sql = "delete from tb_c_schedule_temp where week_id=?";
		this.jdbcTemplate.update(sql, weekId);
	}
	/**
	 * 获取待审核的领导日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getSubmitScheduleTemp(String weekId){
		String sql = "select l.leader_id,l.leader_name,l.mobile,sl.schedule_leader_id," +
				"s.schedule_id,s.week_id,s.title,s.start_time,s.end_time from tb_c_leader l " +
				"LEFT JOIN tb_c_schedule_leader_temp sl on l.leader_id=sl.leader_id LEFT JOIN " +
				"tb_c_schedule_temp s on sl.schedule_id=s.schedule_id " +
				"where s.week_id=? and s.type ='0' and s.del_flag='0' and sl.del_flag='0' ORDER BY leader_id,start_time";
		return this.jdbcTemplate.queryForList(sql, weekId);
	}
	/**
	 * 获取本周领导日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getThisWeekScheduleTemp(String weekId){
		String sql = "select l.leader_id,l.leader_name,l.mobile,sl.schedule_leader_id," +
				"s.schedule_id,s.week_id,s.title,s.start_time,s.end_time from tb_c_leader l " +
				"LEFT JOIN tb_c_schedule_leader_temp sl on l.leader_id=sl.leader_id LEFT JOIN " +
				"tb_c_schedule_temp s on sl.schedule_id=s.schedule_id " +
				"JOIN tb_c_week w on s.week_id=w.week_id " +
				"where s.week_id=? and s.type ='0' and s.del_flag='0' and sl.del_flag='0' ORDER BY leader_id,start_time";
		return this.jdbcTemplate.queryForList(sql, weekId);
	}
	/**
	 * 获取所有领导
	 * @return
	 */
	public List<Map<String,Object>> getAllLeaders(){
		String sql = "select l.leader_id,l.leader_name from tb_c_leader l LEFT JOIN tb_c_system_user u on l.leader_id=u.leader_id GROUP BY l.leader_id ORDER BY u.dis_order";
		return this.jdbcTemplate.queryForList(sql);
	}

	/**
	 * 与该日程时间冲突的日程
	 * @return
	 */
	public List<Map<String,Object>> getExpectScheduleTemps(Date start,Date end,String scheduleId,int leaderId){
		String sql="select s.schedule_id,s.title,s.start_time from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_schedule_temp s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and type='0' " +
				" and s.schedule_id !=? and not(s.start_time>=? or s.end_time<=?) and s.del_flag='0' and sl.del_flag='0' order by s.start_time";
		return this.jdbcTemplate.queryForList(sql, leaderId,scheduleId,end,start);
	}
	/**
	 * 将日程存为待办
	 * @param scheduleId
	 */
	public void updateToToDo(String scheduleId){
		String sql = "update tb_c_schedule_temp set start_time=null,end_time=null,type='3' where schedule_id=?";
		this.jdbcTemplate.update(sql, scheduleId);
	}
	/**
	 * 时间冲突的日程
	 * @return
	 */
	public List<Map<String,Object>> getExpectScheduleTempsByLeader(Date start,Date end,int leaderId){
		String sql="select s.schedule_id,s.title from tb_c_schedule_leader_temp sl LEFT JOIN tb_c_schedule_temp s " +
				" on sl.schedule_id=s.schedule_id where sl.leader_id=? " +
				" and type='0' " +
				" and not(s.start_time>=? or s.end_time<=?) and s.del_flag='0' and sl.del_flag='0'";
		return this.jdbcTemplate.queryForList(sql, leaderId,end,start);
	}
	/**
	 * 初始化临时表的数据
	 * @param userId
	 * @param week
	 * @return
	 */
	public boolean initTempData(String userId,Week week){
		String weekId = week.getWeekId();
		String sql = "select mark1 from tb_c_week where week_id=?";
		Map<String,Object> map =this.jdbcTemplate.queryForMap(sql, weekId);
		String mark1 = map.get("mark1")==null?null:map.get("mark1").toString();
		if(null==mark1 || "".equals(mark1)){
			sql="delete from tb_c_schedule_leader_temp " +
					"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
			this.jdbcTemplate.update(sql,weekId);
			sql = "delete from tb_c_schedule_temp where week_id=?";
			this.jdbcTemplate.update(sql,weekId);
			sql="insert into tb_c_schedule_temp select * from tb_c_schedule_commit where week_id=?";
			this.jdbcTemplate.update(sql, weekId);
			sql="insert into tb_c_schedule_leader_temp select sl.* " +
					"from tb_c_schedule_commit_leader sl,tb_c_schedule_commit s " +
					"where sl.schedule_id=s.schedule_id and s.week_id=? ";
			this.jdbcTemplate.update(sql, weekId);
			sql = "update tb_c_week set mark1=? where week_id=?";
			this.jdbcTemplate.update(sql,userId,weekId);
		}else if(!"".equals(mark1) && !userId.equals(mark1)){
			return false;
		}else{
			//
		}
		return true;
	}
	/**
	 * 添加已修改标记
	 * @param userId
	 */
	public void addChangeFlag(String userId){
		String sql = "update tb_c_week set mark2='1' where mark1=?";
		this.jdbcTemplate.update(sql,userId);
	}
	/**
	 * 删除临时表，修改操作人为null
	 * @param userId
	 */
	public void clearTemp(String userId){
		String sql= "select week_id from tb_c_week where mark1=?";
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql,userId);
		if(null !=list&&list.size()>0){
			String weekId = list.get(0).get("week_id").toString();
			sql = "delete from tb_c_schedule_leader_temp " +
					"where schedule_id in (select schedule_id from tb_c_schedule_temp s where  s.week_id=?)";
			this.jdbcTemplate.update(sql,weekId);
			sql = "delete from tb_c_schedule_temp where week_id=?";
			this.jdbcTemplate.update(sql, weekId);
			sql = "update tb_c_week set mark1=null,mark2=null where mark1=?";
			this.jdbcTemplate.update(sql,userId);
		}
	}
	/**
	 * 判断是否有改动
	 * @param userId
	 * @return
	 */
	public boolean checkModify(String userId){
		String sql= "select count(*) from tb_c_week where mark1=? and mark2='1'";
		int count = this.jdbcTemplate.queryForInt(sql, userId);
		if(count>0){
			return true;
		}
		return false;
	}
	public void delSchedule(String id) {
		String sql ="update tb_c_schedule_temp set del_flag='1' where schedule_id=?";
		this.jdbcTemplate.update(sql, id);
	}
	
}
