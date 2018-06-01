package otamendi.urtzi.com.safeway.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import otamendi.urtzi.com.safeway.Adapter.savedLocationAdapter;
import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.MainApplication;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.location.locationService;
import otamendi.urtzi.com.safeway.Utils.mapsService;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;
import otamendi.urtzi.com.safeway.Utils.permissionModule;
import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

public class safeWayHome extends AppCompatActivity implements OnMapReadyCallback {


    private static Toolbar toolbar;
    private List<myLocation> locationList= new ArrayList<myLocation>();
    private RecyclerView locationsPager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng position = new LatLng(-1, -1);
    private FloatingActionButton edit_fab, delete_fab, save_fab, stop_fab;
    private myLocation selectedLocation = null;
    private LinearLayoutManager manager;
    private savedLocationAdapter locationAdapter;
    private Button select_location;
    private String placeName;
    private static GoogleMap mMap;
    private MapFragment mapFragment;
    private FragmentTransaction fragmentTransaction;
    private static List<LatLng> trackedLocations = new ArrayList<>();
    private static Polyline trackedPath;
    private locationService mService=null;
    private  MediaPlayer mediaPlayer;
    private ConstraintLayout empty_locationsPager;
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

        //Permission request.
        ActivityCompat.requestPermissions(this, permissionModule.requestAllPermisions(), permissionModule.PERMISSIONS_MULTIPLE);

