// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleLeaderDao;
import com.cmcc.zysoft.schedule.model.ScheduleLeader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleLeaderService extends BaseServiceImpl<ScheduleLeader, String> {

	/**
	 * 属性名称：ScheduleLeaderDao 类型：ScheduleLeaderDao
	 */
	@Resource
	private ScheduleLeaderDao scheduleLeaderDao;
	
	@Override
	public HibernateBaseDao<ScheduleLeader, String> getHibernateBaseDao() {
		return scheduleLeaderDao;
	}
	/**
	 * 删除日程或待办日程的领导关联记录
	 * @param scheduleId
	 */
	public void delBySchedule(String scheduleId){
		this.scheduleLeaderDao.delBySchedule(scheduleId);
	}
	/**
	 * 删除一笔关联记录
	 * @param scheduleId
	 * @param leaderId
	 */
	public void delByScheduleLeader(String scheduleId,int leaderId){
		this.scheduleLeaderDao.delByScheduleLeader(scheduleId, leaderId);
	}
	/**
	 * 获取日程对应的领导
	 * @param scheduleId
	 * @return
	 */
	public List<Map<String,Object>> getBySchedule(String scheduleId){
		return this.scheduleLeaderDao.getBySchedule(scheduleId);
	}
}
