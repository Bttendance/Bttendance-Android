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

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.event.notification.NotificationReceived;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.UserJson;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.BTEventBus;

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
                String courseID = extras.getString("course_id");

                sendNotification(type, title, message, courseID, true);
                BTEventBus.getInstance().post(new NotificationReceived(type, title, message, courseID));

                if ("attendance_started".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("attendance_on_going".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("attendance_checked".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("clicker_started".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("clicker_on_going".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("notice".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } else if ("added_as_manager".equals(type)) {
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String type, String title, String message, String courseID, boolean alert) {

        UserJson user = BTPreference.getUser(this);
        if (user == null || user.email == null || user.password == null) {
            BTPreference.clearUser(this);
            return;
        }

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

        Intent intent = new Intent(BTKey.IntentKey.ACTION_SHOW_COURSE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(BTKey.EXTRA_TYPE, type);
        intent.putExtra(BTKey.EXTRA_TITLE, title);
        intent.putExtra(BTKey.EXTRA_MESSAGE, message);
        intent.putExtra(BTKey.EXTRA_COURSE_ID, courseID);

        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
