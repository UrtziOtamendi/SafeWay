package otamendi.urtzi.com.safeway.FirebasseMessaginService;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import otamendi.urtzi.com.safeway.Utils.AuthService;

public class FCMIdService extends FirebaseInstanceIdService {

    private static final String TAG = "FCMIdService";

    public FCMIdService() {
    }
    public static String getToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (AuthService.SignedIn()) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(AuthService.getUid()).child("notificationToken").setValue(refreshedToken);
        }
    }



    private void sendMenssage(String SENDER_ID, String goal, Map<String,String> data){
        FirebaseInstanceId fcmID = FirebaseInstanceId.getInstance();
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID+ "@gcm.googleapis.com")
                .setMessageId(SENDER_ID + fcmID)
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());


    }
}
