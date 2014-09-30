// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.Week;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class WeekDao extends HibernateBaseDaoImpl<Week, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 控制领导下周日程提交状态
	 * @param weekId
	 * @param leaderId
	 * @param state
	 */
	public void saveOrUpdateWeekLeader(String weekId,int leaderId,String state){
		String sql ="replace into tb_c_week_leader_state(week_id,leader_id,state)"
				+ "values(?,?,?)";
		this.jdbcTemplate.update(sql, weekId,leaderId,state);
	}
	/**
	 * 获取当前领导下周日程的提交状态
	 * @param weekId
	 * @param leaderId
	 * @return
	 */
	public String getSate(String weekId,int leaderId ){
		String sql = "select state from tb_c_week_leader_state "
				+ "where week_id=? and leader_id=?";
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql, weekId,leaderId);
		if(null != list && !list.isEmpty()){
			return list.get(0).get("state").toString();
		}
		return null;
	}
}
