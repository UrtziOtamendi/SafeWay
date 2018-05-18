package otamendi.urtzi.com.safeway.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;
import java.util.List;

import otamendi.urtzi.com.safeway.Adapter.savedLocationAdapter;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.mapsService;
import otamendi.urtzi.com.safeway.Utils.notificationService;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;

public class safeWayHome extends AppCompatActivity {

    private Toolbar toolbar;
    private List<myLocation> locationList;
    private RecyclerView locationsPager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng position = new LatLng(-1, -1);
    private Boolean deviceTracked= false;
    private myLocation selectedLocation= null;

    protected static final String TAG = "HOME SAFE WAY";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_way_home);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, onSuccessListener).addOnFailureListener(onFailureListener);

    }

    private void bindUI() {
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        locationsPager = (RecyclerView) findViewById(R.id.locationsPager);

    }


    private SimpleCallback<List<myLocation>> displayLocationsOnFragments = new SimpleCallback<List<myLocation>>() {
        @Override
        public void callback(List<myLocation> data) {

            locationList = data;
            configRecyclerView();
            Log.d(TAG, "Display Location---->");
        }
    };

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Display Location----> error" + data.toString());
        }
    };

    private void configRecyclerView() {
        savedLocationAdapter locationAdapter = new savedLocationAdapter(locationList, position, R.layout.view_saved_location, this, safeWayHome.this, listenerRecyclerView);
        locationsPager.setAdapter(locationAdapter);
        locationsPager.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(locationsPager);
    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_settings_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Do something
                Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.linkedUsersButton:
                Toast.makeText(getApplicationContext(), "LinkedUsers", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "+++++++ On Create Options Menu");
        getMenuInflater().inflate(R.menu.home_toolbar, menu);

        return true;
    }

    ////////////////////////////////////////////////////////////
    /////////////  onRecyclerViewClickListener /////////
    ////////////////////////////////////////////////////////////

    private onRecyclerViewClickListener listenerRecyclerView = new onRecyclerViewClickListener() {
        @Override
        public void onClick(View view, int position) {
            if(deviceTracked==false){
                myLocation location = locationList.get(position);
                selectedLocation=location;
                notificationService.createTrackinNotification(safeWayHome.this,location.getName(),safeWayHome.this);
                Toast.makeText(safeWayHome.this, R.string.starting, Toast.LENGTH_SHORT);
                requestLocationPermission();
            }
            else{
                stopLocationUpdates();
                deviceTracked=false;
            }
        }
    };

    /////////////////////////////////////////////////////////////
    ///////////////////// LOCATION PERMISSION ///////////////////
    /////////////////////////////////////////////////////////////
    public static final int PERMISSIONS_REQUEST_LOCATION = 12;

    @SuppressLint("MissingPermission")
    public void requestLocationPermission() {

        if (mapsService.checkLocationEnabled(this)) {
            Log.d(TAG, "Location permission is granted");

            enableGPS();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, "myLocation permission granted");
                    enableGPS();
                } else {
                    Log.d(TAG, "myLocation permission not granted");
                    DatabaseService.getSavedLocations(displayLocationsOnFragments, displayErrorPage);
                }
            }
        }
    }


    private OnSuccessListener<Location> onSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            Log.d(TAG, "onSuccessListener");
            if (location != null) {
                position = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, location.toString());
            }
            DatabaseService.getSavedLocations(displayLocationsOnFragments, displayErrorPage);
        }
    };

    private OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e(TAG, e.toString());

            DatabaseService.getSavedLocations(displayLocationsOnFragments, displayErrorPage);
        }

    };


    //////////////////////////
    ////// Enable gps
    /////////////////////////

    private final static int REQUEST_LOCATION=10;

    private void enableGPS() {
        try{
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d(TAG,"enableGPS googleApiClient conected");
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d(TAG,"enableGPS googleApiClient conexionsuspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            notStartTracking();
                            Log.d(TAG,"enableGPS googleApiClient error");
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d(TAG,"GPS enabled----------");
                            startTracking();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.d(TAG,"GPS not enabled, resolution needed----------");
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        safeWayHome.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d(TAG,"GPS not enabled, unavailable ----------");
                            notStartTracking();
                            Toast.makeText(safeWayHome.this, R.string.error_gps,Toast.LENGTH_LONG).show();
                            break;

                    }
                }

            });
        }catch (Exception e){
            notStartTracking();
            Toast.makeText(this, R.string.error_gps,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        Log.d(TAG,"GPS not enabled, resolution needed----------"+"Location enabled by user!");
                        // All required changes were successfully made
                        startTracking();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        notStartTracking();
                        Log.d(TAG,"GPS not enabled, resolution needed---------- Location not enabled, user cancelled.");
                        // The user was asked to change settings, but chose not to

                        Toast.makeText(safeWayHome.this, R.string.error_gps,Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }


    private void startTracking(){
        deviceTracked=true;
        createTracking();
        startLocationUpdates();
    }

    private void notStartTracking(){
        deviceTracked=true;
        selectedLocation=null;
    }


    //////////////////////
    /// Get location
    /////////////////////

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.d(TAG,"GstartLocationUpdates.");
        mFusedLocationClient.requestLocationUpdates(mapsService.getLocationRequest(),
                locationUpdatesCallback,
                null /* Looper */);
    }

    private LocationCallback locationUpdatesCallback= new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {

                Log.d(TAG,"-------------> Null");
                return;
            }
            for (Location location : locationResult.getLocations()) {

                Log.d(TAG,"------------->" + location.toString());
                trackingLocation tLocation = new trackingLocation(new Date(), location.getLatitude(), location.getLongitude());
                DatabaseService.saveTrackingLocation(tLocation, getBatteryLevel());
                Toast.makeText(safeWayHome.this,"lat: "+location.getLatitude()+" long: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates.");
        DatabaseService.stopTracking();
        mFusedLocationClient.removeLocationUpdates(new LocationCallback());
    }


    ///////////
    /// Database TRackin
    ////////////
    private void createTracking(){

        DatabaseService.createTracking(getBatteryLevel(),selectedLocation);
    }



    private int getBatteryLevel(){
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        return   bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }


}



