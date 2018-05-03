<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.HashMap" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<html ng-app="connexusApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>connexus</title>

<script type="text/javascript" src="/js/appInfo.js"></script>

<script type="text/javascript">
function assignAppInfo()
{
	<% HashMap userMap = (HashMap)session.getAttribute("userMap");%>
	<% if(userMap == null){response.sendRedirect("/");}%>
	
	appData.userId 				= "<%= userMap.get("uId")%>";
	appData.userName			= "<%= userMap.get("uName")%>";
	appData.userEmail			= "<%= userMap.get("uEmail")%>";
	appData.userImageUrl		= "<%= userMap.get("uImgurl")%>";
	appData.userSubscribedNo	= "<%= userMap.get("streamSubscribed")%>";
	appData.userOwnedNo			= "<%= userMap.get("streamOwned")%>";
	
	appData.getLocation();
}
assignAppInfo();

function getBlobStoreUrl()
{
	var returnUrl =  "<%= blobstoreService.createUploadUrl("/uploadFile") %>";
	console.info(returnUrl);
	return returnUrl;
}
</script>

<script type="text/javascript" src="/js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<script type="text/javascript" src="/js/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="/js/jQAllRangeSliders-min.js"></script>

<script type="text/javascript" src="/js/angular.js"></script>
<script type="text/javascript" src="/js/angular-route.min.js"></script>
<script type="text/javascript" src="/js/angular-file-upload.min.js"></script>
<script type="text/javascript" src="/js/connexus-image.js"></script>
<script type="text/javascript" src="/js/connexus-stream.js"></script>
<script type="text/javascript" src="/js/mapController.js"></script>
<script type="text/javascript" src="/js/fileupload.js"></script>
<script type="text/javascript"
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyChfWJy8y0fCVS09RfowfS-b8gUXcwuVeQ
      &sensor=false">
</script> 
<script type="text/javascript" src="/js/markerclusterer_packed.js"></script>  
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/bootstrap-tooltip.js"></script>

<link rel="stylesheet" href="/style/bootstrap.min.css">
<link rel="stylesheet" href="/style/style.css">
<link rel="stylesheet" href="/css/iThing.css"> 

</head>
<body>
	<div id="connexusid" ng-controller="navController">
		<ul class="nav nav-tabs">
			<li class="{{nav['home']}}" ng-click="navclick('home')"><a href="#/user/profile">Profile</a></li>
		    <li class="{{nav['streams']}}" ng-click="navclick('streams')"><a href="#/stream/view">Streams</a></li>
		    <li class="{{nav['search']}}" ng-click="navclick('search')"><a href="#/stream/search">Search</a></li>
			<li class="{{nav['manage']}}" ng-click="navclick('manage')"><a href="#/stream/manage">Manage</a></li>
			<!--<li class="{{nav['images']}}" ng-click="navclick('images')"><a href="#/image/imageView">Images</a></li>  -->
			<li style="float:right;"><a href="/logout">Logout</a></li>
		</ul>
	</div>
	<div>
		<div ng-view></div>
	</div>
</body>

</html>