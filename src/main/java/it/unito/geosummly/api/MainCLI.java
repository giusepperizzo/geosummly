package it.unito.geosummly.api;

import java.io.IOException;
import java.util.Arrays;

import it.unito.geosummly.api.cli.Clustering;
import it.unito.geosummly.api.cli.Discovery;
import it.unito.geosummly.api.cli.Evaluation;
import it.unito.geosummly.api.cli.Sampling;

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
		
		try {
			CommandLine line = parser.parse(new Options(), args, true);
			String action= line.getArgs()[0];
			args=Arrays.copyOfRange(args, 1, args.length); //delete the argument of the action
			switch (action) {
			case "sampling":
				Sampling sampling=new Sampling();
				sampling.run(args);
				break;
			case "discovery":
				Discovery discovery = new Discovery();
				discovery.run(args);
				break;
			case "clustering":
				Clustering clustering=new Clustering();
				clustering.run(args);
				
				break;
			case "evaluation":
				Evaluation evaluation=new Evaluation();
				evaluation.run(args);
				break;
			default:
				throw new IllegalArgumentException("Invalid operation: " + action + ". Allowed operation: sampling, discovery, clustering, evaluation");
			}
			
		} catch (ParseException | IOException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
		}
	}

}
