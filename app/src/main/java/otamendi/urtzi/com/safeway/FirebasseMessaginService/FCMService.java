package otamendi.urtzi.com.safeway.FirebasseMessaginService;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.MainApplication;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.notificationService;
import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static MediaPlayer mediaPlayer;

    public FCMService() {
    }

    public static void AutoInitEnable() {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            if (data.get("type").compareTo("trackingStart") == 0) {
                Log.d(TAG, "trackingStart");
                String userUid = data.get("user");
                String name = data.get("name");
                notificationService.createTrackingStartedNotification(name, userUid);
            }
            if (data.get("type").compareTo("trackingStop") == 0) {
                Log.d(TAG, "trackingStop");
                String userUid = data.get("user");
                String name = data.get("name");
                notificationService.createTrackingStopedNotification(name, userUid);
            }
            if (data.get("type").compareTo("emergencyCall") == 0 && sharedPreferences.readBoolean(MainApplication.getAppContext(), "tracking")) {
                Log.d(TAG, "emergencyCall");
                emergencyCall();
            }
            if (data.get("type").compareTo("alarmOn") == 0 && sharedPreferences.readBoolean(MainApplication.getAppContext(), "tracking")) {
                Log.d(TAG, "alarmOn");
                alarmActivate();

            }
            if (data.get("type").compareTo("emergency") == 0 && sharedPreferences.readBoolean(MainApplication.getAppContext(), "tracking")) {
                Log.d(TAG, "emergency");
                String userUid = data.get("user");
                String name = data.get("name");
                boolean emergency = Boolean.parseBoolean(data.get("emergency"));
                Log.d(TAG, "" + emergency);
                if(emergency){
                   emergencyActivated(userUid, name);
                }else{
                    emergencyDeactivated(userUid, name);

                }


            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


    public static void sendMessage(String recptorUid, String type) {
        String urlParameters = "type=" + type + "&receptor=" + recptorUid;
        Map<String, String> object = new HashMap<String, String>();
        object.put("type", type);
        object.put("receptor", recptorUid);
        FirebaseFunctions.getInstance()
                .getHttpsCallable("sendNotification")
                .call(object)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.e(TAG, "code: " + code.toString() + " details: " + details);
                            }
                        }
                    }
                });
    }

    public  void emergencyCall(){
        DatabaseService.getUser(emergencyCallback, new SimpleCallback<String>() {
            @Override
            public void callback(String data) {
                Log.e(TAG, data);
            }
        });
    }


    //emergencyCallback
    private SimpleCallback<User> emergencyCallback = new SimpleCallback<User>() {
        @SuppressLint("MissingPermission")
        @Override
        public void callback(User data) {
            String tel = "tel:" + data.getEmergencyNumber();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(tel));
            startActivity(callIntent);
        }
    };


    public static void alarmActivate(){

        sharedPreferences.writeBoolean(MainApplication.getAppContext(), "alarmOn", true);
        mediaPlayer = MediaPlayer.create(MainApplication.getAppContext(), R.raw.alarm);
        try {
            mediaPlayer.setVolume(8f, 8f);
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        mediaPlayer.start();

        sharedPreferences.writeBoolean(MainApplication.getAppContext(), "alarmOn", true);
    }
    public static void stopMediaPlayer() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    public static void emergencyActivated(String userUid, String name){
        notificationService.createEmergencyActivatedNotification(name, userUid);
    }

    public static  void emergencyDeactivated(String userUid, String name){
        notificationService.createEmergencyDeactivatedNotification(name, userUid);
    }


}
