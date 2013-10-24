package it.unito.geosummly;

/**
 * @author Giacomo Falcone
 *
 * This class represents the concept of bounding box
 */

public class BoundingBox {
	private double north;
	private double south;
	private double west;
	private double east;
	private int row; //row of the cell (position)
	private int column; //column of the cell (position)

	
	public BoundingBox(){}
	
	public BoundingBox(double n, double s, double w, double e){
		this.north=n;
		this.south=s;
		this.west=w;
		this.east=e;
	}

	public void setNorth(double north){
		this.north=north;
	}

	public double getNorth(){
		return north;
	}

	public void setSouth(double south){
		this.south=south;
	}

	public double getSouth(){
		return south;
	}

	public void setWest(double west){
		this.west=west;
	}

	public double getWest(){
		return west;
	}

	public void setEast(double east){
		this.east=east;
	}

	public double getEast(){
		return east;
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
		return "N: "+north+" S:"+south+" W:"+west+" E:"+east;
	}
}
