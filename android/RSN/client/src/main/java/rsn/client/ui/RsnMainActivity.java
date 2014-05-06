package rsn.client.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import rsn.client.R;
import rsn.client.settings.RsnPreferenceActivity;
import rsn.client.util.RsnRequestHandler;
import rsn.client.util.SettingsAccessor;

public class RsnMainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String SAVED_INST_HAS_BEEN_PROMPTED = "hasBeenPrompted";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "928553698734";

    private static final int MINUTE_IN_MILLIS = 60 * 1000;

    private boolean hasBeenPromptedForReg = false;
    private String registrationId = "";
    private GoogleCloudMessaging gcm;
    private Context context;
    private SettingsAccessor settingsAccessor;
    private RsnRequestHandler rsnRequestHandler;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsn_main);

        context = getApplicationContext();
        settingsAccessor = new SettingsAccessor(this);
        rsnRequestHandler = new RsnRequestHandler();

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
            registrationId = settingsAccessor.getGcmRegistrationId();

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

        AlarmManager alarmManager = (AlarmManager)(getSystemService(Context.ALARM_SERVICE));
        Intent intent = new Intent("rsn.SCHEDULER_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MINUTE_IN_MILLIS, pendingIntent);
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
        Intent prefsIntent = new Intent(this.getApplicationContext(),RsnPreferenceActivity.class);
        MenuItem preferences = menu.findItem(R.id.action_settings);
        preferences.setIntent(prefsIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            this.startService(item.getIntent());
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
                    rsnRequestHandler.registerDevice(registrationId, settingsAccessor.getAllAvailableSensors());
                    // store registration ID in preferences
                    settingsAccessor.setGcmRegistrationId(registrationId);
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
                    rsnRequestHandler.unregisterDevice(registrationId);
                    // erase registration ID from preferences
                    registrationId = "";
                    settingsAccessor.setGcmRegistrationId(registrationId);
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
//                    fragment = new VisualizerFragment();
//                    break;
//                case 2:
                    fragment = new LoggerFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return 3;
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_scheduler).toUpperCase(l);
                case 1:
//                    return getString(R.string.title_visualizer).toUpperCase(l);
//                case 2:
                    return getString(R.string.title_logger).toUpperCase(l);
            }
            return null;
        }
    }
}
