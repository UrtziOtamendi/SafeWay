package otamendi.urtzi.com.safeway.Domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class trackingLocation {


    private Date date;
    private double lat;
    private double lon;

    public trackingLocation(){}


    public trackingLocation(Date date, double lat, double lon) {
        this.date = date;
        this.lat = lat;
        this.lon = lon;
    }

    public LatLng toLatLng(){
        return new LatLng(lat,lon);
    }
    public Date getDate() {
        return date;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}

