package otamendi.urtzi.com.safeway.Activities;


import android.content.DialogInterface;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.mapsService;


public class saveLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton saveLocation;
    private LocationManager locationManager;
    private static final String TAG="Save myLocation";
    private LatLng selectedLocation=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bindUI();
        saveLocation.setOnClickListener(onClickListener);
    }


    private void bindUI() {
        saveLocation = (FloatingActionButton) findViewById(R.id.saveLocationButton);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(onMapClickListener);
        try{
            if(mapsService.checkLocationEnabled(this)){
                Log.d(TAG,"myLocation permission -->" + true);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                //The reason of setting looper parameter, is because we only want to get the device's location once.
                locationManager.requestSingleUpdate(mapsService.saveBatteryLifeCriteria(),locationListener, null );

            }else {
                Log.d(TAG,"myLocation permission -->" + false);
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }catch (SecurityException e){
            Log.e(TAG,"Error getting current location -->" + e.toString() );
        }

    }


    //Map on Click function, to save the selected location
    GoogleMap.OnMapClickListener onMapClickListener= new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG,"OnMapClickListener ----> "+ latLng.toString());
            selectedLocation= latLng;
        }
    };


    //Floating button on click listener
    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                if(mMap==null){
                    Log.d(TAG,"-----> Map not loaded");
                    Toast.makeText(saveLocation.this,R.string.map_not_loaded,Toast.LENGTH_LONG ).show();
                }else{
                    if(selectedLocation==null){
                        Log.d(TAG,"-----> Point not selected");
                        Toast.makeText(saveLocation.this,R.string.point_not_selected,Toast.LENGTH_LONG ).show();
                    }else{
                        locationNameRequest();
                    }
                }
            }catch(Exception e){
                Log.e(TAG,"-----> Error saving point" + e.toString());
                Toast.makeText(saveLocation.this,R.string.error_saving_location,Toast.LENGTH_LONG ).show();
            }

        }
    };


    private void locationNameRequest(){
        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        // Explanation
        new AlertDialog.Builder(this)
                .setTitle(R.string.set_new_location_title)
                .setMessage(R.string.set_new_location_message)
                .setView(nameInput)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = nameInput.getText().toString();
                        if(name==null || name.equals("")){
                            Log.d(TAG,"-----> Name empty");
                            Toast.makeText(saveLocation.this,R.string.location_name_empty,Toast.LENGTH_SHORT ).show();
                        }else{
                            saveLocationOnFirebase(name);
                        }
                    }
                })
                .create()
                .show();
    }



    private void saveLocationOnFirebase(String name){
        try{

            Geocoder geocoder = new Geocoder(saveLocation.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1);
            String address = mapsService.bindAddress(addresses);
            myLocation location = new myLocation(name,address, selectedLocation.latitude, selectedLocation.longitude);
            DatabaseService.saveLocation(location);
            sendHome();
        }catch (Exception e){
            Log.e(TAG,"-----> Error saving location on firebase" + e.toString());
            Toast.makeText(saveLocation.this,R.string.error_saving_location,Toast.LENGTH_LONG ).show();
        }
    }


    //Final listener, to get current location once.
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try{
                Log.d(TAG,"Current myLocation -->" + location.toString());
                LatLng current = new LatLng(location.getLatitude(), location.getLatitude());
                mMap.addMarker(new MarkerOptions().position(current).title(getResources().getString(R.string.saveLocation_current_title)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            }catch (Exception e){
                Log.e(TAG,"Current myLocation --> Error" + e.toString());
                LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };


    private void sendHome() {
        Intent intent = new Intent(saveLocation.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
