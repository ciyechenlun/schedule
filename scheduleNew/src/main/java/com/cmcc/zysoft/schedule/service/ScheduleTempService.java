// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleTempDao;
import com.cmcc.zysoft.schedule.dao.ScheduleLeaderTempDao;
import com.cmcc.zysoft.schedule.dao.WeekDao;
import com.cmcc.zysoft.schedule.model.ScheduleTemp;
import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
import com.cmcc.zysoft.schedule.model.Week;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleTempService extends BaseServiceImpl<ScheduleTemp, String> {

	/**
	 * 属性名称：ScheduleTempDao 类型：ScheduleTempDao
	 */
	@Resource
	private ScheduleTempDao scheduleTempDao;
	@Resource
	private ScheduleLeaderTempDao scheduleLeaderTempDao;
	@Resource
	private WeekDao weekDao;
	
	@Override
	public HibernateBaseDao<ScheduleTemp, String> getHibernateBaseDao() {
		return scheduleTempDao;
	}
	
	/**
	 * 新增日程
	 * @param schedule leaderId
	 * @return
	 */
	public String add(ScheduleTemp schedule,int leaderId){
		//时间存在冲突的日程数
		int count = this.scheduleTempDao.countByStartOrEnd(schedule.getStartTime(),schedule.getEndTime(),leaderId);
		if(count>0){//时间冲突
			return "1";
		}
		try{
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleTempDao.save(schedule);
			ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
			sl.setLeaderId(leaderId);
			sl.setScheduleId(scheduleId);
			sl.setDelFlag("0");
			this.scheduleLeaderTempDao.save(sl);
			return "0";
		}catch(Exception e){
			return "2";
		}
	}
	/**
	 * 新增日程(副总添加，对应多个领导)
	 * @param schedule leaderId
	 * @return
	 */
	public String add(ScheduleTemp schedule,String[] leaders){
		if(null == leaders){
			return "2";
		}
		//时间存在冲突的日程数
		for (String leaderId : leaders) {//判断日程分配的时间是否存在冲突
			int count = this.scheduleTempDao.countByStartOrEnd(schedule.getStartTime(),schedule.getEndTime(),Integer.parseInt(leaderId));
			if(count>0){
				return "1";
			}
		}
		try{
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleTempDao.save(schedule);
			for (String leaderId : leaders) {//日程对应领导不为空时，添加对应领导
				ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
				sl.setScheduleId(scheduleId);
				sl.setLeaderId(Integer.parseInt(leaderId));
				sl.setDelFlag("0");
				this.scheduleLeaderTempDao.save(sl);
			}
			return "0";
		}catch(Exception e){
			return "2";
		}
	}
	/**
	 * 拖拽或者下拉日程导致日程时间变更时
	 * 如果与其他日程时间存在冲突，则覆盖该日程，并将该日程存为待办任务
	 * @param schedule leaderId
	 * @return
	 */
	public String dragScheduleTemp(ScheduleTemp schedule,int leaderId){
		try{
			//时间存在冲突的日程数
			List<Map<String,Object>> list = this.scheduleTempDao.getExpectScheduleTemps
					(schedule.getStartTime(),schedule.getEndTime(),schedule.getScheduleId(),leaderId);
			String titles = "";
			if(null != list){
				for (Map<String, Object> map : list) {
					this.scheduleTempDao.updateToToDo(map.get("schedule_id").toString());
					titles +=map.get("title").toString().trim()+",";
				}
			}
			if(titles.length()>=1){
				titles = titles.substring(0, titles.length()-1);
			}
			schedule.setDelFlag("0");
			this.scheduleTempDao.update(schedule);
			return titles;
		}catch(Exception e){
			return "1";
		}
	}
	/**
	 * 修改日程
	 * @param schedule
	 * @param leaderId
	 * @return
	 */
	public String update(ScheduleTemp schedule,int leaderId){
		//时间存在冲突的日程数
		int count = this.scheduleTempDao.countByStartOrEndExceptOwn(schedule.getStartTime(),schedule.getEndTime(),schedule.getScheduleId(),leaderId);
		if(count>0){//时间冲突
			return "1";
		}
		try{
			schedule.setDelFlag("0");
			this.scheduleTempDao.update(schedule);
			return "0";
		}catch(Exception e){
			return "2";
		}
	}
	/**
	 * 新增待办任务
	 * @param schedule leaderId
	 * @return
	 */
	public String addToDo(ScheduleTemp schedule,int leaderId){
		try{
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleTempDao.save(schedule);
			ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
			sl.setLeaderId(leaderId);
			sl.setScheduleId(scheduleId);
			sl.setDelFlag("0");
			this.scheduleLeaderTempDao.save(sl);
			return "0";
		}catch(Exception e){
			return "1";
		}
	}
	/**
	 * 秘书加载对应领导的所有日程
	 * @param leaderId
	 * @return
	 */
	public List<Map<String,Object>> getScheduleTempByLeader(int leaderId){
		return this.scheduleTempDao.getScheduleTempByLeader(leaderId);
	}



	/**
	 * 发布日程
	 * @param userId
	 */
	public void updateScheduleTempToRelease(String userId,String weekId){
		this.scheduleTempDao.updateScheduleTempToRelease(userId,weekId);
	}
	/**
	 * 无操作发布日程
	 * @param userId
	 */
	public void noOperateRelease(String userId,String weekId){
		this.scheduleTempDao.noOperateRelease(userId,weekId);
	}
	/**
	 * 秘书提交日程
	 * @param userId
	 */
	public void updateScheduleTempToCommit(String userId,String weekId){
		this.scheduleTempDao.updateScheduleTempToCommit(userId,weekId);
	}
	/**
	 * 获取待审核的领导日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getSubmitScheduleTemp(String weekId){
		return this.scheduleTempDao.getSubmitScheduleTemp(weekId);
	}
	/**
	 * 获取本周日程
	 * @param weekId
	 * @return
	 */
	public List<Map<String,Object>> getThisWeekScheduleTemp(String weekId){
		return this.scheduleTempDao.getThisWeekScheduleTemp(weekId);
	}
	/**
	 * 获取所有领导
	 * @return
	 */
	public List<Map<String,Object>> getAllLeaders(){
		return this.scheduleTempDao.getAllLeaders();
	}
	/**
	 * 存在时间冲突的日程数（除去自己，更新日程时用）
	 * @return
	 */
	public int countByStartOrEndExceptOwn(Date start,Date end,String scheduleId,int leaderId){
		return this.scheduleTempDao.countByStartOrEndExceptOwn(start, end, scheduleId, leaderId);
	}

	/**
	 * 拖拽到某个领导区域后
	 * 如果与其他日程时间存在冲突，则覆盖该日程，并将该日程存为待办任务
	 * @param schedule leaderId
	 * @return
	 */
	public Map<String,String> dragCopy(ScheduleTemp schedule,int leaderId){
		Map<String,String> retMap = new HashMap<String,String>();
		try{
			//时间存在冲突的日程数
			List<Map<String,Object>> list = this.scheduleTempDao.getExpectScheduleTempsByLeader
					(schedule.getStartTime(),schedule.getEndTime(),leaderId);
			String titles = "";
			String confictIds="";
			if(null != list){
				for (Map<String, Object> map : list) {
					String scheduleId = map.get("schedule_id").toString();
					List<ScheduleLeaderTemp> flagList = this.scheduleLeaderTempDao.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{scheduleId,"0"});
					if(null != flagList&&flagList.size()>1){//多用户日程
						this.scheduleLeaderTempDao.delByScheduleLeaderTemp(scheduleId,leaderId);
					}else{
						//this.scheduleTempDao.updateToToDo(scheduleId);
						titles +=map.get("title").toString().trim()+",";
						confictIds+=scheduleId+",";
					}
				}
			}
			if(titles.length()>=1){
				titles = titles.substring(0, titles.length()-1);
				confictIds = confictIds.substring(0, confictIds.length()-1);
				retMap.put("result", "2");
				retMap.put("titles", titles);
				retMap.put("confictIds", confictIds);
				return retMap;
			}
			ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
			sl.setScheduleId(schedule.getScheduleId());
			sl.setLeaderId(leaderId);
			sl.setDelFlag("0");
			this.scheduleLeaderTempDao.save(sl);
			retMap.put("result", "0");
			return retMap;
		}catch(Exception e){
			retMap.put("result", "1");
			return retMap;
		}
	}
	/**
	 * 判断是否存在重复，并将结束时间设置为重复事件的前15分钟
	 * 覆盖当前事件
	 * @param schedule
	 * @param dsc
	 * @param leaderId
	 * @return
	 * @throws ParseException
	 */
	public String dragCover(ScheduleTemp schedule,ScheduleTemp dsc,int leaderId){
		try{
			String titles = "";
			dateHandle(schedule, leaderId);
			//对于多用户日程，拖拽时，如果其他领导的日程存在冲突，则提示冲突，不存待办
			List<ScheduleLeaderTemp> selfList = this.scheduleLeaderTempDao.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{schedule.getScheduleId(),"0"});
			if(null != selfList&&selfList.size()>1){//拖拽事件多用户日程
				for (ScheduleLeaderTemp scheduleLeader : selfList) {
					int flagLeaderId = scheduleLeader.getLeaderId();
					if(flagLeaderId != leaderId){
						int count = this.scheduleTempDao.countByStartOrEndExceptOwn(schedule.getStartTime(), schedule.getEndTime(), schedule.getScheduleId(), flagLeaderId);
						if(count>0){
							return "2";
						}
					}
				}
			}
			titles = conflictHandle(schedule, dsc, leaderId, titles);
			return titles;
		}catch(Exception e){
			return "1";
		}
	}

	private String conflictHandle(ScheduleTemp schedule, ScheduleTemp dsc,
			int leaderId, String titles) {
		if(null != dsc){
			List<ScheduleLeaderTemp> flagList = this.scheduleLeaderTempDao.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{dsc.getScheduleId(),"0"});
			if(null != flagList&&flagList.size()>1){//拖拽到的事件为多用户日程，则删除当前用户的关联即可
				this.scheduleLeaderTempDao.delByScheduleLeaderTemp(dsc.getScheduleId(),leaderId);
			}else{//否则将日程转待办
				this.scheduleTempDao.updateToToDo(dsc.getScheduleId());
				titles =dsc.getTitle();
			}
		}
		schedule.setDelFlag("0");
		this.scheduleTempDao.update(schedule);
		return titles;
	}

	private void dateHandle(ScheduleTemp schedule, int leaderId)
			throws ParseException {
		//时间存在冲突的日程数
		List<Map<String,Object>> list = this.scheduleTempDao.getExpectScheduleTemps
				(schedule.getStartTime(),schedule.getEndTime(),schedule.getScheduleId(),leaderId);
		if(null != list && list.size()>1){//按照持续时间计算后的结束时间与其他事件存在冲突，则调整结束时间为其他事件的前15分钟
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = sf.parse(list.get(1).get("start_time").toString());
			Date endTime = new Date(startTime.getTime()-15*60*1000);
			schedule.setEndTime(endTime);
		}
	}
	/**
	 * 跨天日程拖拽时覆盖所有有冲突的日程
	 * @param schedule
	 * @param dsc
	 * @param leaderId
	 * @return
	 */
	public String dragCrossDayCover(ScheduleTemp schedule,int leaderId){
		try{
			String titles = "";
			//对于多用户日程，拖拽时，如果其他领导的日程存在冲突，则提示冲突，不存待办
			List<ScheduleLeaderTemp> selfList = this.scheduleLeaderTempDao.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{schedule.getScheduleId(),"0"});
			if(null != selfList&&selfList.size()>1){//拖拽事件多用户日程
				for (ScheduleLeaderTemp scheduleLeader : selfList) {
					int flagLeaderId = scheduleLeader.getLeaderId();
					if(flagLeaderId != leaderId){
						int count = this.scheduleTempDao.countByStartOrEndExceptOwn(schedule.getStartTime(), schedule.getEndTime(), schedule.getScheduleId(), flagLeaderId);
						if(count>0){
							return "2";
						}
					}
				}
			}
			titles = handle(schedule, leaderId, titles);
			return titles;
		}catch(Exception e){
			return "1";
		}
	}

	private String handle(ScheduleTemp schedule, int leaderId, String titles) {
		//时间存在冲突的日程数
		List<Map<String,Object>> list = this.scheduleTempDao.getExpectScheduleTemps
				(schedule.getStartTime(),schedule.getEndTime(),schedule.getScheduleId(),leaderId);
		if(null != list){
			for (Map<String, Object> map : list) {
				List<ScheduleLeaderTemp> flagList = this.scheduleLeaderTempDao.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{map.get("schedule_id").toString(),"0"});
				if(null != flagList&&flagList.size()>1){//拖拽到的事件为多用户日程，则删除当前用户的关联即可
					this.scheduleLeaderTempDao.delByScheduleLeaderTemp(map.get("schedule_id").toString(),leaderId);
				}else{//否则将日程转待办
					this.scheduleTempDao.updateToToDo(map.get("schedule_id").toString());
					titles +=map.get("title").toString().trim()+",";
				}
			}
		}
		if(titles.length()>=1){
			titles = titles.substring(0, titles.length()-1);
		}
		schedule.setDelFlag("0");
		this.scheduleTempDao.update(schedule);
		return titles;
	}
	/**
	 * 存为待办
	 * @param scheduleId
	 */
	public void updateToToDo(String scheduleId,int leaderId,String[] confictIds){
		for (String confictId : confictIds) {
			this.scheduleTempDao.updateToToDo(confictId);
		}
		ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
		sl.setScheduleId(scheduleId);
		sl.setLeaderId(leaderId);
		sl.setDelFlag("0");
		this.scheduleLeaderTempDao.save(sl);
	}
	/**
	 * 初始化临时表的数据
	 * @param weekId
	 */
	public boolean initTempData(String userId,Week week){
		return this.scheduleTempDao.initTempData(userId,week);
	}
	/**
	 * 删除临时表，修改操作人为null
	 * @param userId
	 */
	public void clearTemp(String userId){
		this.scheduleTempDao.clearTemp(userId);
	}
	/**
	 * seesion过期后删除临时表
	 * @param userId
	 */
	public void sessionDestroyClearTemp(String userId){
		this.scheduleTempDao.clearTemp(userId);
	}
	/**
	 * 判断是否有改动
	 * @param userId
	 * @return
	 */
	public boolean checkModify(String userId){
		return this.scheduleTempDao.checkModify(userId);
	}
	/**
	 * 添加已修改标记
	 * @param userId
	 */
	public void addChangeFlag(String userId){
		this.scheduleTempDao.addChangeFlag(userId);
	}
	public void delSchedule(String id) {
		this.scheduleTempDao.delSchedule(id);
	}
}
