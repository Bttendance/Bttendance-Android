package com.utopia.bttendance.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.ProfessorActivity;
import com.utopia.bttendance.activity.StudentActivity;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.model.BTKey;
import com.utopia.bttendance.model.BTPreference;

/**
 * Created by TheFinestArtist on 2013. 12. 4..
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                BTDebug.LogError("Notification Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                BTDebug.LogError("Notification Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                BTDebug.LogInfo("Notification Received: " + extras.toString());
                sendNotification(extras.getString(TITLE), extras.getString(MESSAGE));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String message) {

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setVibrate(new long[]{
                0, 200, 200, 200
        });

        builder.setTicker(message);
        builder.setContentText(message);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder.setSmallIcon(R.drawable.ic_status_bar_ver2);
            Drawable drawable = getResources().getDrawable(R.drawable.icon);
            if (drawable instanceof BitmapDrawable)
                builder.setLargeIcon(((BitmapDrawable) drawable).getBitmap());
        } else {
            builder.setSmallIcon(R.drawable.ic_status_bar_icon);
        }

        BTKey.Type type = BTPreference.getUserType(getApplicationContext());
        PendingIntent pending;
        switch (type) {
            case PROFESSOR:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, ProfessorActivity.class), 0);
                break;
            case STUDENT:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, StudentActivity.class), 0);
                break;
            default:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, CatchPointActivity.class), 0);
        }

        builder.setContentIntent(pending);
        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
