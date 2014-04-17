package rsn.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

    private boolean hasBeenPromptedForReg = false;
    private String registrationId = "";
    private GoogleCloudMessaging gcm;
    private Context context = this;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsn_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        if (checkPlayServices()) {
            registrationId = getGcmRegistrationId();

            if (savedInstanceState == null) {
                hasBeenPromptedForReg = false;
            } else {
                hasBeenPromptedForReg = savedInstanceState.getBoolean(SAVED_INST_HAS_BEEN_PROMPTED, false);
            }


            if (registrationId.isEmpty() && !hasBeenPromptedForReg) {
                promptForGcmRegistration();
                hasBeenPromptedForReg = true;
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
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
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
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
        return getSharedPreferences(RsnMainActivity.class.getSimpleName(), this.MODE_PRIVATE);
    }

    private void gcmRegisterInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    registrationId = gcm.register(SENDER_ID);
                    setGcmRegistrationId(registrationId);
                    // register on server
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

    private void gcmUnregisterInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    gcm.unregister();
                    // unregister on server
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

    private void registerDeviceOnServer(String registrationId, String[] availableSensors) {
        String path = "http://hnat-server.cs.memphis.edu:9263/device/register";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(path);
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

    private void unregisterDeviceOnServer(String registrationId) {
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
