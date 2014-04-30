package rsn.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jordan on 4/29/14.
 */
public class SettingsAccessor {
    private static final String PREFS_REGISTRATION_ID = "registrationId";

    private Context context;

    public SettingsAccessor(Context context) {
        this.context = context;
    }

    public String getGcmRegistrationId() {
        final SharedPreferences prefs = context.getSharedPreferences(
                SettingsAccessor.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(PREFS_REGISTRATION_ID, "");
    }

    public void setGcmRegistrationId(String registrationId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                SettingsAccessor.class.getSimpleName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_REGISTRATION_ID, registrationId);
        editor.commit();
    }

    public String[] getAllAvailableSensors() {
        Set<String> availableSensors = new HashSet<String>();
        SensorManager sensorManager = (SensorManager)(context.getSystemService(Context.SENSOR_SERVICE));
        LocationManager locationManager = (LocationManager)(context.getSystemService(Context.LOCATION_SERVICE));

        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            switch(sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    availableSensors.add("accelerometer");
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    availableSensors.add("temperature");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    availableSensors.add("gyroscope");
                    break;
                case Sensor.TYPE_LIGHT:
                    availableSensors.add("light");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    availableSensors.add("magnetometer");
                    break;
                case Sensor.TYPE_PRESSURE:
                    availableSensors.add("pressure");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    availableSensors.add("proximity");
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    availableSensors.add("humidity");
                    break;
            }
        }

        for (String locationProvider : locationManager.getAllProviders()) {
            if (locationProvider.equals(LocationManager.GPS_PROVIDER) || locationProvider.equals(LocationManager.NETWORK_PROVIDER)) {
                availableSensors.add("location");
                break;
            }
        }

        return Arrays.copyOf(availableSensors.toArray(), availableSensors.size(), String[].class);
    }
}
