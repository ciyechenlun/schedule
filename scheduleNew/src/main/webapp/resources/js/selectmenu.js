//头部菜单选择

$(function(){
	
	//设置默认高度
	$("#rightWrap").height( $(".menu").height() );
	
	$(".menu li").click(function(){
		var _this =this;
		$.ajax({
			  type: "POST",
			  url: "/pc/auditmodify/checkModify.htm",
			  success: function(data){
				  if(data&&typeof data == "boolean"){
					  $.messager.confirm('提示','确认放弃编辑？',function(ok){
							if(ok){
								clickMenu(_this);
							}
						});
				  }else{
					  clickMenu(_this);
				  }
			  },
			  error:function(){
				  window.location.href='/login.htm';
			  }
   });
		
	});
	function clickMenu(el){
		var text = $.trim($(el).text());
		$(el).parent().find("li").removeClass("active");
		$(el).addClass("active");
		if(text=="本周日程"){
			$('#if_main').attr('src','/pc/theweek/main.htm');
		}else if(text=="下周日程拟定"){
			$('#if_main').attr('src','/pc/schedule/main.htm');
		}else if(text=="审核日程"){
			$('#if_main').attr('src','/pc/audit/main.htm');
		}else if(text=="待办日程"){
			$('#if_main').attr('src','/pc/todo/main.htm');
		}else if(text=="账号管理"){
			$('#if_main').attr('src','/pc/user/main.htm');
		}
		$.post("/pc/auditmodify/clearTemp.htm");
	}
});