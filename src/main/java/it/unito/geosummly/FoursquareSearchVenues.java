package it.unito.geosummly;

import it.unito.geosummly.io.templates.FoursquareObjectTemplate;
import it.unito.geosummly.utils.PropFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;


/**
 * @author Giacomo Falcone
 *
 * Download venue informations from 4square 
 */

public class FoursquareSearchVenues {
	private FoursquareApi foursquareApi;
	private long timestamp;
	
	public static Logger logger = Logger.getLogger(FoursquareSearchVenues.class.toString());
	
	public FoursquareSearchVenues() {
		//Initialize FoursquareApi
		foursquareApi = new FoursquareApi(
		        PropFactory.config.getProperty("it.unito.geosummly.foursquare.clientID"), 
		        PropFactory.config.getProperty("it.unito.geosummly.foursquare.clientSecret"), 
		        "http://www.foursquare.com");
		timestamp=System.currentTimeMillis();
	}
	
	/**Search venues informations. Row and column informations are included*/
	public ArrayList<FoursquareObjectTemplate> searchVenues(int row, int column, double north, double east, double south, double west) throws FoursquareApiException, UnknownHostException {
		String ne=north+","+east;
		String sw=south+","+west;
		Map<String, String> searchParams = new HashMap<String, String>(); 
		searchParams.put("intent", "browse");
		searchParams.put("ne", ne); 
		searchParams.put("sw", sw);
		ArrayList<FoursquareObjectTemplate> doclist=new ArrayList<FoursquareObjectTemplate>(); 
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
	    if(result.getMeta().getCode() == 200) {  	
    		FoursquareObjectTemplate dataobj;
	    	for(CompactVenue venue : result.getResult().getVenues()) {
	    		dataobj=new FoursquareObjectTemplate(row, column, venue.getId(), venue.getName(), venue.getLocation().getLat(),
	    				venue.getLocation().getLng(), venue.getCategories(), venue.getContact().getEmail(),
	    				venue.getContact().getPhone(), venue.getContact().getFacebook(), venue.getContact().getTwitter(), 
	    				venue.getVerified(), venue.getStats().getCheckinsCount(), venue.getStats().getUsersCount(), 
	    				venue.getUrl(), venue.getHereNow().getCount(), this.timestamp);
	    		doclist.add(dataobj);
	    	}
	    	return doclist;
    	} 
    	else {
    			logger.log(Level.INFO, "Error occurred:\ncode: "+result.getMeta().getCode()+"\ntype: "+result.getMeta().getErrorType()+"\ndetail: "+result.getMeta().getErrorDetail());
    			return doclist;
	    }
	}
	
	/**Search venues informations. Row and column informations are not included*/
	public ArrayList<FoursquareObjectTemplate> searchVenues(double north, double east, double south, double west) throws FoursquareApiException, UnknownHostException {
		String ne=north+","+east;
		String sw=south+","+west;
		Map<String, String> searchParams = new HashMap<String, String>(); 
		searchParams.put("intent", "browse");
		searchParams.put("ne", ne); 
		searchParams.put("sw", sw);
		ArrayList<FoursquareObjectTemplate> doclist=new ArrayList<FoursquareObjectTemplate>(); 
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
	    if(result.getMeta().getCode() == 200) {  	
    		FoursquareObjectTemplate dataobj;
	    	for(CompactVenue venue : result.getResult().getVenues()) {
	    		dataobj=new FoursquareObjectTemplate(venue.getId(), venue.getName(), venue.getLocation().getLat(),
	    				venue.getLocation().getLng(), venue.getCategories(), venue.getContact().getEmail(),
	    				venue.getContact().getPhone(), venue.getContact().getFacebook(), venue.getContact().getTwitter(), 
	    				venue.getVerified(), venue.getStats().getCheckinsCount(), venue.getStats().getUsersCount(), 
	    				venue.getUrl(), venue.getHereNow().getCount(), this.timestamp);
	    		doclist.add(dataobj);
	    	}
	    	return doclist;
    	} 
    	else {
    			logger.log(Level.INFO, "Error occurred:\ncode: "+result.getMeta().getCode()+"\ntype: "+result.getMeta().getErrorType()+"\ndetail: "+result.getMeta().getErrorDetail());
    			return doclist;
	    }
	}
 }