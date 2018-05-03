package us.connex.entities;

import java.util.Date;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Image 
{
	@Id
	private String imageId;

	@Index
	private String streamId;
	
	private String userId;
	
	private boolean isImageDeleted;
	
	@Index
	private Date dateAdded;
	
	private GeoPt imageUploadedPosition;
	
	private String imageUrl;
	
	public void setImageUploadedPosition(GeoPt imageUploadedPosition) 
	{
		this.imageUploadedPosition = imageUploadedPosition;
	}
	
	public void setImagePositionInfloat(float latitude,float longitude) 
	{
		this.imageUploadedPosition  = new GeoPt(latitude, longitude);
	}
	
	public GeoPt getImageUploadedPosition() 
	{
		return imageUploadedPosition;
	}
	
	public float getLongitude()
	{
		return this.imageUploadedPosition.getLongitude();
	}
	
	public float getLatitude()
	{
		return this.imageUploadedPosition.getLatitude();
	}
	
	public String getImageId() 
	{
		return imageId;
	}

	public void setImageId(String imageId) 
	{
		this.imageId = imageId;
	}

	public String getStreamId() 
	{
		return streamId;
	}

	public void setStreamId(String streamId) 
	{
		this.streamId = streamId;
	}

	public String getUserId() 
	{
		return userId;
	}

	public void setUserId(String userId) 
	{
		this.userId = userId;
	}

	public boolean isImageDeleted() 
	{
		return isImageDeleted;
	}

	public void setImageDeleted(boolean isImageDeleted) 
	{
		this.isImageDeleted = isImageDeleted;
	}

	public Date getDateAdded() 
	{
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) 
	{
		this.dateAdded = dateAdded;
	}

	public String getImageUrl() 
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) 
	{
		this.imageUrl = imageUrl;
	}
}
