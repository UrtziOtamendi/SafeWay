package otamendi.urtzi.com.safeway.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.generateQR;
import otamendi.urtzi.com.safeway.Utils.mapsService;

public class HomeActivity extends Activity {



    private static final String TAG = "HOME";


    private EditText codeNameQR;
    private Button createQR, readQR, saveLocation;
    private LinearLayout imageLayout;
    private Button sendHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Get elements
        bindUI();
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = codeNameQR.getText().toString();
                if (name == null || name == "") {
                    Log.w(TAG, "---> Nombre nullo o vacio");
                } else {

                    setQR(name);
                }
            }
        });

        saveLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mapsService.checkLocationEnabled(HomeActivity.this)){
                    locationNameRequest();
                }else{
                    requestLocationPermission();
                }
            }
        });

        sendHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHome();
            }
        });

        readQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLinkedUser();
            }
        });
    }

    private void bindUI() {
        imageLayout = findViewById(R.id.imageLayout);
        codeNameQR = findViewById(R.id.nameQR);
        createQR = findViewById(R.id.createQR);
        readQR = findViewById(R.id.readQR);
        saveLocation = findViewById(R.id.addLocation);
        sendHome= findViewById(R.id.sendHome);
    }

    private void setQR(String linker) {
        Bitmap qr = generateQR.generateFromString(linker);
        ImageView imageQR = new ImageView(this);
        imageQR.setImageBitmap(qr);
        imageLayout.addView(imageQR);
    }


    private void getLinkedUser() {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData = mDatabase.child("linkedID").child(userF.getUid());

        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linkedID linkedID = dataSnapshot.getValue(linkedID.class);
                checkLinkAvailable(linkedID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "An error occurred! Try again later", Toast.LENGTH_LONG).show();
                Log.d(TAG, "GetLinked-------> " + databaseError.toString());
            }
        });


    }

    private void checkLinkAvailable(linkedID linker) {
        if (linker != null) {
            if (linker.getLink1() == null || linker.getLink2() == null) {
                sendToLink();
            } else {
                Toast.makeText(HomeActivity.this, "You have linked accounts already", Toast.LENGTH_LONG).show();
            }
        } else {
            sendToLink();
        }

    }

    private void sendToLink() {
        Intent intent = new Intent(HomeActivity.this, linkUsers.class);
        startActivity(intent);
    }

    private void sendSaveLocation() {
        Intent intent = new Intent(HomeActivity.this, saveLocation.class);
        startActivity(intent);
    }

    private void sendHome(){
        Intent intent = new Intent(HomeActivity.this, safeWayHome.class);
        startActivity(intent);
    }


    /////////////////////////////////////////////////////////////
    ///////////////////// LOCATION PERMISSION ///////////////////
    /////////////////////////////////////////////////////////////
    public static final int PERMISSIONS_REQUEST_LOCATION = 12;


    public void requestLocationPermission() {


        // Should we show an explanation to get the permission?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Explanation
            new AlertDialog.Builder(this)
                    .setTitle(R.string.saveLocation_GPS_Request_title)
                    .setMessage(R.string.saveLocation_GPS_Request_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {
            // Directly request
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "myLocation permission granted");
                        locationNameRequest();

                    }
                } else {
                    Log.d(TAG, "myLocation permission not granted");
                    locationNameRequest();
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////
    /////////////////////////// SAVE LOCATION ////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    private final static int PLACE_PICKER_REQUEST = 666;
    private String placeName;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private void locationNameRequest(){
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


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
                            Toast.makeText(HomeActivity.this,R.string.location_name_empty,Toast.LENGTH_SHORT ).show();
                        }else{
                            placeName=name;
                            createPlacePicker();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void createPlacePicker(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e){
            Log.e(TAG,"createPlacePicker--->"+e.toString());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    String address = String.format("%s", place.getAddress());
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    myLocation location=new myLocation(placeName,address,latitude,longitude);
                    DatabaseService.saveLocation(location);

            }
        }
    }



    }
