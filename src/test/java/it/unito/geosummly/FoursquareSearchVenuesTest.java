package it.unito.geosummly;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

import junit.framework.TestCase;
import fi.foyt.foursquare.api.FoursquareApiException;

public class FoursquareSearchVenuesTest extends TestCase {

	public void testSearchVenues() throws UnknownHostException, FoursquareApiException{
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
		//for(int i=0;i<array.size();i++)
			//System.out.println(gson.toJson(array.get(i)));
		
	}
	
	public void testCreateCategoryList() throws UnknownHostException, FoursquareApiException{
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> array;
		array=fsv.searchVenues(1, 1, 45.057, 45.0561, 7.6600, 7.6613);
		ArrayList<String> actual=fsv.createCategoryList(array);
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Home (private)");
		expected.add("Coffee Shop");
		expected.add("Bar");
		expected.add("Gourmet Shop");
		expected.add("Salon / Barbershop");
		expected.add("Lingerie Store");
		expected.add("Café");
		expected.add("Shoe Store");
		expected.add("Electronics Store");
		expected.add("Mobile Phone Shop");
		expected.add("Kids Store");
		expected.add("Clothing Store");
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for(int i=0;i<actual.size();i++)
			assertEquals(expected.get(i), actual.get(i));
	}
	
	public void testGetCategoryOccurences() throws UnknownHostException, FoursquareApiException{
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> array;
		array=fsv.searchVenues(1, 1, 45.057, 45.0561, 7.6600, 7.6613);
		ArrayList<String> cat_list=new ArrayList<String>();
		cat_list.add("Home (private)");
		cat_list.add("Coffee Shop");
		cat_list.add("Bar");
		cat_list.add("Gourmet Shop");
		cat_list.add("Salon / Barbershop");
		cat_list.add("Lingerie Store");
		cat_list.add("Café");
		cat_list.add("Shoe Store");
		cat_list.add("Electronics Store");
		cat_list.add("Mobile Phone Shop");
		cat_list.add("Kids Store");
		cat_list.add("Clothing Store");
		ArrayList<Integer> actual=fsv.getCategoryOccurences(array, cat_list);
		ArrayList<Integer> expected=new ArrayList<Integer>();
		expected.add(1);
		expected.add(1);
		expected.add(1);
		expected.add(1);
		expected.add(2);
		expected.add(1);
		expected.add(1);
		expected.add(1);
		expected.add(1);
		expected.add(3);
		expected.add(1);
		expected.add(2);
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for(int i=0;i<actual.size();i++){
			assertEquals(expected.get(i), actual.get(i));
		}
	}
}
