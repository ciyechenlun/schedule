// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmcc.zysoft.framework.utils.UUIDUtil;
import com.cmcc.zysoft.schedule.model.SystemUser;
import com.cmcc.zysoft.schedule.service.SystemUserPCService;
import com.cmcc.zysoft.schedule.util.MD5Tools;
import com.cmcc.zysoft.schedule.util.SendMsgTest;
import com.cmcc.zysoft.schedule.util.StringUtil;
import com.cmcc.zysoft.schedule.util.sendMsg;

/**
 * @author 李梦华
 * <br />邮箱： li.menghua@ustcinfo.com
 * <br />描述：testMsgController
 * <br />版本:1.0.0
 * <br />日期： 2013-3-12 上午10:03:43
 * <br />CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
 */
@Controller
@RequestMapping("/mobile/testMsg")
public class testMsgController {
	
	@Resource
	private SystemUserPCService userService;
	
	/**
	 * 测试短信发送.
	 * @param userName 
	 * @return 
	 * 返回类型：int
	 */
	@RequestMapping("/send")
	@Transactional
	@ResponseBody
	public int send(String userName) {
		String userNameStr = userName;
		List<SystemUser> list = this.userService.findByNamedParam(new String[]{"userName", "delFlag"}, new Object[]{userNameStr, "0"});
		if(list.size() > 0) {
			SystemUser systemUser = list.get(0);
			String salt = UUIDUtil.generateUUID();
			int number = (int)((Math.random() + 1) * 1000000);
			String code = "" + number;
			code = code.substring(1, 7);
			String passWord = MD5Tools.encodePassword(code, salt);
			String msg = "您的用户名" + userNameStr + "的新密码为" + code + ",请使用新密码登陆！";
			if(userNameStr.indexOf("admin") >= 0)
			{
				userNameStr = systemUser.getMobile();
			}
			String result = sendMsg.sendMMM(msg, userNameStr);
			if(StringUtils.hasText(result)) {
				systemUser.setPassSalt(salt);
				systemUser.setPassword(passWord);
				this.userService.updateEntity(systemUser);
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 发送短信.
	 * @param userName 
	 * 返回类型：int
	 */
	@RequestMapping("/sendMsg.htm")
	@Transactional
	@ResponseBody
	public int sendMsg(String userName,HttpServletRequest request) {
		List<SystemUser> list = this.userService.findByNamedParam(new String[]{"userName", "delFlag"}, new Object[]{userName, "0"});
		if(list.isEmpty()) {
			return 0;
		} else {
			Date lastDate = (Date)request.getSession().getAttribute(userName);
			
			if(null !=lastDate){
				long between=(new Date().getTime()-lastDate.getTime())/1000;
				if(between<60){
					return 2;
				}
			}
			SystemUser systemUser = list.get(0);
			String salt = UUIDUtil.generateUUID();
			int number = (int)((Math.random() + 1) * 1000000);
			String code = "" + number;
			code = code.substring(1, 7);
			String passWord = MD5Tools.encodePassword(code, salt);
			String content = "您的用户名" + userName + "的新密码为" + code + ",请使用新密码登陆!";
			if(!StringUtil.isMobileNO(userName))
			{
				userName = systemUser.getMobile();
			}
			String result = SendMsgTest.sendMsg(content, userName);
			if("SUCCESS".equals(result)) {
				systemUser.setPassSalt(salt);
				systemUser.setPassword(passWord);
				this.userService.updateEntity(systemUser);
				request.getSession().setAttribute(userName, new Date());
				return 1;
			} else {
				return 0;
			}
		}
	}
}