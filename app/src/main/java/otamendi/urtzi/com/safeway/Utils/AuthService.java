package otamendi.urtzi.com.safeway.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by urtzi on 07/05/2018.
 */

public  class AuthService {


    public static final String TAG="AuthService";

    public static boolean SignedIn(){

        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        return userF != null;
    }

    public static String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



}
