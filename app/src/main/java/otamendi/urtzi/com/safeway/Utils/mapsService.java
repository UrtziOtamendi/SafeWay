package otamendi.urtzi.com.safeway.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.List;


public class mapsService {

    private static final String TAG= "Maps Service";

    public static boolean checkLocationEnabled(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           return true;
        } else {
            return false;
        }
    }


    public static Criteria saveBatteryLifeCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        return criteria;
    }


    public static String bindAddress(List<Address> addresses){
        String addressString= "";
        if(addresses.size()!=0){
            Address address = addresses.get(0);
            Log.d(TAG,"Address----->"+address.toString());
            addressString= address.getFeatureName();
        }
        return addressString;
    }



}
