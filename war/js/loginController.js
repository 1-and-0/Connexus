var loginApp 			=	angular.module('loginApp', []);

var loginController		=	loginApp.controller('loginController',function($scope,$http,$window) {
	
	$scope.loginId				= 	null;
	$scope.password				=	null;

	$scope.sup_loginid			= 	null;
	$scope.sup_password			=	null;
	$scope.sup_rpassword		=	null;
	$scope.sup_email			=	null;
	$scope.sup_name				=	null;
	
	$scope.newusersignup		=	function(){
	
		if(!$scope.signupform.$valid)
		{
			alert("please enter valid signup details")
			return;
		}
		if($scope.sup_password != $scope.sup_rpassword)
		{
			alert("password does not match");
			return;
		}
		
		var dataObject			= new Object();
		
		dataObject.userId 		= $scope.sup_loginid;
		dataObject.userPass		= $scope.sup_password;
		dataObject.userEmail	= $scope.sup_email;
		dataObject.userName		= $scope.sup_name;
		console.info(dataObject);
		ajaxCall($scope,$http,"/addnewuser",dataObject,"POST",afterRegistrationFunction,$window);
	};
	
	$scope.validateLogin		=	function(){
		
		if(!$scope.loginform.$valid)
		{
			alert("please enter valid login details")
			return;
		}
		
		var dataObject			= new Object();
		dataObject.userId 		= $scope.loginId;
		dataObject.userPass		= $scope.password;
		console.info(dataObject);
		ajaxCall($scope,$http,"/validatelogin",dataObject,"POST",afterLoginFunction,$window);
	};
	
});

function ajaxCall($scope,$http,url,postData,method,callback,$window) {
    $http({
        url: url,
        method: "POST",
        data: postData,
        headers: {'Content-Type': 'application/json'}
    })
    .then(function(response) 
    	{
    		callback(response,$window);
        }, 
        function(response) 
        {
        	console.log("failure response  :: "+response);
        }
    );
};

function afterLoginFunction(response,$window)
{
	console.log("response  :: "+JSON.stringify(response));
	if(response.data.success)
	{
		$window.location.href = "/"
	}
	else
	{
		alert("invalid username or password");
	}
}

function afterRegistrationFunction(response,$window)
{
	console.log("response  :: "+JSON.stringify(response));
	if(response.data.success)
	{
		$window.location.href = "/"
	}
	else
	{
		alert("registration failed");
	}
}


