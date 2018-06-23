package otamendi.urtzi.com.safeway.Domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class myLocation {
    private String name;
    private String address;
    private double lat;
    private double lon;
    private int usage=0;

    public myLocation(){}

    public myLocation(String name, String address, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.usage=0;
    }

    public myLocation(String name, String address, double lat, double lon, int usage) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.usage=usage;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();





        result.put("address", address);
        result.put("latitude", lat);
        result.put("longitude", lon);
        result.put("usage", usage);


        return result;


    }

    public LatLng toLatLng(){
        return new LatLng(this.lat, this.lon);
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

    public int getUsage() {
        return usage;
    }

    public void setName(String name) {
        this.name = name;
    }
}
