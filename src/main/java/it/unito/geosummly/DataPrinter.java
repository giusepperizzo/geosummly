package it.unito.geosummly;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class DataPrinter {
	
	public DataPrinter() {}
	
	public void printResultHorizontal(ArrayList<ArrayList<Double>> matrix, ArrayList<String> features, String output) {
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
            for(ArrayList<Double> a: matrix) {
            	for(Double d: a) {
            		csv.print(d);
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
	
	public void printResultSingles(ArrayList<Long> timestamps, ArrayList<String> singlesId, ArrayList<ArrayList<Double>> matrix, ArrayList<String> features, String output) {
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
            	csv.print(timestamps.get(i));
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
