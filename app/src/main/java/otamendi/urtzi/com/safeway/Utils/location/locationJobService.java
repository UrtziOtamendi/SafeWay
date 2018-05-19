package otamendi.urtzi.com.safeway.Utils.location;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.mapsService;

public class locationJobService extends JobService {


    private static final String TAG = "locationJobService";
    private  FusedLocationProviderClient mFusedLocationClient;
    private AsyncTask mBackgroundTask;

    public locationJobService() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG,"OnStartJob");
        initializeLocationClient();
        try {
            Looper.prepare();
            Log.d(TAG,"GstartLocationUpdates.");
            mFusedLocationClient.requestLocationUpdates(mapsService.getLocationRequest(),
                    locationUpdatesCallback,
                    null /* Looper */);
        } catch (Exception ex) {
            Log.d(TAG, "-----errror" + ex.getMessage());
        }
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG,"onStopJob");
        stopLocationUpdates();
        return true;
    }



    private  void initializeLocationClient(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private   void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates.");
        DatabaseService.stopTracking();
        mFusedLocationClient.removeLocationUpdates(new LocationCallback());
    }

    private  LocationCallback locationUpdatesCallback= new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {

                Log.d(TAG,"-------------> Null");
                return;
            }
            for (Location location : locationResult.getLocations()) {

                Date date = new Date(location.getTime());
                Log.d(TAG,"------------->" + location.toString());
                trackingLocation tLocation = new trackingLocation(date, location.getLatitude(), location.getLongitude());
                DatabaseService.saveTrackingLocation(tLocation, getBatteryLevel());
            }
        }
    };


    private int getBatteryLevel(){
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        return   bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }


}
