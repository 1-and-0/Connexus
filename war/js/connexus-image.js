var connexusApp 	=	angular.module('connexusApp', ['ngRoute','angularFileUpload']);

var urlRouting		=	connexusApp.config(function($routeProvider){
	$routeProvider.when('/image/imageView', {
		templateUrl: '/views/imageView.html',
		controller: 'imageController'
	})
	.when('/stream/search', {
		templateUrl: '/views/search.html',
		controller: 'searchController'
	})
	.when('/user/profile', {
		templateUrl: '/views/profile.html',
		controller: 'userController'
	})
	.when('/image/mapView', {
		templateUrl: '/views/mapView.html',
		controller: 'mapViewController'
	})
	.when('/stream/manage', {
		templateUrl: '/views/manage.html',
		controller: 'streamEditor'
	})
	.when('/stream/view', {
		templateUrl: '/views/stream.html',
		controller: 'streamEditor'
	})
	.when('/stream/subscribed', {
		templateUrl: '/views/subscribed.html',
		controller: 'streamEditor'
	})
});

var CMimageBinaryString	= new Object();
var CMhandleFileSelect = function(fileName) {	
	var files = document.getElementsByName(fileName)[0].files;
    var file = files[0];
    var reader = new FileReader();
    if (files && file) 
    {
    	reader.onload = function(readerEvt) {
    		CMimageBinaryString[fileName] = readerEvt.target.result;
        };
    }   
    reader.readAsDataURL(file);
};

var searchController	=   connexusApp.controller('searchController',function($scope,$location,$http,$timeout,SearchstreamReceiver,SubscribeStream) 
{
	$scope.searchText 	=	"";
	$scope.searchStreamsData	=	new Object();
	
	$scope.searchStreams	=	function(){
		if($scope.searchText.trim() == "")
		{
			$scope.searchStreamsData = new Object();
			return;
		}
		SearchstreamReceiver.streamData($scope.searchText,function(jsonData)
		{
			$scope.searchStreamsData 			= jsonData;
		});
	}

	
	$scope.subscribeStreams = function()
	{
		Object.keys($scope.searchStreamsData).forEach(function (key) { 
		   if($scope.searchStreamsData[key].subscribe)
		   {
			   SubscribeStream.subscribe($scope.searchStreamsData[key].streamId,function(jsonData){
					console.info("response");
				});
		   }
		});
	}
	
	$scope.$watch("searchStreamsData", function (value) 
	{	  
		console.info("sss");
		 console.info(value);
		 if(value[0] !=null)
		 {
			 var tmp = value;
			 console.info(tmp[0].streamId);
			 $timeout(function () { $("#"+tmp[0].streamId+"_search").tooltip({placement : 'right',
				 title:'<ul class="unstyled"><li>'+tmp[0].streamName+'</li><li>no of images :'+tmp[0].imageCount+'<li><li>last Update :'
				 +tmp[0].lastUpdateTime+'<li><li>description :'+tmp[0].streamDescription.value+'<li><ul>'
				 ,html:true}); }, 500);
		 }
	});
	
	$scope.openStream	=	function(streamId)
	{
		appData.currentStream	=	streamId;
		$location.path("/image/imageView");
	}
});

var SearchstreamReceiver		=	connexusApp.factory('SearchstreamReceiver',function($http){
	return {
		streamData : function(searchText,callback){
			return $http.get('../searchstreambyname/'+searchText).success(callback);
		}
	}
});

var SubscribeStream		=	connexusApp.factory('SubscribeStream',function($http){
	return {
		subscribe : function(streamId,callback){
			return $http.post('../subscribeToStream/'+streamId+"/"+appData.userId).success(callback);
		}
	}
});
var UserImageBinary = "";

