package rsn.client.scheduler;

/**
 * Created by jordan on 4/27/14.
 */
public class SensorSample {
    private String sensorName;
    private long timestamp;
    private double sensorValue;

    public SensorSample(String sensorName, long timestamp, double sensorValue) {
        this.sensorName = sensorName;
        this.timestamp = timestamp;
        this.sensorValue = sensorValue;
    }

    public String getSensorName() {
        return sensorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getSensorValue() {
        return sensorValue;
    }

    public String toString() {
        return sensorName + "," + timestamp + "," + sensorValue;
    }
}
