package rsn.client.scheduler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import rsn.client.util.RsnRequestHandler;
import rsn.client.util.SettingsAccessor;

/**
 * Created by jordan on 4/23/14.
 */
public class RsnScheduleDispatcherService extends IntentService {
    private static final int MINUTE_IN_SECONDS = 60;

    public RsnScheduleDispatcherService() {
        super("RsnScheduleDispatcherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("RSN", "Schedule dispatch starting.");
        long curTime = System.currentTimeMillis() / 1000;

        dispatchSchedules(curTime);
        offloadSensorSamples();

        RsnSchedulerAlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void dispatchSchedules(long currentTime) {
        ScheduleAccessor scheduleAccessor = new ScheduleAccessor(this);
        ArrayList<Schedule> schedules = scheduleAccessor.getAllSchedules();

        for (Schedule schedule : schedules) {
            if (currentTime >= schedule.getEndTime()) {
                Log.i("RSN", schedule.toString() + " has expired.");
                scheduleAccessor.removeSchedule(schedule);
            } else {
                long startTime = currentTime;
                long endTime = currentTime + MINUTE_IN_SECONDS;

                if (currentTime <= schedule.getStartTime()) {
                    startTime = schedule.getStartTime();
                }

                if (currentTime + MINUTE_IN_SECONDS >= schedule.getEndTime()) {
                    endTime = schedule.getEndTime();
                }

                Log.i("RSN", "Dispatching " + schedule.toString());

                if (schedule.getSensorName().equals("location")) {
                    new RsnLocationListener(this, schedule, startTime, endTime);
                } else {
                    new RsnSensorEventListener(this, schedule, startTime, endTime);
                }
            }
        }
    }

    private void offloadSensorSamples() {
        ConnectivityManager connectivityManager = (ConnectivityManager)(getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            Log.i("RSN", "Offloading now!");
            SensorSamplesAccessor sensorSamplesAccessor = new SensorSamplesAccessor(this);
            ArrayList<SensorSample> sensorSamplesList = sensorSamplesAccessor.getAllSensorSamples();
            SensorSample[] sensorSamples = Arrays.copyOf(sensorSamplesList.toArray(),
                    sensorSamplesList.size(), SensorSample[].class);
            RsnRequestHandler rsnRequestHandler = new RsnRequestHandler();
            SettingsAccessor settingsAccessor = new SettingsAccessor(this);
            rsnRequestHandler.samplesUpload(settingsAccessor.getGcmRegistrationId(), sensorSamples);

            for (SensorSample sensorSample : sensorSamplesList) {
                sensorSamplesAccessor.removeSensorSample(sensorSample);
            }
        }
    }
}
