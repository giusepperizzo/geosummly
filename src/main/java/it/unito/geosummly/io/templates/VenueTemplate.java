package it.unito.geosummly.io.templates;

public class VenueTemplate {
	private long timestamp;
	private int beenHere;
	private String id;
	private double venueLatitude;
	private double venueLongitude;
	private double centroidLatitude;
	private double centroidLongitude;
	private String category;
	
	public VenueTemplate(long t, int b, String id, double vLat, double vLng, double fLat, double fLng, String c) {
		
		this.timestamp=t;
		this.beenHere=b;
		this.id=id;
		this.venueLatitude=vLat;
		this.venueLongitude=vLng;
		this.centroidLatitude=fLat;
		this.centroidLongitude=fLng;
		this.category=c;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getBeenHere() {
		return beenHere;
	}
	
	public void setBeenHere(int been_here) {
		this.beenHere = been_here;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public double getVenueLatitude() {
		return venueLatitude;
	}
	
	public void setVenueLatitude(double venueLatitude) {
		this.venueLatitude = venueLatitude;
	}
	
	public double getVenueLongitude() {
		return venueLongitude;
	}
	
	public void setVenueLongitude(double venueLongitude) {
		this.venueLongitude = venueLongitude;
	}
	
	public double getCentroidLatitude() {
		return centroidLatitude;
	}
	
	public void setCentroidLatitude(double centroidLatitude) {
		this.centroidLatitude = centroidLatitude;
	}
	
	public double getCentroidLongitude() {
		return centroidLongitude;
	}
	
	public void setCentroidLongitude(double centroidlongitude) {
		this.centroidLongitude = centroidlongitude;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return "\n\t\t\tVenues [timestamp=" + timestamp + ", been_here=" + beenHere
				+ ", id=" + id + ", venueLatitude=" + venueLatitude
				+ ", venueLongitude=" + venueLongitude + ", centroidLatitude="
				+ centroidLatitude + ", centroidLongitude=" + centroidLongitude
				+ ", category=" + category + "]";
	}	
}