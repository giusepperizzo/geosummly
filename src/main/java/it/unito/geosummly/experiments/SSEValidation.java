package it.unito.geosummly.experiments;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class SSEValidation {
	
	public static void main(String[] args) 
	{
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("x=c(");
			List<String> lines = 
			 FileUtils.readLines(new File("output/evaluation/clustering_correctness/random/sse.log"));
			NumberFormat nf= NumberFormat.getInstance();
			nf.setMaximumFractionDigits(4);
			nf.setMinimumFractionDigits(4);
			nf.setRoundingMode(RoundingMode.HALF_UP);
			
			for(int i=0; i<lines.size(); i++) {
				String[] chunks = lines.get(i).split(",");
				sb.append( nf.format(Double.parseDouble(chunks[1])) );
				if(i<lines.size()-1) sb.append(",");
			}
			sb.append(");\n");
			sb.append("bins=seq(65,115,by=0.5);\n");
			sb.append("hist(x,"
							+ "breaks=bins,"
							+ "xlab=\"SSE\",ylab=\"count\","
							+ "main=\"Histogram of SSE for 500 random data sets\")");
			
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