var userController		=   connexusApp.controller('userController',function($scope,$http) 
{
	$scope.uname 		=	appData.userName;
	$scope.umail		=	appData.userEmail;
	$scope.uid			=	appData.userId;
	$scope.scount		=	appData.getStreamCount();
	$scope.sscount		=	appData.getSubscribedStreamCount();
	
	
	$scope.triggerProfileImagefile = function(){
		$("#userImageId").trigger("click");
	}
	
	$scope.profileImageUrl	    =  appData.getImageUrl()
	
	$scope.uploadProfileImages = function(){
		console.info("inside");
		var file = document.getElementById("userImageId").files[0];
		var reader = new FileReader();
		if(file)
		{
			reader.onload = function(readerEvt) {
				UserImageBinary = readerEvt.target.result;
				
				var dataObject = new Object();
				console.info(UserImageBinary);
				dataObject.imageData	= UserImageBinary.split(",")[1];
				dataObject.imageDetails	= UserImageBinary.split(",")[0];
				
				var additionalData	= new Object();
				additionalData.userId = appData.userId;
				
				ajaxCall($scope,$http,"/uploadUserImage/"+appData.userId,dataObject,"POST",changeDisplayInUi,additionalData);
	        };
		}
		reader.readAsDataURL(file);
	}
	
	function changeDisplayInUi(data)
	{
		console.info(data.data);
		if(data.data != null)
		{
			appData.userImageUrl   = data.data;
			$scope.profileImageUrl = data.data;
		}
	}
});

var navController			=	connexusApp.controller('navController',function($scope,$window,$location) {
	
	$scope.nav				=	new Object();
	
	$scope.nav.home			=	'active';
	$scope.nav.streams		=	'';
	$scope.nav.manage		=	'';
	$scope.nav.images		=	'';
	$scope.nav.search		=	'';
	
	$scope.navclick			=	function(option)
	{
		for (nav in $scope.nav)
		{
			  if ( $scope.nav.hasOwnProperty(nav)) 
			  {
				  if(nav == option)
				  {
					  $scope.nav[nav]	=	'active';
				  }
				  else
				  {
					  $scope.nav[nav]		=	'';
				  }
			  }
		}
	}
});


var imageController		=	connexusApp.controller('imageController',function($scope,imageReceiver,$upload,$http,$location) {

	console.info("inside image controller");
	$scope.images			= 	null;
	$scope.calledServer		=	false;
	$scope.imageSize		=	140;
	
	$scope.setImageSize		=	function(size)
	{
		$scope.imageSize	=	size;
	}
	
	$scope.$watch(function() { return $location.path(); }, function(newValue, oldValue){  
	   console.log(newValue+" :: "+oldValue);
	   if(newValue.indexOf("/image/imageView") != -1)
	   {
		   $scope.getImagesDataFromServer();
	   }
	});
	
	$scope.getImagesDataFromServer 	=	function(){
		console.info("getting image from server");
		imageReceiver.imageData(function(jsonData){
			$scope.images 			= jsonData;
			$scope.calledServer		= true;
		});
	}
	
	$scope.getDataForTheCurrentStream = function()
	{
		console.log("inside on change "+appData.getCurrentStream());
		$scope.getImagesDataFromServer();
	}
	
	$scope.getDataForTheCurrentStreamByRange = function()
	{
		$scope.getImagesDataFromServerByRange();
	}
	
	if(!$scope.calledServer)
	{
		$scope.getImagesDataFromServer();
	}
	else
	{
		console.info("already called server");
	}
	
	$scope.url_csv				= 	null;
	$scope.submitCsvForUpload	=	function()	
	{
		if($scope.url_csv == null || $scope.url_csv == "")
			return;
		var urls 		= $scope.url_csv.split(",");
		
		if(urls.length == 0)
			return;
		
		var dataObject		= new Object();
		dataObject.lon 		= appData.longitude;
		dataObject.lat 		= appData.latitude;
		dataObject.urlList	= urls;
		
		var additionalData	= new Object();
		additionalData.userId = appData.userId;
		additionalData.streamName = appData.getCurrentStream();
		
		ajaxCall($scope,$http,"/uploadImagesByUrl/"+appData.userId+"/"+appData.getCurrentStream(),dataObject,"POST",addUrlsToStream,additionalData);
	};
	
	function addUrlsToStream(data,streamData)
	{
		console.info(data);
		console.log(JSON.stringify(data));
		for(var i in data.data)
		{
			var img = new Object();
			img.imageUrl = data.data[i];
			$scope.images.push(img);
		}
	};
	
	$scope.addFileoption 		=	function()	
	{
		this.filecount.push(this.filecount.length+1);
	};
	

	$scope.imFile				=	new Array();
	$scope.filecount			=	[1];
	
	
	$scope.urluploadDisplay				=	'block';
	$scope.fileuploadDisplay			=	'none';
	$scope.isUrluploadDisplayActive		=	'active';
	$scope.isFileuploadDisplayActive	=	''
	
	$scope.makeUrluploadDisplayTrue 	=	function()
	{
		$scope.urluploadDisplay				=	'block';
		$scope.fileuploadDisplay			=	'none';
		$scope.isUrluploadDisplayActive		=	'active';
		$scope.isFileuploadDisplayActive	=	''
	};
	
	$scope.makeFileuploadDisplayTrue	=	function()
	{
		$scope.urluploadDisplay				=	'none';
		$scope.fileuploadDisplay			=	'block';
		$scope.isUrluploadDisplayActive		=	''
		$scope.isFileuploadDisplayActive	=	'active'
	};
	
	$scope.getImageFileForUpload	=	function(fileName)
	{
		CMhandleFileSelect(fileName);
	}
	
	$scope.uploadFiles		= 	function()
	{
		for(fileNo in $scope.filecount)
		{
			var dataObject = new Object();

			dataObject.lon 			= appData.longitude;
			dataObject.lat 			= appData.latitude;
			dataObject.imageData	= CMimageBinaryString["file_"+$scope.filecount[fileNo]].split(",")[1];
			dataObject.imageDetails	= CMimageBinaryString["file_"+$scope.filecount[fileNo]].split(",")[0];
			

			var additionalData	= new Object();
			additionalData.userId = appData.userId;
			additionalData.streamName = appData.getCurrentStream();
			
			ajaxCall($scope,$http,"/uploadImageByFile/"+appData.userId+"/"+appData.getCurrentStream(),dataObject,"POST",addImageToStream,additionalData);
		}
		
		function addImageToStream(data,streamData)
		{
			console.info(data);
			console.log(JSON.stringify(data));
			if(data.data != null || data.data.trim() != "")
			{
				var img = new Object();
				img.imageUrl = data.data;
				$scope.images.push(img);
			}
		};
		
		$scope.filecount =  [1];
		$scope.imFile	=	new Array();
		delete CMimageBinaryString;
		CMimageBinaryString	=	new Object();
	}
});

