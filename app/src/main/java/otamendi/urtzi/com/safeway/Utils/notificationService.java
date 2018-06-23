package otamendi.urtzi.com.safeway.Utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import otamendi.urtzi.com.safeway.Activities.safeWayHome;
import otamendi.urtzi.com.safeway.Activities.trackingLiveWay;
import otamendi.urtzi.com.safeway.Activities.usersTrackingList;
import otamendi.urtzi.com.safeway.R;

public class notificationService {

    private static String TAG= "notificationService";
    private static String CHANNEL_ID= "com.urtzi.otamendi";

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SafeWay";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void createTrackinNotification(Context context, String location, Activity activity){


        Intent intent = new Intent(activity, safeWayHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);

        Log.d(TAG,"creating notif");
        createNotificationChannel(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Tracking")
                .setContentText("You are goingo to "+ location+ " your position is being recorded.")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());

    }

    public static void createTrackingStartedNotification(String name, String userUid ){
        Intent intent = new Intent(MainApplication.getAppContext(), trackingLiveWay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("users_uid", userUid);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getAppContext(), 0, intent, 0);
        Log.d(TAG,"createTrackingStartedNotification ");

        createNotificationChannel(MainApplication.getAppContext());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainApplication.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(MainApplication.getAppContext().getResources().getString(R.string.notif_tracking_started))
                .setContentText(name +" "+ MainApplication.getAppContext().getResources().getString(R.string.notif_tracking_started_body))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getAppContext());

        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
        sharedPreferences.writeInt(MainApplication.getAppContext(),"notify_id",id);
    }


    public static void createTrackingStopedNotification(String name, String userUid ){
        Intent intent = new Intent(MainApplication.getAppContext(), usersTrackingList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("users_uid", userUid);

        // Delete old tracking started notification
        int old_id = sharedPreferences.readInt(MainApplication.getAppContext(),"notify_id");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getAppContext());
        notificationManager.cancel(old_id);


        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getAppContext(), 0, intent, 0);
        Log.d(TAG,"createTrackingStartedNotification ");

        createNotificationChannel(MainApplication.getAppContext());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainApplication.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(MainApplication.getAppContext().getResources().getString(R.string.notif_tracking_stoped))
                .setContentText(name +" "+ MainApplication.getAppContext().getResources().getString(R.string.notif_tracking_stoped_body))
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
        sharedPreferences.writeInt(MainApplication.getAppContext(),"notify_id",id);
    }

    public static void createEmergencyActivatedNotification(String name, String userUid ){
        Intent intent = new Intent(MainApplication.getAppContext(), trackingLiveWay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("users_uid", userUid);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getAppContext());

        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getAppContext(), 0, intent, 0);
        Log.d(TAG,"createEmergencyActivatedNotification ");

        createNotificationChannel(MainApplication.getAppContext());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainApplication.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(MainApplication.getAppContext().getResources().getString(R.string.notif_emergency_title))
                .setContentText(name +" "+ MainApplication.getAppContext().getResources().getString(R.string.notif_emergency_body))
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());

    }


    public static void createEmergencyDeactivatedNotification(String name, String userUid ){
        Intent intent = new Intent(MainApplication.getAppContext(), trackingLiveWay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("users_uid", userUid);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getAppContext());

        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getAppContext(), 0, intent, 0);
        Log.d(TAG,"createEmergencyActivatedNotification ");

        createNotificationChannel(MainApplication.getAppContext());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainApplication.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(MainApplication.getAppContext().getResources().getString(R.string.notif_noemergency_title))
                .setContentText(name +" "+ MainApplication.getAppContext().getResources().getString(R.string.notif_noemergency_body))
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());

    }
}
