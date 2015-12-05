package it.unito.geosummly.io;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by pysherlock on 12/5/15.
 */
public class CixtyJSONReader {
    public File getJSOMMateData(File cixtyjson) {
        File jsonfile = cixtyjson;
        File MateData = null;
        JSONObject jsonObject = null;
        Scanner input = null;
        PrintWriter output = null;
        HashMap<String, Object> venueMap = new HashMap<String, Object>(); //store all the venues
        //    LinkedList<Venue> venuelist = new LinkedList<Venue>();  //store the venue with the same key
        Venue venue = null;

        try {
            input = new Scanner(jsonfile);
            output = new PrintWriter("Matedata.csv");
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
                //    output.println(venue.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /*int keys = 0;
            for (Map.Entry<String, Object> entry : venueMap.entrySet()) {
                //    System.out.println("Key: " + entry.getKey());
                keys++;
            }
            System.out.println("Key num: " + keys);*/

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        MateData = new File("Matedata.csv");
        return MateData;
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
