package rsn.client.scheduler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by jordan on 4/22/14.
 */
public class RsnSchedulerAlarmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "rsn.SCHEDULER_ALARM") {
            ComponentName componentName = new ComponentName(context.getPackageName(), RsnScheduleDispatcherService.class.getName());
            startWakefulService(context, intent.setComponent(componentName));
            setResultCode(Activity.RESULT_OK);
        }
    }
}
