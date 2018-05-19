package otamendi.urtzi.com.safeway.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import otamendi.urtzi.com.safeway.R;

public class sharedPreferences {

    public static SharedPreferences getSharedPreferences(Context context){
       return context.getSharedPreferences(context.getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE);
    }

    public static void writeString(Context context,String key, String value){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void writeInt(Context context,String key, int value){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void writeBoolean(Context context,String key, Boolean value){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String readString(Context context, String key){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static int readInt(Context context, String key){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        return sharedPreferences.getInt(key, -1);
    }

    public static boolean readBoolean(Context context, String key){
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        Boolean pref= false;
        return sharedPreferences.getBoolean(key,pref);
    }
}
