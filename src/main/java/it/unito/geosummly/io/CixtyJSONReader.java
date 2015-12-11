package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by pysherlock on 12/5/15.
 */
public class CixtyJSONReader {

    private HashMap<String, String>CategoryTree;

    public HashMap<String, String> getCategoryTree() {
        return CategoryTree;
    }

        /*
        Read and Decode the JSONdata from 3Cixty files
        */
    public ArrayList <BoundingBox> decodeForSampling(String cixtyjson) throws IOException, JSONException  {
        File jsonfile = new File(cixtyjson);
        //File MateData = null;
        JSONObject jsonObject = null;
        Scanner input = new Scanner(jsonfile);
        //PrintWriter output = new PrintWriter("Matedata.csv");
        ArrayList<BoundingBox> VenueList = new ArrayList<BoundingBox>();  //store the venue with the same key
        Venue venue = null;
        double north = 0;
        double east = 0;
        double south = 0;
        double west = 0;
        BoundingBox VENUE;

        while(input.hasNext()) {
            try {
                jsonObject = new JSONObject(input.nextLine());
                System.out.println(jsonObject.toString());
                venue = new Venue(jsonObject.getJSONObject("category").getString("value"),
                        jsonObject.getJSONObject("label").getString("value"),
                        jsonObject.getJSONObject("latitude").getDouble("value"),
                        jsonObject.getJSONObject("longitude").getDouble("value"));

                north = venue.getLatitude() + 0.002006698267435473;
                east = venue.getLongitude() + 0.0032294008231027185;
                south = venue.getLatitude()- 0.002006698267435473;
                west = venue.getLongitude() - 0.0032294008231027185;
                VENUE = new BoundingBox(north, east, south, west);
                VenueList.add(VENUE);
                CategoryTree.put(venue.getKey(), null);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            //  output.println(venue.toString());

        }
        //Set the matrix indices
        //First row and col indices are equal to 1
        int size = (int) Math.sqrt(VenueList.size());
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
        //MateData = new File("Matedata.csv");
        return VenueList;
    }

    public class Venue {
        String Key;
        String Label;
        Double Latitude;
        Double Longitude;

        public Venue(){}

        public Venue(String key, String label, Double latitude, Double longitude) {
            Key = key;
            Label = label;
            Latitude = latitude;
            Longitude = longitude;
        }

        public String getKey() {
            return Key;
        }

        public String getLabel() {
            return Label;
        }

        public Double getLatitude() {
            return Latitude;
        }

        public Double getLongitude() {
            return Longitude;
        }

        public String toString(){
            return "Key: " + Key + " Label: " + Label + " Latitude: " + Latitude + " Longitude: " + Longitude + "\n";
        }
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
