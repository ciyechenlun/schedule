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
import com.cmcc.zysoft.schedule.model.ScheduleTemp;
import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
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
 * 审核日程模块的控制层
 * 编辑日程后，日程被保存到临时表中
 * 当点击发布后，将临时表中的内容同步到日程表中
 * 如果编辑中间跳出，则视为放弃编辑，清空临时表，还原本来数据
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/auditmodify")
public class AuditModifyContoller extends BaseController {
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
	 * 获取临时表中的下周日程
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getSubmitSchedule.htm")
	@ResponseBody
	public List<Map<String, Object>> getSubmitSchedule(
			HttpServletRequest request) {
		//获取下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		//获取临时表中的下周日程
		return this.scheduleTempService.getSubmitScheduleTemp(week.getWeekId());
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
			//获取当前用户
			User user = SecurityContextUtil.getCurrentUser();
			//获取保存在session中的下周实例
			Week week = (Week) request.getSession().getAttribute("week");
			//初始化下周日程临时表的数据，判断是否有人正在操作
			boolean flag = this.scheduleTempService.initTempData(
					user.getUserId(), week);
			if (!flag) {
				return "LOCK";// lock表示有人正在操作
			}
			String scheduleId = schedule.getScheduleId();
			ScheduleTemp s = this.scheduleTempService.getEntity(scheduleId);
			String type = s.getType();
			if (null == scheduleId) {// 日程主键为空，返回错误
				return "2";
			}
			this.scheduleLeaderTempService.delBySchedule(scheduleId);// 先删除领导日程关系表，再添加当前对应领导
			if (null == leaders) {// 日程对应领导为空时，删除改日程本身，并返回成功
				del(request, scheduleId, type);
				return "0";
			}
			for (String str : leaders) {// 日程对应领导不为空时，添加对应领导
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
			for (String str : leaders) {// 判断日程分配的时间是否存在冲突
				int count = this.scheduleTempService
						.countByStartOrEndExceptOwn(start, end, scheduleId,
								Integer.parseInt(str));
				if (count > 0) {
					return "1";
				}
			}
			if (null != s) {// 日程存在时，更新该日程
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
	 * 删除日程 如果日程为已发布或发布后重改的状态，则更新update_type为2（删除后发布），type为4（发布后重改）
	 * 如果日程为其他状态，则直接物理删除
	 * 
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value = "/del.htm")
	@ResponseBody
	public String del(HttpServletRequest request, String scheduleId, String type) {
		try {
			//获取当前用户
			User user = SecurityContextUtil.getCurrentUser();
			//获取保存在session中的下周实例
			Week week = (Week) request.getSession().getAttribute("week");
			//初始化下周日程临时表的数据，判断是否有人正在操作
			boolean flag = this.scheduleTempService.initTempData(
					user.getUserId(), week);
			if (!flag) {
				return "LOCK";// lock表示有人正在操作
			}
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
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取保存在session中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		//初始化下周日程临时表的数据，判断是否有人正在操作
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
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
		schedule.setType("0");//正常日程
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
		//获取保存在session中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		//初始化下周日程临时表的数据，判断是否有人正在操作
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
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
	 * 发布下周日程
	 * 
	 * @return
	 */
	@RequestMapping(value = "/releaseSchedule.htm")
	@ResponseBody
	public String releaseSchedule(HttpServletRequest request) {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取保存在session中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		//初始化下周日程临时表的数据，判断是否有人正在操作
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
		try {
			this.scheduleTempService.updateScheduleTempToRelease(
					user.getUserId(), week.getWeekId());
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
	 * 复制日程存在日程冲突后
	 * @param request
	 * @param scheduleId
	 * @param leaderId
	 * @param confictIds
	 * @return
	 */
	@RequestMapping(value = "/completeCopy.htm")
	@ResponseBody
	public String completeCopy(HttpServletRequest request,String scheduleId,String leaderId, String[] confictIds) {
		try{
			this.scheduleTempService.updateToToDo(scheduleId,Integer.parseInt(leaderId),confictIds);
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 复制日程（日程移动到另一位领导相当于添加一个日程与领导的关联关系）
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
		User user = SecurityContextUtil.getCurrentUser();
		Week week = (Week) request.getSession().getAttribute("week");
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			retMap.put("result", "LOCK");
			return retMap;// lock表示有人正在操作
		}
		//查询该领导是否存在该日程，如果存在则返回“”，否则复制日程
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
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
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//获取保存在session中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		//初始化下周日程临时表的数据，判断是否有人正在操作
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
		ScheduleTemp schedule = this.scheduleTempService.getEntity(sId);
		Date start = schedule.getStartTime();
		Date end = schedule.getEndTime();
		Calendar c1 = Calendar.getInstance();
		c1.setTime(start);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(end);
		long s = start.getTime();
		long e = end.getTime();
		ScheduleTemp dSc = this.scheduleTempService.getEntity(dId);
		schedule.setStartTime(dSc.getStartTime());
		long news = dSc.getStartTime().getTime();
		schedule.setEndTime(new Date(news + (e - s)));
		//日程没有跨天
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
		User user = SecurityContextUtil.getCurrentUser();
		Week week = (Week) request.getSession().getAttribute("week");
		boolean flag = this.scheduleTempService.initTempData(user.getUserId(),
				week);
		if (!flag) {
			return "LOCK";// lock表示有人正在操作
		}
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
		Date startTime = schedule.getStartTime();
		Date endTime = schedule.getEndTime();
		Calendar c1 = Calendar.getInstance();
		c1.setTime(startTime);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(endTime);
		long s = startTime.getTime();
		long e = endTime.getTime();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			schedule.setStartTime(sf.parse(start));
			long news = sf.parse(start).getTime();
			schedule.setEndTime(new Date(news + (e - s)));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		//日程没有跨天
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
		ScheduleTemp schedule = this.scheduleTempService.getEntity(scheduleId);
		List<Map<String, Object>> list = this.scheduleLeaderTempService
				.getBySchedule(scheduleId);
		String leaders = "";
		String leaderIds = "";
		//将日程关联到的领导id与名称，拼接成字符串形式，供页面直接显示
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
	 * 推送消息
	 * 
	 * @param request
	 */
	@RequestMapping(value = "/push.htm")
	@ResponseBody
	public void push(HttpServletRequest request) {
		//获取seesion中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		Date date = week.getWeekStart();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String monday = df.format(date);
		//构建推送内容
		JSONObject obj = new JSONObject();
		obj.put("time", monday);
		obj.put("content", "下周日程已更新");
		//获取所有领导
		List<Leader> leaders = this.leaderService.loadEntities();
		//获取所有系统用户
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
		MqttPush.sendMultiTopic(mobiles, obj.toJSONString());
	}
	/**
	 * 日程修改成功后，添加修改标记
	 */
	@RequestMapping(value = "/addChangeFlag.htm")
	@ResponseBody
	public void addChangeFlag(){
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//修改week表中操作用户为有修改状态
		this.scheduleTempService.addChangeFlag(user.getUserId());
	}
	/**
	 * 注销时调用，删除临时表
	 */
	@RequestMapping(value = "/clearTemp.htm")
	@ResponseBody
	public void clearTemp(HttpServletRequest request) {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		//清空临时表
		if (null != user) {
			this.scheduleTempService
					.clearTemp(user.getUserId());
		}
	}

	/**
	 * 判断是否有改动
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/checkModify.htm")
	@ResponseBody
	public boolean checkModify() {
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		if (null != user) {
			//判断日程是否有变更
			return this.scheduleTempService.checkModify(user.getUserId());
		}
		return false;
	}
}
