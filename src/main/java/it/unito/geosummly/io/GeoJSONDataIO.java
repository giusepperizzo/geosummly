package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVRecord;
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
    
    public void encode(HashMap<Integer, String> labels, HashMap<Integer, ArrayList<ArrayList<Double>>> cells, HashMap<Integer, ArrayList<CSVRecord>> venues) throws JSONException, IOException {
    	ArrayList<Integer> keys=new ArrayList<Integer>(labels.keySet()); //keys of clusters
    	String name; //cluster label
    	int key; //cluster key
    	ArrayList<Coordinate> coordinates; //cells coordinates of a cluster
    	ArrayList<ArrayList<Double>> cellsOfCluster; //cells informations (cell_id, cell_lat, cell_lng) of a cluster
    	ArrayList<CSVRecord> venuesOfCell; //venues (as CSVRecord) of a cell
    	ArrayList<String> venuesAsString; //venues (as String) of a cell
    	MfFeature features; //single feature of geojson file
    	ArrayList<MfFeature> oft=new ArrayList<MfFeature>(); //list of feature of geojson file
    	MfFeatureCollection fc; //feature collection of geojson file (it will contains all the features)
    	
    	//iterate for each cluster
    	for(Integer i: keys) {
    		name=labels.get(i);
    		key=i;
    		coordinates=new ArrayList<Coordinate>();
    		cellsOfCluster=new ArrayList<ArrayList<Double>>(cells.get(i));
    		venuesAsString=new ArrayList<String>();
    		
    		//iterate for each cell of the cluster
    		for(ArrayList<Double> cl: cellsOfCluster) {
    			coordinates.add(new Coordinate(cl.get(1), cl.get(2)));
    			if(venues.containsKey(cl.get(0).intValue())) {
    				venuesOfCell=new ArrayList<CSVRecord>(venues.get(cl.get(0).intValue()));
    			}
    			else
    				venuesOfCell=new ArrayList<CSVRecord>();
    			
    			//iterate for each venue of the cell
    			for(CSVRecord r: venuesOfCell) {
    				venuesAsString.add(r.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
    			}
    		}
    		
    		//create features
    		features=new OutputFeatureTemplate(key, name, coordinates.toArray(new Coordinate[coordinates.size()]), venuesAsString.toArray(new String[venuesAsString.size()]));
    		oft.add(features);
    	}
    	
    	//create feature collection with all the feature and serialize the result to file
    	fc=new MfFeatureCollection(oft);
    	writer.encodeFeatureCollection(fc);
    	FileUtils.writeStringToFile(new File("clustering-output.geojson"), stringer.toString());
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
	private String[] venues;

	public OutputFeatureTemplate(int id, String name, Coordinate[] coordinates, String[] venues) {
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
