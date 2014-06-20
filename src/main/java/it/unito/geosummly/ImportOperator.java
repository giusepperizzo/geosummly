package it.unito.geosummly;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.ImportTools;

public class ImportOperator {
	
	public void execute(String inFile, 
						ArrayList<Double> coord, 
						int gnum, 
						CoordinatesNormalizationType ltype, 
						String outDir) throws IOException {
		
		//Read the csv file
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> listSingles=dataIO.readCSVFile(inFile);
		
		ImportTools tools=new ImportTools();
		
		//Get the grid
		BoundingBox bbox = new BoundingBox(
									new BigDecimal(coord.get(0)),
									new BigDecimal(coord.get(1)),
									new BigDecimal(coord.get(2)),
									new BigDecimal(coord.get(3)));
		
    	ArrayList<BoundingBox> data = new ArrayList<BoundingBox>();
    	Grid grid=new Grid();
    	grid.setCellsNumber(gnum);
    	grid.setBbox(bbox);
    	grid.setStructure(data);
    	grid.createCells();
    	
    	//Get the areas
    	ArrayList<Double> bboxArea=tools.getAreas(data);
    	
    	//Get the header: focal_lat, focal_lon, f1,...,fn
		ArrayList<String> features = tools.getHeader(listSingles);
		
		//Get the singles by removing the header
		//The values considered are: focal_lat, focal_lng, f1,...,fn
		ArrayList<ArrayList<Double>> venues = tools.getValues(listSingles);
		
		//Get the timestamp value
		ArrayList<Long> timestamp=new ArrayList<Long>();
		timestamp.add(Long.parseLong(listSingles.get(1).get(0)));
		
		//Get the frequency, density and normalized matrices (lat, lng, features)
		ArrayList<ArrayList<Double>> frequency = tools.buildFrequencyMatrix(data, venues);
		ArrayList<ArrayList<Double>> density=tools.buildDensityMatrix(ltype, frequency, bboxArea);
		ArrayList<ArrayList<Double>> normalized=tools.buildNormalizedMatrix(ltype, density);
		
		//Write down the matrices to file
		dataIO.printResultHorizontal(timestamp, frequency, 
									 tools.getFeaturesLabel(ltype, "f", features), 
									 outDir, "/frequency-transformation-matrix.csv");
		dataIO.printResultHorizontal(timestamp, density, 
									 tools.getFeaturesLabel(ltype, "density", features), 
									 outDir, "/density-transformation-matrix.csv");
		dataIO.printResultHorizontal(timestamp, normalized, 
									 tools.getFeaturesLabel(ltype, "normalized_density", features), 
									 outDir, "/normalized-transformation-matrix.csv");
		
	}

}
