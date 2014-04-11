package it.unito.geosummly.io;

import it.unito.geosummly.BoundingBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public interface IGeoWriter {
    public void writeStream(		
    							BoundingBox bbox,
    							HashMap<Integer, 
    							String> labels, 
    							HashMap<Integer, ArrayList<ArrayList<Double>>> cells, 
    							HashMap<Integer, ArrayList<ArrayList<String>>> venues, 
    							double eps, 
    							String output,
    							Calendar cal) ; 
}
