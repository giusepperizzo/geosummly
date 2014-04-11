package it.unito.geosummly.io.templates;

import java.util.ArrayList;

public class GeometryTemplate {
	
	private String type;
	private ArrayList<ArrayList<Double>> coordinates;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public ArrayList<ArrayList<Double>> getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(ArrayList<ArrayList<Double>> coordinates) {
		this.coordinates = coordinates;
	}
	
	@Override
	public String toString() {
		return "\n\t\tGeometry [type=" + type + ", coordinates=" + coordinates + "]";
	}
}
