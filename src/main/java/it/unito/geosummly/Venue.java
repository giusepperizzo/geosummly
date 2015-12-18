package it.unito.geosummly;

/**
 * Created by pysherlock on 12/17/15.
 * Venue for 3cixty JSON file
 *
 */
public class Venue {
    long TimeStamp;
    String ID;
    int BeenHere;
    String Category;
    String Label;
    Double Latitude;
    Double Longitude;

    public Venue(){}

    public Venue(long timeStamp, int beenHere, String id, String category, String label, Double latitude, Double longitude) {
        TimeStamp = timeStamp;
        BeenHere = beenHere;
        ID = id;
        Category = category;
        Label = label;
        Latitude = latitude;
        Longitude = longitude;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setBeenHere(int beenHere) {
        BeenHere = beenHere;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getCategory() {
        return Category;
    }

    public String getLabel() {
        return Label;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public int getBeenHere() {
        return BeenHere;
    }

    public String getID() {
        return ID;
    }

    public String toString(){
        return "Category: " + Category + " Label: " + Label + " Latitude: " + Latitude + " Longitude: " + Longitude + "\n";
    }
}
