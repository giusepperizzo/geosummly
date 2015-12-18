package it.unito.geosummly.io;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.Venue;
import it.unito.geosummly.io.templates.SamplingFeatureTemplate;
import it.unito.geosummly.io.templates.VenueTemplate;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapfish.geo.*;

import java.io.*;
import java.util.*;

/**
 * Created by pysherlock on 12/5/15.
 */
public class CixtyJSONReader {

    /*
        Read and Decode the JSONdata from 3Cixty files
        */
    public ArrayList <Venue> decodeForSampling(String cixtyjson) throws IOException, JSONException  {

    /*    MfGeoFactory mfFactory = new MfGeoFactory() {
            public MfFeature createFeature(String id, MfGeometry geometry, JSONObject properties) {
                return new SamplingFeatureTemplate(id, geometry, properties);
            }
        };
        MfGeoJSONReader reader = new MfGeoJSONReader(mfFactory);

        System.out.println("here\n");
        File f=new File(cixtyjson);
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


        HashMap<String, String>Tree = new HashMap<String, String>();
        ArrayList<Venue> VenueList = new ArrayList<Venue>();  //As Category Tree


        for(MfFeature mf: coll) {

            feature=(SamplingFeatureTemplate) mf;
            featureGeometry=feature.getMfGeometry(); //get the feature geometry
            jts=featureGeometry.getInternalGeometry();

            polygon=(Polygon) jts; //feature geometry is a polygon

        }

*/

        File jsonfile = new File(cixtyjson);
        JSONObject jsonObject = null;
        Scanner input = new Scanner(jsonfile);
        ArrayList<Venue> VenueList = new ArrayList<Venue>();  //As Category Tree
        Venue venue = null;
        double north = 0;
        double east = 0;
        double south = 0;
        double west = 0;
        int num = 0;
    //  BoundingBox VenueBox;

        while(input.hasNext()) {
            try {
                num++;
                jsonObject = new JSONObject(input.nextLine());
            //    System.out.println(jsonObject.toString());
                venue = new Venue(System.currentTimeMillis(),
                        (int)(Math.random()*500 + 1),
                        String.valueOf(num),
                        jsonObject.getJSONObject("category").getString("value"),
                        jsonObject.getJSONObject("label").getString("value"),
                        jsonObject.getJSONObject("latitude").getDouble("value"),
                        jsonObject.getJSONObject("longitude").getDouble("value"));

                venue.setCategory(venue.getCategory().split("/3cixty/")[1]);
                VenueList.add(venue);

                north = venue.getLatitude();
                south = venue.getLatitude();
                east = venue.getLongitude();
                west = venue.getLongitude();

                if(venue.getLatitude() > north)
                    north = venue.getLatitude();
                else if (venue.getLatitude() < south)
                    south = venue.getLatitude();

                if(venue.getLongitude() > east)
                    east = venue.getLongitude();
                else if (venue.getLongitude() < west)
                    west = venue.getLongitude();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            //output.println(venue.toString());
        }
        //Set the matrix indices
        //First row and col indices are equal to 1
    /*    int size = (int) Math.sqrt(VenueList.size());
        int i=1;
        int j=1;
        for(BoundingBox box: VenueList) {
            box.setRow(i);
            box.setColumn(j);
            j++;

            if (j > size) { //row complete, continue with the next row
                i++;
                j = 1;
            }
        }
*/
        System.out.println("North: " + north + " East: " + east + " South: " + south + " West: " + west);
        return VenueList;
    }



  /*  public static void main(String[] args) {
        File jsonfile = new File("/home/pysherlock/Documents/geosummly/3cixty.json");
        JSONObject jsonObject = null;
        Scanner input = null;
        PrintWriter output = null;
        HashMap<String, Object> venueMap = new HashMap<String, Object>(); //store all the venues
    //    LinkedList<Venue> venuelist = new LinkedList<Venue>();  //store the venue with the same key
        Venue venue = null;

        try {
            input = new Scanner(jsonfile);
            output = new PrintWriter("Metadata.csv");
            while(input.hasNext()) {
                try {
                    jsonObject = new JSONObject(input.nextLine());
                    //System.out.println(jsonObject.toString());
                    venue = new Venue(jsonObject.getJSONObject("category").getString("value"),
                            jsonObject.getJSONObject("label").getString("value"),
                            jsonObject.getJSONObject("latitude").getDouble("value"),
                            jsonObject.getJSONObject("longitude").getDouble("value"));
                    //venuelist.add(venue);
                    venueMap.put(venue.getKey(), venue);
                    output.println(venue.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            int keys = 0;
            for (Map.Entry<String, Object> entry : venueMap.entrySet()) {
            //    System.out.println("Key: " + entry.getKey());
                keys++;
            }
            System.out.println("Key num: " + keys);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }*/
}
