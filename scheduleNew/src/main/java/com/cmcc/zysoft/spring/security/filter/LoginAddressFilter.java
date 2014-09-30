// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.spring.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.GenericFilterBean;

import com.cmcc.zysoft.framework.utils.StringUtil;
import com.cmcc.zysoft.schedule.util.CookieUtil;
import com.cmcc.zysoft.schedule.util.Des;
import com.cmcc.zysoft.spring.security.util.Constant;

/**
 * @author 李三来
 * <br />邮箱： li.sanlai@ustcinfo.com
 * <br />描述：用于拦截登录地址
 * <br />版本:1.0.0
 * <br />日期： 2013-1-14 上午8:41:43
 * <br />CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
 */
public class LoginAddressFilter extends GenericFilterBean {
	//~ fields------------------------------------------------------
	
	/**
	 * 属性名称：logger 类型：Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoginAddressFilter.class);
	
	/**
	 * 属性名称：redirectStrategy 类型：RedirectStrategy
	 */
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	
	
	//~ methods------------------------------------------------------
	/**
	 * 构造方法
	 */
	public LoginAddressFilter() {
	}
	
	/**
	 * 构造方法
	 * @param redirectStrategy
	 */
	public LoginAddressFilter(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}


	/**
	 * 执行拦截操作，获取登录地址，以确定是哪个公司
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		logger.debug("#执行拦截操作，获取登录地址，以确定是哪个公司");
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		String url = req.getRequestURI();
		String uri = "";
		logger.debug("#登录地址={}",url);
		if(url.startsWith("/login/")){
			uri = url.replaceFirst("/login/", "");
		}else{
			uri = "/";
		}
		req.getSession().setAttribute(Constant.SESSION_LOGINURL, uri);
		//登录成功之后在cookie里面放一个登录地址，以便注销的时候方便知道跳转到那个登录地址
		CookieUtil.addCookie(res, Constant.SESSION_LOGINURL,uri,-1);
		String usernameAndPassword = CookieUtil.getCookieValue(req, "xy");
		if(!StringUtil.isNullOrEmpty(usernameAndPassword)){
			String[] temp = usernameAndPassword.split("[|]");
			String username = Des.Decrypt(temp[0], Des.hex2byte("0002000200020002"));
			String password = Des.Decrypt(temp[1], Des.hex2byte("0002000200020002"));
			req.setAttribute("x", username);
			req.setAttribute("y", password);
		}
		chain.doFilter(request, response);
	}

	/**
	 * 返回redirectStrategy
	 * @return the redirectStrategy
	 */
	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	/**
	 * 设置redirectStrategy
	 * @param redirectStrategy the redirectStrategy to set
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
	
}
