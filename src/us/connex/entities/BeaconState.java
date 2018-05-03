package us.connex.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class BeaconState 
{
	@Id
	private String bsid;
	
	private String fieldName;
	private String fieldValue;
	
	public BeaconState()
	{
		
	}
	
	public BeaconState(String id,String fieldName,String fieldValue)
	{
		this.bsid				=	id;
		this.fieldName		=	fieldName;
		this.fieldValue		=	fieldValue;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
