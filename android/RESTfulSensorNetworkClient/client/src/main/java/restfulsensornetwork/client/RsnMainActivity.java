package restfulsensornetwork.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.lang.Void;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RsnMainActivity extends Activity {
    private static final String SENDER_ID = "928553698734";
    private static final String GCM_TAG = "GCM";
    private static final String SENSOR_TAG = "Sensor";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PREFS_REGISTRATION_ID = "registrationId";

    private Context context;
    private String registrationId;
    private GoogleCloudMessaging gcm;

    private TextView infoTextView;
    private Button registrationButton;
    private boolean isRegistered;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsn_main);

        infoTextView = (TextView)(findViewById(R.id.infoTextView));
        registrationButton = (Button)(findViewById(R.id.registrationButton));

        if (checkPlayServices()) {
            context = getApplicationContext();
            registrationId = getRegistrationId(context);

            if (registrationId.isEmpty()) {
                Log.i(GCM_TAG, "Registration ID not found.");
                registrationButton.setText("Register");
                isRegistered = false;
            } else {
                registrationButton.setText("Unregister");
                infoTextView.setText(registrationId);
                isRegistered = true;
            }
        } else {
            Log.i(GCM_TAG, "Google Play Services not installed.");
            finish();
        }

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRegistered) {
                    unregisterInBackground();
                } else {
                    registerInBackground();
                }
                isRegistered = !isRegistered;
            }
        });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences(RsnMainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    registrationId = gcm.register(SENDER_ID);
                    Log.i(GCM_TAG, "New device registered.  Registration ID: " + registrationId);
                    registerOnServer(registrationId, getAvailableSensors());
                    setRegistrationId(context, registrationId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                registrationButton.setText("Unregister");
                infoTextView.setText("Registration ID:\n" + registrationId);
            }
        }.execute();
    }

    private void unregisterInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    gcm.unregister();
                    Log.i(GCM_TAG, "Device unregistered");
                    unregisterOnServer(registrationId);
                    setRegistrationId(context, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                infoTextView.setText("Registration ID:\nN/A");
                registrationButton.setText("Register");
            }
        }.execute();
    }

    private void registerOnServer(String registrationId, String[] sensorsArray) {
        String path = "http://hnat-server.cs.memphis.edu:9263/register";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(path);
        HttpResponse response;
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        StringEntity strEntity = null;

        try {
            obj.put("registrationId", registrationId);

            for (String sensor : sensorsArray) {
                arr.put(sensor);
            }

            obj.put("availableSensors", arr);
            strEntity = new StringEntity(obj.toString());
            strEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(strEntity);
            response = client.execute(request);
            // validate hnat-server response
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unregisterOnServer(String registrationId) {
        String path = "http://hnat-server.cs.memphis.edu:9263/unregister";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(path);
        HttpResponse response;
        JSONObject json = new JSONObject();
        StringEntity strEntity = null;

        try {
            json.put("registrationId", registrationId);
            strEntity = new StringEntity(json.toString());
            strEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(strEntity);
            response = client.execute(request);
            // validate hnat-server response
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRegistrationId(Context context) {
        SharedPreferences prefs = getGcmPreferences(context);
        registrationId = prefs.getString(PREFS_REGISTRATION_ID, "");

        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    private void setRegistrationId(Context context, String registrationId) {
        SharedPreferences prefs = getGcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_REGISTRATION_ID, registrationId);
        editor.commit();
    }

    private String[] getAvailableSensors() {
        SensorManager sensorManager = (SensorManager)(getSystemService(SENSOR_SERVICE));
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        ArrayList<String> sensorList = new ArrayList<String>();

        for (Sensor sensor : allSensors) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensorList.add("accelerometer");
                    Log.i(SENSOR_TAG, "Accelerometer available.");
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorList.add("temperature");
                    Log.i(SENSOR_TAG, "Temperature available.");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorList.add("gyroscope");
                    Log.i(SENSOR_TAG, "Gyroscope available.");
                    break;
                case Sensor.TYPE_LIGHT:
                    sensorList.add("light");
                    Log.i(SENSOR_TAG, "Light available.");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorList.add("magnetometer");
                    Log.i(SENSOR_TAG, "Magnetometer available.");
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorList.add("pressure");
                    Log.i(SENSOR_TAG, "Pressure available.");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensorList.add("proximity");
                    Log.i(SENSOR_TAG, "Proximity available.");
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensorList.add("humidity");
                    Log.i(SENSOR_TAG, "Humidity available.");
                    break;
            }
        }

        LocationManager locationManager = (LocationManager)(getSystemService(Context.LOCATION_SERVICE));
        List<String> locationProviders = locationManager.getAllProviders();

        for (String provider : locationProviders) {
            if (provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.NETWORK_PROVIDER)) {
                sensorList.add("location");
                Log.i(SENSOR_TAG, "Location available.");
                break;
            }
        }

        return Arrays.copyOf(sensorList.toArray(), sensorList.size(), String[].class);
    }
}
