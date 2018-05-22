package otamendi.urtzi.com.safeway.FirebasseMessaginService;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService{

    private static final String TAG="FCMService";
    public FCMService() {
    }

    public  static void AutoInitEnable(){
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String,String> data=remoteMessage.getData();
            if (data.get("Goal")=="LinkUser") {
                String linkUid =data.get("LinkUid");
                String linkName =data.get("LinkerName");
                //linkUsers.linkReceptor(linkUid,linkName);

            } else {
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

}
