var appData =
{
	userId					:	null,
	userName				:	null,
	userEmail				:	null,
	currentStream			:	null,
	
	longitude				:	null,
	latitude				:	null,
	
	userImageUrl			:	"../images/dp.jpg",
	userOwnedNo				:	null,
	userSubscribedNo		:	null,
	
	currentStream			:	null,
	
	minimumVal				:	null,
	maximumVal				:	null,
	
	
	minValue		:	function(){
		
		if(this.minimumVal != null)
		{
			return this.minimumVal;
		}
		console.info($("#dRangeSlider").dateRangeSlider("min"));
		return $("#dRangeSlider").dateRangeSlider("min").getTime();
	},

	maxValue		:	function(){
		
		if(this.maximumVal != null)
		{
			return this.maximumVal;
		}
		console.info($("#dRangeSlider").dateRangeSlider("max"));
		return $("#dRangeSlider").dateRangeSlider("max").getTime();
	},

	getLocation		:	function()
	{
		console.info("inside get location");
		if (navigator.geolocation)
		{
			navigator.geolocation.getCurrentPosition(setPosition);
		}
		else
		{
			console.log("Geolocation is not supported by this browser.");
		}
		
		function setPosition(position)
		{
			this.longitude	=	position.coords.latitude;
			this.latitude	=	position.coords.longitude;
			
			console.info(this.latitude+" :: "+this.longitude+" :: "+position);
			if(this.longitude == undefined || this.longitude == null || this.longitude == '')
				this.longitude = 0;
			if(this.latitude == undefined || this.latitude == null || this.latitude == '')
				this.latitude = 0;
		}
	},
	
	getCurrentStream :	function()
	{
		if(this.currentStream == null)
			return "";
		return this.currentStream.trim();
	},
	
	getImageUrl		:	function()
	{
		if(this.userImageUrl == null || this.userImageUrl.trim() == "" || this.userImageUrl == "null")
			return "../images/dp.jpg";
		else
			return this.userImageUrl;
	},
	
	getStreamCount	: 	function()
	{
		if(this.userOwnedNo == null)
			return 0;
		else
			return this.userOwnedNo;
	},
	
	getSubscribedStreamCount	: function()
	{
		if(this.userSubscribedNo == null)
			return 0;
		else
			return this.userSubscribedNo;
	}
	
}