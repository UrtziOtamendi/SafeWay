package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

public class myLocation {
    private String name;
    private String address;
    private double lat;
    private double lon;

    public myLocation(){};

    public myLocation(String name, String address, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();





        result.put("address", address);
        result.put("latitude", lat);
        result.put("longitude", lon);


        return result;


    }
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
