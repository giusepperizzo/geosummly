package it.unito.geosummly.api.cli;

import java.io.IOException;

import it.unito.geosummly.DiscoveryOperator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Discovery {
	
	private String inFile=null;
	private String outDir=null;
	private int comb=5;
	
	public void run (String[] args) throws IOException {
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		String helpUsage="geosummly discovery -input<path/to/file.csv> -output<path/to/dir> [options]";
		String helpFooter="\nThe options input, output are mandatory. Input file has to be a .csv either of singles or"
							+ " grid-shaped venues. The output consist of a file of standard deviation values for"
							+ " the categories combinations.";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("input") && line.hasOption("output")) {
				inFile=line.getOptionValue("input");
				//file extension has to be csv
				if(!inFile.endsWith("csv")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				outDir=line.getOptionValue("output");
				mandatory=true;
			}
			
			if(line.hasOption("combination")) {
				comb=Integer.parseInt(line.getOptionValue("combination"));
				if(comb<0) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}		
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(150, helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			DiscoveryOperator d=new DiscoveryOperator();
			d.execute(inFile, outDir, comb);
			
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
		 options.addOption(OptionBuilder.withLongOpt("input").withDescription("set the input file")
						.hasArg().withArgName("path/to/file").create("I"));
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("set the output directory")
					.hasArg().withArgName("path/to/dir").create("O"));
		 
		 //option combination
		 options.addOption(OptionBuilder.withLongOpt("combination").withDescription("set the number of categories combinations for minpts estimation. Default 5")
					.hasArg().withArgName("arg").create("c"));
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}
}
