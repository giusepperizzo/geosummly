package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeo;
import org.mapfish.geo.MfGeoFactory;
import org.mapfish.geo.MfGeoJSONReader;
import org.mapfish.geo.MfGeoJSONWriter;
import org.mapfish.geo.MfGeometry;

import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class GeoJSONDataIO {
	
	private MfGeoFactory mfFactory;
    private MfGeoJSONReader reader;
    private JSONStringer stringer;
    private MfGeoJSONWriter writer;
    
    public GeoJSONDataIO() {
    	mfFactory = new MfGeoFactory() {
    		public MfFeature createFeature(String id, MfGeometry geometry, JSONObject properties) {
    			return new InputFeatureTemplate(id, geometry, properties);
            }
        };
        reader = new MfGeoJSONReader(mfFactory);
        stringer=new JSONStringer();
    	writer=new MfGeoJSONWriter(stringer);
    }
    
    public ArrayList<BoundingBox> decode(String path) throws IOException, JSONException {
    	ArrayList<BoundingBox> data=new ArrayList<BoundingBox>();
    	File f=new File(path);
    	InputStream in= new FileInputStream(f);
		String str= IOUtils.toString(in);
		in.close();
		MfGeo result = reader.decode(str); //decode geojson file given as a String
		MfFeatureCollection collection= (MfFeatureCollection) result;
		ArrayList<MfFeature> coll = (ArrayList<MfFeature>) collection.getCollection(); //all the geojson features
		InputFeatureTemplate feature;
		MfGeometry featureGeomtry; 
		Geometry jts;
		Polygon polygon;
		double north;
		double south;
		double west;
		double east;
		BoundingBox b;
		for(MfFeature mf: coll) {
			feature=(InputFeatureTemplate) mf;
			featureGeomtry=feature.getMfGeometry(); //get the feature geometry
			jts=featureGeomtry.getInternalGeometry();
			polygon=(Polygon) jts; //feature geometry is a polygon
			north=polygon.getExteriorRing().getPointN(0).getCoordinate().y; //get the bbox coordinates
			south=polygon.getExteriorRing().getPointN(2).getCoordinate().y;
			west=polygon.getExteriorRing().getPointN(3).getCoordinate().x;
			east=polygon.getExteriorRing().getPointN(1).getCoordinate().x;
			b=new BoundingBox(north, south, west, east);
			data.add(b);
		}
    	return data;
    }
    
    public void encode(HashMap<Integer, String> labels, HashMap<Integer, ArrayList<ArrayList<Double>>> cells, HashMap<Integer, ArrayList<ArrayList<String>>> venues, String out) throws JSONException, IOException {
    	Gson gson=new Gson();
    	ArrayList<Integer> keys=new ArrayList<Integer>(labels.keySet()); //keys of clusters
    	String name; //cluster label
    	int key; //cluster key
    	ArrayList<Coordinate> coordinates; //cells coordinates of a cluster
    	ArrayList<ArrayList<Double>> cellsOfCluster; //cells informations (cell_id, cell_lat, cell_lng) of a cluster
    	ArrayList<ArrayList<String>> venuesOfCell; //venues of a cell
    	MfFeature features; //single feature of geojson file
    	ArrayList<MfFeature> oft=new ArrayList<MfFeature>(); //list of feature of geojson file
    	MfFeatureCollection fc; //feature collection of geojson file (it will contains all the features)
    	
    	//iterate for each cluster
    	for(Integer i: keys) {
    		name=labels.get(i);
    		key=i;
    		coordinates=new ArrayList<Coordinate>();
    		cellsOfCluster=new ArrayList<ArrayList<Double>>(cells.get(i));
    		ArrayList<VenueObject> vo_array=new ArrayList<VenueObject>();
    		
    		//iterate for each cell of the cluster
    		for(ArrayList<Double> cl: cellsOfCluster) {
    			DecimalFormat df=new DecimalFormat("#.##");
    			String s1=df.format(cl.get(1)).replaceAll(",", ".");
    			String s2=df.format(cl.get(2)).replaceAll(",", ".");
    			coordinates.add(new Coordinate( Double.parseDouble(s1), Double.parseDouble(s2)));
    			if(venues.containsKey(cl.get(0).intValue())) {
    				venuesOfCell=new ArrayList<ArrayList<String>>(venues.get(cl.get(0).intValue()));
    			}
    			else
    				venuesOfCell=new ArrayList<ArrayList<String>>();
    			
    			//iterate for each venue of the cell
    			for(ArrayList<String> r: venuesOfCell) {
    				String bH="";
    				if(!r.get(1).equals("0"))
    					bH=r.get(1);
    				String vLat=df.format(Double.parseDouble(r.get(3))).replaceAll(",", ".");
    				String vLng=df.format(Double.parseDouble(r.get(4))).replaceAll(",", ".");
    				String fLat=df.format(Double.parseDouble(r.get(5))).replaceAll(",", ".");
    				String fLng=df.format(Double.parseDouble(r.get(6))).replaceAll(",", ".");
    				VenueObject vo=new VenueObject(r.get(0), bH, r.get(2), vLat, vLng, fLat, fLng, r.get(7));
    				vo_array.add(vo);
    			}
    		}
    		
    		String s=gson.toJson(vo_array.toArray(new VenueObject[vo_array.size()]));
    		//create features
    		features=new OutputFeatureTemplate(key, name, coordinates.toArray(new Coordinate[coordinates.size()]), s);
    		oft.add(features);
    	}
    	
    	//create feature collection with all the feature and serialize the result to file
    	fc=new MfFeatureCollection(oft);
    	writer.encodeFeatureCollection(fc);
    	FileUtils.writeStringToFile(new File(out+"/clustering-output.geojson"), stringer.toString());
    }
}

