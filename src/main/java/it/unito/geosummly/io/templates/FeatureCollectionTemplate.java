package it.unito.geosummly.io.templates;

import java.util.ArrayList;

public class FeatureCollectionTemplate {
	
	private String type;
	private ArrayList<FeatureTemplate> features;
	private FeatureCollectionPropertiesTemplate properties;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public ArrayList<FeatureTemplate> getFeatures() {
		return features;
	}
	
	public void setFeatures(ArrayList<FeatureTemplate> features) {
		this.features = features;
	}
	
	public FeatureCollectionPropertiesTemplate getProperties() {
		return properties;
	}
	
	public void setProperties(FeatureCollectionPropertiesTemplate properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return "Type: "+type+"\nFeatures: "+features.toString()+"\nProperties: "+properties.toString();
	}
}
