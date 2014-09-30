
Theweek = function() {

	return {
		init:function(isChange){
			$.ajax({
				type: "POST",
				async:false,
				url: isChange?"/pc/theweekedit/getThisWeekSchedule.htm":"/pc/theweek/getThisWeekSchedule.htm",
				success: function(data){
					Theweek.loadData(data);
				}
			});
			if(isChange){
				var roleId = $('#role_id').val();
				if(roleId != '3'){
					$('.event_self').draggable({
						containment: [134,166,1472,625],
						//grid: [1, 80],
						distance:20,
						//axis:'x',
						 //helper: 'clone' ,
						//addClasses:true,
						zIndex: 1 ,
						//stack: ".event_self",
						//cursor:"move",
						cursorAt: {left:0,top: 0},
						start:function(event,ui) {
							var o = $(this).position();
							var w = $(this).width();
							var h = $(this).height();
							$('#source_position').val(o.top+','+o.left+','+w+','+h);
							$(this).width(20).height(20).find('a').hide();
							
						},
						stop:function(event,ui) {
							Theweek.stopDrag(this);
						}
					});
				}
				$('.mark td').click(function(){
					var leaderId = $(this).parent().attr('id');//点击表格的领导
					var leadId = $('#leaderId').val();//当前用户对应领导
					var space = $(this).index();
					var space1 = parseInt((space+1)/2);
					var day = $('.tr_date th:eq('+space1+')').attr('title');
					var start ="";
					var end ="";
					if(space%2==0){
						start = day+' '+'14:15:00';
						end = day+' '+'18:00:00';
					}else{
						start = day+' '+'08:45:00';
						end = day+' '+'12:00:00';
					}

					if(roleId=='3'){
						if(leadId == leaderId){
							//Theweek.toAddDetail(start,end,leaderId);
							$(this).html('<a id="add_a_img" href=\'javascript:Theweek.toAddDetail("'+start+'","'+end+'","'+leaderId+'")\'><img src="/resources/images/btn_tj.png" alt="添加" /></a>');
							$(this).mouseleave(function(){
								$('#add_a_img').remove();
							});
						}
					}else{
						//Theweek.toAddDetail(start,end,leaderId);
						$(this).html('<a id="add_a_img" href=\'javascript:Theweek.toAddDetail("'+start+'","'+end+'","'+leaderId+'")\'><img src="/resources/images/btn_tj.png" alt="添加" /></a>');
						$(this).mouseleave(function(){
							$('#add_a_img').remove();
						});
					}
				});
			}
			$("#event_div div").click(function(e){
				var maginBottom = $(window).height()+$(window).scrollTop()-e.pageY;
				var marginRight = $(window).width()+$(window).scrollLeft()-e.pageX;
				var y=e.pageY-30;
				var x=e.pageX+10;
				if(maginBottom<226){
					y=$(window).height()+$(window).scrollTop()-226;
				}
				if(marginRight<460){
					x=$(window).width()+$(window).scrollLeft()-460;
				}
			    $('#showsc').attr('style','left:'+x+'px;top:'+y+'px').show();
			    if(!isChange){
			    	$('#del_btn').hide();
			    	$('#edit_btn').hide();
			    }
			    var scheduleId=$(this).find('a').attr('id');
				$.ajax({
					type: "POST",
					cache: true, 
					url: isChange?"/pc/theweekedit/getScheduleById.htm?scheduleId="+scheduleId
							:"/pc/audit/getScheduleById.htm?scheduleId="+scheduleId,
					success: function(data){
						$('#title').text(data.title);
						$('#s_type').val(data.type);
						$('#scheduleId').val(data.scheduleId);
						$('#startTime').text(new Date(data.startTime.replace(/-/g,"/")).format('M月d日 hh:mm'));
						$('#endTime').text(new Date(data.endTime.replace(/-/g,"/")).format('M月d日 hh:mm'));
						$('#leaders').text(data.mark1);
						$('#details').text(data.details?data.details:'');
						$('#remark').text(data.remark?data.remark:'');
						if(isChange){
							var roleId = $('#role_id').val();
							if(roleId=='3'){
								var leaderName =$('#leaderName').val();
								if(data.mark1==leaderName){
									$('#edit_btn').show();
									$('#del_btn').show();
								}else if(data.mark1.indexOf(leaderName)>-1){
									$('#edit_btn').show();
									$('#del_btn').hide();
								}else{
									$('#edit_btn').hide();
									$('#del_btn').hide();
								}
							}else{
								$('#edit_btn').show();
								$('#del_btn').show();
							}
						}
						
					}
				});
			  });
			$("#event_div div").mouseleave(function(){
			    $('#showsc').hide();
			  });
			$("#showsc").mouseenter(function(){
			    $('#showsc').show();
			  });
			$("#showsc").mouseleave(function(){
			    $('#showsc').hide();
			  });
			$('#edit_btn').unbind();
			$('#edit_btn').click(function(){
				Theweek.toEdit(isChange);
			});
		},
		stopDrag:function(el){
			var o = $(el).position();
			var x =o.left;
			var y =o.top;
			var s_id=$(el).find('a').attr('id');
			var text =$('#source_position').val().split(',');
			var s_top = parseInt(text[0]);
			if(y<s_top||y>s_top+80){
				Theweek.dragCopy(s_id,y);
				return;
			}
			var isCoin=false;//是否有重合
			$(".event_self").each(function(){
				var x0 = $(this).position().left;
				var y0=$(this).position().top;
				var x1 = x0+$(this).width();
				var y1=y0+$(this).height();
				var flag=false;//是否是自己
				if($(this).height()==20){
					x0=parseInt(text[1]);
					y0=parseInt(text[0]);
					x1 = x0+parseInt(text[2]);
					y1=y0+parseInt(text[3]);
					flag=true;
				}
				if(s_top ==y0){//水平移动
					if(x>=x0&&x<=x1&&y>=y0&&y<=y1){
						isCoin =true;
						if(flag){
							Theweek.initLoad();
							return;
						}else{
							var d_id=$(this).find('a').attr('id');
							Theweek.dragCover(s_id,d_id,y);
							return;
						}
					}
				}
				
			});
			if(!isCoin){
				Theweek.dragUpdate(s_id,x,y);
			}
		},
		initLoad:function(flag){
			$('#event_div').empty();
			$('#event_div').append('<input type="hidden" id="source_position"/>');
			var changeFlag = $('#change_flag').val();
			if(changeFlag!='true'&&flag && flag=='success'){
				$('#change_flag').val('true');
				$.post('/pc/auditmodify/addChangeFlag.htm');
			}
			Theweek.init(true);
		},
		confirmTodo:function(titles,confictIds,scheduleId,leaderId){
			$.messager.confirm('提示信息','因时间冲突，系统将会把'+'"'+titles+'"'+'事件替换并放置于待办日程，请确认?',function(ok){ 		
				if (ok){ 		
					$.ajax({
		 				  url: "/pc/auditmodify/completeCopy.htm",
		 				  data:{"confictIds":confictIds,"scheduleId":scheduleId,"leaderId":leaderId},
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						Theweek.initLoad('success');
							}else{
								$.messager.alert("提示","操作失败",'info',function(){
									Theweek.initLoad();
		 						});
							}
		 				  }
		             });
				}else{
					Theweek.initLoad();
				}
			});
		},
		dragCopy:function(scheduleId,top){
			var leaderId = $("tr:eq("+parseInt(((top-143)/80)+2)+")").attr('id');
			$.ajax({
				  type: "POST",
				  url: "/pc/theweekedit/dragCopy.htm",
				  data: {'scheduleId' :scheduleId,'leaderId' :leaderId},
				  success: function(data){
					  var result = data.result;
					  var titles = data.titles;
					  var confictIds = data.confictIds;
					  if(result=="1"){
							$.messager.alert('提示','操作失败','info');
					  }else if(result=="0"){
						  Theweek.initLoad('success');
					  }else if(result=="2"){
						  Theweek.confirmTodo(titles,confictIds,scheduleId,leaderId);
					  }else{
						  Theweek.initLoad();
					  }
				  }
           });
		},
		dragCover:function(s_id,d_id,top){
			var leaderId = $("tr:eq("+parseInt(((top-143)/80)+2)+")").attr('id');
			$.ajax({
				  type: "POST",
				  url: "/pc/theweekedit/dragCover.htm",
				  data: {'sId' :s_id,'dId' :d_id,'leaderId':leaderId},
				  success: function(data){
					  if(data=="1"){
							$.messager.alert('提示','操作失败','info');
							
						}else if(data==""){
							Theweek.initLoad('success');
						}else if(data=="2"){
							$.messager.alert('提示','多用户事件移动时发生时间冲突','info',function(){
								Theweek.initLoad();
							});
						}else if(data=="LOCK"){
							$.messager.alert('提示','其他用户正在操作','info',function(){
								Theweek.initLoad();
							});
						}else{
							$.messager.alert('提示','"'+data+'"'+'因时间冲突存为待办，发布后可在待办日程中查看到','info',function(){
								Theweek.initLoad('success');
							});
						}
				  }
         });
		},
		dragUpdate:function(scheduleId,left,top){
			var leaderId = $("tr:eq("+parseInt(((top-143)/80)+2)+")").attr('id');
			var space = parseInt((left-134)/97);
			var space1 = parseInt((space+2)/2);
			var day =$('.tr_date th:eq('+space1+')').attr('title');
			var start ='';
			if(space%2==0){
				start = day+' '+'08:45:00';
			}else{
				start = day+' '+'14:15:00';
			}
			$.ajax({
				  type: "POST",
				  url: "/pc/theweekedit/dragUpdate.htm",
				  data: {'scheduleId' :scheduleId,'start' :start,'leaderId':leaderId},
				  success: function(data){
					  if(data=="1"){
							$.messager.alert('提示','操作失败','info');
							
						}else if(data==""){
							Theweek.initLoad('success');
						}else if(data=="2"){
							$.messager.alert('提示','多用户事件移动时发生时间冲突','info',function(){
								Theweek.initLoad();
							});
						}else{
							$.messager.alert('提示','"'+data+'"'+'因时间冲突存为待办，发布后可在待办日程中查看到','info',function(){
								Theweek.initLoad('success');
							});
						}
				  }
           });
		},
		loadData:function(data){
			var td_width = $('.mark td').width();
			td_width =96;
			for(var i=0;i<data.length;i++){
				var id= data[i].leader_id;
				var start = data[i].start_time;
				var end = data[i].end_time;
				var title=data[i].title;
				var schedule_id=data[i].schedule_id;
				var startDate = new Date(start.replace(/-/g,"/"));
				var endDate = new Date(end.replace(/-/g,"/"));
				var startTime = startDate.format('hh:mm');
				var endTime = endDate.format('hh:mm');
				var space =Theweek.getSpace(start,true);
				var end_space= Theweek.getSpace(end,false);
				var nextCount=1;
				var endCount=1;
				var preCount=0;
				for(var j=i+1;j<data.length;j++){
					if(id != data[j].leader_id){
						break;
					}
					if(space == Theweek.getSpace(data[j].start_time,true)){
						nextCount++;
					}
					if(end_space == Theweek.getSpace(data[j].start_time,true)){
						endCount++;
					}
				}
				for(var x=i-1;x>=0;x--){
					if(id != data[x].leader_id){
						break;
					}
					if(space == Theweek.getSpace(data[x].end_time,false)){
						preCount++;
					}
				}
				var count= preCount+nextCount;
				var top = ($('#'+id).index()-2)*80+143;
				var left = 134+td_width*space+(preCount/count)*td_width+parseInt(space);
				var width=0;
				if(nextCount>1){
					width=(1/count)*td_width;
				}else{
					if(space==end_space){
						width=(1/count)*td_width;
					}else{
						width=(1/count+1/endCount)*td_width+(end_space-space-1)*(td_width+1)+1;
					}
				}
				$('#event_div').append('<div class="event_self" style="position: absolute;top:'+top+'px;left:'+left+'px;width:'+width+'px;height:80px;"><a id="'+schedule_id+'" value="'+endTime+'">'+startTime+'<br/>'+title+'</a></div>');
				
			}
		
		},
		getSpace:function(date,isStart){
			var flagDate = new Date(date.replace(/-/g,"/"));
			var day = flagDate.getDay();
			var h =flagDate.getHours();
			var m =flagDate.getMinutes();
			var space ='0';
			var h_flag=true;
			if(isStart){
				h_flag=h<12;
			}else{
				h_flag=h<12||(h==12&&m==0);
			}
			switch(day){
				case 1:
					space=h_flag?'0':'1';
					break;
				case 2:
					space=h_flag?'2':'3';
					break;
				case 3:
					space=h_flag?'4':'5';
					break;
				case 4:
					space=h_flag?'6':'7';
					break;
				case 5:
					space=h_flag?'8':'9';
					break;
				case 6:
					space=h_flag?'10':'11';
					break;
				case 0:
					space=h_flag?'12':'13';
					break;
				default:
					space='0';
			}
			return space;
		},
		del:function(){
			var id=$('#scheduleId').val();
			var type = $('#s_type').val();
			$.messager.confirm('提示信息','确认删除?',function(ok){ 		
				if (ok){ 		
					$.ajax({
		 				  url: "/pc/theweekedit/del.htm?scheduleId="+id+"&type="+type,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						Theweek.initLoad('success');
							}else{
								$.messager.alert("提示","删除失败",'info',function(){
									Theweek.initLoad();
		 						});
							}
		 				  }
		             });
				}
			});
		},
		toEdit:function(isChange){
			$('#showsc').hide();
			$('#fade').show();
			$('#light1').show();
			var id=$('#scheduleId').val();
			$.ajax({
				url: isChange?"/pc/theweekedit/getScheduleById.htm?scheduleId="+id
						:"/pc/audit/getScheduleById.htm?scheduleId="+id,
				  success: function(data){
					$('#scheduleId1').val(data.scheduleId);
					$('#title1').val(data.title);
					$('#start1').datetimebox('setValue',data.startTime);
					$('#end1').datetimebox('setValue',data.endTime);
					$('#place1').val(data.place);
					$('#men1').val(data.men);
					$('#details1').val(data.details);
					$('#remark1').val(data.remark);
					var array = data.mark2.split(',');
					  $(".chse li input").each(function(){
						   $(this).attr("checked",false);
						  });  
					for(var i=0;i<array.length;i++){
						$('.chse li input[value='+array[i]+']').attr('checked',true);
					}
					var roleId = $('#role_id').val();
					//多用户日程且是秘书
					if(Theweek.checkIsManyLeaderEvent(data.scheduleId)&&roleId=='3'){
						Theweek.disableWindow();
						$("#save_btn").unbind();
						$('#save_btn').click(function(){
							Theweek.delLeaderFromSchedule();
						  });
					}else{
						Theweek.enableWindow();
						$("#save_btn").unbind();
						$('#save_btn').click(function(){
							Theweek.editSchedule();
						  });
					}
				  }
           });
			$("#can_btn").unbind();
			$('#can_btn').click(function(){
				Theweek.cancelEdit();
			  });
		},
		//禁用输入框
		disableWindow:function(){
			$('#title1').attr('disabled','disabled');
			$('#start1').datetimebox('disable');
			$('#end1').datetimebox('disable');
			$('#place1').attr('disabled','disabled');
			$('#men1').attr('disabled','disabled');
			$('#details1').attr('disabled','disabled');
			$('#remark1').attr('disabled','disabled');
		},
		//启用输入框
		enableWindow:function(){
			$('#title1').attr('disabled',false);
			$('#start1').datetimebox('enable');
			$('#end1').datetimebox('enable');
			$('#place1').attr('disabled',false);
			$('#men1').attr('disabled',false);
			$('#details1').attr('disabled',false);
			$('#remark1').attr('disabled',false);
		},
		//编辑取消按钮
		cancelEdit:function(){
			$('#light1').hide();
			$('#fade').hide();
		},
		cancelDetail:function(){
			$('#light1').hide();
			$('#fade').hide();
		},
		//编辑取消按钮
		cancelAdd:function(){
			$('#light2').hide();
			$('#fade').hide();
		},
		toAddDetail:function(start,end,leadId){
			$('#add_a_img').remove();
			$('#fade').show();
			$('#light1').show();
			$('#down_btn').hide();
			$("#save_btn").show();
			Theweek.enableWindow();
			if(start){
				$('#start1').datetimebox('setValue',new Date(start).format("yyyy-MM-dd hh:mm:ss"));
			}else{
				$('#start1').datetimebox('setValue','');
			}
			if(end){
				$('#end1').datetimebox('setValue',new Date(end).format("yyyy-MM-dd hh:mm:ss"));
			}else{
				$('#end1').datetimebox('setValue','');
			}
			//$('#start1').datetimebox('setValue',new Date($('#start2').attr("title")).format("yyyy-MM-dd hh:mm:ss"));
			//$('#end1').datetimebox('setValue',new Date($('#end2').attr("title")).format("yyyy-MM-dd hh:mm:ss"));
			var leaderId='';
			if(!leadId){
				leaderId = $('#leaderId').val();
			}else{
				leaderId = leadId;
			}
			$(".chse li input").each(function(){
					var value = $(this).val();
					if(value==leaderId){
						$(this).attr("checked",true);
					}else{
						$(this).attr("checked",false);
					}
				  }); 
			//var el = $('[name="s_leader"]:checked');
			 // for (var i = 0; i < el.length; i++)
			//  {
			//	   $('.chse li input[value='+el[i].value+']').attr('checked',true);
			   
			 // }
			  $('#scheduleId1').val('');
			  $('#title1').val('');
			  $('#place1').val('');
			  $('#men1').val('');
			  $('#details1').val('');
			  $('#remark1').val('');
			  $("#save_btn").unbind();
			  $("#can_btn").unbind();
			  $('#save_btn').click(function(){
				  Theweek.saveDetail();
			  });
			  $('#can_btn').click(function(){
				  Theweek.cancelDetail();
			  });
		},
		saveEasy:function(){

			var title=$('#title2').val();
			var start=$('#start2').attr("title");
			var end=$('#end2').attr("title");
			var el = $('[name="s_leader"]:checked');
			var leaders = "";
			  for (var i = 0; i < el.length; i++)
			  {
				   if(i==el.length-1){
					   leaders += el[i].value;
				   }else{
					   leaders += el[i].value+',';
				   }
			   
			  }
			if(title == ''){
				$.messager.alert('提示','主题不能为空','info');
				return;
			}
			$.ajax({
				type: "POST",
				url: "/pc/theweekedit/saveEasy.htm",
				data: {'title' :title,leaders:leaders,'startTime':new Date(start).format("yyyy/MM/dd hh:mm:ss"),'endTime':new Date(end).format("yyyy/MM/dd hh:mm:ss")},
				success: function(data){
					if(data=="0"){
						$('#light2').hide();
						$('#fade').hide();
						Theweek.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			}); 
		
		},
		saveDetail :function(){

			var title=$('#title1').val();
			var start = $('#start1').datetimebox('calendar').calendar('options').current;
	    	var end = $('#end1').datetimebox('calendar').calendar('options').current;
	    	var leader_num = $('[name="leaders"]:checked').length;
			$('#schedule_form').form('submit',{
				url:'/pc/theweekedit/saveDetail.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    	if($('#start1').datetimebox('getValue')==''||$('#end1').datetimebox('getValue')==''){
			    		$.messager.alert('提示','时间不能为空','info');
						return false;
			    	}
			    	if(start>=end){
			    		$.messager.alert('提示','起始时间不能大于结束时间','info');
						return false;
			    	}
			    	if(!Theweek.checkThisWeek(start)){
			    		$.messager.alert('提示','时间不在本周范围内','info');
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
						$('#light2').hide();
						$('#fade').hide();
						Theweek.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		
		},
		editSchedule:function(){
			var title=$('#title1').val();
			var start = $('#start1').datetimebox('calendar').calendar('options').current;
	    	var end = $('#end1').datetimebox('calendar').calendar('options').current;
			$('#schedule_form').form('submit',{
				url:'/pc/theweekedit/editSchedule.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    	if(start>=end){
			    		$.messager.alert('提示','起始时间不能大于结束时间','info');
						return false;
			    	}
			    	if(!Theweek.checkThisWeek(start)){
			    		$.messager.alert('提示','时间不在本周范围内','info');
						return false;
			    	}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#fade').hide();
						Theweek.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		},
		delLeaderFromSchedule:function(){
			var id=$('#scheduleId1').val();
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
		 				  url: "/pc/theweekedit/delLeaderFromEvent.htm?scheduleId="+id,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						$('#light1').hide();
								$('#fade').hide();
								Theweek.initLoad('success');
							}else{
								$.messager.alert("提示","操作失败",'info');
							}
		 				  }
		             });
				}
			});
		},
		commit:function(){
			var changeFlag = $('#change_flag').val();
			if(changeFlag==''){
				$.messager.alert('提示','没有任何改动，无需提交','info');
				return;
			}
			$.messager.confirm('提示信息','确认提交?',function(r){ 		
				if (r){ 		
					$.ajax({
						type: "POST",
						url: "/pc/theweekedit/commitSchedule.htm",
						success: function(data){
							if(data=="SUCCESS"){
								$.messager.alert('提示','提交成功','info',function(){
									//Theweek.initLoad();
									window.location.href="/pc/theweek/main.htm";
								});
							}else{
								$.messager.alert('提示','提交失败','info',function(){
									Theweek.initLoad();
								});
							}
						}
					});
				}
			});
		},
		release:function(){
			var changeFlag = $('#change_flag').val();
			if(changeFlag==''){
				$.messager.alert('提示','没有任何改动，无需发布','info');
				return;
			}
			$.messager.confirm('提示信息','确认发布?',function(r){ 		
				if (r){ 		
					$.ajax({
						type: "POST",
						url: "/pc/theweekedit/releaseSchedule.htm",
						success: function(data){
							if(data=="SUCCESS"){
								$.messager.alert('提示','发布成功','info',function(){
									//Theweek.initLoad();
									window.location.href="/pc/theweek/main.htm";
								});
								$.post("/pc/theweekedit/push.htm");
							}else{
								$.messager.alert('提示','发布失败','info',function(){
									Theweek.initLoad();
								});
							}
						}
					});
				}
			});
			
		},
		releaseChange:function(){
			$.messager.confirm('提示信息','确认发布?',function(r){ 		
				if (r){ 		
					$.ajax({
						type: "POST",
						url: "/pc/theweekedit/noOperateRelease.htm",
						success: function(data){
							if(data=="SUCCESS"){
								$.messager.alert('提示','发布成功','info',function(){
									//Theweek.initLoad();
									window.location.href="/pc/theweek/main.htm";
								});
								$.post("/pc/theweekedit/push.htm");
							}else{
								$.messager.alert('提示','发布失败','info',function(){
									Theweek.initLoad();
								});
							}
						}
					});
				}
			});
		},
		toEditState:function(){
			$.ajax({
				type: "POST",
				url: "/pc/theweekedit/toEditState.htm",
				success: function(data){
					if(data=="LOCK"){
						$.messager.alert('提示','其他用户正在操作','info');
					}else{
						var roleId = $('#role_id').val();
						Theweek.initLoad();
						$('#to_add_btn').show();
						$("#edit_release_btn").unbind();
						if(roleId=='3'){//秘书
							$('#edit_release_btn').val('提交');
							$('#edit_release_btn').click(function(){
								Theweek.commit();
							});
						}else{//副总或管理员
							if($('#release_tag').val()=='0'){//有更新时，可直接发布
								$('#change_flag').val('false');
								$('#release_a').hide();
							}
							$('#edit_release_btn').val('发布');
							$('#edit_release_btn').click(function(){
								Theweek.release();
							});
						}
						
					}
				}
			});
		},
		//判断是否多用户日程
		checkIsManyLeaderEvent:function(id){
			var isManyLeader = false;
			$.ajax({
				type: "POST",
				async:false,
				url: "/pc/theweekedit/checkIsManyLeaderEvent.htm",
				data: {'scheduleId' :id},
				success: function(data){
					if(data===true){
						isManyLeader =true;
					}
				}
			});
			return isManyLeader;
		},
		exportExcel:function(){
			window.location.href = "/pc/export/export.htm";
		},
		print:function(){
			var flag = $('#to_add_btn').is(":hidden");
			$('#print_btn').hide();
			$('#export_btn').hide();
			if(!flag){
				$('#to_add_btn').hide();
			}
			$('#edit_release_btn').hide();
			window.print();
			$('#print_btn').show();
			$('#export_btn').show();
			if(!flag){
				$('#to_add_btn').show();
			}
			$('#edit_release_btn').show();
		},
		//验证添加事件是否为本周，只允许给下周添加事件
		checkThisWeek:function(start){
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
		    var thismonday = new Date( now- (curweek-1)*86400000);  //获取当前时间的周一
		    if(eventMonday.getYear() != thismonday.getYear() ||
		    		eventMonday.getMonth() != thismonday.getMonth() ||
		    		eventMonday.getDate() != thismonday.getDate()){//判断是否为同一天，只允许给本周添加事件
		    	return false;
		    }
		    return true;
		}
	
	};

}();

$(document).ready(function() {
	Theweek.init(false);
	$('#edit_release_btn').click(function(){
		Theweek.toEditState();
	});
});
