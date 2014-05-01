package rsn.client.scheduler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jordan on 4/28/14.
 */
public class RsnSensorEventListener implements SensorEventListener {
    private SensorManager sensorManager;
    private double periodLength;
    private double nextSampleTime;
    private long endTime;
    private int numAxes;
    private String sensorName;
    private SensorSamplesAccessor sensorSamplesAccessor;

    public RsnSensorEventListener(Context context, Schedule schedule, long startTime, long endTime) {
        sensorManager = (SensorManager)(context.getSystemService(Context.SENSOR_SERVICE));
        Sensor sensor = null;
        numAxes = 1;
        sensorName = schedule.getSensorName();

        if (sensorName.equals("accelerometer")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            numAxes = 3;
        } else if (sensorName.equals("temperature")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else if (sensorName.equals("gyroscope")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            numAxes = 3;
        } else if (sensorName.equals("light")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else if (sensorName.equals("magnetometer")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            numAxes = 3;
        } else if (sensorName.equals("pressure")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else if (sensorName.equals("proximity")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        } else if (sensorName.equals("humidity")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        periodLength = 1.0 / schedule.getFrequency();
        nextSampleTime = startTime;
        this.endTime = endTime;

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorSamplesAccessor = new SensorSamplesAccessor(context);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis() / 1000;
        if (event != null) {
            if (curTime > nextSampleTime) {
                nextSampleTime = curTime + periodLength;

                if (numAxes == 1) {
                    sensorSamplesAccessor.addSensorSample(new SensorSample(sensorName, curTime,
                            event.values[0]));
                } else if (numAxes == 3) {
                    sensorSamplesAccessor.addSensorSample(new SensorSample(sensorName + "X", curTime,
                            event.values[0]));
                    sensorSamplesAccessor.addSensorSample(new SensorSample(sensorName + "Y", curTime,
                            event.values[1]));
                    sensorSamplesAccessor.addSensorSample(new SensorSample(sensorName + "Z", curTime,
                            event.values[2]));
                }
            }
        }
        if (curTime > endTime) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
