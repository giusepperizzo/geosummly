package it.unito.geosummly;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

import junit.framework.TestCase;
import fi.foyt.foursquare.api.FoursquareApiException;

public class FoursquareSearchVenuesTest extends TestCase {

	public void testSearchVenues() throws UnknownHostException, FoursquareApiException {
		Gson gson=new Gson();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> array;
		array=fsv.searchVenues(1, 1, 45.057, 45.0561, 7.6600, 7.6613);
		String s=gson.toJson(array.get(1));
		s=s.replace("\"","");
		
		//Construct the test case
		String s1="{row:1,column:1,venueId:4e1028a36284edb6bacc6d51,venueName:Caffetteria Trentuno,latitude:45.05632787,longitude:7.66053669,categories:[{id:4bf58dd8d48988d1e0931735,name:Coffee Shop,pluralName:Coffee Shops,icon:https://ss1.4sqi.net/img/categories/food/coffeeshop.png,parents:[Food],primary:true}],verified:false,checkinsCount:72,usersCount:38,hereNow:0}";
		
		//Start the tests
		assertNotNull(array);
		assertEquals(s1, s);	
	}
}
