package rsn.client.scheduler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by jordan on 4/28/14.
 */
public class RsnSensorEventListener implements SensorEventListener {
    private SensorManager sensorManager;
    private int periodLength;
    private long nextSampleTime;
    private long endTime;
    private int numAxes;

    public RsnSensorEventListener(Context context, Schedule schedule, long startTime, long endTime) {
        sensorManager = (SensorManager)(context.getSystemService(Context.SENSOR_SERVICE));
        Sensor sensor = null;
        numAxes = 1;

        if (schedule.getSensorName().equals("accelerometer")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            numAxes = 3;
        } else if (schedule.getSensorName().equals("temperature")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else if (schedule.getSensorName().equals("gyroscope")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            numAxes = 3;
        } else if (schedule.getSensorName().equals("light")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else if (schedule.getSensorName().equals("magnetometer")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            numAxes = 3;
        } else if (schedule.getSensorName().equals("pressure")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else if (schedule.getSensorName().equals("proximity")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else if (schedule.getSensorName().equals("humidity")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        periodLength = (int)(1.0 / schedule.getFrequency());
        nextSampleTime = startTime;
        this.endTime = endTime;

        if (sensor != null) {
            Log.i("RSN", "Let's get started!");
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis() / 1000;
        if (curTime > nextSampleTime) {
            nextSampleTime = curTime + periodLength;
            Log.i("RSN", "BAM, some sensor output!");
        }

        if (curTime > endTime) {
            Log.i("RSN", "Looks like we are done here!");
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
