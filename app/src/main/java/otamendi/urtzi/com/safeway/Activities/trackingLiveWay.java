package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.ComplexCallback;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.mapsService;

public class trackingLiveWay extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    protected static final String TAG = "LIVE TRACKING SESSION ";
    private boolean emergency=false;
    private String users_uid;
    private Toolbar toolbar;
    private TextView startText, destinationText, batteryText, lastText;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private Date start;
    private FloatingActionButton emergencyCallFab,emergencyAlarmFab;
    private myLocation destination;
    private List<trackingLocation> locationList = new ArrayList<trackingLocation>();
    private List<LatLng> locationListLatLng = new ArrayList<LatLng>();
    private boolean startMarked=false;
    private boolean mapDisplayed=false;
    private Polyline trackedPath;
    private SeekBar timeProgress;
    private Switch autoProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_live_way);
        bindUI();
        configToolbar();
        Bundle extras = getIntent().getExtras();
        users_uid = extras.getString("users_uid");
        DatabaseService.getLiveTracking(users_uid,getLiveTrackingCallback, displayErrorPage);
        DatabaseService.getLiveTrackingBattery(users_uid,getLiveTrackingBatteryCallback, displayErrorPage );
        //DatabaseService.getLiveTrackingOldPoints(users_uid,getLiveTrackingOldPointsCallback, displayErrorPage );
        DatabaseService.getLiveTrackingNewPoints(users_uid,getLiveTrackingNewPointsCallback, displayErrorPage);
        DatabaseService.getLiveTrackingNewEmergencyPoints(users_uid, getLiveTrackingNewEmergencyPointsCallback, displayErrorPage);
        DatabaseService.getLiveTrackingNewEmergency(users_uid, getLiveTrackingNewEmergency, displayErrorPage);
        autoProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d(TAG,"checked on");
                    getSupportActionBar().setTitle("");
                    timeProgress.setVisibility(View.INVISIBLE);
                    drawNewPolyline();
                }else{
                    Log.d(TAG,"checked off");
                    timeProgress.setVisibility(View.VISIBLE);
                    configSeekBar();
                }
            }
        });
        emergencyCallFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FCMService.sendMessage(users_uid, "emergencyCall");
                emergencyCallFab.setVisibility(View.INVISIBLE);

            }
        });
        emergencyAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FCMService.sendMessage(users_uid, "alarmOn");
                emergencyAlarmFab.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!mapDisplayed) displaySesion();
    }

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            if(data.compareTo("stoped")==0){
               // Toast.makeText(trackingLiveWay.this, R.string.have_arrived, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(trackingLiveWay.this, linkedUsersList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
           // Toast.makeText(trackingLiveWay.this, R.string.error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Display Location----> error" + data.toString());
        }
    };

    private void bindUI(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapLiveTracking);
        mapFragment.getMapAsync(this);
        toolbar = findViewById(R.id.mapLiveTracking_toolbar);
        startText = findViewById(R.id.startText);
        destinationText = findViewById(R.id.destinationText);
        batteryText = findViewById(R.id.batteryText);
        lastText = findViewById(R.id.lastText);
        timeProgress= findViewById(R.id.seekBarTime);
        autoProgress=findViewById(R.id.autoSwitch);
        emergencyCallFab= findViewById(R.id.emergency_call_fab);
        emergencyAlarmFab = findViewById(R.id.emergency_alarm_fab);

    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trackingLiveWay.this, linkedUsersList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
    }

    private ComplexCallback<Date,myLocation> getLiveTrackingCallback = new ComplexCallback<Date,myLocation>() {
        @Override
        public void callback(Date date, myLocation goal) {
            if(date!=null && goal!=null){
                Log.e(TAG,"getLiveTrackingCallback");
                start = date;
                destination= goal;
                displayGoal();
            }
            else{
                Log.e(TAG,"getLiveTRacking----> null");
            }
        }
    };

    private SimpleCallback<Integer> getLiveTrackingBatteryCallback= new SimpleCallback<Integer>() {
        @Override
        public void callback(Integer data) {
            if(data!=null){
                batteryText.setText(data+ " %");
            }
        }
    };


    private SimpleCallback<Boolean> getLiveTrackingNewEmergency = new SimpleCallback<Boolean>() {
        @Override
        public void callback(Boolean data) {
            Log.e(TAG,"getLiveTrackingNewEmergency "+ data);
            if(data!=null ){
                emergency=data;
                if(data){
                    show_fab();
                }else{
                    hide_fab();
                }
            }
        }
    };

    private SimpleCallback<trackingLocation> getLiveTrackingNewEmergencyPointsCallback = new SimpleCallback<trackingLocation>() {
        @Override
        public void callback(trackingLocation data) {
            if(data!=null ){
                displayEmergency(data);
            }
        }
    };

    private SimpleCallback<trackingLocation> getLiveTrackingNewPointsCallback = new SimpleCallback<trackingLocation>() {
        @Override
        public void callback(trackingLocation data) {
            if(data!=null){

                setLastText(data);
                locationList.add(data);
                locationListLatLng.add(data.toLatLng());
                int aux =locationListLatLng.size();
                if(aux>=10){
                 LatLng p1= locationListLatLng.get(aux-1);
                 LatLng p2= locationListLatLng.get(aux-10);
                 double distance=   mapsService.distanceToGoalMeters(p1,p2);
                 if(distance<20){
                     show_fab();
                 }else{
                     if(!emergency)
                     hide_fab();
                 }
                }
                if(!autoProgress.isChecked()){
                    configSeekBar();
                }else{
                    Log.e(TAG,"getLiveTrackingNewPointsCallback-----> "+ locationList.size());
                    drawNewPolyline();
                }
            }
        }
    };


    private void displayGoal() {
        Log.e(TAG,"goal display");
        if(mMap!=null){
            mapDisplayed=true;
            destinationText.setText(destination.getAddress());
            startText.setText(df.format(start));
            mMap.addMarker(new MarkerOptions()
                    .position(destination.toLatLng())
                    .title(getResources().getString(R.string.destination))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        }
    }

    private void displayEmergency(trackingLocation location) {
        Log.e(TAG,"displayEmergency ");
        if(mMap!=null){
            mapDisplayed=true;
            mMap.addMarker(new MarkerOptions()
                    .position(location.toLatLng())
                    .title(df.format(location.getDate()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            moveCamera(location.toLatLng());
        }
    }

    private void displaySesion(){
        if(mMap==null) return;
        Log.e(TAG,"displaySesion ");
        trackedPath = mMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(30));
        trackedPath.setEndCap(new RoundCap());
        if(locationListLatLng.size()>0){
            mMap.addMarker(new MarkerOptions().position(locationListLatLng.get(0))
                    .title((getResources().getString(R.string.started)))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            startMarked=true;
            moveCamera(locationListLatLng.get(locationListLatLng.size()-1));
            setLastText(locationList.get(locationList.size()-1));
        }


    }

    private void drawNewPolyline(){
        if(!startMarked){
            mMap.addMarker(new MarkerOptions().position(locationListLatLng.get(0))
                    .title((getResources().getString(R.string.started)))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            startMarked=true;
        }
        trackedPath.setPoints(locationListLatLng);

    }

    private void moveCamera(LatLng location){
        if(mMap!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    private void setLastText(trackingLocation last){
        String locationString= df.format(last.getDate())+ " Lat: "+last.getLat() + "º Long: " + last.getLon()+"º";
        lastText.setText(locationString);
    }

    private void configSeekBar(){
        Log.e(TAG,"CofigSeekBar");
        timeProgress.setMax(locationList.size()-1);
       //timeProgress.setProgress(locationList.size()-1);
       // toolbar.setTitle("   "+df.format(locationList.get(0).getDate()));
        timeProgress.setOnSeekBarChangeListener(timeProgressListener);

    }

      private SeekBar.OnSeekBarChangeListener timeProgressListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            trackedPath.setPoints(locationListLatLng.subList(0,i));
            toolbar.setTitle("   "+df.format(locationList.get(i).getDate()));
            moveCamera(locationListLatLng.get(i));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG,"Seek moving");

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // emergency call


    private void show_fab(){
        emergencyAlarmFab.setVisibility(View.VISIBLE);
        emergencyCallFab.setVisibility(View.VISIBLE);
    }

    private void hide_fab(){
        emergencyAlarmFab.setVisibility(View.INVISIBLE);
        emergencyCallFab.setVisibility(View.INVISIBLE);
    }
}
