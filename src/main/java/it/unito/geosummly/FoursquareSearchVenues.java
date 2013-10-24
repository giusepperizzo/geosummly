package it.unito.geosummly;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;


/**
 * @author Giacomo Falcone
 *
 * This class built for download venue informations from 4square 
 */

public class FoursquareSearchVenues{
	private FoursquareApi foursquareApi;
	
	//Constructor method
	public FoursquareSearchVenues(){
		//Initialize FoursquareApi
		foursquareApi = new FoursquareApi("SD1NLAAR3HC5DRFKIM3AAMNACXKKT0WKIH301M5WGNJMTLAY", "3T5N0TCD0PSMITOKVU2A41AE0C1Y2MAXTLUV1MUNTTBAIVA0", "http://www.foursquare.com");
	}
	
	//Search venue informations
	public ArrayList<String> searchVenues(int row, int column, double north, double south, double west, double east) throws FoursquareApiException, UnknownHostException {
		
		//Initialize parameters for venues search
		String ne=north+","+east;
		String sw=south+","+west;
		Map<String, String> searchParams = new HashMap<String, String>(); 
		searchParams.put("intent", "browse");
		searchParams.put("ne", ne); 
		searchParams.put("sw", sw);
		
		//Array to return
		ArrayList<String> doclist=new ArrayList<String>(); 
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
	    if(result.getMeta().getCode() == 200) {
	    	   	
    		//Initialize a Gson instance and declare a FoursquareDataObject
	    	Gson gson=new Gson();
    		FoursquareDataObject dataobj;
	    	
	    	//For each point: create a JSON file (using Gson and FoursquareDataObject)
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
	    		doclist.add(obj);
	    	}
	    	return doclist;
    	} 
    	else {
		      System.out.println("Error occured: ");
		      System.out.println("  code: " + result.getMeta().getCode());
		      System.out.println("  type: " + result.getMeta().getErrorType());
		      System.out.println("  detail: " + result.getMeta().getErrorDetail());
		      return doclist;
	    }
	}
 }