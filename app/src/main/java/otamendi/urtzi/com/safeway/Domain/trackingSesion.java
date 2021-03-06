package otamendi.urtzi.com.safeway.Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class trackingSesion {
    private int battery;
    private myLocation destination;
    private Date ended;
    private Date started;
    private HashMap<String,trackingLocation> locations,emergencyPosition;

    public trackingSesion() {
    }

    public trackingSesion(int battery, myLocation destination, Date started,  Date ended) {
        this.battery = battery;
        this.destination = destination;
        this.ended = ended;
        this.started = started;
        locations= new HashMap<>();
        emergencyPosition= new HashMap<>();
    }

    public trackingSesion(int battery, myLocation destination,  Date started, Date ended, HashMap<String,trackingLocation> locations,  HashMap<String,trackingLocation> emergencyPosition) {
        this.battery = battery;
        this.destination = destination;
        this.ended = ended;
        this.started = started;
        this.locations = locations;
        this.emergencyPosition= emergencyPosition;
    }



    public int getBattery() {
        return battery;
    }

    public myLocation getDestination() {
        return destination;
    }

    public Date getEnded() {
        return ended;
    }

    public Date getStarted() {
        return started;
    }

    public HashMap<String, trackingLocation> getLocations() {
        return locations;
    }

    public List<trackingLocation> getLocationList() {
        return (new ArrayList<>(locations.values()));
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public void setDestination(myLocation destination) {
        this.destination = destination;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public void setLocations(HashMap<String, trackingLocation> locations) {
        this.locations = locations;
    }

    public HashMap<String, trackingLocation> getEmergencyPosition() {
        return emergencyPosition;
    }

    public void setEmergencyPosition(HashMap<String, trackingLocation> emergencyPosition) {
        this.emergencyPosition = emergencyPosition;
    }
}
