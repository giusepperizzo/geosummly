package it.unito.geosummly;

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
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;


/**
 * @author Giacomo Falcone
 *
 * Download venue informations from 4square 
 */

public class FoursquareSearchVenues {
	private FoursquareApi foursquareApi;
	
	public static Logger logger = Logger.getLogger(FoursquareSearchVenues.class.toString());
	
	public FoursquareSearchVenues() {
		//Initialize FoursquareApi
		foursquareApi = new FoursquareApi(
		        PropFactory.config.getProperty("it.unito.geosummly.foursquare.clientID"), 
		        PropFactory.config.getProperty("it.unito.geosummly.foursquare.clientSecret"), 
		        "http://www.foursquare.com");
	}
	
	/**Search venues informations. Row and column informations are included*/
	public ArrayList<FoursquareDataObject> searchVenues(int row, int column, double north, double south, double west, double east) throws FoursquareApiException, UnknownHostException {
		String ne=north+","+east;
		String sw=south+","+west;
		Map<String, String> searchParams = new HashMap<String, String>(); 
		searchParams.put("intent", "browse");
		searchParams.put("ne", ne); 
		searchParams.put("sw", sw);
		ArrayList<FoursquareDataObject> doclist=new ArrayList<FoursquareDataObject>(); 
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
	    if(result.getMeta().getCode() == 200) {  	
    		FoursquareDataObject dataobj;
	    	for(CompactVenue venue : result.getResult().getVenues()) {
	    		dataobj=new FoursquareDataObject(row, column, venue.getId(), venue.getName(), venue.getLocation().getLat(),
	    				venue.getLocation().getLng(), venue.getCategories(), venue.getContact().getEmail(),
	    				venue.getContact().getPhone(), venue.getContact().getFacebook(), venue.getContact().getTwitter(), 
	    				venue.getVerified(), venue.getStats().getCheckinsCount(), venue.getStats().getUsersCount(), 
	    				venue.getUrl(), venue.getHereNow().getCount());
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
	public ArrayList<FoursquareDataObject> searchVenues(double north, double south, double west, double east) throws FoursquareApiException, UnknownHostException {
		String ne=north+","+east;
		String sw=south+","+west;
		Map<String, String> searchParams = new HashMap<String, String>(); 
		searchParams.put("intent", "browse");
		searchParams.put("ne", ne); 
		searchParams.put("sw", sw);
		ArrayList<FoursquareDataObject> doclist=new ArrayList<FoursquareDataObject>(); 
	    
	    //After client has been initialized we can make queries.
	    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
	    if(result.getMeta().getCode() == 200) {  	
    		FoursquareDataObject dataobj;
	    	for(CompactVenue venue : result.getResult().getVenues()) {
	    		dataobj=new FoursquareDataObject(venue.getId(), venue.getName(), venue.getLocation().getLat(),
	    				venue.getLocation().getLng(), venue.getCategories(), venue.getContact().getEmail(),
	    				venue.getContact().getPhone(), venue.getContact().getFacebook(), venue.getContact().getTwitter(), 
	    				venue.getVerified(), venue.getStats().getCheckinsCount(), venue.getStats().getUsersCount(), 
	    				venue.getUrl(), venue.getHereNow().getCount());
	    		doclist.add(dataobj);
	    	}
	    	return doclist;
    	} 
    	else {
    			logger.log(Level.INFO, "Error occurred:\ncode: "+result.getMeta().getCode()+"\ntype: "+result.getMeta().getErrorType()+"\ndetail: "+result.getMeta().getErrorDetail());
    			return doclist;
	    }
	}
	
	//Return the total number of categories for a bounding box cell
	public int getCategoriesNumber(ArrayList<FoursquareDataObject> array) {
		int n=0;
		for(FoursquareDataObject fdo: array) {
			n+=fdo.getCategories().length;
		}
		return n;
	}
	
	//Create a list with distinct categories for a bounding box cell
	public ArrayList<String> createCategoryList(ArrayList<FoursquareDataObject> array) {
		ArrayList<String> categories=new ArrayList<String>();
		for(int i=0; i<array.size();i++){
			Category[] cat_array=array.get(i).getCategories();
			for(int j=0; j<cat_array.length;j++){
				String c;
				if(cat_array[j].getParents().length>0)
					c=cat_array[j].getParents()[0]; //take the parent category name only if it is set
				else
					c=cat_array[j].getName();
				int k=0;
				boolean found=false;
				while(k<categories.size() && !found) {
					String s=categories.get(k);
					if(c.equals((String) s))
						found=true;
					k++;
				}
				if(!found)
					categories.add(c);
			}
		}
		return categories;
	}
		
	//Create a list with the number of occurrences for each distinct category
	public ArrayList<Integer> getCategoryOccurences(ArrayList<FoursquareDataObject> array, ArrayList<String> cat_list) {
		int n;
		ArrayList<Integer> occurrences=new ArrayList<Integer>();
		for(String s: cat_list) {
			n=0;
			for(FoursquareDataObject fdo: array)
				for(Category c: fdo.getCategories()) {
					String str;
					if(c.getParents().length>0)
						str=c.getParents()[0]; //take the parent category name only if it is set
					else
						str=c.getName();
					if(str.equals((String) s))
						n++;
				}
			occurrences.add(n);
		}
		return occurrences;
	}
 }