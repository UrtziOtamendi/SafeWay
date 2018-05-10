package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

public class linkedID {

    private String uid;
    private String receptor1=null;
    private String receptor2=null;

    //Constructors
    public linkedID(String id){
        uid=id;
    }

    public linkedID(String id, String id1 ){
        uid=id;
        receptor1=id1;
    }

    public linkedID(String id, String id1 ,  String id2){
        uid=id;
        receptor1=id1;
        receptor2=id2;
    }


    public String getUid() {
        return uid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("receptor1",receptor1);
        result.put("receptor2", receptor2);

        return result;
    }

    public String getReceptor1() {
        return receptor1;
    }

    public String getReceptor2() {
        return receptor2;
    }
}
