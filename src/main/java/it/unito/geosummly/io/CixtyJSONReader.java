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
                //if(venue.getCategory().equals("nightlifespot") || venue.getCategory().equals("artsentertainment")) {

//                    if(venue.getID().equals("http://data.linkedevents.org/location/f9fced65-1379-4382-90d1-5253f4a12a48"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/cab61ebd-475b-4a8e-83ad-53ca819588ec"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/b4b7960a-3467-43bf-86b6-f33b6eab94b4"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/650bbbe6-9299-4727-9d17-fce3a804fb90"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/19008e90-6e98-4461-91a9-45258fc79415"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/1ea76f36-f31e-4c66-bce6-e18b06fbeace"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/6d10e77f-0a9e-4588-bd3a-46386fdadaf1"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/d8d04854-ad03-4c39-a760-dfa0b3567d88"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/4bc810ff-0bf6-4f99-859c-54036881fbbb"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/20ab4f72-4754-4733-933d-baa854ecaaf3"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/78c7e17e-0c48-4523-9d3b-8ca45a51c901"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/5d7785ce-28bd-4d1a-b52b-706674dd3b41"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/b8ca8622-ed2c-47d9-a02d-4798215592ab"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/210b69cd-3850-4f9d-91da-ddd6b4024103"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/bd6b7954-478b-46c0-9122-070898dd3e4b"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/b5f0382b-244f-41e1-af49-1dfefd90e7a7"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/0e4b4ea5-d84b-4e77-ab01-6b313d76f4ac"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/3f1cba94-2721-4f92-a90c-dcacb66400c8"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/79561415-6427-4fc6-9d9e-5ad88ab1fcc8"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/5baf8a08-f486-4de1-a661-73813b2b95bf"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/865e0195-091f-4a6f-b685-46f66a863182"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/8d34d4f1-7222-4f72-a11b-c3fdd6bba42e"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/da380ffe-8b00-4af2-9a18-3c47eb7a1860"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/dc85e7ad-4bd9-40f5-bbb9-9299dae89d9c"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/796b1165-b5f4-4d8e-b5cc-83b27bd91b3d"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/4fba0d65-bc25-4c41-ab46-799afaf989cb"))
//                        VenueList.add(venue);
//                    else if(venue.getID().equals("http://data.linkedevents.org/location/9a057971-75fd-4e2e-96ec-3fbc482d3514"))
//                        VenueList.add(venue);
                    VenueList.add(venue);
                //}
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
