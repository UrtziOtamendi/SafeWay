package otamendi.urtzi.com.safeway.Utils;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MainApplication.context;
    }
}