/**Input template of a feature */
class InputFeatureTemplate extends MfFeature {
	private String id;
	private MfGeometry geometry;

	public InputFeatureTemplate(String id, MfGeometry geometry, JSONObject properties) {
		this.id=id;
		this.geometry=geometry;
	}
	
	@Override
	public String getFeatureId() {
		return id;
	}

	@Override
	public MfGeometry getMfGeometry() {
		return geometry;
	}

	@Override
	public void toJSON(JSONWriter builder) throws JSONException {}

}

/**Output template of a feature */
class OutputFeatureTemplate extends MfFeature {
	private int id;
	private String name;
	private Coordinate[] coordinates;
	private String venues;

	public OutputFeatureTemplate(int id, String name, Coordinate[] coordinates, String venues) {
		this.id=id;
		this.coordinates=coordinates;
		this.name=name;
		this.venues=venues;
	}
	
	@Override
	public String getFeatureId() {
		return id+"";
	}

	@Override
	public MfGeometry getMfGeometry() {
		return new MfGeometry(
				new GeometryFactory().createMultiPoint(coordinates));
	}

	@Override
	public void toJSON(JSONWriter builder) throws JSONException {
		builder.key("clusterId").value(this.id+1);
		builder.key("name").value(this.name);
		builder.key("venues").value(this.venues);
	}

}

class VenueObject {
	 
	private String timestamp;
	private String been_here;
	private String id;
	private String venue_latitude;
	private String venue_longitude;
	private String focal_latitude;
	private String focal_longitude;
	private String category;
	
	public VenueObject(String t, String b, String id, String vLat, String vLng, String fLat, String fLng, String c) {
		this.timestamp=t;
		if(b.length()>0)
			this.been_here=b;
		this.id=id;
		this.venue_latitude=vLat;
		this.venue_longitude=vLng;
		this.focal_latitude=fLat;
		this.focal_longitude=fLng;
		this.category=c;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getBeen_here() {
		return been_here;
	}

	public void setBeen_here(String been_here) {
		this.been_here = been_here;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVenue_latitude() {
		return venue_latitude;
	}

	public void setVenue_latitude(String venue_latitude) {
		this.venue_latitude = venue_latitude;
	}

	public String getVenue_longitude() {
		return venue_longitude;
	}

	public void setVenue_longitude(String venue_longitude) {
		this.venue_longitude = venue_longitude;
	}

	public String getFocal_latitude() {
		return focal_latitude;
	}

	public void setFocal_latitude(String focal_latitude) {
		this.focal_latitude = focal_latitude;
	}

	public String getFocal_longitude() {
		return focal_longitude;
	}

	public void setFocal_longitude(String focal_longitude) {
		this.focal_longitude = focal_longitude;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}

/*class VenueArray {
	private VenueObject[] venues;
	
	public VenueArray(VenueObject[] venues) {
		this.venues=venues;
	}

	public VenueObject[] getVenues() {
		return venues;
	}

	public void setVenues(VenueObject[] venues) {
		this.venues = venues;
	}
	
}*/