package it.unito.geosummly.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class LogWriter {
	
	/**
	 * Write the log file of clustering process 
	*/
	public void printClusteringLog(StringBuilder sb, double eps, double sse, String output) {
		sb.append("\n\neps value: "+eps);
		sb.append("\nSSE value: "+sse);
		
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(output+"/clustering-log-eps"+eps+".log");
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(sb.toString());
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write the log file of SSE values
	*/
	public void printSSELog(ArrayList<Double> SSEs, String output) {
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(output+"/SSEs.log");
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        int index=0;
			for(Double d: SSEs) {
				bw.write(index+","+d+"\n");
				index++;
			}
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write a R script in order to get the SSEs' gaussian distribution
	*/
	public void printSSEforR(ArrayList<Double> SSEs, String output) {
		int min=Collections.min(SSEs).intValue();
		int max=Collections.max(SSEs).intValue();
		StringBuilder sb=new StringBuilder();
		NumberFormat nf= NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		nf.setRoundingMode(RoundingMode.HALF_UP);
		
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(output+"/SSE_distribution.R");
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        sb.append("x=c(");
			for(Double d: SSEs) {
				sb.append((double) Math.round(d*10000)/10000+","); //keep only 4 decimal digits
				System.out.println(nf.format(d));
			}
			sb=sb.replace(sb.length()-1, sb.length(), "");
			sb.append(");\n");
			sb.append("bins=seq("+(min-5)+","+(max+5)+",by=0.5);\n");
			sb.append("hist(x,breaks=bins,xlab=\"SSE\",ylab=\"count\",main=\"Histogram of SSE for "+ 
						SSEs.size() +" random data sets\")");
	        
			bw.write(sb.toString());
			bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write the log file of holdouts 
	*/
	public void printHoldoutLog(HashMap<String, Vector<Integer>> holdout, String output) {
	    try {
	    	File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(output+"/holdout_results.log");
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
			
			//get the last line to know the last fold number created
			BufferedReader br = new BufferedReader(new FileReader(output+"/holdout_results.log"));
			String currentLine="";
			String lastLine="";
    		int lastFold=0;
    		
    	    while ((currentLine=br.readLine())!=null) {
    	        lastLine = currentLine;
    	    }
    	    br.close();
    	    
    	    if(lastLine.length()>0)
    	    	lastFold=Integer.parseInt(lastLine.substring(lastLine.length()-1));
 
    		FileWriter fw = new FileWriter(file, true); //true=append
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        if(lastFold > 0)
	        	bw.write("\n");
	        ArrayList<String> keys=new ArrayList<String>(holdout.keySet());
	        for(String label: keys) {
	        	bw.write(label+";");
	        	for(Integer i: holdout.get(label)) {
	        		bw.write(i/*-length*/+" ");
	        	}
	        	bw.write("\n");
	        }
	        bw.write("_END_HO"+(lastFold+1));
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write the log file of Jaccard evaluation 
	*/
	public void printJaccardLog(StringBuilder builder, String output) {
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(output+"/jaccard_report.log");
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(builder.toString());
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
}
