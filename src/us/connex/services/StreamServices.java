package us.connex.services;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.Text;

import us.connex.entities.Stream;
import us.connex.entities.Subscription;


@Controller
public class StreamServices 
{
	private static Logger log = Logger.getLogger(StreamServices.class.getPackage().getName());
	
	public String addNewStreamToDB(String userId,String newStreamData) 
	{
		Stream stream 							=		null;
		String streamName						=		null;
		ObjectMapper objectMapper				=		null;
		HashMap	streamData						=		null;
		String streamDesc						=		null;
		String streamImage						=		null;
		String streamImageUrl					=		null;
		byte[] imageData						=		null;
		ImageHelper	imageHelper					=		null;
		
		try
		{
			stream 		= new Stream();
			streamData 	= new HashMap();
			
			try
			{
				objectMapper	=	new ObjectMapper();
				streamData		=	objectMapper.readValue(newStreamData, HashMap.class);
				
				streamName		=	(String) streamData.get("name");
				
				if(streamName ==null || streamName.trim().equals(""))
				{
					return null;
				}
				
				streamDesc		=	(String)streamData.get("description");
				streamImage		=	(String)streamData.get("imageData");
				
				if(streamImage == null)
				{
					return null;
				}
				
				imageData 			= 	Base64.decodeBase64(streamImage.getBytes());
				imageHelper			=	new ImageHelper();
				streamImageUrl		=	imageHelper.uploadImageDataToBlob(imageData, "jpg");
				
				if(streamImageUrl == null)
				{
					return null;
				}
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"problem in converting streamData to map "+e.getMessage(),e);
				return null;
			}
			String streamId = streamName+"_"+new Date().getTime();
			
			stream.setdStreamId(streamId);
			stream.setStreamId(streamId);
			stream.setStreamDescription(new Text(streamDesc));
			stream.setStreamName(streamName);
			stream.setImageUrl(streamImageUrl);
			stream.setStreamCreationDate(new Date());
			stream.setLastUpdateTime(new Date());
			stream.setNumberOfImages(0);
			stream.setUserId(userId);
			
			ofy().save().entity(stream);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db "+e.getMessage(),e);
			return "false";
		}
		return "true";
	}
	
	@RequestMapping(value="/addStreamToDataStore/{userid}/{timeinmillis}",method=RequestMethod.POST)
	public @ResponseBody String createNewStream(@PathVariable(value="userid")String userId,@PathVariable(value="timeinmillis")String milliseconds,@RequestParam(value="streamData")String newStreamData) 
	{
		log.info("inside addStreamToDataStore ::"+milliseconds+" :: "+newStreamData);
		
		Stream stream 							=		null;
		String streamId							=		null;
		ObjectMapper objectMapper				=		null;
		HashMap	streamData						=		null;
		Random random							=		null;
		
		try
		{
			newStreamData = URLDecoder.decode(newStreamData, "UTF-8");
			log.info("inside addStreamToDataStore ::"+milliseconds+" :: "+newStreamData);
			stream 		= new Stream();
			streamData 	= new HashMap();
			
			try
			{
				objectMapper	=	new ObjectMapper();
				streamData		=	objectMapper.readValue(newStreamData, HashMap.class);
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"problem in converting streamData to map "+e.getMessage(),e);
				return "error in the streamData input";
			}
			
			if(streamData.get("name") !=null && !String.valueOf(streamData.get("name")).trim().equals(""))
			{
				random		=	new Random(); 
				stream.setStreamName((String)streamData.get("name"));
				String streamid	=	String.valueOf(streamData.get("name"))+"_"+String.valueOf(new Date().getTime());
				stream.setStreamId(streamid);
			}
			else
			{
				return "stream name null or empty";
			}
			if(streamData.get("description") !=null && !String.valueOf(streamData.get("description")).trim().equals(""))
			{
				stream.setStreamDescription(new Text((String)streamData.get("description")));
			}
			
			stream.setStreamCreationDate(new Date());
			stream.setLastUpdateTime(new Date());
			stream.setNumberOfImages(0);
			stream.setUserId(userId);
			
			ofy().save().entities(stream);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db "+e.getMessage(),e);
			return "false";
		}
		return "true";
	}

	public String getAllStreamInfo(String userId) 
	{
		HashMap	streamMap					=	 null;
		List<HashMap> getAllStreamData		=	 null;
		String streamListJson				=	 null;
		
		List<Stream>	streamList			=	 null;
		StringWriter stringWriter			=	 null;
		
		ObjectMapper objectMapper 			=	 null;
		
		try
		{
			streamList 					= 	ofy().load().type(Stream.class).filter("userId", userId).list();
			getAllStreamData			= 	new ArrayList<HashMap>();
			
			for(Stream stream:streamList)
			{
				streamMap 	=	new HashMap();
				
				streamMap.put("imageCount", stream.getNumberOfImages());
				streamMap.put("streamId", stream.getStreamId());
				streamMap.put("streamName", stream.getStreamName());
				streamMap.put("streamDescription", stream.getStreamDescription());
				streamMap.put("imageUrl", stream.getImageUrl());
				streamMap.put("lastUpdateTime", stream.getLastUpdateTime());
				
				getAllStreamData.add(streamMap);
			}
			
			try
			{
				if(getAllStreamData.size() == 0)
				{
					return null;
				}
				
				objectMapper 		=	new ObjectMapper();
				stringWriter		=	new StringWriter();
				
				objectMapper.writeValue(stringWriter,getAllStreamData);
				streamListJson 		= 	stringWriter.toString();
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"error in converting list to json "+e.getMessage(),e);
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db "+e.getMessage(),e);
			return null;
		}
		return streamListJson;
	}

	public String removeStreamData(String streamId, String userId) 
	{
		String resultString 		= null;
		List<Stream>	stream		= null;
		try
		{
			resultString = "success";
			
			stream		 =	ofy().load().type(Stream.class).filter("streamId", streamId).filter("userId", userId).list();
			
			if(stream.size() != 1)
				return null;
			
			ofy().delete().entities(stream);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db "+e.getMessage(),e);
			resultString = "failure";
		}
		return resultString;
	}

	//Subscribe to new stream
	public String subscribeToStream(String streamId, String userId) 
	{
		List<Stream> streamToSubscribe 		= 		null;
		Stream	streamData					=		null;	
		Subscription subscriptionData		=		null;
		
		try
		{
			streamToSubscribe 		=	ofy().load().type(Stream.class).filter("streamId", streamId).list();
			
			if(streamToSubscribe.size()!=1 || streamToSubscribe.get(0) == null)
			{
				log.info("some thing is really wrong :: "+streamToSubscribe.size());
				return null;
			}
			
			streamData				=	streamToSubscribe.get(0);
			subscriptionData		=	new Subscription();
			
			subscriptionData.setStreamId(streamData.getStreamId());
			subscriptionData.setStreamsUserId(streamData.getUserId());
			subscriptionData.setUserId(userId);
			subscriptionData.setSubscribedDate(new Date());
			
			ofy().save().entity(subscriptionData);			
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db/or receiving stream to subcribe "+e.getMessage(),e);
			return null;
		}
		return "success";
	}
	
	public String getAllSubscribedStreamInfo(String userId) 
	{
		HashMap	streamMap					=	 null;
		List<HashMap> getAllStreamData		=	 null;
		String streamListJson				=	 null;
		
		ArrayList<String> streamIds			=	 null;
		
		List<Stream>	streamList			=	 null;
		List<Subscription> subscriptions	=	 null;
		
		StringWriter stringWriter			=	 null;
		ObjectMapper objectMapper 			=	 null;
		
		try
		{
			subscriptions				=	ofy().load().type(Subscription.class).filter("userId", userId).list();
			
			if(subscriptions.size() == 0)
			{
				return null;
			}
			
			streamIds		=	new ArrayList<String>();
			for(Subscription subscription :subscriptions)
			{
				streamIds.add(subscription.getStreamId());
			}
			log.info("streamIds "+streamIds.size()+" :: "+streamIds);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in getting subscribed streamId from db "+e.getMessage(),e);
			return null;
		}
		
		try
		{
			streamList 					= 	ofy().load().type(Stream.class).filter("dStreamId IN", streamIds).list();
			getAllStreamData			= 	new ArrayList<HashMap>();
			
			for(Stream stream:streamList)
			{
				streamMap 	=	new HashMap();
				
				streamMap.put("imageCount", stream.getNumberOfImages());
				streamMap.put("streamId", stream.getStreamId());
				streamMap.put("streamName", stream.getStreamName());
				streamMap.put("streamDescription", stream.getStreamDescription());
				streamMap.put("imageUrl", stream.getImageUrl());
				streamMap.put("lastUpdateTime", stream.getLastUpdateTime());
				
				getAllStreamData.add(streamMap);
			}
			
			try
			{
				if(getAllStreamData.size() == 0)
				{
					return null;
				}
				
				objectMapper 		=	new ObjectMapper();
				stringWriter		=	new StringWriter();
				
				objectMapper.writeValue(stringWriter,getAllStreamData);
				streamListJson 		= 	stringWriter.toString();
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"error in converting list to json "+e.getMessage(),e);
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in setting data in db "+e.getMessage(),e);
			return null;
		}
		return streamListJson;
	}

	public String unsubscribeToStream(String streamId, String userId) 
	{
		List<Subscription> streamToUnsubscribe	=	null;
		
		try
		{
			streamToUnsubscribe		=	ofy().load().type(Subscription.class).filter("userId", userId).filter("streamId", streamId).list();
			if(streamToUnsubscribe.size() != 1)
			{
				return null;
			}
			ofy().delete().entities(streamToUnsubscribe);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error in deleting subscription data/or inreceiving subscription data to delete "+e.getMessage(),e);
			return null;
		}
		return "success";
	}
	
	
	public String updateStreamCount(int toAddNumber,String streamId)
	{
		List<Stream> streams	=	null;
		Stream	stream			=	null;
		try
		{
			streams = ofy().load().type(Stream.class).filter("streamId", streamId).list();
			if(streams.size() != 1)
			{
				return null;
			}
			
			stream = streams.get(0);
			stream.setNumberOfImages(stream.getNumberOfImages()+toAddNumber);
			
			ofy().save().entity(stream);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE," "+e.getMessage(),e);
			return null;
		}
		return "success";
	}
}
