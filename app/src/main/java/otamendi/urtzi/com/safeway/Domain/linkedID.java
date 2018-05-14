package otamendi.urtzi.com.safeway.Domain;

import java.util.HashMap;
import java.util.Map;

public class linkedID {

    private String link1 =null;
    private String link2 =null;

    private String name1 =null;
    private String name2 =null;

    public linkedID(){}
    public linkedID( String nm1, String id1 ){

        link1 =id1;
        name1=nm1;
    }

    public linkedID(String nm1, String id1 , String nm2, String id2){

        link1 =id1;
        link2 =id2;
        link2 =id2;
        name2=nm2;
    }




    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        if(name1!= null)
        result.put(name1, link1);
        if(name2!=null)
        result.put(name2, link2);

        return result;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public String getLink1() {
        return link1;
    }

    public String getLink2() {
        return link2;
    }

}
