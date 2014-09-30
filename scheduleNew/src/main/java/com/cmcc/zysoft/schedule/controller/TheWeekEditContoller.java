package com.cmcc.zysoft.schedule.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cmcc.zysoft.push.MqttPush;
import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.schedule.model.Leader;
import com.cmcc.zysoft.schedule.model.ScheduleLeader;
import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
import com.cmcc.zysoft.schedule.model.ScheduleTemp;
import com.cmcc.zysoft.schedule.model.SystemUser;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleLeaderTempService;
import com.cmcc.zysoft.schedule.service.ScheduleTempService;
import com.cmcc.zysoft.schedule.service.SystemUserPCService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.Des;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;
/**
 * 本周日程的修改控制层
 * 如果本周日程进入编辑状态，则将本周日程中的数据暂存临时表
 * 编辑状态下的操作都是对临时表的操作
 * 点击发布后则将临时表中的数据同步到本周日程中
 * 编辑状态中间跳出，则视为放弃编辑，清空临时表，还原本来数据
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/theweekedit")
public class TheWeekEditContoller extends BaseController {
	@Resource
	private ScheduleTempService scheduleTempService;
	@Resource
	private ScheduleLeaderTempService scheduleLeaderTempService;
	@Resource
	private WeekService weekService;
	@Resource
	private LeaderService leaderService;
	@Resource
	private SystemUserPCService userService;
	private static final String key = "0002000200020002";
	/**
	 * 获取临时表中的本周日程
	 * 包括提交和已发布状态下的日程
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getThisWeekSchedule.htm")
	@ResponseBody
	public List<Map<String, Object>> getThisWeekSchedule(
			HttpServletRequest request) {
		//获取session中的本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		//或取临时表中的本周日程
		return this.scheduleTempService.getThisWeekScheduleTemp(week
				.getWeekId());
	}

	/**
	 * 到编辑状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/toEditState.htm")
	@ResponseBody
	public String toEditState(HttpServletRequest request) {
		//获取当前操作用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		//初始化本周日程临时表的数据，返回是否有人正在操作
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
		return "SUCCESS";
	}

	/**
	 * 审核人员编辑日程
	 * 
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param leaders
	 * @return
	 */
	@RequestMapping(value = "/editSchedule.htm")
	@ResponseBody
	public String editSchedule(HttpServletRequest request,
			ScheduleTemp schedule, String startTime, String endTime,
			String[] leaders) {
		try {
			//获取当前操作用户
			User user = SecurityContextUtil.getCurrentUser();
			String scheduleId = schedule.getScheduleId();
			ScheduleTemp s = this.scheduleTempService.getEntity(scheduleId);
			String type = s.getType();
			// 日程主键为空，返回错误
			if (null == scheduleId) {
				return "2";
			}
			// 先删除领导日程关系表，再添加当前对应领导
			this.scheduleLeaderTempService.delBySchedule(scheduleId);
			// 日程对应领导为空时，删除改日程本身，并返回成功
			if (null == leaders) {
				del(request, scheduleId, type);
				return "0";
			}
			// 日程对应领导不为空时，添加对应领导
			for (String str : leaders) {
				ScheduleLeaderTemp sl = new ScheduleLeaderTemp();
				sl.setScheduleId(scheduleId);
				sl.setLeaderId(Integer.parseInt(str));
				sl.setDelFlag("0");
				this.scheduleLeaderTempService.insertEntity(sl);
			}
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = null;
			Date end = null;
			try {
				start = sf.parse(startTime);
				end = sf.parse(endTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 判断日程分配的时间是否存在冲突
			for (String str : leaders) {
				int count = this.scheduleTempService
						.countByStartOrEndExceptOwn(start, end, scheduleId,
								Integer.parseInt(str));
				if (count > 0) {
					return "1";
				}
			}
			// 日程存在时，更新该日程
			if (null != s) {
				s.setTitle(schedule.getTitle());
				s.setStartTime(start);
				s.setEndTime(end);
				s.setMen(schedule.getMen());
				s.setPlace(schedule.getPlace());
				s.setDetails(schedule.getDetails());
				s.setRemark(schedule.getRemark());
				s.setModifyUser(user.getUserId());
				s.setModifyTime(new Date());
				this.scheduleTempService.updateEntity(s);
			}
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "2";
		}
	}

	/**
	 * 删除该日程
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value = "/del.htm")
	@ResponseBody
	public String del(HttpServletRequest request, String scheduleId, String type) {
		try {
			this.scheduleLeaderTempService.delBySchedule(scheduleId);
			this.scheduleTempService.delSchedule(scheduleId);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * 副总新建日程时，直接为提交状态 需考虑，对应多个领导的情况
	 * 
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @param leaders
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveEasy.htm")
	@ResponseBody
	public String saveEasy(String title, String startTime, String endTime,
			String[] leaders, HttpServletRequest request) {
		User user = SecurityContextUtil.getCurrentUser();
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		ScheduleTemp schedule = new ScheduleTemp();
		schedule.setTitle(title);
		schedule.setWeekId(week.getWeekId());
		try {
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setType("0");// 正常日程
		schedule.setCreateUserType("0");// 非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		return this.scheduleTempService.add(schedule, leaders);
	}

	/**
	 * 审核日程模块创建完整日程，直接为提交状态 需考虑，对应多个领导的情况
	 * 
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveDetail.htm")
	@ResponseBody
	public String saveDetail(ScheduleTemp schedule, String startTime,
			String endTime, String[] leaders, HttpServletRequest request) {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		schedule.setWeekId(week.getWeekId());
		try {
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setType("0");// 正常日程
		schedule.setCreateUserType("0");// 非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		return this.scheduleTempService.add(schedule, leaders);
	}

	/**
	 * 发布本周日程
	 * 
	 * @return
	 */
	@RequestMapping(value = "/releaseSchedule.htm")
	@ResponseBody
	public String releaseSchedule(HttpServletRequest request) {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		try {
			//发布日程
			this.scheduleTempService.updateScheduleTempToRelease(
					user.getUserId(), week.getWeekId());
			//发布状态
			week.setReleaseTag("1");
			week.setReleaseTime(new Date());
			week.setMark1(null);
			week.setMark2(null);// 取消编辑状态
			this.weekService.updateEntity(week);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 无操作发布日程
	 * @param userId
	 */
	@RequestMapping(value = "/noOperateRelease.htm")
	@ResponseBody
	public String noOperateRelease(HttpServletRequest request){
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		try {
			//发布日程
			this.scheduleTempService.noOperateRelease(
					user.getUserId(), week.getWeekId());
			//发布状态
			week.setReleaseTag("1");
			week.setReleaseTime(new Date());
			week.setMark1(null);
			week.setMark2(null);// 取消编辑状态
			this.weekService.updateEntity(week);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 提交本周日程
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commitSchedule.htm")
	@ResponseBody
	public String commitSchedule(HttpServletRequest request) {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取本周实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		try {
			//发布日程
			this.scheduleTempService.updateScheduleTempToCommit(
					user.getUserId(), week.getWeekId());
			//未发布状态
			week.setReleaseTag("0");
			week.setReleaseTime(new Date());
			week.setMark1(null);
			week.setMark2(null);// 取消编辑状态
			this.weekService.updateEntity(week);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 日程复制
	 * @param request
	 * @param scheduleId
	 * @param leaderId
	 * @return
	 */
	@RequestMapping(value = "/dragCopy.htm")
	@ResponseBody
	public Map<String,String> dragCopy(HttpServletRequest request, String scheduleId,
			String leaderId) {
		Map<String,String> retMap = new HashMap<String,String>();
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
		//获取该领导是否存在该日程，如果存在则返回“”，否则添加日程到该领导
		List<ScheduleLeaderTemp> list = this.scheduleLeaderTempService
				.findByNamedParam(new String[] { "scheduleId", "leaderId" ,"delFlag"},
						new Object[] { scheduleId, Integer.parseInt(leaderId),"0" });
		if (null != list && list.size() > 0) {
			retMap.put("result", "3");
			return retMap;
		}
		return this.scheduleTempService.dragCopy(schedule,
				Integer.parseInt(leaderId));
	}

	/**
	 * 水平移动时，如果有冲突，则覆盖
	 * 
	 * @param sId
	 *            原事件id
	 * @param dId
	 *            被覆盖的事件id
	 * @return
	 */
	@RequestMapping(value = "/dragCover.htm")
	@ResponseBody
	public String dragCover(HttpServletRequest request, String sId, String dId,
			String leaderId) {
		//根据ID获取日程(原事件)
		ScheduleTemp schedule = this.scheduleTempService.getEntity(sId);
		Date start = schedule.getStartTime();
		Date end = schedule.getEndTime();
		Calendar c1 = Calendar.getInstance();
		c1.setTime(start);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(end);
		long s = start.getTime();
		long e = end.getTime();
		//获取被覆盖的日程
		ScheduleTemp dSc = this.scheduleTempService.getEntity(dId);
		//日程的起始时间设置为被覆盖的日程的起始时间
		schedule.setStartTime(dSc.getStartTime());
		long news = dSc.getStartTime().getTime();
		//日程的结束时间为日程本来的所需时长+被覆盖的日程的起始时间
		schedule.setEndTime(new Date(news + (e - s)));
		//日程不跨天
		if (c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
			return this.scheduleTempService.dragCover(schedule, dSc,
					Integer.parseInt(leaderId));
		} else {//日程跨天
			return this.scheduleTempService.dragCrossDayCover(schedule,
					Integer.parseInt(leaderId));
		}
	}

	/**
	 * 水平移动时，如果有冲突，则覆盖
	 * 
	 * @param sId
	 *            原事件id
	 * @param dId
	 *            被覆盖的事件id
	 * @return
	 */
	@RequestMapping(value = "/dragUpdate.htm")
	@ResponseBody
	public String dragUpdate(HttpServletRequest request, String scheduleId,
			String start, String leaderId) {
		//根据主键获取相应日程
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
		Date startTime = schedule.getStartTime();
		Date endTime = schedule.getEndTime();
		//获取日程的起始与结束时间
		Calendar c1 = Calendar.getInstance();
		c1.setTime(startTime);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(endTime);
		long s = startTime.getTime();
		long e = endTime.getTime();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			//将日程的启示时间设置为拖动后的起始时间
			schedule.setStartTime(sf.parse(start));
			long news = sf.parse(start).getTime();
			//日程的结束时间为日程所需时间+拖动后的起始时间
			schedule.setEndTime(new Date(news + (e - s)));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		//日程不跨天
		if (c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
			return this.scheduleTempService.dragCover(schedule, null,
					Integer.parseInt(leaderId));
		} else {//日程跨天
			return this.scheduleTempService.dragCrossDayCover(schedule,
					Integer.parseInt(leaderId));
		}
	}

	/**
	 * 获取日程详情
	 * 
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value = "/getScheduleById.htm")
	@ResponseBody
	public ScheduleTemp getScheduleById(String scheduleId) {
		//根据主键获取日程
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
		//获取日程关联的领导集合
		List<Map<String, Object>> list = this.scheduleLeaderTempService
				.getBySchedule(scheduleId);
		String leaders = "";
		String leaderIds = "";
		//使用循环拼接领导ID与名称的字符串，供客户端直接显示
		for (int i = 0; i < list.size(); i++) {
			String leaderName = list.get(i).get("leader_name").toString();
			String leaderId = list.get(i).get("leader_id").toString();
			if (i == list.size() - 1) {
				leaders += leaderName;
				leaderIds += leaderId;
			} else {
				leaders += leaderName + ",";
				leaderIds += leaderId + ",";
			}
		}
		schedule.setMark1(leaders);// 暂存领导名称
		schedule.setMark2(leaderIds);// 暂存领导id，便于客户端获取
		return schedule;
	}
	/**
	 * 判断是否多用户日程
	 * @return
	 */
	@RequestMapping(value="/checkIsManyLeaderEvent.htm") 
	@ResponseBody
	public boolean checkIsManyLeaderEvent(String scheduleId){
		List<ScheduleLeaderTemp> flagList = this.scheduleLeaderTempService.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{scheduleId,"0"});
		if(null != flagList&&flagList.size()>1){//多用户日程
			return true;
		}
		return false;
	}
	/**
	 * 领导不参加
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value="/delLeaderFromEvent.htm")
	@ResponseBody
	public String delLeaderFromEvent(String scheduleId){
		try{
			User user = SecurityContextUtil.getCurrentUser();
			this.scheduleLeaderTempService.delByScheduleLeaderTemp(scheduleId,user.getLeader().getLeaderId());
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 推送消息
	 * 
	 * @param request
	 */
	@RequestMapping(value = "/push.htm")
	@ResponseBody
	public void push(HttpServletRequest request) {
		//获取本周日程的实例
		Week week = (Week) request.getSession().getAttribute("thisWeek");
		Date date = week.getWeekStart();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String monday = df.format(date);
		//构建推送内容
		JSONObject obj = new JSONObject();
		obj.put("time", monday);
		obj.put("content", "本周日程已更新");
		//获取所有领导
		List<Leader> leaders = this.leaderService.loadEntities();
		//获取所有用户
		List<SystemUser> users = this.userService.loadEntities();
		List<String> list = new ArrayList<String>();  
		for (int i = 0; i < leaders.size(); i++) {
			String mobile = Des.Encrypt(leaders.get(i).getMobile(), Des.hex2byte(key));
			if(!list.contains(mobile)){
				list.add(mobile);
			}
		}
		for (int i = 0; i < users.size(); i++) {
			String mobile = Des.Encrypt(users.get(i).getMobile(), Des.hex2byte(key));
			if(!list.contains(mobile)){
				list.add(mobile);
			}
		}
		String[] mobiles = list.toArray(new String[list.size()]);
		//调用推送接口
		MqttPush.sendMultiTopic(mobiles, obj.toJSONString());
	}
}
