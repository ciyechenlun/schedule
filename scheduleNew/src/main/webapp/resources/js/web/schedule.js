
Schedule = function() {

	return {
		//判断是否多用户日程
		checkIsManyLeaderEvent:function(id){
			var isManyLeader = false;
			$.ajax({
				type: "POST",
				async:false,
				url: "/pc/schedule/checkIsManyLeaderEvent.htm",
				data: {'scheduleId' :id},
				success: function(data){
					if(data===true){
						isManyLeader =true;
					}
				}
			});
			return isManyLeader;
		},
		//验证添加事件是否为下周，只允许给下周添加事件
		checkNextWeek:function(start){
			var week = start.getDay();
			 if ( week == 0 )
			    {
				 week = 7;
			    }
		    var eventMonday = new Date(start-(week-1)*86400000);//获取事件所在周的周一
		    var now = new Date();
		    var curweek = now.getDay();
			 if ( curweek == 0 )
			    {
				 curweek = 7;
			    }
		    var nextmonday = new Date( now- (curweek-8)*86400000);  //获取当前时间的下周一
		    if(eventMonday.getYear() != nextmonday.getYear() ||
		    		eventMonday.getMonth() != nextmonday.getMonth() ||
		    		eventMonday.getDate() != nextmonday.getDate()){//判断是否为同一天，只允许给下周添加事件
		    	return false;
		    }
		    return true;
		},
		//获取下周一的年月日
		getNextWeek:function(){
			var now = new Date();
		    var curweek = now.getDay();
			 if ( curweek == 0 )
			    {
				 curweek = 7;
			    }
		    var nextmonday = new Date( now- (curweek-8)*86400000);  //获取当前时间的下周一
		    return {year:nextmonday.getFullYear(),month:nextmonday.getMonth(),date:nextmonday.getDate()};
		},
		changeSubmitState:function(){
			if($('#submit_btn').attr("disabled")=="disabled"){
				$('#submit_btn').attr("disabled", false).attr("style","")
				.removeClass("btn_edit").addClass("btn_safe").val("提交");
				$.post("/pc/schedule/changeCommitState.htm");
			}
		},
		//打开添加事件窗口
		toCreate:function(start,end,allDay){
		    if(!Schedule.checkNextWeek(start)){
		    	$('#calendar').fullCalendar('unselect');
		    	return;
		    }
		    //提交后仍可修改
		    /*if($('#submit_btn').attr('disabled')=='disabled'){
		    	$('#calendar').fullCalendar('unselect');
		    	return;
		    }*/
			$('#light').show();
			$('#fade').show();
			$('#title').val('');
			var start_str = new Date(start).format("M月d日 hh:mm");
			var end_str = new Date(end).format("M月d日 hh:mm");
			$('#start').text(start_str);
			$('#end').text(end_str);
			$('#start').attr("title",start);
			$('#end').attr("title",end);
		},
		//禁用输入框
		disableWindow:function(){
			$('#d_title').attr('disabled','disabled');
			$('#d_start').datetimebox('disable');
			$('#d_end').datetimebox('disable');
			$('#d_place').attr('disabled','disabled');
			$('#d_men').attr('disabled','disabled');
			$('#d_details').attr('disabled','disabled');
			$('#d_remark').attr('disabled','disabled');
		},
		//启用输入框
		enableWindow:function(){
			$('#d_title').attr('disabled',false);
			$('#d_start').datetimebox('enable');
			$('#d_end').datetimebox('enable');
			$('#d_place').attr('disabled',false);
			$('#d_men').attr('disabled',false);
			$('#d_details').attr('disabled',false);
			$('#d_remark').attr('disabled',false);
		},
		//简易编辑取消按钮
		cancelEasy:function(){
			$('#light').hide();
			$('#fade').hide();
		},
		//完整编辑取消按钮
		cancelDetail:function(){
			$('#light1').hide();
		},
		//修改日程时取消按钮
		cancelEdit:function(){
			$('#light1').hide();
			$('#fade').hide();
		},
		//保存简易日程
		saveEasy:function(){
			var title=$('#title').val();
			var start=$('#start').attr("title");
			var end=$('#end').attr("title");
			if(title == ''){
				$.messager.alert('提示','主题不能为空','info');
				return;
			}
			$.ajax({
				type: "POST",
				url: "/pc/schedule/saveEasy.htm",
				data: {'title' :title,'startTime':new Date(start).format("yyyy/MM/dd hh:mm:ss"),'endTime':new Date(end).format("yyyy/MM/dd hh:mm:ss")},
				success: function(data){
					if(data=="0"){
						$('#light').hide();
						$('#fade').hide();
						$('#calendar').fullCalendar('refetchEvents');
						Schedule.changeSubmitState();
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			}); 
		},
		//打开完整日程窗口
		toDetailEdit:function(){
			$('#light1').show();
			Schedule.enableWindow();
			$('#d_start').datetimebox('setValue',new Date($('#start').attr("title")).format("yyyy-MM-dd hh:mm:ss"));
			$('#d_end').datetimebox('setValue',new Date($('#end').attr("title")).format("yyyy-MM-dd hh:mm:ss"));
			var	leaderId = $('#leaderId').val();
			$(".chse li input").each(function(){
					var value = $(this).val();
					if(value==leaderId){
						$(this).attr("checked",true);
					}else{
						$(this).attr("checked",false);
					}
				  }); 
			//文本框内容初始化，以免填入不需要的值
			$('#scheduleId').val('');
			  $('#d_title').val($('#title').val());
			  $('#d_place').val('');
			  $('#d_men').val('');
			  $('#d_details').val('');
			  $('#d_remak').val('');
			  
			  $('#del_btn').hide();
			  //先解绑按钮事件后重新绑定
			$("#todo_btn").unbind().attr('style','margin-left:80px;');
			  $("#save_btn").unbind().attr('style','margin-left:80px;');
			  $("#can_btn").unbind().attr('style','margin-left:80px;');
			  $('#todo_btn').click(function(){
				  Schedule.saveToDo();
			  });
			  $('#save_btn').click(function(){
				  Schedule.saveDetail();
			  });
			  $('#can_btn').click(function(){
				  Schedule.cancelDetail();
			  });
		},
		//完整日程保存
		saveDetail:function(){
			var title=$('#d_title').val();
			var start = $('#d_start').datetimebox('calendar').calendar('options').current;
	    	var end = $('#d_end').datetimebox('calendar').calendar('options').current;
	    	var leader_num = $('[name="leaders"]:checked').length;
			$('#schedule_form').form('submit',{
				url:'/pc/schedule/saveDetail.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    	if(start>=end){
			    		$.messager.alert('提示','起始时间不能大于结束时间','info');
						return false;
			    	}
			    	if(!Schedule.checkNextWeek(start)){
			    		$.messager.alert('提示','时间不在下周范围内','info');
						return false;
			    	}
			    	if(leader_num<1){
			    		$.messager.alert('提示','请选择参与领导','info');
						return false;
			    	}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#light').hide();
						$('#fade').hide();
						$('#calendar').fullCalendar('refetchEvents');
						Schedule.changeSubmitState();
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		},
		//到编辑页
		toEdit:function(calEvent){
			$('#fade').show();
	        $('#light1').show();
			var id=calEvent.id;
			$.ajax({
				url: "/pc/schedule/getScheduleById.htm?scheduleId="+id,
				  success: function(data){
					$('#scheduleId').val(data.scheduleId);
					$('#d_title').val(data.title);
					$('#d_start').datetimebox('setValue',data.startTime);
					$('#d_end').datetimebox('setValue',data.endTime);
					$('#d_place').val(data.place);
					$('#d_men').val(data.men);
					$('#d_details').val(data.details);
					$('#d_remark').val(data.remark);
					var array = data.mark2.split(',');
					 $(".chse li input").each(function(){
						   $(this).attr("checked",false);
						  });  
					for(var i=0;i<array.length;i++){
						$('.chse li input[value='+array[i]+']').attr('checked',true);
					}
				  }
           });
			$('#del_btn').show();
			  //先解绑按钮事件后重新绑定
			$("#todo_btn").unbind().attr('style','margin-left:40px;');
			  $("#save_btn").unbind().attr('style','margin-left:40px;');
			  $("#can_btn").unbind().attr('style','margin-left:40px;');
			  $("#del_btn").unbind();
			 if(!Schedule.checkIsManyLeaderEvent(id)){
				 $("#del_btn").attr('disabled',false).css('color','#fff').removeClass('btn_edit').addClass('btn_add');
				 $("#todo_btn").attr('disabled',false).css('color','#fff').removeClass('btn_edit').addClass('btn_cancel');
				 $('#save_btn').click(function(){
					 Schedule.editDetail();
					  });
				 Schedule.enableWindow();
			 }else{
				 $("#del_btn").attr('disabled','disabled').css('color','#000000').removeClass('btn_add').addClass('btn_edit');
				 $("#todo_btn").attr('disabled','disabled').css('color','#000000').removeClass('btn_cancel').addClass('btn_edit');
				 $('#save_btn').click(function(){
					 Schedule.delLeaderFromSchedule();
					  });
				 Schedule.disableWindow();
			 }
			 $('#todo_btn').click(function(){
				  Schedule.editToDo();
			  });
			  $('#del_btn').click(function(){
				  Schedule.delSchedule();
			  });
			  $('#can_btn').click(function(){
				  Schedule.cancelEdit();
			  });
		},
		delSchedule:function(){
			var id=$('#scheduleId').val();
			$.messager.confirm('提示信息','确认删除?',function(ok){ 		
				if (ok){ 		
					$.ajax({
		 				  url: "/pc/todo/del.htm?scheduleId="+id,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
	 							$('#light1').hide();
	 							$('#fade').hide();
	 							$('#calendar').fullCalendar('refetchEvents');
	 							Schedule.changeSubmitState();
							}else{
								$.messager.alert("提示","删除失败",'info');
							}
		 				  }
		             });
				}
			});
		},
		delLeaderFromSchedule:function(){
			var id=$('#scheduleId').val();
			var leaderName =$('#leaderName').val();
			var leadId = $('#leaderId').val();//当前用户对应领导
			var checkLeader =$('[name="leaders"][value="'+leadId+'"]').attr('checked');
			if(checkLeader=='checked'){
				$('#light1').hide();
				$('#fade').hide();
				return;
			}
			$.messager.confirm('提示信息','确认将'+leaderName+'移出该事件?',function(ok){ 		
				if (ok){ 		
					$.ajax({
		 				  url: "/pc/schedule/delLeaderFromEvent.htm?scheduleId="+id,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
	 							$('#light1').hide();
	 							$('#fade').hide();
	 							$('#calendar').fullCalendar('refetchEvents');
	 							Schedule.changeSubmitState();
							}else{
								$.messager.alert("提示","操作失败",'info');
							}
		 				  }
		             });
				}
			});
		},
		//编辑日程
		editDetail:function(){

			var title=$('#d_title').val();
			var start = $('#d_start').datetimebox('calendar').calendar('options').current;
	    	var end = $('#d_end').datetimebox('calendar').calendar('options').current;
	    	var leader_num = $('[name="leaders"]:checked').length;
			$('#schedule_form').form('submit',{
				url:'/pc/schedule/editDetail.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    	if(start>=end){
			    		$.messager.alert('提示','起始时间不能大于结束时间','info');
						return false;
			    	}
			    	if(!Schedule.checkNextWeek(start)){
			    		$.messager.alert('提示','时间不在下周范围内','info');
						return false;
			    	}
			    	if(leader_num<1){
			    		$.messager.alert('提示','请选择参与领导','info');
						return false;
			    	}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#fade').hide();
						$('#calendar').fullCalendar('refetchEvents');
						Schedule.changeSubmitState();
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		
		},
		//编辑时存为待办
		editToDo:function(){
			var title=$('#d_title').val();
			$('#schedule_form').form('submit',{
				url:'/pc/todo/updateToDo.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#fade').hide();
						$('#calendar').fullCalendar('refetchEvents');
						Schedule.changeSubmitState();
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		},
		//存为待办
		saveToDo:function(){
			var title=$('#d_title').val();
			$('#schedule_form').form('submit',{
				url:'/pc/schedule/saveToDo.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#light').hide();
						$('#fade').hide();
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		
		},
		//提交日程
		submitSchedule:function(){
			$.messager.confirm('提示信息','确认提交?',function(r){ 		
				if (r){ 		
					$.ajax({
		 				  url: "/pc/schedule/submitSchedule.htm",
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						$.messager.alert("提示","提交成功",'info',function(){
			 						window.location.href="/pc/schedule/main.htm";
		 						});
							}else{
								$.messager.alert("提示","提交失败",'info');
							}
		 				  }
		             });
				}
			});
		},
		dragEvent:function(event){
			$.ajax({
				type: "POST",
				url: "/pc/schedule/dragSchedule.htm",
				data: {scheduleId:event.id,'startTime':event.start.format("yyyy/MM/dd hh:mm:ss"),'endTime':event.end.format("yyyy/MM/dd hh:mm:ss")},
				success: function(data){
					if(data=="1"){
						$.messager.alert('提示','保存失败','info');
						
					}else if(data==""){
						$('#calendar').fullCalendar('refetchEvents');
						Schedule.changeSubmitState();
					}else{
						$.messager.alert('提示','"'+data+'"'+'因时间冲突存为待办，可在待办日程中查看到','info',function(){
							$('#calendar').fullCalendar('refetchEvents');
							Schedule.changeSubmitState();
						});
						
					}
				}
			}); 
		}
	};

}();

