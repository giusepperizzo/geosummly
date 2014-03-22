package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeo;
import org.mapfish.geo.MfGeoFactory;
import org.mapfish.geo.MfGeoJSONReader;
import org.mapfish.geo.MfGeometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class GeoJSONReader {
	
	private MfGeoFactory mfFactory;
    private MfGeoJSONReader reader;
    
    public GeoJSONReader() {
    	mfFactory = new MfGeoFactory() {
    		public MfFeature createFeature(String id, MfGeometry geometry, JSONObject properties) {
    			return new InputFeatureTemplate(id, geometry, properties);
            }
        };
        reader = new MfGeoJSONReader(mfFactory);
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
		double east;
		double south;
		double west;;
		BoundingBox b;
		for(MfFeature mf: coll) {
			feature=(InputFeatureTemplate) mf;
			featureGeomtry=feature.getMfGeometry(); //get the feature geometry
			jts=featureGeomtry.getInternalGeometry();
			polygon=(Polygon) jts; //feature geometry is a polygon
			north=polygon.getExteriorRing().getPointN(0).getCoordinate().y; //get the bbox coordinates
			east=polygon.getExteriorRing().getPointN(1).getCoordinate().x;
			south=polygon.getExteriorRing().getPointN(2).getCoordinate().y;
			west=polygon.getExteriorRing().getPointN(3).getCoordinate().x;
			b=new BoundingBox(north, east, south, west);
			data.add(b);
		}
    	return data;
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