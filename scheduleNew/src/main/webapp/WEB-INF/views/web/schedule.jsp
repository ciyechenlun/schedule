<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>下周日程拟定</title>
<%@include file="../common/baseIncludeJs.jsp" %>
<link href='/resources/scripts/fullcalendar-1.6.4/fullcalendar/fullcalendar.css' rel='stylesheet' />
<link href='/resources/scripts/fullcalendar-1.6.4//fullcalendar/fullcalendar.print.css' rel='stylesheet' media='print' />
<script src='/resources/scripts/fullcalendar-1.6.4//lib/jquery-ui.custom.min.js'></script>
<script src='/resources/scripts/fullcalendar-1.6.4//fullcalendar/fullcalendar.min.js'></script>
<script src='/resources/scripts/dateformat.js'></script>
<script type="text/javascript" src="/resources/js/web/schedule.js"></script>
<style>

	body {
		margin-top: 20px;
		font-size: 14px;
		font-family: "Lucida Grande",Helvetica,Arial,Verdana,sans-serif;
		}

	#calendar {
		width: 930px;
		margin: 0 auto;
		}
		table{
		border-collapse:separate
		}
		.fc-event-inner{
			top:30%;
			margin-top:-8px;
		}
</style>
</head>

<body >
    <!--蒙版-->
    <div id="fade" class="black_overlay">123</div>
    <!--蒙版结束-->
    <input type="hidden" id="leaderName" value="${leader.leaderName}"/>
    <input type="hidden" id="leaderId" value="${leader.leaderId}"/>
   <div class="nextweek">
        <p class="left">你负责为 <span>${leader.leaderName}</span> 设定日程</p>
        <input id="submit_btn" <c:if test="${commit eq true}">disabled class="btn_edit left" style="color:#000000"  value="已提交"</c:if><c:if test="${commit ne true}">class="btn_safe left" value="提交"</c:if> type="button"  onclick="Schedule.submitSchedule()"/>
    </div>
       <div id='calendar'></div>
            <div id="light" class="window_content" >
        	<div class="windowHeader"></div> 
        	<div class="windowCont clearfix">
              <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="title" name="title"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>时间：</label>
                        <span id="start" title=""></span>&nbsp;-&nbsp;
                        <span id="end" title=""></span>
                    </li>
                </ul>
                <div class="window_btn">
                    <input type="button" value="完整编辑" class="btn_cancel" style="margin-left:50px;" id="b_edit" onclick="Schedule.toDetailEdit()"/>
                    <input type="button" value="保存" class="btn_safe" style="margin-left:50px;"  onclick="Schedule.saveEasy()"/>
                     <input type="button" value="取消" class="btn_red"  style="margin-left:50px;" onclick="Schedule.cancelEasy()"/>
                </div>
        	</div>
        	</div>
        	  <!--完整编辑弹窗-->
            <div id="light1" class="w_cont" >
        		<div class="wHeader"></div> 
        		<div class="wCont clearfix">
        		<form id="schedule_form" method="post">
        		<input type="hidden" id="scheduleId" name="scheduleId"/>
             	 <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="d_title" name="title"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>时间：</label>
                        <input id="d_start" class="easyui-datetimebox" data-options="required:true,editable:false" name="startTime" type="text" style="width:150px;" />&nbsp;&nbsp;至&nbsp;
                        <input id="d_end" class="easyui-datetimebox" data-options="required:true,editable:false" name="endTime" type="text" style="width:150px;" />
                        <span class="red" style="margin-left:20px;"></span>
                    </li>
                    <li>
                        <label>地点：</label>
                        <input id="d_place" type="text"  name="place"/>
                    </li>
                    <li>
                        <label>参与领导：</label>
                        <ul class="chse">
                        <c:forEach items="${leaders}" var="lead" >
                            <li><input <c:if test="${leader.leaderId ne lead.leader_id}">disabled="disabled" </c:if> type="checkbox" name="leaders" style=" width:10px; height:10px;" value="${lead.leader_id }"/>${lead.leader_name}</li>
                        </c:forEach>
                        </ul>
                    </li>
                    <li>
                        <label>参与人(非领导)：</label>
                        <input type="text" id="d_men"  name="men" placeholder="如数据业务部，政企客户部"/>
                    </li>
                    <li>
                        <label>日程详情：</label>
                        <textarea  style="" id="d_details" name="details"></textarea>
                    </li>
                    <li>
                        <label>备注：</label>
                        <input type="text" id="d_remark"  name="remark"/>
                    </li>
                 </ul>
                 <div class="w_btn">
                    <input id="todo_btn" type="button" value="存为待办" class="btn_cancel"style="margin-left:40px;"/>
                    <input id="save_btn" type="button" value="保存" class="btn_safe" style="margin-left:40px;"  />
                    <input id="del_btn" type="button" value="删除" class="btn_add" style="margin-left:40px;" />
                    <input id="can_btn" type="button" value="取消" class="btn_red"  style="margin-left:40px;" />
                </div>
                </form>
        	</div>  
   		 </div>
        	
</body>
</html>
