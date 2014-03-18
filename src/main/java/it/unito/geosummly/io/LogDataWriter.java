package it.unito.geosummly.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class LogDataWriter {
	
	/**
	 * Write the log file of holdouts 
	*/
	public void printHoldoutLog(HashMap<String, Vector<Integer>> holdout, String output) {
	    try {
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
