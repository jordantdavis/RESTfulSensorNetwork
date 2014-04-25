package rsn.client.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import rsn.client.R;
import rsn.client.scheduler.ScheduleAccessor;
import rsn.client.ui.RsnMainActivity;
import rsn.client.scheduler.Schedule;

/**
 * Created by jordan on 4/20/14.
 */
public class GcmMessageService extends IntentService {
    private static final int NOTIFICATION_ID = 9263;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public GcmMessageService() {
        super("GcmMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        JSONObject scheduleJson = null;

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                try {
                    scheduleJson = new JSONObject(extras.getString("schedule"));

                    String sensorName = scheduleJson.getString("sensorName");
                    int startTime = scheduleJson.getInt("startTime");
                    int endTime = scheduleJson.getInt("endTime");
                    double frequency = scheduleJson.getDouble("frequency");

                    Schedule schedule = new Schedule(sensorName, startTime, endTime, frequency);
                    ScheduleAccessor scheduleAccessor = new ScheduleAccessor(this);
                    scheduleAccessor.appendSchedule(schedule);
                    sendNotification("New RSN schedule received.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmMessageBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)(this.getSystemService(Context.NOTIFICATION_SERVICE));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, RsnMainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("RSN Notification")
            .setStyle(new NotificationCompat.BigTextStyle()
            .bigText(msg))
            .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}