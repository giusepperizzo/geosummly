package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.Venue;
import org.json.JSONException;
import org.json.JSONObject;
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
        File jsonfile = new File(file);
        JSONObject jsonObject = null;
        Scanner input = new Scanner(jsonfile);
        Venue venue = null;
        String Publisher = new String("");
    //  int Num = 0, count = 0;

        while(input.hasNext()) {
            try {
                jsonObject = new JSONObject(input.nextLine());
                venue = new Venue(System.currentTimeMillis(),
                        0,
                        jsonObject.getJSONObject("s").getString("value"),
                        jsonObject.getJSONObject("category").getString("value"),
                        jsonObject.getJSONObject("label").getString("value"),
                        jsonObject.getJSONObject("publisher").getString("value"),
                        jsonObject.getJSONObject("latitude").getDouble("value"),
                        jsonObject.getJSONObject("longitude").getDouble("value"));

                if(!Publisher.equals(venue.getPublisher())) {
                    Publisher = venue.getPublisher();
                    System.out.println(Publisher);
                }
                venue.setCategory(venue.getCategory().split("/3cixty/")[1]);

                //venue.setCategory(venue.getCategory().split("/3cixty/")[1]+Publisher);
                //Use to Put different publishers into one map.

                VenueList.add(venue);
        /*        if(!BaseMap.containsKey(venue.getCoordinate())) {
                    BaseMap.put(venue.getCoordinate(), venue);
                    VenueList.add(venue);
                }
                else {
                    System.out.println("BaseMap contains Venue in " + venue.getLatitude() + " " + venue.getLongitude());
                    Num++;
                }*/
        //        count++;

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
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

    public ArrayList <Venue> decodeForSampling(String cixtyjson) throws IOException, JSONException  {
        ReadVenue(cixtyjson);
        return VenueList;
    }
}
