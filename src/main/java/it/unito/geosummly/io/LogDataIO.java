package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.pareto.ParetoPoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogDataIO {
	
	/**
	 * Read the sampling log file
	*/
	public ArrayList<Integer> readSamplingLog(String logFile) throws IOException {
		
		int cellsNum=0;
		int catNum=0;
		
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		String current=null;
		
		//Get the values from file
		while((current=br.readLine()) !=null) {
			if(current.contains("Number of cells")) {
				cellsNum=Integer.parseInt(current.split(":")[1].trim()); //get the number of cells of the bbox
			}
			else if(current.contains("1st level")) {
				catNum=Integer.parseInt(current.split(":")[1].trim()); //get the total number of categories found
			}	
		}
		
		br.close();
		
		ArrayList<Integer> logInfos=new ArrayList<Integer>();
		logInfos.add(cellsNum);
		logInfos.add(catNum);
		
		return logInfos;
	}
	
	
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
	 * Write the log file of sampling process 
	*/
	public void writeSamplingLog(BoundingBox bbox, ArrayList<BoundingBox> data, int categories_1st, int categories_2nd, String output) {
		
		int cellNumber=data.size();
		double cellArea=data.get(0).getArea().doubleValue();
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/sampling.log"));
	    	
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        bw.write("Bounding box: "+bbox.getNorth()+", "+bbox.getEast()+", "+bbox.getSouth()+","+bbox.getWest()+"\n");
	        bw.write("Area of the bounding box (km^2): "+bbox.getArea()+"\n");
	        bw.write("Number of cells of the grid: "+cellNumber+"\n");
	        bw.write("Area of a cell (km^2): "+cellArea+"\n");
	        bw.write("Categories number (1st level): "+categories_1st+"\n");
	        bw.write("Categories number (2nd level): "+categories_2nd);
			
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
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
	    	File file=new File(dir.getPath().concat("/clustering.log"));
	 
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
	public void writeSSELog(ArrayList<Double> SSEs, double cl_sse, double pvalue, String output) {
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
			
			bw.write("\n\nPDF(X) evaluated at " + cl_sse + " is equal to: " + pvalue);
			
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
	
	/**
	 * Write the log file of optimization process
	*/
	@SuppressWarnings("rawtypes")
	public void writeOptimizationLog(List<Integer> selected, 
									Map<Integer, Double> map, 
									ArrayList<Double> weights,
									ArrayList<Double> f1,
									ArrayList<Double> f2,
									ArrayList<Double> f3,
									ArrayList<Double> f0,
									String output) 
	{	
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/optimization.log"));
	    	
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        ArrayList<Integer> keys=new ArrayList<Integer>(map.keySet());
	        Collections.sort(keys);
	        
	        bw.write("Clusters before optimization: "+keys.size()+"\n");
	        bw.write("Clusters after optimization: "+selected.size()+"\n");
	        bw.write("Top clusters selected (cluster_id): "+selected.toString()+"\n");
	        bw.write("Weights: "+weights.toString()+"\n");
	        
	        bw.write("\n---------------------------------------------\n");
	        bw.write("Ranking\n");
	        bw.write("---------------------------------------------\n");
	        for (Map.Entry entry : map.entrySet()) {
				bw.write("cluster_id : " + entry.getKey() + "\t\tf0_value : "
					+ entry.getValue()+"\n");
			}
	        
	        for(int i=0;i<f1.size();i++) {
	        	bw.write("\n---------------\n");
	        	bw.write("CLUSTER "+(i+1)+"\n");
	        	bw.write("---------------\n");
	        	bw.write("Spatial coverage = "+f1.get(i)+"\n");
	        	bw.write("Density = "+f2.get(i)+"\n");
	        	bw.write("Heterogeneity = "+f3.get(i)+"\n");
	        	bw.write("Total = "+f0.get(i)+"\n");
	        }
			
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}
	}


	public void writeParetoLog(Collection<ParetoPoint> paretoPoints, 
							   List<Integer> selected, 
							   String output) 
	{	
		try {
			File dir=new File(output); //create the output directory if it doesn't exist
        	dir.mkdirs();
	    	File file=new File(dir.getPath().concat("/pareto.log"));
	    	
    		FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        bw.write("Clusters before optimization: "+ paretoPoints.size() +"\n");
	        bw.write("Object on the Pareto Efficient Frontier: "+selected.size()+"\n");
	        
	        bw.flush();
	        bw.close();
	 
    	} catch(IOException e){
    		e.printStackTrace();
    	}		
	}
}
