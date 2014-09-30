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
import com.cmcc.zysoft.schedule.model.Holiday;
import com.cmcc.zysoft.schedule.model.Leader;
import com.cmcc.zysoft.schedule.model.Schedule;
import com.cmcc.zysoft.schedule.model.ScheduleLeader;
import com.cmcc.zysoft.schedule.model.ScheduleLeaderTemp;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.HolidayService;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleLeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;
/**
 * 下周日程拟定的控制层
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/schedule")
public class ScheduleContoller extends BaseController{
	@Resource
	private ScheduleService scheduleService;
	@Resource
	private ScheduleLeaderService scheduleLeaderService;
	@Resource
	private WeekService weekService;
	@Resource
	private LeaderService leaderService;
	@Resource
	private HolidayService holidayService;
	/**
	 * 跳转到下周日程拟定页面
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/main.htm") 
	public String showSchedule(ModelMap modelMap,HttpServletRequest request){
		//获取当前用户对应的领导
		User user = SecurityContextUtil.getCurrentUser();
		int leaderId = user.getLeader().getLeaderId();
		Leader leader = this.leaderService.getEntity(leaderId);
		modelMap.addAttribute("leader", leader);
		Week week = getWeek(request);
		String state = this.weekService.getSate(week.getWeekId(), leaderId);
		if(null != state && "1".equals(state)){
			modelMap.addAttribute("commit", true);
		}
		//获取所有领导
		List<Map<String, Object>> leaders = this.scheduleService
				.getAllLeaders();
		modelMap.addAttribute("leaders", leaders);
		return "web/schedule";
	}
	/**
	 * 获取对应领导的所有日程
	 * @return
	 */
	@RequestMapping(value="/loadEvent.htm") 
	@ResponseBody
	public List<Map<String,Object>> loadEvent(String start,String end){
		User user = SecurityContextUtil.getCurrentUser();
		Date dateStart = new Date(Long.valueOf(start)*1000);
		Date dateEnd = new Date(Long.valueOf(end)*1000);
		//获取当前用户对应领导的全部日程，并返回
		List<Map<String,Object>> list = this.scheduleService.getScheduleByLeader(user.getLeader().getLeaderId(),dateStart,dateEnd);
		return list;
	}
	/**
	 * 保存简易日程
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveEasy.htm") 
	@ResponseBody
	public String saveEasy(String title,String startTime,String endTime,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		Week week = getWeek(request);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Schedule schedule = new Schedule();
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
		schedule.setCreateUserType("0");//非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		int leaderId = user.getLeader().getLeaderId();
		return this.scheduleService.add(schedule, leaderId);
	}
	/**
	 * 保存完整日程
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveDetail.htm") 
	@ResponseBody
	public String saveDetail(Schedule schedule,String startTime,String endTime,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		Week week = getWeek(request);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		schedule.setWeekId(week.getWeekId());
		try {
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setType("0");//正常日程
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		int leaderId = user.getLeader().getLeaderId();
		return this.scheduleService.add(schedule, leaderId);
	}

	/**
	 * 保存为待办任务
	 * @param schedule
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveToDo.htm") 
	@ResponseBody
	public String saveToDo(Schedule schedule,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		schedule.setStartTime(null);
		schedule.setEndTime(null);
		schedule.setType("3");//待办任务
		schedule.setCreateUserType("0");//非领导
		schedule.setCreateUser(user.getUserId());
		schedule.setCreateTime(new Date());
		int leaderId = user.getLeader().getLeaderId();
		return this.scheduleService.addToDo(schedule, leaderId);
	}
	/**
	 * 秘书编辑日程
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param leaders
	 * @return
	 */
	@RequestMapping(value="/editDetail.htm") 
	@ResponseBody
	public String editDetail(Schedule schedule,String startTime,String endTime){
		try {
			String scheduleId = schedule.getScheduleId();
			if (null == scheduleId) {// 日程主键为空，返回错误
				return "2";
			}
			User user = SecurityContextUtil.getCurrentUser();
			Schedule s = this.scheduleService.getEntity(scheduleId);
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

			}
			int leaderId = user.getLeader().getLeaderId();
			return this.scheduleService.update(s, leaderId);
		} catch (Exception e) {
			e.printStackTrace();
			return "2";
		}
	}
	/**
	 * 提交日程
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/submitSchedule.htm") 
	@ResponseBody
	public String submitSchedule(HttpServletRequest request){
		Week week = getWeek(request);
		User user = SecurityContextUtil.getCurrentUser();
		try{
			this.scheduleService.updateScheduleToSubmit(user.getUserId(), user.getLeader().getLeaderId(),week.getWeekId());
			this.weekService.saveOrUpdateWeekLeader(week.getWeekId(), user.getLeader().getLeaderId(), "1");
			week.setReleaseTag("0");
			this.weekService.updateEntity(week);//未发布
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
	}
	/**
	 * 获取日程详情
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value="/getScheduleById.htm") 
	@ResponseBody
	public Schedule getScheduleById(String scheduleId){
		Schedule schedule = this.scheduleService.getEntity(scheduleId);
		List<Map<String,Object>> list = this.scheduleLeaderService.getBySchedule(scheduleId);
		String leaders ="";
		String leaderIds="";
		for (int i=0;i<list.size();i++) {
			String leaderName= list.get(i).get("leader_name").toString();
			String leaderId= list.get(i).get("leader_id").toString();
			if(i==list.size()-1){
				leaders +=leaderName;
				leaderIds +=leaderId;
			}else{
				leaders +=leaderName+",";
				leaderIds +=leaderId+",";
			}
		}
		schedule.setMark1(leaders);//暂存领导名称
		schedule.setMark2(leaderIds);//暂存领导id，便于客户端获取
		return schedule;
	}
	/**
	 * 拖拽或下拉日程时触发
	 * @param scheduleId
	 * @param startTime
	 * @param endTime
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/dragSchedule.htm") 
	@ResponseBody
	public String dragSchedule(String scheduleId,String startTime,String endTime,HttpServletRequest request){
		User user = SecurityContextUtil.getCurrentUser();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Schedule schedule =  this.scheduleService.getEntity(scheduleId);
		try {
			schedule.setStartTime(sf.parse(startTime));
			schedule.setEndTime(sf.parse(endTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedule.setModifyUser(user.getUserId());
		schedule.setModifyTime(new Date());
		int leaderId = user.getLeader().getLeaderId();
		return this.scheduleService.dragSchedule(schedule, leaderId);
	}
	/**
	 * 获取所有假期安排
	 * @return
	 */
	@RequestMapping(value="/getHoliday.htm") 
	@ResponseBody
	public List<Holiday> getHoliday(){
		return this.holidayService.findAllEntity();
	}
	/**
	 * 获取下周周数据
	 * @param request
	 * @return
	 */
	private Week getWeek(HttpServletRequest request){
		Week week = null;
		if(null != request.getSession().getAttribute("week")){//从session中获取
			week = (Week)request.getSession().getAttribute("week");
		}else{
			Date nextMonday = WeekUtil.getNextMonday(new Date());
			List<Week> list = this.weekService.findByNamedParam("weekStart", nextMonday);
			if(null != list && list.size()>0){//从数据库获取
				week =  list.get(0);
				request.getSession().setAttribute("week", week);
			}else{//创建并添加到session中
				week = new Week();
				week.setWeekStart(nextMonday);
				week.setReleaseTag("0");
				this.weekService.insertEntity(week);
				request.getSession().setAttribute("week", week);
			}
		}
		return week;
	}
	/**
	 * 控制提交状态为未提交
	 * @return
	 */
	@RequestMapping(value="/changeCommitState.htm") 
	@ResponseBody
	public void changeCommitState(HttpServletRequest request){
		Week week = getWeek(request);
		User user = SecurityContextUtil.getCurrentUser();
		//改为未提交状态
		this.weekService.saveOrUpdateWeekLeader(week.getWeekId(), user.getLeader().getLeaderId(), "0");
	}
	/**
	 * 判断是否多用户日程
	 * @return
	 */
	@RequestMapping(value="/checkIsManyLeaderEvent.htm") 
	@ResponseBody
	public boolean checkIsManyLeaderEvent(String scheduleId){
		List<ScheduleLeader> flagList = this.scheduleLeaderService.findByNamedParam(new String[]{"scheduleId","delFlag"}, new Object[]{scheduleId,"0"});
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
			this.scheduleLeaderService.delByScheduleLeader(scheduleId,user.getLeader().getLeaderId());
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
	}
}
