package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by urtzi on 07/05/2018.
 */

public class User {

    private String emergencyNumber;
    private String password ;
    private receptorID receptorsID;
    private Map<String, String> linkedID = new HashMap<>();
    private String notificationToken;

// Constructor
    public User(){

    }
    public User(String emNum){
        emergencyNumber=emNum;
    }
    public User(String emNum, String pass, String token){
        emergencyNumber=emNum;
        password=pass;
        notificationToken=token;
        receptorsID= new receptorID();
    }

    public User(String emNum, String pass, String token, receptorID links){
        emergencyNumber=emNum;
        password=pass;
        notificationToken=token;
        receptorsID= links;
    }

    public User(String emNum, String pass, String token, receptorID links, Map<String, String> linkedWithID){
        emergencyNumber=emNum;
        password=pass;
        notificationToken=token;
        receptorsID= links;
        this.linkedID= linkedWithID;
    }




    @Override
    public String toString() {
        return "User{" + '\n' +
                "   emergencyNumber='" + emergencyNumber + '\n' +
                "   password='" + password + '\n' +
                "   receptorsID=" + receptorsID.toString() + '\n' +
                "   linkedID=" + linkedID.toString() + '\n' +
                "   notificationToken='" + notificationToken + '\n' +
                '}';
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public Map<String, String> getLinkedWithID() {
        return linkedID;
    }

    public String getPassword() {
        return password;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public receptorID getLink() {
        return receptorsID;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLink(receptorID link) {
        this.receptorsID = link;
    }

    public void setLinkedWithID(Map<String, String> linkedWithID) {
        this.linkedID = linkedWithID;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }



}