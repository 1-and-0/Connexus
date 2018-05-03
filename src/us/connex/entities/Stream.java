package us.connex.entities;

import java.util.Date;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Stream {

	@Id
	private String streamId;
	
	//this is just a duplicate of streamId added only to perform join queries
	@Index
	private String dStreamId;

	@Index
	private String streamName;
	
	@Index
	private String userId;
	
	private Text streamDescription;
	
	private long numberOfImages;
	
	private Date lastUpdateTime;
	
	private String streamImageUrl;
	
	
	@Index
	private Date streamCreationDate;
	

	
	public String getdStreamId() {
		return dStreamId;
	}

	public void setdStreamId(String dStreamId) {
		this.dStreamId = dStreamId;
	}
	
	public String getStreamId() 
	{
		return streamId;
	}

	public void setStreamId(String streamId) 
	{
		this.streamId = streamId;
	}

	public String getStreamName() 
	{
		return streamName;
	}

	public void setStreamName(String streamName) 
	{
		this.streamName = streamName;
	}

	public String getUserId() 
	{
		return userId;
	}

	public void setUserId(String userId) 
	{
		this.userId = userId;
	}

	public Text getStreamDescription() 
	{
		return streamDescription;
	}

	public void setStreamDescription(Text streamDescription) 
	{
		this.streamDescription = streamDescription;
	}

	public long getNumberOfImages() 
	{
		return numberOfImages;
	}

	public void setNumberOfImages(long numberOfImages) 
	{
		this.numberOfImages = numberOfImages;
	}

	
	public Date getLastUpdateTime() 
	{
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) 
	{
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getStreamCreationDate() 
	{
		return streamCreationDate;
	}

	public void setStreamCreationDate(Date streamCreationDate) 
	{
		this.streamCreationDate = streamCreationDate;
	}

	public String getImageUrl() 
	{
		return streamImageUrl;
	}

	public void setImageUrl(String streamImageUrl) 
	{
		this.streamImageUrl = streamImageUrl;
	}
}
