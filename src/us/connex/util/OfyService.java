package us.connex.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import us.connex.entities.*;

// To Register the Entity class
public class OfyService 
{
	static 
	{
		factory().register(Image.class);
		factory().register(Stream.class);
		factory().register(User.class);
		factory().register(ViewCount.class);
		factory().register(Subscription.class);
		factory().register(BeaconState.class);
		factory().register(Beacon.class);
	}

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
