package it.unito.geosummly;

import java.io.ByteArrayOutputStream;
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
	
	public List<CSVRecord> readCSVFile(String file) throws IOException {
		FileReader reader =new FileReader(file);
    	CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
		return list;
	}
	
	public void printResultHorizontal(ArrayList<Long> timestamps, ArrayList<ArrayList<Double>> matrix, ArrayList<String> features, String output) {
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
            outputStream = new FileOutputStream (output);
            bout.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void printResultVertical(ArrayList<Double> deltadValues, ArrayList<String> features, String output) {
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
            outputStream = new FileOutputStream (output);
            bout.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void printResultSingles(ArrayList<Long> timestamps, ArrayList<Integer> beenHere, ArrayList<String> singlesId, ArrayList<ArrayList<Double>> matrix, ArrayList<String> features, String output) {
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
            outputStream = new FileOutputStream (output);
            bout.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
