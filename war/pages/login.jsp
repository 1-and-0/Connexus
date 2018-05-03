<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html ng-app="loginApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>

<script type="text/javascript" src="/js/angular.js"></script>
<script type="text/javascript" src="/js/angular-route.min.js"></script>
<script type="text/javascript" src="/js/loginController.js"></script>

<link rel="stylesheet" href="/style/bootstrap.min.css">
<link rel="stylesheet" href="/style/style.css">

</head>
<body ng-controller="loginController">
	<div class="row" style="background-color: rgba(245, 248, 239, 0.95)">
		<h2 style="margin-left:30px;">Connexus.us</h2>
	</div>
	<div class="row" style="background-color: rgba(245, 248, 239, 0.95)">
		<form name="loginform" class="col-md-4 rght-nav" style="margin: 10px;">
			<input type="text" ng-model="loginId" placeholder="login id">
			<input type="password" ng-model="password" placeholder="Password">
			<input type="button" ng-click="validateLogin()" class="btn-success"
				value="Sign In" />
		</form>
	</div>
	<div class="row">
		<form name="signupform" class="col-md-4 sign-up-div">
			<ul class="unstyled" style="margin: 15px;">
				<li><input type="text" ng-model="sup_loginid"
					placeholder="choose a UserName" name="loginid" required></li>
				<li><input type="email" ng-model="sup_email"
					placeholder="Enter your email" name="emailid" required></li>
				<li><input type="text" ng-model="sup_name"
					placeholder="Enter name"  name="name" required></li>
				<li><input type="password" ng-model="sup_password"
					placeholder="Enter a password" name="pass1" required></li>
				<li><input type="password" ng-model="sup_rpassword"
					placeholder="Re enter password" name="pass2" required></li>
				<li><input type="button" ng-click="newusersignup()"
					class="btn-success" value="Sign Up" required /></li>
			</ul>
		</form>
	</div>

</body>
</html>