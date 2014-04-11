package it.unito.geosummly.io.templates;

public class FeatureCollectionPropertiesTemplate {
	
	private String name;
	private String date;
	private double eps;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public double getEps() {
		return eps;
	}
	
	public void setEps(double eps) {
		this.eps = eps;
	}
	
	@Override
	public String toString() {
		return "\n\tCollectionProperties [name=" + name + ", date=" + date
				+ ", eps=" + eps + "]";
	}
}