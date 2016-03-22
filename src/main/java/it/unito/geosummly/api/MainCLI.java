package it.unito.geosummly.api;

import java.io.IOException;
import java.util.Arrays;

import it.unito.geosummly.api.cli.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class MainCLI {

	public static void main(String[] args) {
		MainCLI mainCLI=new MainCLI();
		mainCLI.run(args);
	}

	private void run(String[] args) {
		
		CommandLineParser parser=new PosixParser(); //create the command line parser
		String helpFooter="Allowed operation: sampling, import, discovery, clustering, evaluation, optimization."
				+ "\nDigit <operation> help for more details.";

		try {
			CommandLine line = parser.parse(new Options(), args, true);
			String action= line.getArgs()[0];
			args=Arrays.copyOfRange(args, 1, args.length); //delete the argument of the action
			switch (action) {
				case "sampling":
					Sampling sampling = new Sampling();
					sampling.run(args);
					break;
				case "D-sampling":
					DynamicSampling dynamicSampling = new DynamicSampling();
					dynamicSampling.run(args);
					break;
				case "import":
					Import imp = new Import();
					imp.run(args);
					break;
				case "discovery":
					Discovery discovery = new Discovery();
					discovery.run(args);
					break;
				case "clustering":
					Clustering clustering = new Clustering();
					clustering.run(args);
					break;
				case "evaluation":
					Evaluation evaluation = new Evaluation();
					evaluation.run(args);
					break;
				case "optimization":
					//			Optimization optimization=new Optimization();
					//			optimization.run(args);
					break;
				default:
					System.out.println("Invalid operation: " + action + ".\n" + helpFooter);
					System.exit(-1);
			}
			
		} catch (ParseException | IOException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
		}
	}

}
