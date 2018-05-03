package us.connex.services;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.entities.Image;
import us.connex.entities.Stream;


@Controller
public class ImageServices 
{
	private static final Logger log		=	Logger.getLogger(ImageServices.class.getName()) ;

	@RequestMapping(value="/addImageToDataStore/{userid}/{streamid}/{timeinmillis}",method=RequestMethod.POST)
	private @ResponseBody String addImageTodb(@PathVariable(value="userid")String userId,@PathVariable(value="streamid")String streamId,@RequestParam(value="imageDetails",required=true) String imageData)
	{
		log.info("inside add image to data store queue ::"+imageData);
		List<Image> imagesList 	=	null;
		Image		image		=	null;
		String 		imageUrl	=	null;

		ArrayList<HashMap> imageDetailsMap  	=	null;
		ObjectMapper objectmapper				=	null;
		ImageHelper  imageHelper				=	null;

		try
		{
			imageDetailsMap  		=	new ArrayList<HashMap>();
			try
			{
				objectmapper		=	new ObjectMapper();
				imageDetailsMap		=	objectmapper.readValue(imageData, ArrayList.class);

				if(imageDetailsMap == null || imageDetailsMap.size() == 0)
				{
					log.info("input map is null or data should be invalid ::"+imageData);
					return null;
				}
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,""+e.getMessage(),e);
				return null;
			}
			try
			{
				imagesList			=	new ArrayList<Image>();
				imageHelper			=	new ImageHelper();

				for(HashMap imageDataMap : imageDetailsMap)
				{
					if((boolean) imageDataMap.get("isByUrl"))
					{
						if(imageDataMap.get("imageUrl") != null)
							imageUrl	=	imageHelper.uploadToBlobByUrl(String.valueOf(imageDataMap.get("imageUrl")));
						else
							return null;
					}
					image		=	new Image();

					image.setDateAdded(new Date());
					image.setImageUrl(imageUrl);
					image.setStreamId(streamId);
					image.setUserId(userId);
					image.setImageDeleted(false);
					
					if(imageDataMap.get("lattitude") != null && imageDataMap.get("longitude") != null)
						image.setImagePositionInfloat(Float.valueOf(imageDataMap.get("lattitude").toString()), Float.valueOf(imageDataMap.get("longitude").toString()));

					imagesList.add(image);
				}

				ofy().save().entities(imagesList);
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,""+e.getMessage(),e);
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		return "success";
	}

