package otamendi.urtzi.com.safeway.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

import otamendi.urtzi.com.safeway.Activities.scannerQR;
import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;

/**
 * Created by urtzi on 07/05/2018.
 */

public  class AuthService {


    public static final String TAG="AuthService";

    public static boolean SignedIn(){

        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        return (userF!=null) ? true : false;
    }

    public static String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



}
