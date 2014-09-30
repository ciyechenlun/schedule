/**
 * 头部页面JS
 * 
 * @author li.sanlai@ustcinfo.com
 */

Header = function() {
	

	return {

		//注销
		logout : function(){
			$.messager.confirm('提示','<span style="color:#000">确定要注销吗？</span>',function(b){
				if(b){
					$.ajax({
						  type: "POST",
						  url: "/pc/auditmodify/checkModify.htm",
						  success: function(data){
							  if(data&&typeof data == "boolean"){
								  $.messager.confirm('提示','确认放弃编辑？',function(ok){
										if(ok){
											$.post("/pc/auditmodify/clearTemp.htm");
											window.location.href = "/logout";
										}
									});
							  }else{
								  $.post("/pc/auditmodify/clearTemp.htm");
									window.location.href = "/logout";
							  }
						  },
						  error:function(){
							  window.location.href='/login.htm';
						  }
		         });
					
				}
			});
		},
		parseDate :function(date){
			var strdate=date.getDay();
			switch(strdate)
			{
				case 0:
				strdate="星期天";
				break;
				case 1:
				strdate="星期一";
				break;
				case 2:
				strdate="星期二";
				break;
				case 3:
				strdate="星期三";
				break;
				case 4:
				strdate="星期四";
				break;
				case 5:
				strdate="星期五";
				break;
				case 6:
				strdate="星期六";
				break;
			}
			var allStr = date.format("yyyy年M月d日")+"，"+strdate;
			return allStr;
		},
		//打开修改密码窗口
		changePwd :function(){
			//Header.winShow('window_password');
			$('#light').show();
		},
		
		savePassword : function(){
			var oldPassword = $.trim($("#oldPassword").val());
			var newPassword = $.trim($("#newPassword").val());
			var newPassword2 = $.trim($("#newPassword2").val());
			if(oldPassword == ""){
				$.messager.alert('提示','请输入旧密码','info');
				return;
			}
			if(newPassword == ""){
				$.messager.alert('提示','密码为空,请勿输入空格！','info');
				return;
			}
			if(newPassword2 == ""){
				$.messager.alert('提示','密码为空,请勿输入空格！','info');
				return;
			}
			if(newPassword != newPassword2){
				$.messager.alert('提示','两次输入的密码不相同','info');
				return;
			}
			$.ajax({
				  url: "/pc/user/changePassword.htm",
				  type:'post',
				  data:{
				  	'oldPassword' : oldPassword,
				  	'newPassword' : newPassword
				  },
				  dataType:'json',
				  success: function(data){
				  	if(data.success=='true'){
				  		$.messager.alert('提示','密码修改成功,请重新登陆!','info',function(){
				  			window.location.href="/logout";
				  		});
				  	}else{
				  		if(data.msg){
				  			$.messager.alert('提示',data.msg,'error');
				  		}else{
				  			$.messager.alert('提示','密码修改失败,请稍后重试!','error');
				  		}
				  		
				  	}
				  },
				  error:function(){
					  $.messager.alert('提示','系统出错，密码修改失败！','error');
				  }
			});
			
		},
		photo:function(value){
			value = "/pc/company/images/" + value;
			art.dialog({
				content : '<img src='+value+'>',
				width:'auto',
				height:'auto',
				zIndex : "20000",
				left: '40%',
			    top: '30%',
			    lock : true,
			    cancelVal: '关闭',
			    cancel: true
			});
		},
		winShow : function(id){
			var w=Hg.get(id),b=document.body, cw=b.clientWidth,ch=b.scrollTop;
			$(w).css('left',(cw -500)/2).css('top',parseInt(ch)).css('display','block');
		},
		
		winShow1 : function(id){
			var w=Hg.get(id),b=document.body, cw=b.clientWidth,ch=b.scrollTop + 300;
			$(w).css('left',(cw -500)/2).css('top',parseInt(ch)).css('display','block');
		},
		
		winHide : function(id){
			var w=Hg.get(id);
			w.style.display="none";
		}
		
	};

}();
$(function(){
	var now = Header.parseDate(new Date());
	$('#now').text(now);
});
