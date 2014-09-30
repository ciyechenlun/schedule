<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>用户管理</title>
<%@include file="../common/baseIncludeJs.jsp" %>
<script type="text/javascript" src="/resources/js/web/user.js"></script>
<style type="text/css">
body{padding:10px 20px;}
</style>
</head>

<body >
<!--弹出窗口-->
    <div id="light" class="window_content">
        <div class="windowHeader">新建用户</div> 
        <div class="windowCont clearfix">
        <form id="userForm" action="post">
        <input type="hidden" id="userId" name="userId" />
        <input type="hidden" id="preOrder" name="preOrder" />
        <input type="hidden" id="preUserName" name="preUserName" />
       	  <ul>
            	<li>
                	<label>用户名：</label>
                    <input type="text"  id="userName" name="userName" maxlength="32" onblur="User.checkExist()"/>
                    <span id="user_tip" class="red">*</span>
                </li>
                <li>
                	<label>名称：</label>
                    <input type="text"  id="realName"  name="realName" maxlength="32"/>
                    <span  class="red">*</span>
                </li>
                <li>
                	<label>手机：</label>
                    <input type="text"  id="mobile"  name="mobile" maxlength="11"/>
                     <span  class="red">*</span>
                </li>
                <li>
                	<label>角色：</label>
                    <select id="roleId" name="roleId">
                    	<option value=''>-请选择-</option>
                         <c:forEach items="${roleList}" var="role">
	                        <option value='${role.roleId }'>${role.roleName}</option>
                        </c:forEach>
                    </select>
                     <span  class="red">*</span>
                </li>
                <li>
                	<label>对应领导：</label>
                    <select id="leaderId" name="leaderId">
                    	<option value=''>-请选择-</option>
                    	 <c:forEach items="${leaderList}" var="leader">
	                        <option value='${leader.leaderId }'>${leader.leaderName}</option>
                        </c:forEach>
                    </select>
                </li>
                <li>
                	<label>级别排序：</label>
                    <select id="disOrder" name="disOrder">
                    	<option value=''>-请选择-</option>
                    	 <c:forEach items="${list}" varStatus="status">
                    	<option>${status.index + 1}</option>
						</c:forEach>
						<option>${fn:length(list)+1}</option>
                    </select>
                     <span  class="red">*</span>
                </li>
            </ul>
            <div class="window_btn">
            <input type="button" value="保存" class="btn_safe"  onclick="User.save()" />
            <input type="button" value="取消" class="btn_red"  onclick="document.getElementById('light').style.display='none';document.getElementById('fade').style.display='none'" />
            </div>
            </form>
        </div> 
    </div>
    <!--弹出窗口结束-->
    <!--蒙版-->
    <div id="fade" class="black_overlay">123</div>
    <!--蒙版结束-->
    
    <!--账户表格-->
	<div class="newuser_btn">
        <input type="button" value="新建账户"  onclick="User.toAddUser()" />
    </div>    
    <div class="gridCont">
    	<table width="100%" border="1" cellspacing="0" cellpadding="0">
          <tr>
            <th scope="col">账户名</th>
            <th scope="col">所有者</th>
            <th scope="col">手机</th>
            <th scope="col">角色</th>
            <th scope="col">级别排序</th>
            <th scope="col">对应领导</th>
            <th scope="col">操作</th>
          </tr>
          <c:forEach items="${list}" var="user" >
          <tr>
            <td>${user.userName}</td>
            <td>${user.realName}</td>
            <td>${user.mobile}</td>
            <td style="text-align:left;padding-left:10px;">${user.role.roleName}</td>
            <td>${user.disOrder}</td>
            <td>${user.leader.leaderName}</td>
            <td>
                <input type="button" class="del_btn" onclick="User.delUser('${user.userId}')"></input>
                <input type="button"  class="edit_btn" onclick="User.toEditUser('${user.userId}')"></input>            
            </td>
          </tr>
          </c:forEach>
        </table>
    </div>
</body>
</html>
