package it.unito.geosummly.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public enum PropFactory {
    
    /*
     *  Singleton mode
     */
    INSTANCE;
    
    public static Properties config = new Properties();
    static {
      try {
          config.load(new FileInputStream(new File("props/config.properties")));
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
}
