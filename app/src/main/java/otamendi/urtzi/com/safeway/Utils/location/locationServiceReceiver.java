package otamendi.urtzi.com.safeway.Utils.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

public class locationServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(sharedPreferences.readBoolean(context,"tracking")==true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, locationService.class));
            }else{
                context.startService(new Intent(context, locationService.class));
            }
        }
    }
}
