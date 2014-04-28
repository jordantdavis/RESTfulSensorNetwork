package rsn.client.scheduler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by jordan on 4/26/14.
 */
public class SensorSamplingRunnable implements SensorEventListener, Runnable {
    private Context context;
    private Sensor sensor;
    private SensorManager sensorManager;
    private String sensorName;
    private long endTime;
    private long curTime;
    private double samplePeriod;
    private long nextSampleTime;
    private int numAxes;
    private SensorSamplesAccessor sensorSamplesAccessor;

    public SensorSamplingRunnable(Context context, String sensorName, long startTime, long endTime, double frequency) {
        this.context = context;
        this.sensorName = sensorName;
        this.endTime = endTime;

        int sensorType = 0;

        if (sensorName.equals("accelerometer")) {
            sensorType = Sensor.TYPE_ACCELEROMETER;
            numAxes = 3;
        } else if (sensorName.equals("temperature")) {
            sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE;
            numAxes = 1;
        } else if (sensorName.equals("gyroscope")) {
            sensorType = Sensor.TYPE_GYROSCOPE;
            numAxes = 3;
        } else if (sensorName.equals("light")) {
            sensorType = Sensor.TYPE_LIGHT;
            numAxes = 1;
        } else if (sensorName.equals("magnetometer")) {
            sensorType  = Sensor.TYPE_MAGNETIC_FIELD;
            numAxes = 3;
        } else if (sensorName.equals("pressure")) {
            sensorType = Sensor.TYPE_PRESSURE;
            numAxes = 1;
        } else if (sensorName.equals("proximity")) {
            sensorType = Sensor.TYPE_PROXIMITY;
            numAxes = 1;
        } else if (sensorName.equals("humidity")) {
            sensorType = Sensor.TYPE_RELATIVE_HUMIDITY;
            numAxes = 1;
        }

        sensorManager = (SensorManager)(context.getSystemService(Context.SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(sensorType);

        curTime = System.currentTimeMillis() / 1000;
        samplePeriod = 1.0 / frequency;
        nextSampleTime = startTime;
        sensorSamplesAccessor = new SensorSamplesAccessor(context);
    }

    @Override
    public void run() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        curTime = System.currentTimeMillis() / 1000;

        while (curTime < endTime) {
            curTime = System.currentTimeMillis() / 1000;
        }

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        curTime = System.currentTimeMillis() / 1000;
        if (curTime > nextSampleTime) {
            if (numAxes == 3) {
                Log.i("RSN", sensorName + "{" + event.values[0] + "," + event.values[1] + "," + event.values[2] + "}");
                sensorSamplesAccessor.appendSensorSample(new SensorSample(sensorName + "X",
                        curTime, event.values[0]));
                sensorSamplesAccessor.appendSensorSample(new SensorSample(sensorName + "Y",
                        curTime, event.values[1]));
                sensorSamplesAccessor.appendSensorSample(new SensorSample(sensorName + "Z",
                        curTime, event.values[2]));
            } else {
                Log.i("RSN", sensorName + "{" + event.values[0] + "}");
                sensorSamplesAccessor.appendSensorSample(new SensorSample(sensorName,
                        curTime, event.values[0]));
            }

            nextSampleTime += (long)(samplePeriod);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
