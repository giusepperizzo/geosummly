package it.unito.geosummly.io.templates;

import java.util.ArrayList;

public class FeatureTemplate {
	
	private String type;
	private int id;
	private ArrayList<Integer> cells;
	private GeometryTemplate geometry;
	private FeaturePropertiesTemplate properties;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public GeometryTemplate getGeometry() {
		return geometry;
	}
	
	public void setGeometry(GeometryTemplate geometry) {
		this.geometry = geometry;
	}
	
	public FeaturePropertiesTemplate getProperties() {
		return properties;
	}
	
	public void setProperties(FeaturePropertiesTemplate properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return "\n\tFeature [type=" + type + ", id=" + id + ", geometry="
				+ geometry + ", properties=" + properties + "]";
	}

	public ArrayList<Integer> getCells() {
		return cells;
	}

	public void setCells(ArrayList<Integer> cells) {
		this.cells = cells;
	}
}