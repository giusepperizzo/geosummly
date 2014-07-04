package it.unito.geosummly;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * Representation of bounding box concept
 */

public class BoundingBox {
	
	private Double north;
	private Double east;
	private Double south;
	private Double west;
	private Double centerLat; //Latitude of central point
	private Double centerLng; //Longitude of central point
	private Double area; //area of the bbox
	private int row; //row of the cell (position)
	private int column; //column of the cell (position)
	
	public static Logger logger = Logger.getLogger(BoundingBox.class.toString());

	
	public BoundingBox(){}
	
	public BoundingBox(Double n, Double e, Double s, Double w){
		this.north = n;
		this.east = e;
		this.south = s;
		this.west = w;
		this.centerLat = ( n + s ) / 2;
		this.centerLng = ( e + w ) / 2;
		this.area = new Double(getDistance(s, w, n, w) * getDistance(n, w, n, e));
	}

	public void setNorth(Double north){
		this.north=north;
	}

	public Double getNorth(){
		return north;
	}
	
	public void setEast(Double east){
		this.east=east;
	}
	
	public Double getEast(){
		return east;
	}
	

	public void setSouth(Double south){
		this.south=south;
	}

	public Double getSouth(){
		return south;
	}
	
	public void setWest(Double west){
		this.west=west;
	}

	public Double getWest(){
		return west;
	}
	
	public void setCenterLat(Double centerLat){
		this.centerLat=centerLat;
	}

	public Double getCenterLat(){
		return centerLat;
	}
	
	public void setCenterLng(Double centerLng){
		this.centerLng=centerLng;
	}

	public Double getCenterLng(){
		return centerLng;
	}
	
	
	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String toString(){
		return "Row: "+row+" Column:"+column+" N:"+north+" E:"+east+" S:"+south+" W:"+west+" C_Lat:"+centerLat+" C_Lng:"+centerLng+" Area:"+area;
	}
	
	/** Haversine formula implementation. It returns the distance (in kilometers) between 
	 * two points given latitude and longitude values
	 */
	public double getDistance(Double Blat1, Double Blng1, 
						 	  Double Blat2, Double Blng2){
		
		double lat1 = Blat1.doubleValue();
		double lng1 = Blng1.doubleValue();
		double lat2 = Blat2.doubleValue();
		double lng2 = Blng2.doubleValue();
		
		double earthRadius = 6371; //in km
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    DecimalFormat df = new DecimalFormat("#.###");
	    String value = df.format(dist);
	    value = value.replace(",", ".");
	    
	    return Double.parseDouble(value);
	}

	/**
	 * Overrided from java to implement equals method
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(centerLat.doubleValue());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(centerLng.doubleValue());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Overrided from java. The equality check is based on 
	 * center latitude and center longitude values
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BoundingBox))
			return false;
		BoundingBox other = (BoundingBox) obj;
		if (Double.doubleToLongBits(centerLat.doubleValue()) != Double
				.doubleToLongBits(other.centerLat.doubleValue())  ||  
			Double.doubleToLongBits(centerLng.doubleValue()) != Double
				.doubleToLongBits(other.centerLng.doubleValue()))
			return false;
		return true;
	}
}
