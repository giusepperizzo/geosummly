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
    String Publisher;
    Double Latitude;
    Double Longitude;
    Coordinate coordinate;

    public Venue(){}

    public Venue(long timeStamp, int beenHere, String id, String category, String label, String publisher, Double latitude, Double longitude) {
        TimeStamp = timeStamp;
        BeenHere = beenHere;
        ID = id;
        Category = category;
        Label = label;
        Publisher = publisher;
        Latitude = latitude;
        Longitude = longitude;
        coordinate = new Coordinate();
    }

    public class Coordinate{
        double latitude;
        double logitude;
        String category;

        public Coordinate(){
            latitude = Latitude;
            logitude = Latitude;
            category = Category;
        }

        public void set(double Lat, double Log) {
            latitude = Lat;
            logitude = Log;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Coordinate) {
                if (((Coordinate) obj).equals(this)){
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }

        public boolean equals(Coordinate obj) {
            if(latitude == obj.latitude && logitude == obj.logitude && Category.equals(obj.category)) {
                return true;
            }
            else
                return false;
        }

        @Override
        public int hashCode() {
            int hash = 5381;
            Integer Lat = (int)latitude*10000;
            Integer Log = (int)logitude*10000;
            hash = ((hash<<5)+hash) + Lat.hashCode();
            hash = ((hash<<5)+hash) + Log.hashCode();
            hash = ((hash<<5)+hash) + category.hashCode();

            return hash;
        }
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

    public String getPublisher() {
        return Publisher;
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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String toString(){
        return "Category: " + Category + " Label: " + Label + " Latitude: " + Latitude + " Longitude: " + Longitude + "\n";
    }
}
