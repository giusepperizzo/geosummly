package it.unito.geosummly.io;

import it.unito.geosummly.Venue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

/**
 * Created by pysherlock on 12/5/15.
 */
public class CixtyJSONReader {

    /*Read and Decode the JSONdata from 3Cixty files*/

    private ArrayList<Venue> VenueList = new ArrayList<>();   //As Category Tree
//  private HashMap<Venue.Coordinate, Venue> BaseMap = new HashMap<>();  //Original Map from some database source, use for checking wheather there are override venues

    public void ReadVenue(String file)throws IOException{
        Venue venue = null;
        String Publisher = new String("");
    //  int Num = 0, count = 0;

        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(file)));
            JSONArray jsonArray = jsonObject.getJSONObject("results").getJSONArray("bindings");

            for(int i = 0; i < jsonArray.length(); i++) {
                venue = new Venue(System.currentTimeMillis(),
                        10,
                        jsonArray.getJSONObject(i).getJSONObject("s").getString("value"),
                        jsonArray.getJSONObject(i).getJSONObject("category").getString("value"),
                        jsonArray.getJSONObject(i).getJSONObject("label").getString("value"),
                        jsonArray.getJSONObject(i).getJSONObject("publisher").getString("value"),
                        jsonArray.getJSONObject(i).getJSONObject("latitude").getDouble("value"),
                        jsonArray.getJSONObject(i).getJSONObject("longitude").getDouble("value"));

                if (!Publisher.equals(venue.getPublisher())) {
                    Publisher = venue.getPublisher();
                    System.out.println(Publisher);
                }
                venue.setCategory(venue.getCategory().split("/3cixty/")[1]);

                //venue.setCategory(venue.getCategory().split("/3cixty/")[1]+Publisher);
                //Use to Put different publishers into one map.
                if(venue.getCategory().equals("nightlifespot") || venue.getCategory().equals("artsentertainment"))
                    VenueList.add(venue);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        System.out.println("Total num of venues: " + count);
        System.out.println("The num of override venues: " + Num);
        File sample_log = new File("sample_log");
        FileWriter fw = new FileWriter(sample_log);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Total num of venues: " + count + '\n');
        bw.write("The num of override venues: " + Num + '\n');
        bw.flush();
        bw.close();*/

        try {
            Thread.sleep(1000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList <Venue> decodeForSampling(String cixtyjson) throws IOException, JSONException {
        ReadVenue(cixtyjson);
        if (VenueList.isEmpty()){
            System.out.println("Cannot read venues from the target JSON file");
            System.exit(-1);
        }
        return VenueList;
    }
}
