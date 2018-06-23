package otamendi.urtzi.com.safeway.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class mapTrackingSesion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String sesion_id, users_uid;
    private Toolbar toolbar;
    private LatLng destination;
    private SeekBar timeProgress;
    private List<trackingLocation> locations;
    private Polyline trackedPath;
    private  List<LatLng> locationList;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    protected static final String TAG = "MAP TRACKING SESSION ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking_sesion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        bindUI();
        Bundle extras = getIntent().getExtras();
        sesion_id = extras.getString("sesion_id");
        users_uid = extras.getString("users_uid");
        Double lat =extras.getDouble("lat");
        Double lon =extras.getDouble("lon");
        destination= new LatLng(lat,lon);
        setSupportActionBar(toolbar);
        configToolbar();
        getTrackingSesion();

    }

    private void bindUI() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTrackingSesion);
        mapFragment.getMapAsync(this);
        toolbar = findViewById(R.id.mapTrackingSesion_toolbar);
        timeProgress= findViewById(R.id.timeProgress);

    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mapTrackingSesion.this, usersTrackingList.class);
                intent.putExtra("users_uid", users_uid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

    }

    ///////// READ SESION
    private void getTrackingSesion() {
        DatabaseService.getTrackingSesion(users_uid, sesion_id, getTrackingSesionCallback, displayErrorPage);
        DatabaseService.getTrackingSesionEmergencyCallback(users_uid , sesion_id, getTrackingSesionEmergencyCallback,displayErrorPage);
    }

    private SimpleCallback<List<trackingLocation>> getTrackingSesionCallback = new SimpleCallback<List<trackingLocation>>() {
        @Override
        public void callback(List<trackingLocation> data) {
            locations = data;
            locationList = new ArrayList<LatLng>();
            for (trackingLocation location : locations) {
                locationList.add(location.toLatLng());
            }
            displaySesion();
            if(locations.size()>0){
                configSeekBar();
            }

        }
    };

    private SimpleCallback<List<trackingLocation>> getTrackingSesionEmergencyCallback = new SimpleCallback<List<trackingLocation>>() {
        @Override
        public void callback(List<trackingLocation> data) {
            if(data==null) return;
            if(data.size()==0) return;
            for (trackingLocation location : data) {
                displayEmergency(location);
            }
        }
    };

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toasty.error(mapTrackingSesion.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();
            Log.e(TAG, "Display Location----> error" + data.toString());
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void displaySesion() {
        displayGoal();
        drawPolyline();

    }

    private void displayGoal() {
        Log.e(TAG,"goal display");
        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title(getResources().getString(R.string.destination))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));


    }

    private void displayEmergency(trackingLocation location) {
        Log.e(TAG,"displayEmergency ");
        if(mMap!=null){
            mMap.addMarker(new MarkerOptions()
                    .position(location.toLatLng())
                    .title(df.format(location.getDate()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }


    @SuppressLint("ResourceType")
    private void drawPolyline() {
        Log.e(TAG,"drawing Poliline");
        PatternItem dot = new Dot();
        PatternItem gap = new Gap(2);
        List<PatternItem> dotted = Arrays.asList(gap, dot);

        Log.e(TAG,"drawing Poliline points --->" + locationList.size());

         trackedPath = mMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(30));


        trackedPath.setEndCap(new RoundCap());
        //trackedPath.setPattern(dotted);
        if(locationList.size()>0){
            mMap.addMarker(new MarkerOptions().position(locationList.get(0))
                                              .title((getResources().getString(R.string.started)))
                                              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( locations.get(locations.size()-1).toLatLng(), 15));

        }

    }


    //Config Seek Bar

    private void configSeekBar(){
        Log.e(TAG,"CofigSeekBar");
        timeProgress.setMax(locationList.size()-1);
        timeProgress.setProgress(0);
        toolbar.setTitle("   "+df.format(locations.get(0).getDate()));
        timeProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                trackedPath.setPoints(locationList.subList(0,i));

                toolbar.setTitle("   "+df.format(locations.get(i).getDate()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG,"Seek moving");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }



}
