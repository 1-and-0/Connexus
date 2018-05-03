package us.connex.controllers;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.entities.Stream;

@Controller
public class SearchController 
{
	private static Logger log = Logger.getLogger(SearchController.class.getPackage().getName());
	
	@RequestMapping(value="/searchstreambyname/{streamName}",method=RequestMethod.GET)
	private @ResponseBody String searchStreamsByName(@PathVariable(value="streamName")String streamName)
	{
		log.info("stream info ::"+streamName);
		Iterable<Stream> searchResults 	= 	null;
		List<HashMap> searchData		=	null;
		HashMap	streamMap				=	null;
		
		try
		{
			searchResults				=	ofy().load().type(Stream.class).filter("streamName >=",streamName).filter("streamName <=",streamName+"\uFFFD");			
			searchData					= 	new ArrayList<HashMap>();
			
			for(Stream stream:searchResults)
			{
				streamMap 	=	new HashMap();
				
				streamMap.put("imageCount", stream.getNumberOfImages());
				streamMap.put("streamId", stream.getStreamId());
				streamMap.put("streamName", stream.getStreamName());
				streamMap.put("streamDescription", stream.getStreamDescription());
				streamMap.put("imageUrl", stream.getImageUrl());
				streamMap.put("lastUpdateTime", stream.getLastUpdateTime());
				
				searchData.add(streamMap);
			}
			
			log.info("stream info ::"+searchData.size());
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in searching"+e.getMessage(),e);
			return null;
		}
		
		ObjectMapper objectmapper	=	null;
		StringWriter strWriter		=	null;
		
		try
		{
			objectmapper 	=		new ObjectMapper();
			strWriter		=		new StringWriter();
			
			objectmapper.writeValue(strWriter, searchData);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in searching"+e.getMessage(),e);
			return null;
		}
		return strWriter.toString();
	}
}
