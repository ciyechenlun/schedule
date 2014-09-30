
Audit = function() {

	return {
		init:function(isChange){
			$.ajax({
				type: "POST",
				async:false,
				url: isChange?"/pc/auditmodify/getSubmitSchedule.htm":"/pc/audit/getSubmitSchedule.htm",
				success: function(data){
					Audit.loadData(data);
				}
			});
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
					Audit.stopDrag(this);
				}
			});
			$('.mark td').click(function(){
				var leaderId = $(this).parent().attr('id');//点击表格的领导
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

				//Audit.toAddDetail(start,end,leaderId);
				$(this).html('<a id="add_a_img" href=\'javascript:Audit.toAddDetail("'+start+'","'+end+'","'+leaderId+'")\'><img src="/resources/images/btn_tj.png"  alt="添加" /></a>');
				$(this).mouseleave(function(){
					$('#add_a_img').remove();
				});
			});
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
			    var scheduleId=$(this).find('a').attr('id');
				$.ajax({
					type: "POST",
					cache: true, 
					url: isChange?"/pc/auditmodify/getScheduleById.htm?scheduleId="+scheduleId
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
			$('#to_edit').unbind();
			$('#to_edit').click(function(){
				Audit.toEdit(isChange);
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
				Audit.dragCopy(s_id,y);
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
							Audit.initLoad();
							return;
						}else{
							var d_id=$(this).find('a').attr('id');
							Audit.dragCover(s_id,d_id,y);
							return;
						}
					}
				}
				
			});
			if(!isCoin){
				Audit.dragUpdate(s_id,x,y);
			}
		},
		initLoad:function(flag){
			$('#event_div').empty();
			$('#event_div').append('<input type="hidden" id="source_position"/>');
			var changeFlag = $('#change_flag').val();
			if(changeFlag==''&&flag && flag=='success'){
				$('#change_flag').val('true');
				$.post('/pc/auditmodify/addChangeFlag.htm');
			}
			if($('#change_flag').val()=='true'){
				if($('#release_btn').attr("disabled")=="disabled"){
					$('#release_btn').attr("disabled", false).attr("style","")
					.removeClass("btn_edit").addClass("btn_safe").val('发布');
				}
				Audit.init(true);
			}else{
				 Audit.init(false);
			}
		},
		confirmTodo:function(titles,confictIds,scheduleId,leaderId){
			$.messager.confirm('提示信息','因时间冲突，系统将会把'+'"'+titles+'"'+'事件替换并放置于待办日程，请确认?',function(ok){ 		
				if (ok){ 		
					$.ajax({
		 				  url: "/pc/auditmodify/completeCopy.htm",
		 				  data:{"confictIds":confictIds,"scheduleId":scheduleId,"leaderId":leaderId},
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						Audit.initLoad('success');
							}else{
								$.messager.alert("提示","操作失败",'info',function(){
									Audit.initLoad();
		 						});
							}
		 				  }
		             });
				}else{
					Audit.initLoad();
				}
			});
		},
		dragCopy:function(scheduleId,top){
			var leaderId = $("tr:eq("+parseInt(((top-143)/80)+2)+")").attr('id');
			$.ajax({
				  type: "POST",
				  url: "/pc/auditmodify/dragCopy.htm",
				  data: {'scheduleId' :scheduleId,'leaderId' :leaderId},
				  success: function(data){
					  var result = data.result;
					  var titles = data.titles;
					  var confictIds = data.confictIds;
					  if(result=="1"){
							$.messager.alert('提示','操作失败','info',function(){
								Audit.initLoad();
							});
					  }else if(result=="0"){
							Audit.initLoad('success');
					  }else if(result=="LOCK"){
							$.messager.alert('提示','其他用户正在操作','info',function(){
								Audit.initLoad();
							});
					  }else if(result=="2"){
						  Audit.confirmTodo(titles,confictIds,scheduleId,leaderId);
					  }else{
						  Audit.initLoad();
					  }
				  }
           });
		},
		dragCover:function(s_id,d_id,top){
			var leaderId = $("tr:eq("+parseInt(((top-143)/80)+2)+")").attr('id');
			$.ajax({
				  type: "POST",
				  url: "/pc/auditmodify/dragCover.htm",
				  data: {'sId' :s_id,'dId' :d_id,'leaderId':leaderId},
				  success: function(data){
					  if(data=="1"){
							$.messager.alert('提示','操作失败','info',function(){
								Audit.initLoad();
							});
							
						}else if(data==""){
							Audit.initLoad('success');
						}else if(data=="2"){
							$.messager.alert('提示','多用户事件移动时发生时间冲突','info',function(){
								Audit.initLoad();
							});
						}else if(data=="LOCK"){
							$.messager.alert('提示','其他用户正在操作','info',function(){
								Audit.initLoad();
							});
						}else{
							$.messager.alert('提示','"'+data+'"'+'因时间冲突存为待办，发布后可在待办日程中查看到','info',function(){
								Audit.initLoad('success');
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
				  url: "/pc/auditmodify/dragUpdate.htm",
				  data: {'scheduleId' :scheduleId,'start' :start,'leaderId':leaderId},
				  success: function(data){
					  if(data=="1"){
							$.messager.alert('提示','操作失败','info',function(){
								Audit.initLoad();
							});
							
						}else if(data==""){
							Audit.initLoad('success');
						}else if(data=="2"){
							$.messager.alert('提示','多用户事件移动时发生时间冲突','info',function(){
								Audit.initLoad();
							});
						}else if(data=="LOCK"){
							$.messager.alert('提示','其他用户正在操作','info',function(){
								Audit.initLoad();
							});
						}else{
							$.messager.alert('提示','"'+data+'"'+'因时间冲突存为待办，发布后可在待办日程中查看到','info',function(){
								Audit.initLoad('success');
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
				var space =Audit.getSpace(start,true);
				var end_space= Audit.getSpace(end,false);
				var nextCount=1;
				var endCount=1;
				var preCount=0;
				for(var j=i+1;j<data.length;j++){
					if(id != data[j].leader_id){
						break;
					}
					if(space == Audit.getSpace(data[j].start_time,true)){
						nextCount++;
					}
					if(end_space == Audit.getSpace(data[j].start_time,true)){
						endCount++;
					}
				}
				for(var x=i-1;x>=0;x--){
					if(id != data[x].leader_id){
						break;
					}
					if(space == Audit.getSpace(data[x].end_time,false)){
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
		 				  url: "/pc/auditmodify/del.htm?scheduleId="+id+"&type="+type,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						Audit.initLoad('success');
							}else if(data=="LOCK"){
								$.messager.alert('提示','其他用户正在操作','info');
							}else{
								$.messager.alert("提示","删除失败",'info',function(){
									Audit.initLoad();
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
				url: isChange?"/pc/auditmodify/getScheduleById.htm?scheduleId="+id
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
				  }
           });
			$("#save_btn").unbind();
			$("#can_btn").unbind();
			$('#save_btn').click(function(){
				Audit.editSchedule();
			  });
			$('#can_btn').click(function(){
				Audit.cancelEdit();
			  });
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
			if(leadId){
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
				  Audit.saveDetail();
			  });
			  $('#can_btn').click(function(){
				  Audit.cancelDetail();
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
				url: "/pc/auditmodify/saveEasy.htm",
				data: {'title' :title,leaders:leaders,'startTime':new Date(start).format("yyyy/MM/dd hh:mm:ss"),'endTime':new Date(end).format("yyyy/MM/dd hh:mm:ss")},
				success: function(data){
					if(data=="0"){
						$('#light2').hide();
						$('#fade').hide();
						Audit.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else if(data=="LOCK"){
						$.messager.alert('提示','其他用户正在操作','info');
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
				url:'/pc/auditmodify/saveDetail.htm',
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
			    	if(!Audit.checkNextWeek(start)){
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
						$('#light2').hide();
						$('#fade').hide();
						Audit.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else if(data=="LOCK"){
						$.messager.alert('提示','其他用户正在操作','info');
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
				url:'/pc/auditmodify/editSchedule.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    	if(start>=end){
			    		$.messager.alert('提示','起始时间不能大于结束时间','info');
						return false;
			    	}
			    	if(!Audit.checkNextWeek(start)){
			    		$.messager.alert('提示','时间不在下周范围内','info');
						return false;
			    	}
			    },
				success:function(data){
					if(data=="0"){
						$('#light1').hide();
						$('#fade').hide();
						Audit.initLoad('success');
					}else if(data=="1"){
						$.messager.alert('提示','时间冲突','info');
					}else if(data=="LOCK"){
						$.messager.alert('提示','其他用户正在操作','info');
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		},
		release:function(){
			$.messager.confirm('提示信息','确认发布?',function(r){ 		
				if (r){ 		
					$.ajax({
						type: "POST",
						url: "/pc/auditmodify/releaseSchedule.htm",
						success: function(data){
							if(data=="SUCCESS"){
								$.messager.alert('提示','发布成功','info',function(){
									//Audit.initLoad();
									window.location.href="/pc/audit/main.htm";
								});
								$.post("/pc/auditmodify/push.htm");
							}else if(data=="LOCK"){
								$.messager.alert('提示','其他用户正在操作','info');
							}else{
								$.messager.alert('提示','发布失败','info',function(){
									Audit.initLoad();
								});
							}
						}
					});
				}
			});
			
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
		}
	};

}();

$(document).ready(function() {
	Audit.init(false);
});
