<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="renderer" content="webkit"/>
<title>日程安排</title>
<%@include file="common/baseIncludeJs.jsp" %>
 <script type="text/javascript" src="/resources/js/selectmenu.js"></script>
</head>

<body>
<input type="hidden" id="user_role" value="${user.role.roleId}"/>
<!--top-->
<%@include file="top.jsp" %>
    <div class="main" style=" height:100%; padding-bottom:10px;">
    	<div class="menu">
        	<ul>
            	<li class="menuItem active" onclick="">本周日程</li>
            	<c:if test="${user.role.roleId eq '1' || user.role.roleId eq '2' || user.role.roleId eq '3'}">
                <li class="menuItem" onclick="">下周日程拟定</li>
                </c:if>
                <c:if test="${user.role.roleId eq '1' || user.role.roleId eq '2'}">
                <li class="menuItem" onclick="">审核日程</li>
                </c:if>
                <c:if test="${user.role.roleId eq '1' || user.role.roleId eq '2' || user.role.roleId eq '3'}">
                <li class="menuItem" onclick="">待办日程</li>
                </c:if>
                <c:if test="${user.role.roleId eq '1'}">
                <li class="menuItem" onclick="">账号管理</li>
                </c:if>
            </ul>
        </div>
        <div class="cont clearfix" id="rightWrap" style="height:600px">
    		<iframe id="if_main" frameborder="0" width="100%" height="100%" marginheight="0" marginwidth="0" src="/pc/theweek/main.htm"></iframe>
      	    <!--<div class="maskWrap"></div> -->
    	</div>
    </div>
    <div class="footer">版权信息：中国移动通信安徽分公司</div>
</body>
</html>