        if(sharedPreferences.readBoolean(this,"alarmOn")) alarmAlertDialog();
        DatabaseService.getSavedLocations(displayLocationsOnFragments, displayErrorPage);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, onSuccessListener).addOnFailureListener(onFailureListener);

        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "select location");
                final EditText nameInput = new EditText(safeWayHome.this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                new AlertDialog.Builder(safeWayHome.this)
                        .setTitle(R.string.set_new_location_title)
                        .setMessage(R.string.set_new_location_message)
                        .setView(nameInput)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    String name = nameInput.getText().toString();
                                    if (name == null || name.equals("")) {
                                        Log.d(TAG, "-----> Name empty");
                                        Toast.makeText(safeWayHome.this, R.string.location_name_empty, Toast.LENGTH_SHORT).show();
                                    } else {
                                        placeName = name;
                                        createPlacePicker();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "eerror" + e.toString());
                                    Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();

                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        });
        delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationList.size()<1) return;
                final int position = manager.findFirstVisibleItemPosition() % locationList.size();
                new AlertDialog.Builder(safeWayHome.this)
                        .setTitle(R.string.delete_location_title)
                        .setMessage(R.string.delete_location_mesage)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    DatabaseService.deleteSavedLocation(locationList.get(position).getName());
                                    locationAdapter.deleteItem(position);

                                } catch (Exception e) {
                                    Log.e(TAG, "eerror" + e.toString());
                                    Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();
                                }
                                if(!(locationList.size()>0)){
                                    show_fabs();
                                    manageRecyclerViewVisibility();
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        });

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationList.size()<1) return;
                final EditText nameInput = new EditText(safeWayHome.this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                final int position = manager.findFirstVisibleItemPosition() % locationList.size();
                Log.e(TAG, "Visible item position edit " + position);
                new AlertDialog.Builder(safeWayHome.this)
                        .setTitle(R.string.edit_location_title)
                        .setMessage(R.string.edit_location_mesage)
                        .setView(nameInput)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    String name = nameInput.getText().toString();
                                    if (name == null || name.equals("")) {
                                        Log.d(TAG, "-----> Name empty");
                                        Toast.makeText(safeWayHome.this, R.string.location_name_empty, Toast.LENGTH_SHORT).show();
                                    } else {
                                        String oldName = locationList.get(position).getName();
                                        locationAdapter.updateItem(position, name);
                                        myLocation location = locationList.get(position);
                                        manager.scrollToPosition(position+1);
                                        DatabaseService.updateSavedLocation(location, oldName);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "eerror" + e.toString());
                                    Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();

                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        });

        stop_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Stop clicked");
                if (sharedPreferences.readBoolean(safeWayHome.this, "tracking") == true) {
                    new AlertDialog.Builder(safeWayHome.this)
                            .setTitle(R.string.stop_tracking_title)
                            .setMessage(R.string.stop_tracking_mesage)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {
                                        stopLocationUpdates();
                                        hideTracking();

                                    } catch (Exception e) {
                                        Log.e(TAG, "eerror" + e.toString());
                                        Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create()
                            .show();
                }
            }
        });

        select_location.setOnClickListener(selectLocationListener);
    }


    @Override
    protected void onDestroy() {
        if(binded){

            unbindService(mConnection);
            binded=false;
        }
        super.onDestroy();
    }

    private void bindUI() {
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        locationsPager = (RecyclerView) findViewById(R.id.locationsPager);
        delete_fab = findViewById(R.id.delete_fab);
        edit_fab = findViewById(R.id.edit_fab);
        save_fab = findViewById(R.id.save_fab);
        stop_fab = findViewById(R.id.stop_fab);
        select_location = findViewById(R.id.select_location);
        empty_locationsPager= findViewById(R.id.empty_locationsPager);
        if (sharedPreferences.readBoolean(safeWayHome.this, "tracking") == true) {
            displayTracking();
        } else {
            hideTracking();
        }

        mediaPlayer= MediaPlayer.create(safeWayHome.this,R.raw.alarm);

    }


    ////////////////////////////////////
    ////////////////// GET LOCATION
    ///////////////////////////////////
    private final static int PLACE_PICKER_REQUEST_LOCATION=99;
    private View.OnClickListener selectLocationListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try{
                startActivityForResult(builder.build(safeWayHome.this), PLACE_PICKER_REQUEST_LOCATION);
            }catch (Exception e){
                Log.e(TAG,"createPlacePicker--->"+e.toString());
            }
        }
    };
    private SimpleCallback<List<myLocation>> displayLocationsOnFragments = new SimpleCallback<List<myLocation>>() {
        @Override
        public void callback(List<myLocation> data) {

            if(data.size()>0) {
                locationList = (data);
                Collections.reverse(locationList);
                configRecyclerView();
                show_fabs();
            }else{
                show_fabs();
                manageRecyclerViewVisibility();
            }
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
        locationsPager.setVisibility(View.VISIBLE);
        empty_locationsPager.setVisibility(View.INVISIBLE);
        locationAdapter = new savedLocationAdapter(locationList, position, R.layout.view_saved_location, safeWayHome.this, listenerRecyclerView);
        locationsPager.setAdapter(locationAdapter);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        locationsPager.setLayoutManager(manager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(locationsPager);
        manager.scrollToPosition(Integer.MAX_VALUE / 2);

    }
    private void manageRecyclerViewVisibility() {
        if(locationList.size()>0){
            locationsPager.setVisibility(View.VISIBLE);
            empty_locationsPager.setVisibility(View.INVISIBLE);
        }else{

            locationsPager.setVisibility(View.INVISIBLE);
            empty_locationsPager.setVisibility(View.VISIBLE);
        }

    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_settings_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(safeWayHome.this, settingsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.linkedUsersButton:
                Intent intent = new Intent(safeWayHome.this, linkedUsersList.class);
                startActivity(intent);

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

    private onRecyclerViewClickListener listenerRecyclerView;

    {
        listenerRecyclerView = new onRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!sharedPreferences.readBoolean(safeWayHome.this, "tracking")) {
                    myLocation location = locationList.get(position % locationList.size());
                    selectedLocation = location;
                    requestLocationPermission();
                }
            }
        };
    }

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

        }
    };

    private OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e(TAG, e.toString());

        }

    };


    //////////////////////////
    ////// Enable gps
    /////////////////////////

    private final static int REQUEST_LOCATION = 10;

    private void enableGPS() {
        try {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d(TAG, "enableGPS googleApiClient conected");
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d(TAG, "enableGPS googleApiClient conexionsuspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            notStartTracking();
                            Log.d(TAG, "enableGPS googleApiClient error");
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
                            Log.d(TAG, "GPS enabled----------");
                            startTracking();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.d(TAG, "GPS not enabled, resolution needed----------");
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
                            Log.d(TAG, "GPS not enabled, unavailable ----------");
                            notStartTracking();
                            Toast.makeText(safeWayHome.this, R.string.error_gps, Toast.LENGTH_LONG).show();
                            break;

                    }
                }

            });
        } catch (Exception e) {
            notStartTracking();
            Toast.makeText(this, R.string.error_gps, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.d(TAG, "GPS not enabled, resolution needed----------" + "Location enabled by user!");
                        // All required changes were successfully made
                        startTracking();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        notStartTracking();
                        Log.d(TAG, "GPS not enabled, resolution needed---------- Location not enabled, user cancelled.");
                        // The user was asked to change settings, but chose not to

                        Toast.makeText(safeWayHome.this, R.string.error_gps, Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;

            case PLACE_PICKER_REQUEST:
                try{
                    Place place = PlacePicker.getPlace(this, data);
                    String address = String.format("%s", place.getAddress());
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    myLocation location = new myLocation(placeName, address, latitude, longitude);
                    if(locationList.size()==0){
                        locationsPager.setOnFlingListener(null);
                        locationList.add(location);
                        configRecyclerView();
                        manageRecyclerViewVisibility();
                        show_fabs();
                    }else{
                        locationAdapter.addItem(location);
                    }
                    DatabaseService.saveLocation(location);

                }catch(Exception e){
                    Log.e(TAG,e.toString());
                    Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                }

                break;

            case PLACE_PICKER_REQUEST_LOCATION:
                try {
                    Place place2 = PlacePicker.getPlace(this, data);
                    String address2 = String.format("%s", place2.getAddress());
                    double latitude2 = place2.getLatLng().latitude;
                    double longitude2 = place2.getLatLng().longitude;
                    myLocation location2 = new myLocation("", address2, latitude2, longitude2);
                    selectedLocation = location2;
                    requestLocationPermission();
                }catch(Exception e){
                    Log.e(TAG,e.toString());
                    Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                }
                break;


        }
    }


    private void startTracking() {
        createTracking();
        sharedPreferences.writeBoolean(this, "tracking", true);
        sharedPreferences.writeString(this, "lat", Double.toString(selectedLocation.getLat()));
        sharedPreferences.writeString(this, "long", Double.toString(selectedLocation.getLon()));
        Intent service = new Intent(this, locationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
        bindService( new Intent(this, locationService.class),mConnection, Context.BIND_AUTO_CREATE);
        binded=true;
        displayTracking();


    }

    private void notStartTracking() {
        selectedLocation = null;
    }


    //////////////////////
    /// Get location
    /////////////////////


    private boolean binded=false;
    private void stopLocationUpdates() {
        if(binded){

            unbindService(mConnection);
            binded=false;
        }
        sharedPreferences.writeBoolean(this, "tracking", false);
        stopService(new Intent(this, locationService.class));
        hideTracking();

    }


    ///////////
    /// Database TRackin
    ////////////
    private void createTracking() {

        DatabaseService.createTracking(getBatteryLevel(), selectedLocation);
    }

    private int getBatteryLevel() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }


    private void printInfo() {

        String text = "tracking  :" + sharedPreferences.readBoolean(this, "tracking") + " \n "
                + " ";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

    }

    ///////
    ///Location picker
    /////////

    private static final int PLACE_PICKER_REQUEST = 11;

    private void createPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "createPlacePicker--->" + e.toString());
        }
    }


