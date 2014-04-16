package it.unito.geosummly.io.templates;

public class BoundingBoxTemplate {

	double north;
	double east;
	double south;
	double west;
	
	public double getNorth() {
		return north;
	}
	
	public void setNorth(double north) {
		this.north = north;
	}
	
	public double getEast() {
		return east;
	}
	
	public void setEast(double east) {
		this.east = east;
	}
	
	public double getSouth() {
		return south;
	}
	
	public void setSouth(double south) {
		this.south = south;
	}
	
	public double getWest() {
		return west;
	}
	
	public void setWest(double west) {
		this.west = west;
	}
	
	@Override
	public String toString() {
		return "BoundingBoxTemplate [north=" + north + ", east=" + east
				+ ", south=" + south + ", west=" + west + "]";
	}
}
