
ToDo = function() {

	return {
		//取消按钮
		cancel:function(){
			$('#light1').hide();
			$('#fade').hide();
		},
		//编辑页面的取消按钮
		cancelEdit:function(){
			$('#light2').hide();
			$('#fade').hide();
		},
		//弹出添加窗口
		toCreate:function(){
			$('#fade').show();
			$('#light1').show();
			
		},
		//弹出编辑窗口
		toEdit:function(id){
			$('#fade').show();
			$('#light2').show();
			$.ajax({
				  url: "/pc/todo/getToDoById.htm?scheduleId="+id+"&timeaxis="+new Date().getTime(),
				  success: function(data){
					$('#scheduleId').val(data.scheduleId);
					$('#e_title').val(data.title);
					$('#place').val(data.place);
					$('#men').val(data.men);
					$('#details').val(data.details);
					$('#remark').val(data.remark);
				  }
           });
		},
		//存为下周日程
		saveSchedule:function(){
			var title=$('#e_title').val();
			var start = $('#e_start').datetimebox('calendar').calendar('options').current;
	    	var end = $('#e_end').datetimebox('calendar').calendar('options').current;
	    	if($('#e_start').datetimebox('getValue')==''||$('#e_end').datetimebox('getValue')==''){
	    		$.messager.alert('提示','时间不能为空','info');
				return false;
	    	}
	    	if(!ToDo.checkAfterWeek(start)){
	    		var roleId = parent.$('#user_role').val();
		    	if(roleId=='1'||roleId=='2'){
		    		if(ToDo.checkThisWeek(start)){
		    			$.messager.confirm('提示信息','确认发布为本周日程?',function(r){ 		
		    				if (r){ 	
		    					$('#todo_edit_form').form('submit',{
		    						url:'/pc/todo/saveThisWeekSchedule.htm',
		    					    onSubmit : function(){
		    					    	if(title == ''){
		    								$.messager.alert('提示','主题不能为空','info');
		    								return false;
		    							}
		    					    	if(start>=end){
		    					    		$.messager.alert('提示','起始时间不能大于结束时间','info');
		    								return false;
		    					    	}
		    					    	
		    					    },
		    						success:function(data){
		    							if(data=="0"){
		    								$('#light2').hide();
		    								$('#fade').hide();
		    								window.location.href="/pc/todo/main.htm";
		    								$.post("/pc/theweekedit/push.htm");
		    							}else if(data=="1"){
		    								$.messager.alert('提示','时间冲突','info');
		    							}else{
		    								$.messager.alert('提示','保存失败','info');
		    							}
		    						}
		    					});
		    				}
		    			});
		    		}else{
		    			$.messager.alert('提示','请选择本周及以后的时间','info');
						return;
		    		}
		    	}else{
		    		$.messager.alert('提示','请选择下周及以后的时间','info');
					return;
		    	}
	    	}else{
				$('#todo_edit_form').form('submit',{
					url:'/pc/todo/saveSchedule.htm',
				    onSubmit : function(){
				    	if(title == ''){
							$.messager.alert('提示','主题不能为空','info');
							return false;
						}
				    	if(start>=end){
				    		$.messager.alert('提示','起始时间不能大于结束时间','info');
							return false;
				    	}
				    	
				    },
					success:function(data){
						if(data=="0"){
							$('#light2').hide();
							$('#fade').hide();
							window.location.href="/pc/todo/main.htm";
						}else if(data=="1"){
							$.messager.alert('提示','时间冲突','info');
						}else if(data=="-1"){
							$.messager.alert('提示','下周日程已提交，不能添加日程','info');
						}else{
							$.messager.alert('提示','保存失败','info');
						}
					}
				});
	    	}
		
		},
		//更新待办任务
		updateToDo:function(){
			var title=$('#e_title').val();
			$('#todo_edit_form').form('submit',{
				url:'/pc/todo/updateToDo.htm',
			    onSubmit : function(){
			    	if(title == ''){
						$.messager.alert('提示','主题不能为空','info');
						return false;
					}
			    },
				success:function(data){
					if(data=="0"){
						$('#light2').hide();
						$('#fade').hide();
						window.location.href="/pc/todo/main.htm";
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		},
		//存为待办
		save:function(){
			var title=$('#d_title').val();
			$('#todo_form').form('submit',{
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
						$('#fade').hide();
						window.location.href="/pc/todo/main.htm";
					}else{
						$.messager.alert('提示','保存失败','info');
					}
				}
			});
		
		},
		//删除待办（物理删除）
		del:function(id){
			$.messager.confirm('提示信息','确认删除?',function(r){ 		
				if (r){ 		
					$.ajax({
		 				  url: "/pc/todo/del.htm?scheduleId="+id,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						$.messager.alert("提示","删除成功",'info',function(){
		 							window.location.href="/pc/todo/main.htm";
		 						});
							}else{
								$.messager.alert("提示","删除失败",'info',function(){
		 							window.location.href="/pc/todo/main.htm";
		 						});
							}
		 				  }
		             });
				}
			});
		},
		//检查是否为之后的周
		checkAfterWeek:function(start){
		    var now = new Date(new Date().format('yyyy/MM/dd'));
		    var curweek = now.getDay();
			 if ( curweek == 0 )
			    {
				 curweek = 7;
			    }
		    var nextmonday = new Date( now- (curweek-8)*86400000);  //获取当前时间的下周一
		    if(start<nextmonday){//事件时间在下周一之前
		    	return false;
		    }
		    return true;
		},
		//检查是否为本周
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
		    		eventMonday.getDate() != thismonday.getDate()){//判断是否为同一天
		    	return false;
		    }
		    return true;
		}
	};

}();

