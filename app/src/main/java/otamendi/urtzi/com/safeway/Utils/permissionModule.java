package otamendi.urtzi.com.safeway.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class permissionModule {

    public static final int PERMISSIONS_REQUEST_LOCATION = 12;
    public static final int PERMISSIONS_REQUEST_CAMERA = 13;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 14;
    public static final int PERMISSIONS_REQUEST_BOOT_COMPLETED = 16;
    public static final int PERMISSIONS_REQUEST_CALL_PHONE = 17;
    public static final int PERMISSIONS_REQUEST_CONTACTS = 18;
    public static final int PERMISSIONS_MULTIPLE=1;
    private static Context context = MainApplication.getAppContext();

    public static String[] checkLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
        } else {
            return new String[]{};
        }
    }

    public static String[] checkCamera() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return new String[]{Manifest.permission.CAMERA};
        } else {
            return  new String[]{};
        }
    }

    public static String[] checkWriteExternalStorage() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            return  new String[]{};
        }
    }

    public static String[] checkBootCompleted(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED ) {
            return  new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED};
        } else {
            return  new String[]{};
        }
    }

    public static String[] checkCallPhone(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
            return  new String[]{Manifest.permission.CALL_PHONE};
        } else {
            return  new String[]{};
        }
    }

    public static String[] checkContacts(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            return new String[]{Manifest.permission.READ_CONTACTS};
        } else {
            return new String[]{};
        }
    }

    public static String[] requestAllPermisions(){
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS};

        return PERMISSIONS;
    }




}
