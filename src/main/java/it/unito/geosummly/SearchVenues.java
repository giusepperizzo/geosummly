package it.unito.geosummly;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class SearchVenues {
	public SearchVenues(){}
	
	public int searchVenues(String ll) throws FoursquareApiException, UnknownHostException {
	    // First we need a initialize FoursquareApi. 
	    FoursquareApi foursquareApi = new FoursquareApi("SD1NLAAR3HC5DRFKIM3AAMNACXKKT0WKIH301M5WGNJMTLAY", "3T5N0TCD0PSMITOKVU2A41AE0C1Y2MAXTLUV1MUNTTBAIVA0", "http://www.foursquare.com");
	    
	    // After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(ll, null, null, null, null, null, null, null, null, null, null);
	    if (result.getMeta().getCode() == 200) {
	    	// if query was ok we can finally do something with the data 
	    	
	    	//e.g. print venues names and categories lists
	    	System.out.println("******************** VENUES NAMES LIST ********************\n");
	    	for (CompactVenue venue : result.getResult().getVenues()) {
	    	  System.out.println(venue.getName());
	    	}
	    	CompactVenue venue_1[] = result.getResult().getVenues();
	    	Category category[];
	    	
	    	System.out.println("\n******************** VENUES CATEGORIES LIST ********************\n");
	    	for(int i=0;i<venue_1.length;i++){
	    		category=venue_1[i].getCategories();
	    		for(int j=0;j<category.length;j++){
	    		  System.out.println(category[j].getName());
	    		}
	    	}
	    	//e.g. insert into mongo db
	    	MongoClient mongoClient=new MongoClient("localhost");
	    	DB db=mongoClient.getDB("myDb");
	    	DBCollection coll=db.getCollection("TestVenues");
	    	BasicDBObject doc;
	    	for (CompactVenue venue : result.getResult().getVenues()) {
		    	doc=new BasicDBObject("Venue Name", venue.getName());  
	    		coll.insert(doc);
	    	}
	    	return 0;
    	} 
    	else {
		      System.out.println("Error occured: ");
		      System.out.println("  code: " + result.getMeta().getCode());
		      System.out.println("  type: " + result.getMeta().getErrorType());
		      System.out.println("  detail: " + result.getMeta().getErrorDetail());
		      return 1;
	    }
	}
 }
