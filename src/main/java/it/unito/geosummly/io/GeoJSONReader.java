package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.io.templates.FeatureCollectionTemplate;
import it.unito.geosummly.io.templates.SamplingFeatureTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeo;
import org.mapfish.geo.MfGeoFactory;
import org.mapfish.geo.MfGeoJSONReader;
import org.mapfish.geo.MfGeometry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class GeoJSONReader {
    
	/**
	 * Decode a geojson file for the sampling state
	 */
    public ArrayList<BoundingBox> decodeForSampling(String file) throws IOException, JSONException {
    	
    	MfGeoFactory mfFactory = new MfGeoFactory() {
    		public MfFeature createFeature(String id, MfGeometry geometry, JSONObject properties) {
    			return new SamplingFeatureTemplate(id, geometry, properties);
            }
        };
        MfGeoJSONReader reader = new MfGeoJSONReader(mfFactory);
    	
    	ArrayList<BoundingBox> data=new ArrayList<BoundingBox>();
    	File f=new File(file);
    	InputStream in= new FileInputStream(f);
		String str= IOUtils.toString(in);

		in.close();
		MfGeo result = reader.decode(str); //decode geojson file given as a String
		MfFeatureCollection collection= (MfFeatureCollection) result;
		ArrayList<MfFeature> coll = (ArrayList<MfFeature>) collection.getCollection(); //all the geojson features
		SamplingFeatureTemplate feature;
		MfGeometry featureGeometry; 
		Geometry jts;
		Polygon polygon;
		double north;
		double east;
		double south;
		double west;
		BoundingBox b;

		
		for(MfFeature mf: coll) {
			
			feature=(SamplingFeatureTemplate) mf;
			featureGeometry=feature.getMfGeometry(); //get the feature geometry
			jts=featureGeometry.getInternalGeometry();
			
			polygon=(Polygon) jts; //feature geometry is a polygon
			north=polygon.getExteriorRing().getPointN(0).getCoordinate().y; //get the bbox coordinates
			east=polygon.getExteriorRing().getPointN(1).getCoordinate().x;
			south=polygon.getExteriorRing().getPointN(2).getCoordinate().y;
			west=polygon.getExteriorRing().getPointN(3).getCoordinate().x;

			b = new BoundingBox(north, east, south, west);
			data.add(b);

		}
		
		//Set the matrix indices
		//First row and col indices are equal to 1
		int size = (int) Math.sqrt(data.size());
		int i=1;
		int j=1;
		for(BoundingBox box: data) {
			
			box.setRow(i);
			box.setColumn(j);
			j++;
			
			if(j>size) { //row complete, continue with the next row
				i++;
				j=1;
			}	
		}
    	return data;
    }
    
    /**
	 * Decode a geojson file for the optimization state
	 */
    public FeatureCollectionTemplate decodeForOptimization(String file) throws IOException {
    	
    	BufferedReader br = new BufferedReader(
				new FileReader(file));
		Gson gson=new GsonBuilder().setPrettyPrinting().create();
		FeatureCollectionTemplate fct=gson.fromJson(br, FeatureCollectionTemplate.class);
		
		return fct;
    }
}