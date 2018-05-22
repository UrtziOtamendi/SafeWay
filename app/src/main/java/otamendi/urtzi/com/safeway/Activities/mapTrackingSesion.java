package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.Domain.trackingSesion;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class mapTrackingSesion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String sesion_id, users_uid;
    private Toolbar toolbar;
    private trackingSesion trackingSesion;
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
        setSupportActionBar(toolbar);
        configToolbar();
        getTrackingSesion();

    }

    private void bindUI() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTrackingSesion);
        mapFragment.getMapAsync(this);
        toolbar = (Toolbar) findViewById(R.id.mapTrackingSesion_toolbar);

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
    }

    private SimpleCallback<trackingSesion> getTrackingSesionCallback = new SimpleCallback<trackingSesion>() {
        @Override
        public void callback(trackingSesion data) {
            trackingSesion = data;
            displaySesion();

        }
    };

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(mapTrackingSesion.this, R.string.error, Toast.LENGTH_LONG).show();
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
        myLocation destination = trackingSesion.getDestination();
        LatLng destinationLatLng = destination.toLatLng();
        mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title(getResources().getString(R.string.destination))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));


    }

    private void drawPolyline() {
        Log.e(TAG,"drawing Poliline");
        PatternItem dot = new Dot();
        PatternItem gap = new Gap(2);
        List<PatternItem> dotted = Arrays.asList(gap, dot);

        List<trackingLocation> locations = trackingSesion.getLocationList();
        List<LatLng> locationsLatLng = new ArrayList<LatLng>();
        for (trackingLocation location : locations) {
            locationsLatLng.add(location.toLatLng());
        }
        Log.e(TAG,"drawing Poliline points --->" + locationsLatLng.size());

        Polyline trackedPath = mMap.addPolyline(new PolylineOptions()
                .addAll(locationsLatLng)
                .color(Color.BLUE)
                .width(30));


        trackedPath.setEndCap(new RoundCap());
        //trackedPath.setPattern(dotted);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( locations.get(locations.size()-1).toLatLng(), 15));

    }


}
