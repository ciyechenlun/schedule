package com.cmcc.zysoft.schedule.controller;



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
import com.cmcc.zysoft.schedule.model.Leader;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;
/**
 * 本周日程安排的控制层
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/theweek")
public class TheWeekContoller extends BaseController{
	@Resource
	private ScheduleService scheduleService;
	@Resource
	private LeaderService leaderService;
	@Resource
	private WeekService weekService;
	/**
	 * 跳转到本周日程页面
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/main.htm") 
	public String showTheWeekSchedule(ModelMap modelMap,HttpServletRequest request){
		//获取当前用户
		User user = SecurityContextUtil.getCurrentUser();
		modelMap.addAttribute("user", user);
		//获取本周实例
		Week week = (Week)request.getSession().getAttribute("thisWeek");
		//获取本周一的时间
		Date mon = week.getWeekStart();
		SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日");
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		//获取本周一至周日的时间
		Date[] array = WeekUtil.getWeekDay(mon);
        for (int i=0;i<array.length;i++) {
        	modelMap.addAttribute("day"+i, dateFormat.format(array[i]));
        	modelMap.addAttribute("date"+i, df.format(array[i]));
        }
        //获取所有领导
		List<Map<String,Object>> leaders = this.scheduleService.getAllLeaders();
		String releaseTag=this.weekService.getEntity(week.getWeekId()).getReleaseTag();
		modelMap.addAttribute("leaders", leaders);
		modelMap.addAttribute("release_tag", releaseTag);
		
		int leaderId = user.getLeader().getLeaderId();
		Leader leader = this.leaderService.getEntity(leaderId);
		modelMap.addAttribute("leader", leader);
		//调转到本周日程页面
		return "web/theweek";
	}
	/**
	 * 获取本周日程
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getThisWeekSchedule.htm") 
	@ResponseBody
	public List<Map<String,Object>> getThisWeekSchedule(HttpServletRequest request){
		//获取本周实例
		Week week = (Week)request.getSession().getAttribute("thisWeek");
		//获取本周所有已提交或者已发布的日程，并返回给客户端
		return this.scheduleService.getThisWeekSchedule(week.getWeekId());
	}
}
