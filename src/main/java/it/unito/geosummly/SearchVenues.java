package it.unito.geosummly;

import java.net.UnknownHostException;

import org.json.JSONWriter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.JSONFieldParser;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class SearchVenues {
	
	//Constructor method
	public SearchVenues(){}
	
	//Search venue's informations given latitude and longitude as parameters
	public int searchVenues(String ll, int row, int column) throws FoursquareApiException, UnknownHostException {
	    
		//Initialize FoursquareApi. 
	    FoursquareApi foursquareApi = new FoursquareApi("SD1NLAAR3HC5DRFKIM3AAMNACXKKT0WKIH301M5WGNJMTLAY", "3T5N0TCD0PSMITOKVU2A41AE0C1Y2MAXTLUV1MUNTTBAIVA0", "http://www.foursquare.com");
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(ll, null, null, null, null, null, null, null, null, null, null);
	    if(result.getMeta().getCode() == 200) {
	    	
	    	//Initialize MongoDB instance
	    	MongoClient mongoClient=new MongoClient("localhost");
	    	DB db=mongoClient.getDB("VenueDB");
	    	DBCollection coll=db.getCollection("ResultVenues");
	    	
	    	//Declare the documents which will contain the JSON results
	    	BasicDBObject doc;
	    	BasicDBObject innerdoc_contact;
	    	BasicDBObject innerdoc_category;
	    	BasicDBObject innerdoc_innercategory;
	    	BasicDBObject innerdoc_stats;
	    	
	    	//For each point: create a JSON file with the informations
	    	for(CompactVenue venue : result.getResult().getVenues()){
	    		
	    		//Initialize the documents which will contain the JSON results
	    		doc=new BasicDBObject();
	    		innerdoc_contact=new BasicDBObject();
	    		innerdoc_category=new BasicDBObject();
	    		innerdoc_innercategory=new BasicDBObject();
	    		innerdoc_stats=new BasicDBObject();
	    		
	    		//Add information about the corresponding cell of the bounding box
	    		doc.append("Box cell", new BasicDBObject("Row", row).append("Column", column));
	    		
	    		//From this point: information using Foursquare
	    		doc.append("Venue ID", venue.getId());
	    		doc.append("Venue name", venue.getName());
	    		doc.append("Latitude", venue.getLocation().getLat());
		    	doc.append("Longitude", venue.getLocation().getLng());
		    	//For each category of the point: append informations such as id, name, plural name, primary status
		    	for(int i=0;i<venue.getCategories().length;i++){
		    		innerdoc_category.append("Category_"+i, innerdoc_innercategory.
		    				append("Category ID", venue.getCategories()[i].getId()).
		    				append("Category name", venue.getCategories()[i].getName()).
		    				append("Category plural name", venue.getCategories()[i].getPluralName()).
		    				append("Category primary", venue.getCategories()[i].getPrimary()));
		    	}
		    	doc.append("Categories", innerdoc_category);
		    	//Contact information has several fields: email, phone, Facebook or Twitter address
		    	doc.append("Contact", innerdoc_contact.
		    			append("Email", venue.getContact().getEmail()).
		    			append("Phone", venue.getContact().getPhone()).
		    			append("Facebook", venue.getContact().getFacebook()).
		    			append("Twitter", venue.getContact().getTwitter()));
		    	doc.append("Verified", venue.getVerified());
		    	//Stats information has two fields: checkins and users count
		    	doc.append("Stats", innerdoc_stats.
		    			append("Checkins count", venue.getStats().getCheckinsCount()).
		    			append("Users counts", venue.getStats().getUsersCount()));
    			doc.append("Url", venue.getUrl());
    			doc.append("Here now", venue.getHereNow().getCount());
    			
    			//Insert JSON document into MongoDB collection
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