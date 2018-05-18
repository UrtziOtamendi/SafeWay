package otamendi.urtzi.com.safeway.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;

public class DatabaseService {

    public static final String TAG="DatabaseService";

    public static void getUser(@NonNull final SimpleCallback<User> finishedCallback, @NonNull final SimpleCallback<String> errorCallback) {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(userF.getUid());
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                if(user!=null) Log.d(TAG, user.toString());
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback("An error occurred! Try again later");
                Log.d("sigmInAuth", "Configured-------> "+databaseError.toString() );
            }
        });

    }

    public static void getUsersLinks(@NonNull final SimpleCallback<linkedID> finishedCallback, @NonNull final SimpleCallback<String> errorCallback) {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(userF.getUid()).child("receptorsID");
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                linkedID linkers= dataSnapshot.getValue(linkedID.class);
                if(linkers!=null) Log.d(TAG, linkers.toString());
                finishedCallback.callback(linkers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback("An error occurred! Try again later");
                Log.d("sigmInAuth", "Configured-------> "+databaseError.toString() );
            }
        });

    }


    // Ordenatu erabilitako aldien arabera
    public static void getSavedLocations(@NonNull final SimpleCallback<List<myLocation>> finishedCallback, @NonNull final SimpleCallback<String> errorCallback){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        final Query locationData= mDatabase.child("locations").child(userF.getUid()).orderByChild("usage");
        locationData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long locations = dataSnapshot.getChildrenCount();
                Log.d("getSaveLocations"," Locations retrieved, exactly -->"+locations);
                List<myLocation> locationList= new ArrayList<myLocation>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    myLocation post = postSnapshot.getValue(myLocation.class);
                    locationList.add(post);
                }
                finishedCallback.callback( locationList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback(databaseError.toString());
                Log.e("getSaveLocations", "Error-------> "+databaseError.toString() );
            }
        });

    }

    public static void saveUser(User user){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
    }
    public static void saveLinks(linkedID links){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("receptorsID").setValue(links);
    }



    public static void saveLocation(myLocation location){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("locations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(location.getName()).setValue(location);
    }


    public  static void userExists( String senderUID, @NonNull final SimpleCallback<Boolean> finishedCallback,  @NonNull final SimpleCallback<String> errorCallback ){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(senderUID);

        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    User sender = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "Sernder------> IS null ?" + (sender == null));
                    if (sender == null) {
                        errorCallback.callback("Wrong QR code");
                    } else {
                        finishedCallback.callback(true);
                    }

                } catch (NullPointerException e) {
                    errorCallback.callback("An error occurred! Try again later");
                    Log.e(TAG, "userExists-------> " + e.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback("An error occurred! Try again later");
                Log.d(TAG, "userExists-------> "+databaseError.toString() );
            }
        });
    }

    public static void Configured(@NonNull final SimpleCallback<User> finishedCallback,  @NonNull final SimpleCallback<String> errorCallback) {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData = mDatabase.child("users").child(userF.getUid());
        Log.d("AuthService", "-----------> " + userData.toString());
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback("An error occurred! Try again later");
                Log.d("sigmInAuth", "Configured-------> " + databaseError.toString());
            }
        });
    }





    public static void createTracking( int battery, myLocation goal){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersTracking = mDatabase.child("tracking").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //add one usage
        mDatabase.child("locations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(goal.getName()).child("usage").setValue(goal.getUsage()+1);
        Date now = new Date();
        usersTracking.removeValue();
        usersTracking.child("started").setValue(now);
        usersTracking.child("destination").setValue(goal);
        usersTracking.child("battery").setValue(battery);
    }

    public static void stopTracking( ){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersTracking = mDatabase.child("tracking").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Date now = new Date();
        usersTracking.child("ended").setValue(now);
    }



    public static void saveTrackingLocation(trackingLocation location, int battery){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersTracking = mDatabase.child("tracking").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersTracking.child("locations").push().setValue(location);
        usersTracking.child("battery").setValue(battery);
    }
}
