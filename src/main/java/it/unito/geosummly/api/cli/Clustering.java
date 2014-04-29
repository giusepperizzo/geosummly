package it.unito.geosummly.api.cli;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import it.unito.geosummly.ClusteringOperator;

public class Clustering {
	
	private String inDensity=null;
	private String inNorm=null;
	private String inDeltad=null;
	private String inVenues=null;
	private ArrayList<Double> coordinates=new ArrayList<Double>();
	private String outDir=null;
	private Double epsValue=0.0;
	//private String method="geosubclu";

	public void run(String[] args) {
		
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		String helpUsage="geosummly clustering -density <path/to/file.csv> -normalized <path/to/file.csv> -deltad <path/to/file.csv> -venues <path/to/file.csv> -coord <n,e,s,w> -output <path/to/dir> [options]";
		String helpFooter="\n------------------------------------------------------------------"
				+ "\nThe options density, normalized,  deltad, venues, coord, output are mandatory."
				+ "\nDensity file has to be a .csv of grid-shaped density values, "
				+ "output of the sampling state. "
				+ "\nNormalized file has to be a .csv of grid-shaped normalized density values, "
				+ "output of the sampling state. "
				+ "\nDeltad file has to be a .csv of deltad values, output of the discovery state. "
				+ "\nVenues file has to be a .csv of single venues, output of the sampling state. "
				+ "\nThe output consists of a .geojson file expressed as a feature collection "
				+ "whose features are the clusters, a set of RDF Turtle file (one for each cluster), "
				+ "a log file with the clustering informations."
				+ "\n------------------------------------------------------------------"
				+ "\nExamples:"
				+ "\ngeosummly clustering -density path/to/file.csv -normalized path/to/file1.csv "
				+ "-deltad path/to/file2.csv -venues path/to/file3.csv -coord 45.01,8.3,44.0,7.2856 "
				+ "-output path/to/dir -method geosubclu -eps 0.03";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("density") && line.hasOption("normalized") && line.hasOption("deltad") && line.hasOption("venues") && line.hasOption("coord") && line.hasOption("output")) {
				inDensity=line.getOptionValue("density");
				inNorm=line.getOptionValue("normalized");
				inDeltad=line.getOptionValue("deltad");
				inVenues=line.getOptionValue("venues");
				String[] c=line.getOptionValues("coord");
				for(String s: c)
					coordinates.add(Double.parseDouble(s));
				//file extension has to be csv
				if(!inDensity.endsWith("csv") || !inNorm.endsWith("csv") || !inDeltad.endsWith("csv") || !inVenues.endsWith("csv")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				outDir=line.getOptionValue("output");
				mandatory=true;
			}
			
			/*if(line.hasOption("method")) {
				//manage clustering method
			}*/
			
			if(line.hasOption("eps")) {
				epsValue=Double.parseDouble(line.getOptionValue("eps"));
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			ClusteringOperator co=new ClusteringOperator();
			co.execute(coordinates, inDensity, inNorm, inDeltad, inVenues, outDir, epsValue, "");
		}
		catch(IOException | ParseException | NumberFormatException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("static-access")
	private Options initOptions() {
			 
		 Options options = new Options(); //define list of options
		 
		//option density
		 options.addOption(OptionBuilder.withLongOpt("density").withDescription("set the input file of density values")
						.hasArg().withArgName("path/to/file").create("D"));
		 
		//option normalized
		 options.addOption(OptionBuilder.withLongOpt("normalized").withDescription("set the input file of normalized density values")
					.hasArg().withArgName("path/to/file").create("N"));
		 
		//option input deltad
		 options.addOption(OptionBuilder.withLongOpt("deltad").withDescription("set the input file of deltad values")
					.hasArg().withArgName("path/to/file").create("S"));
		 
		//option venues
		 options.addOption(OptionBuilder.withLongOpt("venues").withDescription("set the input file of single venues")
					.hasArg().withArgName("path/to/file").create("V"));
		 
		 //option coord
		 options.addOption(OptionBuilder.withLongOpt("coord").withDescription("set the bounding box coordinates")
					.hasArgs(4).withValueSeparator(',').withArgName("n,e,s,w").create("L"));
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("set the output directory")
					.hasArg().withArgName("path/to/dir").create("O"));
		 
		 //option method
		 options.addOption(OptionBuilder.withLongOpt("method").withDescription("set the clustering algorithm. So far only geosubclu is activable. Default geosubclu")
					.hasArg().withArgName("arg").create("c"));
		 
		 //option eps
		 options.addOption(OptionBuilder.withLongOpt("eps").withDescription("set the eps value of clustering algorithm. Default sqrt(2) * (1/ sqrt( size(density_values) ))")
					.hasArg().withArgName("arg").create("e"));
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}
}
