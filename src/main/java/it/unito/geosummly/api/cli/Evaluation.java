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
	private String inFile=null;
	private String inDeltad=null;
	private String outDir=null;
	private int mnum=500;
	private int fnum=10;
	
	public void run(String[] args) {
		Options options= initOptions(); //define list of options
		CommandLineParser parser=new PosixParser(); //create the command line parser
		HelpFormatter formatter = new HelpFormatter();
		Boolean mandatory=false; //check the presence of mandatory options
		
		String helpUsage="\ngeosummly evaluation -etype validation -input path/to/file.csv -output path/to/dir [options]"
							+ "\ngeosummly evaluation -etype correctness -input path/to/file.csv -output path/to/dir [options]";
		String helpFooter="\nThe options etype, input, output are mandatory. If etype argument is equal to correctness, "
							+ "the input file has to be a .csv of grid-shaped aggregates, and the output is a set of"
							+ " random grid-shaped aggregates. Moreover fnum option cannot be used. If etype argument "
							+ "is equal to validation, the input file has to be a .csv of single venues, and, for each fold, "
							+ "the output is the same as the one returned by the sampling state. Moreover mnum option cannot be used.";
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if(line.hasOption("etype") && line.hasOption("input") && line.hasOption("deltad") && line.hasOption("output")) {
				evalType=line.getOptionValue("etype");
				if(!evalType.equals("correctness") && !evalType.equals("validation")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				inFile=line.getOptionValue("input");
				//file extension has to be csv
				if(!inFile.endsWith("csv")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				inDeltad=line.getOptionValue("deltad");
				//file extension has to be csv
				if(!inDeltad.endsWith("csv")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				outDir=line.getOptionValue("output");
				mandatory=true;
			}
			
			if(line.hasOption("mnum")) {
				if(!evalType.equals("correctness")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				mnum=Integer.parseInt(line.getOptionValue("mnum"));
				if(mnum<0) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}		
			}
			
			if(line.hasOption("fnum")) {
				if(!evalType.equals("validation")) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}
				fnum=Integer.parseInt(line.getOptionValue("fnum"));
				if(fnum<0) {
					formatter.printHelp(150, helpUsage, "\ncommands list:", options, helpFooter);
					System.exit(-1);
				}		
			}
			
			if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(150, helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }
			
			EvaluationOperator eo=new EvaluationOperator();
			if(evalType.equals("correctness")) {
				eo.executeCorrectness(inFile, inDeltad, outDir, mnum);
			}
			else if(evalType.equals("validation"))
			eo.executeValidation(inFile, inDeltad, outDir, fnum);
			
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
		 options.addOption(OptionBuilder.withLongOpt("etype").withDescription("set the operation to do. Allowed values: correctness, validation")
					.hasArg().withArgName("arg").create("E"));
		 
		 //option input
		 options.addOption(OptionBuilder.withLongOpt("input").withDescription("set the input file")
						.hasArg().withArgName("path/to/file").create("I"));
		 
		 //option deltad
		 options.addOption(OptionBuilder.withLongOpt("deltad").withDescription("set the input file of deltad values")
						.hasArg().withArgName("path/to/file").create("S"));
		 
		 //option output
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("set the output directory")
					.hasArg().withArgName("path/to/dir").create("O"));
		 
		 //option mnum
		 options.addOption(OptionBuilder.withLongOpt("mnum").withDescription("set the random matrix number to create. Default 500")
					.hasArg().withArgName("arg").create("m"));
		 
		//option fnum
		 options.addOption(OptionBuilder.withLongOpt("fnum").withDescription("set the fold number to create for the cross-validation. Default 10")
					.hasArg().withArgName("arg").create("f"));
		 
		//more options
		options.addOption("H", "help", false, "print the command list");
		
		return options;
	}

}
