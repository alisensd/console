<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<!DOCTYPE html>
<html>

	<head>
		<meta name="description" content="User login page" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<script src="${ctxStatic}/jquery/jquery-2.1.1.min.js" type="text/javascript"></script>
		<script src="${ctxStatic}/jquery/jquery-migrate-1.1.1.min.js" type="text/javascript"></script>
		<script src="${ctxStatic}/jquery-validation/1.14.0/jquery.validate.js" type="text/javascript"></script>
		<script src="${ctxStatic}/jquery-validation/1.14.0/localization/messages_zh.min.js" type="text/javascript"></script>
		<link href="${ctxStatic}/bootstrap/3.3.4/css_default/bootstrap.min.css" type="text/css" rel="stylesheet" />
		<script src="${ctxStatic}/bootstrap/3.3.4/js/bootstrap.min.js"  type="text/javascript"></script>
		<link href="${ctxStatic}/awesome/4.4/css/font-awesome.min.css" rel="stylesheet" />
		<!-- jeeplus -->
		<link href="${ctxStatic}/common/jeeplus.css" type="text/css" rel="stylesheet" />
		<script src="${ctxStatic}/common/jeeplus.js" type="text/javascript"></script>
		<link rel="shortcut icon" href="images/favicon.png" type="image/png">
		<!-- text fonts -->
		<link rel="stylesheet" href="${ctxStatic }/common/login/ace-fonts.css" />

		<!-- ace styles -->
		<link rel="stylesheet" href="${ctxStatic }/common/login/ace.css" />

		<!-- 引入layer插件 -->
		<script src="${ctxStatic}/layer-v2.3/layer/layer.js"></script>
		<script src="${ctxStatic}/layer-v2.3/layer/laydate/laydate.js"></script>
		
		
		<!--[if lte IE 9]>
			<link rel="stylesheet" href="../assets/css/ace-part2.css" />
		<![endif]-->
		<link rel="stylesheet" href="${ctxStatic }/common/login/ace-rtl.css" />
		<style type="text/css">
		
			.login-container {
				width: 320px;
				min-width: 320px;
				margin: 160px auto 0;
			}
			.bound {
			    background-color: #fff;
			    border-radius: 10px;
			}
			
			.marginB-10 {
				margin-bottom: 10px;
			}
			
			.marginT-10 {
				margin-top: 10px;
			}
			
			.login-layout .widget-box .widget-main {
				background: #fff;
				padding: 0 30px 30px;
			}
			
			.login-layout label {
				margin-bottom: 15px;
			}
			
			.login-header {
				height: 50px;
				line-height: 50px;
				background-color: #428bca;
				border-radius: 10px 10px 0 0;
				margin-bottom: 20px;
			}
			
			.forgot-header {
				background-color: #d15b47 !important;
			}
			
			.signup-header {
				background-color: #87b87f !important;
			}
			
			.login-header h1 {
				line-height: 50px;
				text-align: center;
				font-size: 26px;
				margin: 0;
				color: #fff;
			}
			
			.btn,.btn:hover {
				border: none;
				padding: 8px 9px;
			}
		</style>
		<title>${fns:getConfig('productName')} 登录</title>
		<script>
			if (window.top !== window.self) {
				window.top.location = window.location;
			}
		</script>
		<script type="text/javascript">
				$(document).ready(function() {
					$("#loginForm").validate({
						rules: {
							validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
						},
						messages: {
							username: {required: "请填写用户名."},password: {required: "请填写密码."},
							validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
						},
						errorLabelContainer: "#messageBox",
						errorPlacement: function(error, element) {
							error.appendTo($("#loginError").parent());
						} 
					});
				});
				// 如果在框架或在对话框中，则弹出提示并跳转到首页
				if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
					alert('未登录或登录超时。请重新登录，谢谢！');
					top.location = "${ctx}";
				}
		</script>
		<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				rules: {
				loginName: {remote: "${ctx}/sys/user/validateLoginName"},
				mobile: {remote: "${ctx}/sys/user/validateMobile"},
				randomCode: {

					  remote:{

						   url:"${ctx}/sys/register/validateMobileCode", 
	
						  data:{
					       mobile:function(){
					          return $("#tel").val();
					          }
			          		} 

						}


					}
			},
				messages: {
					confirmNewPassword: {equalTo: "输入与上面相同的密码"},
					ck1: {required: "必须接受用户协议."},
					loginName: {remote: "此用户名已经被注册!", required: "用户名不能为空."},
					mobile:{remote: "此手机号已经被注册!", required: "手机号不能为空."},
					randomCode:{remote: "验证码不正确!", required: "验证码不能为空."}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			$("#resetForm").validate({
				rules: {
				mobile: {remote: "${ctx}/sys/user/validateMobileExist"}
			},
				messages: {
					mobile:{remote: "此手机号未注册!", required: "手机号不能为空."}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			// 手机号码验证
			jQuery.validator.addMethod("isMobile", function(value, element) {
			    var length = value.length;
			    var mobile = /^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$/;
			    return (length == 11 && mobile.test(value));
			}, "请正确填写您的手机号码");



			$('#sendPassBtn').click(function () { 
				if($("#tel_resetpass").val()=="" || $("#tel_resetpass-error").text()!=""){
					top.layer.alert("请输入有效的注册手机号码！", {icon: 0});//讨厌的白色字体问题
					return;

				}
				$("#sendPassBtn").attr("disabled", true); 
				$.get("${ctx}/sys/user/resetPassword?mobile="+$("#tel_resetpass").val(),function(data){
						if(data.success == false){
							top.layer.alert(data.msg, {icon: 0});//讨厌的白色字体问题
							//alert(data.msg);
							$("#sendPassBtn").html("重新发送").removeAttr("disabled"); 
							clearInterval(countdown); 

						}

					});
				var count = 60; 
				var countdown = setInterval(CountDown, 1000); 
				function CountDown() { 
					$("#sendPassBtn").attr("disabled", true); 
					$("#sendPassBtn").html("等待 " + count + "秒!"); 
					if (count == 0) { 
						$("#sendPassBtn").html("重新发送").removeAttr("disabled"); 
						clearInterval(countdown); 
					} 
					count--; 
				}

				
			}) ;
			

			$('#sendCodeBtn').click(function () { 
				if($("#tel").val()=="" || $("#tel-error").text()!=""){
					top.layer.alert("请输入有效的注册手机号码！", {icon: 0});//讨厌的白色字体问题
					return;

				}
				$("#sendCodeBtn").attr("disabled", true); 
				$.get("${ctx}/sys/register/getRegisterCode?mobile="+$("#tel").val(),function(data){
						if(data.success == false){
							//top.layer.alert(data.msg, {icon: 0});讨厌的白色字体问题
							alert(data.msg);
							$("#sendCodeBtn").html("重新发送").removeAttr("disabled"); 
							clearInterval(countdown); 

						}

					});
				var count = 60; 
				var countdown = setInterval(CountDown, 1000); 
				function CountDown() { 
					$("#sendCodeBtn").attr("disabled", true); 
					$("#sendCodeBtn").html("等待 " + count + "秒!"); 
					if (count == 0) { 
						$("#sendCodeBtn").html("重新发送").removeAttr("disabled"); 
						clearInterval(countdown); 
					} 
					count--; 
				}

				
			}) ;
			
		});

	
		
		

		
	</script>
	</head>

	
	<body class="login-layout light-login">
		<div class="main-container">
			<div class="main-content">
				<div class="row">
					<div class="col-sm-10 col-sm-offset-1">
						<div class="login-container">
							
							<div class="center">
								<sys:message content="${message}"/>
							</div>

							<div class="space-6"></div>

							<div class="position-relative">
								<div id="login-box" class="login-box visible widget-box no-border bound">
									<div class="widget-body bound">
										<div class="login-header">
											<h1>
												<i class="fa fa-opencart"></i> 订货系统
											</h1>
										</div>
										<div class="widget-main bound">
											<h4 class="header blue lighter bigger">
												<i class="ace-icon fa fa-user green"></i>
												用户登录
											</h4>
											<div class="space-6"></div>
	
											<form id="loginForm" class="form-signin" action="${ctx}/login" method="post">
												<fieldset>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text"  id="username" name="username" class="form-control required" placeholder="用户名" />
															<i class="ace-icon fa fa-user"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" id="password" name="password" class="form-control required" placeholder="密码" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>
													<c:if test="${isValidateCodeLogin}">
														<div class="input-group m-b text-muted validateCode">
														<label class="input-label mid" for="validateCode">验证码:</label>
														<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:5px;"/>
														</div>
													</c:if>
													<div class="space"></div>

													<div class="clearfix">
														<label class="inline pull-left">
															<input  type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''} class="ace" />
															<span class="lbl"> 记住我</span>
														</label>
														
														<div id="themeSwitch" class="dropdown pull-right">
															<a class="dropdown-toggle" data-toggle="dropdown" href="#">${fns:getDictLabel(cookie.theme.value,'theme','默认主题')}<b class="caret"></b></a>
															<ul class="dropdown-menu">
															  <c:forEach items="${fns:getDictList('theme')}" var="dict"><li><a href="#" onclick="location='${pageContext.request.contextPath}/theme/${dict.value}?url='+location.href"><font color="black">${dict.label}</font></a></li></c:forEach>
															</ul>
															<!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
														</div>
													</div>
													<div class="login-btn marginB-10 clearfix">
														<button type="submit" class="btn btn-sm btn-primary col-md-12">
															<i class="ace-icon fa fa-sign-in"></i>
															<span class="bigger-110">登录</span>
														</button>
													</div>
												</fieldset>
											</form>
											<br>
											<div class="form-options clearfix">
											<div>
												<a href="#" data-target="#forgot-box" class="pull-left">
													<font color=" #007aff;"><i class="ace-icon fa fa-hand-o-right"></i>
													忘记密码</font> 
												</a>
											</div>
											
											<!-- 屏蔽注册功能 -->
											<!-- <div>
												<a href="#" data-target="#signup-box" class="pull-right user-signup-link">
													<font color=" #A73438">没有账号？注册 
													<i class="ace-icon fa fa-arrow-right"></i></font>
												</a>
											</div> -->
										</div>
										</div><!-- /.widget-main -->

										
									</div><!-- /.widget-body -->
								</div><!-- /.login-box -->

								<div id="forgot-box" class="forgot-box widget-box no-border bound">
									<div class="widget-body bound">
										<div class="login-header forgot-header">
											<h1>
												<i class="fa fa-opencart"></i> 订货系统
											</h1>
										</div>
										<div class="widget-main bound">
											<h4 class="header red lighter bigger">
												<i class="ace-icon fa fa-key"></i>
												找回密码
											</h4>

											<div class="space-6"></div>
											<p>
												请输入您绑定的手机号，您将会收到新的密码。
											</p>

											<form id="resetForm">
												<fieldset>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="tel_resetpass" name="mobile" type="tel" class="form-control  text-muted required isMobile" placeholder="请输入手机号" />
															<i class="ace-icon fa fa-envelope"></i>
														</span>
													</label>

													<div class="clearfix">
														<button id="sendPassBtn" type="button" class="btn btn-sm btn-danger col-md-12">
															<i class="ace-icon fa fa-lightbulb-o"></i>
															<span class="bigger-110">发送</span>
														</button>
													</div>
												</fieldset>
											</form>
											<div class="form-options center">
												<br/>
												<br/>
												<a href="#" data-target="#login-box" class="">
													<font color=" #A73438"><i class="ace-icon fa fa-arrow-left"></i>
													返回登录
													</font>
												</a>
											</div>
										</div><!-- /.widget-main -->

									</div><!-- /.widget-body -->
								</div><!-- /.forgot-box -->

								<%-- <div id="signup-box" class="signup-box widget-box no-border bound">
									<div class="widget-body bound">
										<div class="login-header signup-header">
											<h1>
												<i class="fa fa-opencart"></i> 订货系统
											</h1>
										</div>
										<div class="widget-main bound">
											<h4 class="header green lighter bigger">
												<i class="ace-icon fa fa-users blue"></i>
												用户注册
											</h4>

											<div class="space-6"></div>
											<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/register/registerUser" method="post" class="form-group form-horizontal">
												<input  type="hidden" value="system" name="roleName"><!-- 默认注册用户都是超级管理员 -->
												<fieldset>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="tel" name="mobile" type="text" value="" maxlength="11" minlength="11" class="form-control text-muted required isMobile"  placeholder="手机号"/>
															<i class="ace-icon fa fa-phone"></i>
														</span>
													</label>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="code" name="randomCode" type="text" value="" maxlength="4" minlength="4" class="required col-md-6 pull-left col-xs-6"  placeholder="验证码"/>
															<button class="pull-right btn btn-sm btn-primary col-xs-6 col-md-6 width-40" type="button" id="sendCodeBtn"  >
																<i class="ace-icon fa fa-lightbulb-o"></i>
																<span class="">获取验证码</span>
															</button>
															<label id="code-error" class="error" for="code" style="display:none"></label>
														</span>
													</label>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="userId" name="loginName" type="text" value="${user.loginName }" maxlength="20" minlength="3" class="form-control required" placeholder="用户名" />
															<i class="ace-icon fa fa-user"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="newPassword" name="password" type="password" value="" maxlength="20" minlength="3"  class="form-control required" placeholder="密码" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="20" minlength="3" class="form-control required" equalTo="#newPassword" placeholder="重复密码" />
															<i class="ace-icon fa fa-retweet"></i>
														</span>
													</label>

													<label class="block">
														<input name="ck1" type="checkbox" class="required ace" />
														<span class="lbl">
															我接受
															<a href="#">《炼金台用户注册协议》</a>
														</span>
														<label id="ck1-error" class="error" for="ck1" style="display: none;">必须接受用户协议</label>
													</label>

													<div class="space-24"></div>

													<div class="clearfix">
														<button type="reset" class="width-30 pull-left btn btn-sm">
															<i class="ace-icon fa fa-refresh"></i>
															<span class="bigger-110">重置</span>
														</button>

														<button type="submit" class="width-65 pull-right btn btn-sm btn-success">
															<span class="bigger-110">注册</span>

															<i class="ace-icon fa fa-arrow-right icon-on-right"></i>
														</button>
													</div>
												</fieldset>
											</form:form>
											<div class="form-options center">
												<br/>
												<br/>
												<a href="#" data-target="#login-box" class="">
													<font color=" #A73438"><i class="ace-icon fa fa-arrow-left"></i>
													返回登录
													</font>
												</a>
											</div>
										</div>

										
									</div><!-- /.widget-body -->
								</div><!-- /.signup-box --> --%>
							</div><!-- /.position-relative -->
				
						</div>
					</div><!-- /.col -->
				</div><!-- /.row -->
			</div><!-- /.main-content -->
		</div><!-- /.main-container -->

		<!-- basic scripts -->

		<!--[if !IE]> -->
		<script type="text/javascript">
			window.jQuery || document.write("<script src='../assets/js/jquery.js'>"+"<"+"/script>");
		</script>

		<!-- <![endif]-->

		<!--[if IE]>
<script type="text/javascript">
 window.jQuery || document.write("<script src='../assets/js/jquery1x.js'>"+"<"+"/script>");
</script>
<![endif]-->

		<script type="text/javascript">
			if('ontouchstart' in document.documentElement) document.write("<script src='../assets/js/jquery.mobile.custom.js'>"+"<"+"/script>");
		</script>
		<style>
		/* Validation */

			label.error {
			    color: #cc5965;
			    display: inline-block;
			    margin-left: 5px;
			}
			
			.form-control.error {
			    border: 1px dotted #cc5965;
			}
		</style>
	<!-- inline scripts related to this page -->
		<script type="text/javascript">
		$(document).ready(function() {
			 $(document).on('click', '.form-options a[data-target]', function(e) {
				e.preventDefault();
				var target = $(this).data('target');
				$('.widget-box.visible').removeClass('visible');//hide others
				$(target).addClass('visible');//show target
			 });
			});
			
			
			
		</script>
	</body>
</html>
