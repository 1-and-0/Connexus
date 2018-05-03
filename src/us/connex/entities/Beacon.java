package us.connex.entities;

import java.util.ArrayList;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Beacon 
{
	@Id
	private Long id;
	
	@Index
	private String UUID;
	
	@Index
	private String MAJORID;
	
	@Index
	private String MINORID;
	
	private ArrayList<BeaconState> beaconstates;
	
	public void setBeaconData(String uuid,String majorId,String minorId)
	{
		this.UUID 		=	uuid;
		this.MAJORID	=	majorId;
		this.MINORID	=	minorId;
	}
	
	public ArrayList<BeaconState> getBeaconstates() {
		return beaconstates;
	}
	public void setBeaconstates(ArrayList<BeaconState> beaconstates) {
		this.beaconstates = beaconstates;
	}
	
	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getMAJORID() {
		return MAJORID;
	}

	public void setMAJORID(String mAJORID) {
		MAJORID = mAJORID;
	}

	public String getMINORID() {
		return MINORID;
	}

	public void setMINORID(String mINORID) {
		MINORID = mINORID;
	}
}
