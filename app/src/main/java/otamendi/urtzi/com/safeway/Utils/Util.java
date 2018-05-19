package otamendi.urtzi.com.safeway.Utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import otamendi.urtzi.com.safeway.Utils.location.locationService;

public class Util {


    public static void scheduleJob(Context context) {
        Log.e("SCHDULEJOB", "CALLED");


        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(0,
                new ComponentName(context, locationService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build());
    }

    public static void stopScheduleJob(Context context){
        JobScheduler jobScheduler =  (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(0);
    }

}