	public String getImagesFromStream(String streamId)
	{
		List<Image>			imageList		=	null;
		ArrayList<HashMap>	fullImageData	=	null;
		HashMap				imageMap		=	null;

		String 				imgaesDataJson	=	null;
		StringWriter		stringWriter	=	null;

		try
		{
			stringWriter	=	new StringWriter();
			imageList		=	ofy().load().type(Image.class).filter("streamId", streamId).list();
			fullImageData	=	new ArrayList<HashMap>();

			for(Image image:imageList)
			{
				imageMap 	=	new HashMap();

				imageMap.put("imageId",image.getImageId());
				imageMap.put("imageUrl",image.getImageUrl());
				imageMap.put("dateAdded",image.getDateAdded());
				imageMap.put("streamId", image.getStreamId());
				imageMap.put("longitude", image.getLongitude());
				imageMap.put("latitude", image.getLatitude());
				
				fullImageData.add(imageMap);
			}

			new ObjectMapper().writeValue(stringWriter,fullImageData);
			imgaesDataJson	=	stringWriter.toString();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		return imgaesDataJson;
	}
	

	public String getImagesByRange(String streamId, String fromDate, String toDate) 
	{
		Date fromdate						=	null;
		Date todate							=	null;
		
		List<Image>			imageList		=	null;
		ArrayList<HashMap>	fullImageData	=	null;
		HashMap				imageMap		=	null;

		String 				imgaesDataJson	=	null;
		StringWriter		stringWriter	=	null;

		try
		{
			fromdate	=	new Date(Long.valueOf(fromDate));
			todate		=	new Date(Long.valueOf(toDate));
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE," "+e.getMessage(),e);
			return null;
		}
		
		try
		{
			stringWriter	=	new StringWriter();
			imageList		=	ofy().load().type(Image.class).filter("streamId", streamId).filter("streamCreationDate >=", fromdate).filter("streamCreationDate <=", todate).list();
			fullImageData	=	new ArrayList<HashMap>();

			for(Image image:imageList)
			{
				imageMap 	=	new HashMap();

				imageMap.put("imageId",image.getImageId());
				imageMap.put("imageUrl",image.getImageUrl());
				imageMap.put("dateAdded",image.getDateAdded());
				imageMap.put("streamId", image.getStreamId());
				imageMap.put("longitude", image.getLongitude());
				imageMap.put("latitude", image.getLatitude());
				
				fullImageData.add(imageMap);
			}

			new ObjectMapper().writeValue(stringWriter,fullImageData);
			imgaesDataJson	=	stringWriter.toString();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		return imgaesDataJson;
	}
	
	public String uploadImagesToDB(String userId,String streamId,String imageDetails)
	{
		String 	imageUrl 					=	null;
		Image	image						=	null;
		ImageHelper	imageHelper				=	null;
		
		ObjectMapper objectMapper			=	null;
		
		HashMap	imageMap					=	null;
		byte[] imageData					=	null;
		String imageType					=	null;
		String imageDataString				=	null;
		StreamServices streamservices		=	null;
		
		try
		{
			objectMapper		=	new ObjectMapper();
			imageMap			=	objectMapper.readValue(imageDetails, HashMap.class);
			
			imageType			=	(String)imageMap.get("imageDetails");
			imageDataString		=	(String)imageMap.get("imageData");
			
			imageData 			= 	Base64.decodeBase64(imageDataString.getBytes());
			
			imageHelper			=	new ImageHelper();
			imageUrl			=	imageHelper.uploadImageDataToBlob(imageData, "jpg");
			
			if(imageUrl == null)
			{
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		
		try
		{
			image		=	new Image();

			image.setDateAdded(new Date());
			image.setImageUrl(imageUrl);
			image.setStreamId(streamId);
			image.setUserId(userId);
			image.setImageDeleted(false);
			
			float lat = 0;
			float lon = 0;
			try
			{
				lat = Float.valueOf((String) imageMap.get("lat"));
				lon = Float.valueOf((String) imageMap.get("lon"));
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE,""+e.getMessage(),e);
				lat = 0;
				lon = 0;
			}
			image.setImagePositionInfloat(lat,lon);
			
			ofy().save().entity(image);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		
		try
		{
			streamservices	=	new StreamServices();
			streamservices.updateStreamCount(1, streamId);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		return imageUrl;
	}

	public String uploadProfileImagesToDB(String userId, String imageDetails) 
	{
		String 	imageUrl 					=	null;
		ImageHelper	imageHelper				=	null;
		ObjectMapper objectMapper			=	null;
		
		HashMap	imageMap					=	null;
		byte[] imageData					=	null;
		String imageType					=	null;
		String imageDataString				=	null;
		
		try
		{
			objectMapper		=	new ObjectMapper();
			imageMap			=	objectMapper.readValue(imageDetails, HashMap.class);
			
			imageType			=	(String)imageMap.get("imageDetails");
			imageDataString		=	(String)imageMap.get("imageData");
			
			imageData 			= 	Base64.decodeBase64(imageDataString.getBytes());
			
			imageHelper			=	new ImageHelper();
			imageUrl			=	imageHelper.uploadImageDataToBlob(imageData, "jpg");
			
			if(imageUrl == null)
			{
				return null;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,""+e.getMessage(),e);
			return null;
		}
		
		return imageUrl;
	}
}
