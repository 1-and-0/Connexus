package us.connex.controllers;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import us.connex.services.StreamServices;

@Controller
public class StreamController 
{
	private static Logger log = Logger.getLogger(StreamController.class.getPackage().getName());
	
	/**
	 * to create new stream data under a user.
	 * @param newStreamData - is the json string value of the new stream to be created
	 * @return - the service result (various return points based on the errors).
	 */
	@RequestMapping(value="/createNewStream/{userid}",method=RequestMethod.POST)
	private @ResponseBody String createNewStream(@PathVariable(value="userid")String userId,@RequestBody String newStreamData)
	{
		String functionResult 					= 	"failure";
		
		HashMap streamData	  					= 	null;
		StreamServices	streamservice			=	null;
		
		Queue	queue							=	null;
		
		try
		{
			//lets put the map value to database. 
			try
			{
				//trigger the queue
				queue = QueueFactory.getQueue("addStream");
				queue.add( TaskOptions.Builder.withUrl("/addStreamToDataStore/"+userId+"/"+System.currentTimeMillis()).param("streamData",newStreamData));
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"problem in adding the stream data to db "+e.getMessage(),e);
				return "problem in adding the data or triggering the queue";
			}
			functionResult = "success";
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			functionResult = "failure";
		}
		return functionResult;
	}
	
	@RequestMapping(value="/addNewStream/{userid}",method=RequestMethod.POST)
	private @ResponseBody String addNewStream(@PathVariable(value="userid")String userId,@RequestBody String newStreamData)
	{
		String functionResult 					= 	"failure";
		StreamServices	streamservice			=	null;
		
		try
		{
			streamservice		=	new StreamServices();
			functionResult 		= 	streamservice.addNewStreamToDB(userId,newStreamData);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			return null;
		}
		return functionResult;
	}
	
	
	@RequestMapping(value="/getStream/{userid}",method=RequestMethod.GET)
	private @ResponseBody String getStreamData(@PathVariable(value="userid") String userId)
	{
		StreamServices	streamservice			=	null;
		String streamListJson					=	null;
		
		try
		{
			streamservice	=	new StreamServices();
			streamListJson	=	streamservice.getAllStreamInfo(userId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			return null;
		}
		return streamListJson;
	}
	
	@RequestMapping(value="/getSubscribedStreamData/{userid}",method=RequestMethod.GET)
	private @ResponseBody String getSubscribedStreamName(@PathVariable(value="userid")String userId)
	{
		StreamServices	streamservice			=	null;
		String streamListJson					=	null;
		
		try
		{
			streamservice	=	new StreamServices();
			streamListJson	=	streamservice.getAllSubscribedStreamInfo(userId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in receving data from the stream"+e.getMessage(),e);
			return null;
		}
		return streamListJson;
	}
	
	@RequestMapping(value="/deleteStream/{streamId}/{userId}",method=RequestMethod.POST)
	private @ResponseBody String deleteStreamData(@PathVariable(value="streamId")String streamId,@PathVariable(value="userId")String userId)
	{
		StreamServices	streamservice			=	null;
		String finalResult						=	null;
		try
		{
			streamservice = new StreamServices();
			finalResult = streamservice.removeStreamData(streamId,userId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			finalResult = "failure";
		}
		return finalResult;
	}
	
	@RequestMapping(value="/subscribeToStream/{streamId}/{userId}",method=RequestMethod.POST)
	private @ResponseBody String subscribeToNewStream(@PathVariable(value="streamId")String streamId,@PathVariable(value="userId")String userId)
	{
		StreamServices	streamservice			=	null;
		String finalResult						=	null;
		try
		{
			streamservice = new StreamServices();
			finalResult   = streamservice.subscribeToStream(streamId,userId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			finalResult = "failure";
		}
		return finalResult;
	}
	
	@RequestMapping(value="/unsubscribeToStream/{streamId}/{userId}",method=RequestMethod.POST)
	private @ResponseBody String unsubscribeToStream(@PathVariable(value="streamId")String streamId,@PathVariable(value="userId")String userId)
	{
		String finalResult					=	null;
		StreamServices streamService		=	null;
		
		try
		{
			streamService = new StreamServices();
			finalResult   = streamService.unsubscribeToStream(streamId,userId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in unsubscribing to stream "+e.getMessage(),e);
			finalResult = "failure";
		}
		
		return finalResult;
	}
}
