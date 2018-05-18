package otamendi.urtzi.com.safeway.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.List;

import otamendi.urtzi.com.safeway.Activities.HomeActivity;
import otamendi.urtzi.com.safeway.R;


public class mapsService {

    private static final String TAG = "Maps Service";

    public static boolean checkLocationEnabled(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        } else {
            return true;
        }
    }



    public static Criteria saveBatteryLifeCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        return criteria;
    }


    public static String distanceToGoal(LatLng goal, LatLng position) {
        String distance = "";
        if (position == null) {
            Log.d(TAG, "Current location null");
            return distance;
        }
        if (position.longitude == -1 && position.latitude == -1) {
            Log.d(TAG, "Current location -1,-1");
            return distance;
        }
        float[] results = new float[1];
        Location.distanceBetween(position.latitude, position.longitude,
                goal.latitude, goal.longitude, results);

        if (results != null && results.length > 0) {
            float distancAux = results[0];
             distancAux= distancAux/1000;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(3);
            distance=df.format(distancAux);
            Log.d(TAG, "RESULTS------->" +df.format(distancAux));
        }
        return distance;
    }

    public static String bindAddress(List<Address> addresses) {
        String addressString = "";
        if (addresses.size() != 0) {
            Address address = addresses.get(0);
            Log.d(TAG, "Address----->" + address.toString());
            addressString = address.getFeatureName();
        }
        return addressString;
    }

    public static LocationRequest getLocationRequest(){

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000 * 10);
        locationRequest.setFastestInterval(1000 * 5);
        return locationRequest;
    }



}
