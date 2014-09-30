package com.cmcc.zysoft.schedule.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.schedule.model.Schedule;
import com.cmcc.zysoft.schedule.model.ScheduleCommit;
import com.cmcc.zysoft.schedule.model.ScheduleRelease;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleLeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;

/**
 * 待办任务的控制层
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/todo")
public class ToDoListContoller extends BaseController{
	@Resource
	private ScheduleService scheduleService;
	@Resource
	private LeaderService leaderService;
	@Resource
	private ScheduleLeaderService slService;
	@Resource
	private WeekService weekService;
	/**
	 * 跳转到下周日程拟定页面
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/main.htm") 
	public String showSchedule(ModelMap modelMap,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		int leaderId = user.getLeader().getLeaderId();
		List<Map<String,Object>> list = this.scheduleService.getToDoList(leaderId);
		modelMap.addAttribute("list", list);
		return "web/todolist";
	}
	public String checkSubmit(String startTime){
		
		return null;
	}
	/**
	 * 物理删除待办任务
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value="/del.htm")
	@ResponseBody
	public String del(String scheduleId){
		try{
			this.slService.delBySchedule(scheduleId);
			this.scheduleService.delSchedule(scheduleId);
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 根据id，获取待办任务信息
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value="/getToDoById.htm")
	@ResponseBody
	public Schedule getToDoById(String scheduleId){
		Schedule s = this.scheduleService.getEntity(scheduleId);
		return s;
	}
	/**
	 * 保存为下周任务
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveSchedule.htm")
	@ResponseBody
	public String saveSchedule(Schedule schedule,String startTime,String endTime,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		int leaderId = user.getLeader().getLeaderId();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Week week = getWeek(sf.parse(startTime));
			String state = this.weekService.getSate(week.getWeekId(), leaderId);
			if(null != state && "1".equals(state)){
				return "-1";
			}
			schedule.setWeekId(week.getWeekId());
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setType("0");//正常日程
		schedule.setCreateUserType("0");//非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		return this.scheduleService.update(schedule, leaderId);
	}
	/**
	 * 副总直接将待办日程发布为本周日程
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveThisWeekSchedule.htm")
	@ResponseBody
	public String saveThisWeekSchedule(Schedule schedule,String startTime,String endTime,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		int leaderId = user.getLeader().getLeaderId();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Week week = (Week) request.getSession().getAttribute("thisWeek");
			schedule.setWeekId(week.getWeekId());
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setType("0");//发布
		schedule.setCreateUserType("0");//非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		String result = this.scheduleService.update(schedule, leaderId);
		try {
			this.scheduleService.addReleaseEvent(schedule.getScheduleId());
		} catch (Exception e) {
			e.printStackTrace();
			result = "2";
		}
		
		return result;
	}
	/**
	 * 更新待办任务
	 * @param schedule
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateToDo.htm")
	@ResponseBody
	public String updateToDo(Schedule schedule,HttpServletRequest request){
		try{
		User user = SecurityContextUtil.getCurrentUser();
		Schedule s = this.scheduleService.getEntity(schedule.getScheduleId());
		if(null != s){
			s.setWeekId(null);
			s.setStartTime(null);
			s.setEndTime(null);
			s.setTitle(schedule.getTitle());
			s.setMen(schedule.getMen());
			s.setType("3");
			s.setPlace(schedule.getPlace());
			s.setDetails(schedule.getDetails());
			s.setRemark(schedule.getRemark());
			s.setModifyUser(user.getUserId());
			s.setModifyTime(new Date());
		}
		this.scheduleService.updateEntity(s);
			return "0";
		}catch(Exception e){
			e.printStackTrace();
			return "1";
		}
	}
	/**
	 * 获取时间对应的周
	 * @param date
	 * @param request
	 * @return
	 */
	private Week getWeek(Date date){
		Week week = null;
		Date thisMonday = WeekUtil.getThisMonday(date);
		List<Week> list = this.weekService.findByNamedParam("weekStart", thisMonday);
		if(null != list && list.size()>0){//从数据库获取
			week =  list.get(0);
		}else{
			week = new Week();
			week.setWeekStart(thisMonday);
			week.setReleaseTag("0");
			this.weekService.insertEntity(week);
		}
		return week;
	}
}
