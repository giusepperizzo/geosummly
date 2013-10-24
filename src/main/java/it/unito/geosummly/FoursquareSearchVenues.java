package it.unito.geosummly;

import java.net.UnknownHostException;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class FoursquareSearchVenues {
	
	//Constructor method
	public FoursquareSearchVenues(){}
	
	//Search venue's informations given latitude and longitude as parameters
	public int searchVenues(String ll, int row, int column) throws FoursquareApiException, UnknownHostException {
	    
		//Initialize FoursquareApi. 
	    FoursquareApi foursquareApi = new FoursquareApi("SD1NLAAR3HC5DRFKIM3AAMNACXKKT0WKIH301M5WGNJMTLAY", "3T5N0TCD0PSMITOKVU2A41AE0C1Y2MAXTLUV1MUNTTBAIVA0", "http://www.foursquare.com");
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(ll, null, null, null, null, null, null, null, null, null, null);
	    if(result.getMeta().getCode() == 200) {
	    	
	    	//Initialize a MongoDB instance
	    	MongoClient mongoClient=new MongoClient("localhost");
	    	DB db=mongoClient.getDB("VenueDB");
	    	DBCollection coll=db.getCollection("ResultVenues");
	    	   	
    		//Initialize a Gson instance and declare a FoursquareDataObject
	    	Gson gson=new Gson();
    		FoursquareDataObject dataobj;
    		
    		//Declare the document which will contain the JSON results for MongoDB 
    		BasicDBObject doc;
	    	
	    	//For each point: create a JSON file (using Gson and FoursquareDataObject) and store it in MongoDB  
	    	for(CompactVenue venue : result.getResult().getVenues()){
	    		
	    		//Initialize the FoursquareDataObject and fill it with the venue informations
	    		dataobj=new FoursquareDataObject();
	    		dataobj.setRow(row);
	    		dataobj.setColumn(column);
	    		dataobj.setVenueId(venue.getId());
	    		dataobj.setVenueName(venue.getName());
	    		dataobj.setLatitude(venue.getLocation().getLat());
	    		dataobj.setLongitude(venue.getLocation().getLng());
	    		dataobj.setCategories(venue.getCategories());
	    		dataobj.setEmail(venue.getContact().getEmail());
	    		dataobj.setPhone(venue.getContact().getPhone());
	    		dataobj.setFacebook(venue.getContact().getFacebook());
	    		dataobj.setTwitter(venue.getContact().getTwitter());
	    		dataobj.setVerified(venue.getVerified());
	    		dataobj.setCheckinsCount(venue.getStats().getCheckinsCount());
	    		dataobj.setUsersCount(venue.getStats().getUsersCount());
	    		dataobj.setUrl(venue.getUrl());
	    		dataobj.setHereNow(venue.getHereNow().getCount());
	    		String obj=gson.toJson(dataobj);
	    		
	    		//Initialize the document which will contain the JSON result parsed for MongoDB
	    		doc= (BasicDBObject) JSON.parse(obj);
	    		
	    		//Insert the document into MongoDB collection
	    		coll.insert(doc);
	    	}
	    	
	    	//Print JSON files (just for debug)
	    	DBCursor cursorDocJSON = coll.find();
	    	while (cursorDocJSON.hasNext()) {
	    		System.out.println(cursorDocJSON.next());
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