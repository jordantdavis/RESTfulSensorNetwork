package rsn.client.scheduler;

import android.content.Context;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jordan on 4/27/14.
 */
public class SensorSamplesAccessor {
    private static final String SENSOR_SAMPLE_FILENAME = "sensorSamples.csv";

    private Context context;

    public SensorSamplesAccessor(Context context) {
        this.context = context;
    }

    public ArrayList<SensorSample> getAllSensorSamples() {
        ArrayList<SensorSample> sensorSamples = new ArrayList<SensorSample>();

        synchronized (SensorSamplesAccessor.class) {
            try {
                Scanner scanner = new Scanner(context.openFileInput(SENSOR_SAMPLE_FILENAME));

                while (scanner.hasNext()) {
                    String[] tokens = scanner.nextLine().split(",");
                    String sensorName = tokens[0];
                    long timestamp = Long.parseLong(tokens[1]);
                    double sensorValue = Double.parseDouble(tokens[2]);
                    sensorSamples.add(new SensorSample(sensorName, timestamp, sensorValue));
                }

                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return sensorSamples;
    }

    public void appendSensorSample(SensorSample sensorSample) {
        synchronized (SensorSamplesAccessor.class) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(SENSOR_SAMPLE_FILENAME,
                        Context.MODE_PRIVATE | Context.MODE_APPEND);

                fileOutputStream.write((sensorSample.toString() + "\n").getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
