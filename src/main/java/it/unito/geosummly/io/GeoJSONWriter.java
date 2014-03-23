package it.unito.geosummly.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.google.gson.stream.JsonWriter;

public class GeoJSONWriter implements IGeoWriter{
	
	@Override
	public void writeStream(HashMap<Integer, String> labels, HashMap<Integer, ArrayList<ArrayList<Double>>> cells,
			HashMap<Integer, ArrayList<ArrayList<String>>> venues, double eps, String output, Calendar cal) {
		try {
			
			//Get the current date. Example: 2014-03-19T18:10:57+01:00
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ssXXX");
			dateFormat.setLenient(false); //now month index starts from 1
			String date=dateFormat.format(cal.getTime());
			
			//Create GeoJSON
			OutputStream os= new FileOutputStream(new File(output+"/clustering-output-eps"+eps+""+".geojson"));
	    	ArrayList<Integer> keys=new ArrayList<Integer>(labels.keySet()); //keys of clusters
	    	String name; //cluster label
	    	int key; //cluster key
	    	ArrayList<ArrayList<Double>> cellsOfCluster; //cells informations (cell_id, cell_lat, cell_lng) of a cluster
	    	ArrayList<ArrayList<String>> venuesOfCell; //all venues of a cell
	    	
	    	JsonWriter writer = new JsonWriter(new OutputStreamWriter(os, "UTF-8"));
	        
	        writer.setIndent("  ");
	        writer.beginObject();
	    	writer.name("type").value("FeatureCollection");
	    	writer.name("features");
			writer.beginArray();
			
			//iterate for each cluster
	        for(Integer i: keys) {
	    		name=labels.get(i);
	    		key=i;
	    		cellsOfCluster=new ArrayList<ArrayList<Double>>(cells.get(i));
	    		ArrayList<VenueObject> vo_array=new ArrayList<VenueObject>();
	    		writer.beginObject();
	    		writer.name("type").value("Feature");
		        writer.name("id").value(key);
		        writer.name("geometry");
		        writer.beginObject();
	        	writer.name("type").value("MultiPoint");
	        	writer.name("coordinates");
	        	writer.beginArray();
	        	
	    		//iterate for each cell of the cluster
	    		for(ArrayList<Double> cl: cellsOfCluster) {
	    			DecimalFormat df=new DecimalFormat("#.########");
	    			String s1=df.format(cl.get(1)).replaceAll(",", ".");
	    			String s2=df.format(cl.get(2)).replaceAll(",", ".");
	    			writer.beginArray();
	    			writer.value(Double.parseDouble(s1));
	    			writer.value(Double.parseDouble(s2));
	    			writer.endArray();
	    			
	    			//put venue informations to the list
			        if(venues.containsKey(cl.get(0).intValue())) {
	    				venuesOfCell=new ArrayList<ArrayList<String>>(venues.get(cl.get(0).intValue()));
	    			}
	    			else
	    				venuesOfCell=new ArrayList<ArrayList<String>>();
			        
	    			//iterate for each venue of the cell
	    			for(ArrayList<String> r: venuesOfCell) {
	    				Long timestamp=Long.parseLong(r.get(0));
	    				Integer bH=Integer.parseInt(r.get(1));
	    				Double vLat=Double.parseDouble(df.format(Double.parseDouble(r.get(3))).replaceAll(",", "."));
	    				Double vLng=Double.parseDouble(df.format(Double.parseDouble(r.get(4))).replaceAll(",", "."));
	    				Double fLat=Double.parseDouble(df.format(Double.parseDouble(r.get(5))).replaceAll(",", "."));
	    				Double fLng=Double.parseDouble(df.format(Double.parseDouble(r.get(6))).replaceAll(",", "."));
	    				
	    				//create a VenueObject with the venue informations
	    				VenueObject vo=new VenueObject(timestamp, bH, r.get(2), vLat, vLng, fLat, fLng, r.get(7));
	    				vo_array.add(vo);
	    			}
	    		}
	    		writer.endArray();
		        writer.endObject();
		        writer.name("properties");
	        	writer.beginObject();
	    		writer.name("clusterId").value(key+1);
	    		writer.name("name").value(name);
	    		writer.name("venues");
	    		writer.beginArray();
	    		
	    		//write down all the VenueObjects of the cluster
	    		for(VenueObject obj: vo_array) {
	    			writer.beginObject();
	    			writer.name("timestamp").value(obj.getTimestamp());
	    			if(obj.getBeen_here()>0)
	    				writer.name("beenHere").value(obj.getBeen_here());
	    			writer.name("id").value(obj.getId());
	    			writer.name("venueLatitude").value(obj.getVenue_latitude());
	    			writer.name("venueLongitude").value(obj.getVenue_longitude());
	    			writer.name("centroidLatitude").value(obj.getFocal_latitude());
	    			writer.name("centroidLongitude").value(obj.getFocal_longitude());
	    			writer.name("category").value(obj.getCategory());
	    			writer.endObject();
	    		}
	    		writer.endArray();
	        	writer.endObject();
	        	writer.endObject();
	    	}
	        writer.endArray();
	        writer.name("properties");
	        writer.beginObject();
	        writer.name("name").value("geosummly");
	        writer.name("date").value(date);
			writer.name("eps").value(eps);
	        writer.endObject();
	        writer.endObject();
	        
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**Venue Object Template*/
class VenueObject {
	 
	private long timestamp;
	private int been_here;
	private String id;
	private double venue_latitude;
	private double venue_longitude;
	private double focal_latitude;
	private double focal_longitude;
	private String category;
	
	public VenueObject(long t, int b, String id, double vLat, double vLng, double fLat, double fLng, String c) {
		this.timestamp=t;
		this.been_here=b;
		this.id=id;
		this.venue_latitude=vLat;
		this.venue_longitude=vLng;
		this.focal_latitude=fLat;
		this.focal_longitude=fLng;
		this.category=c;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getBeen_here() {
		return been_here;
	}

	public void setBeen_here(int been_here) {
		this.been_here = been_here;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getVenue_latitude() {
		return venue_latitude;
	}

	public void setVenue_latitude(double venue_latitude) {
		this.venue_latitude = venue_latitude;
	}

	public double getVenue_longitude() {
		return venue_longitude;
	}

	public void setVenue_longitude(double venue_longitude) {
		this.venue_longitude = venue_longitude;
	}

	public double getFocal_latitude() {
		return focal_latitude;
	}

	public void setFocal_latitude(double focal_latitude) {
		this.focal_latitude = focal_latitude;
	}

	public double getFocal_longitude() {
		return focal_longitude;
	}

	public void setFocal_longitude(double focal_longitude) {
		this.focal_longitude = focal_longitude;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}