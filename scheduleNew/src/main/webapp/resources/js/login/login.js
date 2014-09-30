/**
 * 登录页面JS
 * login.js
 * @author lisanlai
 * @email li.sanlai@ustcinfo.com
 * @date 2013-1-4 下午3:15:28
 */

Login = function(){
	
	return {
		
		//初始化
		init : function() {
			//输入框获取焦点
			$("#j_username").focus();
			
			Login.bindListners();
			
    		document.onkeydown = function(e){   
    		    var ev = document.all ? window.event : e; 
    		    if(ev.keyCode == 13) { 
    		    	Login.login();
			 	}
    		};
    	},
    	
    	//绑定事件
    	bindListners:function(){
    		$("#loginBtn").on({
    			'click' : Login.login
    		});
    	},
    	
    	//登录
    	login : function() {
    		if(!Login.validationForm())
    			return;
    		
    		$("#error").hide();
    		$("#loginBtn").attr("disabled", true);
    		$.post("j_spring_security_check", $("#loginForm").serialize(), function(data) {
    			if(data.type == "BadCapatch") {
    				$("#error").show().html("验证码错误，请重新输入 ");
    				Login._loginHande();
    			} else if(data.type == "BadCredentials") {
    				$("#error").show().html("密码错误，请重新输入 ");
    				$("#j_password").val("");
    				Login._loginHande();
    			} else if(data.type == "UsernameNotFound") {
    				$("#error").show().html("该用户不存在");
    				$("#j_password").val("");
    				Login._loginHande();
    			} else if(data.type == "NoAuthority") {
    				$("#error").show().html("您无权限登录网页版");
    				$("#j_password").val("");
    				Login._loginHande();
    			} else {
    				$("#error").hide();
    				window.location.href = "/index.htm";
    			}
    		}).error(function() { $("#loginBtn").attr("disabled", false); });
    	},
    	
    	//登录回调函数
    	_loginHande : function() {
    		//$('#errorMsg').hide();
    		$("#loginBtn").removeAttr("disabled");
    	},
    	
    	//验证表单
    	validationForm : function() {
    		if($("#j_username").val() == "") {
    			//$('#errorMsg').hide();
    			$("#error").show().html("请输入用户名");
    			return false;
    		} else if($("#j_password").val() == "") {
    			//$('#errorMsg').hide();
    			$("#error").show().html("请输入密码");
    			return false;
    		}
    		
    		return true;
    	},
    	
    	//忘记密码
    	forget : function(){
    		if($("#j_username").val() == "") {
    		//	$('#errorMsg').hide();
    			$("#error").show().html("请输入用户名,新密码将发送到您的手机！");
    			return;
    		}
    		$.ajax({
    			url : '/mobile/testMsg/sendMsg.htm',
				data : {
					'userName' :$("#j_username").val() 
				},
				type : 'post',
				async : false,
				success : function(data) {
					if(data=="1"){
						//$('#errorMsg').hide();
		    			$("#error").show().html("短信发送成功,请注意查收!");
		    			return;
					}else if(data=="0"){
						//$('#errorMsg').hide();
		    			$("#error").show().html("发送失败,请确认输入的用户名!");
		    			return;
					}else{
						//$('#errorMsg').hide();
		    			$("#error").show().html("忘记密码提示短信间隔不足一分钟，请稍候尝试!");
		    			return;
					}
				}
    		});
    	}
		
	};
	
}();

$(function(){
	$.loginForm = $('#loginForm');
	Login.init();
});