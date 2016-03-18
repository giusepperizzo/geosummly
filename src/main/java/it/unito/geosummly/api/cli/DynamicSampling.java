package it.unito.geosummly.api.cli;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.DynamicReader;
import it.unito.geosummly.SamplingOperator;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import org.apache.commons.cli.*;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by pysherlock on 3/18/16.
 */
public class DynamicSampling {

    private String inFile=null;
    private String outDir=null;
    private long sleepMs=0;
    private CoordinatesNormalizationType coordType=CoordinatesNormalizationType.NORM; //What is this useful, seems that it isn't used in SamplingOperator
    private boolean secondLevel=false;


    public void run(String args[]) {
        Options options= initOptions(); //define list of options
        CommandLineParser parser=new PosixParser(); //create the command line parser
        HelpFormatter formatter = new HelpFormatter();
        Boolean mandatory=false; //check the presence of mandatory options
        Boolean inputFlag=false; //check the presence either of input or coord;

        String helpUsage="geosummly D-sampling -source <city>/<3cixty|googleplaces|facebook|yelp|expedia|vaxita|evensi> -output <path/to/dir> [options]";
        String helpFooter="\n------------------------------------------------------------------"
                + "\nThe options source, output are mandatory."
                + "\nThe output consist of a file of single venues for "
                + "each of the two levels of the Foursquare categories taxonomy, "
                + "a log file with the sampling informations."
                + "\n------------------------------------------------------------------"
                + "\nExamples:"
                + "\n1. geosummly D-sampling -source city/publisher -output path/to/dir -sleep 730 -ctype missing";

        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("source") && line.hasOption("output")) {
                String source = line.getOptionValue("source");

                DynamicReader dynamicReader = new DynamicReader(); //Dynamic read data from online database, using sparql
                inFile = dynamicReader.Cixty_Query(source);
                outDir = line.getOptionValue("output");
                mandatory=true;
                inputFlag = true;
            }

            if (line.hasOption("help") || !mandatory) {
                formatter.printHelp(helpUsage,"\ncommands list:", options, helpFooter);
                System.exit(-1);
            }

            SamplingOperator so=new SamplingOperator();
            if(inputFlag)
                so.executeWithInput(inFile, outDir, coordType, sleepMs, secondLevel);

        }
        catch (ParseException | NumberFormatException | IOException | JSONException | InterruptedException | FoursquareApiException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("static-access")
    private Options initOptions() {

        Options options = new Options(); //define list of options

        //option input
        Option input= OptionBuilder.withLongOpt("input").withDescription("set the input file")
                .hasArg().withArgName("path/to/file").create("I");

        OptionGroup g1=new OptionGroup();
        g1.addOption(input);
        g1.addOption(OptionBuilder.withLongOpt("dynamic").withDescription("set dynamic input target")
                .hasArgs().withArgName("target").create("D"));

        //add mutually exclusive options for sampling
        options.addOptionGroup(g1);

        //add all other options for sampling
        options.addOption(OptionBuilder.withLongOpt( "output" ).withDescription("set the output directory")
                .hasArg().withArgName("path/to/dir").create("O"));
        options.addOption(OptionBuilder.withLongOpt( "ltype" ).withDescription("set the type of coordinates (latitude and longitude) normalization. Allowed values: norm, notnorm, missing. Default norm")
                .hasArg().withArgName("arg").create("l"));
        options.addOption(OptionBuilder.withLongOpt( "sleep" ).withDescription("set the milliseconds between two calls to social media server. Default 0")
                .hasArg().withArgName("arg").create("z"));
        options.addOption("C", "cache", false, "cache activation. Default deactivated");

        //more options
        options.addOption("H", "help", false, "print the command list");

        return options;
    }
}
