package it.unito.geosummly.api.cli;

import it.unito.geosummly.OptimizationOperator;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Optimization {
	
	private String inGeo=null;
	private String inLog=null;
	private String outDir=null;
	private ArrayList<Double> fWeights=new ArrayList<Double>();
	private int topCat=10;
	
	public void run (String[] args) throws IOException {
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		String helpUsage="geosummly optimization -input <path/to/file.geojson> -infos <path/to/file.log> -output <path/to/dir> [options]";
		String helpFooter="\n------------------------------------------------------------------"
				+ "\nThe options input, infos, output are mandatory."
				+ "\nInput file has to be a geojson file, output of the clustering state."
				+ "\nInfos file has to be a log file, output of the sampling state."
				+ "\nThe output consists of a geojson file with the clustering result after the optimization, a log file."
				+ "\n------------------------------------------------------------------"
				+ "\nExamples:"
				+ "\ngeosummly optimization -input path/to/file.geojson -infos path/to/file1.log -output path/to/dir -weight 0.5,0.2,0.3 -top 5";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("input") && line.hasOption("infos") && line.hasOption("output")) {
				inGeo=line.getOptionValue("input");
				//file extension has to be geojson
				if(!inGeo.endsWith("geojson")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				
				inLog=line.getOptionValue("infos");
				//file extension has to be geojson
				if(!inLog.endsWith("log")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				
				outDir=line.getOptionValue("output");
				mandatory=true;
			}
			
			if(line.hasOption("weight")) {
				String[] c=line.getOptionValues("weight");
				for(String s: c)
					fWeights.add(Double.parseDouble(s));		
			}
			else {
				int i=0;
				while(i<3) {
					fWeights.add(0.3);
					i++;
				}
			}
			
			if(line.hasOption("top")) {
				topCat=Integer.parseInt(line.getOptionValue("top"));
				if(topCat<0) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}		
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			OptimizationOperator o=new OptimizationOperator();
			o.execute(inGeo, inLog, outDir, fWeights, topCat);
			
		}
		catch(ParseException | NumberFormatException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	private Options initOptions() {
			 
		 Options options = new Options(); //define list of options
		 
		 //option input
		 options.addOption(OptionBuilder.withLongOpt("input").withDescription("set the geojson input file")
						.hasArg().withArgName("path/to/file").create("I"));
		 
		 //option infos
		 options.addOption(OptionBuilder.withLongOpt("infos").withDescription("set the log input file")
					.hasArg().withArgName("path/to/file").create("i"));
		 
		 //option top
		 options.addOption(OptionBuilder.withLongOpt("top").withDescription("set the number of clusters to hold in the fingerprint. Default 10")
					.hasArg().withArgName("arg").create("t"));
		 
		 //option weight
		 options.addOption(OptionBuilder.withLongOpt("weight").withDescription("set the weights to assign to each optimization function. Default 0.3")
					.hasArgs(3).withValueSeparator(',').withArgName("w1,w2,w3").create("w"));
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("set the output directory")
					.hasArg().withArgName("path/to/dir").create("O"));
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}

}
