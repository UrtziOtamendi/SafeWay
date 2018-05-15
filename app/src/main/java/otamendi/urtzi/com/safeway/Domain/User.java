package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by urtzi on 07/05/2018.
 */

public class User {

    private String emergencyNumber;
    private String password ;
    private linkedID link;
    private Map<String, String> linkedWithID = new HashMap<>();
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
        link= new linkedID();
    }

    public void updateLink(linkedID newLink){
        link=newLink;
    }

    public void linkID(String name, String ID){

        linkedWithID.put(name,ID);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("emergencyNumber", emergencyNumber);
        result.put("password", password);
        result.put("notificationToken", notificationToken);
        result.put("linkedIDs", linkedWithID);
        result.put("receptorsID", link.toMap());

        return result;
    }


    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public Map<String, String> getLinkedWithID() {
        return linkedWithID;
    }

    public String getPassword() {
        return password;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public linkedID getLink() {
        return link;
    }
}