package otamendi.urtzi.com.safeway.Utils.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import otamendi.urtzi.com.safeway.Activities.safeWayHome;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.MainApplication;
import otamendi.urtzi.com.safeway.Utils.mapsService;
import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

public class locationService extends Service {


    private static final String TAG = "locationService";
    private  FusedLocationProviderClient mFusedLocationClient;
    private final static int NOTIFICATION_ID=getID();
    private LatLng destination;
    private Handler handler;
    private final IBinder mIBinder = new LocalBinder();


    private static int getID(){
        Date now = new Date();
       return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.getDefault()).format(now));
    }

    public locationService() {

    }
    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        return START_STICKY;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification());
        }
        Log.e(TAG,"OnCreate");
        Double lat= Double.parseDouble(sharedPreferences.readString(MainApplication.getAppContext(),"lat"));
        Double lon= Double.parseDouble(sharedPreferences.readString(MainApplication.getAppContext(),"long"));
        destination = new LatLng(lat,lon);
        initializeLocationClient();
        try {

            Log.d(TAG,"GstartLocationUpdates.");
            mFusedLocationClient.requestLocationUpdates(mapsService.getLocationRequest(),
                    locationUpdatesCallback,
                    null /* Looper */);
        } catch (Exception ex) {
            Log.d(TAG, "-----errror" + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        try {
            stopForeground(true);
            stopLocationUpdates();
            Log.d(TAG,"Stoping service");
            if(handler != null)
            {
                handler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    private Notification createNotification(){
        Notification notification = new Notification(R.drawable.ic_notifications_black_24dp, "GPS tracked",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, safeWayHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return notification;
    }


    private  void initializeLocationClient(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private  void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationUpdatesCallback);
        Log.d(TAG,"stopLocationUpdates.");
        DatabaseService.stopTracking();


    }

    private  LocationCallback locationUpdatesCallback= new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {

                Log.d(TAG,"-------------> Null");
                return;
            }
            Location last= null;
            for (Location location : locationResult.getLocations()) {

                Date date = new Date(location.getTime());
                Log.d(TAG,"------------->" + location.toString());
                trackingLocation tLocation = new trackingLocation(date, location.getLatitude(), location.getLongitude());
                DatabaseService.saveTrackingLocation(tLocation, getBatteryLevel());
                last=location;
                safeWayHome.addLocation(new LatLng(last.getLatitude(), last.getLongitude()));
            }
            if(last!=null){
                Float distance = mapsService.distanceToGoalMeters(destination,new LatLng(last.getLatitude(), last.getLongitude()));
                safeWayHome.changeDistance(distance);
                if((distance<10) && (handler!=null)){
                    Log.e(TAG,"arrived to goal");
                    sharedPreferences.writeBoolean(MainApplication.getAppContext(), "tracking", false);
                    handler.handleMessage(new Message());
                    stopSelf();
                }
                if(sharedPreferences.readBoolean(MainApplication.getAppContext(),"emergency")){
                    if(sharedPreferences.readBoolean(MainApplication.getAppContext(),"emergencyPosition")){
                        sharedPreferences.writeBoolean(MainApplication.getAppContext(),"emergencyPosition", false);
                        Date date = new Date(last.getTime());
                        DatabaseService.saveLiveTrackingEmergencyPosition( new trackingLocation(date, last.getLatitude(), last.getLongitude()));
                    }
                }

            }

        }
    };

    private  LocationCallback locationLastUpdatesCallback= new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG,"LAST");
            if (locationResult == null) {

                Log.d(TAG,"LAST--------> Null");
                return;
            }
            for (Location location : locationResult.getLocations()) {

                Date date = new Date(location.getTime());
                Log.d(TAG,"LAST------>" + location.toString());
                trackingLocation tLocation = new trackingLocation(date, location.getLatitude(), location.getLongitude());
                DatabaseService.saveTrackingLocation(tLocation, getBatteryLevel());
            }
            if(sharedPreferences.readBoolean(MainApplication.getAppContext(),"tracking")==false){
                stopLocationUpdates();
            }
        }
    };


    private int getBatteryLevel(){
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        return   bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    public void registerHandler(Handler serviceHandler) {
        handler = serviceHandler;
    }

    public class LocalBinder extends Binder
    {
        public locationService getInstance()
        {
            return locationService.this;
        }
    }

}
