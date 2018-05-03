package us.connex.controllers;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.entities.Image;
import us.connex.services.ImageHelper;
import us.connex.services.ImageServices;
import us.connex.services.StreamServices;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
public class ImageController 
{
	private static Logger log = Logger.getLogger(ImageController.class.getPackage().getName());
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	/**
	 * the main service to add images - not used now since we do not pass stream id and userid
	 * @param userId
	 * @param streamId
	 * @param timeinmillis
	 * @param imageDetails
	 * @return
	 */
	@RequestMapping(value="/addImage/{userid}/{streamid}/{timeinmillis}",method=RequestMethod.POST)
	private @ResponseBody String addNewImageToStream(@PathVariable(value="userid")String userId,@PathVariable(value="streamid")String streamId,@PathVariable(value="timeinmillis")String timeinmillis,@RequestBody String imageDetails)
	{
		String functionResult 					= 	"failure";
		Queue queue								=	null;
		
		try
		{
			//lets put the map value to database. 
			try
			{
				//trigger the queue
				queue = QueueFactory.getQueue("addImage");
				queue.add( TaskOptions.Builder.withUrl("/addImageToDataStore/"+userId+"/"+streamId+"/"+System.currentTimeMillis()).param("imageDetails",imageDetails));
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,"problem in adding the image data to db "+e.getMessage(),e);
				return "problem in adding the data or triggering the queue";
			}
			functionResult = "success";
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new image "+e.getMessage(),e);
			functionResult = "failure";
		}
		return functionResult;
	}
	
	/**
	 * receive user images based on stream choosen
	 * @param userId
	 * @param streamId
	 * @param timeinmillis
	 * @return
	 */
	@RequestMapping(value="/getImages/{userid}/{streamid}",method=RequestMethod.GET)
	private @ResponseBody String getImagesFromStream(@PathVariable(value="userid")String userId,@PathVariable(value="streamid")String streamId)
	{
		ImageServices imageservice 		= 	null;
		String		  imagesDataJson	=	null;
		
		try
		{
			imageservice	=	new ImageServices();
			imagesDataJson	=	imageservice.getImagesFromStream(streamId);
			
			if(imagesDataJson.trim().equals(""))
				return null;
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in retriving image "+e.getMessage(),e);
			return null;
		}
		return imagesDataJson;
	}
	
	/**
	 * to add images from url under a stream
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value="/uploadImagesByUrl/{userid}/{streamid}",method=RequestMethod.POST)
	public @ResponseBody String uploadImagesFromUrl(@PathVariable(value="userid")String userId,@PathVariable(value="streamid")String streamId,@RequestBody String jsonData)
	{
		Image 		image 				= null;
		HashMap		imageData			= null;
		List<Image>	listOfImage			= null;	
		ArrayList<String> urlList		= null;
		ImageHelper	imageHelper			= null;
		StreamServices streamService	= null;
		List<String> imageUrls			= null;
		
		try
		{
			listOfImage		=	new ArrayList<Image>();
			imageUrls		=	new ArrayList<String>();
			imageHelper		=	new ImageHelper();
			
			imageData		=	new ObjectMapper().readValue(jsonData, HashMap.class);
			urlList			=	(ArrayList<String>) imageData.get("urlList");
			String appUrl	=	null;
			
			try
			{
				for(String urlString : urlList)
				{
					image	=	new Image();
					
					appUrl	= imageHelper.uploadToBlobByUrl(urlString);

					imageUrls.add(appUrl);
					
					image.setDateAdded(new Date());
					image.setImageUrl(appUrl);
					image.setStreamId(streamId);
					image.setUserId(userId);
					image.setImageDeleted(false);
					
					float lat = 0;
					float lon = 0;
					try
					{
						lat = Float.valueOf((String) imageData.get("lat"));
						lon = Float.valueOf((String) imageData.get("lon"));
					}
					catch(Exception e)
					{
						log.log(Level.SEVERE,"lat and long is  :: "+String.valueOf(lat)+String.valueOf(lon)+e.getMessage(),e);
						lat = 0;
						lon = 0;
					}
					image.setImagePositionInfloat(lat,lon);
					
					appUrl 	= null; 
					
					listOfImage.add(image);
				}
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,""+e.getMessage(),e);
				return null;
			}
			ofy().save().entities(listOfImage);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		
		int streamImageNoToAdd = 0;
		try
		{
			streamImageNoToAdd	=	listOfImage.size();
			
			if(streamImageNoToAdd == 0)
			{
				return null;
			}
			
			streamService	=	new StreamServices();
			streamService.updateStreamCount(streamImageNoToAdd, streamId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		
		StringWriter strWriter 	=	null;
		try
		{
			strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, imageUrls);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		return strWriter.toString();
	}
	
	/**
	 * get the images data in Base64 datafromat - > converts to blob ->receives an url
	 * updates the stream count ->returns the uploaded url as a response data.
	 * @param userId
	 * @param streamId
	 * @param imageData
	 * @return
	 */
	@RequestMapping(value="/uploadImageByFile/{userId}/{streamId}",method=RequestMethod.POST)
	public @ResponseBody String uploadFilesToBlob(@PathVariable("userId")String userId,@PathVariable("streamId")String streamId,@RequestBody String imageData)
	{
		String functionResult 					= 	null;
		ImageServices	imageservice			=	null;
		
		try
		{
			imageservice		=	new ImageServices();
			functionResult 		= 	imageservice.uploadImagesToDB(userId, streamId, imageData);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding new stream "+e.getMessage(),e);
			return null;
		}
		return functionResult;
	}
	
	@RequestMapping(value="/getImagesByRange/{userId}/{streamId}/{fDate}/{tDate}",method=RequestMethod.GET)
	private @ResponseBody String getImagesByRange(@PathVariable(value="streamId")String streamId,@PathVariable(value="userId")String userId,@PathVariable(value="fDate")String fromDate,@PathVariable(value="tDate")String toDate)
	{
		String finalResult					=	null;
		ImageServices imageService			=	null;
		
		try
		{
			imageService 		= 	new ImageServices();
			finalResult			=	imageService.getImagesByRange(streamId,fromDate,toDate);
			if(finalResult == null || finalResult.trim().equalsIgnoreCase(""))
			{
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE," "+e.getMessage(),e);
			return null;
		}
		return finalResult;
	}
}
