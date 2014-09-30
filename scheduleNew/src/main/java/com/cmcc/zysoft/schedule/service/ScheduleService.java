// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.ScheduleDao;
import com.cmcc.zysoft.schedule.dao.ScheduleLeaderDao;
import com.cmcc.zysoft.schedule.model.Schedule;
import com.cmcc.zysoft.schedule.model.ScheduleLeader;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class ScheduleService extends BaseServiceImpl<Schedule, String> {

	/**
	 * 属性名称：ScheduleDao 类型：ScheduleDao
	 */
	@Resource
	private ScheduleDao scheduleDao;
	@Resource
	private ScheduleLeaderDao scheduleLeaderDao;

	@Override
	public HibernateBaseDao<Schedule, String> getHibernateBaseDao() {
		return scheduleDao;
	}

	/**
	 * 新增日程
	 * 
	 * @param schedule
	 *            leaderId
	 * @return
	 */
	public String add(Schedule schedule, int leaderId) {
		// 时间存在冲突的日程数
		int count = this.scheduleDao.countByStartOrEnd(schedule.getStartTime(),
				schedule.getEndTime(), leaderId);
		if (count > 0) {// 时间冲突
			return "1";
		}
		try {
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleDao.save(schedule);
			ScheduleLeader sl = new ScheduleLeader();
			sl.setLeaderId(leaderId);
			sl.setScheduleId(scheduleId);
			sl.setDelFlag("0");
			this.scheduleLeaderDao.save(sl);
			return "0";
		} catch (Exception e) {
			return "2";
		}
	}

	/**
	 * 新增日程(副总添加，对应多个领导)
	 * 
	 * @param schedule
	 *            leaderId
	 * @return
	 */
	public String add(Schedule schedule, String[] leaders) {
		if (null == leaders) {
			return "2";
		}
		// 时间存在冲突的日程数
		for (String leaderId : leaders) {// 判断日程分配的时间是否存在冲突
			int count = this.scheduleDao.countByStartOrEnd(
					schedule.getStartTime(), schedule.getEndTime(),
					Integer.parseInt(leaderId));
			if (count > 0) {
				return "1";
			}
		}
		try {
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleDao.save(schedule);
			for (String leaderId : leaders) {// 日程对应领导不为空时，添加对应领导
				ScheduleLeader sl = new ScheduleLeader();
				sl.setScheduleId(scheduleId);
				sl.setLeaderId(Integer.parseInt(leaderId));
				sl.setDelFlag("0");
				this.scheduleLeaderDao.save(sl);
			}
			return "0";
		} catch (Exception e) {
			return "2";
		}
	}

	/**
	 * 拖拽或者下拉日程导致日程时间变更时 如果与其他日程时间存在冲突，则覆盖该日程，并将该日程存为待办任务
	 * 
	 * @param schedule
	 *            leaderId
	 * @return
	 */
	public String dragSchedule(Schedule schedule, int leaderId) {
		try {
			// 时间存在冲突的日程数
			List<Map<String, Object>> list = this.scheduleDao
					.getExpectSchedules(schedule.getStartTime(),
							schedule.getEndTime(), schedule.getScheduleId(),
							leaderId);
			String titles = "";
			if (null != list) {
				for (Map<String, Object> map : list) {
					this.scheduleDao.updateToToDo(map.get("schedule_id")
							.toString());
					titles += map.get("title").toString().trim() + ",";
				}
			}
			if (titles.length() >= 1) {
				titles = titles.substring(0, titles.length() - 1);
			}
			schedule.setDelFlag("0");
			this.scheduleDao.update(schedule);
			return titles;
		} catch (Exception e) {
			return "1";
		}
	}

	/**
	 * 修改日程
	 * 
	 * @param schedule
	 * @param leaderId
	 * @return
	 */
	public String update(Schedule schedule, int leaderId) {
		// 时间存在冲突的日程数
		int count = this.scheduleDao.countByStartOrEndExceptOwn(
				schedule.getStartTime(), schedule.getEndTime(),
				schedule.getScheduleId(), leaderId);
		if (count > 0) {// 时间冲突
			return "1";
		}
		try {
			schedule.setDelFlag("0");
			this.scheduleDao.update(schedule);
			return "0";
		} catch (Exception e) {
			return "2";
		}
	}

	/**
	 * 新增待办任务
	 * 
	 * @param schedule
	 *            leaderId
	 * @return
	 */
	public String addToDo(Schedule schedule, int leaderId) {
		try {
			schedule.setDelFlag("0");
			String scheduleId = this.scheduleDao.save(schedule);
			ScheduleLeader sl = new ScheduleLeader();
			sl.setLeaderId(leaderId);
			sl.setScheduleId(scheduleId);
			sl.setDelFlag("0");
			this.scheduleLeaderDao.save(sl);
			return "0";
		} catch (Exception e) {
			return "1";
		}
	}

	/**
	 * 秘书加载对应领导的所有日程
	 * 
	 * @param leaderId
	 * @return
	 */
	public List<Map<String, Object>> getScheduleByLeader(int leaderId,Date dateStart,Date dateEnd) {
		return this.scheduleDao.getScheduleByLeader(leaderId,dateStart,dateEnd);
	}

	/**
	 * 将对应领导日程修改为提交状态
	 * 
	 * @param userId
	 * @param leaderId
	 */
	public void updateScheduleToSubmit(String userId, int leaderId,String weekId) {
		this.scheduleDao.updateScheduleToSubmit(userId, leaderId,weekId);
	}


	/**
	 * 获取所有领导的待办任务
	 * 
	 * @param leaderId
	 * @return
	 */
	public List<Map<String, Object>> getToDoList(int leaderId) {
		return this.scheduleDao.getToDoList(leaderId);
	}


	/**
	 * 获取待审核的领导日程
	 * 
	 * @param weekId
	 * @return
	 */
	public List<Map<String, Object>> getSubmitSchedule(String weekId) {
		return this.scheduleDao.getSubmitSchedule(weekId);
	}

	/**
	 * 获取本周日程
	 * 
	 * @param weekId
	 * @return
	 */
	public List<Map<String, Object>> getThisWeekSchedule(String weekId) {
		return this.scheduleDao.getThisWeekSchedule(weekId);
	}

	/**
	 * 获取所有领导
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getAllLeaders() {
		return this.scheduleDao.getAllLeaders();
	}

	/**
	 * 存在时间冲突的日程数（除去自己，更新日程时用）
	 * 
	 * @return
	 */
	public int countByStartOrEndExceptOwn(Date start, Date end,
			String scheduleId, int leaderId) {
		return this.scheduleDao.countByStartOrEndExceptOwn(start, end,
				scheduleId, leaderId);
	}


	/**
	 * 拖拽到某个领导区域后 如果与其他日程时间存在冲突，则覆盖该日程，并将该日程存为待办任务
	 * 
	 * @param schedule
	 *            leaderId
	 * @return
	 */
	public String dragCopy(Schedule schedule, int leaderId) {
		try {
			// 时间存在冲突的日程数
			List<Map<String, Object>> list = this.scheduleDao
					.getExpectSchedulesByLeader(schedule.getStartTime(),
							schedule.getEndTime(), leaderId);
			String titles = "";
			if (null != list) {
				for (Map<String, Object> map : list) {
					String scheduleId = map.get("schedule_id").toString();
					List<ScheduleLeader> flagList = this.scheduleLeaderDao
							.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{scheduleId,"0"});
					if (null != flagList && flagList.size() > 1) {// 多用户日程
						this.scheduleLeaderDao.delByScheduleLeader(scheduleId,
								leaderId);
					} else {
						this.scheduleDao.updateToToDo(scheduleId);
						titles += map.get("title").toString().trim() + ",";
					}
				}
			}
			if (titles.length() >= 1) {
				titles = titles.substring(0, titles.length() - 1);
			}
			ScheduleLeader sl = new ScheduleLeader();
			sl.setScheduleId(schedule.getScheduleId());
			sl.setLeaderId(leaderId);
			sl.setDelFlag("0");
			this.scheduleLeaderDao.save(sl);
			return titles;
		} catch (Exception e) {
			return "1";
		}
	}

	/**
	 * 判断是否存在重复，并将结束时间设置为重复事件的前15分钟 覆盖当前事件
	 * 
	 * @param schedule
	 * @param dsc
	 * @param leaderId
	 * @return
	 * @throws ParseException
	 */
	public String dragCover(Schedule schedule, Schedule dsc, int leaderId) {
		try {
			String titles = "";
			// 时间存在冲突的日程数
			List<Map<String, Object>> list = this.scheduleDao
					.getExpectSchedules(schedule.getStartTime(),
							schedule.getEndTime(), schedule.getScheduleId(),
							leaderId);
			dateHandle(schedule, list);
			// 对于多用户日程，拖拽时，如果其他领导的日程存在冲突，则提示冲突，不存待办
			List<ScheduleLeader> selfList = this.scheduleLeaderDao
					.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{schedule.getScheduleId(),"0"});
			if (null != selfList && selfList.size() > 1) {// 拖拽事件多用户日程
				for (ScheduleLeader scheduleLeader : selfList) {
					int flagLeaderId = scheduleLeader.getLeaderId();
					if (flagLeaderId != leaderId) {
						int count = this.scheduleDao
								.countByStartOrEndExceptOwn(
										schedule.getStartTime(),
										schedule.getEndTime(),
										schedule.getScheduleId(), flagLeaderId);
						if (count > 0) {
							return "2";
						}
					}
				}
			}
			titles = descHandle(dsc, leaderId, titles);
			schedule.setDelFlag("0");
			this.scheduleDao.update(schedule);
			return titles;
		} catch (Exception e) {
			return "1";
		}
	}

	private void dateHandle(Schedule schedule, List<Map<String, Object>> list)
			throws ParseException {
		if (null != list && list.size() > 0) {// 按照持续时间计算后的结束时间与其他事件存在冲突，则调整结束时间为其他事件的前15分钟
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = sf.parse(list.get(0).get("start_time").toString());
			Date endTime = new Date(startTime.getTime() - 15 * 60 * 1000);
			schedule.setEndTime(endTime);
		}
	}

	private String descHandle(Schedule dsc, int leaderId, String titles) {
		if (null != dsc) {
			List<ScheduleLeader> flagList = this.scheduleLeaderDao
					.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{dsc.getScheduleId(),"0"});
			if (null != flagList && flagList.size() > 1) {// 拖拽到的事件为多用户日程，则删除当前用户的关联即可
				this.scheduleLeaderDao.delByScheduleLeader(dsc.getScheduleId(),
						leaderId);
			} else {// 否则将日程转待办
				this.scheduleDao.updateToToDo(dsc.getScheduleId());
				titles = dsc.getTitle();
			}
		}
		return titles;
	}

	/**
	 * 跨天日程拖拽时覆盖所有有冲突的日程
	 * 
	 * @param schedule
	 * @param dsc
	 * @param leaderId
	 * @return
	 */
	public String dragCrossDayCover(Schedule schedule, int leaderId) {
		try {
			String titles = "";
			// 对于多用户日程，拖拽时，如果其他领导的日程存在冲突，则提示冲突，不存待办
			List<ScheduleLeader> selfList = this.scheduleLeaderDao
					.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{schedule.getScheduleId(),"0"});
			if (null != selfList && selfList.size() > 1) {// 拖拽事件多用户日程
				for (ScheduleLeader scheduleLeader : selfList) {
					int flagLeaderId = scheduleLeader.getLeaderId();
					if (flagLeaderId != leaderId) {
						int count = this.scheduleDao
								.countByStartOrEndExceptOwn(
										schedule.getStartTime(),
										schedule.getEndTime(),
										schedule.getScheduleId(), flagLeaderId);
						if (count > 0) {
							return "2";
						}
					}
				}
			}
			titles = conflictHandle(schedule, leaderId, titles);
			this.scheduleDao.update(schedule);
			return titles;
		} catch (Exception e) {
			return "1";
		}
	}

	private String conflictHandle(Schedule schedule, int leaderId, String titles) {
		// 时间存在冲突的日程数
		List<Map<String, Object>> list = this.scheduleDao.getExpectSchedules(
				schedule.getStartTime(), schedule.getEndTime(),
				schedule.getScheduleId(), leaderId);
		if (null != list) {
			for (Map<String, Object> map : list) {
				List<ScheduleLeader> flagList = this.scheduleLeaderDao
						.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{map.get("schedule_id").toString(),"0"});
				if (null != flagList && flagList.size() > 1) {// 拖拽到的事件为多用户日程，则删除当前用户的关联即可
					this.scheduleLeaderDao.delByScheduleLeader(
							map.get("schedule_id").toString(), leaderId);
				} else {// 否则将日程转待办
					this.scheduleDao.updateToToDo(map.get("schedule_id")
							.toString());
					titles += map.get("title").toString().trim() + ",";
				}
			}
		}
		if (titles.length() >= 1) {
			titles = titles.substring(0, titles.length() - 1);
		}
		return titles;
	}
	/**
	 * 发布单个日程
	 * @param scheduleId
	 */
	public void addReleaseEvent(String scheduleId){
		this.scheduleDao.addReleaseEvent(scheduleId);
	}

	public void delSchedule(String id) {
		this.scheduleDao.delSchedule(id);
	}
}
