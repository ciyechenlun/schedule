package com.cmcc.zysoft.schedule.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmcc.zysoft.framework.utils.UUIDUtil;
import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.schedule.model.Leader;
import com.cmcc.zysoft.schedule.model.Role;
import com.cmcc.zysoft.schedule.model.SystemUser;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.RoleService;
import com.cmcc.zysoft.schedule.service.SystemUserPCService;
import com.cmcc.zysoft.schedule.util.MD5Tools;
import com.cmcc.zysoft.spring.security.model.User;
import com.cmcc.zysoft.spring.security.util.SecurityContextUtil;

@Controller
@RequestMapping("/pc/user")
public class UserContoller extends BaseController{
	@Resource
	private SystemUserPCService userService;
	
	@Resource
	private RoleService roleService;
	
	@Resource	
	private LeaderService leaderService;
	
	@RequestMapping(value="/main.htm") 
	public String showUserList(ModelMap modelMap,HttpServletRequest request){
		List<SystemUser> list = this.userService.loadAll();
		modelMap.put("list", list);
		List<Leader> leaderList = this.leaderService.loadEntities();
		modelMap.put("leaderList", leaderList);
		List<Role> roleList = this.roleService.loadEntities();
		modelMap.put("roleList", roleList);
		return "web/user";
	}
	
	@RequestMapping(value="/save.htm") 
	@ResponseBody
	public String save(SystemUser systemUser,String roleId,String leaderId,String preOrder){
		try{
			if(StringUtils.hasText(roleId)){
				systemUser.setRole(new Role(Integer.parseInt(roleId)));
			}
			if(StringUtils.hasText(leaderId)){
				systemUser.setLeader(new Leader(Integer.parseInt(leaderId)));
			}
			systemUser.setDelFlag("0");
			if(StringUtils.hasText(systemUser.getUserId())){//修改
				SystemUser su = this.userService.getEntity(systemUser.getUserId());
				systemUser.setPassSalt(su.getPassSalt());
				systemUser.setPassword(su.getPassword());
				this.userService.updateDisOrderIfUpdate(systemUser.getDisOrder(), Integer.parseInt(preOrder));
				this.userService.updateEntity(systemUser);
			}else{//添加
				this.userService.updateDisOrderIfAdd(systemUser.getDisOrder());
				String salt = UUIDUtil.generateUUID();
				systemUser.setPassSalt(salt);
				systemUser.setPassword(MD5Tools.encodePassword("111111", salt));
				this.userService.insertEntity(systemUser);
			}
			return "SUCCESS";
		}catch(Exception e){
			return "ERROR";
		}
	}
	@RequestMapping(value="/getRoleList.htm") 
	@ResponseBody
	@Transactional(readOnly=true)
	public List<Role> getRoleList(){
		return this.roleService.loadEntities();
	}
	@RequestMapping(value="/getLeaderList.htm") 
	@ResponseBody
	@Transactional(readOnly=true)
	public List<Leader> getLeaderList(){
		return this.leaderService.loadEntities();
	}
	@RequestMapping(value="/pauseUser.htm") 
	@ResponseBody
	@Transactional
	public String pauseUser(String userId){
		try{
			SystemUser user = this.userService.getEntity(userId);
			if(null !=user){
				user.setDelFlag("1");//0正常 1暂停 2删除
				this.userService.updateEntity(user);
			}
			return "SUCCESS";
		}catch(Exception e){
			return "ERROR";
		}
		
	}
	@RequestMapping(value="/delUser.htm") 
	@ResponseBody
	@Transactional
	public String delUser(String userId){
		try{
			SystemUser user = this.userService.getEntity(userId);
			if(null !=user){
				int disOrder = user.getDisOrder();
				user.setDelFlag("2");//0正常 1暂停 2删除
				this.userService.updateEntity(user);
				this.userService.updateDisOrderIfDel(disOrder);
			}
			return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
		
	}
	/**
	 * 判断用户名是否已存在
	 */
	@RequestMapping(value="/checkUserName.htm") 
	@ResponseBody
	public String checkUserName(String userName){
		List<SystemUser> list = this.userService.findByNamedParam(new String[]{"userName","delFlag"},new Object[]{userName,"0"});
		if(null !=list && list.size()>0){
			return "1";//代表存在
		}
		return "0";//代表不存在
	}
	@RequestMapping(value="/getUser.htm") 
	@ResponseBody
	public SystemUser getUser(String userId){
		SystemUser user = this.userService.getEntity(userId);
		return user;
	}
	/**
	 * 修改密码.
	 * @param oldPassword 
	 * @param newPassword 
	 * @param request 
	 * @return 
	 * 返回类型：Map<String,String>
	 */
	@RequestMapping(value="/changePassword.htm")
	@Transactional
	@ResponseBody
	public Map<String, String> changePassword(String oldPassword,String newPassword,HttpServletRequest request){
		Map<String, String>  map = new HashMap<String, String>();
		User user = SecurityContextUtil.getCurrentUser();
		SystemUser systemUser = this.userService.getEntity(user.getUserId());
		if(!MD5Tools.isPasswordValid(systemUser.getPassword(), oldPassword, systemUser.getPassSalt())){
			map.put("success", "false");
			map.put("msg", "旧密码输入不正确");
			return map;
		}
		String passwordSalt = UUIDUtil.generateUUID();
		systemUser.setPassSalt(passwordSalt);
		systemUser.setPassword(MD5Tools.encodePassword(newPassword, passwordSalt));
		this.userService.updateEntity(systemUser);
		map.put("success", "true");
		return map;
	}
}
