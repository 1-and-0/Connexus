package us.connex.controllers;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import us.connex.entities.User;
import us.connex.services.ImageServices;
import us.connex.services.UserService;

@Controller
public class UserController 
{
	private static Logger log = Logger.getLogger(UserController.class.getPackage().getName());
	
	@RequestMapping(value="/addnewuser",method=RequestMethod.POST)
	public @ResponseBody String addNewUser(@RequestBody String userDetailsJson,HttpServletRequest request)
	{
		HashMap userInputMap 			=	null;
		ObjectMapper objectmapper		=	null;
		UserService  userservice		=   null;
		HashMap userMap 				=	null;
		StringWriter strWriter			=	null;
		String finalResult				=	null;
		HttpSession session				=	null;
		
		try
		{
			objectmapper			=		new ObjectMapper();
			userInputMap			= 		objectmapper.readValue(userDetailsJson, HashMap.class);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		
		try
		{
			userservice		=		new UserService();
			userMap			=		userservice.createNewUser(userInputMap);
			if(userMap == null)
			{
				return null;
			}
			
			session			=		request.getSession();
			session.setAttribute("userMap",userMap);
			
			strWriter		=		new StringWriter();
			
			objectmapper.writeValue(strWriter, userMap);
			finalResult		=  		strWriter.toString();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return finalResult;
	}

	@RequestMapping(value="/getuserdetails",method=RequestMethod.GET)
	public @ResponseBody String getUserDetails(@RequestParam(value="userId",required =true)String userId,HttpServletRequest request)
	{
		String finalResult 			=	null;
		UserService  userservice	=   null;
		HttpSession	session			=	null;
		HashMap userMap				=	null;
		StringWriter strWriter		=	null;
		ObjectMapper objectmapper	=   null;
		
		try
		{
			session				=	 request.getSession();
			userMap				=	 (HashMap) session.getAttribute("userMap");
			if(userMap != null)
			{
				objectmapper	=		new ObjectMapper();
				strWriter		=		new StringWriter();
				
				objectmapper.writeValue(strWriter, userMap);
				finalResult		=  		strWriter.toString();
				
				return finalResult;
			}
			
			userservice			=	 new UserService();
			userMap				=	 userservice.getUserDetail(userId);
			
			if(userMap == null)
			{
				return null;
			}
			
			objectmapper	=		new ObjectMapper();
			strWriter		=		new StringWriter();
			
			objectmapper.writeValue(strWriter, userMap);
			finalResult		=  		strWriter.toString();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return finalResult;
	}
	
	@RequestMapping(value="/validatelogin",method=RequestMethod.POST)
	public @ResponseBody String login(@RequestBody String loginCredentials,HttpServletRequest request)
	{
		HashMap 		loginCredentailsMap			=	null;
		ObjectMapper 	objectmapper				=	null;
		HashMap			userMap						=	null;
		UserService  	userservice					=   null;
		String 			finalResult					=	null;
		StringWriter 	strWriter					=	null;
		HttpSession		session						=	null;
		
		try
		{
			objectmapper			=	new ObjectMapper();
			strWriter				=	new StringWriter();
			loginCredentailsMap		=	objectmapper.readValue(loginCredentials,HashMap.class);
			
			if(loginCredentailsMap  == null)
				return null;
			
			userservice				=	new UserService();
			userMap					=	userservice.validateUser(loginCredentailsMap);
			
			if(userMap == null)
				return null;
			else
			{
				if(!(boolean)userMap.get("success"))
					return null;
			}
			
			session					=	request.getSession();
			session.setAttribute("userMap", userMap);
			
			objectmapper.writeValue(strWriter, userMap);
			finalResult				=  strWriter.toString();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return finalResult;
	}
	
	
	@RequestMapping(value="/uploadUserImage/{userId}",method=RequestMethod.POST)
	private @ResponseBody String uploadUserImage(@PathVariable("userId") String userId,@RequestBody String imageDetails)
	{
		String functionResult 					= 	null;
		ImageServices	imageservice			=	null;
		User user								=	null;
		
		try
		{
			imageservice		=	new ImageServices();
			functionResult 		= 	imageservice.uploadProfileImagesToDB(userId, imageDetails);
			if(functionResult == null || functionResult.trim().equalsIgnoreCase(""))
			{
				return null;
			}
			user 				=	new User();
			user.setDisplayPictureUrl(functionResult);
			
			ofy().save().entity(user);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"problem in adding image to  user"+e.getMessage(),e);
			return null;
		}
		return functionResult;
	}
}
