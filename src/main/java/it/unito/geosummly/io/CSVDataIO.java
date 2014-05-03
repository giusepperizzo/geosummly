package it.unito.geosummly.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
	 * Print result of single matrix to csv file.
	*/
	public void printResultSingles(ArrayList<Long> timestamps, 
									ArrayList<Integer> beenHere, 
									ArrayList<String> singlesId, 
									ArrayList<ArrayList<Double>> matrix, 
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
            	csv.print(timestamps.get(0));
            	csv.print(beenHere.get(i));
            	csv.print(singlesId.get(i));
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
}
