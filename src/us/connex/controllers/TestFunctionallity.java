package us.connex.controllers;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.entities.Image;

@Controller
public class TestFunctionallity {
	@RequestMapping(value="/addImagesRandom.do",method=RequestMethod.GET)
	private @ResponseBody String addRandomImages()
	{	
		List<Image> imageList = new ArrayList<Image>();
		Image image = null;
		try{
			
		for(int i=0;i<10;i++)
		{
			image = new Image();
			image.setDateAdded(new Date());
			image.setImageId(UUID.randomUUID().toString());
			image.setStreamId(image.getImageId()+"_StreamId");
			image.setImageDeleted(false);
			image.setImagePositionInfloat((float)13.060421,(float)80.249583);
			image.setUserId(UUID.randomUUID().toString());
			
			imageList.add(image);
		}
		
		ofy().save().entities(imageList);
		}
		catch(Exception e){
			e.printStackTrace();
			return "failure";
		}
		return "success";
	}
	
	@RequestMapping(value="/queryImages.do",method=RequestMethod.GET)
	private @ResponseBody String getImages(@RequestParam(value="fromDate") String fromDate,@RequestParam(value="toDate") String toDate) throws ParseException
	{
		ArrayList result   = new ArrayList();

		StringWriter str = new StringWriter();
		HashMap resultData = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date fromdate = sdf.parse(fromDate+"T00:00:00.00");
		Date todate   = sdf.parse(toDate+"T23:59:99.99");
		try
		{
			List<Image> images = ofy().load().type(Image.class).filter("dateAdded >", fromdate).filter("dateAdded <", todate).list();
			for(Image img:images)
			{
				resultData = new HashMap();
				resultData.put("lattitude",img.getImageUploadedPosition().getLatitude());
				resultData.put("longitude", img.getImageUploadedPosition().getLongitude());
				resultData.put("imageUrl", "imageUrl-datatocome");
				resultData.put("date", img.getDateAdded());
				resultData.put("imageId", img.getImageId());
				resultData.put("streamId", img.getStreamId());
				result.add(resultData);
			}
			new ObjectMapper().writeValue(str, result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "failure";
		}
		return str.toString();
	}
}
