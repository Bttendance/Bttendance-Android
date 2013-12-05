package com.utopia.bttendance.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.ProfessorActivity;

/**
 * Created by TheFinestArtist on 2013. 12. 4..
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

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
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    BTDebug.LogInfo("Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                BTDebug.LogInfo("Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                BTDebug.LogInfo("Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ProfessorActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_status_bar_icon)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void pushNotification(String countStr, String titleStr, String contentStr,
                                  PendingIntent pending, String vibrate) {

        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this);

        String content;
        String title;
        int notification_count = 0;
        try {
            notification_count = Integer.parseInt(countStr);
        } catch (Exception e) {
        }
        boolean v = false;
        try {
            v = Boolean.parseBoolean(vibrate);
        } catch (Exception e) {
        }

        switch (notification_count) {
            case 0:
                content = contentStr;
                title = titleStr;
                if (v)
                    builder.setVibrate(new long[]{
                            0, 200, 200, 200
                    });
                break;
            case 1:
                content = titleStr + " " + contentStr;
                title = countStr + " new notification";
                break;
            default:
                content = titleStr + " " + contentStr;
                title = countStr + " new notifications";
        }

        builder.setTicker(content);
        builder.setContentText(content);
        builder.setContentTitle(title);
        builder.setNumber(notification_count);
        builder.setOnlyAlertOnce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder.setSmallIcon(R.drawable.ic_status_bar_icon);
            Drawable drawable = getResources().getDrawable(R.drawable.icon);
            if (drawable instanceof BitmapDrawable)
                builder.setLargeIcon(((BitmapDrawable) drawable).getBitmap());
        } else {
            builder.setSmallIcon(R.drawable.ic_status_bar_ver2);
        }

        builder.setContentIntent(pending);
        Notification n = builder.build();
        nm.notify(NOTIFICATION_ID, n);
    }
}
