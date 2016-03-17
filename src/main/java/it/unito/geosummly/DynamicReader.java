package it.unito.geosummly;


import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
/*
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
*/
import java.io.*;


public class DynamicReader {

    static String sparqlEndpoint = "http://3cixty-alpha.eurecom.fr/sparql";

    public String Cixty_Query(String target) {
        if(target.equals("3cixty"))
            target = "";

        String queryString ="PREFIX dul: <http://ontologydesignpatterns.org/ont/dul/DUL.owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"+
                "PREFIX locationOnt: <http://data.linkedevents.org/def/location#>" +
                "select * where { \n" +
                " graph <http://3cixty.com/milan/" + target + ">{?s a dul:Place}\n" +
                " ?s rdfs:label ?label .\n" +
                " ?s locationOnt:businessTypeTop ?category .\n" +
                " ?s dc:identifier ?identifier .\n" +
                " ?s dc:publisher ?publisher .\n" +
                " ?s geo:location/geo:lat ?latitude .\n" +
                " ?s geo:location/geo:long ?longitude .\n" +
                " ?s dc:publisher ?publisher\n" +
                "}";

        try {

            System.out.println(queryString);
            Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

            QueryEngineHTTP httpQuery = QueryExecutionFactory.createServiceRequest(sparqlEndpoint,query);
            ResultSet results = httpQuery.execSelect();

            File file = new File("auto_3cixty_dataset/" + target + ".cixtyjson");
            FileOutputStream fout = new FileOutputStream(file);

            ResultSetFormatter.outputAsJSON(fout, results);

            fout.close();
            httpQuery.close();
            System.out.println(file.getName());
            return ("auto_3cixty_dataset/" + target + ".cixtyjson");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
