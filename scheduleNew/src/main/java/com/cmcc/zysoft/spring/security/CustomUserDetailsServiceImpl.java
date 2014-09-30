// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.spring.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cmcc.zysoft.schedule.model.SystemUser;
import com.cmcc.zysoft.spring.security.model.User;

/**
 * @author 李三来
 * <br />邮箱： li.sanlai@ustcinfo.com
 * <br />描述：自定义用户详细信息服务类
 * <br />版本:1.0.0
 * <br />日期： 2013-1-10 下午9:13:15
 * <br />CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
 */
public class CustomUserDetailsServiceImpl extends HibernateDaoSupport implements
		UserDetailsService {

	
	/**
	 * 利用用户名登录系统
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//查找用户列表
		String hql = "from SystemUser as u where u.userName = ? and u.delFlag='0'";
		
		try {
			List<SystemUser>  systemUsers = getHibernateTemplate().find(hql,username);
			if (systemUsers.size() < 1) {
	            throw new UsernameNotFoundException(username + "not found");
	        }
			
			SystemUser sysTUser = systemUsers.get(0);
			
			//创建spring security 用户
			User user = new User();
			user.setUsername(username);
			//设置角色列表
			user.setRole(sysTUser.getRole());
			user.setMobile(sysTUser.getMobile());
			user.setPassSalt(sysTUser.getPassSalt());
			user.setPassword(sysTUser.getPassword());
			user.setRealName(sysTUser.getRealName());
			user.setUserId(sysTUser.getUserId());
			user.setLeader(sysTUser.getLeader());
			user.setDisOrder(sysTUser.getDisOrder());
			user.setDelFlag(sysTUser.getDelFlag());
			
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new com.cmcc.zysoft.spring.security.model.Role(String.valueOf(sysTUser.getRole().getRoleId())));
			user.setAuthorities(authorities);
			
			return user;
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new UsernameNotFoundException(username + "not found");
		}
	}

}
