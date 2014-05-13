package com.bttendance.service;

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
import android.support.v4.app.NotificationCompat;

import com.bttendance.activity.MainActivity;
import com.bttendance.event.refresh.RefreshCourseListEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.model.json.UserJson;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.BTEventBus;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.sign.CatchPointActivity;
import com.bttendance.event.attendance.AttdCheckedEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.model.BTPreference;

/**
* Created by TheFinestArtist on 2013. 12. 4..
*/
public class GcmIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                BTDebug.LogError("Notification Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                BTDebug.LogError("Notification Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                BTDebug.LogInfo("Notification Received: " + extras.toString());

//                BTTable.UUIDLIST_refresh();
//                BTTable.UUIDLISTSENDED_refresh();

                String type = extras.getString("type");
                String title = extras.getString("title");
                String message = extras.getString("message");
                sendNotification(title, message, true);

                if ("attendance_started".equals(type)) {
                    BTEventBus.getInstance().post(new AttdStartedEvent(false));
                } else if ("attendance_on_going".equals(type)) {
                    BTEventBus.getInstance().post(new AttdStartedEvent(true));
                } else if ("attendance_checked".equals(type)) {
                    BTEventBus.getInstance().post(new AttdCheckedEvent(title));
                } else if ("clicker_started".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("clicker_on_going".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("notice".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("added_as_manager".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshCourseListEvent());
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("course_created".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshCourseListEvent());
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String message, boolean alert) {

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setTicker(message);
        builder.setContentText(message);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder.setSmallIcon(R.drawable.ic_status_bar_ver2);
            Drawable drawable = getResources().getDrawable(R.drawable.icon);
            if (drawable instanceof BitmapDrawable)
                builder.setLargeIcon(((BitmapDrawable) drawable).getBitmap());
        } else {
            builder.setSmallIcon(R.drawable.ic_status_bar_icon);
        }

        UserJson user = BTPreference.getUser(this);
        PendingIntent pending;
        if (user == null || user.username == null || user.password == null) {
            BTPreference.clearUser(this);
            pending = PendingIntent.getActivity(this, 0, new Intent(this, CatchPointActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pending = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        builder.setContentIntent(pending);

        Notification noti = builder.build();
        if (alert) {
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;
            noti.defaults |= Notification.DEFAULT_LIGHTS;
        } else {
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_LIGHTS;
        }
        nm.notify(NOTIFICATION_ID, noti);
    }
}