var mapViewController  	=	connexusApp.controller('mapViewController',function($scope,$http,imageReceiverByRange) {
	
	$scope.images			= 	new Array();
	$scope.markers 			=	new Array();
	$scope.map				=	null;	
	$scope.calledServer		=	false;
	$scope.fromDate			=	null;
	$scope.toDate			=	null;

	$scope.getImagesDataFromServerByRange	=	function(){
		imageReceiverByRange.imageData(function(jsonData){
			$scope.images 			= jsonData;
			$scope.calledServer		= true;
			$scope.markPosition();
		});
	};

	if(!$scope.calledServer)
		$scope.getImagesDataFromServerByRange();
	
	$scope.initMap = function() {
	    var mapOptions = {
	      center: new google.maps.LatLng(0, 0),
	      zoom: 2
	    };
	    $scope.map = new google.maps.Map(document.getElementById("mapPlacer"),mapOptions);
	};
	
	$scope.markPosition = function(){ 
	    $scope.markers = new Array();
	    for (var i = 0; i < $scope.images.length; i++) {
	      //console.info($scope.images[i].latitude+" :: "+$scope.images[i].longitude);
	      var latLng = new google.maps.LatLng($scope.images[i].latitude,
	    		  $scope.images[i].longitude);
	      var marker = new google.maps.Marker({'position': latLng});
	      $scope.markers.push(marker);
	    }
	    var markerCluster = new MarkerClusterer($scope.map, $scope.markers);
	};
	
	
	$scope.initMap();
});

var imageReceiver				=	connexusApp.factory('imageReceiver',function($http){
	return {
		imageData : function(callback){
			return $http.get('../getImages/'+appData.userId+'/'+appData.getCurrentStream()).success(callback);
		}
	}
});

var imageReceiverByRange		=	connexusApp.factory('imageReceiverByRange',function($http){
	return {
		imageData : function(callback){
			return $http.get('../getImagesByRange/'+appData.userId+'/'+appData.getCurrentStream()+'/'+appData.minValue()+'/'+appData.maxValue()).success(callback);
		}
	}
});

function ajaxCall($scope,$http,url,postData,method,callback,additionalData) {
    $http({
        url: url,
        method: method,
        data: postData,
        headers: {'Content-Type': 'application/json'}
    })
    .then(function(response) {
    		console.log("response  :: "+response);
    		callback(response,additionalData);
        }, 
        function(response) { // optional
        	console.log("failure response  :: "+response);
        }
    );
}

