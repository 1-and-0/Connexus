package us.connex.entities;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Subscription 
{
	@Id
	private String subscriptionId;
	
	@Index
	private String userId;
	
	@Index
	private String streamId;
	
	@Index
	private String streamsUserId;
	
	@Index
	private Date subscribedDate;
	

	
	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public String getStreamsUserId() {
		return streamsUserId;
	}

	public void setStreamsUserId(String streamsUserId) {
		this.streamsUserId = streamsUserId;
	}

	public Date getSubscribedDate() {
		return subscribedDate;
	}

	public void setSubscribedDate(Date subscribedDate) {
		this.subscribedDate = subscribedDate;
	}

}
