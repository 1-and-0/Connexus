package us.connex.services;

import static us.connex.util.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.connex.entities.Beacon;
import us.connex.entities.BeaconState;

public class BeaconManagementService 
{
	private static Logger log = Logger.getLogger(BeaconManagementService.class.getPackage().getName());

	public boolean addNewBeacon(HashMap beaconData)
	{
		Beacon beacon						=	null;
		BeaconState	beaconstate				=	null;
		ArrayList<BeaconState> beaconstates	=	null;

		try
		{
			if(beaconData != null)
			{
				if(beaconData.get("beaconStates") != null)
				{
					beaconstates						=	new ArrayList<BeaconState>();
					HashMap<String,String> beaconMap	=	(HashMap)beaconData.get("beaconStates");
					for (Entry<String, String> entry : beaconMap.entrySet()) 
					{
						String key 			= 	entry.getKey();
						String value		=	entry.getValue();
						beaconstate			=	new BeaconState(UUID.randomUUID().toString(),key, value);
						
						beaconstates.add(beaconstate);
					}
				}

				if(beaconData.get("uuid")!=null && beaconData.get("majorId")!=null && beaconData.get("minorId")!=null)
				{
					log.info(beaconData.get("uuid").toString()+" "+beaconData.get("majorId").toString()+" "+beaconData.get("minorId").toString());
					beacon		=	new Beacon();
					beacon.setBeaconData(beaconData.get("uuid").toString(),beaconData.get("majorId").toString(),beaconData.get("minorId").toString());
					beacon.setBeaconstates(beaconstates);
				}
				
				if(beacon!=null)
				{
					ofy().save().entity(beacon);
				}
				else
				{
					return false;
				}
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error "+e.getMessage(),e);
			return false;
		}
		return true;
	}

	public ArrayList getBecaonData(String uuid, String majorId, String minorId) 
	{
		ArrayList dataList			=		null;
		HashMap resultMap			=		null;
		List<Beacon> beaconResult	=		null;	

		try
		{
			beaconResult	=		ofy().load().type(Beacon.class).filter("UUID", uuid).filter("MAJORID", majorId).filter("MINORID", minorId).list();
			dataList		=		new ArrayList();
			resultMap		=		new HashMap();
			for(Beacon beacon:beaconResult)
			{
				if(beacon.getBeaconstates() != null)
				{
					resultMap		=		new HashMap();
					for(BeaconState beaconstate:beacon.getBeaconstates())
					{
						resultMap.put(beaconstate.getFieldName(), beaconstate.getFieldValue());
					}
					dataList.add(resultMap);
				}
			}
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE,"error "+e.getMessage(),e);
			return null;
		}
		return dataList;
	}
}
