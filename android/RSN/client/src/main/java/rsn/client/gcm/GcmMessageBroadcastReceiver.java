package rsn.client.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by jordan on 4/20/14.
 */
public class GcmMessageBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmMessageService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmMessageService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}
