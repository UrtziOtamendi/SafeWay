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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.Domain.trackingLocation;
import otamendi.urtzi.com.safeway.Domain.trackingSesion;
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

    /// Get Users receptors ID, the ones that are going to receive users tracking information

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



    private static void moveTo(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.push().setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                           Log.e(TAG,"Errror moving");
                        } else {
                            fromPath.removeValue();
                            Log.e(TAG,"Success");

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void getLinkedUsers(@NonNull final SimpleCallback<List<String[]>> finishedCallback, @NonNull final SimpleCallback<String> errorCallback){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        final Query getLinkedUsers= mDatabase.child("users").child(userF.getUid()).child("linkedID");
        getLinkedUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String[]> linkedUserList = new ArrayList<String[]> ();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String[] aux= new String[2];
                    aux[0] = postSnapshot.getKey();
                    aux[1] = postSnapshot.getValue(String.class);

                    linkedUserList.add(aux);
                }
                finishedCallback.callback( linkedUserList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback(databaseError.toString());
                Log.e("getSaveLocations", "Error-------> "+databaseError.toString() );
            }
        });

    }

    public static void getTrackingList(String users_uid, final ComplexCallback<List<trackingSesion>,List<String>> finishedCallback, final SimpleCallback<String> errorCallback) {
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        final Query getTrackingList= mDatabase.child("trackingList").child(users_uid);
        getTrackingList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keyList= new ArrayList<String>();
                List<trackingSesion> trackingSesionList= new ArrayList<trackingSesion>();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Date ended= postSnapshot.child("ended").getValue(Date.class);
                    Date started= postSnapshot.child("started").getValue(Date.class);
                    myLocation destination= postSnapshot.child("destination").getValue(myLocation.class);
                    int battery= postSnapshot.child("battery").getValue(int.class);
                    trackingSesion sesion = new trackingSesion(battery, destination, started, ended);
                    keyList.add(postSnapshot.getKey());
                    trackingSesionList.add(sesion);

                }
                finishedCallback.callback(trackingSesionList, keyList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback(databaseError.toString());
                Log.e("getTrackingList", "Error-------> "+databaseError.toString() );
            }
        });

    }


    public static void getTrackingSesion(String users_uid, String tracking_id, final SimpleCallback<trackingSesion> finishedCallback, final SimpleCallback<String> errorCallback) {
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        final Query getTrackingSesion= mDatabase.child("trackingList").child(users_uid).child(tracking_id);
        getTrackingSesion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trackingSesion trackingSesion= dataSnapshot.getValue(trackingSesion.class);

                finishedCallback.callback(trackingSesion);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback(databaseError.toString());
                Log.e("getTrackingList", "Error-------> "+databaseError.toString() );
            }
        });

    }

    public static void isTracking(final String userUid, final SimpleCallback<Boolean> finishedCallback) {
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        final Query isTracking= mDatabase.child("tracking");
        isTracking.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isTracking = false;
                if(dataSnapshot.hasChild(userUid)){
                    isTracking=true;
                }
                finishedCallback.callback(isTracking);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("getSaveLocations", "Error-------> "+databaseError.toString() );
            }
        });
    }


    /////////////////////////////
    ///////////////////// WRITE
    ////////////////////////////



    /////////////
    ///////TRACKING
    ////////////////

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
        DatabaseReference usersTrackingList = mDatabase.child("trackingList").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Date now = new Date();
        usersTracking.child("ended").setValue(now);
        moveTo(usersTracking, usersTrackingList);
    }



    public static void saveTrackingLocation(trackingLocation location, int battery){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersTracking = mDatabase.child("tracking").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersTracking.child("locations").push().setValue(location);
        usersTracking.child("battery").setValue(battery);
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

    public static void linkReceptor(String receptorUID, String name){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userF.getUid()).child("linkedID").child(receptorUID).setValue(name);
    }



}
