// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.schedule.model.Search;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;

/**
 * 首页控制层，主要创建下周实例
 * @author 张军
 * @mail zahng.jun3@ustcinfo.com
 * @date 2014-07-17 上午1:29:49
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseController {
	@Resource
	private WeekService weekService;

	/**
	 * 跳转到首页.
	 * 
	 * @param request
	 * @return 返回类型：ModelAndView
	 */
	@RequestMapping("index.htm")
	public String index(ModelMap modelMap, Search search,
			HttpServletRequest request) {
		//获取当前操作用户并添加到modelMap中国
		User user = SecurityContextUtil.getCurrentUser();
		modelMap.addAttribute("user", user);
		//获取下周一的时间
		Date nextMonday = WeekUtil.getNextMonday(new Date());
		//获取本周一的时间
		Date thisMonday = WeekUtil.getThisMonday(new Date());
		//查询数据库中是否存在下周一
		List<Week> list = this.weekService.findByNamedParam("weekStart",
				nextMonday);
		//查询数据库中是否存在本周一
		List<Week> thisList = this.weekService.findByNamedParam("weekStart",
				thisMonday);
		//存在下周日程则将下周实例添加到session中
		if (null != list && list.size() > 0) {
			request.getSession().setAttribute("week", list.get(0));
		} else {
			//否则创建新的下周实例，并添加到session中
			Week week = new Week();
			week.setWeekStart(nextMonday);
			week.setReleaseTag("0");
			this.weekService.insertEntity(week);
			request.getSession().setAttribute("week", week);
		}
		//存在本周日程，则将本周实例添加到session中
		if (null != thisList && thisList.size() > 0) {
			request.getSession().setAttribute("thisWeek", thisList.get(0));
		} else {
			//否则创建本周实例，并添加到session中
			Week week = new Week();
			week.setWeekStart(thisMonday);
			week.setReleaseTag("1");
			this.weekService.insertEntity(week);
			request.getSession().setAttribute("thisWeek", week);
		}
		return "index";

	}
}
