package it.unito.geosummly;

import it.unito.geosummly.io.templates.FoursquareObjectTemplate;
import it.unito.geosummly.utils.PropFactory;

import java.io.IOException;
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
		//foursquareApi.setVersion("20151216");
		foursquareApi.setVersion("20140501");
		//foursquareApi.setVersion("20120131");
		timestamp=System.currentTimeMillis();
	}
	
	/**Search venues informations. Row and column informations are included*/
	public ArrayList<FoursquareObjectTemplate> searchVenues(int row, 
															int column, 
															Double north, 
															Double east, 
															Double south, 
															Double west) 
															throws FoursquareApiException, 
																   UnknownHostException {
		
		try {
			String ne=north+","+east;
			String sw=south+","+west;
			Map<String, String> searchParams = new HashMap<String, String>(); 
			searchParams.put("intent", "browse");
			searchParams.put("ne", ne); 
			searchParams.put("sw", sw);
			ArrayList<FoursquareObjectTemplate> doclist = 
									new ArrayList<FoursquareObjectTemplate>(); 
		    
		    //After client has been initialized we can make queries.
		    Result<VenuesSearchResult> result = foursquareApi.venuesSearch(searchParams);
			//For debug
		    System.out.println("here");
			System.out.println(result.getMeta().getCode());
			System.out.println("");
		    if(result.getMeta().getCode() == 200) {  	
	    		
		    	FoursquareObjectTemplate dataobj;
		    	
		    	for(CompactVenue venue : result.getResult().getVenues()) {
		    		
		    		dataobj=new FoursquareObjectTemplate(row, 
		    											 column, 
		    											 venue.getId(), 
		    											 venue.getName(), 
		    											 venue.getLocation().getLat(),
		    											 venue.getLocation().getLng(), 
		    											 venue.getCategories(), 
		    											 venue.getContact().getEmail(),
		    											 venue.getContact().getPhone(), 
		    											 venue.getContact().getFacebook(), 
		    											 venue.getContact().getTwitter(), 
		    											 venue.getVerified(), 
		    											 venue.getStats().getCheckinsCount(), 
		    											 venue.getStats().getUsersCount(), 
		    											 venue.getUrl(), 
		    											 venue.getHereNow().
		    											 getCount(), 
		    											 this.timestamp);
		    		doclist.add(dataobj);
		    	}
		    	
		    	return doclist;
	    	} 
	    	else {
	    			logger.log(Level.INFO, "Error occurred:\ncode: "+
	    					   result.getMeta().getCode()+
	    					   "\ntype: "+result.getMeta().getErrorType()+
	    					   "\ndetail: "+result.getMeta().getErrorDetail());
	    			
	    			return doclist;
		    }
		} 
		catch(Exception e) {
			e.printStackTrace(); 
			return null;
		}
	}
	
	/**
	 * Get the 4square category tree
	*/
	public HashMap<String, String> getCategoryTree() throws FoursquareApiException, IOException {
		
		Result<Category[]> result= foursquareApi.venuesCategories();
		Category[] mainTree = result.getResult();
		HashMap<String, String> map = new HashMap<String, String>();
		
		//Top categories
		for(int i=0;i<mainTree.length;i++) {
			String cat = mainTree[i].getName();
			Category[] subTree = mainTree[i].getCategories();
			
			//Subcategories
			for(int j=0;j<subTree.length;j++) {
				String subCat = subTree[j].getName();
				Category[] subSubTree = subTree[j].getCategories();
				
				//Subsubcategories
				for(int k=0;k<subSubTree.length;k++) {
					map.put(subSubTree[k].getName(), subCat);
				}
				
				map.put(subCat, cat);
			}
			
			map.put(cat, null);
		}
		
		return map;
	}
 }