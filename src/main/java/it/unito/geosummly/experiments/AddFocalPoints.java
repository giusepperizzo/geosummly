package it.unito.geosummly.experiments;

import it.unito.geosummly.api.MainCLI;
import it.unito.geosummly.tools.ImportTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class AddFocalPoints {

	private String pathIn = "/home/rizzo/Cloud/Dropbox/SMAT-F2_WP4_UNITO/Dati/IRPI-CNR/Alluvioni/wip/single-matrix-nocentroid.csv";
	private String pathOut = "/home/rizzo/Cloud/Dropbox/SMAT-F2_WP4_UNITO/Dati/IRPI-CNR/Alluvioni/wip/";
	private Integer gnum = 16;
	private String features_header;
	private List<Record> objects = new ArrayList<Record> ();
	private Double north;
	private Double south;
	private Double est; 
	private Double west;
	private Double delta = 0.00001;
	
	public static void main(String[] args) {
		AddFocalPoints main = new AddFocalPoints();
		main.readFile();
		main.computeBoundaries();
		main.buildBBox();
		main.output();
		
		final String args_ = "import -input /home/rizzo/Cloud/Dropbox/SMAT-F2_WP4_UNITO/Dati/IRPI-CNR/Alluvioni/wip/single-matrix.csv "
				+ "-coord 44.557237754000006,8.183214487999999,44.2085034374,7.028183287 "
				+ "-gnum 16 -output /home/rizzo/Cloud/Dropbox/SMAT-F2_WP4_UNITO/Dati/IRPI-CNR/Alluvioni/wip/";
		MainCLI.main(args_.split(" "));
	}
	
	private void buildBBox() 
	{
		int num = (int) Math.pow(gnum, 0.5);
		Double lngIncr = (est-west) / (num); 
		Double latIncr = (north-south) / (num);
		
		Double northTemp = north;
		for (int i=0; i<num; i++) {
			Double westTemp = west;
			for (int j=0; j<num; j++) {
				Double cellN = northTemp;
				Double cellE = westTemp+lngIncr;
				Double cellS = northTemp-latIncr;
				Double cellW = westTemp;
				assignVenuesToCell(cellN,cellE,cellS,cellW);
				//System.out.print(northTemp + "," + (westTemp+lngIncr) + "," + (northTemp-latIncr) + "," + westTemp + " - ");			
				westTemp += lngIncr;
			}
			northTemp -= latIncr;
		}
	}

	private void assignVenuesToCell(Double cellN, Double cellE, 
									Double cellS, Double cellW
									) 
	{
		
		System.out.println("Filling cell: " + cellN + "," + cellE + "," + cellS + "," + cellW);
		
		Collections.sort(objects, RecordComparator.descending(RecordComparator.getComparator(RecordComparator.LAT)));
		for (Record r : objects) {			
			if (r.getLat()<=cellN && r.getLat()>=cellS) {
				if(r.getLng()>=cellW && r.getLng()<=cellE) {
					r.setFocalLat( cellN - ((cellN-cellS) / 2) );
					r.setFocalLng( cellW + ((cellE-cellW) / 2) );
				}
			}
		}
	
	}

	private void computeBoundaries() 
	{
		Collections.sort(objects, RecordComparator.ascending(RecordComparator.getComparator(RecordComparator.LAT)));
		this.south = objects.get(0).getLat() - delta;
		this.north = objects.get(objects.size()-1).getLat() + delta;
		
		Collections.sort(objects, RecordComparator.ascending(RecordComparator.getComparator(RecordComparator.LNG)));
		this.west = objects.get(0).getLng() - delta;
		this.est = objects.get(objects.size()-1).getLng() + delta;
	}

	private void output() {
		System.out.println(features_header);
		System.out.println(objects.size());
		System.out.println(north);
		System.out.println(est);
		System.out.println(south);
		System.out.println(west);	
		
		try {
			//write single-matrix
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathOut + "single-matrix.csv"), "utf-8"));
			writer.write(Record.buildHeader() + features_header + "\n");
			for (Record r : objects)
				writer.write(r.serialize() + "\n");
			writer.close();
			
			//write sampling.log
			ImportTools tool = new ImportTools();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathOut + "sampling.log"), "utf-8"));
			writer.write("Bounding Box: " + north + ", " + est + ", " + south + ", " + west + "\n");
			writer.write("Area of the bounding box (km^2): " + tool.getDistance(north, west, south, west) *
															   tool.getDistance(south, west, south, est) + "\n");
			writer.write("Number of cells of the grid: " + gnum + "\n");
			writer.write("Area of a cell (km^2): " + (tool.getDistance(north, west, south, west) *
					   								  tool.getDistance(south, west, south, est)) / gnum + "\n");
			writer.write("Categories number (1st level): " + features_header.split(",").length + "\n");
			writer.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void readFile () 
	{
		try {
			String file = FileUtils.readFileToString(new File(pathIn), "utf8");
			
			for(int i = 0; i < file.split("\n").length; i++) {
				if (i==0) {
					features_header=file.split("\n")[0];
					String[] tmp = features_header.split(",");
					features_header = "";
					for(int j=2; j<tmp.length; j++)
						features_header += tmp[j] + ",";
					features_header = features_header.substring(0, features_header.length()-1);
				}
				
				else {
					String[] chunks = file.split("\n")[i].split(",");
					Record r = new Record();
					r.setLat(Double.parseDouble(chunks[0]));
					r.setLng(Double.parseDouble(chunks[1]));
					ArrayList<Double> features = new ArrayList<>();
					for (int j=2; j<chunks.length; j++)
						features.add(Double.parseDouble(chunks[j]));
					
					r.setFeatures(features);
					
					objects.add(r);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
