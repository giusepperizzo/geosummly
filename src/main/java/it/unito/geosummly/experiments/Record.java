package it.unito.geosummly.experiments;

import java.util.ArrayList;

public class Record {
	
	private Long timestamp = 0L;
	private Integer beenHere = 1;
	private String id = "";
	private Double lat;
	private Double lng;
	private Double focalLat;
	private Double focalLng;
	
	private ArrayList<Integer> features;
	
	public Record (int id) {
		this.id = "" + id;
	}
	
	public Record (Long timestamp, 
			       Integer beenHere,
			   	   String id,
				   Double lat,
				   Double lng,
				   Double focalLat,
				   Double focalLng,
				   ArrayList<Integer> features) 
	{
		this.id = "" + id;
		this.timestamp = timestamp;
		this.beenHere = beenHere;
		this.lat = lat;
		this.lng = lng;
		this.focalLat = focalLat;
		this.focalLng = focalLng;
		this.features = features;
	}
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public ArrayList<Integer> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<Integer> features) {
		this.features = features;
	}
	public Double getFocalLat() {
		return focalLat;
	}
	public void setFocalLat(Double focalLat) {
		this.focalLat = focalLat;
	}
	public Double getFocalLng() {
		return focalLng;
	}
	public void setFocalLng(Double focalLng) {
		this.focalLng = focalLng;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public static String buildHeader() 
	{
		return "timestamp,been_here,id,lat,lng,focal_lat,focal_lng,";
	}
	public String serialize(){
		String result = timestamp + "," + beenHere + "," + id + "," + lat + "," + lng + "," + focalLat + "," + focalLng + ",";
		for (Integer feature : features) {
			result += feature + ",";
		}
		return result.substring(0,result.length()-1);
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Integer getBeenHere() {
		return beenHere;
	}
	public void setBeenHere(Integer beenHere) {
		this.beenHere = beenHere;
	}
	
}