$(document).ready(function() {
	//提交后仍可修改
	//var hasSubmit = $('#submit_btn').attr('disabled')=='disabled';
	var nextmonday=Schedule.getNextWeek();
    var year = nextmonday.year;
    var month=nextmonday.month;
    var date =nextmonday.date;
	//日程安排插件加载
	var calendar = $('#calendar').fullCalendar({
		header: {
			left: '',
			center: 'prev,title,next',
			right: ''
		},
		selectable: true,
		selectHelper: true,
		select: function(start, end, allDay) {
			Schedule.toCreate(start,end,allDay);//添加事件
		},
		titleFormat: {
			week: "M月d日[ yyyy]{ '&#8212;' M月d日}"
		},
		year:year,
		month:month,
		date:date,
		editable: true,
		//disableDragging:hasSubmit?true:false,
		//disableResizing:hasSubmit?true:false,
		firstDay:1,
		aspectRatio: 1.45,
		allDaySlot:false,
		axisFormat:'HH:mm',
		timeFormat: { 
			agenda: 'HH:mm{ - HH:mm}'
		}, 
		firstHour: 8,
		minTime:'08:00',
		maxTime:'19:00',
		events: '/pc/schedule/loadEvent.htm',
		eventClick:function(calEvent, jsEvent, view){//事件单击可编辑
			if(Schedule.checkNextWeek(calEvent.start)){
				Schedule.toEdit(calEvent);
		    }
		},
		eventDrop:function(event, dayDelta, minuteDelta, allDay, ev, ui){
			if(Schedule.checkNextWeek(event.start)){
				//非多用户日程
				if(!Schedule.checkIsManyLeaderEvent(event.id)){
					Schedule.dragEvent(event);
				}else{
					$.messager.alert('提示','多领导日程秘书不能拖拽','info');
					ev();
				}
			}else{
				ev(); //恢复原状 
			}
		},
		eventResize:function( event, dayDelta, minuteDelta, ev, ui){
			if(Schedule.checkNextWeek(event.start)){
				//非多用户日程
				if(!Schedule.checkIsManyLeaderEvent(event.id)){
					Schedule.dragEvent(event);
				}else{
					$.messager.alert('提示','多领导日程秘书不能拖拽','info');
					ev();
				}
			}else{
				ev(); //恢复原状 
			}
		}
	});
	//calendar.fullCalendar('next');//默认显示下周
	$('.fc-header-title h2').addClass('caltitle');//日程表头样式修改
	/*$.ajax({
		url: "/pc/schedule/getHoliday.htm",
		  success: function(data){
			  for(var i=0;i<data.length;i++){
				  var day =data[i].day;
				  var type=data[i].type;
				  var name= data[i].holidayName;
				  var el = $(".fc-agenda-days thead tr").find('[title="'+day+'"]');
				  if(name){
					 el.text(name+el.text().substring(3));
				  }
				  if(type=='2'){
					  el.append('<font color=red> (休)</font>');
				  }else if(type=='0'){
					  if(el.hasClass('fc-sat')||el.hasClass('fc-sun')){
						  el.append('<font color=red> (班)</font>'); 
					  }else{
						  //
					  }
				  }
				  
			  }
		  }
   });*/
});
