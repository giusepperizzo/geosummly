package it.unito.geosummly.io.templates;

import it.unito.geosummly.BoundingBox;

public class FeatureCollectionPropertiesTemplate {
	
	private String name;
	private String date;
	private double eps;
	private BoundingBox bbox;
	
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
	
	public BoundingBox getBbox() {
		return bbox;
	}

	public void setBbox(BoundingBox bbox) {
		this.bbox = bbox;
	}

	@Override
	public String toString() {
		return "FeatureCollectionPropertiesTemplate [name=" + name + ", date="
				+ date + ", eps=" + eps + ", bbox=" + bbox + "]";
	}

}