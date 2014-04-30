package it.unito.geosummly.api.cli;

import java.io.IOException;
import java.util.ArrayList;

import it.unito.geosummly.ImportOperator;
import it.unito.geosummly.tools.CoordinatesNormalizationType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Import {
		
	private String inFile=null;
	private String outDir=null;
	private ArrayList<Double> coordinates=new ArrayList<Double>();
	private Integer gridCells=null;
	private CoordinatesNormalizationType coordType=CoordinatesNormalizationType.NORM;
	
	public void run (String[] args) {
		
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		String helpUsage="geosummly import -input <path/to/file.csv> -coord<n,e,s,w> "
				+ "-gnum<arg> -output <path/to/dir> [options]";
		String helpFooter="\n------------------------------------------------------------------"
				+ "\nThe options input, coord, gnum, output are mandatory."
				+ "\nInput file has to be a .csv of single venues, output of the sampling state."
				+ "\nThe output consist of a file of grid-shaped aggregated venues, "
				+ "a file of density values of the previous aggregates, "
				+ "a file with intra-feature normalized density values shifted in [0,1] "
				+ "\n------------------------------------------------------------------"
				+ "\nExamples:"
				+ "\ngeosummly import -input path/to/file.csv -coord 48,8,44,7 "
				+ "-gnum 100 -output path/to/dir -ltype notnorm";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("input") && line.hasOption("coord") && line.hasOption("gnum") && line.hasOption("output")) {
				
				inFile=line.getOptionValue("input");
				
				//file extension has to be csv
				if(!inFile.endsWith("csv")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				
				//read the coordinates
				String[] c=line.getOptionValues("coord");
				for(String s: c)
					coordinates.add(Double.parseDouble(s));
				
				//read gnum parameter
				gridCells=Integer.parseInt(line.getOptionValue("gnum"));
				if(gridCells<0) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				
				outDir=line.getOptionValue("output");
				mandatory=true;
			}
			
			if(line.hasOption("ltype")) {
				String l=line.getOptionValue("ltype");
				if(l.equals("norm"))
					coordType=CoordinatesNormalizationType.NORM;
				else if(l.equals("notnorm"))
					coordType=CoordinatesNormalizationType.NOTNORM;
				else if(l.equals("missing"))
					coordType=CoordinatesNormalizationType.MISSING;
				else {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			ImportOperator imp=new ImportOperator();
			imp.execute(inFile, coordinates, gridCells, coordType, outDir);
			
		}
		catch(IOException | ParseException | NumberFormatException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	 
	 
	@SuppressWarnings("static-access")
	private Options initOptions() {
			 
		 Options options = new Options(); //define list of options
		 
		//option input
		 options.addOption(OptionBuilder.withLongOpt("input").withDescription("set the input file")
						.hasArg().withArgName("path/to/file").create("I"));
		 
		//option coord
		 options.addOption(OptionBuilder.withLongOpt("coord").withDescription("set the bounding box coordinates")
					.hasArgs(4).withValueSeparator(',').withArgName("n,e,s,w").create("L"));
		 
		 options.addOption(OptionBuilder.withLongOpt("gnum").withDescription("set the number of cells of a side of the squared grid. Default 20")
					.hasArg().withArgName("arg").create("g"));
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("set the output directory")
					.hasArg().withArgName("path/to/dir").create("O"));
		 
		 //option ltype
		 options.addOption(OptionBuilder.withLongOpt( "ltype" ).withDescription("set the type of coordinates (latitude and longitude) normalization. Allowed values: norm, notnorm, missing. Default norm")
					.hasArg().withArgName("arg").create("l"));
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}
}
