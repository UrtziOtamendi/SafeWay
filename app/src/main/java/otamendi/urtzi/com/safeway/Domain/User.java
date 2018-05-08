package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by urtzi on 07/05/2018.
 */

public class User {

    private String emergencyNumber;
    private Map<Integer, String> linkedID = new HashMap<>();



    public User(){

    }

    public void linkID(String ID){
        int i =linkedID.size();
        i++;
        linkedID.put(i,ID);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("emergencyNumber", emergencyNumber);
        result.put("linkedCount",linkedID.size());
        result.put("linkedID", linkedID);

        return result;
    }


    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
    }

    public Map<Integer, String> getLinkedID() {
        return linkedID;
    }

    public void setLinkedID(Map<Integer, String> linkedID) {
        this.linkedID = linkedID;
    }
}