// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleLeaderTempDao;
import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleLeaderTempService extends BaseServiceImpl<ScheduleLeaderTemp, String> {

	/**
	 * 属性名称：ScheduleLeaderTempDao 类型：ScheduleLeaderTempDao
	 */
	@Resource
	private ScheduleLeaderTempDao scheduleLeaderTempDao;
	
	@Override
	public HibernateBaseDao<ScheduleLeaderTemp, String> getHibernateBaseDao() {
		return scheduleLeaderTempDao;
	}
	/**
	 * 删除日程或待办日程的领导关联记录
	 * @param scheduleId
	 */
	public void delBySchedule(String scheduleId){
		this.scheduleLeaderTempDao.delBySchedule(scheduleId);
	}
	/**
	 * 删除一笔关联记录
	 * @param scheduleId
	 * @param leaderId
	 */
	public void delByScheduleLeaderTemp(String scheduleId,int leaderId){
		this.scheduleLeaderTempDao.delByScheduleLeaderTemp(scheduleId, leaderId);
	}
	/**
	 * 获取日程对应的领导
	 * @param scheduleId
	 * @return
	 */
	public List<Map<String,Object>> getBySchedule(String scheduleId){
		return this.scheduleLeaderTempDao.getBySchedule(scheduleId);
	}
}
