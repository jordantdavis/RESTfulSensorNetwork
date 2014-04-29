package rsn.client.scheduler;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jordan on 4/28/14.
 */
public class RsnLocationListener implements LocationListener {
    private LocationManager locationManager;
    private int periodLength;
    private long nextSampleTime;
    private long endTime;
    private SensorSamplesAccessor sensorSamplesAccessor;

    public RsnLocationListener(Context context, Schedule schedule, long startTime, long endTime) {
        locationManager = (LocationManager)(context.getSystemService(Context.LOCATION_SERVICE));
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);

        periodLength = (int)(1.0 / schedule.getFrequency());
        nextSampleTime = startTime;
        this.endTime = endTime;

        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 0, 0, this);
            sensorSamplesAccessor = new SensorSamplesAccessor(context);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        long curTime = System.currentTimeMillis() / 1000;
        if (curTime > nextSampleTime) {
            nextSampleTime = curTime + periodLength;

            sensorSamplesAccessor.addSensorSample(new SensorSample("locationLat", curTime,
                    location.getLatitude()));
            sensorSamplesAccessor.addSensorSample(new SensorSample("locationLng", curTime,
                    location.getLongitude()));
        }

        if (curTime > endTime) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
