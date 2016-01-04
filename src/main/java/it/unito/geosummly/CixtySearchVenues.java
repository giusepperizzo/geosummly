package it.unito.geosummly;

import it.unito.geosummly.Venue;

import java.util.ArrayList;

/**
 * Created by pysherlock on 12/18/15.
 * Search the venue in each cell of 3cixty data
 */
public class CixtySearchVenues {

    public ArrayList<Venue> SearchVenues(double north, double west, double south, double east, ArrayList<Venue> VenueList) {
        ArrayList <Venue>searchvenues = new ArrayList<Venue>();
        for (Venue venue : VenueList) {
            if (venue.getLatitude() < north && venue.getLatitude() > south) {
                if (venue.getLongitude() < east && venue.getLongitude() > west)
                    searchvenues.add(venue);
            }
        }
        return searchvenues;
    }

}
