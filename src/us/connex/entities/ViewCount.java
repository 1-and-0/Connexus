package us.connex.entities;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class ViewCount 
{
	// The below values could be used for (Trending now,popular) feature.
	@Id
	private long id;
	
	private String streamId;

	private long hitCount;

	private Date hitDate;
	
	public long getId() 
	{
		return id;
	}
	public void setId(long id) 
	{
		this.id = id;
	}
	public String getStreamId() 
	{
		return streamId;
	}
	public void setStreamId(String streamId) 
	{
		this.streamId = streamId;
	}
	public long getHitCount() 
	{
		return hitCount;
	}
	public void setHitCount(long hitCount) 
	{
		this.hitCount = hitCount;
	}
	public Date getHitDate() 
	{
		return hitDate;
	}
	public void setHitDate(Date hitDate) 
	{
		this.hitDate = hitDate;
	}
}
