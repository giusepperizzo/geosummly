package it.unito.geosummly.io;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class GeoTurtleWriter implements IGeoWriter {

	public static void main(String[] args) {
		
		GeoTurtleWriter geo = new GeoTurtleWriter();
				
		Model model = geo.serialize();
		model.write(System.out,"Turtle");
	}
	
	
	public Model serialize () 
	{
		// uris definition
		String OPEN_ANNOTATION_URI = "http://www.w3.org/ns/oa#";
		String GEO_URI =  "http://www.opengis.net/ont/geosparql#";
		String PROV_URI = "http://www.w3.org/ns/prov#";
		String GEOSUMMLY_URI = "http://data.geosummly.eurecom.fr/";
		
		OntModel modelOA = ModelFactory.createOntologyModel();
		OntModel modelPROV= ModelFactory.createOntologyModel();
		OntModel modelGEO = ModelFactory.createOntologyModel();
		
		// define OA classes 
		OntClass OAannotation = modelOA.createClass( OPEN_ANNOTATION_URI + "Annotation" );
		OntProperty OAtargetProperty = modelOA.createObjectProperty(OPEN_ANNOTATION_URI + "hasTarget");
		OntProperty OAbodyProperty = modelOA.createObjectProperty(OPEN_ANNOTATION_URI + "hasBody");
		// define PROV classes 		
		OntProperty PROVstartAtTime = modelPROV.createObjectProperty(PROV_URI + "startedAtTime");
		OntProperty PROVwasAttributedTo = modelPROV.createObjectProperty(PROV_URI + "wasAttributedTo");
		// define GEO classes
		OntClass GeoGeometry = modelGEO.createClass( GEO_URI + "Geometry" );
		OntClass GeoSpatialObject = modelGEO.createClass( GEO_URI + "SpatialObject" );
		OntProperty GeoasWKT = modelGEO.createObjectProperty(GEO_URI + "asWKT");
		OntProperty GeosfContains = modelGEO.createObjectProperty(GEO_URI + "sfContains");
	
		// create models
		OntModel model = ModelFactory.createOntologyModel();
		// create prefixes
		model.setNsPrefix("geo", GEO_URI);
		model.setNsPrefix("oa", OPEN_ANNOTATION_URI);
		model.setNsPrefix("prov", PROV_URI);
		model.setNsPrefix("geosummly", GEOSUMMLY_URI);
		
		// some definitions
		String annotationURI = GEOSUMMLY_URI
							   .concat("annotation/")
							   .concat(UUID.randomUUID().toString());
		
		String geometryURI = GEOSUMMLY_URI
								.concat("geometry/")
								.concat(UUID.randomUUID().toString());
		
		String fingerprintURI = GEOSUMMLY_URI
								.concat("fingerprint/")
								.concat(UUID.randomUUID().toString());		
		// create the resource
		//   and add the properties cascading style
		Calendar cal = GregorianCalendar.getInstance();
		Literal timestamp = model.createTypedLiteral(cal);
		
//		Individual tool = model.createIndividual(
//				OPEN_ANNOTATION_URI
//				   .concat("annotation/")
//				   .concat(UUID.randomUUID().toString()),
//				   CAnnotation
//				);
		
		Resource fingerprint = model.createResource(fingerprintURI);		
		Resource geometry = model.createResource(geometryURI);
		Literal wkt = model.createLiteral(
				"MultiPoint((45.45913473,9.18133672)," +
				"(45.45068759,9.17229372),(45.45701465,9.18433557))");
		
		Resource annotation = 
				model.createResource(annotationURI)
		  		.addProperty(RDF.type, OAannotation)
		  		.addProperty(OAtargetProperty, geometry)
		  		.addProperty(OAbodyProperty, fingerprint)
				.addProperty(PROVstartAtTime, timestamp)
				.addProperty(PROVwasAttributedTo, model.createResource("http://geosummly.eurecom.fr"));
		
		geometry
			.addProperty(RDF.type, GeoGeometry)
			.addProperty(GeoasWKT, wkt);
		
		fingerprint
			.addProperty(RDF.type, GeoSpatialObject)
			.addProperty(RDFS.label, "c(Arts & Entertainment)")
			.addProperty(GeosfContains, model.createResource("http://foursquare.com/v/4cbc16c49552b60c676fe38b"))
			.addProperty(GeosfContains, model.createResource("http://foursquare.com/v/4bffc482daf9c9b6f4a6faef"))
			.addProperty(GeosfContains, model.createResource("http://foursquare.com/v/4bc415bf920eb713295a1e2c"));
	
		return model;
	}
	
	@Override
	public void writeStream(HashMap<Integer, String> labels,
			HashMap<Integer, ArrayList<ArrayList<Double>>> cells,
			HashMap<Integer, ArrayList<ArrayList<String>>> venues, double eps,
			String output, Calendar cal) 
	{
		
	}		
}

