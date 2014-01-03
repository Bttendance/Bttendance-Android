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
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.ProfessorActivity;
import com.utopia.bttendance.activity.StudentActivity;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.event.AttdCheckedEvent;
import com.utopia.bttendance.event.AttdStartedEvent;
import com.utopia.bttendance.model.BTKey;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;

import java.util.HashSet;

/**
 * Created by TheFinestArtist on 2013. 12. 4..
 */
public class GcmIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String POST = "post";
    private static final String COURSE = "course";
    private static final String ATTENDANCE_STARTED = "attendance_started";
    private static final String ATTENDANCE_CHECKED = "attendance_checked";

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

                updateTable(extras);
                BTTable.UUIDLIST = new HashSet<String>();
                BTTable.UUIDLISTSENDED = new HashSet<String>();

                if (ATTENDANCE_STARTED.equals(extras.getString(TYPE))) {
                    BTEventBus.getInstance().post(new AttdStartedEvent());
                    sendNotification(extras.getString(TITLE), extras.getString(MESSAGE), true);
                } else if (ATTENDANCE_CHECKED.equals(extras.getString(TYPE))) {
                    BTEventBus.getInstance().post(new AttdCheckedEvent(extras.getString(TITLE)));
                    sendNotification(extras.getString(TITLE), extras.getString(MESSAGE), false);
                } else
                    sendNotification(extras.getString(TITLE), extras.getString(MESSAGE), true);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void updateTable(Bundle extras) {

        String postJson = extras.getString(POST);
        String courseJson = extras.getString(COURSE);

        Gson gson = new Gson();
        PostJson post = gson.fromJson(postJson, PostJson.class);
        CourseJson course = gson.fromJson(courseJson, CourseJson.class);

        if (post != null) {
            BTTable.PostTable.append(post.id, post);
            BTTable.getPosts(BTTable.FILTER_MY_POST).append(post.id, post);
        }

        if (course != null) {
            BTTable.CourseTable.append(course.id, course);
            BTTable.getCourses(BTTable.FILTER_MY_COURSE).append(course.id, course);
        }

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
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

        BTKey.Type type = BTPreference.getUserType(getApplicationContext());
        PendingIntent pending;
        switch (type) {
            case PROFESSOR:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, ProfessorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case STUDENT:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, StudentActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            default:
                pending = PendingIntent.getActivity(this, 0, new Intent(this, CatchPointActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        builder.setContentIntent(pending);

        Notification noti = builder.build();
        if (alert) {
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;
            noti.defaults |= Notification.DEFAULT_LIGHTS;
        }
        nm.notify(NOTIFICATION_ID, noti);
    }
}
