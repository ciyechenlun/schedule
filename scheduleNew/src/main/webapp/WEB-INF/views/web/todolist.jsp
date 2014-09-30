<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>待办任务</title>
<%@include file="../common/baseIncludeJs.jsp" %>
<script src='/resources/scripts/dateformat.js'></script>
<script type="text/javascript" src="/resources/js/web/todolist.js"></script>
<style type="text/css">
body{padding:10px 20px;}
.calendar-body table{
	border-collapse:separate
}
</style>
</head>

<body >
    <!--蒙版-->
    <div id="fade" class="black_overlay">123</div>
    <!--蒙版结束-->
            	  <!--新增弹窗-->
            <div id="light1" class="window_content">
        	<div class="windowHeader"></div> 
        	<div class="windowCont clearfix">
        		<form id="todo_form" method="post">
             	 <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="d_title" name="title"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>地点：</label>
                        <input type="text"  name="place"/>
                    </li>
                    <li>
                        <label>参与人：</label>
                        <input type="text"  name="men"/>
                    </li>
                    <li>
                        <label>详情：</label>
                        <textarea  style="" name="details"></textarea>
                    </li>
                    <li>
                        <label>备注：</label>
                        <input type="text"  name="remark"/>
                    </li>
                 </ul>
                 <div class="window_btn">
	            <input type="button" value="保存" class="btn_safe"  onclick="ToDo.save()" />
	            <input type="button" value="取消" class="btn_red"  onclick="ToDo.cancel()" />
	            </div>
                </form>
        	</div>  
   		 </div>
   		   <!--完整编辑弹窗-->
            <div id="light2" class="w_cont" >
        		<div class="wHeader"></div> 
        		<div class="wCont clearfix">
        		<form id="todo_edit_form" method="post">
        		 <input type="hidden" id="scheduleId" name="scheduleId"/>
             	 <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="e_title" name="title"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>时间：</label>
                        <input id="e_start" class="easyui-datetimebox" data-options="editable:false" name="startTime" type="text" style="width:150px;" />&nbsp;&nbsp;至&nbsp;
                        <input id="e_end" class="easyui-datetimebox" data-options="editable:false" name="endTime" type="text" style="width:150px;" />
                    </li>
                    <li>
                        <label>地点：</label>
                        <input type="text" id="place"  name="place"/>
                    </li>
                    <li>
                        <label>参与人：</label>
                        <input type="text" id="men" name="men"/>
                    </li>
                    <li>
                        <label>日程详情：</label>
                        <textarea  id="details" name="details"></textarea>
                    </li>
                    <li>
                        <label>备注：</label>
                        <input type="text" id="remark"  name="remark"/>
                    </li>
                 </ul>
                 <div class="w_btn">
                    <input type="button" value="存为日程" class="btn_cancel"style="margin-left:80px;"   onclick="ToDo.saveSchedule()"/>
                    <input type="button" value="存为待办" class="btn_safe" style="margin-left:80px;"  onclick="ToDo.updateToDo()" />
                    <input type="button" value="取消" class="btn_red"  style="margin-left:80px;" onclick="ToDo.cancelEdit()"/>
                </div>
                </form>
        	</div>  
   		 </div>
	<div class="list_title">
        <input type="button" value="添加待办" class="btn_add left" onclick="ToDo.toCreate()"/>
        <span>待办日程</span>
    </div>
    <div class="list">
    	<ul>
    	  <c:forEach items="${list}" var="todo">
        	<li>
            	<div class="mtop"></div>
                <div class="mcont">
                	<p>${todo.title}</p>
                    <p style="margin-top:10px;">${todo.men}</p>
                    <input type="button" value="编辑" class="btn_edit left" onclick="ToDo.toEdit('${todo.schedule_id}')"/>
                    <input type="button" value="删除" class="btn_delete right" onclick="ToDo.del('${todo.schedule_id}')"/>
                </div>
            </li>
		</c:forEach>
        </ul>
    </div>
</body>
</html>
