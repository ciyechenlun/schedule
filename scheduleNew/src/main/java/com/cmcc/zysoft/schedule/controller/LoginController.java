// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cmcc.zysoft.framework.utils.StringUtil;
import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.spring.security.util.Constant;

/**
 * @author 李三来
 * <br />邮箱： li.sanlai@ustcinfo.com
 * <br />描述：登录控制器
 * <br />版本:1.0.0
 * <br />日期： 2013-1-13 上午10:29:08
 * <br />CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
 */
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {
	
	//~ fields------------------------------------------------------
	
	/**
	 * 登录页面
	 */
	private static final String LOGIN_VIEW = "login";
	
	private static final String PLOGIN_VIEW = "/pedmeter/mLogin.htm";
	
	/**
	 * 404页面
	 */
	private static final String NO_PAGE_VIEW = "404";
	
	
	
	//~ methods------------------------------------------------------
	
	/**
	 * 跳转到登录页面
	 * @return
	 */
	@RequestMapping("/login.htm")
	public String loginViewFirst(HttpServletRequest request, String errorCode,String to,ModelMap modelMap){
		if(!StringUtil.isNullOrEmpty(errorCode) 
				&& !StringUtil.isNullOrEmpty(Constant.ERROR_MESSAGE_MAP.get(errorCode))){
			request.setAttribute(Constant.ERROR_MESSAGE, Constant.ERROR_MESSAGE_MAP.get(errorCode));
		}
		if (StringUtils.hasText(to)){
			modelMap.addAttribute("to", to);
		}
		else
		{
			modelMap.addAttribute("to", "");
		}
		return LOGIN_VIEW;
	}
	
	/**
	 * 跳转到登录页面
	 * @return
	 */
	@RequestMapping("/")
	public String loginViewSecond(HttpServletRequest request,String errorCode){
		if(!StringUtil.isNullOrEmpty(errorCode) 
				&& !StringUtil.isNullOrEmpty(Constant.ERROR_MESSAGE_MAP.get(errorCode))){
			request.setAttribute(Constant.ERROR_MESSAGE, Constant.ERROR_MESSAGE_MAP.get(errorCode));
		}
		return LOGIN_VIEW;
	}
	
	/**
	 * 跳转到登录页面
	 * @return
	 */
	@RequestMapping("/login")
	public String loginViewThird(HttpServletRequest request,String errorCode){
		if(!StringUtil.isNullOrEmpty(errorCode) 
				&& !StringUtil.isNullOrEmpty(Constant.ERROR_MESSAGE_MAP.get(errorCode))){
			request.setAttribute(Constant.ERROR_MESSAGE, Constant.ERROR_MESSAGE_MAP.get(errorCode));
		}
		return LOGIN_VIEW;
	}
	
	
	/**
	 * 计步器登录界面
	 * @param request
	 * @param errorCode
	 * @return
	 */
	@RequestMapping("/plogin.htm")
	public String loginPed(HttpServletRequest request,String errorCode)
	{
		return LOGIN_VIEW;
	}
	
	/**
	 * 跳转到不通公司的登录页面
	 * @return
	 */
	@RequestMapping("/login/{loginUrl}")
	public String loginViewByCompany(HttpServletRequest request,@PathVariable("loginUrl") String loginUrl,String errorCode){
		//查看地址是否带有errorCode=401参数
		if(!StringUtil.isNullOrEmpty(errorCode) 
				&& !StringUtil.isNullOrEmpty(Constant.ERROR_MESSAGE_MAP.get(errorCode))){
			request.setAttribute(Constant.ERROR_MESSAGE, Constant.ERROR_MESSAGE_MAP.get(errorCode));
		}
		return LOGIN_VIEW;
	}
	
}
