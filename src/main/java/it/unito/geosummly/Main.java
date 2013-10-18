package it.unito.geosummly;

import java.net.UnknownHostException;

import fi.foyt.foursquare.api.FoursquareApiException;


public class Main {
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		SearchVenues sv=new SearchVenues();
		sv.searchVenues("45.05,7.666667", 1, 1);
	}
}
