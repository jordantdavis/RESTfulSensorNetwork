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

        dispatchSchedules();
        offloadSensorData();

        RsnSchedulerAlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void dispatchSchedules() {
        ScheduleAccessor scheduleAccessor = new ScheduleAccessor(this);
        ArrayList<Schedule> schedules = scheduleAccessor.getAllSchedules();

        for (Schedule s : schedules) {
            long curTime = System.currentTimeMillis() / 1000;
            if (s.getEndTime() < curTime) {
                if (s.getSensorName().equals("location")) {

                } else {
                    Log.i("RSN", "New thread?");
                    new Thread(new SensorSamplingRunnable(this, s.getSensorName(), curTime, curTime + 60, s.getFrequency()));
                }
            }
        }
    }

    private void offloadSensorData() {

    }
}
