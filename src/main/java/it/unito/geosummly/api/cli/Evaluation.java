package it.unito.geosummly.api.cli;

import java.io.IOException;

import it.unito.geosummly.EvaluationOperator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Evaluation {
	
	private String evalType=null;
	private String inLog=null;
	private String inNorm=null;
	private String inSingles=null;
	private String outDir=null;
	private int mnum=500;
	private int fnum=10;
	
	public void run(String[] args) {
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		
		String helpUsage="geosummly evaluation -etype validation -input <path/to/file.log> -venues <path/to/file.csv> -output <path/to/dir> [options]";
		String helpFooter="\n------------------------------------------------------------------"
				+ "\nThe options etype, input, frequency (only if etype is equal to correctness), "
				+ "venues (only if etype is equal to validation), output are mandatory."
				+ "\nThe input file has to be the log file returned by the clustering state."
				+ "\nIf etype argument is equal to correctness, the frequency option "
				+ "(csv file of grid-shaped aggregates) is mandatory and, for each of the mnum matrices, "
				+ "the output is: a random grid-shaped aggregates, a grid of density values of the previous "
				+ "aggregates, a grid with intra-feature normalized density values shifted in [0,1]. "
				+ "In addition to the output a SSE log and a R script (visualization of SSE values) are provided. "
				+ "Moreover venues and fnum options cannot be used."
				+ "If etype argument is equal to validation, "
				+ "the venues option (csv file of single venues) is mandatory and, for each fold, "
				+ "the output is a file of density values and a file with intra-feature normalized density values "
				+ "shifted in [0,1]. In addition to the output a Jaccard log is provided. "
				+ "Moreover frequency and mnum options cannot be used."
				+ "\n--------------------------------------------------------"
				+ "\nExamples:"
				+ "\n1. geosummly evaluation -etype correctness -input path/to/file.log "
				+ "-normalized path/to/file1.csv -output path/to/dir -mnum 300"
				+ "\n2. geosummly evaluation -etype validation -input path/to/file.log "
				+ "-venues path/to/file1.csv -output path/to/dir -fnum 5";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("etype") && line.hasOption("input") && line.hasOption("output")) {
				evalType=line.getOptionValue("etype");
				if(!evalType.equals("correctness") && !evalType.equals("validation")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				inLog=line.getOptionValue("input");
				//file extension has to be log
				if(!inLog.endsWith("log")) {
					formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				if(line.hasOption("normalized")) {
					if(evalType.equals("correctness")) {
						inNorm=line.getOptionValue("normalized");
						mandatory=true;
					}
					else {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}
				}
				if(line.hasOption("venues")) {
					if(evalType.equals("validation")) {
						inSingles=line.getOptionValue("venues");
						mandatory=true;
					}
					else {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}
				}
				outDir=line.getOptionValue("output");
			}
			
			if(mandatory) {
				if(line.hasOption("mnum")) {
					if(!evalType.equals("correctness")) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}
					mnum=Integer.parseInt(line.getOptionValue("mnum"));
					if(mnum<0) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}		
				}
				
				if(line.hasOption("fnum")) {
					if(!evalType.equals("validation")) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}
					fnum=Integer.parseInt(line.getOptionValue("fnum"));
					if(fnum<0) {
						formatter.printHelp(helpUsage, "\ncommands list:", options, helpFooter);
						System.exit(-1);
					}		
				}
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			EvaluationOperator eo=new EvaluationOperator();
			if(evalType.equals("correctness")) {
				eo.executeCorrectness(inLog, inNorm, outDir, mnum);
			}
			else if(evalType.equals("validation"))
				eo.executeValidation(inLog, inSingles, outDir, fnum);
			
		}
		catch(ParseException | NumberFormatException | IOException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	private Options initOptions() {
			 
		 Options options = new Options(); //define list of options
		 
		 //option etype
		 options.addOption(OptionBuilder.withLongOpt("etype")
				 			.withDescription("set the operation to do. Allowed values: correctness, validation")
				 			.hasArg().withArgName("arg").create("E")
				 		   );
		 
		 //option input
		 options.addOption(OptionBuilder.withLongOpt("input")
				 			.withDescription("set the clustering log as input file")
				 			.hasArg().withArgName("path/to/file").create("I")
				 		  );
		 
		 //option normalized
		 options.addOption(OptionBuilder.withLongOpt("normalized")
				 			.withDescription("set as input file the normalized matrix")
				 			.hasArg().withArgName("path/to/file").create("N")
				 		   );
		 
		 //option venues
		 options.addOption(OptionBuilder.withLongOpt("venues")
				 			.withDescription("set the input file of single venues")
				 			.hasArg().withArgName("path/to/file").create("V")
				 		  );
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output")
				 			.withDescription("set the output directory")
				 			.hasArg().withArgName("path/to/dir").create("O")
				 		  );
		 
		 //option mnum
		 options.addOption(OptionBuilder.withLongOpt("mnum")
				 			.withDescription("set the random matrix number to create. Default 500")
				 			.hasArg().withArgName("arg").create("m")
				 		  );
		 
		//option fnum
		 options.addOption(OptionBuilder.withLongOpt("fnum")
				 			.withDescription("set the fold number to create for the cross-validation. Default 10")
				 			.hasArg().withArgName("arg").create("f")
				 		  );
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}

}
