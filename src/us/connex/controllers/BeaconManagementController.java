package us.connex.controllers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.services.BeaconManagementService;

@Controller
public class BeaconManagementController 
{
	private static Logger log = Logger.getLogger(BeaconManagementController.class.getPackage().getName());
	
	@RequestMapping(value="/addnewbeacondata",method=RequestMethod.PUT)
	public @ResponseBody String addNewBeaconData(@RequestBody String beaconData)
	{
		ObjectMapper objectmapper					=	null;
		HashMap beaconMap							=	null;
		BeaconManagementService	bmService			=	null;		
		try
		{
			objectmapper	=	new ObjectMapper();
			beaconMap		=	objectmapper.readValue(beaconData, HashMap.class);	
			bmService		=	new BeaconManagementService();
			
			if(!bmService.addNewBeacon(beaconMap))
			{
				return "error";
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error "+e.getMessage(),e);
			return "error";
		}
		
		return "success";
	}
	
	@RequestMapping(value="/retreivebeacondata",method=RequestMethod.GET)
	public @ResponseBody String retriveBeaconData(@RequestParam("uuid") String uuid,@RequestParam("majorid") String majorId,@RequestParam("minorid") String minorId)
	{
		ObjectMapper objectmapper					=	null;
		StringWriter strWriter						=	null;
		ArrayList beaconResultMap						=	null;
		BeaconManagementService	bmService			=	null;	
		
		try
		{
			bmService			=		new BeaconManagementService();
			beaconResultMap		=		bmService.getBecaonData(uuid,majorId,minorId);
			
			if(beaconResultMap!=null)
			{
				objectmapper	=		new ObjectMapper();
				strWriter		=		new StringWriter();
				
				objectmapper.writeValue(strWriter, beaconResultMap);
				return strWriter.toString();
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error "+e.getMessage(),e);
		}
		return "error";
	}
}
