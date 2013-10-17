package it.unito.geosummly;

import java.net.UnknownHostException;

import fi.foyt.foursquare.api.FoursquareApiException;
import junit.framework.TestCase;

public class SearchVenuesTest extends TestCase {

	public void testSearchVenues() throws FoursquareApiException, UnknownHostException {
		SearchVenues my_sv=new SearchVenues();
		String ll="45.05,7.666667";
		int toRet=my_sv.searchVenues(ll, 1, 1);
		assertEquals(0, toRet);
	}

}
