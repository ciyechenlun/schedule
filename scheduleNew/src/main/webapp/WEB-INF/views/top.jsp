<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/resources/js/header.js"></script>
<script src='/resources/scripts/dateformat.js'></script>
<%@include file="common/baseIncludeJs.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<div class="header">
   	<div class="photo left"><img src="/resources/images/head_photo.png" /></div>
       <div style="padding:10px 70px 0 177px;">
       	<div>
           	<span class="left white">欢迎您，${user.realName }，今天是<time id="now"></time></span>
               <span class="right">
               <a href="javascript:void(0)" onclick="Header.changePwd()">[修改密码]</a>
               <a href="javascript:void(0)" onclick="Header.logout()">[退出]</a></span>
           </div>
           <div class="point" style="margin-top:30px;">
           	<div class="left point_left"></div>
               <div style=" padding:5px 15px;">
               	<div class="w_line"></div>
               </div>               
           </div>
       </div>
   </div>
   <div id="light" class="window_content" >
        	<div class="windowHeader"></div> 
        	<div class="windowCont clearfix">
              <ul>
                    <li>
                        <label>旧密码：</label>
                        <input id="oldPassword" name="oldPassword" type="password" required="required" />
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>新密码：</label>
                         <input id="newPassword" name="newPassword" type="password" required="required"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>确认新密码：</label>
                         <input id="newPassword2" name="newPassword2" type="password" required="required"/>
                        <span  class="red">*</span>
                    </li>
                </ul>
                <div class="window_btn">
                    <input type="button" value="保存" class="btn_safe" style="margin-left:90px;"  onclick="Header.savePassword();"/>
                     <input type="button" value="取消" class="btn_red"  style="margin-left:90px;" onclick="$('#light').hide()"/>
                </div>
        	</div>
        	</div>