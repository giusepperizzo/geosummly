package it.unito.geosummly.api.cli;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.SamplingOperator;
import it.unito.geosummly.tools.CoordinatesNormalizationType;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.json.JSONException;

/**
 * @author James
 *
 * API for sampling state
 */

public class Sampling {
	
	private String inFile=null;
	private String outDir=null;
	private ArrayList<Double> coordinates=new ArrayList<Double>();
	private int gridCells=20;
	private int randomCells=-1;
	private long sleepMs=0;
	private CoordinatesNormalizationType coordType=CoordinatesNormalizationType.NORM;
	//private String socNet="foursquare";
	
	public void run (String[] args) {
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		Boolean inputFlag=false; //check the presence either of input or coord;
		String helpUsage="geosummly sampling -input <path/to/file.geojson> -output <path/to/dir> [options]";
		String helpFooter="\n------------------------------------------------------------------"
				+ "\nThe options coord, input (only if coord is not specified), output are mandatory."
				+ "\nThe options input and coord are mutually exclusive."
				+ "\nThe options input and gnum are mutually exclusive."
				+ "\nThe options input and rnum are mutually exclusive. "
				+ "\nThe output consist of a file of single venues for "
				+ "each of the two levels of the Foursquare categories taxonomy, "
				+ "a log file with the sampling informations."
				+ "\n------------------------------------------------------------------"
				+ "\nExamples:"
				+ "\n1. geosummly sampling -input path/to/file.geojson -output path/to/dir -sleep 730 -ctype missing"
				+ "\n2. geosummly sampling -coord 45.01,8.3,44.0,7.2856 -output path/to/dir -gnum 40 -srnum 100";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("input") && line.hasOption("output")) {
				inFile=line.getOptionValue("input");
				//file extension has to be geojson
				if(!inFile.endsWith("geojson")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				outDir=line.getOptionValue("output");
				mandatory=true;
				inputFlag=true;
			}
			
			if(line.hasOption("coord") && line.hasOption("output")) {
				String[] c=line.getOptionValues("coord");
				for(String s: c)
					coordinates.add(Double.parseDouble(s));
				outDir=line.getOptionValue("output");
				if(line.hasOption("gnum")) {
					gridCells=Integer.parseInt(line.getOptionValue("gnum"));
					if(gridCells<0) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}		
				}
				if(line.hasOption("rnum")) {
					randomCells=Integer.parseInt(line.getOptionValue("rnum"));
					if(randomCells<0) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}
				}
				mandatory=true;
				inputFlag=false;
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
			
			/*if(line.hasOption("social")) {
				//manage social media
			}
			if(line.hasOption("cache")) {
				//manage cache
			}*/
			
			if(line.hasOption("sleep")) {
				sleepMs=Long.parseLong(line.getOptionValue("sleep"));
				if(sleepMs<0) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			SamplingOperator so=new SamplingOperator();
			if(inputFlag)
				so.executeWithInput(inFile, outDir, coordType, sleepMs);
			else
				so.executeWithCoord(coordinates, outDir, gridCells, randomCells, coordType, sleepMs);
			
		}
		catch(ParseException | NumberFormatException | FoursquareApiException | IOException | JSONException | InterruptedException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("static-access")
	private Options initOptions() {
			 
		 Options options = new Options(); //define list of options
		 
		 //option input
		 Option input=OptionBuilder.withLongOpt("input").withDescription("set the input file")
						.hasArg().withArgName("path/to/file").create("I");
		 
		 //options input and coord have to be mutually exclusive
		 OptionGroup g1=new OptionGroup();
		 g1.addOption(input);
		 g1.addOption(OptionBuilder.withLongOpt("coord").withDescription("set the input grid coordinates")
						.hasArgs(4).withValueSeparator(',').withArgName("n,e,s,w").create("L"));
		 
		 //options input and gnum have to be mutually exclusive
		 OptionGroup g2=new OptionGroup();
		 g2.addOption(input);
		 g2.addOption(OptionBuilder.withLongOpt("gnum").withDescription("set the number of cells of a side of the squared grid. Default 20")
						.hasArg().withArgName("arg").create("g"));
		 
		 //options input and rnum have to be mutually exclusive
		 OptionGroup g3=new OptionGroup();
		 g3.addOption(input);
		 g3.addOption(OptionBuilder.withLongOpt("rnum").withDescription("set the number of cells, taken randomly, chosen for the sampling")
						.hasArg().withArgName("arg").create("r"));
		 
		 //add mutually exclusive options for sampling
		 options.addOptionGroup(g1);
		 options.addOptionGroup(g2);
		 options.addOptionGroup(g3);
		 
		 //add all other options for sampling
		 options.addOption(OptionBuilder.withLongOpt( "output" ).withDescription("set the output directory")
							.hasArg().withArgName("path/to/dir").create("O"));
		 options.addOption(OptionBuilder.withLongOpt( "ltype" ).withDescription("set the type of coordinates (latitude and longitude) normalization. Allowed values: norm, notnorm, missing. Default norm")
							.hasArg().withArgName("arg").create("l"));
		 options.addOption(OptionBuilder.withLongOpt("social").withDescription("set the social network for meta-data collection. So far only foursquare is activable. Default fourquare")
							.hasArg().withArgName("arg").create("s"));
		 options.addOption(OptionBuilder.withLongOpt( "sleep" ).withDescription("set the milliseconds between two calls to social media server. Default 0")
					.hasArg().withArgName("arg").create("z"));
		 options.addOption("C", "cache", false, "cache activation. Default deactivated");
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}
}
