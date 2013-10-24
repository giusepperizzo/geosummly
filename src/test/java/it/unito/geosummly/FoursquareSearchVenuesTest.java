package it.unito.geosummly;

import static org.junit.Assert.*;
import fi.foyt.foursquare.api.FoursquareApiException;
import java.net.UnknownHostException;
import org.junit.Test;

public class FoursquareSearchVenuesTest {

	@Test
	public void testSearchVenues()throws FoursquareApiException, UnknownHostException{
		FoursquareSearchVenues my_sv=new FoursquareSearchVenues();
		String ll="45.05,7.666667";
		int toRet=my_sv.searchVenues(ll, 1, 1);
		assertEquals(0, toRet);
	}


}
