package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class GeoTurtleWriter implements IGeoWriter {
	
	// uris definition
	String OPEN_ANNOTATION_URI = "http://www.w3.org/ns/oa#";
	String GEO_URI =  "http://www.opengis.net/ont/geosparql#";
	String PROV_URI = "http://www.w3.org/ns/prov#";
	String GEOSUMMLY_URI = "http://data.geosummly.eurecom.fr/";
	
	// used models
	OntModel modelOA = ModelFactory.createOntologyModel();
	OntModel modelPROV= ModelFactory.createOntologyModel();
	OntModel modelGEO = ModelFactory.createOntologyModel();
	
	private List<Resource> fingerprints = new LinkedList<Resource>();

	
	@Override
	public void writeStream(
							BoundingBox bbox,
							HashMap<Integer, String> labels,
							HashMap<Integer, ArrayList<ArrayList<Double>>> cells,
							HashMap<Integer, ArrayList<ArrayList<String>>> venues, 
							HashMap<Integer, Double> cSSE,
							HashMap<Integer, Double> cSurface, 
							HashMap<Integer, Double> cHeterogeneity, 
							HashMap<Integer, Double> cDensity, 							
							double eps,
							String output, 
							Calendar cal
						   ) 
	{
		
    	ArrayList<Integer> keys=new ArrayList<Integer>(labels.keySet()); //keys of clusters
    	ArrayList<ArrayList<Double>> cellsOfCluster; //cells informations (cell_id, cell_lat, cell_lng) of a cluster
    	ArrayList<ArrayList<String>> venuesOfCell; //all venues of a cell
		
		//iterate for each cluster (get the cluster label)
        for(Integer i: keys) 
        {
			String multipoint = "MultiPoint(";
			String clusterLabel = "";
			Model model;
			
    		clusterLabel=labels.get(i);
    		cellsOfCluster=new ArrayList<ArrayList<Double>>(cells.get(i));
    		List<String> sqrVenues = new LinkedList<>();
        	
    		//iterate for each cell of the cluster (get all the coordinates for multipoint)
    		for(ArrayList<Double> cl: cellsOfCluster) {
    			DecimalFormat df=new DecimalFormat("#.########");
    			String s1=df.format(cl.get(1)).replaceAll(",", ".");
    			String s2=df.format(cl.get(2)).replaceAll(",", ".");
    			multipoint=multipoint+"("+s1+","+s2+"),";
    		}
    		
    		//iterate for each venue of the cluster (get all the venues it they exist)
    		Object tmp=venues.get(i);
    		if(tmp!=null) {
    			venuesOfCell=new ArrayList<ArrayList<String>>(venues.get(i));
    			for(ArrayList<String> r: venuesOfCell)
					sqrVenues.add("http://foursquare.com/v/"+r.get(2));
    		}
    		
    		multipoint=multipoint.substring(0, multipoint.length()-1); //remove last comma
    		multipoint=multipoint+")"; //concatenate parenthesis
    		model=serializeAnnotation(multipoint, clusterLabel, cSSE.get(i), sqrVenues, cal);
        	int index=i+1;
    		
			//Create Turtle
			OutputStream os;
        	try {
        		File dir=new File(output); //create the output directory if it doesn't exist
            	dir.mkdirs();
				os=new FileOutputStream(new File(dir.getPath().concat("/cluster").concat(index+"").concat(".ttl")));
	    		model.write(os, "Turtle");
	    		os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } //end for clusters
        
        
        Model model = serializeSummary(bbox);
        //Create Turtle
		OutputStream os;
    	try {
    		File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
			os=new FileOutputStream(new File(dir.getPath().concat("/summary").concat(".ttl")));
    		model.write(os, "Turtle");
    		os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void serializeAll() {
		for (;;) {
			
		}
	}
	
	public Model serializeSummary (BoundingBox bbox) 
	{
		// define GEO classes
		OntClass GeoGeometry = modelGEO.createClass( GEO_URI + "Geometry" );
		OntClass GeoSpatialObject = modelGEO.createClass( GEO_URI + "SpatialObject");
		OntProperty GeoasWKT = modelGEO.createObjectProperty(GEO_URI + "asWKT");
		OntProperty GeosfContains = modelGEO.createObjectProperty(GEO_URI + "sfContains");
		OntProperty GeohasGeometry = modelGEO.createObjectProperty(GEO_URI + "hasGeometry");
		
		// create models
		OntModel model = ModelFactory.createOntologyModel();
		// create prefixes
		model.setNsPrefix("geo", GEO_URI);
		model.setNsPrefix("oa", OPEN_ANNOTATION_URI);
		model.setNsPrefix("prov", PROV_URI);
		model.setNsPrefix("geosummly", GEOSUMMLY_URI);
		
		RDFDatatype wkttype = WKTType.type;
		TypeMapper.getInstance().registerDatatype(wkttype);

		// some definitions
		String summaryURI = GEOSUMMLY_URI
							   .concat("summary/")
							   .concat(UUID.randomUUID().toString());
			

		String ne = Double.toString(bbox.getNorth().doubleValue())
						.concat(" " + Double.toString(bbox.getEast().doubleValue()) );
		String sw = Double.toString(bbox.getSouth().doubleValue())
				.concat(" " + Double.toString(bbox.getWest().doubleValue()) );

				
		String polygon = "Polygon(("
							.concat(ne)
							.concat("),(")
							.concat(sw)
							.concat("))");
		Literal wkt = model.createTypedLiteral(polygon, wkttype);
		Resource geometry = model.createResource().addProperty(GeoasWKT, wkt);
		
		Resource summary = model.createResource(summaryURI)
						.addProperty(RDF.type, GeoSpatialObject)
						.addProperty(RDFS.label, "geosummly")
						.addProperty(GeohasGeometry, geometry);
		for (Resource r : fingerprints)
			summary.addProperty(GeosfContains, r);
		
		return model;
	}
	

	public Model serializeAnnotation (	String multipoint, 
										String clusterLabel, 
										double sse,
										List<String> sqrVenues, 
										Calendar cal) 
	{
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
		OntProperty GeosummlySSE = model.createObjectProperty(GEOSUMMLY_URI + "hasSSE");

		RDFDatatype wkttype = WKTType.type;
		TypeMapper.getInstance().registerDatatype(wkttype);

		
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
		Literal timestamp = model.createTypedLiteral(cal);
		
//		Individual tool = model.createIndividual(
//				OPEN_ANNOTATION_URI
//				   .concat("annotation/")
//				   .concat(UUID.randomUUID().toString()),
//				   CAnnotation
//				);
		
		Resource fingerprint = model.createResource(fingerprintURI);		
		fingerprints.add(fingerprint);		
		
		Resource geometry = model.createResource(geometryURI);
		Literal wkt = model.createTypedLiteral(multipoint, wkttype);
		
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
			.addProperty(RDFS.label, clusterLabel)
			.addProperty(GeosummlySSE, ResourceFactory.createTypedLiteral(sse));
		
		// add all sqrVenues in there
		for (String sqrVenue : sqrVenues)
			fingerprint.addProperty(GeosfContains, model.createResource(sqrVenue));
	
		return model;
	}		
}

