package rsn.client.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
        long curTime = System.currentTimeMillis() / 1000;
        RsnSensorEventListener rsnSensorEventListener = new RsnSensorEventListener(this,
            new Schedule("accelerometer", 1398661200, 1398747600, 0.1), curTime, curTime + 60);
        RsnSchedulerAlarmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
