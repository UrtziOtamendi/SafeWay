package otamendi.urtzi.com.safeway.Utils.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

import otamendi.urtzi.com.safeway.Utils.Util;

public class locationServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(sharedPreferences.readBoolean(context,"tracking")==true){

        }
    }
}
