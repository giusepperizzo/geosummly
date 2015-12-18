package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class CSVDataIO {
	
	public CSVDataIO() {}
	
	/**
	 * Get a csv file as a List of CSVRecord.
	*/
	public List<CSVRecord> readCSVFile(String file) throws IOException {
		FileReader reader =new FileReader(file);
    	CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
		return list;
	}
	
	/**
	 * Print result of transformation matrix to csv file.
	*/
	public void printResultHorizontal(
										ArrayList<Long> timestamps, 
										ArrayList<ArrayList<Double>> matrix,
										ArrayList<String> features, 
										String directoryName, 
										String fileName ) 
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
            //print the header of the matrix
            for(String f: features) {
            	csv.print(f);
            }
            csv.println();
            
            //iterate per each row of the matrix
            for(int i=0; i<matrix.size();i++) {
            	if(timestamps!=null)
            		csv.print(timestamps.get(0));
            	for(int j=0; j<matrix.get(i).size();j++) {
            		csv.print(matrix.get(i).get(j));
            	}
            	csv.println();
            }
            csv.flush();
            csv.close();
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        OutputStream outputStream;
        try {
        	File dir=new File(directoryName); //create the output directory if it doesn't exist
        	dir.mkdirs();
            outputStream = new FileOutputStream (dir.getPath().concat(fileName));
            bout.writeTo(outputStream);
            
            bout.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Print result of deltad to csv file.
	*/
	public void printResultVertical(ArrayList<Double> deltadValues, 
									ArrayList<String> features, 
									String directoryName, 
									String fileName ) {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
            //iterate per each element of the list
        	for(int i=0;i<deltadValues.size();i++) {
        		csv.print(features.get(i));
        		csv.print(deltadValues.get(i));
        		csv.println();
            }
            csv.flush();
            csv.close();
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        OutputStream outputStream;
        try {
        	File dir=new File(directoryName); //create the output directory if it doesn't exist
        	dir.mkdirs();
            outputStream = new FileOutputStream (dir.getPath().concat(fileName));
            bout.writeTo(outputStream);
            bout.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Print result of single matrix of the validation step to csv file.
	*/
	public void printSinglesForValidation(ArrayList<ArrayList<String>> matrix, 
											ArrayList<String> header,
											String directoryName, 
											String fileName) {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
            //print the header of the matrix
            for(String h: header) {
            	csv.print(h);
            }
            csv.println();
            
            //iterate per each row of the matrix
            for(ArrayList<String> rec: matrix) {
            	for(String s: rec) {
            		csv.print(s);
            	}
            	csv.println();
            }
            csv.flush();
            csv.close();
            
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        
        OutputStream outputStream;
        try {
        	File dir=new File(directoryName); //create the output directory if it doesn't exist
        	dir.mkdirs();
            outputStream = new FileOutputStream (dir.getPath().concat(fileName));
            bout.writeTo(outputStream);
            bout.close();
            outputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Print result of single matrix to csv file.
	*/
	public void printResultSingles2(long timestamp, 
									ArrayList<Integer> beenHere, 
									ArrayList<String> singlesId, 
									ArrayList<ArrayList<Double>> coord,
									ArrayList<ArrayList<Byte>> matrix,
									ArrayList<String> features, 
									String directoryName, 
									String fileName) {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
            //print the header of the matrix
            for(String f: features) {
            	csv.print(f);
            }
            csv.println();
            
            //iterate per each row of the matrix
            for(int i=0; i<matrix.size();i++) {
            	csv.print(timestamp);
            	csv.print(beenHere.get(i));
            	csv.print(singlesId.get(i));
            	for(int k=0; k<coord.get(i).size(); k++)
            		csv.print(coord.get(i).get(k));
            	for(int j=0;j<matrix.get(i).size();j++) {
            		csv.print(matrix.get(i).get(j));
            	}
            	csv.println();
            }
            csv.flush();
            csv.close();
            
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        
        OutputStream outputStream;
        try {
        	File dir=new File(directoryName); //create the output directory if it doesn't exist
        	dir.mkdirs();
            outputStream = new FileOutputStream (dir.getPath().concat(fileName));
            bout.writeTo(outputStream);
            bout.close();
            outputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}



	/**
	 * Print the cell information (N,E,S,W coordinates). Useful for geojson grid creation
	*/
	public void printCells(ArrayList<BoundingBox> data, String directoryName, String fileName) {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
        	csv.print("Row");
        	csv.print("Column");
        	csv.print("North");
        	csv.print("East");
        	csv.print("South");
        	csv.print("West");
        	csv.print("Center_Lat");
        	csv.print("Center_Lng");
        	csv.print("Area");
        	csv.println();
            
            for(BoundingBox b: data) {
            	csv.print(b.getRow());
            	csv.print(b.getColumn());
            	csv.print(b.getNorth());
            	csv.print(b.getEast());
            	csv.print(b.getSouth());
            	csv.print(b.getWest());
            	csv.print(b.getCenterLat());
            	csv.print(b.getCenterLng());
            	csv.print(b.getArea());
            	csv.println();
            }
            
            csv.flush();
            csv.close();
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        OutputStream outputStream;
        try {
        	File dir=new File(directoryName); //create the output directory if it doesn't exist
        	dir.mkdirs();
            outputStream = new FileOutputStream (dir.getPath().concat(fileName));
            bout.writeTo(outputStream);
            
            bout.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
}
