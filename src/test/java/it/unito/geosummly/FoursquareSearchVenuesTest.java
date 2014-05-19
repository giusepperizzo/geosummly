package it.unito.geosummly;

import it.unito.geosummly.io.templates.FoursquareObjectTemplate;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

import junit.framework.TestCase;
import fi.foyt.foursquare.api.FoursquareApiException;

public class FoursquareSearchVenuesTest extends TestCase {

	public void testSearchVenues() throws UnknownHostException, FoursquareApiException {
		Gson gson=new Gson();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareObjectTemplate> array;
		array=fsv.searchVenues(1, 1, 45.057, 7.6613, 45.0561, 7.6600);
		String s=gson.toJson(array.get(1));
		s=s.replace("\"","");
		s=s.substring(0, 48);
		
		//Construct the test case
		String s1="{row:1,column:1,venueId:4e1028a36284edb6bacc6d51";
		
		//Start the tests
		//assertNotNull(array);
		//assertEquals(s1, s);	
	}
}
