package it.unito.geosummly.clients;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public interface IGeoKBClient {    
    
    public File getMapMetadata(Double left, Double bottom, Double right, Double top);
    public List<SimpleEntry<String,String>> 
        getAnnotations(Double left, Double bottom, Double right, Double top);
}
