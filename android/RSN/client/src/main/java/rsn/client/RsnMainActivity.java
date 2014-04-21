package rsn.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

public class RsnMainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String SAVED_INST_HAS_BEEN_PROMPTED = "hasBeenPrompted";
    private static final String PREFS_REGISTRATION_ID = "registrationId";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "928553698734";

    private static final int SCHEDULER_TAB = 0;
    private static final int VISUALIZER_TAB = 1;
    private static final int LOGGER_TAB = 2;

    private boolean hasBeenPromptedForReg = false;
    private String registrationId = "";
    private GoogleCloudMessaging gcm;
    private Context context;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsn_main);

        context = getApplicationContext();

        // set up action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // set up adapter for tabs
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // set up fragment view and set its adapter
        mViewPager = (ViewPager)(findViewById(R.id.pager));
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // listener to allow for movement between tabs
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // set the tabs to the action bar
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                .setText(mSectionsPagerAdapter.getPageTitle(i))
                .setTabListener(this));
        }

        // check to see if the device has Google Play Services
        if (checkPlayServices()) {
            registrationId = getGcmRegistrationId();

            // find out if the user has been prompted to register with GCM/RSN
            if (savedInstanceState == null) {
                hasBeenPromptedForReg = false;
            } else {
                hasBeenPromptedForReg = savedInstanceState.getBoolean(SAVED_INST_HAS_BEEN_PROMPTED, false);
            }

            // if the user is not registered and they have not been prompted to register
            if (registrationId.isEmpty() && !hasBeenPromptedForReg) {
                promptForGcmRegistration();
                hasBeenPromptedForReg = true;
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        // save whether or not the user was prompted to register
        outState.putBoolean(SAVED_INST_HAS_BEEN_PROMPTED, hasBeenPromptedForReg);
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rsn_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // present the user with an alert dialog to register
    private void promptForGcmRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GCM Registration")
                .setMessage("GCM Registration ID not found, sign up?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gcmRegisterInBackground();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.show();
    }

    // check for Google Play Services on the device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // if the device does not have it and it can be downloaded, alert
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private String getGcmRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        return prefs.getString(PREFS_REGISTRATION_ID, "");
    }

    private void setGcmRegistrationId(String registrationId) {
        final SharedPreferences prefs = getGcmPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_REGISTRATION_ID, registrationId);
        editor.commit();
    }

    private SharedPreferences getGcmPreferences() {
        return getSharedPreferences(RsnMainActivity.class.getSimpleName(), MODE_PRIVATE);
    }

    // register with GCM and RSN in the background
    private void gcmRegisterInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    // GCM register
                    registrationId = gcm.register(SENDER_ID);
                    // RSN register
                    registerDeviceOnServer(registrationId, getAvailableSensors());
                    // store registration ID in preferences
                    setGcmRegistrationId(registrationId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                Toast.makeText(context, "GCM Registration Successful!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    // unregister with GCM and RSN in the background
    private void gcmUnregisterInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    // GCM unregister
                    gcm.unregister();
                    // RSN unregister
                    unregisterDeviceOnServer(registrationId);
                    // erase registration ID from preferences
                    registrationId = "";
                    setGcmRegistrationId(registrationId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                Toast.makeText(context, "GCM Unregistration Successful!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    // register the device with RSN
    private void registerDeviceOnServer(String registrationId, String[] availableSensors) {
        String resource = "http://hnat-server.cs.memphis.edu:9263/device/register";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(resource);
        HttpResponse response;
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        StringEntity strEntity = null;

        try {
            obj.put("registrationId", registrationId);

            for (String sensor : availableSensors) {
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

    // unregister the device with RSN
    private void unregisterDeviceOnServer(String registrationId) {
        String resource = "http://hnat-server.cs.memphis.edu:9263/unregister";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(resource);
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

    // get all sensors on the device
    private String[] getAvailableSensors() {
        Set<String> availableSensors = new HashSet<String>();
        SensorManager sensorManager = (SensorManager)(getSystemService(Context.SENSOR_SERVICE));
        LocationManager locationManager = (LocationManager)(getSystemService(Context.LOCATION_SERVICE));

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

    // adapter to map tabs to fragments
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new SchedulerFragment();
                    break;
                case 1:
                    fragment = new VisualizerFragment();
                    break;
                case 2:
                    fragment = new LoggerFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_scheduler).toUpperCase(l);
                case 1:
                    return getString(R.string.title_visualizer).toUpperCase(l);
                case 2:
                    return getString(R.string.title_logger).toUpperCase(l);
            }
            return null;
        }
    }
}
