package it.unito.geosummly.io.templates;

/**
 * Venue Object Template
 * Used in order to serialized venue information on geojson output file of clustering
*/
public class VenueDataObject {
	 
	private long timestamp;
	private int been_here;
	private String id;
	private double venue_latitude;
	private double venue_longitude;
	private double focal_latitude;
	private double focal_longitude;
	private String category;
	
	public VenueDataObject(long t, int b, String id, double vLat, double vLng, double fLat, double fLng, String c) {
		this.timestamp=t;
		this.been_here=b;
		this.id=id;
		this.venue_latitude=vLat;
		this.venue_longitude=vLng;
		this.focal_latitude=fLat;
		this.focal_longitude=fLng;
		this.category=c;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getBeen_here() {
		return been_here;
	}

	public void setBeen_here(int been_here) {
		this.been_here = been_here;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getVenue_latitude() {
		return venue_latitude;
	}

	public void setVenue_latitude(double venue_latitude) {
		this.venue_latitude = venue_latitude;
	}

	public double getVenue_longitude() {
		return venue_longitude;
	}

	public void setVenue_longitude(double venue_longitude) {
		this.venue_longitude = venue_longitude;
	}

	public double getFocal_latitude() {
		return focal_latitude;
	}

	public void setFocal_latitude(double focal_latitude) {
		this.focal_latitude = focal_latitude;
	}

	public double getFocal_longitude() {
		return focal_longitude;
	}

	public void setFocal_longitude(double focal_longitude) {
		this.focal_longitude = focal_longitude;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}