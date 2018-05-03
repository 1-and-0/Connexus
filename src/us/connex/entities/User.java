package us.connex.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class User 
{
	@Id
	private String userIdKey;
	
	@Index
	private String userId;
	
	private String userEmail;
	
	@Index
	private String userName;

	@Index
	private String userPassword;
	
	private String displayPictureUrl;
	
	public String getUserIdKey() 
	{
		return userIdKey;
	}

	public void setUserIdKey(String userIdKey) 
	{
		this.userIdKey = userIdKey;
	}

	public String getUserId() 
	{
		return userId;
	}

	public void setUserId(String userId) 
	{
		this.userId = userId;
	}

	public String getUserEmail() 
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail) 
	{
		this.userEmail = userEmail;
	}

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getUserPassword() 
	{
		return userPassword;
	}

	public void setUserPassword(String userPassword) 
	{
		this.userPassword = userPassword;
	}

	public String getDisplayPictureUrl() {
		return displayPictureUrl;
	}

	public void setDisplayPictureUrl(String displayPictureUrl) {
		this.displayPictureUrl = displayPictureUrl;
	}

}
