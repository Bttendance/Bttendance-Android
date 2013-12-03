//
//package com.utopia.bttendance.service;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.utopia.bttendance.R;
//
//import java.util.HashMap;
//
///**
// * VingleGCMIntentService
// *
// * @author huewu.yang & The Finest Artist
// */
//public class BTGCMService extends GoogleCloudMessaging {
//
//    public static final int CUSTOM = 0;
//    public static final int ENTIRE_NOTIFICATION = 1000;
//    public static final int FOLLOWS_MY_COLLECTION = 1001;
//    public static final int LIKES_MY_COLLECTION = 1002;
//    public static final int COMMENTS_ON_MY_CARD = 1003;
//    public static final int COMMENTS_ON_A_CARD_THAT_I_COMMENTED_ON = 1004;
//    public static final int MENTIONS_ME_IN_A_CARD = 1005;
//    public static final int MENTIONS_ME_IN_A_COMMENT = 1006;
//    public static final int LIKES_MY_CARD = 1007;
//    public static final int CLIPS_MY_CARD = 1008;
//
//    @Override
//    protected String[] getSenderIds(Context context) {
//        String senderId = GCMHelper.getSenderID(this);
//        return new String[]{
//                senderId
//        };
//    }
//
//    @Override
//    protected void onError(Context arg0, String arg1) {
//        // Typically, there is nothing to be done other than evaluating the
//        // error (returned by errorId) and trying to fix the problem.
//    }
//
//    @Override
//    protected void onMessage(Context arg0, Intent msg) {
//        // parse message.
//        parseGCMMessage(msg);
//    }
//
//    @Override
//    protected void onRegistered(Context ctx, String rid) {
//        // Typically, you should send the regid to your server so it can use it
//        // to send messages to this device.
//        // send GCM reg id to our clock server.
//        // save rid here.
//        VinglePreference.setGCMRegisterID(ctx, rid);
//        VingleEventBus.getInstance().post(new RegisterGCMEvent(true));
//    }
//
//    @Override
//    protected void onUnregistered(Context ctx, String regID) {
//        // Typically, you should send the regid to the server so it unregisters
//        // the device.
//        VinglePreference.setGCMRegisterID(ctx, "");
//        VingleEventBus.getInstance().post(new RegisterGCMEvent(false));
//    }
//
//    @SuppressLint("NewApi")
//    private void parseGCMMessage(Intent msg) {
//        // only if vingle app is not working...
//        VingleDebug.LogDebug(TAG, "Bundle: " + msg.getExtras());
//
//        handleBackgroundNotification(msg);
//    }
//
//    private void handleBackgroundNotification(Intent msg) {
//        String countStr = msg.getStringExtra("notification_count");
//        String titleStr = msg.getStringExtra("title");
//        String contentStr = msg.getStringExtra("content");
//
//        try {
//            int count = Integer.parseInt(countStr);
//            VingleInstanceData.setUnreadNotificationNumber(count);
//        } catch (Exception e) {
//        }
//
//        String noti_type = msg.getStringExtra("notification_type");
//        if ((isNotificationEnabled(noti_type)
//                && isNotificationEnabled("entire"))
//                || "custom".equals(noti_type)) {
//            String source_type = msg.getStringExtra("source_type");
//            String source_id = msg.getStringExtra("source_id");
//            // this flag should is applied only for admin notification stuffs.
//            String vibrate = msg.getStringExtra("vibrate");
//
//            Intent intent = parseSource(source_type, source_id, titleStr);
//
//            if (intent == null)
//                return;
//
//            intent.putExtra("notification_type", noti_type);
//            PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//            pushNotification(countStr, titleStr, contentStr, pi, vibrate);
//        }
//    }
//
//    private final HashMap<String, Integer> mNotiEnalbleMathcer = new HashMap<String, Integer>();
//
//    {
//        mNotiEnalbleMathcer.put("entire", ENTIRE_NOTIFICATION);
//        mNotiEnalbleMathcer.put("follow_received", FOLLOWS_MY_COLLECTION);
//        mNotiEnalbleMathcer.put("collection_followed", FOLLOWS_MY_COLLECTION);
//        mNotiEnalbleMathcer.put("collection_liked", LIKES_MY_COLLECTION);
//        mNotiEnalbleMathcer.put("comment_received", COMMENTS_ON_MY_CARD);
//        mNotiEnalbleMathcer.put("comment_thread", COMMENTS_ON_A_CARD_THAT_I_COMMENTED_ON);
//        mNotiEnalbleMathcer.put("tag_me_on_post", MENTIONS_ME_IN_A_CARD);
//        mNotiEnalbleMathcer.put("tag_me_on_comment", MENTIONS_ME_IN_A_COMMENT);
//        mNotiEnalbleMathcer.put("tag_me_on_mention", MENTIONS_ME_IN_A_COMMENT);
//        mNotiEnalbleMathcer.put("like_received", LIKES_MY_CARD);
//        mNotiEnalbleMathcer.put("clipping_received", CLIPS_MY_CARD);
//        mNotiEnalbleMathcer.put("custom", CUSTOM);
//    }
//
//    private boolean isNotificationEnabled(String noti_type) {
//
//        if (noti_type == null || noti_type.length() == 0)
//            return false;
//
//        Integer key = mNotiEnalbleMathcer.get(noti_type);
//        if (key == null)
//            return false;
//
//        return VinglePreference.getNotificationPreference(this, key);
//    }
//
//    private final HashMap<String, String> mIntentActionMathcer = new HashMap<String, String>();
//
//    {
//        mIntentActionMathcer.put("card", IntentKey.ACTION_SHOW_CARD);
//        mIntentActionMathcer.put("collection", IntentKey.ACTION_SHOW_COLLECTION);
//        mIntentActionMathcer.put("party", IntentKey.ACTION_SHOW_PARTY);
//        mIntentActionMathcer.put("user", IntentKey.ACTION_SHOW_USER);
//        mIntentActionMathcer.put("app", Intent.ACTION_MAIN);
//    }
//
//    private Intent parseSource(String type, String id, String title) {
//
//        String action = mIntentActionMathcer.get(type);
//
//        VingleDebug.LogDebug(TAG,"parse Source: " + type + " : " + id);
//
//        Intent intent = new Intent(action);
//
//        if (action.equals(Intent.ACTION_MAIN)) {
//            intent.setClass(this, VingleSplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//        } else {
//            intent.putExtra(BundleKey.EXTRA_CARD_ID, Integer.valueOf(id));
//            intent.putExtra(BundleKey.EXTRA_COLLECTION_ID, Integer.valueOf(id));
//            intent.putExtra(BundleKey.EXTRA_PARTY_ID, Integer.valueOf(id));
//            intent.putExtra(BundleKey.EXTRA_USERNAME, title);
//        }
//
//        return intent;
//    }
//
//    private void pushNotification(String countStr, String titleStr, String contentStr,
//                                  PendingIntent pending, String vibrate) {
//
//        NotificationManager nm = (NotificationManager)
//                getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder = new
//                NotificationCompat.Builder(this);
//
//        String content;
//        String title;
//        int notification_count = 0;
//        try {
//            notification_count = Integer.parseInt(countStr);
//        } catch (Exception e) {
//        }
//        boolean v = false;
//        try {
//            v = Boolean.parseBoolean(vibrate);
//        } catch (Exception e) {
//        }
//
//        switch (notification_count) {
//            case 0:
//                content = contentStr;
//                title = titleStr;
//                if (v)
//                    builder.setVibrate(new long[]{
//                            0, 200, 200, 200
//                    });
//                break;
//            case 1:
//                content = titleStr + " " + contentStr;
//                title = countStr + " new notification";
//                break;
//            default:
//                content = titleStr + " " + contentStr;
//                title = countStr + " new notifications";
//        }
//
//        builder.setTicker(content);
//        builder.setContentText(content);
//        builder.setContentTitle(title);
//        builder.setNumber(notification_count);
//        builder.setOnlyAlertOnce(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            builder.setSmallIcon(R.drawable.ic_status_bar_icon);
//            Drawable drawable = getResources().getDrawable(R.drawable.icon);
//            if (drawable instanceof BitmapDrawable)
//                builder.setLargeIcon(((BitmapDrawable) drawable).getBitmap());
//        } else {
//            builder.setSmallIcon(R.drawable.ic_status_bar_ver2);
//        }
//
//        builder.setContentIntent(pending);
//        Notification n = builder.build();
//        nm.notify(R.id.notification, n);
//    }
//
//}// end of class
