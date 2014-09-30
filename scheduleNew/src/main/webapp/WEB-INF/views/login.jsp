<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>日程安排-登录</title>
<link type="text/css" rel="stylesheet" href="/resources/css/reset.css" />
<link type="text/css" rel="stylesheet" href="/resources/css/style.css" />
<script type="text/javascript" src="/resources/scripts/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="/resources/js/login/login.js"></script>
<script type="text/javascript">
if (window != top)
top.location.href = location.href;
</script>
</head>

<body class="login_body">
	<div class="header" style="height:2px; padding:40px 0;">
    	<div class="point" style=" margin:0 100px;">
            	<div class="left point_left"></div>
                <div style=" padding:5px 15px;">
                	<div class="w_line"></div>
                </div>               
            </div>
    </div>
    <div class="login_bg">    
    <form name="loginForm" id="loginForm" action="" method="post"> 	
            <div class="login_title red" >
            	<span id="error" style="display:none;">
            	登录信息错误提示，默认隐藏，包含用户名和密码
            	</span>
            </div>
        <input type="text"  class="login_input" name="j_username" id="j_username" value="${x}" style="margin:30px 0 25px 35px;" />       
        <input type="password"  class="login_input" name="j_password" id="j_password" value="${y}"  style="margin-left:35px;" />
        <div class="l_botm">
        	<span class="left white">
            	<input type="checkbox" <c:if test="${x ne null}">checked="checked"</c:if> id="flag_token" name="flag_token"/>  记住密码
            	<a href="javascript:void(0)" onclick="Login.forget();">忘记密码？</a>
            </span>
            <input id="loginBtn" type="button" class="login_btn right" />
        </div>   
        </form>    
    </div>
</body>
</html>
