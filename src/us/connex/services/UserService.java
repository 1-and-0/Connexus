package us.connex.services;

import static us.connex.util.OfyService.ofy;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import us.connex.entities.Stream;
import us.connex.entities.Subscription;
import us.connex.entities.User;

public class UserService 
{
	private static Logger log = Logger.getLogger(UserService.class.getPackage().getName());
	
	public HashMap createNewUser(HashMap userMap) 
	{
		User user					=	null;
		HashMap	returnMap			=	null;
		String userId				=	null;
		
		try
		{
			 if((String)userMap.get("userId") == null)
			 {
				 return null;
			 }
			 
			 userId = (String) userMap.get("userId");
			 
			 if(!isUserIdAvailable(userId))
			 {
				 return null;
			 }
			
			user	=	new User();
			
			user.setUserIdKey(userId+new Date().getTime());
			user.setUserEmail((String)userMap.get("userEmail"));
			user.setUserId(userId);
			user.setUserName((String)userMap.get("userName"));
			user.setUserPassword((String)userMap.get("userPass"));
			user.setDisplayPictureUrl("../images/dp.jpg");
			
			ofy().save().entity(user);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		try
		{
			returnMap		=	new HashMap();
			
			returnMap.put("uId", user.getUserId());
			returnMap.put("uEmail", user.getUserEmail());
			returnMap.put("uName", user.getUserName());
			returnMap.put("success", true);
			returnMap.put("userImage",user.getDisplayPictureUrl());
			returnMap.put("streamSubscribed", 0);
			returnMap.put("streamOwned", 0);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return returnMap;
	}

	public HashMap getUserDetail(String userId) 
	{
		List<User> userList			=	null;
		HashMap	 userMap			=	null;
		User	user				=	null;
		int streamSubscribed 		=	0;
		int streamOwned				=	0;
		
		try
		{
			userList	=	ofy().load().type(User.class).filter("userId", userId).list();
			
			if(userList.size() != 1)
			{
				log.info("it should be one ::"+userList.size()+" :: "+userId);
				return null;
			}
			
			user			=	userList.get(0);
			
			if(user == null)
			{
				return null;
			}
			
			userMap			=	new HashMap();
			
			userMap.put("uId", user.getUserId());
			userMap.put("uEmail", user.getUserEmail());
			userMap.put("uName", user.getUserName());
			userMap.put("success", true);
			userMap.put("userImage",user.getDisplayPictureUrl());
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		try
		{
			streamOwned 		= ofy().load().type(Stream.class).filter("userId",userId).list().size();
			streamSubscribed	= ofy().load().type(Subscription.class).filter("userId",userId).list().size();
			
			userMap.put("streamOwned", streamOwned);
			userMap.put("streamSubscribed",streamSubscribed);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return userMap;
	}

	public HashMap validateUser(HashMap loginCredentailsMap) 
	{
		List<User> userList			=	null;
		User user					=	null;
		HashMap	 userMap			=	null;
		String 	 userId				=	null;
		String 	 password			=	null;
		int streamSubscribed 		=	0;
		int streamOwned				=	0;
		
		try
		{
			userId 		=	(String) loginCredentailsMap.get("userId");
			password	=	(String) loginCredentailsMap.get("userPass");
			
			if(userId == null || password == null)
			{
				return null;
			}
			
			userList	=	ofy().load().type(User.class).filter("userId", userId).filter("userPassword", password).list();
			
			if(userList.size() != 1)
			{
				log.info("it should be one ::"+userList.size()+" :: "+"");
				return null;
			}
			
			user			=	userList.get(0);
			if(user == null || user.getUserId() == null)
			{
				log.info("wat can i do with a null user");
				return null;
			}
		
			userMap			=	new HashMap();
			
			userMap.put("uId", user.getUserId());
			userMap.put("uEmail", user.getUserEmail());
			userMap.put("uName", user.getUserName());
			userMap.put("success", true);
			userMap.put("userImage",user.getDisplayPictureUrl());
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			
			userMap		=	new HashMap();
			userMap.put("success", false);
			
			return null;
		}
		try
		{
			streamOwned 		= ofy().load().type(Stream.class).filter("userId",userId).list().size();
			streamSubscribed	= ofy().load().type(Subscription.class).filter("userId",userId).list().size();
			
			userMap.put("streamOwned", streamOwned);
			userMap.put("streamSubscribed",streamSubscribed);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
		return userMap;
	}
	
	public boolean isUserIdAvailable(String userId)
	{
		List<User> userData 	=	null;
		
		try
		{
			userData	=	ofy().load().type(User.class).filter("userId", userId).list();
			if(userData != null && userData.size() == 0)
			{
				return true;
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,e.getMessage(),e);
			return false;
		}
		return false;
	}
}
