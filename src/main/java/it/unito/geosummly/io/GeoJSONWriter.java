package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.io.templates.VenueTemplate;

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
import java.util.List;
import java.util.TimeZone;

import com.google.gson.stream.JsonWriter;

public class GeoJSONWriter implements IGeoWriter{
	
	@Override
	public void writeStream(BoundingBox bbox, HashMap<Integer, String> labels, 
							HashMap<Integer, ArrayList<ArrayList<Double>>> cells,
							HashMap<Integer, ArrayList<ArrayList<String>>> venues, 
							HashMap<Integer, Double> cDistance,
							HashMap<Integer, Double> cSSE,
							HashMap<Integer, Double> cSurface, 
							HashMap<Integer, Double> cHeterogeneity, 
							HashMap<Integer, Double> cDensity, 
							double eps, 
							String output, 
							Calendar cal) 
	{
		try {
			//Get the current date. Example: 2014-03-19T17:10:57.616Z
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ss.SSS'Z'");
			dateFormat.setLenient(false); //now month index starts from 1
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //set UTC time zone
			String date=dateFormat.format(cal.getTime());
			
			//Create GeoJSON
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
        	OutputStream os= new FileOutputStream(new File(dir.getPath().concat("/clustering-output-eps").concat(eps+"").concat(".geojson")));
	    	ArrayList<Integer> keys=new ArrayList<Integer>(labels.keySet()); //keys of clusters
	    	String name; //cluster label
	    	int key; //cluster key
	    	ArrayList<ArrayList<String>> venuesOfCell; //all venues of a cell
	    	DecimalFormat df=new DecimalFormat("#.###############");
	    	
	    	JsonWriter writer = new JsonWriter(new OutputStreamWriter(os, "UTF-8"));
	        
	        writer.setIndent("  ");
	        writer.beginObject();
	    	writer.name("type").value("FeatureCollection");
	    	writer.name("features");
			writer.beginArray();
			
			//iterate for each cluster
	        for(Integer i: keys) 
	        {	
	    		name=labels.get(i);
	    		key=i;
	    		ArrayList<ArrayList<Double>> cellsOfCluster=new ArrayList<ArrayList<Double>>(cells.get(i));
	    		ArrayList<VenueTemplate> vo_array=new ArrayList<VenueTemplate>();
	    		
	    		//iterate for each venue of the cluster
	    		Object tmp=venues.get(key);
	    		if(tmp!=null) {
	    			venuesOfCell=new ArrayList<ArrayList<String>>(venues.get(key));
		    		for(ArrayList<String> r: venuesOfCell) {
	    				Long timestamp=Long.parseLong(r.get(0));
	    				Integer bH=Integer.parseInt(r.get(1));
	    				Double vLat=Double.parseDouble(df.format(Double.parseDouble(r.get(3))).replaceAll(",", "."));
	    				Double vLng=Double.parseDouble(df.format(Double.parseDouble(r.get(4))).replaceAll(",", "."));
	    				Double fLat=Double.parseDouble(df.format(Double.parseDouble(r.get(5))).replaceAll(",", "."));
	    				Double fLng=Double.parseDouble(df.format(Double.parseDouble(r.get(6))).replaceAll(",", "."));
	    				
	    				//create a VenueObject with the venue informations
	    				VenueTemplate vo=new VenueTemplate(timestamp, bH, r.get(2), vLat, vLng, fLat, fLng, r.get(7));
    					vo_array.add(vo);
	    			}
	    		}
	    		
	    		writer.beginObject();
	    		writer.name("type").value("Feature");
		        writer.name("id").value(key);
		        writer.name("geometry");
		        writer.beginObject();
	        	writer.name("type").value("MultiPoint");
	        	writer.name("coordinates");
	        	writer.beginArray();
	        	
	        	// serialize inside the MultiPoint the lat,long of the centroids
	    		//iterate for each cell of the cluster
//	    		for(ArrayList<Double> cl: cellsOfCluster) {
//	    			String s1=df.format(cl.get(1)).replaceAll(",", ".");
//	    			String s2=df.format(cl.get(2)).replaceAll(",", ".");
//	    			writer.beginArray();
//	    			writer.value(Double.parseDouble(s1));
//	    			writer.value(Double.parseDouble(s2));
//	    			writer.endArray();
//	    		}
	        	
	        	//serialize inside the MultiPoint lat, long of the venues
	        	for(VenueTemplate obj: vo_array) 
	        	{
	    			String s1=df.format(obj.getVenueLatitude()).replaceAll(",", ".");
	    			String s2=df.format(obj.getVenueLongitude()).replaceAll(",", ".");
	    			writer.beginArray();
	    			writer.value(Double.parseDouble(s1));
	    			writer.value(Double.parseDouble(s2));
	    			writer.endArray();
	    		}
	    		
	    		writer.endArray();
		        writer.endObject();
		        writer.name("properties");
	        	writer.beginObject();
	    		writer.name("clusterId").value(key+1);
	    		writer.name("name").value(name);
	    		writer.name("surface").value(cSurface.get(key));
	    		writer.name("density").value(cDensity.get(key));
	    		writer.name("heterogeneity").value(cHeterogeneity.get(key));
	    		writer.name("sse").value(cSSE.get(key));
	    		writer.name("distance").value(cDistance.get(key));
	    		writer.name("venues");
	    		writer.beginArray();

	    		//write down all the VenueObjects of the cluster
	    		for(VenueTemplate obj: vo_array) {
	    			writer.beginObject();
	    			writer.name("timestamp").value(obj.getTimestamp());
	    			if(obj.getBeenHere()>0)
	    				writer.name("beenHere").value(obj.getBeenHere());
	    			writer.name("id").value(obj.getId());
	    			writer.name("venueLatitude").value(obj.getVenueLatitude());
	    			writer.name("venueLongitude").value(obj.getVenueLongitude());
	    			writer.name("centroidLatitude").value(obj.getCentroidLatitude());
	    			writer.name("centroidLongitude").value(obj.getCentroidLongitude());
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
	        writer.name("bbox");
	        writer.beginObject();
	        writer.name("north").value(bbox.getNorth());
	        writer.name("east").value(bbox.getEast());
	        writer.name("south").value(bbox.getSouth());
	        writer.name("west").value(bbox.getWest());
	        writer.endObject();
	        writer.name("date").value(date);
			writer.name("eps").value(eps);
	        writer.endObject();
	        writer.endObject();
	       
	        writer.close();
	        os.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writerAfterOptimization(
											BoundingBox bbox, 
											List<Integer> selected, 
											ArrayList<ArrayList<ArrayList<Double>>> multipoints, 
											ArrayList<Double> surface,
											ArrayList<Double> density,
											ArrayList<Double> heterogeneity,											
											ArrayList<Double> sse,
											ArrayList<Double> distance,
											ArrayList<ArrayList<VenueTemplate>> venues, 
											ArrayList<String[]> labels,
											double eps, 
											String date, 
											String output,
											String preamble
										) 
	{	
		try {
			
			//Create GeoJSON
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
        	OutputStream os= new FileOutputStream(
        			new File(dir.getPath().concat("/" + preamble + "-clustering-output-eps").concat(eps+"").concat(".geojson")));
	    	String name=""; //cluster label
	    	int key; //cluster key
	    	ArrayList<ArrayList<Double>> multipointsOfCluster; //cells informations (cell_lat, cell_lng) of a cluster
	    	
	    	JsonWriter writer = new JsonWriter(new OutputStreamWriter(os, "UTF-8"));
	        
	        writer.setIndent("  ");
	        writer.beginObject();
	    	writer.name("type").value("FeatureCollection");
	    	writer.name("features");
			writer.beginArray();
			
			//iterate for each cluster
	        for(int i=0;i<multipoints.size();i++) 
	        {	
	        	name="";
	        	for(String s: labels.get(i))
	    			name=name.concat(s).concat(",");
	    		name=name.substring(0, name.length()-1); //delete last comma
	    		
	    		key=selected.get(i);
	    		multipointsOfCluster=new ArrayList<ArrayList<Double>>(multipoints.get(i)); //get the cells of the ith cluster
	    		
	    		writer.beginObject();
	    		writer.name("type").value("Feature");
		        writer.name("id").value(key-1);
		        writer.name("geometry");
		        writer.beginObject();
	        	writer.name("type").value("MultiPoint");
	        	writer.name("coordinates");
	        	writer.beginArray();
	        	
	    		//iterate for each cell of the cluster
	    		for(ArrayList<Double> cl: multipointsOfCluster) {
	    			writer.beginArray();
	    			writer.value(cl.get(0));
	    			writer.value(cl.get(1));
	    			writer.endArray();
	    		}
	    		writer.endArray();
		        writer.endObject();
		        writer.name("properties");
	        	writer.beginObject();
	    		writer.name("clusterId").value(key);
	    		writer.name("name").value(name);
	    		writer.name("surface").value(surface.get(selected.get(i)-1));
	    		writer.name("density").value(density.get(selected.get(i)-1));
	    		writer.name("heterogeneity").value(heterogeneity.get(selected.get(i)-1));
	    		writer.name("sse").value(sse.get(selected.get(i)-1));
	    		writer.name("distance").value(distance.get(selected.get(i)-1));
	    		writer.name("venues");
	    		writer.beginArray();
	    		
	    		//write down all the VenueObjects of the cluster
	    		for(VenueTemplate obj: venues.get(i)) {
	    			writer.beginObject();
	    			writer.name("timestamp").value(obj.getTimestamp());
	    			if(obj.getBeenHere()>0)
	    				writer.name("beenHere").value(obj.getBeenHere());
	    			writer.name("id").value(obj.getId());
	    			writer.name("venueLatitude").value(obj.getVenueLatitude());
	    			writer.name("venueLongitude").value(obj.getVenueLongitude());
	    			writer.name("centroidLatitude").value(obj.getCentroidLatitude());
	    			writer.name("centroidLongitude").value(obj.getCentroidLongitude());
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
	        writer.name("bbox");
	        writer.beginObject();
	        writer.name("north").value(bbox.getNorth());
	        writer.name("east").value(bbox.getEast());
	        writer.name("south").value(bbox.getSouth());
	        writer.name("west").value(bbox.getWest());
	        writer.endObject();
	        writer.name("date").value(date);
			writer.name("eps").value(eps);
	        writer.endObject();
	        writer.endObject();
	       
	        writer.close();
	        os.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}