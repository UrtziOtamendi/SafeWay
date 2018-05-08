package otamendi.urtzi.com.safeway.Utils;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.ArrayList;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;

/**
 * Created by urtzi on 07/05/2018.
 */

public  class signInAuth {


    public static boolean SignedIn(){

        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        return (userF!=null) ? true : false;
    }

    public static boolean Configured(){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(userF.getUid());
        User user= null;
        final List<User> tokenContainer = new ArrayList<>();
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                tokenContainer.add(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("sigmInAuth", "Configured-------> "+databaseError.toString() );
            }
        });
        if(tokenContainer.size()!=0)
            user=tokenContainer.get(0);


        return (user.getEmergencyNumber()!=null) ? true : false;
    }

    public static User getUser() {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("users").child(userF.getUid());
        User user= null;
        final List<User> tokenContainer = new ArrayList<>();
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                tokenContainer.add(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("sigmInAuth", "Configured-------> "+databaseError.toString() );
            }
        });
        if(tokenContainer.size()!=0)
            user=tokenContainer.get(0);


        return user;

    }

    public static void saveUser(User user){
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user.toMap());
    }
}
