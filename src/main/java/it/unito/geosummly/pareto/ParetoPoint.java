package it.unito.geosummly.pareto;

import java.text.DecimalFormat;

public class ParetoPoint {
	private int index;
	private Double x;
	private Double y;
	private Double z;
	private Double s;
	private DecimalFormat df = new DecimalFormat("#.#######################################");
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index + 1;
	}
	public ParetoPoint(int i, Double x, Double y, Double z, Double s) {
		this.setIndex(i);
		this.x = Double.valueOf( df.format(x).replace(",", ".") );
		this.y = Double.valueOf( df.format(y).replace(",", ".") );
		this.z = Double.valueOf( df.format(z).replace(",", ".") );
		this.s = Double.valueOf( df.format(s).replace(",", ".") );
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = Double.valueOf( df.format(x).replace(",", ".") );
	}
	
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = Double.valueOf( df.format(y).replace(",", ".") );
	}

	public Double getZ() {
		return z;
	}
	public void setZ(Double z) {
		this.z = Double.valueOf( df.format(z).replace(",", ".") );
	}
	
	public Double getS() {
		return s;
	}
	public void setS(Double s) {
		this.s = Double.valueOf( df.format(s).replace(",", ".") );
	}
}
