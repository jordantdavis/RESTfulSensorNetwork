package rsn.client.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import rsn.client.R;
/**
 * Created by Quinn on 4/27/14.
 */
public class RsnPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
