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
import com.cmcc.zysoft.schedule.model.ScheduleCommit;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.ScheduleCommitLeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleCommitService;
import com.cmcc.zysoft.schedule.service.ScheduleService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;

/**
 * 审核日程模块的控制层
 * 
 * @author AMCC
 *
 */
@Controller
@RequestMapping("/pc/audit")
public class AuditContoller extends BaseController {
	@Resource
	private ScheduleService scheduleService;
	@Resource
	private ScheduleCommitService scheduleCommitService;
	@Resource
	private ScheduleCommitLeaderService scheduleCommitLeaderService;
	@Resource
	private WeekService weekService;
	/**
	 * 跳转到审核日程页面
	 * 
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/main.htm")
	public String showSubmitSchedule(ModelMap modelMap,
			HttpServletRequest request) {
		//获取session中的下周实例
		Week week = (Week) request.getSession().getAttribute("week");
		Date mon = week.getWeekStart();
		SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日");
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		//获取周一到周日的日期
		Date[] array = WeekUtil.getWeekDay(mon);
		for (int i = 0; i < array.length; i++) {
			modelMap.addAttribute("day" + i, dateFormat.format(array[i]));
			modelMap.addAttribute("date" + i, df.format(array[i]));
		}
		//获取所有领导
		List<Map<String, Object>> leaders = this.scheduleService
				.getAllLeaders();
		String releaseTag=this.weekService.getEntity(week.getWeekId()).getReleaseTag();
		modelMap.addAttribute("leaders", leaders);
		modelMap.addAttribute("release_tag", releaseTag);
		return "web/audit";
	}
	/**
	 * 获取日程详情
	 * @param scheduleId
	 * @return
	 */
	@RequestMapping(value="/getScheduleById.htm") 
	@ResponseBody
	public ScheduleCommit getScheduleById(String scheduleId){
		ScheduleCommit schedule = this.scheduleCommitService.getEntity(scheduleId);
		List<Map<String,Object>> list = this.scheduleCommitLeaderService.getBySchedule(scheduleId);
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
	 * 获取下周所有已提交或已发布的日程
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getSubmitSchedule.htm")
	@ResponseBody
	public List<Map<String, Object>> getSubmitSchedule(
			HttpServletRequest request) {
		Week week = (Week) request.getSession().getAttribute("week");
		return this.scheduleService.getSubmitSchedule(week.getWeekId());
	}
}
