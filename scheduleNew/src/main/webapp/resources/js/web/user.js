
User = function() {
	

	return {
		checkExist:function(){
			var userName = $('#userName').val();
			var userId = $('#userId').val();
			var preUserName=$('#preUserName').val();
			var flag=false;
			if( userId =='' || preUserName!=userName){
                 $.ajax({
   				  url: "/pc/user/checkUserName.htm?userName="+$('#userName').val(),
   				  async:false,
   				  success: function(data){
   					if(data=="1"){
						$('#user_tip').text("!用户名已存在");
						flag=true;
					}else{
						$('#user_tip').text("*");
					}
   				  }
                 });
			}else{
				$('#user_tip').text("*");
			}
			return flag;
		},
		toAddUser:function(){
			$('#fade').show();
			$('#light').show();
			$('.windowHeader').text('新建用户');
			$('#userName').val('');
			$('#realName').val('');
			$('#mobile').val('');
			$('#roleId').val('');
			$('#leaderId').val('');
			$('#disOrder').val('');
			$('#userId').val('');
			$('#preOrder').val('');
			$('#preUserName').val('');
			$('#user_tip').text("*");
			$('#disOrder option:last').show();//新增时级别顺序比用户数多一个
		},
		toEditUser:function(userId){
			$('#fade').show();
			$('#light').show();
			$('.windowHeader').text('修改用户');
			$.post("/pc/user/getUser.htm?userId="+userId,function(data){
				$('#userName').val(data.userName);
				$('#realName').val(data.realName);
				$('#mobile').val(data.mobile);
				$('#roleId').val(data.role.roleId);
				if(data.leader){
					$('#leaderId').val(data.leader.leaderId);
				}else{
					$('#leaderId').val('');
				}
				$('#disOrder').val(data.disOrder);
				$('#userId').val(data.userId);
				$('#preOrder').val(data.disOrder);
				$('#preUserName').val(data.userName);
				$('#user_tip').text("*");
				$('#disOrder option:last').hide();//修改时级别顺序与用户数相同
			});
			
		},
		save :function(){
			$('#userForm').form('submit',{
				url:'/pc/user/save.htm',
			     onSubmit : function(){
			    	 if(''==$('#userName').val()){
			    		 $.messager.alert('提示','用户名不能为空','info');
		                  return false;
			    	 }
			    	 if(''==$('#realName').val()){
			    		 $.messager.alert('提示','名称不能为空','info');
		                  return false;
			    	 }
			    	 if(''==$('#mobile').val()){
			    		 $.messager.alert('提示','手机号不能为空','info');
		                  return false;
			    	 }
	                 if(!(/^((13[0-9])|(147)|(15[0-9])|(18[0-9]))\d{8}$/).test($('#mobile').val())){
	                	 $.messager.alert('提示','手机号输入不合法','info');
		                  return false;
	                 }
	                 if(''==$('#roleId').val()){
			    		 $.messager.alert('提示','请选择角色','info');
		                  return false;
			    	 }
	                 if(''==$('#disOrder').val()){
			    		 $.messager.alert('提示','请选择顺序','info');
		                  return false;
			    	 }
	                 var flag=User.checkExist();
	                 if(flag){
	                	 $.messager.alert('提示','用户名已存在','info');
		                  return false;
	                 }
	                },
				success:function(data){
					if(data == "SUCCESS"){
						$('#light').hide();
						$('#fade').hide();
						window.location.href="/pc/user/main.htm";
					}else{
						$.messager.alert("提示","保存失败",'info');
					}
				}
			});
		},
		delUser : function(userId){
			$.messager.confirm('提示信息','确认是否删除该用户信息?',function(r){ 		
				if (r){ 		
					$.ajax({
		 				  url: "/pc/user/delUser.htm?userId="+userId,
		 				  async:false,
		 				  success: function(data){
		 					if(data=="SUCCESS"){
		 						window.location.href="/pc/user/main.htm";
							}else{
								$.messager.alert("提示","删除失败",'info',function(){
									window.location.href="/pc/user/main.htm";
								});
							}
		 				  }
		             });
				}
			});
		}
	};

}();

