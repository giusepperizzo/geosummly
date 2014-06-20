package it.unito.geosummly.io.templates;

import java.util.ArrayList;

public class FeaturePropertiesTemplate {
	
	private int clusterId;
	private String name;
	private double sse;
	private ArrayList<VenueTemplate> venues;
	
	public int getClusterId() {
		return clusterId;
	}
	
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	} 
	
	public double getSSE() {
		return sse;
	}

	public void setSSE(double sse) {
		this.sse = sse;
	}
	
	public ArrayList<VenueTemplate> getVenues() {
		return venues;
	}
	
	public void setVenues(ArrayList<VenueTemplate> venues) {
		this.venues = venues;
	}
	
	@Override
	public String toString() {
		return "\n\t\tFeatureProperties [clusterId=" + clusterId + ", name=" + name
				+ ", venues=" + venues + "]";
	}
}
