package otamendi.urtzi.com.safeway.Domain;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class receptorID {

    private String link1 =null;
    private String link2 =null;

    private String name1 =null;
    private String name2 =null;

    public receptorID(){}
    public receptorID(String nm1, String id1 ){

        link1 =id1;
        name1=nm1;
    }

    public receptorID(String nm1, String id1 , String nm2, String id2){

        link1 =id1;
        link2 =id2;
        name1 =nm1;
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

    @Override
    public String toString() {
        return "receptorID{" + '\n' +
                "   link1='" + link1 + '\n' +
                "   link2='" + link2 + '\n' +
                "   name1='" + name1 + '\n' +
                "   name2='" + name2 + '\n' +
                '}';
    }

    public static receptorID bindDataSnapshot(DataSnapshot data){
        Log.d("receptorID",data.toString());
        if(data==null){
            return new receptorID();
        }else{
            Iterator<DataSnapshot> iterator = data.getChildren().iterator();
            while(iterator.hasNext()){
                DataSnapshot aux = iterator.next();
                aux.getKey();
                aux.getValue();
            }
        }
        return new receptorID();
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

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}
