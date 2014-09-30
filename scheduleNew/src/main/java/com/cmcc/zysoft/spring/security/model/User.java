// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.spring.security.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cmcc.zysoft.schedule.model.Leader;
import com.cmcc.zysoft.schedule.model.Role;


/**
 * @author 李三来 <br />
 *         邮箱： li.sanlai@ustcinfo.com <br />
 *         描述：用户 <br />
 *         版本:1.0.0 <br />
 *         日期： 2013-1-10 下午7:59:37 <br />
 *         CopyRight © 2012 USTC SINOVATE SOFTWARE CO.LTD All Rights Reserved.
 */
public class User implements UserDetails {

	 private String userId;
     private String username;
     private String password;
     private String passSalt;
     private String mobile;
     private String realName;
     private Role role;
     private int disOrder;
     private Leader leader;
     private String delFlag;
     
     
     
	public User() {
	}

	public User(String userId, String username, String password,
			String passSalt, String mobile, String realName, Role role,
			int disOrder, Leader leader, String delFlag,
			List<GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.passSalt = passSalt;
		this.mobile = mobile;
		this.realName = realName;
		this.role = role;
		this.disOrder = disOrder;
		this.leader = leader;
		this.delFlag = delFlag;
		this.authorities = authorities;
	}

	/**
	 * 角色列表
	 */
	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

	// ~ serialVersionUID ： long
	private static final long serialVersionUID = -8438527754900746558L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getAuthorities
	 * ()
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired
	 * ()
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked
	 * ()
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * 返回userId
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置userId
	 * 
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 返回passSalt
	 * 
	 * @return the passSalt
	 */
	public String getPassSalt() {
		return passSalt;
	}

	/**
	 * 设置passSalt
	 * 
	 * @param passSalt
	 *            the passSalt to set
	 */
	public void setPassSalt(String passSalt) {
		this.passSalt = passSalt;
	}

	/**
	 * 返回mobile
	 * 
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * 设置mobile
	 * 
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 返回realName
	 * 
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * 设置realName
	 * 
	 * @param realName
	 *            the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}


	/**
	 * 设置username
	 * 
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 设置password
	 * 
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 设置authorities
	 * 
	 * @param authorities
	 *            the authorities to set
	 */
	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}



	public int getDisOrder() {
		return disOrder;
	}

	public void setDisOrder(int disOrder) {
		this.disOrder = disOrder;
	}


	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Leader getLeader() {
		return leader;
	}

	public void setLeader(Leader leader) {
		this.leader = leader;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	/**
	 * 重写hashcode方法，解决session并发不生效问题
	 */
	@Override
	public int hashCode() {
		return this.userId.hashCode();
	}

	/**
	 * 重写equals方法，解决session并发不生效问题
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
	           return false;
		User user = (User) obj;
		return user.getUserId().equals(userId);
	}

}
