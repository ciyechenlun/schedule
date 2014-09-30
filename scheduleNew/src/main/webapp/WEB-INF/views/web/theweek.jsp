<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>本周日程</title>
<%@include file="../common/baseIncludeJs.jsp" %>
<script type="text/javascript" src="/resources/scripts/jquery-ui.min.js"></script>
<script src='/resources/scripts/dateformat.js'></script>
<script type="text/javascript" src="/resources/js/web/theweek.js"></script>
<style type="text/css">
body{padding:10px 20px;}
.showsc{ width:470px;display:none;position: absolute; z-index: 9002;}
.showsc li{  text-overflow: ellipsis;
	overflow: hidden;
    white-space: nowrap;
    width: 440px;}
.calendar-body table{
	border-collapse:separate
}
</style>
</head>

<body >
<div class="theweek" style="height:28px;">
<input type="hidden" id="change_flag" value=""/>
<input type="hidden" id="release_tag" value="${release_tag}"/>
<input type="hidden" id="role_id" value="${user.role.roleId}"/>
<input type="hidden" id="leaderName" value="${leader.leaderName}"/>
<input type="hidden" id="leaderId" value="${leader.leaderId}"/>
    <!--蒙版-->
    <div id="fade" class="black_overlay">123</div>
    <!--蒙版结束-->
    <c:if test="${user.role.roleId eq '1' || user.role.roleId eq '2'|| user.role.roleId eq '3'}">
        <input id="print_btn" type="button" value="打印"  class="btn_cancel" onclick="Theweek.print()"/>
        <input id="export_btn" type="button" value="导出" class="btn_add" onclick="Theweek.exportExcel()"/>
        <input id="to_add_btn" style="display:none" type="button" value="添加"  class="btn_safe" onclick="Theweek.toAddDetail()"/>
        <input id="edit_release_btn" type="button" value="编辑" class="btn_safe"/>
   </c:if>
   <c:if test="${user.role.roleId eq '1' || user.role.roleId eq '2'}">
   		<c:if test="${release_tag eq '0'}">
   			<input id="release_a" type="button" value="  发布" class="btn_release" onclick="javascript:Theweek.releaseChange()" title="本周日程有更新，请审核后发布"/>
   		</c:if>
   </c:if>
    </div>
    <div id="print_area" class="ca">
    	<div class="caltitle"><${day0}-${day6}></div>
        <!--日历-->
        <div class="calendar1">
        	<table width="99%" border="1" cellspacing="0" cellpadding="0" align="center" style="text-align:center;" >
              <tr style="height:48px;" class="tr_date">
                <th rowspan="2" class="col_b w10">公司领导</th>
                <th colspan="2" class="col_b" title="${date0}">${day0}（星期一）</th>
                <th colspan="2" class="col_b" title="${date1}">${day1}（星期二）</th>
                <th colspan="2" class="col_b" title="${date2}">${day2}（星期三）</th>
                <th colspan="2" class="col_b" title="${date3}">${day3}（星期四）</th>
                <th colspan="2" class="col_b" title="${date4}">${day4}（星期五）</th>
                <th colspan="2" class="col_b" title="${date5}">${day5}（星期六）</th>
                <th colspan="2" class="col_b" title="${date6}">${day6}（星期天）</th>
              </tr>
              <tr style="height:40px;">
               <td class="col_b" width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                 <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
                 <td class="col_b " width="96">上午</td>
                <td class="col_b " width="96">下午</td>
              </tr>
              <c:forEach items="${leaders}" var="leader" >
	              <tr id="${leader.leader_id}" class="mark">
	                <th>${leader.leader_name}</th>
	                <td>
	                </td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	                <td></td>
	              </tr>
              </c:forEach>
            </table>
			<div id="event_div" style="left: 0px; top: 23px; width: 100%; position: absolute; z-index: 2;">
			<input type="hidden" id="source_position"/>
			</div>
        </div>

    </div>
        <div id="showsc" class="showsc" >
        <div class="wind right clearfix">
        	<input type="hidden" id="scheduleId"/>
        	<input type="hidden" id="s_type"/>
        	<ul>
            	<li class="blue" style="font-weight:bold;">
                	<label>主题：</label>
                    <span id="title"></span>
                    <img src="/resources/images/row.png" style="float:left;margin-left:-112px; margin-top:10px;"/>
                </li>
                <li class="back_wi">
                	<label>开始时间：</label>
                    <span id="startTime"></span>&nbsp;&nbsp;&nbsp;
                    <span>结束时间：</span>
                    <span id="endTime"></span>
                </li>
                <li class="blue">
                	<label>参与领导：</label>
                    <span id="leaders"></span>
                </li>
                <li class="back_wi">
                	<label>详情：</label>
                    <span id="details"></span>
                </li>
                <li class="blue">
                	<label>备注：</label>
                    <span id="remark"></span>
                </li>
            </ul>
            <div class="window_btn blue">
            	<input id="edit_btn" type="button" value="编辑" class="btn_safe" />
            	<input id="del_btn" type="button" value="删除" class="btn_red" onclick="Theweek.del()"/>
            </div>
        </div>
        </div>
   		 <div id="light2" class="window_content" >
        	<div class="windowHeader">添加日程</div> 
        	<div class="windowCont clearfix">
              <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="title2" value="" />
                    </li>
                    <li>
                        <label>开始时间：</label>
                        <span id="start2" title="">4月7日  08:45</span>&nbsp;&nbsp;&nbsp;
                        <span>结束时间：</span>
                        <span id="end2" title="">4月7日  10:45</span>
                    </li>
                    <li>
                        <label>参与领导：</label>
                        <ul class="choose">
                          <c:forEach items="${leaders}" var="leader" >
                            <li><input type="checkbox" name="s_leader" style=" width:10px; height:10px;" value="${leader.leader_id }"/>${leader.leader_name}</li>
                        </c:forEach>
                        </ul>
                    </li>
                </ul>
                <div class="window_btn">
                    <input type="button" value="完整编辑" class="btn_cancel" style="margin-left:50px;" onclick="Theweek.toAddDetail()" />
                    <input type="button" value="保存" class="btn_safe" style="margin-left:50px;" onclick="Theweek.saveEasy()" />
                    <input type="button" value="取消" class="btn_red" style="margin-left:50px;" onclick="Theweek.cancelAdd()"/>
                </div>
        	</div> 
    </div>
            	  <!--完整编辑弹窗-->
            <div id="light1" class="w_cont" >
        		<div class="wHeader"></div> 
        		<div class="wCont clearfix">
        		<form id="schedule_form" method="post">
        		<input type="hidden" id="scheduleId1" name="scheduleId"/>
             	 <ul>
                    <li>
                        <label>主题：</label>
                        <input type="text" id="title1" name="title"/>
                        <span  class="red">*</span>
                    </li>
                    <li>
                        <label>时间：</label>
                        <input id="start1" class="easyui-datetimebox" data-options="editable:false" name="startTime" type="text" style="width:150px;" />&nbsp;&nbsp;至&nbsp;
                        <input id="end1" class="easyui-datetimebox" data-options="editable:false" name="endTime" type="text" style="width:150px;" />
                        <span class="red" style="margin-left:20px;"></span>
                    </li>
                    <li>
                        <label>地点：</label>
                        <input type="text" id="place1"  name="place"/>
                    </li>
                    <li>
                        <label>参与领导：</label>
                        <ul class="chse">
                        <c:forEach items="${leaders}" var="lead" >
                            <li><input <c:if test="${user.role.roleId eq '3' && leader.leaderId ne lead.leader_id}">disabled="disabled" </c:if> type="checkbox" name="leaders" style=" width:10px; height:10px;" value="${lead.leader_id }"/>${lead.leader_name}</li>
                        </c:forEach>
                        </ul>
                    </li>
                    <li>
                        <label>参与人(非领导)：</label>
                        <input type="text" id="men1"  name="men" placeholder="如数据业务部，政企客户部"/>
                    </li>
                    <li>
                        <label>日程详情：</label>
                        <textarea  style="" id="details1" name="details"></textarea>
                    </li>
                    <li>
                        <label>备注：</label>
                        <input type="text" id="remark1"  name="remark"/>
                    </li>
                 </ul>
                 <div class="w_btn">
                    <input id="save_btn" type="button" value="保存" class="btn_safe"  />
                    <input id="can_btn" type="button" value="取消" class="btn_red"  />
                </div>
                </form>
        	</div>  
   		 </div>
</body>
</html>
