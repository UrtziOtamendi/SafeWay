package otamendi.urtzi.com.safeway.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;

public class DatabaseService {

    public static final String TAG="DatabaseService";

    public static void getUser(@NonNull final SimpleCallback<User> finishedCallback, @NonNull final SimpleCallback<String> errorCallback) {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(userF.getUid());
        User user= null;
        final List<User> tokenContainer = new ArrayList<>();
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorCallback.callback("An error occurred! Try again later");
                Log.d("sigmInAuth", "Configured-------> "+databaseError.toString() );
            }
        });

    }

    public static void saveUser(User user){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user.toMap());
        FCMService.AutoInitEnable();
    }


    private  static void userExists( String senderUID, @NonNull final SimpleCallback<Boolean> finishedCallback,  @NonNull final SimpleCallback<String> errorCallback ){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(senderUID);

        userData.addValueEventListener(new ValueEventListener() {
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
        userData.addValueEventListener(new ValueEventListener() {
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
}
