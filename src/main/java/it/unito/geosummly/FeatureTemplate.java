package it.unito.geosummly;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;

public class FeatureTemplate extends MfFeature {
	private String id;
	private MfGeometry geometry;

	public FeatureTemplate(String id, MfGeometry geometry, JSONObject properties) {
		this.id=id;
		this.geometry=geometry;
	}
	
	@Override
	public String getFeatureId() {
		return id;
	}

	@Override
	public MfGeometry getMfGeometry() {
		return geometry;
	}

	@Override
	public void toJSON(JSONWriter builder) throws JSONException {}

}
