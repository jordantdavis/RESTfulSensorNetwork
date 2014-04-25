package rsn.client.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jordan on 4/23/14.
 */
public class RsnScheduleDispatcherService extends IntentService {
    public RsnScheduleDispatcherService() {
        super("RsnScheduleDispatcherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("RSN", "Schedule dispatch starting.");
        RsnSchedulerAlarmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