////START STOP

    private void displayTracking() {
        hide_fabs();
        stop_fab.show();
        startMarker=false;

        toolbar.setBackgroundResource(R.color.colorPrimary);

        select_location.setVisibility(View.INVISIBLE);
        mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mapFragment);
        fragmentTransaction.commit();

    }

    private void hide_fabs(){
        edit_fab.hide();
        save_fab.hide();
        delete_fab.hide();
    }

    private void show_fabs(){
        if (sharedPreferences.readBoolean(safeWayHome.this, "tracking") == true) return;
        if(locationList.size()>0){
            delete_fab.show();
            edit_fab.show();
        }else{
            delete_fab.hide();
            edit_fab.hide();
        }
        save_fab.show();
    }


    private void hideTracking() {

        show_fabs();
        stop_fab.hide();
        toolbar.setTitle("");
        toolbar.setBackgroundResource(R.color.transparent);
        manageRecyclerViewVisibility();
        select_location.setVisibility(View.VISIBLE);
        startMarker=false;
        if (fragmentTransaction != null && mapFragment != null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(mapFragment).commitAllowingStateLoss();
            mapFragment = null;
        }
    }

    public  static void changeDistance(Float distance) {
        if (trackedPath != null) {
            trackedPath.setPoints(trackedLocations);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trackedLocations.get(trackedLocations.size() - 1), 20));
        }
        if(toolbar!=null){
            toolbar.setTitle(distance + " m");
        }
    }

    private static boolean startMarker = false;
    public static void addLocation(LatLng position) {
        if (!startMarker && mMap!=null) {
            if (trackedLocations.size() > 0) {
                Log.e(TAG,"add start marker");
                mMap.addMarker(new MarkerOptions().position(trackedLocations.get(0))
                        .title(MainApplication.getAppContext().getResources().getString(R.string.started))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                startMarker = true;
            }
        }
        trackedLocations.add(position);
        Log.e(TAG, "location added");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e(TAG, "MAp ready");

        Double lat = Double.parseDouble(sharedPreferences.readString(this, "lat"));
        Double lon = Double.parseDouble(sharedPreferences.readString(this, "long"));
        LatLng destination = new LatLng(lat, lon);
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title(getResources().getString(R.string.destination))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        trackedPath = mMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(30));

        trackedPath.setEndCap(new RoundCap());


    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((locationService.LocalBinder) iBinder).getInstance();
            mService.registerHandler(serviceHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService mService = null;
        }
    };


    private Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG,"arrived to goal");
            if(binded){
                unbindService(mConnection);
                binded=false;
            }
            hideTracking();
        }
    };

    private static String pass="";
    public  void alarmAlertDialog(){
        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(this)
                .setTitle(R.string.alarm_dialog_title)
                .setMessage(R.string.alarm_dialog_message)
                .setView(nameInput)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            pass = nameInput.getText().toString();
                            if (pass == null || pass.equals("")) {
                                Toast.makeText(safeWayHome.this, R.string.password_empty, Toast.LENGTH_SHORT).show();
                                alarmAlertDialog();
                            } else {
                                DatabaseService.getUser(alarmCallback,new SimpleCallback<String>() {
                                    @Override
                                    public void callback(String data) {
                                        Log.e(TAG, data);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "eerror" + e.toString());
                            Toast.makeText(safeWayHome.this, R.string.error, Toast.LENGTH_LONG).show();

                        }

                    }
                })
                .create()
                .show();
    }
    private  SimpleCallback<User> alarmCallback = new SimpleCallback<User>() {
        @SuppressLint("MissingPermission")
        @Override
        public void callback(User data) {
            if(data.getPassword().compareTo(pass)==0){
                sharedPreferences.writeBoolean(safeWayHome.this,"alarmOn",false);
                FCMService.stopMediaPlayer();
            }else{
                alarmAlertDialog();
                Toast.makeText(safeWayHome.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
            }

        }
    };
}



