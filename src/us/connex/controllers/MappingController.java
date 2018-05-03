package us.connex.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MappingController 
{
	@RequestMapping(value="/",method=RequestMethod.GET)
	private String returnHomePage(HttpServletRequest request,HttpServletResponse response)
	{
		HttpSession	session = null;
		HashMap	userMap		= null;
		try
		{
			session 	=	request.getSession();
			userMap		=	(HashMap) session.getAttribute("userMap");
			
			if(userMap == null)
			{
				return "login";
			}
			else
			{
				return "connexus";
			}
		}
		catch(Exception e)
		{
			return "login";
		}
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	private String logout(HttpServletRequest request,HttpServletResponse response)
	{
		HttpSession session = null;
		try
		{
			session = request.getSession();
			session.removeAttribute("userMap");
			session.invalidate();
			return "login";
		}
		catch(Exception e)
		{
			return "login";
		}
	}
}
