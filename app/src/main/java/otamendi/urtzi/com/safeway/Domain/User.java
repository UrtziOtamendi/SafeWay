package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by urtzi on 07/05/2018.
 */

public class User {

    private String emergencyNumber;
    private String password;
    private Map<Integer, String> linkedWithID = new HashMap<>();


// Constructor
    public User(){

    }
    public User(String emNum){
        emergencyNumber=emNum;
    }
    public User(String emNum, String pass){
        emergencyNumber=emNum;
        password=pass;
    }

    public void linkID(String ID){
        int i =linkedWithID.size();
        i++;
        linkedWithID.put(i,ID);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("emergencyNumber", emergencyNumber);
        result.put("password", password);

        result.put("linkedCount",linkedWithID.size());
        result.put("linkedID", linkedWithID);

        return result;
    }


    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public Map<Integer, String> getLinkedWithID() {
        return linkedWithID;
    }

    public String getPassword() {
        return password;
    }
}