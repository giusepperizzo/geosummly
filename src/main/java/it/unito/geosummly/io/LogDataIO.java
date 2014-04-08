package it.unito.geosummly.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogDataIO {
	
	/**
	 * Read the clustering log file
	*/
	public ArrayList<ArrayList<String>> readClusteringLog(String logFile) throws IOException {
		ArrayList<String> labels=new ArrayList<String>();
		ArrayList<String> minpts=new ArrayList<String>();
		ArrayList<String> eps_sse=new ArrayList<String>(); //eps value and sse value
			 
		String current;
 
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		Pattern p1=Pattern.compile("\\.(.*)\\."); //regex for feature label
		Pattern p2=Pattern.compile("=(.*)"); //regex for minpts value
		Pattern p3=Pattern.compile(":(.*)"); //regex for eps value
		Matcher matcher;
		
		//read the file
		while ((current = br.readLine()) != null) {
			//get the label
			if(current.startsWith("{")) {
				matcher=p1.matcher(current);
				if(matcher.find()) {
					boolean found =false;
					
					//check if label is already in the list
					for(int i=0;i<labels.size() && !found;i++) {
						if(matcher.group(1).equals(labels.get(i)))
							found=true;
					}
					
					//add label if it's not in the list yet
					//add also minpts
					if(!found) {
						labels.add(matcher.group(1));
						matcher=p2.matcher(current);
						if (matcher.find())
							minpts.add(matcher.group(1));
					}
				}
			}
	
			//get the eps
			else if (current.startsWith("eps value")) {
					matcher=p3.matcher(current);
					if(matcher.find())
						eps_sse.add(matcher.group(1).trim());
			}
			
			//get the SSE
			else if (current.startsWith("SSE value")) {
				matcher=p3.matcher(current);
				if(matcher.find())
					eps_sse.add(matcher.group(1).trim());
			}
		}
		
		br.close();
		
		ArrayList<ArrayList<String>> logInfo=new ArrayList<ArrayList<String>>();
		logInfo.add(labels);
		logInfo.add(minpts);
		logInfo.add(eps_sse);
		
		return logInfo;
	}
	
	/**
	 * Write the log file of clustering process 
	*/
	public void writeClusteringLog(StringBuilder sb, double eps, double sse, String output) {
		sb.append("\n\neps value: "+eps);
		sb.append("\nSSE value: "+sse);
		
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/clustering-log-eps").concat(eps+"").concat(".log"));
	 
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
	public void writeSSELog(ArrayList<Double> SSEs, double discard, String output) {
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/SSEs.log"));
	 
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
			
			bw.write("\n\nDiscard between real SSE and median of SSE random values: "+ discard);
			
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write a R script in order to get the SSEs' gaussian distribution
	*/
	public void writeSSEforR(ArrayList<Double> SSEs, String output) {
		int min=Collections.min(SSEs).intValue();
		int max=Collections.max(SSEs).intValue();
		StringBuilder sb=new StringBuilder();
		
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/SSE_distribution.R"));
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        sb.append("x=c(");
			for(Double d: SSEs) {
				sb.append((int) Math.floor(d)+", ");
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
	public void writeHoldoutLog(HashMap<String, Vector<Integer>> holdout, String output) {
	    try {
	    	File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/holdout_results.log"));
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
			
			//get the last line to know the last fold number created
			BufferedReader br = new BufferedReader(new FileReader(dir.getPath().concat("/holdout_results.log")));
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
	        fw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Write the log file of Jaccard evaluation 
	*/
	public void writeJaccardLog(StringBuilder builder, String output) {
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/jaccard_report.log"));
	 
    		//if file doesn't exist, then create it
			if(!file.exists()){
				file.createNewFile();
			}
 
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(builder.toString());
	        bw.flush();
	        bw.close();
	        fw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}
}
