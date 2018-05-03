var imageBinaryString	=	null;

var handleFileSelect = function() {
	imageBinaryString	=	null;
	imageDetails		=	null;
	
	var files = document.getElementById('fileValue').files;
    var file = files[0];
    var reader = new FileReader();
    var base64String	=	null;
    if (files && file) 
    {
    	reader.onload = function(readerEvt) {
    		imageBinaryString = readerEvt.target.result;
        };
    }
    reader.readAsDataURL(file);
};

var streamEditor	=	connexusApp.controller('streamEditor',function($scope,$timeout,$http,$location,streamReceiver,subscribedstreamReceiver) {
	
	function getSubscribedStreamDataFromServer(){
		subscribedstreamReceiver.substreamData(function(jsonData){
			$scope.subscribedStreamData						= jsonData;
			$scope.calledServerforSubscription				= true;
			console.log("sub streams Data ::"+$scope.subscribedStreamData );
		});
	}
	
	function getStreamDataFromServer(){
		streamReceiver.streamData(function(jsonData){
			$scope.streamsData 			= jsonData;
			$scope.calledServer			= true;
			console.log("streams Data ::"+$scope.streamsData );
		});
	}

	if(!$scope.calledServer)
		getStreamDataFromServer();
	
	if(!$scope.calledServerforSubscription)
		getSubscribedStreamDataFromServer();
	
	 $scope.$watch("streamsData", function (value) {//I change here
		 console.info(value);	    
		 if(value !=null)
		 {
			 var tmp = value;
			 console.info(tmp[0].streamId);
			 $timeout(function () { $("#"+tmp[0].streamId+"_stream").tooltip({placement : 'right',
				 title:'<ul class="unstyled"><li>'+tmp[0].streamName+'</li><li>no of images :'+tmp[0].imageCount+'<li><li>last Update :'
				 +tmp[0].lastUpdateTime+'<li><li>description :'+tmp[0].streamDescription.value+'<li><ul>'
				 ,html:true}); }, 500);
			 
		 }
	 });
	$scope.calledServer		= 	false;
	$scope.streamsData 		=	null;
	
	$scope.subscribedStreamData				=	new Object();
	$scope.calledServerforSubscription		= 	false;
	
	$scope.streamsSelected	=	new Object();
	
	$scope.newStreamName	=	null;
	$scope.newStremDesc		=	null;
	
	$scope.getStreamImageFile	=	function(){
		handleFileSelect();
	}
	
	$scope.addNewStream = function()
	{
		var dataObject = new Object();
		dataObject.name 		= $scope.newStreamName;
		dataObject.description	= $scope.newStremDesc;
		dataObject.imageData	= imageBinaryString.split(",")[1];
		dataObject.imageDetails	= imageBinaryString.split(",")[0];
		
		ajaxCall($scope,$http,"/addNewStream/"+appData.userId,dataObject,"POST",getStreamDataFromServer);
	}
	
	$scope.deleteCheckedStream	=	function()
	{
		for (stream in $scope.streamsSelected)
		{
			  if ( $scope.streamsSelected.hasOwnProperty(stream)) 
			  {
				  if($scope.streamsSelected[stream])
				  {
					  ajaxCall($scope,$http,"/deleteStream/"+stream+"/"+appData.userId,null,"POST",getStreamDataFromServer);
				  }
			  }
		}
	}
	
	$scope.openStream	=	function(streamId)
	{
		appData.currentStream	=	streamId;
		$location.path("/image/imageView");
	}
});

var streamReceiver		=	connexusApp.factory('streamReceiver',function($http){
	return {
		streamData : function(callback){
			return $http.get('../getStream/'+appData.userId).success(callback);
		}
	}
});

var subscribedstreamReceiver		=	connexusApp.factory('subscribedstreamReceiver',function($http){
	return {
		substreamData : function(callback){
			return $http.get('../getSubscribedStreamData/'+appData.userId).success(callback);
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